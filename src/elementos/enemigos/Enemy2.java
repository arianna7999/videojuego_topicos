package elementos.enemigos;

import static utils.Constantes.ConstantesEnemigos.*;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import elementos.jugador.Jugador;
import juego.Juego;
import utils.Constantes;
import utils.LoadSave;
import utils.Constantes.ConstantesEnemigos;

public class Enemy2 extends Enemigo {

    private BufferedImage[][] animaciones;
    private int aniTick, aniIndex, aniSpeed = 25;
    private int deadTimer = 0;

    private float xDrawOffset = 58 * Juego.SCALE;
    private float yDrawOffset = 55 * Juego.SCALE;
    private static final int ENM_DRAW_WIDTH = 140;
    private static final int ENM_DRAW_HEIGHT = 140;
    private int tipoEnemigo; 


    public Enemy2(float x, float y, int levelNumber) {
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
                if (aniIndex == 4 && !attackChecked) {
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
        animaciones = new BufferedImage[7][12];

        
        switch (levelNumber) {
            case 0:
                tipoEnemigo = ConstantesEnemigos.ORCO;
                img = LoadSave.GetSpriteAtlas(LoadSave.ORC_SPRITE);

                for (int i = 0; i < 6; i++)
                    animaciones[ATACAR][i] = img.getSubimage(i * 100, 200, 100, 100);
                for (int i = 0; i < 4; i++)
                    animaciones[RECIBIR_GOLPE][i] = img.getSubimage(i * 100, 400, 100, 100);
                for (int i = 0; i < 4; i++)
                    animaciones[MUERTO][i] = img.getSubimage(i * 100, 500, 100, 100);
                break;
            case 1:
                tipoEnemigo = ConstantesEnemigos.ORCO_ARMADO;
                img = LoadSave.GetSpriteAtlas(LoadSave.ARMORED_ORC);
                for (int i = 0; i < 7; i++)
                    animaciones[ATACAR][i] = img.getSubimage(i * 100, 400, 100, 100);
                for (int i = 0; i < 4; i++)
                    animaciones[RECIBIR_GOLPE][i] = img.getSubimage(i * 100, 600, 100, 100);
                for (int i = 0; i < 4; i++)
                    animaciones[MUERTO][i] = img.getSubimage(i * 100, 700, 100, 100);
                break;
            case 2:
                img = LoadSave.GetSpriteAtlas(LoadSave.WEREBEAR);
                tipoEnemigo = ConstantesEnemigos.WEREBEAR;
                for (int i = 0; i < 12; i++)
                    animaciones[ATACAR][i] = img.getSubimage(i * 100, 300, 100, 100);
                for (int i = 0; i < 4; i++)
                    animaciones[RECIBIR_GOLPE][i] = img.getSubimage(i * 100, 500, 100, 100);
                for (int i = 0; i < 4; i++)
                    animaciones[MUERTO][i] = img.getSubimage(i * 100, 600, 100, 100);
                break;
            case 3:
                img = LoadSave.GetSpriteAtlas(LoadSave.ELITE_ORC);
                tipoEnemigo = ConstantesEnemigos.ELITE_ORC;
                for (int i = 0; i < 11; i++)
                    animaciones[ATACAR][i] = img.getSubimage(i * 100, 300, 100, 100);
                for (int i = 0; i < 4; i++)
                    animaciones[RECIBIR_GOLPE][i] = img.getSubimage(i * 100, 500, 100, 100);
                for (int i = 0; i < 4; i++)
                    animaciones[MUERTO][i] = img.getSubimage(i * 100, 600, 100, 100);
                break;
            default:
                break;
        }

        for (int i = 0; i < 6; i++)
            animaciones[INACTIVO][i] = img.getSubimage(i * 100, 0, 100, 100);
        for (int i = 0; i < 6; i++)
            animaciones[CORRER][i] = img.getSubimage(i * 100, 100, 100, 100);
        // for (int i = 0; i < 7; i++)
        //     animaciones[ATACAR][i] = img.getSubimage(i * 100, 200, 100, 100);
        // for (int i = 0; i < 4; i++)
        //     animaciones[RECIBIR_GOLPE][i] = img.getSubimage(i * 100, 400, 100, 100);
        // for (int i = 0; i < 4; i++)
        //     animaciones[MUERTO][i] = img.getSubimage(i * 100, 500, 100, 100);
    }
}