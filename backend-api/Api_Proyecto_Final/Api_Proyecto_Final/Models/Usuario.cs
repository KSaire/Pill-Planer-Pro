using System;
using System.Collections.Generic;

namespace Api_Proyecto_Final.Models;

public partial class Usuario
{
    public int Id { get; set; }

    public string Nombre { get; set; } = null!;

    public string Email { get; set; } = null!;

    public string Contrasena { get; set; } = null!;

    public DateOnly? FechaNacimiento { get; set; }

    public string? Telefono { get; set; }

    public int? IdConfig { get; set; }

    public virtual ICollection<Horario> Horarios { get; set; } = new List<Horario>();

    public virtual Configuracion? IdConfigNavigation { get; set; }

    public virtual ICollection<UsuarioMedicamento> UsuarioMedicamentos { get; set; } = new List<UsuarioMedicamento>();

    public virtual ICollection<Usuario> IdGestors { get; set; } = new List<Usuario>();

    public virtual ICollection<Usuario> IdPacientes { get; set; } = new List<Usuario>();

    public virtual ICollection<Rol> IdRols { get; set; } = new List<Rol>();
    public virtual ICollection<Cita> Citas { get; set; } = new List<Cita>();
}
