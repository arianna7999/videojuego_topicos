package elementos.objetos;

import static utils.Constantes.ConstantesObjetos.CANDELABRO; 

public class Candelabro extends ObjetoJuego {

    public Candelabro(int x, int y) {
        super(x, y, CANDELABRO); 
        initHitbox(16, 41); 
        this.animSpeed = 20; 
    }

    public void updateAnimation() {
        animTick++;
        if (animTick >= animSpeed) {
            animTick = 0;
            animInd++;
            
            if (animInd >= 7) {
                animInd = 0;
            }
        }
    }
}