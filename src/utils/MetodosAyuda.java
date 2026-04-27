package utils;

import java.awt.geom.Rectangle2D;

import juego.Juego;

public class MetodosAyuda {

    public static boolean CanMoveHere(float x, float y, int width, int height, int[][] lvlData, int levelIndex) {
        int leftCol = (int) (x / Juego.TILES_SIZE);
        int rightCol = (int) ((x + width - 1) / Juego.TILES_SIZE);
        int topRow = (int) (y / Juego.TILES_SIZE);
        int bottomRow = (int) ((y + height - 1) / Juego.TILES_SIZE);

        for (int c = leftCol; c <= rightCol; c++) {
            for (int r = topRow; r <= bottomRow; r++) {
                if (IsTileSolid(c, r, lvlData, levelIndex)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean IsEntityOnFloor(Rectangle2D.Float hitbox, int[][] lvlData, int levelIndex) {
        int leftCol = (int) (hitbox.x / Juego.TILES_SIZE);
        int rightCol = (int) ((hitbox.x + hitbox.width - 1) / Juego.TILES_SIZE);
        int bottomRow = (int) ((hitbox.y + hitbox.height + 1) / Juego.TILES_SIZE);

        for (int c = leftCol; c <= rightCol; c++) {
            if (IsTileSolid(c, bottomRow, lvlData, levelIndex)) {
                return true;
            }
        }
        return false;
    }

    private static boolean IsTileSolid(int xIndex, int yIndex, int[][] lvlData, int levelIndex) {
        if (xIndex < 0 || xIndex >= lvlData[0].length)
            return true;
        if (yIndex < 0)
            return true;
        if (yIndex >= lvlData.length)
            return false;

        int valor = lvlData[yIndex][xIndex];

        switch (levelIndex) {
            case 0:
                if (valor == 11 || valor == 4 || valor == 5 ||
                        valor == 16 || valor == 17 ||
                        valor == 3 || valor == 15 || valor == 27 ||
                        valor == 8 || valor == 20 || valor == 32) {
                    return false; // no sólido
                }
                break;

            default:
                if (valor == 11) {
                    return false;
                }
                break;
        }

        // Si no entró en ninguna condición de "no sólido", entonces es sólido
        return true;
    }

    public static boolean IsSolid(float x, float y, int[][] lvlData, int levelIndex) {
        return IsTileSolid((int) (x / Juego.TILES_SIZE), (int) (y / Juego.TILES_SIZE), lvlData, levelIndex);
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
        if (airSpeed > 0) {
            int bottomTile = (int) ((hitbox.y + hitbox.height + airSpeed) / Juego.TILES_SIZE);
            return bottomTile * Juego.TILES_SIZE - hitbox.height - 1;
        } else {
            int topTile = (int) ((hitbox.y + airSpeed) / Juego.TILES_SIZE);
            return (topTile + 1) * Juego.TILES_SIZE + 1;
        }
    }

    public static boolean EsSueloSolido(float x, float y, float width, float height, float xSpeed, int[][] lvlData,
            int levelIndex) {
        float proximoX = x + xSpeed;
        if (xSpeed > 0) {

            return IsSolid(proximoX + width - 1, y + height + 1, lvlData, levelIndex);
        } else {
            return IsSolid(proximoX, y + height + 1, lvlData, levelIndex);
        }
    }

    public static boolean IsEntityOnGameObject(java.awt.geom.Rectangle2D.Float hitbox,
            java.awt.geom.Rectangle2D.Float objHitbox) {
        return (hitbox.y + hitbox.height + 10 >= objHitbox.y &&
                hitbox.y + hitbox.height <= objHitbox.y + 10 &&
                hitbox.x + hitbox.width > objHitbox.x &&
                hitbox.x < objHitbox.x + objHitbox.width);
    }

    public static boolean IsEntityOnLadder(Rectangle2D.Float hitbox, int[][] lvlData) {
        int x = (int) (hitbox.x + hitbox.width / 2) / juego.Juego.TILES_SIZE;
        int y = (int) (hitbox.y + hitbox.height / 2) / juego.Juego.TILES_SIZE;

        if (x >= 0 && x < lvlData[0].length && y >= 0 && y < lvlData.length) {
            int valor = lvlData[y][x];
            return valor == 8 || valor == 20 || valor == 32;
        }
        return false;
    }
}