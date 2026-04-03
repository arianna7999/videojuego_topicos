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

    public void reproducirEfecto(String nombreArchivo) {
        try {
            URL url = getClass().getResource("/res/" + nombreArchivo);
            if (url == null) {
                System.err.println("No se encontró el efecto: " + nombreArchivo);
                return;
            }
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            
            // ESTO ES LO QUE EVITA EL BUG:
            // Le decimos al programa que borre el sonido de la memoria en cuanto termine
            clip.addLineListener(event -> {
                if (event.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
                    clip.close();
                }
            });

            clip.start();
        } catch (Exception e) {
            System.err.println("Error al reproducir efecto: " + e.getMessage());
        }
    }
}