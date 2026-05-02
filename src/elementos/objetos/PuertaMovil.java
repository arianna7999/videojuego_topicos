package elementos.objetos;

import juego.Juego;

public class PuertaMovil extends ObjetoJuego {

    private float yObjetivo;
    private float velocidad = .5f * Juego.SCALE;
    private boolean abriendo = false;
    private boolean abierta = false;
    private int animTick, animInd, animSpeed = 15;
    private int nivelActual;

    public PuertaMovil(int x, int y, int tipo, int pixelesASubir, int nivelActual) {
        super(x, y, tipo);
        this.nivelActual = nivelActual;
        this.yObjetivo = y - (pixelesASubir * Juego.SCALE);
        initHitbox(32, 96);
    }

    public void update() {
        if (abriendo) {
            
            // LÓGICA DE MOVIMIENTO FÍSICO
            // Solo restamos Y si NO estamos en el Mundo 3 (nivel index 2)
            if (this.nivelActual != 2) {
                hitbox.y -= velocidad;
            }

            // LÓGICA DE ANIMACIÓN (Afecta a los frames de REJAS.png)
            animTick++;
            if (animTick >= animSpeed) {
                animTick = 0;
                animInd++;
                
                // El límite de animación según tus assets es el frame 4 (total 5)
                if (animInd >= 4) {
                    animInd = 4;
                    // En el mundo 3, aquí termina el proceso
                    if (this.nivelActual == 2) {
                        abriendo = false;
                        abierta = true;
                        // Al terminar la animación, eliminamos la colisión
                        hitbox.height = 0;
                    }
                }
            }

            // LÓGICA DE PARADA PARA PUERTAS QUE SE ALZAN (Mundos 1 y 2)
            if (this.nivelActual != 2) {
                if (hitbox.y <= yObjetivo) {
                    hitbox.y = yObjetivo; // Asegurar posición exacta
                    abriendo = false;
                    abierta = true;
                    // Eliminamos la colisión física para permitir el paso
                    hitbox.height = 0;
                }
            }
        }
    }

    // Getters y Setters de estado
    public int getAnimInd() {
        return animInd;
    }

    public void abrir() {
        if (!abierta)
            abriendo = true;
    }

    public boolean estaAbierta() {
        return abierta;
    }

    public boolean estaAbriendo() {
        return abriendo;
    }
}