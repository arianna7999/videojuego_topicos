package elementos.pantallas;

import juego.VtaJuego;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;

public class MenuPrincipal extends JPanel {

    private BufferedImage fondo, logo, btnInicio, btnOpciones, btnSalir;
    private BufferedImage btnInicioGlow, btnOpcionesGlow, btnSalirGlow;
    private Rectangle rectInicio, rectOpciones, rectSalir;
    private VtaJuego ventana;
    private List<Particula> particulas;

    private double anguloFlotacion = 0;
    private int mouseX, mouseY;
    private Timer animador;

    public MenuPrincipal(VtaJuego ventana) {
        this.ventana = ventana;
        cargarRecursos();
        configurarTeclas();

        particulas = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            particulas.add(new Particula());
        }

        animador = new Timer(16, e -> {
            anguloFlotacion += 0.05;
            repaint();
        });
        animador.start();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (rectInicio != null && rectInicio.contains(e.getPoint()))
                    ventana.mostrarJuego();
                if (rectOpciones != null && rectOpciones.contains(e.getPoint())) {
            ventana.mostrarOpciones(); 
        }
                if (rectSalir != null && rectSalir.contains(e.getPoint()))
                    System.exit(0);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
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

            // Generamos las versiones con brillo una sola vez para ahorrar memoria
            Color colorGlow = new Color(250, 200, 50);
            btnInicioGlow = generarImagenGlow(btnInicio, colorGlow);
            btnOpcionesGlow = generarImagenGlow(btnOpciones, colorGlow);
            btnSalirGlow = generarImagenGlow(btnSalir, colorGlow);
        } catch (Exception e) {
            System.err.println("Error cargando imágenes: " + e.getMessage());
        }
    }

    private BufferedImage generarImagenGlow(BufferedImage original, Color color) {
        BufferedImage brillo = new BufferedImage(original.getWidth(), original.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = brillo.createGraphics();
        g.drawImage(original, 0, 0, null);
        g.setComposite(AlphaComposite.SrcAtop);
        g.setColor(color);
        g.fillRect(0, 0, original.getWidth(), original.getHeight());
        g.dispose();
        return brillo;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fondo == null || logo == null)
            return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        // 1. Fondo
        g2d.drawImage(fondo, 0, 0, getWidth(), getHeight(), null);

        // 2. Partículas
        for (Particula p : particulas) {
            p.actualizar();
            p.dibujar(g2d);
        }
        g2d.setColor(new Color(0, 50, 100, 30)); // R, G, B, Opacidad
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // 3. Viñeta
        float[] dist = { 0.3f, 1.0f };
        Color[] colores = { new Color(0, 0, 0, 0), new Color(0, 0, 0, 180) };
        RadialGradientPaint rgp = new RadialGradientPaint(
                new Point(getWidth() / 2, getHeight() / 2),
                (float) (getWidth() * 0.8), dist, colores);
        g2d.setPaint(rgp);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        int centroX = getWidth() / 2;

        // Resplandor detrás del logo// 1. Resplandor detrás del log

        double escalaLogo = (getWidth() * 0.6) / logo.getWidth();
        int logoW = (int) (logo.getWidth() * escalaLogo);
        int logoH = (int) (logo.getHeight() * escalaLogo);
        int desplazamientoY = (int) (Math.sin(anguloFlotacion) * 5);
        int logoY = (getHeight() / 5000) + desplazamientoY;
        g2d.drawImage(logo, centroX - (logoW / 2), logoY, logoW, logoH, null);

        // 4. RESPLANDOR DETRÁS DEL LOGO (Capa inferior al logo)
        float[] distLogo = { 0.0f, 0.7f };
        Color[] coloresLogo = { new Color(250, 200, 50, 80), new Color(0, 0, 0, 0) };
        RadialGradientPaint rgpLogo = new RadialGradientPaint(
                new Point(centroX, logoY + (logoH / 2)),
                (float) (logoW * 0.8), distLogo, coloresLogo);
        g2d.setPaint(rgpLogo);
        g2d.fillOval(centroX - logoW, logoY - (logoH / 2), logoW * 2, logoH * 2);

        // 5. DIBUJAR LOGO (Capa superior al resplandor)
        g2d.drawImage(logo, centroX - (logoW / 2), logoY, logoW, logoH, null);

        // 5. BOTONES (ESTÁTICOS)
        // Calculamos una posición Y fija para los botones ignorando el desplazamientoY
        // del logo
        int yBotonesBase = (getHeight() / 15) + logoH + 40;

        rectInicio = dibujarBotonAnimado(g2d, btnInicio, btnInicioGlow, centroX, yBotonesBase);

        int separacion = rectInicio.height + 20;
        rectOpciones = dibujarBotonAnimado(g2d, btnOpciones, btnOpcionesGlow, centroX, yBotonesBase + separacion);

        rectSalir = dibujarBotonAnimado(g2d, btnSalir, btnSalirGlow, centroX, yBotonesBase + (separacion * 2));

        // Luz focalizada en el puntero del mouse
        float radioLuz = 250f;
        float[] distMouse = { 0.0f, 1.0f };
        Color[] coloresMouse = { new Color(255, 255, 150, 40), new Color(0, 0, 0, 0) };

        RadialGradientPaint rgpMouse = new RadialGradientPaint(
                new Point(mouseX, mouseY), radioLuz, distMouse, coloresMouse);

        g2d.setPaint(rgpMouse);
        g2d.fillOval(mouseX - (int) radioLuz, mouseY - (int) radioLuz, (int) radioLuz * 2, (int) radioLuz * 2);
    }

    private Rectangle dibujarBotonAnimado(Graphics2D g2d, BufferedImage img, BufferedImage imgGlow, int centroX,
            int y) {
        double escala = (getWidth() * 0.45) / img.getWidth();
        int w = (int) (img.getWidth() * escala);
        int h = (int) (img.getHeight() * escala);
        int x = centroX - (w / 2);
        Rectangle rect = new Rectangle(x, y, w, h);

        if (rect.contains(mouseX, mouseY)) {
            // Brillo con la forma del botón
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            int b = 4;
            for (int dx = -b; dx <= b; dx += 2) {
                for (int dy = -b; dy <= b; dy += 2) {
                    g2d.drawImage(imgGlow, x + dx, y + dy, w, h, null);
                }
            }
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            g2d.drawImage(img, x - 2, y - 2, w + 4, h + 4, null);
        } else {
            g2d.drawImage(img, x, y, w, h, null);
        }
        return rect;
    }

    private void configurarTeclas() {
        InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        im.put(KeyStroke.getKeyStroke("ESCAPE"), "salir");
        am.put("salir", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    private class Particula {
        double x, y, velY;
        int opacidad, tamaño;

        public Particula() {
            reset(true);
        }

        public void reset(boolean inicio) {
            x = Math.random() * getWidth();
            y = inicio ? Math.random() * getHeight() : getHeight() + 10;
            velY = 0.3 + Math.random() * 0.8;
            tamaño = 2 + (int) (Math.random() * 3);
            opacidad = 70 + (int) (Math.random() * 100);
        }

        public void actualizar() {
            y -= velY;
            if (y < -20)
                reset(false);
        }

        public void dibujar(Graphics2D g2d) {
            g2d.setColor(new Color(255, 200, 50, opacidad));
            g2d.fillOval((int) x, (int) y, tamaño, tamaño);
        }
    }

    private void dibujarRayos(Graphics2D g2d) {
        // Usamos una opacidad muy baja para que sea sutil
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.07f));
        g2d.setColor(new Color(255, 255, 200)); // Blanco cálido

        // Dibujamos 3 rayos inclinados con diferentes grosores
        int[][] rayos = {
                { getWidth() / 4, getWidth() / 2, 150 },
                { getWidth() / 2, getWidth() * 3 / 4, 200 },
                { 0, getWidth() / 3, 100 }
        };

        for (int[] r : rayos) {
            int[] xPoints = { r[0], r[1], r[1] - r[2] };
            int[] yPoints = { 0, getHeight(), getHeight() };
            g2d.fillPolygon(xPoints, yPoints, 3);
        }
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }

}