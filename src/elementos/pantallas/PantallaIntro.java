package elementos.pantallas;

import juego.VtaJuego;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class PantallaIntro extends JPanel {
    private VtaJuego ventana;
    private Timer timer;
    private ImageIcon gifIntro;

    public PantallaIntro(VtaJuego ventana) {
        this.ventana = ventana;
        this.setLayout(new BorderLayout());
        this.setBackground(Color.BLACK);

        URL urlVideo = getClass().getResource("/res/intro.gif");
        if (urlVideo != null) {
            gifIntro = new ImageIcon(urlVideo);
            JLabel labelVideo = new JLabel(gifIntro);
            this.add(labelVideo, BorderLayout.CENTER);
        }

        // Timer de 16.5 segundos (16500 ms)
        timer = new Timer(16500, e -> terminarIntro());
        timer.setRepeats(false);

        configurarSaltoIntro();
    }

    public void iniciar() {
        // 1. Iniciar el audio de la intro
        // Asegúrate de que el archivo se llame intro_audio.wav en tu carpeta res
        ventana.getAudioPlayer().reproducirMusica("intro_audio.wav"); 
        
        // 2. Iniciar el cronómetro para el cambio de pantalla
        timer.start();
    }

    private void terminarIntro() {
        if (timer.isRunning()) timer.stop();
        
        // 3. Detener la música de la intro antes de cambiar
        ventana.getAudioPlayer().detenerMusica(); 
        
        // 4. Cambiar al menú
        ventana.mostrarMenu(); 
    }

    private void configurarSaltoIntro() {
        this.setFocusable(true);
        KeyAdapter saltarTeclado = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) { terminarIntro(); }
        };
        this.addKeyListener(saltarTeclado);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { terminarIntro(); }
        });
    }
}