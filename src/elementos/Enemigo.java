package elementos;

import static utils.Constantes.ConstantesEnemigos.*;
import static utils.MetodosAyuda.*;

import juego.Juego;

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

    public Enemigo(float x, float y, int width, int height) {
        super(x, y, width, height);
        initHitbox(x, y, width, height);
    }

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

    protected void drawAttackBox(java.awt.Graphics g, int xLvlOffset) {
        g.setColor(java.awt.Color.BLUE);
        g.drawRect((int) attackBox.x - xLvlOffset, (int) attackBox.y, (int) attackBox.width, (int) attackBox.height);
    }

    public void update(int[][] lvlData, Jugador jugador) {
        actualizarComportamiento(lvlData, jugador);
    }

    private void actualizarComportamiento(int[][] lvlData, Jugador jugador) {
        if (firstUpdate) {
            if (!IsEntityOnFloor(hitbox, lvlData)) {
                inAir = true;
            }
            firstUpdate = false;
        }

        if (inAir) {
            if (CanMoveHere(hitbox.x, hitbox.y + fallSpeed, (int) hitbox.width, (int) hitbox.height, lvlData)) {
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

                    if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, (int) hitbox.width, (int) hitbox.height, lvlData)
                            && EsSueloSolido(hitbox.x, hitbox.y, hitbox.width, hitbox.height, xSpeed, lvlData)) {
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

    public int getEnemyState() {
        return enemyState;
    }
    
    public boolean isActivo() {
        return activo;
    }

    public void drawHealthBar(java.awt.Graphics g, int xLvlOffset) {
        if (enemyState == MUERTO) return;

        int barW = (int)(40 * Juego.SCALE);
        int barH = (int)(5 * Juego.SCALE);
        int barX = (int)(hitbox.x + hitbox.width / 2 - barW / 2) - xLvlOffset;
        int barY = (int)(hitbox.y - 10 * Juego.SCALE);

        g.setColor(new java.awt.Color(30, 30, 30));
        g.fillRect(barX - 1, barY - 1, barW + 2, barH + 2);

        g.setColor(new java.awt.Color(220, 220, 220));
        g.fillRect(barX, barY, barW, barH);

        float pct = (float) vidaActual / vidaMaxima;
        int vidaW = (int)(pct * barW);
        java.awt.Color barColor;
        if (pct > 0.5f) {
            barColor = new java.awt.Color(34, 177, 76);
        } else if (pct > 0.25f) {
            barColor = new java.awt.Color(220, 120, 20);
        } else {
            barColor = new java.awt.Color(180, 30, 30);
        }
        g.setColor(barColor);
        g.fillRect(barX, barY, vidaW, barH);

        g.setColor(new java.awt.Color(255, 255, 255, 120));
        g.fillRect(barX, barY, vidaW, (int) Math.max(1, barH * 0.35f));

        int hx = barX - (int)(10 * Juego.SCALE) - 1;
        int hy = barY - (int)(2 * Juego.SCALE);
        int px = (int) Math.max(1, Juego.SCALE);
        int[][] heart = {
            {0,1,1,0,1,1,0},
            {1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1},
            {0,1,1,1,1,1,0},
            {0,0,1,1,1,0,0},
            {0,0,0,1,0,0,0}
        };
        for (int row = 0; row < heart.length; row++) {
            for (int col = 0; col < heart[row].length; col++) {
                if (heart[row][col] == 1) {
                    g.setColor(new java.awt.Color(200, 20, 20));
                    g.fillRect(hx + col * px, hy + row * px, px, px);
                }
            }
        }
        g.setColor(new java.awt.Color(255, 120, 120));
        g.fillRect(hx + px, hy, px, px);
        g.fillRect(hx + 4*px, hy, px, px);
    }
}