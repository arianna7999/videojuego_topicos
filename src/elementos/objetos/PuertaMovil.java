package elementos.objetos;

import juego.Juego;

public class PuertaMovil extends ObjetoJuego {
    private float yObjetivo;
    private float velocidad = .5f * Juego.SCALE;
    private boolean abriendo = false;
    private boolean abierta = false;
    private int animTick, animInd, animSpeed = 15;

    public PuertaMovil(int x, int y, int tipo, int pixelesASubir) {
        super(x, y, tipo);
        // Calculamos hasta dónde debe subir la pared (restando en el eje Y)
        this.yObjetivo = y - (pixelesASubir * Juego.SCALE);

        // Hitbox: 1 tile de ancho (32) y 3 tiles de alto (96) para tu imagen
        initHitbox(32, 96);
    }

    // Velocidad de apertura

    public void update() {
        if (abriendo) {
            animTick++;
            if (animTick >= animSpeed) {
                animTick = 0;
                animInd++;
                // Cuando llega al frame 4 (el quinto), se queda abierta
                if (animInd >= 4) {
                    animInd = 4;
                    abriendo = false;
                    abierta = true;
                    // Al abrirse, eliminamos la colisión haciendo la hitbox pequeña
                    hitbox.height = 0;
                }
            }
        }
    }

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