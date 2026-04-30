package elementos.jugador;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import elementos.enemigos.Cascaron;
import elementos.enemigos.FlechaLluvia;
import elementos.enemigos.FlechaRecta;
import elementos.managers.EnemyManager;
import elementos.managers.ObjectManager;
import elementos.objetos.PlataformaMovil;
import juego.Juego;
import static utils.Constantes.ConstantesJugador.ARCO;
import static utils.Constantes.ConstantesJugador.ATACAR1;
import static utils.Constantes.ConstantesJugador.CAYENDO;
import static utils.Constantes.ConstantesJugador.CORRER;
import static utils.Constantes.ConstantesJugador.ESCALAR;
import static utils.Constantes.ConstantesJugador.INACTIVO;
import static utils.Constantes.ConstantesJugador.MUERTO;
import static utils.Constantes.ConstantesJugador.SALTAR;
import static utils.Constantes.GetNoSprite;
import utils.LoadSave;
import static utils.MetodosAyuda.*;

public class Jugador extends Cascaron {
    private java.awt.geom.Rectangle2D.Float attackBox;
    private boolean onLadder = false;
    private int vidaMaxima = 100;
    private int vidaActual = vidaMaxima;
    private BufferedImage[][] idLeAni;
    private int animInd, animTick = 0, animSpeed = 15;
    private int playerAction = INACTIVO;
    private int playerDirec = -1;
    private utils.AudioPlayer audioPlayer;
    private ArrayList<PlataformaMovil> plataformas;
    private boolean enPlataforma = false;
    private boolean tieneLlave = false;

    // inventario
    private int atomosH = 2;
    private int atomosO = 2;
    private int atomosC = 2;

    private boolean up, down, left, right, jump;

    private int[][] lvlData;

    private BufferedImage healthBarEmpty;
    private BufferedImage healthBarFull;
    private BufferedImage[] heartFrames;

    private int heartAnimTick = 0;
    private int heartAnimInd = 0;
    private int heartAnimSpeed = 20;

    private float xDrawOffset = 87 * Juego.SCALE;
    private float yDrawOffset = 80 * Juego.SCALE;

    private float playerSpeed = 2.0f;
    private float airSpeed = 0f;
    private float gravity = 0.04f * Juego.SCALE;
    private float jumpSpeed = -2.25f * Juego.SCALE;
    private float fallSpeedAfterCollision = 0.5f * Juego.SCALE;

    private boolean inAir = true;
    private boolean isDead = false;
    private boolean moving = false;
    private boolean attacking = false;
    private boolean attackChecked = false;

    // Disparo con arco (Lucerys)
    private boolean shootingArrow = false;
    private boolean arrowChecked = false;

    private int deadTimer = 0;
    private boolean readyToRestart = false;
    private float spawnX, spawnY;

    private boolean inKnockback = false;
    private int knockbackDir = 1;
    private float knockbackSpeed = 1.5f * Juego.SCALE;
    private int invulnerableTimer = 0;

    private int healTimer = 0;
    private int tiempoParaCurar = 200;
    private int cantidadCuraAutomatica = 8;

    public boolean isDead() {
        return isDead;
    }

    private int dañoAtaque = 150;
    private int golpesAcertados = 0;
    private int enemigosDerrotados = 0;

    private String personaje = "Hank";
    private String habilidad = "golpe_brutal";

    // ── Habilidades
    private static final int MAX_USOS_HABILIDAD = 3;
    private int usosHabilidadRestantes = MAX_USOS_HABILIDAD;

    // Hank
    private boolean golpeBrutalActivo = false;

    // Frank
    private boolean corazaActiva = false;
    private int corazaTimer = 0;
    private static final int CORAZA_DURACION = 3000;

    // Saori
    private boolean roboVidaActivo = false;
    private static final int MAX_ROBOS_POR_USO = 4;
    private int robosRestantes = 0;

    // Efectos visuales
    private BufferedImage[] efectoHabilidadFrames = null;
    private int efectoAnimInd = -1;
    private int efectoAnimTick = 0;
    private static final int EFECTO_ANIM_SPEED = 5;
    private static final int EFECTO_COLS = 8;

    // Lucerys ──────────────────────────────────────────────────────
    private java.util.ArrayList<FlechaLluvia> flechasLluvia = new java.util.ArrayList<>();
    private java.util.ArrayList<FlechaRecta> flechasRectas = new java.util.ArrayList<>();
    private static final int NUM_FLECHAS = 8;
    private static final int DELAY_FLECHAS = 8;
    private int flechaSpawnTimer = 0;
    private int flechasSpawneadas = 0;
    private boolean lluviaEnCurso = false;
    private float lluviacentroX, lluviaRadioX, lluviaTargetY;
    private boolean lluviaActivada = false;

