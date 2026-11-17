package cl.duoc.veterinaria.config;

import cl.duoc.veterinaria.rol.Rol;
import cl.duoc.veterinaria.rol.RolRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Order(1)
@Component
public class RolesIniciales implements ApplicationRunner {

    private final RolRepository roles;

    public RolesIniciales(RolRepository roles) {
        this.roles = roles;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (String nombre : List.of("ADMINISTRADOR", "RECEPCIONISTA", "DUENO")) {
            if (!roles.existsByNombre(nombre)) {
                roles.save(new Rol(nombre));
            }
        }
    }
}