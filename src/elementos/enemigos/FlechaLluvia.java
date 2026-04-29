package elementos.enemigos;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import juego.Juego;
import utils.LoadSave;


public class FlechaLluvia {

  
    private static BufferedImage[] frames = null;
    private static final int FRAME_SIZE  = 100;
    private static final int NUM_FRAMES  = 3;
    private static final int RENDER_SIZE = (int)(32 * Juego.SCALE); 

    private float x, y;         
    private float targetY;        
    private float speed;          
    private int   animTick  = 0;
    private int   animInd   = 0;
    private static final int ANIM_SPEED = 6;

    private boolean impactada  = false;  
    private boolean aplicaDaño = false;  
    private int     dañoBase;

    public FlechaLluvia(float x, float startY, float targetY, int daño) {
        this.x       = x;
        this.y       = startY;
        this.targetY = targetY;
        this.dañoBase = daño;
        this.speed    = 8 * Juego.SCALE;
        cargarFrames();
    }

    private static void cargarFrames() {
        if (frames != null) return;
        BufferedImage sheet = LoadSave.GetSpriteAtlas("flecha_lluvia.png");
        if (sheet == null) return;
        frames = new BufferedImage[NUM_FRAMES];
        for (int i = 0; i < NUM_FRAMES; i++) {
            frames[i] = sheet.getSubimage(i * FRAME_SIZE, 0, FRAME_SIZE, FRAME_SIZE);
        }
    }

    public void update() {
        if (impactada) {
           
            animTick++;
            return;
        }

        
        y += speed;

       
        animTick++;
        if (animTick >= ANIM_SPEED) {
            animTick = 0;
            animInd  = (animInd + 1) % NUM_FRAMES;
        }

        if (y >= targetY) {
            y          = targetY;
            impactada  = true;
            aplicaDaño = true;
            animTick   = 0; 
        }
    }

    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (frames == null) return;

        
        if (!impactada) {
            float progress = Math.min(1f, (y - (targetY - 300 * Juego.SCALE)) / (300 * Juego.SCALE));
            int shadowAlpha = (int)(60 * progress);
            shadowAlpha = Math.max(0, Math.min(60, shadowAlpha));
            if (shadowAlpha > 0) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
                g2.setColor(new java.awt.Color(0, 0, 0, shadowAlpha));
                int sx = (int)(x - xLvlOffset) - RENDER_SIZE / 4;
                int sy = (int)targetY;
                g2.fillOval(sx, sy, RENDER_SIZE / 2, (int)(6 * Juego.SCALE));
            }
        }

       
        int frameIdx = impactada ? NUM_FRAMES - 1 : animInd;
        int drawX = (int)(x - xLvlOffset) - RENDER_SIZE / 2;
        int drawY = (int)y - yLvlOffset - RENDER_SIZE / 2;
        g.drawImage(frames[frameIdx], drawX, drawY, RENDER_SIZE, RENDER_SIZE, null);
    }

  
     
    public java.awt.geom.Rectangle2D.Float getHitbox() {
        float radioX = RENDER_SIZE * 0.8f;          
        float alturaNivel = juego.Juego.GAME_HEIGHT; 
        return new java.awt.geom.Rectangle2D.Float(x - radioX, 0, radioX * 2, alturaNivel);
    }

    public boolean isImpactada()  { return impactada;  }
    public boolean isAplicaDaño() { return aplicaDaño; }
    public void    consumirDaño() { aplicaDaño = false; }
    public int     getDañoBase()  { return dañoBase;   }

  
    public boolean isMuerta() {
        return impactada && animTick >= 20;
    }
}
