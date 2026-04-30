package elementos.enemigos;

import static utils.Constantes.ConstantesEnemigos.*;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import elementos.jugador.Jugador;
import juego.Juego;
import utils.Constantes.ConstantesEnemigos;
import utils.Constantes;
import utils.LoadSave;

public class Enemy1 extends Enemigo {

    private BufferedImage[][] animaciones;
    private int aniTick, aniIndex, aniSpeed = 25, deadTimer = 0;
    private float xDrawOffset = 58 * Juego.SCALE;
    private float yDrawOffset = 52 * Juego.SCALE;
    private static final int SKELETON_DRAW_WIDTH = 130;
    private static final int SKELETON_DRAW_HEIGHT = 130;
    private int tipoEnemigo; 

    public Enemy1(float x, float y, int levelNumber) {
        super(x, y, (int) (22 * Juego.SCALE), (int) (19 * Juego.SCALE));
        cargarAnimaciones(levelNumber);
        this.attackRange = (int) (Juego.TILES_SIZE * 0.5f);
        this.damage = 10;
        this.vidaMaxima = 50;
        this.vidaActual = vidaMaxima;
        initAttackBox();
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
                if (aniIndex == 3 && !attackChecked) {
                    checkEnemyHit(jugador);
                }
            }

            // 2. CORRECCIÓN: Usamos la variable dinámica 'tipoEnemigo' en lugar de 'ESQUELETO'
            if (aniIndex >= GetSpriteAmount(tipoEnemigo, enemyState)) {

                if (enemyState == MUERTO) {
                    // Mantenemos el último frame de muerte
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
        
        // 3. CORRECCIÓN: Usamos 'tipoEnemigo' aquí también
        if (enemyState == MUERTO && aniIndex == GetSpriteAmount(tipoEnemigo, MUERTO) - 1) {
            deadTimer++;
            if (deadTimer >= 800) {
                activo = false;
            }
        }
    }

    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
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
                (int) (hitbox.y - yDrawOffset) - yLvlOffset, 
                drawWidth * flipW,
                drawHeight, null);

        drawHitbox(g, xLvlOffset, yLvlOffset);
        drawAttackBox(g, xLvlOffset, yLvlOffset);
        drawHealthBar(g, xLvlOffset, yLvlOffset);
    }

    // private void cargarAnimaciones(int levelNumber) {
    //     BufferedImage img = null;
    //     switch (levelNumber) {
    //         case 0:
    //             img = LoadSave.GetSpriteAtlas(LoadSave.SKELETON_SPRITE);
    //             tipoEnemigo = ConstantesEnemigos.ESQUELETO;
    //             break;
    //         case 1:
    //             img = LoadSave.GetSpriteAtlas(LoadSave.SLIME);
    //             tipoEnemigo = ConstantesEnemigos.SLIME;
    //             break;
    //         case 2:
    //             img = LoadSave.GetSpriteAtlas(LoadSave.WEREWOLF);
    //             tipoEnemigo = ConstantesEnemigos.WEREWOLF;
    //             damage = 15;
    //             break;
    //         case 3:
    //             img = LoadSave.GetSpriteAtlas(LoadSave.ARMORED_AXEMAN);
    //             tipoEnemigo = ConstantesEnemigos.ORCO;
    //             damage = 20;
    //             break;
    //         default:
    //             break;
    //     }

    //     animaciones = new BufferedImage[7][12];

    //     for (int j = 0; j < animaciones.length; j++) {
    //         int cantidadFrames = ConstantesEnemigos.GetSpriteAmount(tipoEnemigo, j);
    //         if (cantidadFrames == 0) {
    //             continue;
    //         }

    //         int filaY = j;
    //         if (j == ConstantesEnemigos.RECIBIR_GOLPE) {
    //             filaY = 3; 
    //         } else if (j == ConstantesEnemigos.MUERTO) {
    //             filaY = 4; 
    //         }

    //         for (int i = 0; i < cantidadFrames; i++) {
    //             animaciones[j][i] = img.getSubimage(i * 100, filaY * 100, 100, 100);
    //         }
    //     }
    // }
    private void cargarAnimaciones(int levelNumber) {
        BufferedImage img = null;
        animaciones = new BufferedImage[8][12];

        
        switch (levelNumber) {
            case 0:
                tipoEnemigo = ConstantesEnemigos.ESQUELETO;
                img = LoadSave.GetSpriteAtlas(LoadSave.SKELETON_SPRITE);

                for (int i = 0; i < 7; i++)
                    animaciones[ATACAR][i] = img.getSubimage(i * 100, 300, 100, 100);
                for (int i = 0; i < 4; i++)
                    animaciones[RECIBIR_GOLPE][i] = img.getSubimage(i * 100, 500, 100, 100);
                for (int i = 0; i < 4; i++)
                    animaciones[MUERTO][i] = img.getSubimage(i * 100, 600, 100, 100);
                break;
            case 1:
                tipoEnemigo = ConstantesEnemigos.SLIME;
                img = LoadSave.GetSpriteAtlas(LoadSave.SLIME);
                for (int i = 0; i < 12; i++)
                    animaciones[ATACAR][i] = img.getSubimage(i * 100, 300, 100, 100);
                for (int i = 0; i < 4; i++)
                    animaciones[RECIBIR_GOLPE][i] = img.getSubimage(i * 100, 400, 100, 100);
                for (int i = 0; i < 4; i++)
                    animaciones[MUERTO][i] = img.getSubimage(i * 100, 500, 100, 100);
                break;
            case 2:
                img = LoadSave.GetSpriteAtlas(LoadSave.WEREWOLF);
                tipoEnemigo = ConstantesEnemigos.WEREWOLF;
                for (int i = 0; i < 12; i++)
                    animaciones[ATACAR][i] = img.getSubimage(i * 100, 300, 100, 100);
                for (int i = 0; i < 4; i++)
                    animaciones[RECIBIR_GOLPE][i] = img.getSubimage(i * 100, 400, 100, 100);
                for (int i = 0; i < 4; i++)
                    animaciones[MUERTO][i] = img.getSubimage(i * 100, 500, 100, 100);
                break;
            case 3:
                img = LoadSave.GetSpriteAtlas(LoadSave.ARMORED_AXEMAN);
                tipoEnemigo = ConstantesEnemigos.ARMORED_AXEMAN;
                for (int i = 0; i < 11; i++)
                    animaciones[ATACAR][i] = img.getSubimage(i * 100, 400, 100, 100);
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
    }
}