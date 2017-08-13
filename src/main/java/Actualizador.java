package actualizador;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class Actualizador {

    private static final String URL = "http://atrom.netai.net/actualizaciones/";
    
    private String programa;
    private String version;
    
    public Actualizador(String[] args) {
        if (args.length == 2){
            programa = args[0];
            version = args[1];
        }else{
            System.exit(0);
        }
    }
    
    public boolean hayActualizacion(){
        boolean hay = false;
        URL url;
        try {
            url = new URL(URL + "?programa=" + programa + "&accion=actualizar&version=" + version);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            DataInputStream dis = new DataInputStream(con.getInputStream());
            String inputLine;

            while ((inputLine = dis.readLine()) != null) {
                if (Integer.parseInt(inputLine) == 1)
                    hay = true;
            }
            
            dis.close();
        } catch (MalformedURLException ex) {
            Logger.getLogger(Actualizador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Actualizador.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return hay;
    }
    
    public void descargar(JProgressBar progress, JLabel texto){
        String[] datosDescarga = null;
        URL url;
        int tamano = 0, cont = 0;
        String nombre = "";
        
        try {
            url = new URL(URL + "?programa=" + programa + "&accion=descargar&version=" + version);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            DataInputStream dis = new DataInputStream(con.getInputStream());
            String inputLine;

            while ((inputLine = dis.readLine()) != null) {
                if (inputLine.length() == 1 && Integer.parseInt(inputLine) == 0)
                    System.exit(0);
                else
                    datosDescarga = inputLine.split("#");
            }
            
            dis.close();
        } catch (MalformedURLException ex) {
            Logger.getLogger(Actualizador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Actualizador.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String[] separadosBarras = datosDescarga[0].split("/");
        nombre = separadosBarras[separadosBarras.length - 1];
        tamano = Integer.parseInt(datosDescarga[1]);
        
        HttpDownloader http = new HttpDownloader();
        http.download(datosDescarga[0], nombre, tamano, progress, texto);
        /**File archivo = new File("new_" + nombre);
        try {
            URLConnection conn = new URL(datosDescarga[0]).openConnection();
            conn.connect();
            
            InputStream in = conn.getInputStream();
            OutputStream out = new FileOutputStream(archivo);
            
            int bytes = 0;
            while (bytes != -1){
                bytes = in.read();
                if (bytes != -1){
                    out.write(bytes);
                    cont += bytes;
                    progress.setValue(cont * 100 / tamano);
                }
            }
            
            out.close();
            in.close();
            
            File archivoBorrar = new File(nombre);
            archivo.renameTo(archivoBorrar);
            **/
        /**} catch (MalformedURLException ex) {
            Logger.getLogger(Actualizador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Actualizador.class.getName()).log(Level.SEVERE, null, ex);
        }**/
    }
}
