package cl.duoc.veterinaria.categoria;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Categoría del catálogo de servicios de la veterinaria (Consultas, Vacunación, Cirugía...)
@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String nombre;

    // Versión del nombre para las direcciones de la página, por ejemplo /categorias/vacunacion
    @Column(nullable = false, unique = true, length = 60)
    private String slug;

    @Column(length = 10)
    private String icono;

    @Column(length = 255)
    private String descripcion;

    protected Categoria() {
    }

    public Categoria(String nombre, String slug, String icono, String descripcion) {
        this.nombre = nombre;
        this.slug = slug;
        this.icono = icono;
        this.descripcion = descripcion;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}