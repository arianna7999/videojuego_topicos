package elementos.objetos;

import juego.Juego;
import static utils.Constantes.ConstantesObjetos.*;

public class Recompensas extends ObjetoJuego {
    private int aniTick, aniIndex;
    private int aniSpeed = 25;
    private int maxFrames;

    public Recompensas(int x, int y, int tipoObjeto) {
        super(x, y, tipoObjeto);
        initHitbox(20, 20);

        if (tipoObjeto == LLAVE) {
            maxFrames = 7;
        } else {
            maxFrames = 1;
        }
    }
    public void updateAnimation() {
        if (getTipoObjeto() == LLAVE) {
            aniTick++;
            if (aniTick >= aniSpeed) {
                aniTick = 0;
                aniIndex++;
                if (aniIndex >= maxFrames) {
                    aniIndex = 0;
                }
            }
        }
    }

    public int getAniIndex() {
        return aniIndex;
    }
}