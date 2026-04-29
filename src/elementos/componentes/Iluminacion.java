package elementos.componentes;
import elementos.jugador.Jugador;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;

import juego.Juego;

public class Iluminacion {
    
    private float radioLuz;
    private Color[] colores;
    private float[] distancias;

    public Iluminacion() {
        // Configuramos cómo queremos que se vea la luz desde que nace
        radioLuz = 200 * Juego.SCALE;
        distancias = new float[] { 0.0f, 1.0f };
        colores = new Color[] {
                new Color(0, 0, 0, 0),    // Luz transparente en el centro
                new Color(0, 0, 0, 240)   // Oscuridad en los bordes
        };
    }

    public void draw(Graphics g, Jugador player, int xLvlOffset, int yLvlOffset) {
        Graphics2D g2 = (Graphics2D) g;

        // Calculamos el centro basándonos en el jugador y la cámara
        int centroX = (int) (player.getHitbox().x + player.getHitbox().width / 2) - xLvlOffset;
        int centroY = (int) (player.getHitbox().y + player.getHitbox().height / 2) - yLvlOffset;

        if (radioLuz > 0) {
            Point2D centroLuz = new Point2D.Float(centroX, centroY);
            RadialGradientPaint luz = new RadialGradientPaint(centroLuz, radioLuz, distancias, colores);
            
            Paint pinturaOriginal = g2.getPaint();
            g2.setPaint(luz);
            g2.fillRect(0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT);
            g2.setPaint(pinturaOriginal);
        }
    }

    // Puedes agregar métodos extra si luego quieres que la luz cambie de tamaño
    public void setRadioLuz(float nuevoRadio) {
        this.radioLuz = nuevoRadio;
    }
}