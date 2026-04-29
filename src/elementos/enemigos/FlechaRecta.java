package elementos.enemigos;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import juego.Juego;
import utils.LoadSave;

public class FlechaRecta {

    private static BufferedImage imgDerecha  = null;
    private static BufferedImage imgIzquierda = null;

    private static final int   RENDER_SIZE = (int)(32 * Juego.SCALE);
    private static final float SPEED       = 9f * Juego.SCALE;

    private float x, y;
    private int   direccion; 
    private int   daño;
    private boolean muerta     = false;
    private boolean aplicaDaño = false;

    public FlechaRecta(float startX, float startY, int direccion, int daño) {
        this.x         = startX;
        this.y         = startY;
        this.direccion = direccion;
        this.daño      = daño;
        cargarImagen();
    }

    private static void cargarImagen() {
        if (imgDerecha != null) return;
        BufferedImage src = LoadSave.GetSpriteAtlas("flecha_recta.png");
        if (src == null) return;
        imgDerecha = src;

       
        int w = src.getWidth(), h = src.getHeight();
        imgIzquierda = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imgIzquierda.createGraphics();
        g2.drawImage(src, w, 0, -w, h, null);
        g2.dispose();
    }

    public void update(int[][] lvlData, int levelIndex) {
        if (muerta) return;

        x += SPEED * direccion;

       
        if (x < 0 || x > Juego.GAME_WIDTH * 10) {
            muerta = true;
            return;
        }

    
        if (utils.MetodosAyuda.IsSolid(x, y, lvlData , levelIndex)) {
            muerta = true;
        }
    }

    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (muerta) return;
        BufferedImage img = (direccion == 1) ? imgDerecha : imgIzquierda;
        if (img == null) return;

        int drawX = (int)(x - xLvlOffset) - RENDER_SIZE / 2;
        int drawY = (int)(y - yLvlOffset) - RENDER_SIZE / 2;
        g.drawImage(img, drawX, drawY, RENDER_SIZE, RENDER_SIZE, null);
    }

    public java.awt.geom.Rectangle2D.Float getHitbox() {
        float hw = RENDER_SIZE * 0.4f;
        float hh = RENDER_SIZE * 0.2f;
        return new java.awt.geom.Rectangle2D.Float(x - hw, y - hh, hw * 2, hh * 2);
    }

    public boolean isAplicaDaño() { return aplicaDaño; }
    public void    consumirDaño() { aplicaDaño = false; }
    public int     getDaño()      { return daño; }
    public boolean isMuerta()     { return muerta; }
    public void    matar()        { muerta = true; aplicaDaño = true; }
}
