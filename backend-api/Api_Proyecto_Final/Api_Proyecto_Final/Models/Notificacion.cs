using System;
using System.Collections.Generic;

namespace Api_Proyecto_Final.Models;

public partial class Notificacion
{
    public int IdNotificacion { get; set; }

    public int? IdHorario { get; set; }

    public int? IdEstado { get; set; }

    public DateTime FechaHora { get; set; }

    public string? Titulo { get; set; }

    public string? Comentario { get; set; }

    public virtual Estado? IdEstadoNavigation { get; set; }

    public virtual Horario? IdHorarioNavigation { get; set; }
}
