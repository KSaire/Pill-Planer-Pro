namespace Api_Proyecto_Final.Models
{
    public class Cita
    {
        public int IdCita { get; set; }
        public int IdUsuario { get; set; }
        public DateTime FechaHora { get; set; }
        public string Descripcion { get; set; }

        public virtual Usuario Usuario { get; set; }
    }
}