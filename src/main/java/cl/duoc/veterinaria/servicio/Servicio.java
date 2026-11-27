package cl.duoc.veterinaria.servicio;

import cl.duoc.veterinaria.categoria.Categoria;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

// Servicio del catálogo de la veterinaria (Consulta general, Vacuna triple felina...)
@Entity
@Table(name = "servicios")
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código visible para el cliente y la recepción, por ejemplo SV001 o VA002
    @Column(nullable = false, unique = true, length = 10)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String titulo;

    // Muchos servicios pertenecen a una categoría (columna categoria_id en la tabla servicios)
    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    // Especies que atiende: "Perro / Gato", "Gata", "Todas"...
    @Column(nullable = false, length = 30)
    private String especie;

    // Duración estimada como texto: "30 min", "24 h"
    @Column(nullable = false, length = 30)
    private String duracion;

    // Precio en pesos chilenos, sin decimales
    @Column(nullable = false)
    private Integer precio;

    // Porcentaje de descuento si el servicio está en oferta (null = sin oferta)
    private Integer descuento;

    @Column(length = 150)
    private String observaciones;

    @Column(length = 500)
    private String descripcion;

    // Lista "Este servicio incluye". Se guarda en la tabla servicio_incluye, en el mismo orden
    @ElementCollection
    @CollectionTable(name = "servicio_incluye", joinColumns = @JoinColumn(name = "servicio_id"))
    @OrderColumn(name = "posicion")
    @Column(name = "item", nullable = false, length = 200)
    private List<String> incluye = new ArrayList<>();

    protected Servicio() {
    }

    public Servicio(String codigo, String titulo, Categoria categoria, String especie, String duracion, Integer precio) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.categoria = categoria;
        this.especie = especie;
        this.duracion = duracion;
        this.precio = precio;
    }

    public Long getId() { return id; }

    public String getCodigo() { return codigo; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }

    public String getDuracion() { return duracion; }
    public void setDuracion(String duracion) { this.duracion = duracion; }

    public Integer getPrecio() { return precio; }
    public void setPrecio(Integer precio) { this.precio = precio; }

    public Integer getDescuento() { return descuento; }
    public void setDescuento(Integer descuento) { this.descuento = descuento; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public List<String> getIncluye() { return incluye; }

    // Reemplaza la lista completa (al editar el servicio)
    public void setIncluye(List<String> incluye) {
        this.incluye.clear();
        if (incluye != null) {
            this.incluye.addAll(incluye);
        }
    }
}