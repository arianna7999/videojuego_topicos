package utils;

import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;

public class AudioPlayer {
    
    private Clip musicaFondo;
    private Clip clipErrorLargo; // Clip específico para "dua.wav"

    public void reproducirMusica(String nombreArchivo) {
        detenerMusica();
        
        try {
            URL url = getClass().getResource("/res/" + nombreArchivo);
            
            if (url == null) {
                System.err.println("No se encontró el archivo de audio: " + nombreArchivo);
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

    /**
     * Detiene específicamente la canción dua.wav si se está reproduciendo.
     */
    public void detenerCancionDua() {
        if (clipErrorLargo != null && clipErrorLargo.isRunning()) {
            clipErrorLargo.stop();
            clipErrorLargo.close();
        }
    }

    /**
     * Reproduce dua.wav. Si ya está sonando, ignora la petición para no solaparse.
     */
    public void reproducirCancionErrorDua() {
        // Si ya está sonando, no hacemos nada
        if (clipErrorLargo != null && clipErrorLargo.isRunning()) {
            return;
        }

        // Si el clip existe pero no suena, lo cerramos antes de volver a abrirlo
        if (clipErrorLargo != null) {
            clipErrorLargo.close();
        }

        try {
            URL url = getClass().getResource("/res/dua.wav");
            if (url == null) {
                System.err.println("No se encontró el archivo: dua.wav");
                return;
            }

            AudioInputStream audioInput = AudioSystem.getAudioInputStream(url);
            clipErrorLargo = AudioSystem.getClip();
            clipErrorLargo.open(audioInput);
            
            // Opcional: Detener la música de fondo para que luzca la canción
            detenerMusica();
            
            clipErrorLargo.start();

            // Listener para cerrar el clip automáticamente al terminar
            clipErrorLargo.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clipErrorLargo.close();
                }
            });

        } catch (Exception e) {
            System.err.println("Error al reproducir dua.wav: " + e.getMessage());
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
            
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });

            clip.start();
        } catch (Exception e) {
            System.err.println("Error al reproducir efecto: " + e.getMessage());
        }
    }
}