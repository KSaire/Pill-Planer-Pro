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
    public class UsuarioMedicamentosController : ControllerBase
    {
        private readonly AlarmaMedicamentosContext _context;

        public UsuarioMedicamentosController(AlarmaMedicamentosContext context)
        {
            _context = context;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<UsuarioMedicamento>>> GetUsuarioMedicamentos()
        {
            try
            {
                var lista = await _context.UsuarioMedicamentos.ToListAsync();
                return Ok(lista);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[GET usuario-medicamentos] Error: {ex.Message}");
                return StatusCode(500, "Error interno al obtener las asociaciones usuario-medicamento.");
            }
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<UsuarioMedicamento>> GetUsuarioMedicamento(int id)
        {
            try
            {
                var um = await _context.UsuarioMedicamentos.FindAsync(id);
                if (um == null)
                    return NotFound();
                return Ok(um);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[GET usuario-medicamento por ID] Error: {ex.Message}");
                return StatusCode(500, "Error interno al obtener la asociación.");
            }
        }

        [HttpPost]
        public async Task<IActionResult> PostUsuarioMedicamento([FromBody] UsuarioMedicamentoDto dto)
        {
            if (dto == null || dto.UserId <= 0 || string.IsNullOrWhiteSpace(dto.CnMed))
                return BadRequest("Parámetros inválidos.");

            try
            {
                var userExists = await _context.Usuarios.AnyAsync(u => u.Id == dto.UserId);
                if (!userExists)
                    return NotFound($"Usuario con ID={dto.UserId} no existe.");

                var medExists = await _context.Medicamentos.AnyAsync(m => m.Cn == dto.CnMed);
                if (!medExists)
                    return NotFound($"Medicamento con CN='{dto.CnMed}' no existe.");

                var already = await _context.UsuarioMedicamentos
                    .AnyAsync(um => um.IdUsuario == dto.UserId && um.CnMed == dto.CnMed);
                if (already)
                    return Conflict("Esta asociación usuario–medicamento ya existe.");

                var um = new UsuarioMedicamento
                {
                    IdUsuario = dto.UserId,
                    CnMed = dto.CnMed
                };

                using (var tx = await _context.Database.BeginTransactionAsync())
                {
                    _context.UsuarioMedicamentos.Add(um);
                    await _context.SaveChangesAsync();
                    await tx.CommitAsync();
                }
                return Ok(um);
            }
            catch (DbUpdateException dbEx) when (dbEx.InnerException?.Message.Contains("UQ_Usuario_Medicamento") == true)
            {
                return Conflict("Este usuario ya tiene asignado ese medicamento.");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[POST usuario-medicamento] Error: {ex.Message}");
                return StatusCode(500, "Error interno al guardar la asociación.");
            }
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> PutUsuarioMedicamento(int id, [FromBody] UsuarioMedicamento um)
        {
            if (id != um.Id)
                return BadRequest("El ID no coincide con la entidad enviada.");

            try
            {
                using (var tx = await _context.Database.BeginTransactionAsync())
                {
                    _context.Entry(um).State = EntityState.Modified;
                    await _context.SaveChangesAsync();
                    await tx.CommitAsync();
                }
                return Ok(um);
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!_context.UsuarioMedicamentos.Any(m => m.Id == id))
                    return NotFound();
                return StatusCode(500, "Error de concurrencia al actualizar la asociación.");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[PUT usuario-medicamento] Error: {ex.Message}");
                return StatusCode(500, "Error interno al actualizar la asociación.");
            }
        }

        [HttpDelete]
        public async Task<IActionResult> DeleteUsuarioMedicamento(int userId, string medCn)
        {
            try
            {
                var um = await _context.UsuarioMedicamentos.FirstOrDefaultAsync(um => um.IdUsuario == userId && um.CnMed == medCn);
                if (um == null)
                    return NotFound(new { mensaje = "No se encontró la relación usuario-medicamento.", exito = false });

                var horarios = await _context.Horarios.Where(h => h.IdUsuario == userId && h.CnMed == medCn).ToListAsync();
             
                using (var tx = await _context.Database.BeginTransactionAsync())
                {
                    foreach (var horario in horarios)
                    {
                        var notificaciones = await _context.Notificacions.Where(h => h.IdHorario == horario.Id).ToListAsync(); ;
                        if (notificaciones.Any())
                            _context.Notificacions.RemoveRange(notificaciones);
                    }

                    if (horarios.Any())
                        _context.Horarios.RemoveRange(horarios);

                    _context.UsuarioMedicamentos.Remove(um);
                    await _context.SaveChangesAsync();
                    await tx.CommitAsync();
                }
                return Ok(new { mensaje = "Medicamento eliminado correctamente", exito = true });
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[DELETE usuario-medicamento] Error: {ex.Message}");
                return StatusCode(500, "Error interno al eliminar la asociación.");
            }
        }
    }
}
