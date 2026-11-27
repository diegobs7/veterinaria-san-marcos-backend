package cl.duoc.veterinaria.config;

import cl.duoc.veterinaria.categoria.Categoria;
import cl.duoc.veterinaria.categoria.CategoriaRepository;
import cl.duoc.veterinaria.common.Slugs;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

// Carga las categorías de partida del catálogo la primera vez que arranca el backend
@Order(3)
@Component
public class CategoriasIniciales implements ApplicationRunner {

    // Nombre, icono y descripción: las mismas que tenía la página en localStorage
    private static final List<String[]> CATEGORIAS = List.of(
            new String[] {"Consultas", "🩺", "Atención médica general y controles de rutina."},
            new String[] {"Vacunación", "💉", "Esquemas de vacunas según especie y edad."},
            new String[] {"Cirugía", "🏥", "Procedimientos quirúrgicos programados."},
            new String[] {"Desparasitación", "💊", "Control de parásitos internos y externos."},
            new String[] {"Exámenes", "🔬", "Análisis de laboratorio e imagenología."},
            new String[] {"Otros", "🐾", "Servicios complementarios y especialidades."});

    private final CategoriaRepository categorias;

    public CategoriasIniciales(CategoriaRepository categorias) {
        this.categorias = categorias;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Solo si la tabla está vacía: así no vuelve a crear las que el administrador eliminó
        if (categorias.count() > 0) {
            return;
        }
        for (String[] c : CATEGORIAS) {
            categorias.save(new Categoria(c[0], Slugs.de(c[0]), c[1], c[2]));
        }
    }
}