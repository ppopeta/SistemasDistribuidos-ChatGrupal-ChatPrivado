package co.edu.unicauca.cliente.controladores;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * Implementación del callback del cliente.
 * Recibe notificaciones públicas y privadas del servidor.
 */
public class UsuarioCllbckImpl extends UnicastRemoteObject implements UsuarioCllbckInt {

    private final String nickName;

    public UsuarioCllbckImpl(String nickName) throws RemoteException {
        super();
        this.nickName = nickName;
    }

    @Override
    public String getNickName() throws RemoteException {
        return nickName;
    }

    /**
     * Recibe mensajes públicos del chat grupal.
     * (c) Muestra lista de nickNames activos y cantidad de usuarios.
     */
    @Override
    public void notificar(String mensaje, int cantidadUsuarios, ArrayList<String> nickNames) throws RemoteException {
        System.out.println("\n[CHAT PÚBLICO] " + mensaje);
        System.out.println("  Usuarios activos (" + cantidadUsuarios + "): " + String.join(", ", nickNames));
    }

    /**
     * Recibe mensajes privados o notificaciones del sistema.
     */
    @Override
    public void notificarEspecifico(String mensaje) throws RemoteException {
        System.out.println("\n[MENSAJE DIRECTO] " + mensaje);
    }
}