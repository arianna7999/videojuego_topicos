package elementos.pantallas;

import juego.VtaJuego;
import javax.swing.*;
import java.awt.*;

public class PantallaIntro extends JPanel {
    private VtaJuego ventana;
    private Timer timerSalto;
    private Image gifImagen;
    private boolean yaSalto = false; // Para evitar múltiples saltos

    public PantallaIntro(VtaJuego ventana) {
        this.ventana = ventana;
        this.setLayout(new BorderLayout());
        this.setBackground(Color.BLACK);
        

        try {

            java.net.URL imgUrl = getClass().getResource("/res/intro.gif");
            if (imgUrl != null) {
                this.gifImagen = new ImageIcon(imgUrl).getImage();
            } else {
                System.err.println("Error: No se encontró el archivo intro.gif en /res/");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.timerSalto = new Timer(18500, e -> saltarAlMenu());

        // Ahora que ya no es null, podemos llamar a start()
        this.timerSalto.setRepeats(false);
        this.timerSalto.start();

        this.setFocusable(false);

        // 3. Permitir saltar la intro con cualquier tecla
        configurarSaltoTeclado();
    }

    private void saltarAlMenu() {
    if (yaSalto) return; // Si ya cambió, no hagas nada más
    yaSalto = true;
    
    if (timerSalto != null) timerSalto.stop();
    ventana.getAudioPlayer().detenerMusica();
    ventana.mostrarMenu(); 
    ventana.getAudioPlayer().reproducirMusica("soundtrack.wav");  
}

    private void configurarSaltoTeclado() {
        this.setFocusable(true);
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
public void keyPressed(java.awt.event.KeyEvent e) {
    e.consume(); // Evita que la tecla "pase" a la siguiente pantalla
    saltarAlMenu();
}
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gifImagen != null) {
            // g.drawImage escala la imagen al tamaño actual del JPanel
            // Esto lo hace totalmente escalable a la ventana
            g.drawImage(gifImagen, 0, 0, this.getWidth(), this.getHeight(), this);
        } else {
            // Mensaje de depuración visual si no carga la imagen
            g.setColor(Color.WHITE);
            g.drawString("Cargando Intro...", 20, 20);
        }
    }
}
