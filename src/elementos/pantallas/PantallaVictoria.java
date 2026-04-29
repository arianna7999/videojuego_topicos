package elementos.pantallas;

import juego.Juego;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.InputStream;

public class PantallaVictoria {
    
    private Juego juego;
    private Font customFont;
    private Font titleFont;
    private Font subFont;
    private Color goldColor;
    private Color shadowColor;

    public PantallaVictoria(Juego juego) {
        this.juego = juego;
        cargarFuentesYColores();
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
        goldColor = new Color(212, 175, 55);
        shadowColor = Color.DARK_GRAY;
    }

    public void dibujar(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        String tituloText = "¡VICTORIA CUEVA LIMPIA!";
        String subText = "[Presiona ENTER para reiniciar]";

        g2.setFont(titleFont);
        FontMetrics metricsTitle = g2.getFontMetrics(titleFont);
        int xTitle = (Juego.GAME_WIDTH - metricsTitle.stringWidth(tituloText)) / 2;
        int yTitle = (Juego.GAME_HEIGHT / 2) - (metricsTitle.getHeight() / 2);

        g2.setFont(subFont);
        FontMetrics metricsSub = g2.getFontMetrics(subFont);
        int xSub = (Juego.GAME_WIDTH - metricsSub.stringWidth(subText)) / 2;
        int ySub = yTitle + metricsTitle.getHeight() + (int) (20 * Juego.SCALE);

        // Sombra del título
        g2.setFont(titleFont);
        g2.setColor(shadowColor);
        g2.drawString(tituloText, xTitle + 3, yTitle + 3);
        
        // Título principal
        g2.setColor(goldColor);
        g2.drawString(tituloText, xTitle, yTitle);

        // Subtítulo
        g2.setFont(subFont);
        g2.setColor(Color.WHITE);
        g2.drawString(subText, xSub, ySub);
    }
}