    public boolean isLluviaActivada() {
        return lluviaActivada;
    }

    // Colores para la coraza de Frank
    private static final java.awt.Color COLOR_CORAZA_FONDO = new java.awt.Color(50, 120, 255, 80);
    private static final java.awt.Color COLOR_CORAZA_BORDE = new java.awt.Color(100, 180, 255, 180);
    private static final java.awt.BasicStroke STROKE_CORAZA = new java.awt.BasicStroke(3);
    private static final java.awt.BasicStroke STROKE_NORMAL = new java.awt.BasicStroke(1);

    // Colores para indicadores de habilidades
    private static final java.awt.Color COLOR_HAB_BRUTAL = new java.awt.Color(255, 80, 30);
    private static final java.awt.Color COLOR_HAB_CORAZA = new java.awt.Color(80, 160, 255);
    private static final java.awt.Color COLOR_HAB_ROBO = new java.awt.Color(180, 50, 200);
    private static final java.awt.Color COLOR_SOMBRA_TEXTO = new java.awt.Color(0, 0, 0, 180);

    // Colores para los usos de habilidades (los circulitos)
    private static final java.awt.Color COLOR_USO_ACTIVO_FONDO = new java.awt.Color(255, 220, 50, 220);
    private static final java.awt.Color COLOR_USO_ACTIVO_BORDE = new java.awt.Color(180, 140, 0, 255);
    private static final java.awt.Color COLOR_USO_INACTIVO_FONDO = new java.awt.Color(80, 80, 80, 180);
    private static final java.awt.Color COLOR_USO_INACTIVO_BORDE = new java.awt.Color(50, 50, 50, 200);

    // Fuente de la UI
    private java.awt.Font fuenteHabilidad;

    public Jugador(float x, float y, int w, int h, utils.AudioPlayer audioPlayer) {
        super(x, y, w, h);
        this.audioPlayer = audioPlayer;
        this.spawnX = x;
        this.spawnY = y;
        loadAnimation();
        initHitbox(x, y, 28 * Juego.SCALE, 31 * Juego.SCALE);
        attackBox = new java.awt.geom.Rectangle2D.Float(x, y, (int) (20 * Juego.SCALE), (int) (20 * Juego.SCALE));
        fuenteHabilidad = new java.awt.Font("Arial", java.awt.Font.BOLD, (int) (11 * Juego.SCALE));
    }

    public void setPersonaje(Personaje p) {
        this.personaje = p.nombre;
        this.tiempoParaCurar = p.tiempoParaCurar;
        this.cantidadCuraAutomatica = p.cantidadCuraAutomatica;
        this.vidaMaxima = p.vida;
        this.playerSpeed = p.velocidad;
        this.dañoAtaque = p.daño;
        this.jumpSpeed = p.salto * Juego.SCALE;
        this.habilidad = p.habilidad;
        this.vidaActual = vidaMaxima;
        this.xDrawOffset = p.drawOffsetX * Juego.SCALE;
        this.yDrawOffset = p.drawOffsetY * Juego.SCALE;
        cargarSprite(p.sprite, p.spriteColumnas, p.spriteFilas, p.spriteCeldaW, p.spriteCeldaH);
        cargarEfectoHabilidad();
    }

    private void cargarSprite(String ruta, int cols, int filas, int celdaW, int celdaH) {
        BufferedImage img = utils.LoadSave.GetSpriteAtlas(ruta);
        int filasUsar = Math.min(filas, 8);
        int colsUsar = Math.min(cols, 13);
        idLeAni = new BufferedImage[8][13];
        for (int j = 0; j < filasUsar; j++) {
            for (int i = 0; i < colsUsar; i++) {
                idLeAni[j][i] = img.getSubimage(i * celdaW, j * celdaH, celdaW, celdaH);
            }
            for (int i = colsUsar; i < 13; i++) {
                idLeAni[j][i] = idLeAni[j][colsUsar - 1];
            }
        }
        for (int j = filasUsar; j < 8; j++) {
            for (int i = 0; i < 13; i++) {
                idLeAni[j][i] = idLeAni[0][i];
            }
        }
    }

