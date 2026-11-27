package cl.duoc.veterinaria.common;

import java.text.Normalizer;
import java.util.Locale;

// Convierte un texto en la versión para direcciones web: "Vacunación general" -> "vacunacion-general".
// Hace lo mismo que crearSlug() del frontend, para que ambos generen el mismo slug
public final class Slugs {

    private Slugs() {
    }

    public static String de(String texto) {
        if (texto == null) {
            return "";
        }
        String sinTildes = Normalizer.normalize(texto.trim().toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinTildes
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
    }
}