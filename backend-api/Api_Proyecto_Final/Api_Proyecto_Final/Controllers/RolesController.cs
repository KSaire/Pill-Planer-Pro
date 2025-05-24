using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Api_Proyecto_Final.Models;

namespace Api_Proyecto_Final.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class RolesController : ControllerBase
    {
        private readonly AlarmaMedicamentosContext _context;

        public RolesController(AlarmaMedicamentosContext context)
        {
            _context = context;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<Rol>>> GetRols()
        {
            try
            {
                var roles = await _context.Rols.ToListAsync();

                return Ok(roles);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error obteniendo roles: {ex.Message}");
                return StatusCode(500, "Error interno al obtener los roles.");
            }
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<Rol>> GetRol(int id)
        {
            try
            {
                var rol = await _context.Rols.FindAsync(id);
                if (rol == null)
                    return NotFound();

                return Ok(rol);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error obteniendo rol: {ex.Message}");
                return StatusCode(500, "Error interno al obtener el rol.");
            }
        }

        [HttpPost]
        public async Task<IActionResult> PostRol([FromBody] Rol rol)
        {
            if (rol == null)
                return BadRequest("Rol no válido.");

            try
            {
                using (var transaction = await _context.Database.BeginTransactionAsync())
                {
                    _context.Rols.Add(rol);
                    await _context.SaveChangesAsync();
                    await transaction.CommitAsync();
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error al insertar rol: {ex.Message}");
                return StatusCode(500, "Error interno al guardar el rol.");
            }

            return Ok(rol);
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> PutRol(int id, [FromBody] Rol rol)
        {
            if (id != rol.IdRol)
                return BadRequest("El ID del rol no coincide con el enviado.");

            try
            {
                using (var transaction = await _context.Database.BeginTransactionAsync())
                {
                    _context.Entry(rol).State = EntityState.Modified;
                    await _context.SaveChangesAsync();
                    await transaction.CommitAsync();
                }
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!_context.Rols.Any(r => r.IdRol == id))
                    return NotFound();

                return StatusCode(500, "Error de concurrencia al actualizar el rol.");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error al actualizar rol: {ex.Message}");
                return StatusCode(500, "Error interno al actualizar el rol.");
            }

            return Ok(rol);
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteRol(int id)
        {
            try
            {
                var rol = await _context.Rols.FindAsync(id);
                if (rol == null)
                    return NotFound();

                using (var transaction = await _context.Database.BeginTransactionAsync())
                {
                    _context.Rols.Remove(rol);
                    await _context.SaveChangesAsync();
                    await transaction.CommitAsync();
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error al eliminar rol: {ex.Message}");
                return StatusCode(500, "Error interno al eliminar el rol.");
            }

            return Ok();
        }
    }
}
