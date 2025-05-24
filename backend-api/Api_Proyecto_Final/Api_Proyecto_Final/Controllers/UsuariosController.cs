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

        [HttpPost]
        public async Task<IActionResult> PostUsuario([FromBody] UsuarioPeticionDTO udto)
        {
            if (udto == null)
                return BadRequest("Usuario no válido.");

            try
            {
                var existe = await _context.Usuarios.AnyAsync(u => u.Email == udto.Email);
                if (existe)
                    return Conflict("Ya existe un usuario registrado con ese correo.");

                var rol = await _context.Rols.Where(r => r.Nombre.ToLower() == udto.Rol).ToListAsync();

                using (var transaction = await _context.Database.BeginTransactionAsync())
                {
                    var usuario = new Usuario
                    {
                        Nombre = udto.Nombre,
                        Email = udto.Email,
                        Contrasena = udto.Contrasena,
                        IdRols = rol
                    };
                    _context.Usuarios.Add(usuario);
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
                    (u => u.Email == udto.Email && u.Contrasena == udto.Contrasena );

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
    }
}
