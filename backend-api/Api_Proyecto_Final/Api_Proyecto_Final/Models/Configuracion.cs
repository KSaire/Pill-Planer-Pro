using System;
using System.Collections.Generic;

namespace Api_Proyecto_Final.Models;

public partial class Configuracion
{
    public int IdConfig { get; set; }

    public string? Tema { get; set; }

    public string? Idioma { get; set; }

    public virtual ICollection<Usuario> Usuarios { get; set; } = new List<Usuario>();
}
