package co.edu.unicauca.cliente.utilidades;

import java.io.InputStream;
import java.rmi.Naming;
import java.rmi.Remote;
import java.util.Properties;

/**
 * Utilidades de registro RMI para el cliente.
 * (i) Puede leer IP y puerto del NS desde config.properties
 */
public class UtilidadesRegistroC {

    private static final String PROPERTIES_FILE = "/config.properties";

    // ------------------------------------------------------------------ //
    // (i) Obtener IP del NS desde archivo properties
    // ------------------------------------------------------------------ //
    public static String obtenerIpNS() {
        Properties props = cargarProperties();
        String ip = props.getProperty("ns.ip", "localhost");
        System.out.println("[Config] IP del NS: " + ip);
        return ip;
    }

    // (i) Obtener puerto del NS desde archivo properties
    public static int obtenerPuertoNS() {
        Properties props = cargarProperties();
        String puerto = props.getProperty("ns.puerto", "1099");
        try {
            int p = Integer.parseInt(puerto);
            System.out.println("[Config] Puerto del NS: " + p);
            return p;
        } catch (NumberFormatException e) {
            System.err.println("[Config] Puerto inválido, usando 1099 por defecto.");
            return 1099;
        }
    }

    // ------------------------------------------------------------------ //
    // Obtener objeto remoto del NS
    // ------------------------------------------------------------------ //
    public static Remote obtenerObjRemoto(int puerto, String dirIP, String nameObjReg) {
        String urlRegistro = "rmi://" + dirIP + ":" + puerto + "/" + nameObjReg;
        try {
            return Naming.lookup(urlRegistro);
        } catch (Exception e) {
            System.out.println("Excepción al obtener el objeto remoto: " + e);
            return null;
        }
    }

    // ------------------------------------------------------------------ //
    // Cargar archivo properties
    // ------------------------------------------------------------------ //
    private static Properties cargarProperties() {
        Properties props = new Properties();
        try (InputStream input = UtilidadesRegistroC.class.getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                System.err.println("[Config] No se encontró " + PROPERTIES_FILE + ". Usando valores por defecto.");
                return props;
            }
            props.load(input);
        } catch (Exception e) {
            System.err.println("[Config] Error al leer " + PROPERTIES_FILE + ": " + e.getMessage());
        }
        return props;
    }
}