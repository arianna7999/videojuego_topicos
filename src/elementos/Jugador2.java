package elementos;

import juego.Juego;
import utils.LoadSave;

import static utils.Constantes.GetNoSprite;
import static utils.Constantes.ConstantesJugador.ATACAR1;
import static utils.Constantes.ConstantesJugador.CAYENDO;
import static utils.Constantes.ConstantesJugador.CORRER;
import static utils.Constantes.ConstantesJugador.INACTIVO;
import static utils.Constantes.ConstantesJugador.SALTAR;
import static utils.MetodosAyuda.*;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D.Float;
import java.awt.image.BufferedImage;

import eventos.EntraTeclado;

public class Jugador2 extends Cascaron {
    private BufferedImage[][] idLeAni;
    private int animInd, animTick = 0, animSpeed = 15;
    private int playerAction = INACTIVO;
    private int playerDirec = -1;
    private boolean moving = false;
    private boolean attacking = false;
    private boolean up, down, left, right, jump;
    private float playerSpeed = 2.0f;
    private int[][] lvlData;
    private float xDrawOffset = 21 * Juego.SCALE;
    private float yDrawOffset = 4 * Juego.SCALE;

    private float airSpeed = 0f;
    private float gravity = 0.04f * Juego.SCALE;
    private float jumpSpeed = -2.25f * Juego.SCALE;
    private float fallSpeedAfterCollision = 0.5f * Juego.SCALE;
    private boolean inAir = false;

    public Jugador2(float x, float y, int w, int h) {
        super(x, y, w, h);
        loadAnimation();
        initHitbox(x, y, 20 * Juego.SCALE, 27 * Juego.SCALE);
    }

    public void update() {
        actualizarAnim();
        colocarAnim();
        ActuPosicion();
        // loadLvlData(LoadSave.GetLevelData());
    }

    public void loadLvlData(int[][] getLevelData) {
        this.lvlData = getLevelData;
     /*   if(!IsEntityOnFloor(hitbox,lvlData))
            inAir=true;*/
    }

    private void colocarAnim() {
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

    private void actualizarAnim() {
        animTick++;
        if (animTick >= animSpeed) {
            animTick = 0;
            animInd++;
            if (animInd >= GetNoSprite(playerAction)) {
                animInd = 0;
                attacking = false;
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
        moving = false;
        if (jump)
            jump();
        if (!left && !right && !inAir)
            return;
        float xSpeed = 0;// ySpeed = 0;
        if (left)
            xSpeed = -playerSpeed;//cambie 8
        if (right)
            xSpeed = +playerSpeed;//cambie 8
        if (!inAir && !IsEntityOnFloor(hitbox, lvlData))
            inAir = true;
        if (inAir) {
            if (CanMoveHere(hitbox.x, hitbox.y + airSpeed,
                    (int) hitbox.width, (int) hitbox.height, lvlData)) {
                hitbox.y +=airSpeed;
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
        }else
            updateXPos(xSpeed);
        moving=true;
    }
    private void updateXPos(float xSpeed) {
        if(CanMoveHere(hitbox.x+xSpeed, hitbox.y, 
            (int)hitbox.width, (int)hitbox.height, lvlData))
           hitbox.x+=xSpeed;
        else{
           hitbox.x=GetEntityXPosNextToWall(hitbox,xSpeed);  
           if(airSpeed>0)
             resetInAir();
        }
}

    

    private void resetInAir() {
        inAir=false;
        airSpeed=0;        
    }
    

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    private void jump() {
        if(inAir) return;
        inAir=true;
        airSpeed=jumpSpeed;        
    }

    public void render(Graphics g,int LvlOffset) {
        g.drawImage(idLeAni[playerAction][animInd],
                (int) (hitbox.x - xDrawOffset)-LvlOffset,
                (int) (hitbox.y - yDrawOffset),
                w, h, null);
    }

    private void loadAnimation() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
        idLeAni = new BufferedImage[9][7];
        for (int j = 0; j < idLeAni.length; j++) {
            for (int i = 0; i < idLeAni[j].length; i++) {
                idLeAni[j][i] = img.getSubimage(i * 42, j * 39, 15, 18);//PENDIENTE 
            }
        }
    }

    public void resetDirBoolean() {
        left = right = up = down = false;
    }

}
