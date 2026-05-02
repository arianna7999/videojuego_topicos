package elementos.enemigos;

import static utils.Constantes.ConstantesEnemigos.*;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import elementos.jugador.Jugador;
import juego.Juego;
import utils.LoadSave;
import utils.Constantes.ConstantesEnemigos;

public class Enemy3 extends Enemigo {

    private BufferedImage[][] animaciones;
    private int aniTick, aniIndex, aniSpeed = 25;
    private int deadTimer = 0;

    private float xDrawOffset = 30 * Juego.SCALE;
    private float yDrawOffset = 65 * Juego.SCALE;
    private static final int ENM_DRAW_WIDTH = 85;
    private static final int ENM_DRAW_HEIGHT = 85;
    private int tipoEnemigo;

    public Enemy3(float x, float y, int levelNumber) {
        super(x, y, (int) (24 * Juego.SCALE), (int) (22 * Juego.SCALE));
        this.vidaMaxima = 80;
        this.vidaActual = vidaMaxima;
        this.attackRange = (int) (Juego.TILES_SIZE * 0.8f);
        this.damage = 20;

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

                int attackFrame = (tipoEnemigo == PUMPKIN) ? 5 : 4;
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
                        enemyState = INACTIVO;
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

        if (walkDir == -1) {
            flipX = drawWidth;
            flipW = -1;
        }

        g.drawImage(animaciones[enemyState][aniIndex],
                (int) (hitbox.x - xDrawOffset) - xLvlOffset + flipX,
                (int) (hitbox.y - yDrawOffset) - yLvlOffset,
                drawWidth * flipW,
                drawHeight, null);

        drawHitbox(g, xLvlOffset, yLvlOffset);
        drawAttackBox(g, xLvlOffset, yLvlOffset);
        drawHealthBar(g, xLvlOffset, yLvlOffset);
    }

    private void cargarAnimaciones(int levelNumber) {
        BufferedImage img = null;

        animaciones = new BufferedImage[8][15];

        switch (levelNumber) {
            case 0:
                tipoEnemigo = PUMPKIN;
                img = LoadSave.GetSpriteAtlas(LoadSave.PUMPKIN_SPRITE);
                cargarCiclo(img, INACTIVO, 0);
                cargarCiclo(img, CORRER, 64);
                cargarCiclo(img, ATACAR, 64);
                cargarCiclo(img, MUERTO, 128);
                break;

            case 1:
                tipoEnemigo = WARRIOR;
                img = LoadSave.GetSpriteAtlas(LoadSave.WARRIOR_SPRITE);
                cargarCiclo(img, INACTIVO, 0);
                cargarCiclo(img, CORRER, 64);
                cargarCiclo(img, ATACAR, 128);
                cargarCiclo(img, MUERTO, 192);
                break;

            case 2:
                tipoEnemigo = PLANT;
                img = LoadSave.GetSpriteAtlas(LoadSave.PLANT_SPRITE);
                cargarCiclo(img, INACTIVO, 0);
                cargarCiclo(img, CORRER, 64);
                cargarCiclo(img, ATACAR, 128);
                cargarCiclo(img, MUERTO, 192);
                break;

            case 3:
                tipoEnemigo = BAT;
                img = LoadSave.GetSpriteAtlas(LoadSave.BAT_SPRITE);
                cargarCiclo(img, INACTIVO, 0);
                cargarCiclo(img, CORRER, 0);
                cargarCiclo(img, ATACAR, 64);
                cargarCiclo(img, MUERTO, 128);
                break;

            // case 4: // Polilla/Mosquito
                // tipoEnemigo = MOTH;
                // img = LoadSave.GetSpriteAtlas(LoadSave.MOTH_SPRITE);
                // cargarCiclo(img, ATACAR, 64); // Fila 2
                // cargarCiclo(img, MUERTO, 128); // Fila 3
                // break;
        }

        // for (int i = 0; i < GetSpriteAmount(tipoEnemigo, INACTIVO); i++) {
        //     animaciones[INACTIVO][i] = img.getSubimage(i * 64, 0, 64, 64);
        // }

        // for (int i = 0; i < GetSpriteAmount(tipoEnemigo, CORRER); i++) {
        //     animaciones[CORRER][i] = img.getSubimage(i * 64, 64, 64, 64);
        // }
    }

    private void cargarCiclo(BufferedImage img, int estado, int yPos) {
        for (int i = 0; i < GetSpriteAmount(tipoEnemigo, estado); i++) {
            animaciones[estado][i] = img.getSubimage(i * 64, yPos, 64, 64);
        }
    }
}