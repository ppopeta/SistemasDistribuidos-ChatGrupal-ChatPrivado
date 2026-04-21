
package co.edu.unicauca.servidor.servicios;

import co.edu.unicauca.servidor.controladores.ControladorServidorChatImpl;
import co.edu.unicauca.servidor.utilidades.UtilidadesRegistroS;
import java.rmi.RemoteException;

public class ServidorDeObjetos
{
    public static void main(String args[]) throws RemoteException
    {        
        String direccionIpRMIRegistry = UtilidadesRegistroS.obtenerIpNS();
        int numPuertoRMIRegistry = UtilidadesRegistroS.obtenerPuertoNS();

        System.out.println("Iniciando servidor con NS en " + direccionIpRMIRegistry + ":" + numPuertoRMIRegistry);
     
        ControladorServidorChatImpl objRemoto = new ControladorServidorChatImpl();//se leasigna el puerto de escucha del objeto remoto
        
        try
        {
           UtilidadesRegistroS.arrancarNS(numPuertoRMIRegistry);
           UtilidadesRegistroS.RegistrarObjetoRemoto(objRemoto, direccionIpRMIRegistry, numPuertoRMIRegistry, "ServidorChat");            
           
        } catch (Exception e)
        {
            System.err.println("No fue posible Arrancar el NS o Registrar el objeto remoto" +  e.getMessage());
        }
        
        
    }
}
