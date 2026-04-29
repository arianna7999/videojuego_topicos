package elementos.pantallas;

import juego.Juego;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.BasicStroke;
import java.awt.RenderingHints;
import java.io.InputStream;
import javax.imageio.ImageIO;

import elementos.jugador.Personaje;

import java.awt.image.BufferedImage;

public class MenuSeleccion {
    
    private Juego juego;
    private int seleccionIndice = 0;
    private BufferedImage[] portadasPersonajes;
    
    private Font customFont;
    private Font titleFont;
    private Font subFont;
    private Color redColor;
    private Color goldColor;
    private Color shadowColor;

    public final Personaje[] PERSONAJES = {
            new Personaje("Hank", 120, 2.5f, 25, -2.25f, "Soldier.png", 9, 7, 100, 100, "golpe_brutal", 87f, 80f, 500, 3),
            new Personaje("Frank", 200, 2f, 20, -2.25f, "Frank.png", 9, 7, 100, 100, "coraza", 87f, 80f, 400, 2),
            new Personaje("Saori", 180, 2.8f, 20, -2.6f, "Saori.png", 13, 8, 100, 100, "robo_vida", 87f, 80f, 200, 5),
            new Personaje("Lucerys", 180, 2.5f, 18, -2.4f, "Lucerys.png", 13, 8, 100, 100, "lluvia", 87f, 80f, 400, 3)
    };

    public MenuSeleccion(Juego juego) {
        this.juego = juego;
        cargarFuentesYColores();
        cargarPortadas();
    }

    private void cargarFuentesYColores() {
        try {
            InputStream is = getClass().getResourceAsStream("/res/Goudy_Mediaeval_Regular.ttf");
            if (is != null) {
                customFont = Font.createFont(Font.TRUETYPE_FONT, is);
            } else {
                customFont = new Font("Georgia", Font.BOLD, 12);
            }
        } catch (Exception e) {
            customFont = new Font("Georgia", Font.BOLD, 12);
            e.printStackTrace();
        }
        
        titleFont = customFont.deriveFont(Font.BOLD, (int) (48 * Juego.SCALE));
        subFont = customFont.deriveFont(Font.PLAIN, (int) (16 * Juego.SCALE));
        redColor = new Color(200, 50, 50);
        goldColor = new Color(212, 175, 55);
        shadowColor = Color.DARK_GRAY;
    }

    private void cargarPortadas() {
        String[] portadaArchivos = { "hank_portada.png", "frank_portada.png", "saori_portada.png",
                "lucerys_portada.png" };
        portadasPersonajes = new BufferedImage[portadaArchivos.length];
        for (int i = 0; i < portadaArchivos.length; i++) {
            try {
                InputStream is = getClass().getResourceAsStream("/res/" + portadaArchivos[i]);
                if (is != null) {
                    portadasPersonajes[i] = ImageIO.read(is);
                }
            } catch (Exception e) {
                portadasPersonajes[i] = null;
            }
        }
    }

    public void moverSeleccion(int delta) {
        seleccionIndice = (seleccionIndice + delta + PERSONAJES.length) % PERSONAJES.length;
    }

    public void confirmarSeleccion() {
        juego.aplicarPersonajeElegido(PERSONAJES[seleccionIndice]);
    }

