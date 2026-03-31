package elementos;

import static utils.Constantes.ConstantesEnemigos.*;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import juego.Juego;
import utils.LoadSave;

public class Esqueleto extends Enemigo {

    private BufferedImage[][] animaciones;
    private int aniTick, aniIndex, aniSpeed = 25, deadTimer = 0;
    private float xDrawOffset = 58 * Juego.SCALE;
    private float yDrawOffset = 52 * Juego.SCALE;
    private static final int SKELETON_DRAW_WIDTH = 130;
    private static final int SKELETON_DRAW_HEIGHT = 130;

    public Esqueleto(float x, float y) {
        super(x, y, (int) (22 * Juego.SCALE), (int) (19 * Juego.SCALE));
        cargarAnimaciones();
        this.attackRange = (int) (Juego.TILES_SIZE * 0.5f);
        this.damage = 10;
        this.vidaMaxima = 50;
        this.vidaActual = vidaMaxima;
        initAttackBox();
    }

    public void update(int[][] lvlData, Jugador jugador) {
        super.update(lvlData, jugador);
        updateAttackBox();
        actualizarAnimacion(jugador);
    }

    private void actualizarAnimacion(Jugador jugador) {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;

            if (enemyState == ATACAR) {
                if (aniIndex == 3 && !attackChecked) {
                    checkEnemyHit(jugador);
                }
            }

            if (aniIndex >= GetSpriteAmount(ESQUELETO, enemyState)) {

                if (enemyState == MUERTO) {
        
                    aniIndex = GetSpriteAmount(ESQUELETO, MUERTO) - 1;
                } else {
                    aniIndex = 0;

                    if (enemyState == ATACAR || enemyState == RECIBIR_GOLPE) {
                        enemyState = INACTIVO;
                        attackChecked = false;
                    }
                }
            }
        }
        if (enemyState == MUERTO && aniIndex == GetSpriteAmount(ESQUELETO, MUERTO) - 1) {
            deadTimer++;
            if (deadTimer >= 800) {
                activo = false;
            }
        }
    }

    public void render(Graphics g, int xLvlOffset) {
        int flipX = 0;
        int flipW = 1;

        int drawWidth = (int) (SKELETON_DRAW_WIDTH * Juego.SCALE);
        int drawHeight = (int) (SKELETON_DRAW_HEIGHT * Juego.SCALE);

        if (walkDir == -1) {
            flipX = drawWidth;
            flipW = -1;
        }

        g.drawImage(animaciones[enemyState][aniIndex],
                (int) (hitbox.x - xDrawOffset) - xLvlOffset + flipX,
                (int) (hitbox.y - yDrawOffset),
                drawWidth * flipW,
                drawHeight, null);

        drawHitbox(g, xLvlOffset);
        drawAttackBox(g, xLvlOffset);
        drawHealthBar(g, xLvlOffset);
    }

    private void cargarAnimaciones() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.SKELETON_SPRITE);
        animaciones = new BufferedImage[7][8];

        for (int j = 0; j < animaciones.length; j++) {
            for (int i = 0; i < animaciones[j].length; i++) {
                animaciones[j][i] = img.getSubimage(i * 100, j * 100, 100, 100);
            }
        }
    }
}