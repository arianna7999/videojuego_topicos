package elementos.enemigos;

import static utils.Constantes.ConstantesEnemigos.ATACAR;
import static utils.Constantes.ConstantesEnemigos.CORRER;
import static utils.Constantes.ConstantesEnemigos.INACTIVO;
import static utils.Constantes.ConstantesEnemigos.MUERTO;
import static utils.Constantes.ConstantesEnemigos.RECIBIR_GOLPE;
import static utils.MetodosAyuda.CanMoveHere;
import static utils.MetodosAyuda.EsSueloSolido;
import static utils.MetodosAyuda.GetEntityYPosUnderRoofOrAboveFloor;
import static utils.MetodosAyuda.IsEntityOnFloor;

import juego.Juego;
import java.awt.Graphics;

import elementos.jugador.Jugador;

public abstract class Enemigo extends Cascaron {
    protected java.awt.geom.Rectangle2D.Float attackBox;
    protected int vidaMaxima = 50;
    protected int vidaActual = vidaMaxima;
    protected int attackBoxOffsetX;
    protected boolean attackChecked;
    protected int enemyState = INACTIVO;
    protected boolean firstUpdate = true;
    protected boolean inAir = false;
    protected float fallSpeed = 0f;
    protected float gravity = 0.04f * Juego.SCALE;
    protected float walkSpeed = 0.4f * Juego.SCALE;
    protected int walkDir = 1;
    protected boolean activo = true;
    protected int damage = 10;

    protected int visionRange = (int) (Juego.TILES_SIZE * 5);
    protected int attackRange = (int) (Juego.TILES_SIZE * 1);

    private static final java.awt.Color COLOR_FONDO_BARRA = new java.awt.Color(30, 30, 30);
    private static final java.awt.Color COLOR_FONDO_VIDA = new java.awt.Color(220, 220, 220);
    private static final java.awt.Color COLOR_VIDA_ALTA = new java.awt.Color(34, 177, 76);
    private static final java.awt.Color COLOR_VIDA_MEDIA = new java.awt.Color(220, 120, 20);
    private static final java.awt.Color COLOR_VIDA_BAJA = new java.awt.Color(180, 30, 30);
    private static final java.awt.Color COLOR_BRILLO_BARRA = new java.awt.Color(255, 255, 255, 120);
    private static final java.awt.Color COLOR_CORAZON_BASE = new java.awt.Color(200, 20, 20);
    private static final java.awt.Color COLOR_CORAZON_BRILLO = new java.awt.Color(255, 120, 120);

    private static final int[][] HEART_PIXELS = {
        {0,1,1,0,1,1,0},
        {1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1},
        {0,1,1,1,1,1,0},
        {0,0,1,1,1,0,0},
        {0,0,0,1,0,0,0}
    };

    public Enemigo(float x, float y, int width, int height) {
        super(x, y, width, height);
        initHitbox(x, y, width, height);
    }

    public abstract void render(Graphics g, int xLvlOffset, int yLvlOffset);

    protected void initAttackBox() {
        attackBox = new java.awt.geom.Rectangle2D.Float(x, y, (int) (20 * Juego.SCALE), (int) (20 * Juego.SCALE));
        attackBoxOffsetX = (int) (Juego.SCALE * 1);
    }
    protected void initAttackBox(int width, int height, int offsetX) {
        attackBox = new java.awt.geom.Rectangle2D.Float(x, y, (int)(width * Juego.SCALE), (int)(height * Juego.SCALE));
        attackBoxOffsetX = (int) (Juego.SCALE * offsetX);
    }

    protected void updateAttackBox() {
        if (walkDir == 1) {
            attackBox.x = hitbox.x + hitbox.width + attackBoxOffsetX;
        } else {
            attackBox.x = hitbox.x - attackBox.width - attackBoxOffsetX;
        }
        attackBox.y = hitbox.y;
    }

    protected void checkEnemyHit(Jugador jugador) {
        if (attackBox.intersects(jugador.getHitbox())) {
            jugador.recibirDaño(damage, walkDir);
            attackChecked = true;
        }
    }

    protected void drawAttackBox(java.awt.Graphics g, int xLvlOffset, int yLvlOffset) {
        g.setColor(java.awt.Color.BLUE);
        g.drawRect((int) attackBox.x - xLvlOffset, (int) attackBox.y - yLvlOffset, (int) attackBox.width, (int) attackBox.height);
    }

    public void update(int[][] lvlData, Jugador jugador, int levelIndex) {
        actualizarComportamiento(lvlData, jugador, levelIndex);
    }

