package elementos;

import juego.Juego;

public class PuertaMovil extends ObjetoJuego {
    private float yObjetivo;
    private float velocidad = 1.0f * Juego.SCALE;
    private boolean abriendo = false;
    private boolean abierta = false;

    public PuertaMovil(int x, int y, int tipo, int pixelesASubir) {
        super(x, y, tipo);
        // Calculamos hasta dónde debe subir la pared (restando en el eje Y)
        this.yObjetivo = y - (pixelesASubir * Juego.SCALE); 
        
        // Hitbox: 1 tile de ancho (32) y 3 tiles de alto (96) para tu imagen
        initHitbox(32, 96); 
    }

    public void update() {
        if (abriendo && !abierta) {
            hitbox.y -= velocidad; 
            if (hitbox.y <= yObjetivo) {
                abierta = true;
                abriendo = false;
            }
            // Sincronizamos la variable y entera para el dibujo
            this.y = (int)hitbox.y; 
        }
    }

    public void abrir() {
        if (!abierta) abriendo = true;
    }

    public boolean estaAbierta() {
        return abierta;
    }
    public boolean estaAbriendo() {
        return abriendo;
    }
}