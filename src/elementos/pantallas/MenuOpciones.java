package elementos.pantallas;

import juego.VtaJuego;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;

public class MenuOpciones extends JPanel {

    private BufferedImage imgFondoCueva, imgPanel, imgBarraVacia, imgScrollbar, imgCheck, imgCruz;
    private Rectangle rectSlider, rectToggle;
    private VtaJuego ventana;
    
    private float volumen = 0.5f;
    private boolean sonidoActivo = true;
    
    // Variables para efectos dinámicos
    private ArrayList<Point> particulas;
    private Random random = new Random();
    private float anguloRotacion = 0;

    public MenuOpciones(VtaJuego ventana) {
        this.ventana = ventana;
        this.setDoubleBuffered(true);
        this.particulas = new ArrayList<>();
        
        // Crear partículas iniciales para el ambiente de la cueva
        for(int i = 0; i < 50; i++) {
            particulas.add(new Point(random.nextInt(800), random.nextInt(600)));
        }
        
        cargarAssets();
        
        // Timer a 60 FPS para animaciones suaves
        new Timer(16, e -> {
            actualizarEfectos();
            repaint();
        }).start();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (rectToggle != null && rectToggle.contains(e.getPoint())) {
                    sonidoActivo = !sonidoActivo;
                    ventana.mostrarMenu(); 
                }
                actualizarSlider(e.getPoint());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                actualizarSlider(e.getPoint());
            }
        });
    }

    private void actualizarEfectos() {
        // Mover partículas de polvo
        for (Point p : particulas) {
            p.y -= 1; // Flotan hacia arriba
            if (p.y < 0) {
                p.y = getHeight() > 0 ? getHeight() : 600;
                p.x = random.nextInt(getWidth() > 0 ? getWidth() : 800);
            }
        }
        // Angulo para el brillo pulsante
        anguloRotacion += 0.05f;
    }

    private void cargarAssets() {
        try {
            imgFondoCueva = ImageIO.read(getClass().getResourceAsStream("/res/FondoOpciones.png"));
            imgPanel = ImageIO.read(getClass().getResourceAsStream("/res/wood_panel.png"));
            imgBarraVacia = ImageIO.read(getClass().getResourceAsStream("/res/wood_slider_empty.png"));
            imgScrollbar = ImageIO.read(getClass().getResourceAsStream("/res/wood_scrollbar.png"));
            imgCheck = ImageIO.read(getClass().getResourceAsStream("/res/wood_checkbox_checked.png"));
            imgCruz = ImageIO.read(getClass().getResourceAsStream("/res/wood_checkbox_unchecked.png"));
        } catch (Exception e) {
            System.err.println("Error cargando assets: " + e.getMessage());
        }
    }

    private void actualizarSlider(Point p) {
        if (rectSlider != null && rectSlider.contains(p)) {
            volumen = (float) (p.x - rectSlider.x) / rectSlider.width;
            volumen = Math.max(0, Math.min(1, volumen));
            if (ventana != null && ventana.getAudioPlayer() != null) {
                ventana.getAudioPlayer().setVolumen(volumen);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Fondo
        if (imgFondoCueva != null) g2d.drawImage(imgFondoCueva, 0, 0, getWidth(), getHeight(), null);

        // 2. Partículas de Polvo (Ambiente)
        g2d.setColor(new Color(255, 255, 200, 40));
        for (Point p : particulas) {
            g2d.fillOval(p.x, p.y, 3, 3);
        }

    float parpadeo = (float) (Math.random() * 0.05); // Pequeño ruido aleatorio
    RadialGradientPaint iluminacion = new RadialGradientPaint(
    new Point2D.Float(getWidth()/2, getHeight()/2), 
    getHeight(), 
    new float[]{0.0f, 0.8f}, 
    new Color[]{new Color(0,0,0,0), new Color(0,0,0, (int)(220 * (0.9 + parpadeo)))}
    );
    g2d.setPaint(iluminacion);
    g2d.fillRect(0, 0, getWidth(), getHeight());

        // --- POSICIONAMIENTO ---
        int pW = 500, pH = 450;
        int pX = (getWidth() - pW) / 2;
        int pY = (getHeight() - pH) / 2;

        // 3. Sombra Proyectada Dinámica
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRoundRect(pX + 15, pY + 15, pW, pH, 30, 30);

        // 4. Panel Base
        if (imgPanel != null) g2d.drawImage(imgPanel, pX, pY, pW, pH, null);

        // 5. Marco Rúnico y Decoración de Esquinas
        g2d.setColor(new Color(40, 20, 5, 200));
        g2d.setStroke(new BasicStroke(5f));
        g2d.drawRoundRect(pX + 20, pY + 20, pW - 40, pH - 40, 10, 10);
        
        // Dibujar Runas con Brillo Tenue
        g2d.setFont(new Font("Serif", Font.BOLD, 18));
        g2d.setColor(new Color(180, 140, 80, 150));
        String[] runas = {"ᚦ", "ᚠ", "ᚨ", "ᚱ", "ᚲ", "ᛟ"};
        for(int i = 0; i < runas.length; i++) {
            g2d.drawString(runas[i], pX + 35, pY + 60 + (i * 70));
            g2d.drawString(runas[i], pX + pW - 55, pY + 60 + (i * 70));
        }

        // 6. Título con Efecto Metálico
        g2d.setFont(new Font("Monospaced", Font.BOLD, 45));
        g2d.setColor(new Color(0, 0, 0, 120));
        g2d.drawString("OPCIONES", pX + 138, pY + 83);
        // Brillo pulsante en el título
        int alphaTitulo = 180 + (int)(75 * Math.sin(anguloRotacion));
        g2d.setColor(new Color(255, 215, 130, alphaTitulo));
        g2d.drawString("OPCIONES", pX + 135, pY + 80);

        // 7. Slider Mejorado
        int sW = 350, sY = pY + 180;
        int sX = (getWidth() - sW) / 2;
        rectSlider = new Rectangle(sX, sY, sW, 40);
        
        if (imgBarraVacia != null) g2d.drawImage(imgBarraVacia, sX, sY, sW, 40, null);

        // Indicadores de Volumen Neón
        for (int i = 0; i < 6; i++) {
            float umbral = i * 0.2f;
            int dotX = sX + 30 + (i * 58);
            if (volumen >= umbral) {
                // Aura de brillo
                g2d.setColor(new Color(0, 255, 100, 50));
                g2d.fillOval(dotX - 4, sY + 11, 18, 18);
                g2d.setColor(new Color(150, 255, 200));
                g2d.fillOval(dotX, sY + 15, 10, 10);
            }
        }

        // Cursor (Scrollbar) con Sombra
        int selX = sX + (int)(volumen * (sW - 30));
        g2d.setColor(new Color(0,0,0,100));
        g2d.fillRect(selX + 4, sY - 2, 30, 50);
        if (imgScrollbar != null) g2d.drawImage(imgScrollbar, selX, sY - 5, 30, 50, null);

        // 8. Botón/Checkbox con Brillo de Selección
        int cSize = 100;
        int cX = (getWidth() - cSize) / 2;
        int cY = pY + 300;
        rectToggle = new Rectangle(cX, cY, cSize, cSize);
        
        // Aura pulsante circular detrás del botón
        float pulso = (float) (Math.sin(anguloRotacion * 2) * 0.5 + 0.5);
        int auraAlpha = (int)(30 + (40 * pulso));
        g2d.setColor(new Color(255, 255, 200, auraAlpha));
        g2d.fillOval(cX - 15, cY - 15, cSize + 30, cSize + 30);

        BufferedImage imgActual = sonidoActivo ? imgCheck : imgCruz;
        if (imgActual != null) g2d.drawImage(imgActual, cX, cY, cSize, cSize, null);
        
        // Texto inferior de ayuda
        g2d.setFont(new Font("Monospaced", Font.ITALIC, 14));
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.drawString("Click para aplicar y salir", pX + 140, pY + pH - 25);
    }
}