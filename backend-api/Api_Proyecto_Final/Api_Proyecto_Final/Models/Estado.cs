using System;
using System.Collections.Generic;

namespace Api_Proyecto_Final.Models;

public partial class Estado
{
    public int IdEstado { get; set; }

    public string Nombre { get; set; } = null!;

    public virtual ICollection<Notificacion> Notificacions { get; set; } = new List<Notificacion>();
}
