package co.edu.unicauca.cliente.controladores;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface UsuarioCllbckInt extends Remote {

    /**
     * Notificación pública (broadcast) enviada desde el servidor.
     */
    public void notificar(String mensaje, int cantidadUsuarios, ArrayList<String> nickNames) throws RemoteException;

    /**
     * Retorna el nickName de este usuario.
     */
    public String getNickName() throws RemoteException;

    /**
     * Notificación privada: mensaje privado o mensaje del sistema.
     */
    public void notificarEspecifico(String mensaje) throws RemoteException;
}