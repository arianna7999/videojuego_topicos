package utils;

import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.FloatControl;

public class AudioPlayer {
    
    private Clip musicaFondo;
    private Clip clipErrorLargo;
    private float volumenActual = 1.0f;
    private boolean mute = false;

    public void reproducirMusica(String nombreArchivo) {
    detenerMusica();
    try {
        URL url = getClass().getResource("/res/" + nombreArchivo);
        if (url == null) {
            // SI SALE ESTE MENSAJE, LA RUTA ESTÁ MAL
            System.err.println("¡ERROR! No se encontró el archivo: " + nombreArchivo);
            return;
        }
        System.out.println("Cargando audio: " + nombreArchivo); // Mensaje de éxito
        AudioInputStream audioInput = AudioSystem.getAudioInputStream(url);
        musicaFondo = AudioSystem.getClip();
        musicaFondo.open(audioInput);
        musicaFondo.start();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    public void detenerMusica() {
        if (musicaFondo != null && musicaFondo.isRunning()) {
            musicaFondo.stop();
            musicaFondo.close();
        }
    }

   
    public void detenerCancionDua() {
        if (clipErrorLargo != null && clipErrorLargo.isRunning()) {
            clipErrorLargo.stop();
            clipErrorLargo.close();
        }
    }

   
  
    public void reproducirCancionErrorDua() {
       
        if (clipErrorLargo != null && clipErrorLargo.isRunning()) {
            return;
        }

       
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
            
         
            detenerMusica();
            
            clipErrorLargo.start();

           
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

public void setVolumen(float valor) {
        this.volumenActual = valor; 
        actualizarVolumenClip(musicaFondo, valor);
        actualizarVolumenClip(clipErrorLargo, valor);
    }

    private void actualizarVolumenClip(Clip clip, float valor) {
        if (clip != null && clip.isOpen()) {
            try {
                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    
                    float dB = (float) (Math.log(valor <= 0 ? 0.0001 : valor) / Math.log(10.0) * 20.0);
                    gainControl.setValue(dB);
                }
            } catch (Exception e) {
                System.err.println("No se pudo ajustar el volumen del clip.");
            }
        }
    }
   public void setMute(boolean mute) {
    this.mute = mute; 
    
    // IMPORTANTE: Cambia "musica" por el nombre de tu variable de sonido
    // Si tu objeto se llama 'cancion', pon 'cancion.stop()'
    if (mute) {
        if (musicaFondo != null) musicaFondo.stop(); 
    } else {
        if (musicaFondo != null) musicaFondo.loop(Clip.LOOP_CONTINUOUSLY);
    }
}
}