    private void actualizarComportamiento(int[][] lvlData, Jugador jugador, int levelIndex) {
        if (firstUpdate) {
            if (!IsEntityOnFloor(hitbox, lvlData, levelIndex)) {
                inAir = true;
            }
            firstUpdate = false;
        }

        if (inAir) {
            if (CanMoveHere(hitbox.x, hitbox.y + fallSpeed, (int) hitbox.width, (int) hitbox.height, lvlData, levelIndex)) {
                hitbox.y += fallSpeed;
                fallSpeed += gravity;
            } else {
                inAir = false;
                hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, fallSpeed);
            }
        } else {
            switch (enemyState) {
                case INACTIVO:
                    enemyState = CORRER;
                    break;
                case CORRER:
                    if (esJugadorEnRangoVision(jugador)) {
                        mirarHaciaJugador(jugador);
                        if (esJugadorEnRangoAtaque(jugador)) {
                            enemyState = ATACAR;
                            break;
                        }
                    }
                    float xSpeed = walkDir * walkSpeed;
                    if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, (int) hitbox.width, (int) hitbox.height, lvlData, levelIndex)
                            && EsSueloSolido(hitbox.x, hitbox.y, hitbox.width, hitbox.height, xSpeed, lvlData, levelIndex)) {
                        hitbox.x += xSpeed;
                    } else {
                        walkDir *= -1;
                    }
                    break;
                case ATACAR:
                    if (!esJugadorEnRangoAtaque(jugador)) {
                        enemyState = CORRER;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    protected boolean esJugadorEnRangoVision(Jugador jugador) {
        if (jugador.isDead()) {
            return false;
        }
        int absY = (int) Math.abs(jugador.hitbox.y - hitbox.y);
        if (absY <= Juego.TILES_SIZE) {
            float centroJugadorX = jugador.hitbox.x + (jugador.hitbox.width / 2);
            float centroEnemigoX = hitbox.x + (hitbox.width / 2);
            int distanciaCentroACentro = (int) Math.abs(centroJugadorX - centroEnemigoX);
            return distanciaCentroACentro <= visionRange;
        }
        return false;
    }

    protected boolean esJugadorEnRangoAtaque(Jugador jugador) {
        if (jugador.isDead()) {
            return false;
        }
        float centroJugadorX = jugador.hitbox.x + (jugador.hitbox.width / 2);
        float centroEnemigoX = hitbox.x + (hitbox.width / 2);
        int distanciaCentroACentro = (int) Math.abs(centroJugadorX - centroEnemigoX);
        return distanciaCentroACentro <= attackRange;
    }

    protected void mirarHaciaJugador(Jugador jugador) {
        if (jugador.hitbox.x > hitbox.x) {
            walkDir = 1;
        } else {
            walkDir = -1;
        }
    }

    public void recibirDaño(int cantidad) {
        vidaActual -= cantidad;
        if (vidaActual <= 0) {
            vidaActual = 0;
            enemyState = MUERTO;
        } else {
            enemyState = RECIBIR_GOLPE;
        }
    }

    public int getEnemyState() { return enemyState; }
    
    public boolean isActivo() { return activo; }

    public void drawHealthBar(java.awt.Graphics g, int xLvlOffset, int yLvlOffset) {
        if (enemyState == MUERTO) return;

        int barW = (int)(40 * Juego.SCALE);
        int barH = (int)(5 * Juego.SCALE);
        int barX = (int)(hitbox.x + hitbox.width / 2 - barW / 2) - xLvlOffset;
        int barY = (int)(hitbox.y - 10 * Juego.SCALE) - yLvlOffset;

        g.setColor(COLOR_FONDO_BARRA);
        g.fillRect(barX - 1, barY - 1, barW + 2, barH + 2);

        g.setColor(COLOR_FONDO_VIDA);
        g.fillRect(barX, barY, barW, barH);

        float pct = (float) vidaActual / vidaMaxima;
        int vidaW = (int)(pct * barW);
        
        java.awt.Color barColor;
        if (pct > 0.5f) {
            barColor = COLOR_VIDA_ALTA;
        } else if (pct > 0.25f) {
            barColor = COLOR_VIDA_MEDIA;
        } else {
            barColor = COLOR_VIDA_BAJA;
        }
        
        g.setColor(barColor);
        g.fillRect(barX, barY, vidaW, barH);

        g.setColor(COLOR_BRILLO_BARRA);
        g.fillRect(barX, barY, vidaW, (int) Math.max(1, barH * 0.35f));

        int hx = barX - (int)(10 * Juego.SCALE) - 1;
        int hy = barY - (int)(2 * Juego.SCALE);
        int px = (int) Math.max(1, Juego.SCALE);
        
        for (int row = 0; row < HEART_PIXELS.length; row++) {
            for (int col = 0; col < HEART_PIXELS[row].length; col++) {
                if (HEART_PIXELS[row][col] == 1) {
                    g.setColor(COLOR_CORAZON_BASE);
                    g.fillRect(hx + col * px, hy + row * px, px, px);
                }
            }
        }
        g.setColor(COLOR_CORAZON_BRILLO);
        g.fillRect(hx + px, hy, px, px);
        g.fillRect(hx + 4*px, hy, px, px);
    }
}