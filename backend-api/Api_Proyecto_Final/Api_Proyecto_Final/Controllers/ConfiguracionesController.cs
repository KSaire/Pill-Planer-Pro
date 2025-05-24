using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Api_Proyecto_Final.Models;

namespace Api_Proyecto_Final.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ConfiguracionesController : ControllerBase
    {
        private readonly AlarmaMedicamentosContext _context;

        public ConfiguracionesController(AlarmaMedicamentosContext context)
        {
            _context = context;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<Configuracion>>> GetConfiguraciones()
        {
            try
            {
                var lista = await _context.Configuracions.ToListAsync();

                return Ok(lista);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error obteniendo configuraciones: {ex.Message}");
                return StatusCode(500, "Error interno al obtener las configuraciones.");
            }
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<Configuracion>> GetConfiguracion(int id)
        {
            try
            {
                var configuracion = await _context.Configuracions.FindAsync(id);
                if (configuracion == null)
                    return NotFound();

                return Ok(configuracion);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error obteniendo configuración: {ex.Message}");
                return StatusCode(500, "Error interno al obtener la configuración.");
            }
        }

        [HttpPost]
        public async Task<IActionResult> PostConfiguracion([FromBody] Configuracion configuracion)
        {
            if (configuracion == null)
                return BadRequest("Configuración no válida.");

            try
            {
                using (var transaction = await _context.Database.BeginTransactionAsync())
                {
                    _context.Configuracions.Add(configuracion);
                    await _context.SaveChangesAsync();
                    await transaction.CommitAsync();
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error al insertar configuración: {ex.Message}");
                return StatusCode(500, "Error interno al guardar la configuración.");
            }

            return Ok(configuracion);
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> PutConfiguracion(int id, [FromBody] Configuracion configuracion)
        {
            if (id != configuracion.IdConfig)
                return BadRequest("El ID de configuración no coincide con el enviado.");

            try
            {
                using (var transaction = await _context.Database.BeginTransactionAsync())
                {
                    _context.Entry(configuracion).State = EntityState.Modified;
                    await _context.SaveChangesAsync();
                    await transaction.CommitAsync();
                }
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!_context.Configuracions.Any(c => c.IdConfig == id))
                    return NotFound();

                return StatusCode(500, "Error de concurrencia al actualizar la configuración.");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error al actualizar configuración: {ex.Message}");
                return StatusCode(500, "Error interno al actualizar la configuración.");
            }

            return Ok(configuracion);
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteConfiguracion(int id)
        {
            try
            {
                var configuracion = await _context.Configuracions.FindAsync(id);
                if (configuracion == null)
                    return NotFound();

                using (var transaction = await _context.Database.BeginTransactionAsync())
                {
                    _context.Configuracions.Remove(configuracion);
                    await _context.SaveChangesAsync();
                    await transaction.CommitAsync();
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error al eliminar configuración: {ex.Message}");
                return StatusCode(500, "Error interno al eliminar la configuración.");
            }

            return Ok();
        }
    }
}
