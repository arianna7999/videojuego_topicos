package elementos.enemigos;

import static utils.Constantes.ConstantesEnemigos.*;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import elementos.jugador.Jugador;
import juego.Juego;
import utils.LoadSave;

public class Enemy4 extends Enemigo {

    private BufferedImage[][] animaciones;
    private int aniTick, aniIndex, aniSpeed = 25;
    private int deadTimer = 0;

    // Ajuste de visualización para sprites de 100x100
    private float xDrawOffset = 38 * Juego.SCALE;
    private float yDrawOffset = 70 * Juego.SCALE;
    private static final int ENM_DRAW_WIDTH = 100;
    private static final int ENM_DRAW_HEIGHT = 100;
    private int tipoEnemigo;

    public Enemy4(float x, float y, int levelNumber) {

        super(x, y, (int) (25 * Juego.SCALE), (int) (20 * Juego.SCALE));
        this.vidaMaxima = 60;
        this.vidaActual = vidaMaxima;
        this.attackRange = (int) (Juego.TILES_SIZE * 0.7f);
        this.damage = 15;

        initAttackBox();
        cargarAnimaciones(levelNumber);
    }

    public void update(int[][] lvlData, Jugador jugador, int levelIndex) {
        super.update(lvlData, jugador, levelIndex);
        updateAttackBox();
        actualizarAnimacion(jugador);
    }

    private void actualizarAnimacion(Jugador jugador) {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;

            if (enemyState == ATACAR) {
                // La mayoría de tus nuevos enemigos atacan en el frame 3 o 4
                int attackFrame = 3;
                if (aniIndex == attackFrame && !attackChecked) {
                    checkEnemyHit(jugador);
                }
            }

            if (aniIndex >= GetSpriteAmount(tipoEnemigo, enemyState)) {
                if (enemyState == MUERTO) {
                    aniIndex = GetSpriteAmount(tipoEnemigo, MUERTO) - 1;
                } else {
                    aniIndex = 0;
                    if (enemyState == ATACAR || enemyState == RECIBIR_GOLPE) {
                        enemyState = CORRER; // Vuelve a moverse tras atacar
                        attackChecked = false;
                    }
                }
            }
        }

        if (enemyState == MUERTO && aniIndex == GetSpriteAmount(tipoEnemigo, MUERTO) - 1) {
            deadTimer++;
            if (deadTimer >= 400) {
                activo = false;
            }
        }
    }

    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        int flipX = 0;
        int flipW = 1;
        int drawWidth = (int) (ENM_DRAW_WIDTH * Juego.SCALE);
        int drawHeight = (int) (ENM_DRAW_HEIGHT * Juego.SCALE);

        if (walkDir == 1) {
            flipX = drawWidth;
            flipW = -1;
        }

        g.drawImage(animaciones[enemyState][aniIndex],
                (int) (hitbox.x - xDrawOffset) - xLvlOffset + flipX,
                (int) (hitbox.y - yDrawOffset) - yLvlOffset,
                drawWidth * flipW,
                drawHeight, null);

        // Descomentar para ver las áreas de colisión durante pruebas
        drawHitbox(g, xLvlOffset, yLvlOffset);
        drawAttackBox(g, xLvlOffset, yLvlOffset);
    }

    private void cargarAnimaciones(int levelNumber) {
        BufferedImage img = null;
        animaciones = new BufferedImage[8][15];

        switch (levelNumber) {

            case 0:
            case 2:
                tipoEnemigo = RAT;
                img = LoadSave.GetSpriteAtlas(LoadSave.RAT_SPRITE);
                cargarCiclo(img,INACTIVO, 0);
                cargarCiclo(img,CORRER, 64);
                cargarCiclo(img, ATACAR, 128);
                cargarCiclo(img, MUERTO, 192);
                break;
            case 1:
                tipoEnemigo = MUSHROOM;
                img = LoadSave.GetSpriteAtlas(LoadSave.MUSHROOM_SPRITE);
                cargarCiclo(img, INACTIVO, 0);
                cargarCiclo(img, CORRER, 64);
                cargarCiclo(img, ATACAR, 128);
                cargarCiclo(img, MUERTO, 192);
                break;
            case 3:
                tipoEnemigo = MOTH;
                img = LoadSave.GetSpriteAtlas(LoadSave.MOTH_SPRITE);
                cargarCiclo(img, INACTIVO, 0);
                cargarCiclo(img, CORRER, 0);
                cargarCiclo(img, ATACAR, 0);
                cargarCiclo(img, MUERTO, 64);
                break;
            default:
                break;
        }
    }

    private void cargarCiclo(BufferedImage img, int estado, int yPos) {
        int frames = GetSpriteAmount(tipoEnemigo, estado);
        for (int i = 0; i < frames; i++) {
            animaciones[estado][i] = img.getSubimage(i * 64, yPos, 64, 64);
        }
    }
}