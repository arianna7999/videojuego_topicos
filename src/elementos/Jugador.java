package elementos;

import static utils.Constantes.GetNoSprite;
import static utils.Constantes.ConstantesJugador.ATACAR1;
import static utils.Constantes.ConstantesJugador.CAYENDO;
import static utils.Constantes.ConstantesJugador.CORRER;
import static utils.Constantes.ConstantesJugador.INACTIVO;
import static utils.Constantes.ConstantesJugador.MUERTO;
import static utils.Constantes.ConstantesJugador.SALTAR;
import static utils.MetodosAyuda.*;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import juego.Juego;
import utils.LoadSave;

public class Jugador extends Cascaron {
    private java.awt.geom.Rectangle2D.Float attackBox;

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

    private int deadTimer = 0;
    private boolean readyToRestart = false;
    private float spawnX, spawnY;

    private boolean inKnockback = false;
    private int knockbackDir = 1;
    private float knockbackSpeed = 1.5f * Juego.SCALE;
    private int invulnerableTimer = 0;

    private int healTimer = 0;
    private int tiempoParaCurar = 400; 
    private int cantidadCuraAutomatica = 4;

    public boolean isDead() {
        return isDead;
    }

    private int dañoAtaque = 1500;
    private int golpesAcertados = 0;
    private int enemigosDerrotados = 0;

    public Jugador(float x, float y, int w, int h, utils.AudioPlayer audioPlayer) {
        super(x, y, w, h);
        this.audioPlayer = audioPlayer;
        this.spawnX = x;
        this.spawnY = y;
        loadAnimation();
        initHitbox(x, y, 28 * Juego.SCALE, 31 * Juego.SCALE);
        attackBox = new java.awt.geom.Rectangle2D.Float(x, y, (int) (20 * Juego.SCALE), (int) (20 * Juego.SCALE));
    }

    public void update(EnemyManager enemyMan, ObjectManager objectMan) {
        if (invulnerableTimer > 0)
            invulnerableTimer--;
        updateAttackBox();
        actualizarAnim(enemyMan, objectMan);
        colocarAnim();
        ActuPosicion();
        autoCurar();
        revisarPicos();
        revisarCaidaVacio();
    }

