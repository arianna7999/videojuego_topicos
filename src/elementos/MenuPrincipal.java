package elementos;

import juego.VtaJuego;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent; // Importante añadir este
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class MenuPrincipal extends JPanel {

    private BufferedImage fondo, logo, btnInicio, btnOpciones, btnSalir;
    private Rectangle rectInicio, rectOpciones, rectSalir;
    private VtaJuego ventana;

    public MenuPrincipal(VtaJuego ventana) {
        this.ventana = ventana;
        cargarRecursos();
        configurarTeclas();
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point click = e.getPoint();
                if (rectInicio != null && rectInicio.contains(click)) {
                    ventana.mostrarJuego();
                } else if (rectSalir != null && rectSalir.contains(click)) {
                    System.exit(0);
                }
            }
        });
    }

    private void configurarTeclas() {
        InputMap inputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = this.getActionMap();

        KeyStroke escKey = KeyStroke.getKeyStroke("ESCAPE");

        inputMap.put(escKey, "regresarMenu");
        actionMap.put("regresarMenu", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); 
            }
        });
    }

    private void cargarRecursos() {
        try {
            fondo = ImageIO.read(getClass().getResourceAsStream("/res/menu.png"));
            logo = ImageIO.read(getClass().getResourceAsStream("/res/logo_sombras.png"));
            btnInicio = ImageIO.read(getClass().getResourceAsStream("/res/botone_madera1.png"));
            btnOpciones = ImageIO.read(getClass().getResourceAsStream("/res/botone_madera2.png"));
            btnSalir = ImageIO.read(getClass().getResourceAsStream("/res/botone_madera3.png"));
        } catch (Exception e) {
            System.err.println("Error cargando imágenes: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fondo == null || logo == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        g2d.drawImage(fondo, 0, 0, getWidth(), getHeight(), null);

        int centroX = getWidth() / 2;
        double escalaLogo = (getWidth() * 0.6) / logo.getWidth();
        int logoW = (int) (logo.getWidth() * escalaLogo);
        int logoH = (int) (logo.getHeight() * escalaLogo);
        int logoX = centroX - (logoW / 2);
        int logoY = getHeight() / 120; 

        g2d.drawImage(logo, logoX, logoY, logoW, logoH, null);

        double escalaBtn = (getWidth() * 0.50) / btnInicio.getWidth();
        int bW = (int) (btnInicio.getWidth() * escalaBtn);
        int bH = (int) (btnInicio.getHeight() * escalaBtn);
        int bX = centroX - (bW / 2);
        
        int inicioBotonesY = logoY + logoH + 40; 
        int separacion = bH + 20; 

        rectInicio = new Rectangle(bX, inicioBotonesY, bW, bH);
        g2d.drawImage(btnInicio, bX, inicioBotonesY, bW, bH, null);

        int yOpciones = inicioBotonesY + separacion;
        rectOpciones = new Rectangle(bX, yOpciones, bW, bH);
        g2d.drawImage(btnOpciones, bX, yOpciones, bW, bH, null);

        int ySalir = yOpciones + separacion;
        rectSalir = new Rectangle(bX, ySalir, bW, bH);
        g2d.drawImage(btnSalir, bX, ySalir, bW, bH, null);
    }
}