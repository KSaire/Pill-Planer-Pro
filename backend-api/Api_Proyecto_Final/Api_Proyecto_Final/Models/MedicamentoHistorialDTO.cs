namespace Api_Proyecto_Final.Models
{
    public class MedicamentoHistorialDTO
    {
        public int IdUsuarioMedicamento { get; set; }
        public string NombreMedicamento { get; set; } = null!;
        public List<EventoMedicacionDTO> Historial { get; set; } = new();
    }
}
