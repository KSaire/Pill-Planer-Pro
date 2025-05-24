using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Api_Proyecto_Final.Models;

namespace Api_Proyecto_Final.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class EstadosController : ControllerBase
    {
        private readonly AlarmaMedicamentosContext _context;

        public EstadosController(AlarmaMedicamentosContext context)
        {
            _context = context;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<Estado>>> GetEstados()
        {
            try
            {
                var lista = await _context.Estados.ToListAsync();

                return Ok(lista);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[GET estados] Error: {ex.Message}");
                return StatusCode(500, "Error interno al obtener los estados.");
            }
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<Estado>> GetEstado(int id)
        {
            try
            {
                var estado = await _context.Estados.FindAsync(id);
                if (estado == null)
                    return NotFound();

                return Ok(estado);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[GET estado por ID] Error: {ex.Message}");
                return StatusCode(500, "Error interno al obtener el estado.");
            }
        }

        [HttpPost]
        public async Task<IActionResult> PostEstado([FromBody] Estado estado)
        {
            if (estado == null)
                return BadRequest("Estado no válido.");

            try
            {
                using (var transaction = await _context.Database.BeginTransactionAsync())
                {
                    _context.Estados.Add(estado);
                    await _context.SaveChangesAsync();
                    await transaction.CommitAsync();
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[POST estado] Error: {ex.Message}");
                return StatusCode(500, "Error interno al guardar el estado.");
            }

            return Ok(estado);
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> PutEstado(int id, [FromBody] Estado estado)
        {
            if (id != estado.IdEstado)
                return BadRequest("El ID del estado no coincide con el ID enviado.");

            try
            {
                using (var transaction = await _context.Database.BeginTransactionAsync())
                {
                    _context.Entry(estado).State = EntityState.Modified;
                    await _context.SaveChangesAsync();
                    await transaction.CommitAsync();
                }
            }
            catch (DbUpdateConcurrencyException)
            {
                return NotFound("No se encontró el estado para actualizar.");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[PUT estado] Error: {ex.Message}");
                return StatusCode(500, "Error interno al actualizar el estado.");
            }

            return Ok(estado);
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteEstado(int id)
        {
            try
            {
                var estado = await _context.Estados.FindAsync(id);
                if (estado == null)
                    return NotFound();

                using (var transaction = await _context.Database.BeginTransactionAsync())
                {
                    _context.Estados.Remove(estado);
                    await _context.SaveChangesAsync();
                    await transaction.CommitAsync();
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[DELETE estado] Error: {ex.Message}");
                return StatusCode(500, "Error interno al eliminar el estado.");
            }

            return Ok();
        }
    }
}
