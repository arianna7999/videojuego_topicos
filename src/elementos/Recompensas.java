package elementos;

import juego.Juego;
import static utils.Constantes.ConstantesObjetos.*;

public class Recompensas extends ObjetoJuego {

    // Variables de animación
    private int aniTick, aniIndex;
    private int aniSpeed = 25; // Qué tan rápido gira la llave (menor número = más rápido)
    private int maxFrames;

    public Recompensas(int x, int y, int tipoObjeto) {
        super(x, y, tipoObjeto);
        initHitbox(20, 20); // Tamaño de colisión aproximado de la llave
        
        // Configuramos cuántos frames tiene según el tipo
        if (tipoObjeto == LLAVE) {
            maxFrames = 7; // Tus 7 frames de la llave girando
        } else {
            maxFrames = 1; // Corazones u otros objetos estáticos
        }
    }

    // Método para hacer que la llave gire
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

    // Getters para que ObjectManager sepa qué dibujar
    public int getAniIndex() {
        return aniIndex;
    }
}