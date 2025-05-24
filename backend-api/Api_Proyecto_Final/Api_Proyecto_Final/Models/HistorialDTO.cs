namespace Api_Proyecto_Final.Models
{
    public class HistorialDTO
    {
        public int IdUsuarioMedicamento { get; set; }
        public int IdHorario { get; set; }
        public DateTime FechaHora { get; set; }
        public bool Tomado { get; set; }
    }
}