    public void loadLvlData(int[][] getLevelData) {
        this.lvlData = getLevelData;
        if (!utils.MetodosAyuda.IsEntityOnFloor(hitbox, lvlData) && !enPlataforma) {
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

        if (attacking) {
            playerAction = ATACAR1;
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
                    if (deadTimer >= 40) {
                        readyToRestart = true;
                    }
                }
                return;
            }

            if (playerAction == ATACAR1 && animInd == 1 && !attackChecked) {
                audioPlayer.reproducirEfecto("sonido-golpe.wav");
                enemyMan.checkEnemyHit(attackBox, this);
                objectMan.checkObjectHit(attackBox);
                attackChecked = true;
            }

            if (animInd >= GetNoSprite(playerAction)) {
                animInd = 0;
                attacking = false;
                attackChecked = false;
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
        System.out.println("atacando " + attacking);
        this.attacking = attacking;
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
        System.out.println("abajo " + moving);
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

    public void ActuPosicion() {
        if (isDead)
            return;

        moving = false;
        if (jump)
            jump();

        float xSpeed = 0;

        if (inKnockback) {
            xSpeed = knockbackDir * knockbackSpeed;
            if (!inAir && airSpeed >= 0) {
                inKnockback = false;
            }
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

        if (!left && !right && !inAir && !inKnockback)
            return;

       
        if (!inAir && !IsEntityOnFloor(hitbox, lvlData) && !enPlataforma)
            inAir = true;

        if (inAir) {
            if (CanMoveHere(hitbox.x, hitbox.y + airSpeed,
                    (int) hitbox.width, (int) hitbox.height , lvlData)) {
                hitbox.y += airSpeed;
                airSpeed += gravity;
                updateXPos(xSpeed);
            } else {
                hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
                if (airSpeed > 0)
                    resetInAir();
                else
                    airSpeed = fallSpeedAfterCollision;
                updateXPos(xSpeed);
            }
        } else
            updateXPos(xSpeed);

        moving = true;
    }

    private void updateXPos(float xSpeed) {
        if (isDead)
            return;
        if (CanMoveHere(hitbox.x + xSpeed, hitbox.y ,
                (int) hitbox.width, (int) hitbox.height, lvlData))
            hitbox.x += xSpeed;
        else {
            hitbox.x = GetEntityXPosNextToWall(hitbox, xSpeed);
            /*
             * if(airSpeed>0)
             * resetInAir();
             */
        }
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

// 1. Agregamos yLvlOffset a los parámetros del método
    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        int flipX = 0;
        int flipW = 1;

        if (playerDirec == -1) {
            flipX = w;
            flipW = -1;
        }

        g.drawImage(idLeAni[playerAction][animInd],
                (int) (hitbox.x - xDrawOffset) - xLvlOffset + flipX, // El eje X usa xLvlOffset
                (int) (hitbox.y - yDrawOffset) - yLvlOffset,         // 2. AQUI RESTAMOS EL yLvlOffset
                w * flipW,
                h, null);
                
        // 3. Pasamos ambos offsets a los métodos de dibujo de cajas de colisión
        drawHitbox(g, xLvlOffset, yLvlOffset); 
        drawAttackBox(g, xLvlOffset, yLvlOffset);
    }

    private void drawAttackBox(Graphics g, int xLvlOffset, int yLvlOffset) {
        g.drawRect((int) attackBox.x - xLvlOffset, (int) attackBox.y - yLvlOffset, (int) attackBox.width, (int) attackBox.height);
    }

    private void loadAnimation() {
        healthBarEmpty = LoadSave.GetSpriteAtlas(LoadSave.HEALTH_BAR_EMPTY);
        healthBarFull = LoadSave.GetSpriteAtlas(LoadSave.HEALTH_BAR_FULL);
        BufferedImage heartSheet = LoadSave.GetSpriteAtlas(LoadSave.HEART_SPRITESHEET);
        heartFrames = new BufferedImage[2];
        for (int i = 0; i < 2; i++) {
            heartFrames[i] = heartSheet.getSubimage(i * 90, 0, 90, 28);
        }
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
        idLeAni = new BufferedImage[7][9];
        for (int j = 0; j < idLeAni.length; j++) {
            for (int i = 0; i < idLeAni[j].length; i++) {
                idLeAni[j][i] = img.getSubimage(i * 100, j * 100, 100, 100);
            }
        }
    }

    public void resetDirBoolean() {
        left = right = up = down = false;
    }

 public void recibirDaño(int cantidad, int dirEnemigo) {
        // Llama al método de abajo diciéndole que SÍ haga ruido (true)
        recibirDaño(cantidad, dirEnemigo, true); 
    }

    // 2. Nuevo método que acepta la variable 'hacerRuido'
    public void recibirDaño(int cantidad, int dirEnemigo, boolean hacerRuido) {
        if (isDead || invulnerableTimer > 0)
            return;

        // Solo reproduce el sonido si 'hacerRuido' es true
        if (hacerRuido && audioPlayer != null) {
            audioPlayer.reproducirEfecto("sonido-dano.wav");
        }

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
        if (vidaActual > vidaMaxima) {
            vidaActual = vidaMaxima;
        }
    }

    private void revisarPicos() {
        if (utils.MetodosAyuda.tocandoPicos(hitbox, lvlData)) {
            
            // Verificamos que no esté muerto y que no sea invulnerable AHORA MISMO.
            // Así el sonido solo se dispara 1 sola vez cuando realmente le baja vida.
            if (!isDead && invulnerableTimer == 0) {
                if (audioPlayer != null) {
                    audioPlayer.reproducirEfecto("sonido-dano.wav");
                }
            }
            
            // Llamamos al método original, el cual bajará la vida y pondrá el invulnerableTimer en 80
            recibirDaño(20, -playerDirec); 
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
        int xHeart = 10;
        int yHeart = 10;

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

        if (proporcion <= 4.5f) {
            dañoAtaque += 3;
        } else {
            dañoAtaque += 1;
        }
    }

    public boolean isReadyToRestart() {
        return readyToRestart;
    }

    public void resetAll() {
        resetDirBoolean();
        inAir = false;
        isDead = false;
        attacking = false;
        moving = false;
        inKnockback = false;
        playerAction = INACTIVO;
        vidaActual = vidaMaxima;

        hitbox.x = spawnX;
        hitbox.y = spawnY;

        deadTimer = 0;
        readyToRestart = false;

        if (!utils.MetodosAyuda.IsEntityOnFloor(hitbox, lvlData)) {
            inAir = true;
        }
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
    if(b) {
        inAir = false; // Detiene la caída
    }
}

public void recogerLlave() {
    this.tieneLlave = true;
}

public boolean getTieneLlave() {
    return tieneLlave;
}

// Necesario para el salto desde plataformas que vimos antes
public float getAirSpeed() {
    return airSpeed;
}
    
}