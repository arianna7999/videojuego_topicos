package elementos.objetos;

import java.awt.geom.Rectangle2D;
import juego.Juego;

public class MaquinaGolpeable {

    private float x, y;
    private Rectangle2D.Float hitbox;
    private int tipoMaquina; // 0=Esferas, 1=Cuadrados, 2=Triángulos
    private int colorActual = 0; // 0=Rojo, 1=Amarillo, 2=Azul
    int nuevoTamano = (int) (Juego.TILES_SIZE * 2.5);

    public MaquinaGolpeable(float x, float y, int tipoMaquina) {
        this.x = x;
        this.y = y;
        this.tipoMaquina = tipoMaquina;
        // La hacemos del tamaño de un bloque normal (64x64 aprox)
        hitbox = new Rectangle2D.Float(x, y - (nuevoTamano - Juego.TILES_SIZE), nuevoTamano, nuevoTamano);
    }

    public void golpear() {
        colorActual++; // Cambia al siguiente color
        if (colorActual >= 3) {
            colorActual = 0; // Regresa al rojo
        }
    }

    public Rectangle2D.Float getHitbox() { return hitbox; }
    public float getX() { return x; }
    public float getY() { return y; }
    public int getTipoMaquina() { return tipoMaquina; }
    public int getColorActual() { return colorActual; }
}