    private void cargarEfectoHabilidad() {
        BufferedImage sheet = utils.LoadSave.GetSpriteAtlas("efecto_habilidad.png");
        if (sheet == null)
            return;
        int fila;
        switch (personaje) {
            case "Hank":
                fila = 0;
                break;
            case "Saori":
                fila = 1;
                break;
            case "Frank":
                fila = 2;
                break;
            case "Lucerys":
                fila = 3;
                break;
            default:
                fila = 0;
                break;
        }
        int celdaSize = 64;
        efectoHabilidadFrames = new BufferedImage[EFECTO_COLS];
        for (int i = 0; i < EFECTO_COLS; i++) {
            efectoHabilidadFrames[i] = sheet.getSubimage(i * celdaSize, fila * celdaSize, celdaSize, celdaSize);
        }
    }

    private void dispararEfectoVisual() {
        efectoAnimInd = 0;
        efectoAnimTick = 0;
    }

    private void updateHabilidades() {
        if (corazaActiva) {
            corazaTimer--;
            if (corazaTimer <= 0) {
                corazaActiva = false;
                corazaTimer = 0;
            }
        }
        if (efectoAnimInd >= 0 && efectoHabilidadFrames != null) {
            efectoAnimTick++;
            if (efectoAnimTick >= EFECTO_ANIM_SPEED) {
                efectoAnimTick = 0;
                efectoAnimInd++;
                if (efectoAnimInd >= EFECTO_COLS) {
                    efectoAnimInd = -1;
                }
            }
        }
    }

    public void usarHabilidad() {
        if (habilidad == null)
            return;
        if (usosHabilidadRestantes <= 0)
            return;
        usosHabilidadRestantes--;
        dispararEfectoVisual();
        switch (habilidad) {
            case "golpe_brutal":
                golpeBrutalActivo = true;
                break;
            case "coraza":
                corazaActiva = true;
                corazaTimer = CORAZA_DURACION;
                break;
            case "robo_vida":
                roboVidaActivo = true;
                robosRestantes = MAX_ROBOS_POR_USO;
                break;
            case "lluvia":
                lluviaActivada = true;
                break;
        }
    }

    public void ejecutarLluvia(EnemyManager enemyMan) {
        if (!lluviaActivada)
            return;
        lluviaActivada = false;
        lluviaEnCurso = true;
        flechasSpawneadas = 0;
        flechaSpawnTimer = 0;
        lluviacentroX = hitbox.x + hitbox.width / 2;
        lluviaRadioX = 220 * Juego.SCALE;
        lluviaTargetY = Juego.GAME_HEIGHT - 10 * Juego.SCALE;
    }

    private void updateLluvia(EnemyManager enemyMan) {
        if (!lluviaEnCurso && flechasLluvia.isEmpty())
            return;

        if (lluviaEnCurso && flechasSpawneadas < NUM_FLECHAS) {
            flechaSpawnTimer++;
            if (flechaSpawnTimer >= DELAY_FLECHAS) {
                flechaSpawnTimer = 0;
                float rx = (float) (Math.random() * 2 - 1) * lluviaRadioX;
                float startY = -60 * Juego.SCALE;
                flechasLluvia.add(new FlechaLluvia(
                        lluviacentroX + rx, startY, lluviaTargetY, dañoAtaque));
                flechasSpawneadas++;
            }
            if (flechasSpawneadas >= NUM_FLECHAS)
                lluviaEnCurso = false;
        }

        java.util.Iterator<FlechaLluvia> it = flechasLluvia.iterator();
        while (it.hasNext()) {
            FlechaLluvia f = it.next();
            f.update();
            if (f.isAplicaDaño()) {
                enemyMan.checkEnemyHitArea(f.getHitbox(), this);
                f.consumirDaño();
            }
            if (f.isMuerta())
                it.remove();
        }
    }

    private void updateFlechasRectas(EnemyManager enemyMan, int levelIndex) {
        java.util.Iterator<FlechaRecta> it = flechasRectas.iterator();
        while (it.hasNext()) {
            FlechaRecta f = it.next();
            f.update(lvlData, levelIndex);
            if (!f.isMuerta()) {
                boolean golpeo = enemyMan.checkEnemyHitAreaFlecha(f.getHitbox(), this, f.getDaño());
                if (golpeo)
                    f.matar();
            }
            if (f.isMuerta())
                it.remove();
        }
    }

    public String getPersonaje() {
        return personaje;
    }

    public int getDañoActual() {
        if (golpeBrutalActivo) {
            golpeBrutalActivo = false;
            return dañoAtaque * 3;
        }
        return dañoAtaque;
    }

