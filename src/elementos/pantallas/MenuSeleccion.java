package elementos.pantallas;

import juego.Juego;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.RenderingHints;
import java.io.InputStream;
import javax.imageio.ImageIO;
import elementos.jugador.Personaje;
import java.awt.image.BufferedImage;

public class MenuSeleccion {

    private Juego juego;
    private int seleccionIndice = 0;
    private BufferedImage imagenFondoLiteral;

    // Definición de personajes (se mantiene para la lógica del juego)[cite: 1, 2]
    public final Personaje[] PERSONAJES = {
        new Personaje("Hank", 120, 2.5f, 25, -2.25f, "Soldier.png", 9, 7, 100, 100, "golpe_brutal", 87f, 80f, 500, 3),
        new Personaje("Frank", 200, 2f, 20, -2.25f, "Frank.png", 9, 7, 100, 100, "coraza", 87f, 80f, 400, 2),
        new Personaje("Saori", 180, 2.8f, 20, -2.6f, "Saori.png", 13, 8, 100, 100, "robo_vida", 87f, 80f, 200, 5),
        new Personaje("Lucerys", 180, 2.5f, 18, -2.4f, "Lucerys.png", 13, 8, 100, 100, "lluvia", 87f, 80f, 400, 3)
    };

    public MenuSeleccion(Juego juego) {
        this.juego = juego;
        cargarImagenFondo();
    }

    private void cargarImagenFondo() {
        try {
            // Asegúrate de que el nombre del archivo sea exacto en tu carpeta res[cite: 1, 2]
            InputStream is = getClass().getResourceAsStream("/res/fondo_seleccion.png");
            if (is != null) {
                imagenFondoLiteral = ImageIO.read(is);
            } else {
                System.err.println("Error: No se encontró la imagen de fondo en /res/");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void moverSeleccion(int delta) {
        // Navegación circular entre los 4 personajes[cite: 1, 2]
        seleccionIndice = (seleccionIndice + delta + PERSONAJES.length) % PERSONAJES.length;
    }

    public void confirmarSeleccion() {
        juego.aplicarPersonajeElegido(PERSONAJES[seleccionIndice]);
    }

    public void dibujar(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        // 1. DIBUJAR IMAGEN INTEGRAL (Fondo + Tarjetas)[cite: 1, 2]
        if (imagenFondoLiteral != null) {
            g.drawImage(imagenFondoLiteral, 0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT, null);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT);

        }
                float[] posicionesX = {
            0.245f, // Tarjeta 1 (Hank)
            0.376f, // Tarjeta 2 (Frank)
            0.511f, // Tarjeta 3 (Saori)  <-- Cambia este si no queda
            0.642f  // Tarjeta 4 (Lucerys) <-- Cambia este si no queda
        };

        // 2. CONFIGURACIÓN DE ESCALA PARA EL BRILLO[cite: 1, 2]
        // Estos valores están ajustados para la imagen Gemini_Generated_Image_cefsqdcefsqdcefs.jpg
        float anchoMarco = Juego.GAME_WIDTH * 0.120f; 
        float altoMarco = Juego.GAME_HEIGHT * 0.405f; 
        float yMarcos = Juego.GAME_HEIGHT * 0.165f;   
        float inicioX = Juego.GAME_WIDTH * 0.245f;    
        float separacionX = Juego.GAME_WIDTH * 0.130f; 

        // Calcular posición X según el índice actual
        int cx = (int)(Juego.GAME_WIDTH * posicionesX[seleccionIndice]);
        int cy = (int)yMarcos;

        // 3. RENDERIZAR EFECTO DE SELECCIÓN (GLOW)[cite: 1, 2]
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Brillo exterior (Aura dorada suave)
        g2.setColor(new Color(255, 215, 0, 100)); 
        g2.setStroke(new BasicStroke(6f * Juego.SCALE)); 
        g2.drawRoundRect(cx - 2, cy - 2, (int)anchoMarco + 4, (int)altoMarco + 4, 15, 15);

        // Marco de luz principal (Blanco-Dorado)
        g2.setColor(new Color(255, 255, 200, 200)); 
        g2.setStroke(new BasicStroke(2.5f * Juego.SCALE)); 
        g2.drawRoundRect(cx, cy, (int)anchoMarco, (int)altoMarco, 12, 12);
        
        // Efecto de oscurecimiento para las tarjetas NO seleccionadas (Opcional)
       // dibujarSombraNoSeleccionados(g2, posicionesX, yMarcos, anchoMarco, altoMarco);
    }

    private void dibujarSombraNoSeleccionados(Graphics2D g2, float[] posInstanciasX, float y, float w, float h) {
    g2.setColor(new Color(0, 0, 0, 80)); 
    for (int i = 0; i < 4; i++) {
        if (i != seleccionIndice) {
            int px = (int)(Juego.GAME_WIDTH * posInstanciasX[i]);
            g2.fillRoundRect(px, (int)y, (int)w, (int)h, 12, 12);
        }
    }
}
    
    public int getSeleccionIndice() {
        return seleccionIndice;
    }

    
}