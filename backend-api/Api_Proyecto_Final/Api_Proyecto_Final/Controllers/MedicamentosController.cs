using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Api_Proyecto_Final.Models;

namespace Api_Proyecto_Final.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class MedicamentosController : ControllerBase
    {
        private readonly AlarmaMedicamentosContext _context;

        public MedicamentosController(AlarmaMedicamentosContext context)
        {
            _context = context;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<Medicamento>>> GetMedicamentos()
        {
            try
            {
                var lista = await _context.Medicamentos.ToListAsync();

                return Ok(lista);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error obteniendo medicamentos: {ex.Message}");
                return StatusCode(500, "Error interno al obtener los medicamentos.");
            }
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<Medicamento>> GetMedicamento(string id)
        {
            try
            {
                var medicamento = await _context.Medicamentos.FindAsync(id);
                if (medicamento == null)
                    return NotFound();

                return Ok(medicamento);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error obteniendo medicamento: {ex.Message}");
                return StatusCode(500, "Error interno al obtener el medicamento.");
            }
        }

        [HttpGet("usuario/{userId}")]
        public async Task<ActionResult<IEnumerable<Medicamento>>> GetMedicamentoUsuario(int userId)
        {
            try
            {
                var medicamento = await _context.UsuarioMedicamentos.Where(x=> x.IdUsuario == userId)
                    .Select(x => x.CnMed).Distinct().ToListAsync();
                if (medicamento == null)
                    return NotFound();

                var meds = await _context.Medicamentos.Where(m => medicamento.Contains(m.Cn)).ToListAsync();

                return Ok(meds);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error obteniendo medicamento: {ex.Message}");
                return StatusCode(500, "Error interno al obtener el medicamento.");
            }
        }

        [HttpPost]
        public async Task<IActionResult> PostMedicamento([FromBody] Medicamento med)
        {
            if (med == null)
                return BadRequest("Medicamento no válido.");

            try
            {
                using (var transaction = await _context.Database.BeginTransactionAsync())
                {
                    _context.Medicamentos.Add(med);
                    await _context.SaveChangesAsync();
                    await transaction.CommitAsync();
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error al insertar medicamento: {ex.Message}");
                return StatusCode(500, "Error interno al guardar el medicamento.");
            }

            return Ok(med);
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> PutMedicamento(string id, [FromBody] Medicamento medicamento)
        {
            if (id != medicamento.Cn)
                return BadRequest("El CN del medicamento no coincide con el ID enviado.");

            try
            {
                using (var transaction = await _context.Database.BeginTransactionAsync())
                {
                    _context.Entry(medicamento).State = EntityState.Modified;
                    await _context.SaveChangesAsync();
                    await transaction.CommitAsync();
                } 
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!_context.Medicamentos.Any(m => m.Cn == id))
                    return NotFound();

                return StatusCode(500, "Error de concurrencia al actualizar el medicamento.");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error al actualizar medicamento: {ex.Message}");
                return StatusCode(500, "Error interno al actualizar el medicamento.");
            }

            return Ok(medicamento);
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteMedicamento(string id)
        {
            try
            {
                var medicamento = await _context.Medicamentos.FindAsync(id);
                if (medicamento == null)
                    return NotFound();

                using (var transaction = await _context.Database.BeginTransactionAsync())
                {
                    _context.Medicamentos.Remove(medicamento);
                    await _context.SaveChangesAsync();
                    await transaction.CommitAsync();
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error al eliminar medicamento: {ex.Message}");
                return StatusCode(500, "Error interno al eliminar el medicamento.");
            }

            return Ok();
        }
    }
}