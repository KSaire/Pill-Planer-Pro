using System;
using System.Collections.Generic;

namespace Api_Proyecto_Final.Models;

public partial class Medicamento
{
    public string Cn { get; set; } = null!;

    public string Nregistro { get; set; } = null!;

    public string? Nombre { get; set; }

    public string? Dosis { get; set; }

    public string? FormaFarmaceutica { get; set; }

    public string? ViaAdministracion { get; set; }

    public bool? RecetaMedica { get; set; }

    public virtual ICollection<Horario> Horarios { get; set; } = new List<Horario>();

    public virtual ICollection<UsuarioMedicamento> UsuarioMedicamentos { get; set; } = new List<UsuarioMedicamento>();
}
