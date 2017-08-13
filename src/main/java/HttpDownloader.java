package actualizador;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 *
 * @author nahuel
 */
public class HttpDownloader implements Runnable {

    private final static int BUFFER_SIZE = 2048;
    
    private URL url;
    private String nombre;
    private int tamano;
    private JProgressBar progressBar;
    private JLabel text;
    
    @Override
    public void run() {
        int tamanoDescargado = 0;
        
        BufferedInputStream in = null;
        RandomAccessFile raf = null;  
        
        try {
            // open Http connection to URL
            
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            // connect to server
            conn.connect();

            // Make sure the response code is in the 200 range.
            if (conn.getResponseCode() / 100 != 2) {
                System.out.println("No se puede descargar codigo: " + conn.getResponseCode());
            }
            
            // get the input stream
            in = new BufferedInputStream(conn.getInputStream());

            // open the output file and seek to the start location
            raf = new RandomAccessFile("new_" + nombre, "rw");

            byte data[] = new byte[BUFFER_SIZE];
            int numRead;
                while((numRead = in.read(data,0,BUFFER_SIZE)) != -1){
                // write to buffer
                tamanoDescargado += data.length;
                raf.write(data,0,numRead);
                progressBar.setValue(tamanoDescargado * 100 / tamano);
            }
            
        } catch (IOException e) {
            System.out.println("Error " + e.getMessage());
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    System.out.println("Error " + e.getMessage());
                }
            }

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    System.out.println("Error " + e.getMessage());
                }
            }
            
            text.setText("Actulizacion terminada");
            File archivoNuevo = new File("new_" + nombre),
                 archivoViejo = new File(nombre);
            
            archivoViejo.delete();
            
            archivoNuevo.renameTo(archivoViejo);
           
        }
    }
    
    public void download(String u, String n, int t, JProgressBar progress, JLabel texto){
        try {
            url = new URL(u);
        } catch (MalformedURLException ex) {
            Logger.getLogger(HttpDownloader.class.getName()).log(Level.SEVERE, null, ex);
        }
        nombre = n;
        tamano = t;
        progressBar = progress;
        text = texto;
        
        text.setText("Descargando...");
        Thread thread = new Thread(this);
        thread.start();
    }
}
