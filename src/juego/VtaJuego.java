package juego;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import javax.swing.JFrame;
import javax.swing.JPanel;

import elementos.pantallas.MenuOpciones;
import elementos.pantallas.MenuPrincipal;
import elementos.pantallas.PantallaIntro;
import utils.AudioPlayer;
import elementos.pantallas.PantallaIntro;

public class VtaJuego extends JFrame {

    private JPanel contenedor;
    private CardLayout cardLayout;
    private AudioPlayer audioPlayer = new AudioPlayer();

    // 1. Guardamos las pantallas como variables de la clase para no perderlas
    private PantallaIntro intro;
    private MenuPrincipal menu;
    private MenuOpciones opciones;
    private PanelJuego panelJuego;

    public VtaJuego(PanelJuego n) {
        this.panelJuego = n; // Guardamos el panel del juego

        // Configuraciones de la Ventana (Modo Ventana)
        this.setTitle("Mi Juego");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setUndecorated(false);
        this.setResizable(true);
        this.setSize(1280, 720);
        this.setLocationRelativeTo(null);
        audioPlayer.reproducirMusica("SonidoIntro.wav");

        // Configuración del Layout
        cardLayout = new CardLayout();
        contenedor = new JPanel(cardLayout);

        // Inicialización de Pantallas
        PantallaIntro intro = new PantallaIntro(this);
        intro = new PantallaIntro(this);
        menu = new MenuPrincipal(this);
        opciones = new MenuOpciones(this);

        contenedor.add(intro, "INTRO");
        contenedor.add(menu, "MENU");
        contenedor.add(panelJuego, "JUEGO");
        contenedor.add(opciones, "OPCIONES");

        this.add(contenedor);

        // Listener de foco para pausar el juego
        this.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                // Opcional: Reanudar audio o juego
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                if (panelJuego.getGame() != null) {
                    panelJuego.getGame().windowFocusLost();
                }
            }
        });
        cardLayout.show(contenedor, "INTRO"); // Mostrar la intro al iniciar
        this.setVisible(true);
    }

    public void mostrarMenu() {
        cardLayout.show(contenedor, "MENU");
        contenedor.getComponent(0).requestFocusInWindow(); // Asegura el foco en el Menú
    }

    public void mostrarJuego() {
        cardLayout.show(contenedor, "JUEGO");
        // Le pedimos el foco al panel del juego directamente
        panelJuego.requestFocusInWindow();
    }

    public void mostrarOpciones() {
        cardLayout.show(contenedor, "OPCIONES");
        contenedor.revalidate();
        contenedor.repaint();
        opciones.requestFocusInWindow();
    }

    public AudioPlayer getAudioPlayer() {
        return this.audioPlayer;
    }
}