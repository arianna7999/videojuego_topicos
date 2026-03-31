package utils;

import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class AudioPlayer {
    
    private Clip musicaFondo;

    public void reproducirMusica(String nombreArchivo) {
        detenerMusica();
        
        try {

            URL url = getClass().getResource("/res/" + nombreArchivo);
            
            if (url == null) {
                System.err.println("no se encontró el archivo de audio: " + nombreArchivo);
                return;
            }

            AudioInputStream audioInput = AudioSystem.getAudioInputStream(url);
            musicaFondo = AudioSystem.getClip();
            musicaFondo.open(audioInput);
            musicaFondo.loop(Clip.LOOP_CONTINUOUSLY);
            musicaFondo.start();
            
        } catch (Exception e) {
            System.err.println("Error al cargar la música: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void detenerMusica() {
        if (musicaFondo != null && musicaFondo.isRunning()) {
            musicaFondo.stop();
            musicaFondo.close();
        }
    }
}