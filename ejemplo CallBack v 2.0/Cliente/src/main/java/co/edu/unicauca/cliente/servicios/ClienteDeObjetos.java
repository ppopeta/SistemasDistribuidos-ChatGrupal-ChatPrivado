package co.edu.unicauca.cliente.servicios;

import co.edu.unicauca.cliente.controladores.UsuarioCllbckImpl;
import co.edu.unicauca.cliente.utilidades.UtilidadesConsola;
import co.edu.unicauca.cliente.utilidades.UtilidadesRegistroC;
import co.edu.unicauca.servidor.controladores.ControladorServidorChatInt;
import java.util.ArrayList;

/**
 * Cliente del chat RMI.
 * Implementa todas las funcionalidades del taller (a) al (i).
 */
public class ClienteDeObjetos {

    public static void main(String[] args) {
        try {
            // (i) Leer IP y puerto del NS desde config.properties
            String direccionIpRMIRegistry = UtilidadesRegistroC.obtenerIpNS();
            int numPuertoRMIRegistry      = UtilidadesRegistroC.obtenerPuertoNS();

            System.out.println("=== Cliente de Chat RMI ===");
            System.out.println("Conectando al NS en " + direccionIpRMIRegistry + ":" + numPuertoRMIRegistry);

            // Obtener referencia al servidor
            ControladorServidorChatInt servidor = (ControladorServidorChatInt)
                UtilidadesRegistroC.obtenerObjRemoto(numPuertoRMIRegistry, direccionIpRMIRegistry, "ServidorChat");

            if (servidor == null) {
                System.out.println("No se pudo conectar al servidor. Verifique que el servidor esté activo.");
                return;
            }

            // a) Permitir al cliente registrar su referencia remota junto con un nickName

            // 1. Registrar referencia remota con nickName
            // 2. El servidor valida que sea único, si no lo es, notifica y pedimos otro
            UsuarioCllbckImpl objUsuario = null;
            boolean registrado = false;

            while (!registrado) {
                System.out.println("Digite su nickName:");
                String nickName = UtilidadesConsola.leerCadena().trim();
                if (nickName.isEmpty()) {
                    System.out.println("El nickName no puede estar vacío.");
                    continue;
                }
                objUsuario = new UsuarioCllbckImpl(nickName);

                // Registro del usuario en el servidor
                registrado = servidor.registrarReferenciaUsuario(objUsuario);
                if (!registrado) {
                    System.out.println("NickName '" + nickName + "' ya está en uso. Intente con otro.");
                }
            }

            System.out.println("¡Registrado exitosamente como '" + objUsuario.getNickName() + "'!");

            // Menú principal — bucle de interacción
            boolean activo = true;
            while (activo) {
                mostrarMenu();
                int opcion = UtilidadesConsola.leerEntero();

                switch (opcion) {
                    case 1:
                        // Enviar mensaje público al chat grupal
                        System.out.println("Ingrese el mensaje a enviar al chat grupal:");
                        String msgPublico = UtilidadesConsola.leerCadena();
                        servidor.enviarMensaje("[" + objUsuario.getNickName() + "]: " + msgPublico);
                        break;

                    case 2:
                        // (e) Enviar mensaje privado a un usuario determinado
                        System.out.println("Ingrese el nickName del destinatario:");
                        String receptor = UtilidadesConsola.leerCadena().trim();
                        System.out.println("Ingrese el mensaje privado:");
                        String msgPrivado = UtilidadesConsola.leerCadena();
                        servidor.enviarMensajePrivado(objUsuario.getNickName(), receptor, msgPrivado);
                        break;

                    case 3:
                        // (c) Ver nickNames de usuarios activos
                        ArrayList<String> nicks = servidor.obtenerNickNamesActivos();
                        System.out.println("Usuarios activos (" + nicks.size() + "):");
                        for (String n : nicks) {
                            System.out.println("  - " + n);
                        }
                        break;

                    case 4:
                        // (h) Consultar cantidad de usuarios activos
                        int cantidad = servidor.obtenerCantidadUsuariosActivos();
                        System.out.println("Cantidad de usuarios activos: " + cantidad);
                        break;

                    case 5:
                        // (d) Salir del chat y eliminar referencia en el servidor
                        servidor.salirDelChat(objUsuario.getNickName());
                        System.out.println("Has salido del chat. ¡Hasta pronto!");
                        activo = false;
                        break;

                    default:
                        System.out.println("Opción no válida. Intente nuevamente.");
                }
            }

        } catch (Exception e) {
            System.out.println("Error en el cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void mostrarMenu() {
        System.out.println("\n======= MENÚ DE CHAT =======");
        System.out.println("1. Enviar mensaje al chat grupal");
        System.out.println("2. Enviar mensaje privado a un usuario");
        System.out.println("3. Ver usuarios activos");
        System.out.println("4. Ver cantidad de usuarios activos");
        System.out.println("5. Salir del chat");
        System.out.println("============================");
        System.out.println("Seleccione una opción:");
    }
}