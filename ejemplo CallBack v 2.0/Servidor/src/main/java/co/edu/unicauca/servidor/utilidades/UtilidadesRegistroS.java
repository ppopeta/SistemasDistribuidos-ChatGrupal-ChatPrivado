
package co.edu.unicauca.servidor.utilidades;


import java.io.InputStream;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;
public class UtilidadesRegistroS
{
        private static final String PROPERTIES_FILE = "/config.properties";

        public static String obtenerIpNS() {
                Properties props = cargarProperties();
                String ip = props.getProperty("ns.ip", "localhost");
                System.out.println("[Config] IP del NS: " + ip);
                return ip;
        }

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

        private static Properties cargarProperties() {
                Properties props = new Properties();
                try (InputStream input = UtilidadesRegistroS.class.getResourceAsStream(PROPERTIES_FILE)) {
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

	public static void arrancarNS(int numPuertoRMI) throws RemoteException 
	{
            try
            {

                Registry registro = LocateRegistry.getRegistry(numPuertoRMI);  
                String[] vector=registro.list();
                for (String idNS : vector) {
                    System.out.println("ns : "+idNS );
                }
                System.out.println("El rmiRegistry se ha obtenido y se encuentra escuchando en el puerto: " + numPuertoRMI); 

            }
            catch(RemoteException e)
            {
                    System.out.println("El rmiRegistry no se localizó en el puerto: " + numPuertoRMI);

                    Registry registro = LocateRegistry.createRegistry(numPuertoRMI);
                    System.out.println("El rmiRegistry  se ha creado en el puerto: " + numPuertoRMI);
            }
		
	}
        
        	
	public static void RegistrarObjetoRemoto(Remote objetoRemoto, String dirIPNS, int numPuertoNS, String identificadorObjetoRemoto)
	{
            String UrlRegistro = "rmi://"+dirIPNS+":"+numPuertoNS+"/"+identificadorObjetoRemoto;
            try
            {
                    Naming.rebind(UrlRegistro, objetoRemoto);
                    System.out.println("Se realizó el registro del objeto remoto en el ns ubicado en la dirección: " +dirIPNS+" y "+ "puerto "+numPuertoNS);
            } catch (RemoteException e)
            {
                    System.out.println("Error en el registro del objeto remoto");
                    e.printStackTrace();
            } catch (MalformedURLException e)
            {
                    System.out.println("Error url inválida");
                    e.printStackTrace();
            }
		
	}	
	
}
