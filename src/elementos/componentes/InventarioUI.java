package elementos.componentes;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import elementos.jugador.Jugador;
import juego.Juego;
import utils.LoadSave;

public class InventarioUI {

    private BufferedImage imgInventario;
    private BufferedImage[] imgNumeros; // Arreglo para guardar los 3 números
    private int imgW, imgH;
    private int tileNumSize;

    public InventarioUI() {
        // 1. Cargamos tu diseño base (H, O, C)
        try {
            imgInventario = LoadSave.GetSpriteAtlas("inventario.png");
        } catch (Exception e) {
            System.out.println("¡Error! No se encontró la imagen inventario.png en la carpeta res");
        }

        // 2. Cargamos tu NUEVO diseño de números y lo recortamos
        try {
            BufferedImage tempNumeros = LoadSave.GetSpriteAtlas("numeros.png");
            imgNumeros = new BufferedImage[3]; // Espacio para el 0, 1 y 2
            
            for (int i = 0; i < 3; i++) {
                // Recortamos cuadros de 32x32 píxeles
                imgNumeros[i] = tempNumeros.getSubimage(i * 32, 0, 32, 32);
            }
        } catch (Exception e) {
            System.out.println("¡Error! No se encontró la imagen numeros_inventario.png en la carpeta res");
        }

        // 3. Tamaños escalados para que se vea bien en pantalla
        imgW = (int) (64 * Juego.SCALE);
        imgH = (int) (96 * Juego.SCALE);
        tileNumSize = (int) (32 * Juego.SCALE); // El tamaño de 1 solo cuadrado
    }

    public void draw(Graphics g, Jugador j) {
        if (imgInventario == null || imgNumeros == null) return;

        // Posición general: Pegado a la esquina superior derecha
        int xPos = Juego.GAME_WIDTH - imgW - (int)(20 * Juego.SCALE);
        int yPos = (int)(40 * Juego.SCALE);

        // 1. Dibujamos el panel base (con los cuadrados grises vacíos a la derecha)
        g.drawImage(imgInventario, xPos, yPos, imgW, imgH, null);

        // 2. Calculamos la posición X de la segunda columna (donde van los números)
        // Simplemente le sumamos el tamaño de un cuadro (32 escalado) a la posición original
        int xNumeros = xPos + tileNumSize;

        // 3. Obtenemos las cantidades de tu inventario
        // Usamos Math.min para asegurarnos de que NUNCA pida un número mayor a 2, 
        // así evitamos que el juego crashee si por algún bug el jugador recoge 3 átomos.
        int cantH = Math.min(j.getAtomosH(), 2);
        int cantO = Math.min(j.getAtomosO(), 2);
        int cantC = Math.min(j.getAtomosC(), 2);

        // 4. Dibujamos el número correspondiente como si fuera un rompecabezas
        
        // Fila 1 (Hidrógeno): Mismo nivel Y
        g.drawImage(imgNumeros[cantH], xNumeros, yPos, tileNumSize, tileNumSize, null);

        // Fila 2 (Oxígeno): Bajamos 1 cuadrado (tileNumSize)
        g.drawImage(imgNumeros[cantO], xNumeros, yPos + tileNumSize, tileNumSize, tileNumSize, null);

        // Fila 3 (Carbono): Bajamos 2 cuadrados (tileNumSize * 2)
        g.drawImage(imgNumeros[cantC], xNumeros, yPos + (tileNumSize * 2), tileNumSize, tileNumSize, null);
    }
}