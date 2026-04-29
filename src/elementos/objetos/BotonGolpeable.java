package elementos.objetos;

import java.awt.geom.Rectangle2D;
import juego.Juego;

public class BotonGolpeable {

    private float x, y;
    private Rectangle2D.Float hitbox;

    public BotonGolpeable(float x, float y) {
        this.x = x;
        this.y = y;
        hitbox = new Rectangle2D.Float(x, y, Juego.TILES_SIZE, Juego.TILES_SIZE);
    }

    public Rectangle2D.Float getHitbox() { return hitbox; }
    public float getX() { return x; }
    public float getY() { return y; }
}