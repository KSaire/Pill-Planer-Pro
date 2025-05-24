using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Api_Proyecto_Final.Models;

namespace Api_Proyecto_Final.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class CitasController : ControllerBase
    {
        private readonly AlarmaMedicamentosContext _context;

        public CitasController(AlarmaMedicamentosContext context)
        {
            _context = context;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<Cita>>> GetCitas()
        {
            try
            {
                var lista = await _context.Citas.ToListAsync();
                return Ok(lista);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[GET citas] Error: {ex.Message}");
                return StatusCode(500, "Error interno al obtener las citas.");
            }
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<Cita>> GetCita(int id)
        {
            try
            {
                var cita = await _context.Citas.FindAsync(id);
                if (cita == null)
                    return NotFound();

                return Ok(cita);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[GET cita por ID] Error: {ex.Message}");
                return StatusCode(500, "Error interno al obtener la cita.");
            }
        }

        [HttpGet("usuario/{userId}")]
        public async Task<ActionResult<IEnumerable<Cita>>> GetPorUsuario(int userId)
        {
            try
            {
                var lista = await _context.Citas.Where(c => c.IdUsuario == userId).OrderBy(c => c.FechaHora).ToListAsync();

                return Ok(lista);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[GET citas por usuario] Error: {ex.Message}");
                return StatusCode(500, "Error interno al obtener las citas del usuario.");
            }
        }

        [HttpPost]
        public async Task<ActionResult> PostCita([FromBody] CitaDTO dto)
        {
            if (dto == null)
                return BadRequest("Datos de cita no válidos.");

            var entidad = new Cita
            {
                IdUsuario = dto.IdUsuario,
                FechaHora = dto.FechaHora,
                Descripcion = dto.Descripcion
            };

            try
            {
                using (var tx = await _context.Database.BeginTransactionAsync())
                {
                    _context.Citas.Add(entidad);
                    await _context.SaveChangesAsync();
                    await tx.CommitAsync();
                }
                return Ok(entidad);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[POST cita] Error: {ex.Message}");
                return StatusCode(500, "Error interno al guardar la cita.");
            }
        }

        [HttpPut("{id}")]
        public async Task<ActionResult> PutCita(int id, [FromBody] CitaDTO dto)
        {
            var cita = await _context.Citas.FindAsync(id);
            if (cita == null)
                return NotFound();

            cita.FechaHora = dto.FechaHora;
            cita.Descripcion = dto.Descripcion;

            try
            {
                using (var tx = await _context.Database.BeginTransactionAsync())
                {
                    _context.Entry(cita).State = EntityState.Modified;
                    await _context.SaveChangesAsync();
                    await tx.CommitAsync();
                }
                return Ok(cita);
            }
            catch (DbUpdateConcurrencyException)
            {
                return NotFound("No se encontró la cita para actualizar.");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[PUT cita] Error: {ex.Message}");
                return StatusCode(500, "Error interno al actualizar la cita.");
            }
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteCita(int id)
        {
            var cita = await _context.Citas.FindAsync(id);
            if (cita == null)
                return NotFound();

            try
            {
                using (var tx = await _context.Database.BeginTransactionAsync())
                {
                    _context.Citas.Remove(cita);
                    await _context.SaveChangesAsync();
                    await tx.CommitAsync();
                }
                return Ok(new { mensaje = "Cita eliminada correctamente", exito = true });
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[DELETE cita] Error: {ex.Message}");
                return StatusCode(500, new { mensaje = "Error interno al eliminar la cita", exito = false });
            }
        }
    }
}
