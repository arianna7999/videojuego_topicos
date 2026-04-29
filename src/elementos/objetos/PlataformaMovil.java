package elementos.objetos;

import juego.Juego;

public class PlataformaMovil extends ObjetoJuego {
    private float inicio, fin;
    private float velocidad = .5f * Juego.SCALE;
    private boolean moviendoPositivo = true; // true = se mueve a la derecha o hacia abajo
    private boolean esVertical;

    // Agregamos 'esVertical' al constructor
    public PlataformaMovil(int x, int y, int tipo, int distancia, boolean esVertical) {
        super(x, y, tipo);
        this.esVertical = esVertical;

        if (esVertical) {
            this.inicio = y;
            this.fin = y + distancia;
        } else {
            this.inicio = x;
            this.fin = x + distancia;
        }

        // 80 de ancho (2.5 tiles) y 32 de alto
        initHitbox(96, 32);
    }

    public void update() {
        if (esVertical) {
            // Movimiento de Arriba a Abajo
            if (moviendoPositivo) {
                hitbox.y += velocidad;
                if (hitbox.y >= fin)
                    moviendoPositivo = false;
            } else {
                hitbox.y -= velocidad;
                if (hitbox.y <= inicio)
                    moviendoPositivo = true;
            }
        } else {
            // Movimiento de Izquierda a Derecha
            if (moviendoPositivo) {
                hitbox.x += velocidad;
                if (hitbox.x >= fin)
                    moviendoPositivo = false;
            } else {
                hitbox.x -= velocidad;
                if (hitbox.x <= inicio)
                    moviendoPositivo = true;
            }
        }

        this.x = (int) hitbox.x;
        this.y = (int) hitbox.y;
    }

    // Separé la velocidad en X y Y para que el jugador sepa hacia dónde empujarse
    public float getVelocidadX() {
        if (esVertical)
            return 0; // Si es vertical, no empuja en X
        return moviendoPositivo ? velocidad : -velocidad;
    }
}