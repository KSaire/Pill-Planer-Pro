namespace Api_Proyecto_Final.Models
{
    public class UsuarioPeticionDTO
    {
        public string? Nombre { get; set; }
        public string Email { get; set; } = null!;
        public string Contrasena { get; set; } = null!;
        public DateOnly? FechaNacimiento { get; set; }
        public string? Telefono { get; set; }
        public int? IdConfig { get; set; }
        public string Rol { get; set; } = null!;
    }
}
