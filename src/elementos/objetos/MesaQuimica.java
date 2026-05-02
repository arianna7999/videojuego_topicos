package elementos.objetos;

import static utils.Constantes.ConstantesObjetos.MESA_QUIMICA;

public class MesaQuimica extends ObjetoJuego {

    public MesaQuimica(int x, int y) {
        super(x, y, MESA_QUIMICA); 
        initHitbox(128, 128); 
        this.animSpeed = 20; 
    }

    public void updateAnimation() {
        animTick++;
        if (animTick >= animSpeed) {
            animTick = 0;
            animInd++;
            
            if (animInd >= 5) {
                animInd = 0;
            }
        }
    }
}