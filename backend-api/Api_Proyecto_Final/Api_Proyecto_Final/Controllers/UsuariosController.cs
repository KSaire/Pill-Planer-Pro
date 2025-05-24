using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Api_Proyecto_Final.Models;
using Humanizer;

namespace Api_Proyecto_Final.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class UsuariosController : ControllerBase
    {
        private readonly AlarmaMedicamentosContext _context;

        public UsuariosController(AlarmaMedicamentosContext context)
        {
            _context = context;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<Usuario>>> GetUsuarios()
        {
            try
            {
                var usuarios = await _context.Usuarios.ToListAsync();

                return Ok(usuarios);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error obteniendo usuarios: {ex.Message}");
                return StatusCode(500, "Error interno al obtener los usuarios.");
            }
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<Usuario>> GetUsuario(int id)
        {
            try
            {
                var usuario = await _context.Usuarios.FindAsync(id);
                if (usuario == null)
                    return NotFound();

                return Ok(usuario);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error obteniendo usuario: {ex.Message}");
                return StatusCode(500, "Error interno al obtener el usuario.");
            }
        }

        [HttpGet("gestor/{medId}/pacientes")]
        public async Task<ActionResult<IEnumerable<UsuarioRespuestaDTO>>> GetPacientesDeGestor(int medId)
        {
            try
            {
                var medico = await _context.Usuarios
                    .Include(u => u.IdPacientes)
                    .FirstOrDefaultAsync(u => u.Id == medId);

                if (medico == null)
                    return NotFound($"No existe ningún médico con Id {medId}.");

                var resultado = medico.IdPacientes
                    .Select(p => new UsuarioRespuestaDTO
                    {
                        Id = p.Id,
                        Email = p.Email,
                        Nombre = p.Nombre
                    })
                    .ToList();

                return Ok(resultado);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[GET pacientes de gestor] Error: {ex.Message}");
                return StatusCode(500, "Error interno al obtener la lista de pacientes.");
            }
        }

        [HttpGet("familiares/{pacienteId}")]
        public async Task<ActionResult<IEnumerable<UsuarioRespuestaDTO>>> GetFamiliares(int pacienteId)
        {
            try
            {
                var paciente = await _context.Usuarios.Include(u => u.IdGestors).FirstOrDefaultAsync(u => u.Id == pacienteId);

                if (paciente == null)
                    return NotFound($"No existe paciente con Id {pacienteId}.");

                var familiares = paciente.IdGestors
                    .Select(f => new UsuarioRespuestaDTO
                    {
                        Id = f.Id,
                        Nombre = f.Nombre,
                        Email = f.Email,
                        IdRols = f.IdRols.Select(r => r.Nombre).ToList()
                    })
                    .ToList();

                return Ok(familiares);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[GET familiares] Error: {ex.Message}");
                return StatusCode(500, "Error interno al obtener familiares.");
            }
        }

        [HttpGet("rol/{rolName}")]
        public async Task<ActionResult<IEnumerable<UsuarioRespuestaDTO>>> GetUsuariosPorRol(string rolName)
        {
            try
            {
                var rolNormalized = rolName.Trim().ToLower();

                var lista = await _context.Usuarios
                    .Include(u => u.IdRols)
                    .Where(u => u.IdRols.Any(r => r.Nombre.ToLower() == rolNormalized))
                    .Select(u => new UsuarioRespuestaDTO
                    {
                        Id = u.Id,
                        Nombre = u.Nombre,
                        Email = u.Email,
                        IdRols = u.IdRols.Select(r => r.Nombre).ToList()
                    })
                    .ToListAsync();

                return Ok(lista);
            } catch (Exception ex)
            {
                Console.WriteLine($"[GET familiares] Error: {ex.Message}");
                return StatusCode(500, "Error interno al obtener familiares.");
            }
        }

        [HttpPost]
        public async Task<IActionResult> PostUsuario([FromBody] UsuarioPeticionDTO udto)
        {
            if (udto == null)
                return BadRequest("Usuario no válido.");

            try
            {
                var rolEntidad = await _context.Rols.FirstOrDefaultAsync(r => r.Nombre.ToLower() == udto.Rol.ToLower());
                if (rolEntidad == null)
                    return BadRequest("El rol especificado no existe.");

                var usuarioExistente = await _context.Usuarios.Include(u => u.IdRols)
                        .FirstOrDefaultAsync(u => u.Email == udto.Email);

                using (var transaction = await _context.Database.BeginTransactionAsync())
                {
                    if (usuarioExistente == null)
                    {
                        var nuevoUser = new Usuario
                        {
                            Nombre = udto.Nombre,
                            Email = udto.Email,
                            Contrasena = udto.Contrasena,
                            IdRols = new List<Rol> { rolEntidad }
                        };
                        _context.Usuarios.Add(nuevoUser);
                        await _context.SaveChangesAsync();
                        await transaction.CommitAsync();

                        return Ok(new UsuarioRespuestaDTO
                        {
                            Id = nuevoUser.Id,
                            Nombre = nuevoUser.Nombre,
                            Contrasena = nuevoUser.Contrasena,
                            Email = nuevoUser.Email,
                            IdRols = nuevoUser.IdRols.Select(r => r.Nombre).ToList()
                        });
                    }
                    else
                    {
                        if (usuarioExistente.IdRols.Any(r => r.IdRol == rolEntidad.IdRol))
                            return Conflict("Este correo ya está registrado con ese mismo rol.");

                        usuarioExistente.IdRols.Add(rolEntidad);
                        _context.Usuarios.Update(usuarioExistente);
                        await _context.SaveChangesAsync();
                        await transaction.CommitAsync();

                        return Ok(new UsuarioRespuestaDTO
                        {
                            Id = usuarioExistente.Id,
                            Nombre = usuarioExistente.Nombre,
                            Contrasena = usuarioExistente.Contrasena,
                            Email = usuarioExistente.Email,
                            IdRols = usuarioExistente.IdRols.Select(r => r.Nombre).ToList()
                        });
                    }
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error al insertar usuario: {ex.Message}");
                return StatusCode(500, "Error interno al guardar el usuario.");
            }
        }

        [HttpPost("login")]
        public async Task<IActionResult> LoginUsuario([FromBody] UsuarioPeticionDTO udto)
        {
            if (udto == null)
                return BadRequest("Petición de login inválida.");

            try
            {
                var rolSolicitado = udto.Rol.Trim().ToLower();
                var usuario = await _context.Usuarios.Include(u => u.IdRols).FirstOrDefaultAsync
                    (u => u.Email == udto.Email && u.Contrasena == udto.Contrasena);

                if (usuario == null)
                    return Unauthorized("Email o contraseña incorrectos.");

                var hasRole = usuario.IdRols.Any(r => r.Nombre.ToLower() == rolSolicitado);

                if (!hasRole)
                    return StatusCode(403, "El usuario no tiene asignado ese rol.");

                var respuesta = new UsuarioRespuestaDTO
                {
                    Id = usuario.Id,
                    Nombre = usuario.Nombre,
                    Contrasena = usuario.Contrasena,
                    Email = usuario.Email,
                    IdRols = usuario.IdRols.Select(r => r.Nombre.ToLower()).ToList()
                };

                return Ok(respuesta);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[POST login] Error: {ex.Message}");
                return StatusCode(500, "Error interno al procesar el login.");
            }
        }

        [HttpPost("familiares")]
        public async Task<IActionResult> AddFamiliar([FromBody] FamiliarPeticionDTO dto)
        {
            if (dto == null)
                return BadRequest("Datos inválidos.");

            try
            {
                var paciente = await _context.Usuarios.Include(u => u.IdGestors).FirstOrDefaultAsync(u => u.Id == dto.PacienteId);
                if (paciente == null)
                    return NotFound($"Paciente {dto.PacienteId} no encontrado.");

                var user = await _context.Usuarios.Include(u => u.IdRols).FirstOrDefaultAsync(u => u.Email == dto.EmailFamiliar);
                if (user == null)
                    return NotFound($"No existe usuario con email '{dto.EmailFamiliar}'.");

                if (user.Id == paciente.Id)
                    return BadRequest("Un paciente no puede añadirse a sí mismo como familiar.");

                string rolFamiliar = "familiar";

                var tieneRolFamiliar = user.IdRols.Any(r => r.Nombre.ToLower() == rolFamiliar); ;
                if (!tieneRolFamiliar)
                    return BadRequest("El usuario encontrado no tiene el rol de familiar.");

                if (paciente.IdGestors.Any(f => f.Id == user.Id))
                    return Conflict("Ese familiar ya está asociado a este paciente.");

                var resp = new UsuarioRespuestaDTO
                {
                    Id = user.Id,
                    Nombre = user.Nombre,
                    Email = user.Email,
                    IdRols = user.IdRols.Select(r => r.Nombre).ToList()
                };

                using (var transaction = await _context.Database.BeginTransactionAsync())
                {
                    paciente.IdGestors.Add(user);
                    await _context.SaveChangesAsync();
                    await transaction.CommitAsync();
                }
                return Ok(resp);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[POST familiares] Error: {ex.Message}");
                return StatusCode(500, "Error interno al asociar familiar.");
            }
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> PutUsuario(int id, [FromBody] UsuarioPeticionDTO udto)
        {
            var usuario = await _context.Usuarios.Include(u => u.IdRols).FirstOrDefaultAsync(u => u.Id == id);
            if (usuario == null)
                return BadRequest("El ID del usuario no existe.");

            try
            {
                var nuevoRol = await _context.Rols.FirstOrDefaultAsync(r => r.Nombre.ToLower() == udto.Rol.ToLower());

                if (nuevoRol == null)
                    return BadRequest("El rol especificado no existe.");

                using (var transaction = await _context.Database.BeginTransactionAsync())
                {
                    usuario.Nombre = udto.Nombre;
                    usuario.Email = udto.Email;
                    usuario.Contrasena = udto.Contrasena;
                    if (udto.FechaNacimiento.HasValue)
                        usuario.FechaNacimiento = udto.FechaNacimiento;

                    if (!string.IsNullOrWhiteSpace(udto.Telefono))
                        usuario.Telefono = udto.Telefono;

                    if (udto.IdConfig != null)
                        usuario.IdConfig = udto.IdConfig;

                    var existe = usuario.IdRols.Any(r => r.Nombre.ToLower() == udto.Rol.ToLower());

                    if (existe)
                    {
                        usuario.IdRols.Add(nuevoRol);
                    }

                    _context.Entry(usuario).State = EntityState.Modified;
                    await _context.SaveChangesAsync();
                    await transaction.CommitAsync();

                    return Ok(new UsuarioRespuestaDTO
                    {
                        Id = usuario.Id,
                        Nombre = usuario.Nombre,
                        Contrasena = usuario.Contrasena,
                        Email = usuario.Email,
                        IdRols = usuario.IdRols.Select(r => r.Nombre.ToLower()).ToList()
                    });
                }
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!_context.Usuarios.Any(u => u.Id == id))
                    return NotFound();

                return StatusCode(500, "Error de concurrencia al actualizar el usuario.");
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error al actualizar usuario: {ex.Message}");
                return StatusCode(500, "Error interno al actualizar el usuario.");
            }
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteUsuario(int id)
        {
            try
            {
                var usuario = await _context.Usuarios.FindAsync(id);
                if (usuario == null)
                    return NotFound();

                using (var transaction = await _context.Database.BeginTransactionAsync())
                {
                    _context.Usuarios.Remove(usuario);
                    await _context.SaveChangesAsync();
                    await transaction.CommitAsync();
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Error al eliminar usuario: {ex.Message}");
                return StatusCode(500, "Error interno al eliminar el usuario.");
            }

            return Ok();
        }

        [HttpDelete("familiares")]
        public async Task<IActionResult> RemoveFamiliar(int pacienteId, int familiarId)
        {
            try
            {
                var paciente = await _context.Usuarios.Include(u => u.IdGestors).FirstOrDefaultAsync(u => u.Id == pacienteId);
                if (paciente == null)
                    return NotFound($"Paciente {pacienteId} no existe.");

                var fam = paciente.IdGestors.FirstOrDefault(f => f.Id == familiarId);
                if (fam == null)
                    return NotFound("Ese familiar no está asociado al paciente.");

                
                using (var tx = await _context.Database.BeginTransactionAsync())
                {
                    paciente.IdGestors.Remove(fam);
                    await _context.SaveChangesAsync();
                    await tx.CommitAsync();
                }

                return Ok(new { mensaje = "Familiar desasociado correctamente", exito = true });
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[DELETE familiares] Error: {ex.Message}");
                return StatusCode(500, "Error interno al desasociar familiar.");
            }
        }
    }
}
