using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Api_Proyecto_Final.Models;

namespace Api_Proyecto_Final.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class HorariosController : ControllerBase
    {
        private readonly AlarmaMedicamentosContext _context;

        public HorariosController(AlarmaMedicamentosContext context)
        {
            _context = context;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<Horario>>> GetHorarios()
        {
            try
            {
                var lista = await _context.Horarios.ToListAsync();

                return Ok(lista);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[GET horarios] Error: {ex.Message}");
                return StatusCode(500, "Error interno al obtener los horarios.");
            }
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<Horario>> GetHorario(int id)
        {
            try
            {
                var horario = await _context.Horarios.FindAsync(id);
                if (horario == null)
                    return NotFound();

                return Ok(horario);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[GET horario por ID] Error: {ex.Message}");
                return StatusCode(500, "Error interno al obtener el horario.");
            }
        }

        [HttpGet("usuario/{userId}")]
        public async Task<ActionResult<IEnumerable<Horario>>> GetHorariosPorUsuario(int userId)
        {
            try
            {
                var lista = await _context.Horarios
                    .Where(h => h.IdUsuario == userId).OrderBy(x=>x.Hora)
                    .ToListAsync();

                return Ok(lista);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[GET horarios por usuario] Error: {ex.Message}");
                return StatusCode(500, "Error interno al obtener los horarios del usuario.");
            }
        }

        [HttpPost]
        public async Task<IActionResult> PostHorario([FromBody] HorarioCreateDTO dto)
        {
            if (dto == null)
                return BadRequest("Datos de horario no válidos.");

            var entidad = new Horario
            {
                IdUsuario = dto.IdUsuario,
                CnMed = dto.CnMed,
                Hora = dto.Hora,
                Sonido = dto.Sonido,
                Vibracion = dto.Vibracion,
                Frecuencia = dto.Frecuencia,
                Dias = dto.Dias,
                Nombre = dto.NombreAlarma,
                Activa = true
            };

            try
            {
                using (var transaction = await _context.Database.BeginTransactionAsync())
                {
                    _context.Horarios.Add(entidad);
                    await _context.SaveChangesAsync();
                    await transaction.CommitAsync();
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[POST horario] Error: {ex.Message}");
                return StatusCode(500, "Error interno al guardar el horario.");
            }

            return Ok(entidad);
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> PutHorario(int id, [FromBody] HorarioCreateDTO dto)
        {
            var horario = await _context.Horarios.FindAsync(id);
            if (horario == null)
                return NotFound();

            try
            {
                using (var transaction = await _context.Database.BeginTransactionAsync())
                {
                    horario.CnMed = dto.CnMed;
                    horario.Hora = dto.Hora;
                    horario.Sonido = dto.Sonido;
                    horario.Vibracion = dto.Vibracion;
                    horario.Frecuencia = dto.Frecuencia;
                    horario.Dias = dto.Dias;
                    horario.Nombre = dto.NombreAlarma;
                    if(dto.Activa != null)
                    {
                        horario. Activa = (bool)dto.Activa;
                    }

                    _context.Entry(horario).State = EntityState.Modified;
                    await _context.SaveChangesAsync();
                    await transaction.CommitAsync();
                }
                return Ok(horario);
            }
            catch (DbUpdateConcurrencyException)
            {
                return NotFound("No se encontró el horario para actualizar.");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[PUT horario] Error: {ex.Message}");
                return StatusCode(500, "Error interno al actualizar el horario.");
            }
        }

        [HttpPatch("{id}/activo")]
        public async Task<IActionResult> PatchHorarioActivo(int id, [FromBody] bool activa)
        {
            var horario = await _context.Horarios.FindAsync(id);
            if (horario == null)
                return NotFound();

            horario.Activa = activa;

            try
            {
                await _context.SaveChangesAsync();
                return Ok(new { mensaje = "Horario actualizado correctamente", exito = true });
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[PATCH activo] Error: {ex.Message}");
                return StatusCode(500, "Error interno al actualizar 'activo'.");
            }
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteHorario(int id)
        {
            try
            {
                var horario = await _context.Horarios.FindAsync(id);
                if (horario == null)
                    return NotFound();

                using (var transaction = await _context.Database.BeginTransactionAsync())
                {
                    _context.Horarios.Remove(horario);
                    await _context.SaveChangesAsync();
                    await transaction.CommitAsync();
                }
                return Ok(new { mensaje = "Horario eliminado correctamente", exito = true });
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[DELETE horario] Error: {ex.Message}");
                return StatusCode(500, new { mensaje = "Error interno al eliminar el horario", exito = false });
            }
        }
    }
}
