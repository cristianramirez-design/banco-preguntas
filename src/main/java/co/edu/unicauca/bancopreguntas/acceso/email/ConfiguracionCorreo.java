package co.edu.unicauca.bancopreguntas.acceso.email;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

/**
 * Datos del servidor SMTP leidos de correo.properties (no se sube a Git porque
 * contiene la contrasena). Ver correo.properties.example.
 */
public class ConfiguracionCorreo {

    public static final String ARCHIVO_POR_DEFECTO = "correo.properties";

    private final String host;
    private final int puerto;
    private final String usuario;
    private final String contrasena;
    private final String remitente;
    private final boolean starttls;

    public ConfiguracionCorreo(String host, int puerto, String usuario, String contrasena,
                               String remitente, boolean starttls) {
        this.host = host;
        this.puerto = puerto;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.remitente = remitente;
        this.starttls = starttls;
    }

    /** Lee la configuracion; vacio si el archivo no existe (se usa el correo simulado). */
    public static Optional<ConfiguracionCorreo> desdeArchivo(Path ruta) {
        if (!Files.exists(ruta)) {
            return Optional.empty();
        }
        Properties p = new Properties();
        try (InputStream in = Files.newInputStream(ruta)) {
            p.load(in);
        } catch (IOException e) {
            System.err.println("No se pudo leer " + ruta + ": " + e.getMessage());
            return Optional.empty();
        }
        return Optional.of(new ConfiguracionCorreo(
                p.getProperty("smtp.host", ""),
                Integer.parseInt(p.getProperty("smtp.puerto", "587").trim()),
                p.getProperty("smtp.usuario", ""),
                p.getProperty("smtp.contrasena", ""),
                p.getProperty("smtp.remitente", p.getProperty("smtp.usuario", "")),
                Boolean.parseBoolean(p.getProperty("smtp.starttls", "true").trim())));
    }

    public String getHost() {
        return host;
    }

    public int getPuerto() {
        return puerto;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public String getRemitente() {
        return remitente;
    }

    public boolean isStarttls() {
        return starttls;
    }
}
