using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Api_Proyecto_Final.Models;

namespace Api_Proyecto_Final.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class NotificacionesController : ControllerBase
    {
        private readonly AlarmaMedicamentosContext _context;

        public NotificacionesController(AlarmaMedicamentosContext context)
        {
            _context = context;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<Notificacion>>> GetNotificacions()
        {
            try
            {
                var lista = await _context.Notificacions.ToListAsync();

                return Ok(lista);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error obteniendo notificaciones: {ex.Message}");
                return StatusCode(500, "Error interno al obtener las notificaciones.");
            }
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<Notificacion>> GetNotificacion(int id)
        {
            try
            {
                var notificacion = await _context.Notificacions.FindAsync(id);
                if (notificacion == null)
                    return NotFound();

                return Ok(notificacion);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error obteniendo notificación: {ex.Message}");
                return StatusCode(500, "Error interno al obtener la notificación.");
            }
        }

        [HttpPost]
        public async Task<IActionResult> PostNotificacion([FromBody] Notificacion notificacion)
        {
            if (notificacion == null)
                return BadRequest("Notificación no válida.");

            try
            {
                using (var transaction = await _context.Database.BeginTransactionAsync())
                {
                    _context.Notificacions.Add(notificacion);
                    await _context.SaveChangesAsync();
                    await transaction.CommitAsync();
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error al insertar notificación: {ex.Message}");
                return StatusCode(500, "Error interno al guardar la notificación.");
            }

            return Ok(notificacion);
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> PutNotificacion(int id, [FromBody] Notificacion notificacion)
        {
            if (id != notificacion.IdNotificacion)
                return BadRequest("El ID de la notificación no coincide con el enviado.");

            try
            {
                using (var transaction = await _context.Database.BeginTransactionAsync())
                {
                    _context.Entry(notificacion).State = EntityState.Modified;
                    await _context.SaveChangesAsync();
                    await transaction.CommitAsync();
                }
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!_context.Notificacions.Any(n => n.IdNotificacion == id))
                    return NotFound();

                return StatusCode(500, "Error de concurrencia al actualizar la notificación.");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error al actualizar notificación: {ex.Message}");
                return StatusCode(500, "Error interno al actualizar la notificación.");
            }

            return Ok(notificacion);
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteNotificacion(int id)
        {
            try
            {
                var notificacion = await _context.Notificacions.FindAsync(id);
                if (notificacion == null)
                    return NotFound();

                using (var transaction = await _context.Database.BeginTransactionAsync())
                {
                    _context.Notificacions.Remove(notificacion);
                    await _context.SaveChangesAsync();
                    await transaction.CommitAsync();
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error al eliminar notificación: {ex.Message}");
                return StatusCode(500, "Error interno al eliminar la notificación.");
            }

            return Ok();
        }
    }
}
