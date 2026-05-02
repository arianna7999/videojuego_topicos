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

    private int tipoJefe;
    private float xDrawOffset;
    private float yDrawOffset;
    private int drawWidth;
    private int drawHeight;

    private boolean escudoActivo = true;
    private int escudoMaximo = 150;
    private int escudoActual = escudoMaximo;
    private int escudoRegenTimer = 0;
    private int tiempoParaRegenerar = 800;

    public JefeFinal(float x, float y, int levelIndex) {
        super(x, y, (int) (44 * Juego.SCALE), (int) (56 * Juego.SCALE));
        configurarJefe(levelIndex);
        initAttackBox(60, 60, 20);
        cargarAnimaciones(levelIndex);
    }

    private void configurarJefe(int levelIndex) {
        switch (levelIndex) {
            case 0:
                tipoJefe = JEFE_NIVEL_1;
                this.vidaMaxima = 350;
                this.damage = 25;
                this.attackRange = (int) (Juego.TILES_SIZE * 2.0f);
                drawWidth = (int) (280 * Juego.SCALE);
                drawHeight = (int) (280 * Juego.SCALE);
                xDrawOffset = 110 * Juego.SCALE;
                yDrawOffset = 100 * Juego.SCALE;
                initHitbox(hitbox.x, hitbox.y, 44 * Juego.SCALE, 56 * Juego.SCALE);
            case 1:
            case 3:
                tipoJefe = JEFE_NIVEL_4;
                this.vidaMaxima = 400;
                this.damage = 30;
                this.attackRange = (int) (Juego.TILES_SIZE * 2.0f);
                drawWidth = (int) (280 * Juego.SCALE);
                drawHeight = (int) (280 * Juego.SCALE);
                xDrawOffset = 110 * Juego.SCALE;
                yDrawOffset = 100 * Juego.SCALE;
                initHitbox(hitbox.x, hitbox.y, 44 * Juego.SCALE, 56 * Juego.SCALE);
                break;
            case 2:
                tipoJefe = JEFE_NIVEL_3;
                this.vidaMaxima = 400;
                this.damage = 35;
                this.attackRange = (int) (Juego.TILES_SIZE * 2.0f);
                drawWidth = (int) (100 * Juego.SCALE);
                drawHeight = (int) (100 * Juego.SCALE);
                xDrawOffset = 30 * Juego.SCALE;
                yDrawOffset = 30 * Juego.SCALE;
                initHitbox(hitbox.x, hitbox.y, 44 * Juego.SCALE, 56 * Juego.SCALE);
                break;
            default:
                break;
        }
        this.vidaActual = vidaMaxima;
    }

    private void cargarAnimaciones(int levelIndex) {
        BufferedImage img = null;
        animaciones = new BufferedImage[7][12];

        switch (levelIndex) {
            case 0:
                img = LoadSave.GetSpriteAtlas(LoadSave.FINAL_BOSS_SPRITE);
                tipoJefe = JEFE_NIVEL_1;
                cargarCiclo(img, INACTIVO, 0);
                cargarCiclo(img, CORRER, 100);
                cargarCiclo(img, ATACAR, 400);
                cargarCiclo(img, RECIBIR_GOLPE, 600);
                cargarCiclo(img, MUERTO, 700);
                break;
            case 2:
                tipoJefe = JEFE_NIVEL_3;
                img = LoadSave.GetSpriteAtlas(LoadSave.FINAL_BOSS_3);
                for (int i = 0; i < GetSpriteAmount(tipoJefe, INACTIVO); i++) {
                    animaciones[INACTIVO][i] = img.getSubimage(i * 100 + 200, 0, 100, 100);
                }
                for (int i = 0; i < GetSpriteAmount(tipoJefe, CORRER); i++) {
                    animaciones[CORRER][i] = img.getSubimage(i * 100, 100, 100, 100);
                }
                for (int i = 0; i < GetSpriteAmount(tipoJefe, ATACAR); i++) {
                    animaciones[ATACAR][i] = img.getSubimage(i * 100 + 200, 300, 100, 100);
                }
                for (int i = 0; i < GetSpriteAmount(tipoJefe, RECIBIR_GOLPE); i++) {
                    animaciones[RECIBIR_GOLPE][i] = img.getSubimage(i * 100 + 200, 700, 100, 100);
                }
                for (int i = 0; i < GetSpriteAmount(tipoJefe, MUERTO); i++) {
                    animaciones[MUERTO][i] = img.getSubimage(i * 100 + 200, 800, 100, 100);
                }
                break;
            case 3:
                tipoJefe = WEREWOLF;
                img = LoadSave.GetSpriteAtlas(LoadSave.WEREWOLF);
                cargarCiclo(img, INACTIVO, 0);
                cargarCiclo(img, CORRER, 100);
                cargarCiclo(img, ATACAR, 300);
                cargarCiclo(img, RECIBIR_GOLPE, 400);
                cargarCiclo(img, MUERTO, 500);
                break;
            case 1:
                tipoJefe = WEREBEAR;
                img = LoadSave.GetSpriteAtlas(LoadSave.WEREBEAR);
                cargarCiclo(img, INACTIVO, 0);
                cargarCiclo(img, CORRER, 100);
                cargarCiclo(img, ATACAR, 300);
                cargarCiclo(img, RECIBIR_GOLPE, 500);
                cargarCiclo(img, MUERTO, 600);

            default:
                break;
        }
    }

    private void cargarCiclo(BufferedImage img, int estado, int yPos) {
        if (img == null)
            return;
        int maxFrames = GetSpriteAmount(tipoJefe, estado);
        for (int i = 0; i < maxFrames; i++) {
            animaciones[estado][i] = img.getSubimage(i * 100, yPos, 100, 100);
        }
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
                escudoActual += escudoMaximo / 10;
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
            if (aniIndex >= GetSpriteAmount(tipoJefe, enemyState)) {
                if (enemyState == MUERTO) {
                    aniIndex = GetSpriteAmount(tipoJefe, MUERTO) - 1;
                } else {
                    aniIndex = 0;
                    if (enemyState == ATACAR || enemyState == RECIBIR_GOLPE) {
                        enemyState = INACTIVO;
                        attackChecked = false;
                    }
                }
            }
        }
        if (enemyState == MUERTO && aniIndex == GetSpriteAmount(tipoJefe, MUERTO) - 1) {
            deadTimer++;
            if (deadTimer >= 400)
                activo = false;
        }
    }

    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        int flipX = 0;
        int flipW = 1;
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
        drawHealthBar(g, xLvlOffset, yLvlOffset);
    }
}