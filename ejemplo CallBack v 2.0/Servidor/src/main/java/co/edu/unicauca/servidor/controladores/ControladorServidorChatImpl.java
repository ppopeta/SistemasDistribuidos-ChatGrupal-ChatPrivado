package co.edu.unicauca.servidor.controladores;

import co.edu.unicauca.cliente.controladores.UsuarioCllbckInt;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ControladorServidorChatImpl extends UnicastRemoteObject implements ControladorServidorChatInt {

    private static final String MENSAJE_RECEPTOR_DESCONECTADO = "El mensaje no se logró enviar porque el usuario receptor no está conectado";
    private final HashMap<String, UsuarioCllbckInt> usuarios;

    public ControladorServidorChatImpl() throws RemoteException {
        super();
        usuarios = new HashMap<>();
    }

    @Override
    public synchronized boolean registrarReferenciaUsuario(UsuarioCllbckInt usuario) throws RemoteException {
        String nick = usuario.getNickName().trim();

        if (nick.isEmpty()) {
            return false;
        }

        if (usuarios.containsKey(nick)) {
            return false;
        }

        usuarios.put(nick, usuario);
        System.out.println("Usuario registrado: " + nick);
        return true;
    }

    @Override
    public synchronized void enviarMensaje(String mensaje) throws RemoteException {
        depurarUsuariosDesconectados();
        ArrayList<String> nickNamesActivos = new ArrayList<>(usuarios.keySet());
        int cantidadActivos = nickNamesActivos.size();

        Iterator<Map.Entry<String, UsuarioCllbckInt>> it = usuarios.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, UsuarioCllbckInt> entry = it.next();
            try {
                entry.getValue().notificar(mensaje, cantidadActivos, nickNamesActivos);
            } catch (RemoteException e) {
                System.out.println("Usuario desconectado eliminado: " + entry.getKey());
                it.remove();
            }
        }
    }

    @Override
    public synchronized ArrayList<String> obtenerNickNamesActivos() throws RemoteException {
        depurarUsuariosDesconectados();
        return new ArrayList<>(usuarios.keySet());
    }

    @Override
    public synchronized void salirDelChat(String nickName) throws RemoteException {
        usuarios.remove(nickName);
        System.out.println("Usuario salió: " + nickName);
    }

    @Override
    public synchronized void enviarMensajePrivado(String emisor, String receptor, String mensaje) throws RemoteException {
        UsuarioCllbckInt usuarioEmisor = usuarios.get(emisor);
        if (usuarioEmisor == null) {
            return;
        }

        UsuarioCllbckInt usuarioReceptor = usuarios.get(receptor);
        if (usuarioReceptor == null) {
            usuarioEmisor.notificarEspecifico(MENSAJE_RECEPTOR_DESCONECTADO);
            return;
        }

        if (!estaConectado(usuarioReceptor)) {
            usuarios.remove(receptor);
            usuarioEmisor.notificarEspecifico(MENSAJE_RECEPTOR_DESCONECTADO);
            return;
        }

        try {
            usuarioReceptor.notificarEspecifico("[Privado de " + emisor + "]: " + mensaje);
        } catch (RemoteException e) {
            usuarios.remove(receptor);
            usuarioEmisor.notificarEspecifico(MENSAJE_RECEPTOR_DESCONECTADO);
        }
    }

    @Override
    public synchronized int obtenerCantidadUsuariosActivos() throws RemoteException {
        depurarUsuariosDesconectados();
        return usuarios.size();
    }

    private void depurarUsuariosDesconectados() {
        Iterator<Map.Entry<String, UsuarioCllbckInt>> it = usuarios.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, UsuarioCllbckInt> entry = it.next();
            if (!estaConectado(entry.getValue())) {
                System.out.println("Usuario desconectado eliminado: " + entry.getKey());
                it.remove();
            }
        }
    }

    private boolean estaConectado(UsuarioCllbckInt usuario) {
        try {
            usuario.getNickName();
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }
}