package elementos.objetos;

import java.awt.geom.Rectangle2D;

import juego.Juego;

public class ObjetoJuego {
    protected int x, y, tipoObjeto;
    protected Rectangle2D.Float hitbox;
    protected boolean activo = true;

    // Regresamos animSpeed a 15 como velocidad por defecto
    protected int animTick = 0, animInd = 0, animSpeed = 15;
    protected int estado;

    public ObjetoJuego(int x, int y, int tipoObjeto) {
        this.x = x;
        this.y = y;
        this.tipoObjeto = tipoObjeto;
    }

    // Crea el cuadro invisible que detecta los golpes
    protected void initHitbox(float width, float height) {
        hitbox = new Rectangle2D.Float(x, y, width * Juego.SCALE, height * Juego.SCALE);
    }

    // Getters básicos
    public boolean isActivo() {
        return activo;
    }

    public Rectangle2D.Float getHitbox() {
        return hitbox;
    }

    public int getEstado() {
        return estado;
    }

    public int getAnimInd() {
        return animInd;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getTipoObjeto() {
        return tipoObjeto;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}