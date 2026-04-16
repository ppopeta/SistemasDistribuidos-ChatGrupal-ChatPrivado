package co.edu.unicauca.servidor.controladores;

import co.edu.unicauca.cliente.controladores.UsuarioCllbckInt;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ControladorServidorChatInt extends Remote {

    // (a) Registrar referencia remota con nickName
    public boolean registrarReferenciaUsuario(UsuarioCllbckInt usuario) throws RemoteException;

    // Enviar mensaje público al chat grupal
    public void enviarMensaje(String mensaje) throws RemoteException;

    // (c) Ver nickNames de usuarios registrados y activos
    public ArrayList<String> obtenerNickNamesActivos() throws RemoteException;

    // (d) Salir del chat y eliminar referencia en el servidor
    public void salirDelChat(String nickName) throws RemoteException;

    // (e) Enviar mensaje privado a un usuario determinado
    public void enviarMensajePrivado(String nickNameEmisor, String nickNameReceptor, String mensaje) throws RemoteException;

    // (h) Consultar cantidad de usuarios activos
    public int obtenerCantidadUsuariosActivos() throws RemoteException;
}