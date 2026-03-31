package elementos;

import static utils.Constantes.ConstantesObjetos.*;

import juego.Juego;

public class Recompensas extends ObjetoJuego {
    
    public Recompensas(int x, int y, int tipoObjeto) {
        super(x, y, tipoObjeto);
        this.animSpeed = 10;
        initHitbox(20, 20);
    }

    public void update() {
        actualizarAnimacion();
    }

    private void actualizarAnimacion() {
        animTick++;
        if (animTick >= animSpeed) {
            animTick = 0;
            animInd++;
            if (animInd >= GetSpriteAmount(CORAZON, 0)) {
                animInd = 0;
            }
        }
    }
}