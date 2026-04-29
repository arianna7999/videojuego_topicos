package elementos.enemigos;

import static utils.Constantes.ConstantesEnemigos.*;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import elementos.jugador.Jugador;
import juego.Juego;
import utils.LoadSave;

public class JefeFinal extends Enemigo {

    private BufferedImage[][] animaciones;
    private int aniTick, aniIndex, aniSpeed = 25;
    private int deadTimer = 0;

    private float xDrawOffset = 110 * Juego.SCALE;
    private float yDrawOffset = 100 * Juego.SCALE;
    private static final int BOSS_DRAW_WIDTH = 280;
    private static final int BOSS_DRAW_HEIGHT = 280;
    private boolean escudoActivo = true;
    private int escudoMaximo = 150;
    private int escudoActual = escudoMaximo;
    private int escudoRegenTimer = 0;
    private int tiempoParaRegenerar = 800;

    public JefeFinal(float x, float y) {
        super(x, y, (int) (44 * Juego.SCALE), (int) (56 * Juego.SCALE));
        this.vidaMaxima = 500;
        this.vidaActual = vidaMaxima;
        this.attackRange = (int) (Juego.TILES_SIZE * 2.0f);
        this.damage = 35;
        initAttackBox(60, 60, 20);
        cargarAnimaciones();
    }

    @Override
    public void recibirDaño(int cantidad) {
        escudoRegenTimer = 0;

        if (escudoActivo) {
            escudoActual -= cantidad;
            
            if (escudoActual <= 0) {
                int dañoSobrante = Math.abs(escudoActual);
                escudoActual = 0;
                escudoActivo = false;
                if (dañoSobrante > 0) {
                    super.recibirDaño(dañoSobrante);
                }
            }
        } else {
            super.recibirDaño(cantidad);
        }
    }

    public void update(int[][] lvlData, Jugador jugador, int levelIndex) {
        super.update(lvlData, jugador, levelIndex);
        updateAttackBox();

        if (escudoActual != escudoMaximo && enemyState != MUERTO) {
            escudoRegenTimer++;
            if (escudoRegenTimer >= tiempoParaRegenerar) {
                escudoActivo = true;
                escudoActual += escudoMaximo /10;
                escudoRegenTimer = 0;
            }

        }

        actualizarAnimacion(jugador);
    }

    private void actualizarAnimacion(Jugador jugador) {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (enemyState == ATACAR) {
                if (aniIndex == 4 && !attackChecked) {
                    checkEnemyHit(jugador);
                }
            }
            if (aniIndex >= GetSpriteAmount(JEFE_FINAL, enemyState)) {
                if (enemyState == MUERTO) {
                    aniIndex = GetSpriteAmount(JEFE_FINAL, MUERTO) - 1;
                } else {
                    aniIndex = 0;
                    if (enemyState == ATACAR || enemyState == RECIBIR_GOLPE) {
                        enemyState = INACTIVO;
                        attackChecked = false;
                    }
                }
            }
        }
        if (enemyState == MUERTO && aniIndex == GetSpriteAmount(JEFE_FINAL, MUERTO) - 1) {
            deadTimer++;
            if (deadTimer >= 400)
                activo = false;
        }
    }

    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        int flipX = 0;
        int flipW = 1;
        int drawWidth = (int) (BOSS_DRAW_WIDTH * Juego.SCALE);
        int drawHeight = (int) (BOSS_DRAW_HEIGHT * Juego.SCALE);

        if (walkDir == -1) {
            flipX = drawWidth;
            flipW = -1;
        }

        g.drawImage(animaciones[enemyState][aniIndex],
                (int) (hitbox.x - xDrawOffset) - xLvlOffset + flipX,
                (int) (hitbox.y - yDrawOffset) - yLvlOffset,
                drawWidth * flipW,
                drawHeight, null);

        drawHealthBar(g, xLvlOffset, yLvlOffset);
        drawHitbox(g, xLvlOffset, yLvlOffset);
        drawAttackBox(g, xLvlOffset, yLvlOffset);
        drawEscudo(g, xLvlOffset, yLvlOffset);
    }

    private void drawEscudo(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (enemyState == MUERTO || !escudoActivo) return;
        int cx = (int)(hitbox.x + hitbox.width / 2) - xLvlOffset;
        java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
        float porcentajeEscudo = (float) escudoActual / escudoMaximo;
        int alphaFill = (int)(60 * porcentajeEscudo);
        int alphaBorder = (int)(180 * porcentajeEscudo);
        g2d.setColor(new java.awt.Color(80, 180, 255, alphaFill));
        int radio = (int)(50 * Juego.SCALE);
        g2d.fillOval(cx - radio, (int)(hitbox.y - 20 * Juego.SCALE) - radio/2, radio*2, (int)(hitbox.height + 40 * Juego.SCALE));

        g2d.setColor(new java.awt.Color(100, 200, 255, alphaBorder));
        g2d.setStroke(new java.awt.BasicStroke(2));
        g2d.drawOval(cx - radio, (int)(hitbox.y - 20 * Juego.SCALE) - radio/2, radio*2, (int)(hitbox.height + 40 * Juego.SCALE));
        
        int anchoBarra = (int) (50 * Juego.SCALE);
        int altoBarra = (int) (4 * Juego.SCALE);
        int xBarra = (int) (hitbox.x - xLvlOffset) + (int)(hitbox.width/2) - (anchoBarra/2);
        int yBarra = (int) (hitbox.y - (18 * Juego.SCALE) - yLvlOffset); 

        g.setColor(new java.awt.Color(50, 50, 50)); 
        g.fillRect(xBarra, yBarra, anchoBarra, altoBarra);

        int escudoRelleno = (int) (porcentajeEscudo * anchoBarra);
        g.setColor(new java.awt.Color(0, 150, 255)); 
        g.fillRect(xBarra, yBarra, escudoRelleno, altoBarra);
        
        g.setColor(java.awt.Color.WHITE); 
        g.drawRect(xBarra, yBarra, anchoBarra, altoBarra);
    }

    private void cargarAnimaciones() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.FINAL_BOSS_SPRITE);
        animaciones = new BufferedImage[7][8];
        for (int i = 0; i < 5; i++)
            animaciones[INACTIVO][i] = img.getSubimage(i * 100, 0, 100, 100);
        for (int i = 0; i < 8; i++)
            animaciones[CORRER][i] = img.getSubimage(i * 100, 100, 100, 100);
        for (int i = 0; i < 7; i++)
            animaciones[ATACAR][i] = img.getSubimage(i * 100, 200, 100, 100);
        for (int i = 0; i < 4; i++)
            animaciones[RECIBIR_GOLPE][i] = img.getSubimage(i * 100, 500, 100, 100);
        for (int i = 0; i < 4; i++)
            animaciones[MUERTO][i] = img.getSubimage(i * 100, 600, 100, 100);
    }
}