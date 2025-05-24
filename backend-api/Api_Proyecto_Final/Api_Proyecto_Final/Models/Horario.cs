using System;
using System.Collections.Generic;

namespace Api_Proyecto_Final.Models;

public partial class Horario
{
    public int Id { get; set; }

    public int? IdUsuario { get; set; }

    public string? CnMed { get; set; }

    public TimeOnly Hora { get; set; }

    public string? Frecuencia { get; set; }

    public bool Sonido { get; set; }

    public bool Vibracion { get; set; }

    public string? Dias { get; set; }

    public string Nombre { get; set; } = null!;

    public bool Activa { get; set; }

    public virtual Medicamento? CnMedNavigation { get; set; }

    public virtual Usuario? IdUsuarioNavigation { get; set; }

    public virtual ICollection<Notificacion> Notificacions { get; set; } = new List<Notificacion>();
    public virtual ICollection<Historial> Historiales { get; set; } = new List<Historial>();
}
