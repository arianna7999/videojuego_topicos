package elementos;

public class Aguila {
    private float x, y;
    private int aniTick, aniIndex, aniSpeed = 15; // Ajusta aniSpeed para que aletee más rápido o lento
    private float speed = 1.5f; // Velocidad de vuelo

    public Aguila(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        // Mover el águila hacia la derecha (o izquierda si la pones negativa)
        x += speed;

        // Actualizar la animación
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= 6) { // 6 es el número de fotogramas que tiene el águila
                aniIndex = 0;
            }
        }
    }

    public int getAniIndex() { return aniIndex; }
    public float getX() { return x; }
    public float getY() { return y; }
    
    // Opcional: si quieres que al salirse del mapa vuelva a aparecer por la izquierda
    public void resetPocision(float nuevaX) {
        this.x = nuevaX;
    }
}