    public void dibujar(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Font selTitleFont = customFont.deriveFont(Font.BOLD, (int) (32 * Juego.SCALE));
        Font selNameFont = customFont.deriveFont(Font.BOLD, (int) (20 * Juego.SCALE));
        Font selStatFont = customFont.deriveFont(Font.PLAIN, (int) (11 * Juego.SCALE));
        Font selHintFont = customFont.deriveFont(Font.PLAIN, (int) (13 * Juego.SCALE));

        g2.setFont(selTitleFont);
        String titulo = "ELIGE TU PERSONAJE";
        FontMetrics fmT = g2.getFontMetrics();
        g2.setColor(new Color(212, 175, 55));
        g2.drawString(titulo, (Juego.GAME_WIDTH - fmT.stringWidth(titulo)) / 2, (int) (60 * Juego.SCALE));

        int cardW = (int) (90 * Juego.SCALE);
        int imgH = (int) (70 * Juego.SCALE);
        int cardH = (int) (175 * Juego.SCALE);
        int gap = (int) (20 * Juego.SCALE);
        int totalW = PERSONAJES.length * cardW + (PERSONAJES.length - 1) * gap;
        int startX = (Juego.GAME_WIDTH - totalW) / 2;
        int cardY = (int) (80 * Juego.SCALE);

        int[][] stats = { { 120, 25, 25, 23 }, { 200, 20, 20, 22 }, { 180, 28, 20, 26 }, { 180, 25, 18, 24 } };
        int[] statMax = { 200, 30, 30, 30 };
        String[] roles = { "Guerrero", "Tanque", "Doctora", "Arquero" };
        Color[] roleColors = {
                new Color(100, 180, 255), new Color(100, 220, 100),
                new Color(255, 180, 60), new Color(200, 100, 255)
        };

        for (int i = 0; i < PERSONAJES.length; i++) {
            int cx = startX + i * (cardW + gap);
            boolean sel = (i == seleccionIndice);

            if (sel) {
                g2.setColor(new Color(60, 50, 20));
                g2.fillRoundRect(cx - 4, cardY - 4, cardW + 8, cardH + 8, 16, 16);
                g2.setColor(goldColor);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(cx - 4, cardY - 4, cardW + 8, cardH + 8, 16, 16);
            } else {
                g2.setColor(new Color(30, 30, 40));
                g2.fillRoundRect(cx, cardY, cardW, cardH, 12, 12);
                g2.setColor(new Color(80, 80, 100));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(cx, cardY, cardW, cardH, 12, 12);
            }

            int padding = (int) (4 * Juego.SCALE);
            int imgX = cx + padding;
            int imgY = cardY + padding;
            int imgW = cardW - padding * 2;
            Shape oldClip = g2.getClip();
            g2.setClip(new RoundRectangle2D.Float(imgX, imgY, imgW, imgH, 10, 10));
            
            if (portadasPersonajes != null && portadasPersonajes[i] != null) {
                g2.drawImage(portadasPersonajes[i], imgX, imgY, imgW, imgH, null);
            } else {
                g2.setColor(new Color(50, 50, 70));
                g2.fillRect(imgX, imgY, imgW, imgH);
                g2.setColor(new Color(100, 100, 130));
                g2.drawString("?", imgX + imgW / 2 - 5, imgY + imgH / 2 + 5);
            }
            g2.setClip(oldClip);

            g2.setColor(sel ? goldColor : new Color(60, 60, 80));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(imgX, imgY, imgW, imgH, 10, 10);

            int textBaseY = cardY + imgH + padding * 2;
            g2.setFont(selNameFont);
            FontMetrics fmN = g2.getFontMetrics();
            g2.setColor(sel ? goldColor : Color.WHITE);
            String nombre = PERSONAJES[i].nombre;
            g2.drawString(nombre, cx + (cardW - fmN.stringWidth(nombre)) / 2, textBaseY + (int) (14 * Juego.SCALE));

            g2.setFont(selStatFont);
            FontMetrics fmS = g2.getFontMetrics();
            g2.setColor(roleColors[i]);
            String rol = "[" + roles[i] + "]";
            g2.drawString(rol, cx + (cardW - fmS.stringWidth(rol)) / 2, textBaseY + (int) (24 * Juego.SCALE));

            String[] statLabels = { "VID", "VEL", "DAÑ", "SAL" };
            int[] statVals = stats[i];
            Color[] barColors = {
                    new Color(220, 80, 80), new Color(80, 200, 120),
                    new Color(255, 160, 40), new Color(80, 160, 255)
            };
            int barY = textBaseY + (int) (32 * Juego.SCALE);
            int barAreaW = cardW - (int) (20 * Juego.SCALE);
            int barH2 = (int) (7 * Juego.SCALE);
            int barSpacing = (int) (16 * Juego.SCALE);
            for (int s = 0; s < 4; s++) {
                int by = barY + s * barSpacing;
                g2.setColor(new Color(180, 180, 180));
                g2.drawString(statLabels[s], cx + (int) (6 * Juego.SCALE), by + barH2);
                int bx = cx + (int) (26 * Juego.SCALE);
                int bw = barAreaW - (int) (20 * Juego.SCALE);
                g2.setColor(new Color(50, 50, 60));
                g2.fillRoundRect(bx, by, bw, barH2, 4, 4);
                int fill = (int) ((float) statVals[s] / statMax[s] * bw);
                g2.setColor(barColors[s]);
                g2.fillRoundRect(bx, by, fill, barH2, 4, 4);
            }
        }
        g2.setFont(selTitleFont);
        g2.setColor(new Color(212, 175, 55, 180));
        int arrowY = cardY + cardH / 2 + (int) (10 * Juego.SCALE);
        g2.drawString("<", startX - (int) (30 * Juego.SCALE), arrowY);
        g2.drawString(">", startX + totalW + (int) (10 * Juego.SCALE), arrowY);
        g2.setFont(selHintFont);
        FontMetrics fmH = g2.getFontMetrics();
        String hint = "LEFT/RIGHT para navegar    ENTER para confirmar";
        g2.setColor(new Color(180, 180, 180));
        g2.drawString(hint, (Juego.GAME_WIDTH - fmH.stringWidth(hint)) / 2, cardY + cardH + (int) (30 * Juego.SCALE));
    }
}