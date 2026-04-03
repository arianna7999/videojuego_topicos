package utils;

import java.awt.geom.Rectangle2D;

import juego.Juego;

public class MetodosAyuda {

    public static boolean CanMoveHere(float x, float y, int width, int height, int[][] lvlData) {
        int leftCol = (int) (x / Juego.TILES_SIZE);
        int rightCol = (int) ((x + width - 1) / Juego.TILES_SIZE);
        int topRow = (int) (y / Juego.TILES_SIZE);
        int bottomRow = (int) ((y + height - 1) / Juego.TILES_SIZE);

        for (int c = leftCol; c <= rightCol; c++) {
            for (int r = topRow; r <= bottomRow; r++) {
                if (IsTileSolid(c, r, lvlData)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean IsEntityOnFloor(Rectangle2D.Float hitbox, int[][] lvlData) {
        int leftCol = (int) (hitbox.x / Juego.TILES_SIZE);
        int rightCol = (int) ((hitbox.x + hitbox.width - 1) / Juego.TILES_SIZE);
        int bottomRow = (int) ((hitbox.y + hitbox.height + 1) / Juego.TILES_SIZE);

        for (int c = leftCol; c <= rightCol; c++) {
            if (IsTileSolid(c, bottomRow, lvlData)) {
                return true;
            }
        }
        return false;
    }

private static boolean IsTileSolid(int xIndex, int yIndex, int[][] lvlData) {
        if (xIndex < 0 || xIndex >= lvlData[0].length) return true; // Lados sólidos
        if (yIndex < 0) return true; // Techo sólido
        if (yIndex >= lvlData.length) return false; // ¡El vacío ahora es aire para que puedas caer!
        
        int valor = lvlData[yIndex][xIndex];
        
        if (valor >= 48 || valor < 0) return true;
        
        // Mantén los números así para que los picos sigan siendo sólidos
        if (valor == 11 || valor == 4 || valor == 5 || 
            valor == 16 || valor == 17 || 
            valor == 3 || valor == 15 || valor == 27) {
            
            return false;
        }
        return true;
    }

    private static boolean IsSolid(float x, float y, int[][] lvlData) {
        return IsTileSolid((int) (x / Juego.TILES_SIZE), (int) (y / Juego.TILES_SIZE), lvlData);
    }

public static float GetEntityXPosNextToWall(Rectangle2D.Float hitbox, float xSpeed) {
        if (xSpeed > 0) {
            int rightTile = (int) ((hitbox.x + hitbox.width + xSpeed) / Juego.TILES_SIZE);
            return rightTile * Juego.TILES_SIZE - hitbox.width - 1;
        } else {
            int leftTile = (int) ((hitbox.x + xSpeed) / Juego.TILES_SIZE);
            // Agregamos un + 1 aquí para evitar que se atore con los decimales
            return (leftTile + 1) * Juego.TILES_SIZE + 1; 
        }
    }

    public static float GetEntityYPosUnderRoofOrAboveFloor(Rectangle2D.Float hitbox, float airSpeed) {
        if (airSpeed > 0) { // Cayendo
            int bottomTile = (int) ((hitbox.y + hitbox.height + airSpeed) / Juego.TILES_SIZE);
            return bottomTile * Juego.TILES_SIZE - hitbox.height - 1;
        } else { // Saltando o rebotando por daño
            int topTile = (int) ((hitbox.y + airSpeed) / Juego.TILES_SIZE);
            // Agregamos un + 1 aquí para evitar que la cabeza se atore
            return (topTile + 1) * Juego.TILES_SIZE + 1; 
        }
    }
    public static boolean EsSueloSolido(float x, float y, float width, float height, float xSpeed, int[][] lvlData) {
        float proximoX = x + xSpeed;
        if (xSpeed > 0) {

            return IsSolid(proximoX + width - 1, y + height + 1, lvlData);
        } else {
            return IsSolid(proximoX, y + height + 1, lvlData);
        }
    }

    public static boolean tocandoPicos(java.awt.geom.Rectangle2D.Float hitbox, int[][] lvlData) {
        // Calculamos las celdas que ocupa el jugador, más un pixel hacia abajo para detectar el suelo
        int leftCol = (int) (hitbox.x / Juego.TILES_SIZE);
        int rightCol = (int) ((hitbox.x + hitbox.width - 1) / Juego.TILES_SIZE);
        int topRow = (int) (hitbox.y / Juego.TILES_SIZE);
        int bottomRow = (int) ((hitbox.y + hitbox.height + 1) / Juego.TILES_SIZE); 

        // Recorremos los tiles cercanos al jugador
        for (int c = leftCol; c <= rightCol; c++) {
            for (int r = topRow; r <= bottomRow; r++) {
                // Evitar salirnos del mapa
                if (c >= 0 && c < lvlData[0].length && r >= 0 && r < lvlData.length) {
                    int valor = lvlData[r][c];
                    // Si toca alguno de los sprites de picos (42, 43, 44, 45)
                    if (valor == 42 || valor == 43 || valor == 44 || valor == 45) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean IsEntityOnGameObject(java.awt.geom.Rectangle2D.Float hitbox, java.awt.geom.Rectangle2D.Float objHitbox) {
        // Cambiamos el + 1 por un + 10 para aumentar el rango de detección hacia abajo.
        // Así el jugador detectará la plataforma incluso si esta acaba de bajar.
        return (hitbox.y + hitbox.height + 10 >= objHitbox.y &&
                hitbox.y + hitbox.height <= objHitbox.y + 10 &&
                hitbox.x + hitbox.width > objHitbox.x &&
                hitbox.x < objHitbox.x + objHitbox.width);
    }
}