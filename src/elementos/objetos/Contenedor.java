package elementos.objetos;

import static utils.Constantes.ConstantesObjetos.*;

public class Contenedor extends ObjetoJuego {

    // NUEVO: Variable para controlar que el daño de la explosión solo se aplique una vez
    public boolean danoAplicado = false; 

    public Contenedor(int x, int y, int tipoObjeto) {
        super(x, y, tipoObjeto);
        this.estado = INACTIVO;
        initHitbox(40, 30); 
        
        if (tipoObjeto == BARRIL) {
            this.animSpeed = 30; 
        }
    }

    public void update() {
        if (estado == ANIMACION) {
            animTick++;
            if (animTick >= animSpeed) {
                animTick = 0;
                animInd++;
                if (animInd >= GetSpriteAmount(tipoObjeto, estado)) {
                    activo = false; 
                }
            }
        }
    }

    public void recibirGolpe() {
        if (estado == INACTIVO) {
            estado = ANIMACION;
            animInd = 0;
            animTick = 0;
            danoAplicado = false; // Reiniciamos por si acaso
        }
    }
}