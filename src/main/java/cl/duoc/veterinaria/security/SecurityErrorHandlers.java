package cl.duoc.veterinaria.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class SecurityErrorHandlers implements AuthenticationEntryPoint, AccessDeniedHandler {

    // 401: sin token, token alterado o vencido
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException ex) throws IOException {
        escribir(response, 401, "Unauthorized", "Token ausente, inválido o vencido");
    }

    // 403: autenticado, pero sin el rol necesario
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {
        escribir(response, 403, "Forbidden", "No tienes permiso para realizar esta acción");
    }

    private void escribir(HttpServletResponse response, int status, String error, String mensaje)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"status\":" + status + ",\"error\":\"" + error
                + "\",\"message\":\"" + mensaje + "\",\"timestamp\":\"" + Instant.now() + "\"}");
    }
}