package elementos.jugador;

public class Personaje {

    public String nombre;
    public int vida;
    public float velocidad;
    public int daño;
    public float salto;
    public String sprite;
    public int spriteColumnas;
    public int spriteFilas;
    public int spriteCeldaW;
    public int spriteCeldaH;
    public String habilidad;
    public float drawOffsetX;
    public float drawOffsetY;
    public int tiempoParaCurar;
    public int cantidadCuraAutomatica;

    public Personaje(String nombre, int vida, float velocidad, int daño, float salto,
                     String sprite, int spriteColumnas, int spriteFilas,
                     int spriteCeldaW, int spriteCeldaH, String habilidad,
                     float drawOffsetX, float drawOffsetY,
                     int tiempoParaCurar, int cantidadCuraAutomatica) {
        this.nombre = nombre;
        this.vida = vida;
        this.velocidad = velocidad;
        this.daño = daño;
        this.salto = salto;
        this.sprite = sprite;
        this.spriteColumnas = spriteColumnas;
        this.spriteFilas = spriteFilas;
        this.spriteCeldaW = spriteCeldaW;
        this.spriteCeldaH = spriteCeldaH;
        this.habilidad = habilidad;
        this.drawOffsetX = drawOffsetX;
        this.drawOffsetY = drawOffsetY;
        this.tiempoParaCurar = tiempoParaCurar;
        this.cantidadCuraAutomatica = cantidadCuraAutomatica;
    }
}