package co.edu.unicauca.servidor.controladores;

import co.edu.unicauca.cliente.controladores.UsuarioCllbckInt;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ControladorServidorChatImpl extends UnicastRemoteObject implements ControladorServidorChatInt {

    private HashMap<String, UsuarioCllbckInt> usuarios;

    public ControladorServidorChatImpl() throws RemoteException {
        super();
        usuarios = new HashMap<>();
    }
    @Override
    public boolean registrarReferenciaUsuario(UsuarioCllbckInt usuario) throws RemoteException {
        String nick = usuario.getNickName();

        if (usuarios.containsKey(nick)) {
            return false; // nickname ya existe
        }

        usuarios.put(nick, usuario);
        System.out.println("Usuario registrado: " + nick);
        return true;
    }
    @Override
public void enviarMensaje(String mensaje) throws RemoteException {

    Iterator<Map.Entry<String, UsuarioCllbckInt>> it = usuarios.entrySet().iterator();

    while (it.hasNext()) {
        Map.Entry<String, UsuarioCllbckInt> entry = it.next();
        try {
            entry.getValue().notificarEspecifico(mensaje);
        } catch (Exception e) {
            System.out.println("Usuario desconectado eliminado: " + entry.getKey());
            it.remove(); // eliminar si falló
        }
    }
}
@Override
public ArrayList<String> obtenerNickNamesActivos() throws RemoteException {
    return new ArrayList<>(usuarios.keySet());
}
@Override
public void salirDelChat(String nickName) throws RemoteException {
    usuarios.remove(nickName);
    System.out.println("Usuario salió: " + nickName);
}
@Override
public void enviarMensajePrivado(String emisor, String receptor, String mensaje) throws RemoteException {

    UsuarioCllbckInt usuarioReceptor = usuarios.get(receptor);
    UsuarioCllbckInt usuarioEmisor = usuarios.get(emisor);

    if (usuarioReceptor == null) {
        usuarioEmisor.notificarEspecifico("El usuario no está conectado");
        return;
    }

    try {
        usuarioReceptor.notificarEspecifico("[Privado de " + emisor + "]: " + mensaje);
    } catch (Exception e) {
        usuarios.remove(receptor);
        usuarioEmisor.notificarEspecifico("El mensaje no se logró enviar porque el usuario receptor no está conectado");
    }
}
@Override
public int obtenerCantidadUsuariosActivos() throws RemoteException {
    return usuarios.size();
}
}