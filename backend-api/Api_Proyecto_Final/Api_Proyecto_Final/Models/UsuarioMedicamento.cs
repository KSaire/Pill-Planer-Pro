using System;
using System.Collections.Generic;

namespace Api_Proyecto_Final.Models;

public partial class UsuarioMedicamento
{
    public int Id { get; set; }

    public int IdUsuario { get; set; }

    public string CnMed { get; set; } = null!;

    public virtual Medicamento CnMedNavigation { get; set; } = null!;

    public virtual Usuario IdUsuarioNavigation { get; set; } = null!;
    public virtual ICollection<Historial> Historiales { get; set; } = new List<Historial>();
}
