package elementos.objetos;

public class Aguila {
    private float x, y;
    private int aniTick, aniIndex, aniSpeed = 15; // Ajusta aniSpeed para que aletee más rápido o lento
    private float speed = 1.5f;

    public Aguila(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        x += speed;

        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= 6) {
                aniIndex = 0;
            }
        }
    }

    public int getAniIndex() { return aniIndex; }
    public float getX() { return x; }
    public float getY() { return y; }
    
    public void resetPocision(float nuevaX) {
        this.x = nuevaX;
    }
}