    public void procesarRoboVida(int dañoHecho) {
        if (roboVidaActivo && robosRestantes > 0) {
            curarVida(dañoHecho);
            robosRestantes--;
            if (robosRestantes <= 0) {
                roboVidaActivo = false;
            }
        }
    }

    public int getUsosHabilidadRestantes() {
        return usosHabilidadRestantes;
    }

    public int getMaxUsosHabilidad() {
        return MAX_USOS_HABILIDAD;
    }

    public boolean isRoboVidaActivo() {
        return roboVidaActivo;
    }

    public boolean isCorazaActiva() {
        return corazaActiva;
    }

    public void update(EnemyManager enemyMan, ObjectManager objectMan, int levelIndex) {
        if (invulnerableTimer > 0)
            invulnerableTimer--;
        onLadder = utils.MetodosAyuda.IsEntityOnLadder(hitbox, lvlData);
        updateAttackBox();
        actualizarAnim(enemyMan, objectMan);
        colocarAnim();
        ActuPosicion(levelIndex);
        revisarPicos(objectMan);
        revisarBloqueDanoM3(levelIndex);
        autoCurar();
        revisarCaidaVacio();
        revisarAgua(objectMan);
        updateHabilidades();
        if (lluviaActivada)
            ejecutarLluvia(enemyMan);
        updateLluvia(enemyMan);
        updateFlechasRectas(enemyMan, levelIndex);
    }

    public void loadLvlData(int[][] getLevelData, int levelIndex) {
        this.lvlData = getLevelData;

        if (!utils.MetodosAyuda.IsEntityOnFloor(hitbox, lvlData, levelIndex) && !enPlataforma) {
            inAir = true;
        }
    }

    private void colocarAnim() {
        if (isDead) {
            playerAction = MUERTO;
            return;
        }
        int startAnim = playerAction;
        if (moving)
            playerAction = CORRER;
        else
            playerAction = INACTIVO;
        if (inAir) {
            if (airSpeed < 0)
                playerAction = SALTAR;
            else
                playerAction = CAYENDO;
        }
        if (onLadder && (up || down)) {
            playerAction = ESCALAR;
        }
        if (attacking) {

            if ("Lucerys".equals(personaje)) {
                playerAction = ARCO;
            } else {
                playerAction = ATACAR1;
            }
        }
        if (startAnim != playerAction)
            resetAnimTick();
    }

    private void resetAnimTick() {
        this.animTick = 0;
        animInd = 0;
    }

    private void updateAttackBox() {
        if (right || playerDirec == 1) {
            attackBox.x = hitbox.x + hitbox.width + (int) (Juego.SCALE * 2);
        } else if (left || playerDirec == -1) {
            attackBox.x = hitbox.x - attackBox.width - (int) (Juego.SCALE * 2);
        }
        attackBox.y = hitbox.y + (Juego.SCALE * 10);
    }

    private void actualizarAnim(EnemyManager enemyMan, ObjectManager objectMan) {
        animTick++;
        if (animTick >= animSpeed) {
            animTick = 0;
            animInd++;

            if (playerAction == MUERTO) {
                if (animInd >= GetNoSprite(MUERTO) - 1) {
                    animInd = GetNoSprite(MUERTO) - 1;
                    deadTimer++;
                    if (deadTimer >= 40)
                        readyToRestart = true;
                }
                return;
            }

            if (playerAction == ATACAR1 && animInd == 1 && !attackChecked) {
                audioPlayer.reproducirEfecto("sonido-golpe.wav");
                enemyMan.checkEnemyHit(attackBox, this);
                objectMan.checkObjectHit(attackBox, this);
                attackChecked = true;
            }

            if (playerAction == ARCO && animInd == 8 && !arrowChecked) {
                audioPlayer.reproducirEfecto("sonido-golpe.wav");
                float flechaX = (playerDirec == 1)
                        ? hitbox.x + hitbox.width + 5 * Juego.SCALE
                        : hitbox.x - 5 * Juego.SCALE;
                float flechaY = hitbox.y + hitbox.height * 0.4f;
                flechasRectas.add(new FlechaRecta(flechaX, flechaY, playerDirec, dañoAtaque));
                arrowChecked = true;
            }

            if (animInd >= GetNoSprite(playerAction)) {
                animInd = 0;
                attacking = false;
                attackChecked = false;
                arrowChecked = false;
            }
        }
    }

    public int getPlayerDirec() {
        return playerDirec;
    }

    public void setPlayerDirec(int playerDirec) {
        this.playerDirec = playerDirec;
    }

