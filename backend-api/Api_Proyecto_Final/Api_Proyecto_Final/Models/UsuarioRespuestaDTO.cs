namespace Api_Proyecto_Final.Models
{
    public class UsuarioRespuestaDTO
    {
        public int? Id { get; set; }
        public string Nombre { get; set; } = null!;
        public string Email { get; set; } = null!;
        public string Contrasena { get; set; } = null!;
        public List<string> IdRols { get; set; } = new();
    }
}
