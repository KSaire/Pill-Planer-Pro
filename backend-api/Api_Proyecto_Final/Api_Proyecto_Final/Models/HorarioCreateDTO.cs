namespace Api_Proyecto_Final.Models
{
    public class HorarioCreateDTO
    {
        public int IdUsuario { get; set; }
        public string? CnMed { get; set; }
        public TimeOnly Hora { get; set; }
        public bool Sonido { get; set; }
        public bool Vibracion { get; set; }
        public string Frecuencia { get; set; } = "";
        public string Dias { get; set; } = "";
        public string NombreAlarma { get; set; } = "";
        public bool? Activa { get; set; }
    }

}