    public boolean isMoving() {
        return moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public boolean isAttacking() {
        return attacking;
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

    public boolean isShootingArrow() {
        return shootingArrow;
    }

    public void setShootingArrow(boolean shootingArrow) {
        this.shootingArrow = shootingArrow;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public void ActuPosicion(int levelIndex) {
        if (isDead)
            return;
        moving = false;
        if (jump)
            jump();
        float xSpeed = 0;
        float ySpeed = 0;

        if (inKnockback) {
            xSpeed = knockbackDir * knockbackSpeed;
            if (!inAir && airSpeed >= 0)
                inKnockback = false;
        } else {
            if (left) {
                xSpeed -= playerSpeed;
                playerDirec = -1;
            }
            if (right) {
                xSpeed += playerSpeed;
                playerDirec = 1;
            }
        }

        if (onLadder) {
            inAir = false;
            airSpeed = 0;
            if (up) {
                ySpeed = -playerSpeed;
                moving = true;
            } else if (down) {
                ySpeed = playerSpeed;
                moving = true;
            }
            if (ySpeed != 0) {
                if (CanMoveHere(hitbox.x, hitbox.y + ySpeed, (int) hitbox.width, (int) hitbox.height, lvlData,
                        levelIndex))
                    hitbox.y += ySpeed;
            }
            updateXPos(xSpeed, levelIndex);
            if (xSpeed != 0 || ySpeed != 0)
                moving = true;
            return;
        }

        if (!left && !right && !inAir && !inKnockback)
            return;

        if (!inAir && !IsEntityOnFloor(hitbox, lvlData, levelIndex) && !enPlataforma)
            inAir = true;

        if (inAir) {
            if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, (int) hitbox.width, (int) hitbox.height, lvlData,
                    levelIndex)) {
                hitbox.y += airSpeed;
                airSpeed += gravity;
                updateXPos(xSpeed, levelIndex);
            } else {
                hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
                if (airSpeed > 0)
                    resetInAir();
                else
                    airSpeed = fallSpeedAfterCollision;
                updateXPos(xSpeed, levelIndex);
            }
        } else {
            updateXPos(xSpeed, levelIndex);
        }

        if (xSpeed != 0)
            moving = true;
    }

    private void updateXPos(float xSpeed, int levelIndex) {
        if (isDead)
            return;
        if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, (int) hitbox.width, (int) hitbox.height, lvlData, levelIndex))
            hitbox.x += xSpeed;
        else
            hitbox.x = GetEntityXPosNextToWall(hitbox, xSpeed);
    }

    private void resetInAir() {
        inAir = false;
        airSpeed = 0;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    private void jump() {
        if (inAir)
            return;
        inAir = true;
        airSpeed = jumpSpeed;
    }

    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        int flipX = 0;
        int flipW = 1;
        if (playerDirec == -1) {
            flipX = w;
            flipW = -1;
        }

        g.drawImage(idLeAni[playerAction][animInd],
                (int) (hitbox.x - xDrawOffset) - xLvlOffset + flipX,
                (int) (hitbox.y - yDrawOffset) - yLvlOffset,
                w * flipW, h, null);

        if (corazaActiva) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
            int cx = (int) (hitbox.x + hitbox.width / 2) - xLvlOffset;
            int cy = (int) (hitbox.y + hitbox.height / 2) - yLvlOffset;
            int radio = (int) (30 * Juego.SCALE);

            g2.setColor(COLOR_CORAZA_FONDO);
            g2.fillOval(cx - radio, cy - radio, radio * 2, radio * 2);

            g2.setColor(COLOR_CORAZA_BORDE);
            g2.setStroke(STROKE_CORAZA);
            g2.drawOval(cx - radio, cy - radio, radio * 2, radio * 2);
            g2.setStroke(STROKE_NORMAL);
        }

        if (efectoAnimInd >= 0 && efectoHabilidadFrames != null) {
            int efSize = (int) (96 * Juego.SCALE);
            int ex = (int) (hitbox.x + hitbox.width / 2 - efSize / 2) - xLvlOffset;
            int ey = (int) (hitbox.y + hitbox.height / 2 - efSize / 2) - yLvlOffset;
            g.drawImage(efectoHabilidadFrames[efectoAnimInd], ex, ey, efSize, efSize, null);
        }

        for (FlechaLluvia f : flechasLluvia) {
            f.render(g, xLvlOffset, yLvlOffset);
        }
        for (FlechaRecta f : flechasRectas) {
            f.render(g, xLvlOffset, yLvlOffset);
        }

        drawHitbox(g, xLvlOffset, yLvlOffset);
        drawAttackBox(g, xLvlOffset, yLvlOffset);
    }

    private void drawAttackBox(Graphics g, int xLvlOffset, int yLvlOffset) {
        g.drawRect((int) attackBox.x - xLvlOffset, (int) attackBox.y - yLvlOffset,
                (int) attackBox.width, (int) attackBox.height);
    }

    private void loadAnimation() {
        healthBarEmpty = LoadSave.GetSpriteAtlas(LoadSave.HEALTH_BAR_EMPTY);
        healthBarFull = LoadSave.GetSpriteAtlas(LoadSave.HEALTH_BAR_FULL);
        BufferedImage heartSheet = LoadSave.GetSpriteAtlas(LoadSave.HEART_SPRITESHEET);
        heartFrames = new BufferedImage[2];
        for (int i = 0; i < 2; i++)
            heartFrames[i] = heartSheet.getSubimage(i * 90, 0, 90, 28);

        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
        idLeAni = new BufferedImage[8][13];
        for (int j = 0; j < idLeAni.length; j++)
            for (int i = 0; i < 9; i++)
                idLeAni[j][i] = img.getSubimage(i * 100, j * 100, 100, 100);

        cargarEfectoHabilidad();
    }

    public void resetDirBoolean() {
        left = right = up = down = false;
    }

    public void recibirDaño(int cantidad, int dirEnemigo) {
        recibirDaño(cantidad, dirEnemigo, true);
    }

    public void recibirDaño(int cantidad, int dirEnemigo, boolean hacerRuido) {
        if (isDead || invulnerableTimer > 0)
            return;

        if (corazaActiva)
            cantidad = Math.max(1, cantidad / 2);

        if (hacerRuido && audioPlayer != null)
            audioPlayer.reproducirEfecto("sonido-dano.wav");

        vidaActual -= cantidad;
        invulnerableTimer = 80;
        healTimer = -600;

        if (vidaActual <= 0) {
            vidaActual = 0;
            setDead(true);
        } else {
            inKnockback = true;
            knockbackDir = dirEnemigo;
            inAir = true;
            airSpeed = -1.0f * Juego.SCALE;
        }
    }

    public void curarVida(int cantidad) {
        vidaActual += cantidad;
        if (vidaActual > vidaMaxima)
            vidaActual = vidaMaxima;
    }

    private void revisarPicos(ObjectManager objectMan) {
        if (objectMan.checkDañoPorPicos(this)) {
            if (!isDead && invulnerableTimer == 0) {
                if (audioPlayer != null) {
                    audioPlayer.reproducirEfecto("sonido-dano.wav");
                }
                recibirDaño(20, -playerDirec);
            }
        }
    }

    private void revisarCaidaVacio() {
        int limiteInferiorMapa = lvlData.length * Juego.TILES_SIZE;
        if (hitbox.y > limiteInferiorMapa) {
            vidaActual = 0;
            setDead(true);
        }
    }

    public void setDead(boolean isDead) {
        this.isDead = isDead;
    }

    public void drawUI(Graphics g) {
        int heartW = (int) (90 * 3 * Juego.SCALE);
        int heartH = (int) (28 * 3 * Juego.SCALE);
        int xHeart = 10, yHeart = 10;

        int barStartX = xHeart + (int) (27.0 / 90.0 * heartW);
        int barW = (int) (60.0 / 90.0 * heartW);
        int barStartY = yHeart + (int) (7.0 / 28.0 * heartH);
        int barH = (int) (18.0 / 28.0 * heartH);

        float porcentajeVida = (float) vidaActual / vidaMaxima;
        int anchoVidaActual = (int) (porcentajeVida * barW);

        g.drawImage(healthBarEmpty, xHeart, yHeart, heartW, heartH, null);

        java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
        java.awt.Shape oldClip = g2d.getClip();
        g2d.setClip(barStartX, barStartY, anchoVidaActual, barH);
        g2d.drawImage(healthBarFull, xHeart, yHeart, heartW, heartH, null);
        g2d.setClip(oldClip);

        if (heartFrames != null && heartFrames.length > 0) {
            heartAnimTick++;
            if (heartAnimTick >= heartAnimSpeed) {
                heartAnimTick = 0;
                heartAnimInd = (heartAnimInd + 1) % heartFrames.length;
            }
            g.drawImage(heartFrames[heartAnimInd], xHeart, yHeart, heartW, heartH, null);
        }

        dibujarIndicadorHabilidad(g2d, xHeart, yHeart + heartH + (int) (4 * Juego.SCALE));
        dibujarUsosHabilidad(g2d, xHeart, yHeart + heartH + (int) (22 * Juego.SCALE));
        drawInventario(g2d, xHeart, yHeart, heartH);

    }
    private void drawInventario(java.awt.Graphics2D g2d, int xHeart, int yHeart, int heartH) {
        g2d.setColor(java.awt.Color.WHITE);
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, (int)(12 * Juego.SCALE)));
        g2d.drawString("MATERIA -  H: " + atomosH + "  |  O: " + atomosO + "  |  C: " + atomosC, xHeart, yHeart + heartH + (int)(45 * Juego.SCALE));
    }

    private void dibujarIndicadorHabilidad(java.awt.Graphics2D g2d, int x, int y) {
        String textoHab = null;
        java.awt.Color colorHab = java.awt.Color.WHITE;

        if (golpeBrutalActivo) {
            textoHab = "GOLPE BRUTAL";
            colorHab = COLOR_HAB_BRUTAL;
        } else if (corazaActiva) {
            int segs = (int) Math.ceil(corazaTimer / 60.0);
            textoHab = "CORAZA (" + segs + "s)";
            colorHab = COLOR_HAB_CORAZA;
        } else if (roboVidaActivo) {
            textoHab = "ROBO DE VIDA (" + robosRestantes + ")";
            colorHab = COLOR_HAB_ROBO;
        }

        if (textoHab != null) {
            g2d.setFont(fuenteHabilidad);
            g2d.setColor(COLOR_SOMBRA_TEXTO);
            g2d.drawString(textoHab, x + 2, y + (int) (13 * Juego.SCALE) + 2);
            g2d.setColor(colorHab);
            g2d.drawString(textoHab, x, y + (int) (13 * Juego.SCALE));
        }
    }

    private void dibujarUsosHabilidad(java.awt.Graphics2D g2d, int x, int y) {
        int tamCirculo = (int) (8 * Juego.SCALE);
        int gap = (int) (4 * Juego.SCALE);

        for (int i = 0; i < MAX_USOS_HABILIDAD; i++) {
            int cx = x + i * (tamCirculo + gap);
            if (i < usosHabilidadRestantes) {
                g2d.setColor(COLOR_USO_ACTIVO_FONDO);
                g2d.fillOval(cx, y, tamCirculo, tamCirculo);
                g2d.setColor(COLOR_USO_ACTIVO_BORDE);
                g2d.drawOval(cx, y, tamCirculo, tamCirculo);
            } else {
                g2d.setColor(COLOR_USO_INACTIVO_FONDO);
                g2d.fillOval(cx, y, tamCirculo, tamCirculo);
                g2d.setColor(COLOR_USO_INACTIVO_BORDE);
                g2d.drawOval(cx, y, tamCirculo, tamCirculo);
            }
        }
    }

    public int getDañoAtaque() {
        return dañoAtaque;
    }

    public void registrarGolpe() {
        golpesAcertados++;
    }

    public void registrarMuerte() {
        enemigosDerrotados++;
        calcularMejoraDaño();
    }

    private void calcularMejoraDaño() {
        float proporcion = (float) golpesAcertados / enemigosDerrotados;
        if (proporcion <= 4.5f)
            dañoAtaque += 3;
        else
            dañoAtaque += 1;
    }

    public boolean isReadyToRestart() {
        return readyToRestart;
    }

    public void resetAll(int levelIndex) {
        resetDirBoolean();
        inAir = false;
        isDead = false;
        attacking = false;
        shootingArrow = false;
        arrowChecked = false;
        moving = false;
        inKnockback = false;
        playerAction = INACTIVO;
        vidaActual = vidaMaxima;

        golpeBrutalActivo = false;
        corazaActiva = false;
        corazaTimer = 0;
        roboVidaActivo = false;
        robosRestantes = 0;
        lluviaActivada = false;
        lluviaEnCurso = false;
        flechasLluvia.clear();
        flechasRectas.clear();
        flechasSpawneadas = 0;
        efectoAnimInd = -1;
        efectoAnimTick = 0;
        usosHabilidadRestantes = MAX_USOS_HABILIDAD;

        hitbox.x = spawnX;
        hitbox.y = spawnY;
        deadTimer = 0;
        readyToRestart = false;

        if (!utils.MetodosAyuda.IsEntityOnFloor(hitbox, lvlData, levelIndex))
            inAir = true;
    }

    private void autoCurar() {
        if (isDead || vidaActual >= vidaMaxima) {
            healTimer = 0;
            return;
        }
        healTimer++;
        if (healTimer >= tiempoParaCurar && playerAction == INACTIVO) {
            curarVida(cantidadCuraAutomatica);
            healTimer = 0;
        }
    }

    public void setEnPlataforma(boolean b) {
        this.enPlataforma = b;
        if (b)
            inAir = false;
    }

    public void recogerLlave() {
        this.tieneLlave = true;
    }

    public boolean getTieneLlave() {
        return tieneLlave;
    }

    public float getAirSpeed() {
        return airSpeed;
    }

   private void revisarAgua(ObjectManager objectMan) {
        if (objectMan.checkMuertePorAgua(hitbox)) {
            if (!isDead) {
                if (audioPlayer != null)
                    audioPlayer.reproducirEfecto("sonido-dano.wav"); // Puedes cambiar este sonido por uno de caída si tienes
                
                vidaActual = 0; // Matamos al jugador lógicamente
                
                // --- INICIA EFECTO DE CAÍDA AL VACÍO ---
                inKnockback = true; 
                knockbackDir = 0; // 0 para que no se mueva a los lados
                inAir = true; // Forzamos que esté "en el aire"
                airSpeed = 3.0f * Juego.SCALE; // Lo empujamos rápidamente hacia ABAJO
                // ----------------------------------------
            }
        }
    }
    private void revisarBloqueDanoM3(int levelIndex) {
        if (levelIndex == 2) { // Recuerda: levelIndex 2 es el Mundo 3
            int leftCol = (int) (hitbox.x - 3) / juego.Juego.TILES_SIZE;
            int rightCol = (int) (hitbox.x + hitbox.width + 3) / juego.Juego.TILES_SIZE;
            int topRow = (int) (hitbox.y - 3) / juego.Juego.TILES_SIZE;
            int bottomRow = (int) (hitbox.y + hitbox.height + 5) / juego.Juego.TILES_SIZE;

            for (int c = leftCol; c <= rightCol; c++) {
                for (int r = topRow; r <= bottomRow; r++) {
                    if (c >= 0 && c < lvlData[0].length && r >= 0 && r < lvlData.length) {
                        
                        int bloquePisado = lvlData[r][c];
                        
                        // --- ESTA LÍNEA IMPRIMIRÁ EN LA CONSOLA QUÉ BLOQUE ESTÁS TOCANDO ---
                        // Si pisas el nopal y en la consola NO sale "Bloque actual: 46", 
                        // entonces el problema está en cómo el LevelManager lee la imagen.
                        System.out.println("Bloque tocado: " + bloquePisado); 

                        if (bloquePisado == 42 || bloquePisado == 43 || bloquePisado == 44 || bloquePisado == 45 || bloquePisado == 46) {
                            
                            System.out.println("¡TRAMPA DETECTADA! isDead: " + isDead + ", InvTimer: " + invulnerableTimer);

                            if (!isDead && invulnerableTimer == 0) {
                                if (audioPlayer != null) {
                                    audioPlayer.reproducirEfecto("sonido-dano.wav");
                                }
                                recibirDaño(20, -playerDirec);
                                return; 
                            }
                        }
                    }
                }
            }
        }
    }
    public void vaciarInventario() {
        atomosH = 0;
        atomosO = 0;
        atomosC = 0;
    }
    public void setSpawn(int levelIndex) {
        switch (levelIndex) {
            case 3:
                spawnX = 100;
                spawnY = 1500;
                break;
            default:
                spawnX = 100;
                spawnY = 200;
                break;
        }
        
        if (hitbox != null) {
            hitbox.x = spawnX;
            hitbox.y = spawnY;
        }
    }
    public void recogerAtomoH() { atomosH++; }
    public void recogerAtomoO() { atomosO++; }
    public void recogerAtomoC() { atomosC++; }

    public int getAtomosH() { return atomosH; }
    public int getAtomosO() { return atomosO; }
    public int getAtomosC() { return atomosC; }

    public boolean sintetizarAgua() { // H2O
        if (atomosH >= 2 && atomosO >= 1) {
            atomosH -= 2; atomosO -= 1;
            return true;
        }
        return false;
    }

    public boolean sintetizarDioxidoCarbono() { // CO2
        if (atomosC >= 1 && atomosO >= 2) {
            atomosC -= 1; atomosO -= 2;
            return true;
        }
        return false;
    }

    public boolean sintetizarMetano() { // CH4
        if (atomosC >= 1 && atomosH >= 4) {
            atomosC -= 1; atomosH -= 4;
            return true;
        }
        return false;
    }
}