namespace Api_Proyecto_Final.Models
{
    public partial class Historial
    {
        public int IdHistorial { get; set; }
        public int IdUsuarioMedicamento { get; set; }
        public int IdHorario { get; set; }
        public DateTime FechaHora { get; set; }
        public bool Tomado { get; set; }

        public virtual UsuarioMedicamento IdUsuarioMedicamentoNavigation { get; set; } = null!;
        public virtual Horario IdHorarioNavigation { get; set; } = null!;
    }
}
