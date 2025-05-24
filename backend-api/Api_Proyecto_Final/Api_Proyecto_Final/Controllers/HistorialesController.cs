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
    public class HistorialesController : ControllerBase
    {
        private readonly AlarmaMedicamentosContext _context;

        public HistorialesController(AlarmaMedicamentosContext context)
        {
            _context = context;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<Historial>>> GetHistoriales()
        {
            try
            {
                var lista = await _context.Historiales.ToListAsync();
                return Ok(lista);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[GET Historiales] Error: {ex.Message}");
                return StatusCode(500, "Error interno al obtener el historial.");
            }
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<Historial>> GetHistorial(int id)
        {
            try
            {
                var hist = await _context.Historiales.FindAsync(id);
                if (hist == null)
                    return NotFound();

                return Ok(hist);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[GET Historial por ID] Error: {ex.Message}");
                return StatusCode(500, "Error interno al obtener el historial.");
            }
        }

        [HttpGet("usuario/{userId}")]
        public async Task<ActionResult<IEnumerable<MedicamentoHistorialDTO>>> GetHistorialPorUsuario(int userId)
        {
            try
            {
                var ums = await _context.UsuarioMedicamentos.Include(um => um.CnMedNavigation).Where(um => um.IdUsuario == userId).ToListAsync();

                var result = new List<MedicamentoHistorialDTO>(ums.Count);

                foreach (var um in ums)
                {
                    var eventos = await _context.Historiales.Where(h => h.IdUsuarioMedicamento == um.Id).OrderByDescending(h => h.FechaHora)
                        .Select(h => new EventoMedicacionDTO{ FechaHora = h.FechaHora,Tomado = h.Tomado }).ToListAsync();

                    if (eventos.Any())
                    {
                        result.Add(new MedicamentoHistorialDTO
                        {
                            IdUsuarioMedicamento = um.Id,
                            NombreMedicamento = um.CnMedNavigation.Nombre,
                            Historial = eventos
                        });
                    }
                }

                return Ok(result);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[GET Historial por UsuarioMedicamento] Error: {ex.Message}");
                return StatusCode(500, "Error interno al obtener el historial del usuario-medicamento.");
            }
        }

        [HttpPost]
        public async Task<IActionResult> PostHistorial([FromBody] HistorialDTO dto)
        {
            if (dto == null)
                return BadRequest("Datos de historial no válidos.");

            var entidad = new Historial
            {
                IdUsuarioMedicamento = dto.IdUsuarioMedicamento,
                IdHorario = dto.IdHorario,
                FechaHora = dto.FechaHora,
                Tomado = dto.Tomado
            };

            try
            {
                using (var tx = await _context.Database.BeginTransactionAsync())
                {
                    _context.Historiales.Add(entidad);
                    await _context.SaveChangesAsync();
                    await tx.CommitAsync();
                }
                    return Ok(entidad);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[POST Historial] Error: {ex.Message}");
                return StatusCode(500, "Error interno al guardar el historial.");
            }
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> PutHistorial(int id, [FromBody] HistorialDTO dto)
        {
            var entidad = await _context.Historiales.FindAsync(id);
            if (entidad == null)
                return NotFound("No se encontró el historial para actualizar.");

            entidad.IdUsuarioMedicamento = dto.IdUsuarioMedicamento;
            entidad.IdHorario = dto.IdHorario;
            entidad.FechaHora = dto.FechaHora;
            entidad.Tomado = dto.Tomado;

            try
            {
                using var tx = await _context.Database.BeginTransactionAsync();

                _context.Entry(entidad).State = EntityState.Modified;
                await _context.SaveChangesAsync();

                await tx.CommitAsync();
                return Ok(entidad);
            }
            catch (DbUpdateConcurrencyException)
            {
                return NotFound("No se encontró el historial para actualizar.");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[PUT Historial] Error: {ex.Message}");
                return StatusCode(500, "Error interno al actualizar el historial.");
            }
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteHistorial(int id)
        {
            var entidad = await _context.Historiales.FindAsync(id);
            if (entidad == null)
                return NotFound("No se encontró el historial para eliminar.");

            try
            {
                using var tx = await _context.Database.BeginTransactionAsync();

                _context.Historiales.Remove(entidad);
                await _context.SaveChangesAsync();

                await tx.CommitAsync();
                return Ok(new { mensaje = "Historial eliminado correctamente", exito = true });
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[DELETE Historial] Error: {ex.Message}");
                return StatusCode(500, new { mensaje = "Error interno al eliminar el historial", exito = false });
            }
        }
    }
}
