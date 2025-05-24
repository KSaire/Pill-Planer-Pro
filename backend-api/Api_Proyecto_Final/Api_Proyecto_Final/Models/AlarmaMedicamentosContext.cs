using System;
using System.Collections.Generic;
using Microsoft.EntityFrameworkCore;

namespace Api_Proyecto_Final.Models;

public partial class AlarmaMedicamentosContext : DbContext
{
    public AlarmaMedicamentosContext()
    {
    }

    public AlarmaMedicamentosContext(DbContextOptions<AlarmaMedicamentosContext> options)
        : base(options)
    {
    }

    public virtual DbSet<Cita> Citas { get; set; }
    public virtual DbSet<Configuracion> Configuracions { get; set; }

    public virtual DbSet<Estado> Estados { get; set; }

    public virtual DbSet<Historial> Historiales { get; set; }

    public virtual DbSet<Horario> Horarios { get; set; }

    public virtual DbSet<Medicamento> Medicamentos { get; set; }

    public virtual DbSet<Notificacion> Notificacions { get; set; }

    public virtual DbSet<Rol> Rols { get; set; }

    public virtual DbSet<Usuario> Usuarios { get; set; }

    public virtual DbSet<UsuarioMedicamento> UsuarioMedicamentos { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Cita>(entity =>
        {
            entity.HasKey(e => e.IdCita).HasName("PK__Cita__xxxxxx");
            entity.ToTable("Citas");

            entity.Property(e => e.IdCita).HasColumnName("Id");
            entity.Property(e => e.IdUsuario).HasColumnName("IdUsuario");
            entity.Property(e => e.FechaHora)
                  .HasColumnType("datetime")
                  .HasColumnName("FechaHora");
            entity.Property(e => e.Descripcion)
                  .HasMaxLength(255)
                  .HasColumnName("Descripcion");

            entity.HasOne(d => d.Usuario)
                  .WithMany(u => u.Citas)
                  .HasForeignKey(d => d.IdUsuario)
                  .OnDelete(DeleteBehavior.Cascade)
                  .HasConstraintName("FK_Cita_Usuario");
        });

        modelBuilder.Entity<Configuracion>(entity =>
        {
            entity.HasKey(e => e.IdConfig).HasName("PK__Configur__009E1BFACC148E4B");

            entity.ToTable("Configuracion");

            entity.Property(e => e.IdConfig).HasColumnName("ID_Config");
            entity.Property(e => e.Idioma).HasMaxLength(50);
            entity.Property(e => e.Tema).HasMaxLength(50);
        });

        modelBuilder.Entity<Estado>(entity =>
        {
            entity.HasKey(e => e.IdEstado).HasName("PK__Estado__9CF49395A56B061B");

            entity.ToTable("Estado");

            entity.HasIndex(e => e.Nombre, "UQ__Estado__75E3EFCF41C8DBEA").IsUnique();

            entity.Property(e => e.IdEstado).HasColumnName("ID_Estado");
            entity.Property(e => e.Nombre).HasMaxLength(50);
        });

        modelBuilder.Entity<Historial>(entity =>
        {
            entity.HasKey(e => e.IdHistorial).HasName("PK_Historial");
            entity.ToTable("Historial");

            entity.Property(e => e.IdHistorial)
                  .HasColumnName("IdHistorial");

            entity.Property(e => e.IdUsuarioMedicamento)
                  .HasColumnName("IdUsuarioMedicamento");

            entity.Property(e => e.IdHorario)
                  .HasColumnName("IdHorario");

            entity.Property(e => e.FechaHora)
                  .HasColumnType("datetime2")
                  .HasColumnName("FechaHora");

            entity.Property(e => e.Tomado)
                  .HasColumnName("Tomado");

            entity.HasOne(d => d.IdUsuarioMedicamentoNavigation)
                  .WithMany(p => p.Historiales)
                  .HasForeignKey(d => d.IdUsuarioMedicamento)
                  .OnDelete(DeleteBehavior.NoAction)
                  .HasConstraintName("FK_Historial_UsuarioMedicamento");

            entity.HasOne(d => d.IdHorarioNavigation)
                  .WithMany(p => p.Historiales)
                  .HasForeignKey(d => d.IdHorario)
                  .OnDelete(DeleteBehavior.NoAction)
                  .HasConstraintName("FK_Historial_Horario");
        });


        modelBuilder.Entity<Horario>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PK__Horario__3214EC27A7592CC3");

            entity.ToTable("Horario");

            entity.Property(e => e.Id).HasColumnName("ID");
            entity.Property(e => e.CnMed)
                .HasMaxLength(50)
                .HasColumnName("Cn_Med");
            entity.Property(e => e.Dias).HasMaxLength(20);
            entity.Property(e => e.IdUsuario).HasColumnName("ID_Usuario");
            entity.Property(e => e.Nombre)
                .HasMaxLength(100)
                .HasDefaultValue("");
            entity.Property(e => e.Sonido).HasDefaultValue(true);
            entity.Property(e => e.Vibracion).HasDefaultValue(true);
            entity.Property(e => e.Activa).HasDefaultValue(true);

            entity.HasOne(d => d.CnMedNavigation).WithMany(p => p.Horarios)
                .HasForeignKey(d => d.CnMed)
                .HasConstraintName("FK__Horario__Cn_Med__4BAC3F29");

            entity.HasOne(d => d.IdUsuarioNavigation).WithMany(p => p.Horarios)
                .HasForeignKey(d => d.IdUsuario)
                .HasConstraintName("FK__Horario__ID_Usua__4AB81AF0");
        });

        modelBuilder.Entity<Medicamento>(entity =>
        {
            entity.HasKey(e => e.Cn).HasName("PK__Medicame__32149A50258A312E");

            entity.ToTable("Medicamento");

            entity.Property(e => e.Cn).HasMaxLength(50);
            entity.Property(e => e.Dosis).HasMaxLength(200);
            entity.Property(e => e.FormaFarmaceutica).HasMaxLength(200);
            entity.Property(e => e.Nombre).HasMaxLength(200);
            entity.Property(e => e.Nregistro).HasMaxLength(50);
            entity.Property(e => e.RecetaMedica)
                .HasDefaultValue(false)
                .HasColumnName("Receta_Medica");
            entity.Property(e => e.ViaAdministracion).HasMaxLength(200);
        });

        modelBuilder.Entity<Notificacion>(entity =>
        {
            entity.HasKey(e => e.IdNotificacion).HasName("PK__Notifica__283380F1885769B0");

            entity.ToTable("Notificacion");

            entity.Property(e => e.IdNotificacion).HasColumnName("ID_Notificacion");
            entity.Property(e => e.Comentario).HasMaxLength(255);
            entity.Property(e => e.FechaHora)
                .HasColumnType("datetime")
                .HasColumnName("Fecha_Hora");
            entity.Property(e => e.IdEstado).HasColumnName("ID_Estado");
            entity.Property(e => e.IdHorario).HasColumnName("ID_Horario");
            entity.Property(e => e.Titulo).HasMaxLength(255);

            entity.HasOne(d => d.IdEstadoNavigation).WithMany(p => p.Notificacions)
                .HasForeignKey(d => d.IdEstado)
                .HasConstraintName("FK__Notificac__ID_Es__52593CB8");

            entity.HasOne(d => d.IdHorarioNavigation).WithMany(p => p.Notificacions)
                .HasForeignKey(d => d.IdHorario)
                .HasConstraintName("FK__Notificac__ID_Ho__5165187F");
        });

        modelBuilder.Entity<Rol>(entity =>
        {
            entity.HasKey(e => e.IdRol).HasName("PK__Rol__202AD22059C8C4A7");

            entity.ToTable("Rol");

            entity.HasIndex(e => e.Nombre, "UQ__Rol__75E3EFCFCE1952EA").IsUnique();

            entity.Property(e => e.IdRol).HasColumnName("ID_Rol");
            entity.Property(e => e.Nombre).HasMaxLength(50);
        });

        modelBuilder.Entity<Usuario>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PK__Usuario__3214EC27B30C8A82");

            entity.ToTable("Usuario");

            entity.HasIndex(e => e.Email, "UQ__Usuario__A9D105348F2DBEF5").IsUnique();

            entity.Property(e => e.Id).HasColumnName("ID");
            entity.Property(e => e.Contrasena).HasMaxLength(255);
            entity.Property(e => e.Email).HasMaxLength(100);
            entity.Property(e => e.FechaNacimiento).HasColumnName("Fecha_Nacimiento");
            entity.Property(e => e.IdConfig).HasColumnName("ID_Config");
            entity.Property(e => e.Nombre).HasMaxLength(100);
            entity.Property(e => e.Telefono).HasMaxLength(20);

            entity.HasOne(d => d.IdConfigNavigation).WithMany(p => p.Usuarios)
                .HasForeignKey(d => d.IdConfig)
                .HasConstraintName("FK__Usuario__ID_Conf__3A81B327");

            entity.HasMany(d => d.IdGestors).WithMany(p => p.IdPacientes)
                .UsingEntity<Dictionary<string, object>>(
                    "Gestor",
                    r => r.HasOne<Usuario>().WithMany()
                        .HasForeignKey("IdGestor")
                        .OnDelete(DeleteBehavior.ClientSetNull)
                        .HasConstraintName("FK__Gestor__ID_Gesto__44FF419A"),
                    l => l.HasOne<Usuario>().WithMany()
                        .HasForeignKey("IdPaciente")
                        .OnDelete(DeleteBehavior.ClientSetNull)
                        .HasConstraintName("FK__Gestor__ID_Pacie__440B1D61"),
                    j =>
                    {
                        j.HasKey("IdPaciente", "IdGestor").HasName("PK__Gestor__10358373BF56D2DE");
                        j.ToTable("Gestor");
                        j.IndexerProperty<int>("IdPaciente").HasColumnName("ID_Paciente");
                        j.IndexerProperty<int>("IdGestor").HasColumnName("ID_Gestor");
                    });

            entity.HasMany(d => d.IdPacientes).WithMany(p => p.IdGestors)
                .UsingEntity<Dictionary<string, object>>(
                    "Gestor",
                    r => r.HasOne<Usuario>().WithMany()
                        .HasForeignKey("IdPaciente")
                        .OnDelete(DeleteBehavior.ClientSetNull)
                        .HasConstraintName("FK__Gestor__ID_Pacie__440B1D61"),
                    l => l.HasOne<Usuario>().WithMany()
                        .HasForeignKey("IdGestor")
                        .OnDelete(DeleteBehavior.ClientSetNull)
                        .HasConstraintName("FK__Gestor__ID_Gesto__44FF419A"),
                    j =>
                    {
                        j.HasKey("IdPaciente", "IdGestor").HasName("PK__Gestor__10358373BF56D2DE");
                        j.ToTable("Gestor");
                        j.IndexerProperty<int>("IdPaciente").HasColumnName("ID_Paciente");
                        j.IndexerProperty<int>("IdGestor").HasColumnName("ID_Gestor");
                    });

            entity.HasMany(d => d.IdRols).WithMany(p => p.IdUsuarios)
                .UsingEntity<Dictionary<string, object>>(
                    "UsuarioRol",
                    r => r.HasOne<Rol>().WithMany()
                        .HasForeignKey("IdRol")
                        .OnDelete(DeleteBehavior.ClientSetNull)
                        .HasConstraintName("FK__Usuario_R__ID_Ro__412EB0B6"),
                    l => l.HasOne<Usuario>().WithMany()
                        .HasForeignKey("IdUsuario")
                        .OnDelete(DeleteBehavior.ClientSetNull)
                        .HasConstraintName("FK__Usuario_R__ID_Us__403A8C7D"),
                    j =>
                    {
                        j.HasKey("IdUsuario", "IdRol").HasName("PK__Usuario___CC469CE7983E4D18");
                        j.ToTable("Usuario_Rol");
                        j.IndexerProperty<int>("IdUsuario").HasColumnName("ID_Usuario");
                        j.IndexerProperty<int>("IdRol").HasColumnName("ID_Rol");
                    });
        });

        modelBuilder.Entity<UsuarioMedicamento>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PK__Usuario___3214EC07F725B879");

            entity.ToTable("Usuario_Medicamento");

            entity.HasIndex(e => new { e.IdUsuario, e.CnMed }, "UQ_Usuario_Medicamento").IsUnique();

            entity.Property(e => e.CnMed).HasMaxLength(50);

            entity.HasOne(d => d.CnMedNavigation).WithMany(p => p.UsuarioMedicamentos)
                .HasForeignKey(d => d.CnMed)
                .HasConstraintName("FK_Usuario_Medicamento_Medicamento");

            entity.HasOne(d => d.IdUsuarioNavigation).WithMany(p => p.UsuarioMedicamentos)
                .HasForeignKey(d => d.IdUsuario)
                .HasConstraintName("FK_Usuario_Medicamento_Usuario");
        });

        OnModelCreatingPartial(modelBuilder);
    }

    partial void OnModelCreatingPartial(ModelBuilder modelBuilder);
}
