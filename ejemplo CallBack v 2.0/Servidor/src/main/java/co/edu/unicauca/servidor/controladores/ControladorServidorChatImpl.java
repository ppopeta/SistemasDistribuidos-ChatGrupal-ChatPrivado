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
        // El HashMap permite almacenar usuarios con acceso rápido por nickName, lo que
        // es esencial para validar unicidad y enviar mensajes privados eficientemente.
        usuarios = new HashMap<>();
    }

    // b) Validar que el nickName sea único

    @Override
    public synchronized boolean registrarReferenciaUsuario(UsuarioCllbckInt usuario) throws RemoteException {
        String nick = usuario.getNickName().trim();

        if (nick.isEmpty()) {
            return false;
        }
        /*
         * el metodo containsKey permite verificar si el nickName ya existe en el
         * HashMap, lo que garantiza la unicidad de los usuarios registrados en el chat.
         * Si el nickName ya está registrado, se devuelve false para indicar que no se
         * pudo registrar al usuario con ese nickName.
         */

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

    // c) Permitir al cliente ver los nickNames de los usuarios registrados y
    // activos
    @Override
    public synchronized ArrayList<String> obtenerNickNamesActivos() throws RemoteException {
        depurarUsuariosDesconectados();
        return new ArrayList<>(usuarios.keySet());
    }

    // d) Permitir al cliente salir del chat, eliminando su referencia remota del
    // servidor
    @Override
    public synchronized void salirDelChat(String nickName) throws RemoteException {
        usuarios.remove(nickName);
        System.out.println("Usuario salió: " + nickName);
    }

    // e) Permitir al cliente enviar un mensaje privado a otro usuario determinado
    @Override
    public synchronized void enviarMensajePrivado(String emisor, String receptor, String mensaje)
            throws RemoteException {
        UsuarioCllbckInt usuarioEmisor = usuarios.get(emisor);
        if (usuarioEmisor == null) {
            return;
        }

        UsuarioCllbckInt usuarioReceptor = usuarios.get(receptor);
        if (usuarioReceptor == null) {
            usuarioEmisor.notificarEspecifico(MENSAJE_RECEPTOR_DESCONECTADO);
            return;
        }
        // f) Servidor comprueba conexión antes de reenviar chat privado, elimina
        // referencia si terminó abruptamente y notifica al emisor
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
    // h) Permitir al cliente consultar la cantidad de usuarios activos
    @Override
    public synchronized int obtenerCantidadUsuariosActivos() throws RemoteException {
        depurarUsuariosDesconectados();
        return usuarios.size();
    }
    //g) Servidor comprueba conexión antes de reenviar chat público; elimina referencia si terminó abruptamente
    // Método para eliminar usuarios desconectados del HashMap
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

    // Método para verificar si un usuario aún está conectado intentando acceder a su nickName
    private boolean estaConectado(UsuarioCllbckInt usuario) {
        try {
            usuario.getNickName();
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }
}   