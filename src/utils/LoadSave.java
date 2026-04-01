package utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import juego.Juego;
import juego.PanelJuego;

public class LoadSave {
    public static final String LEVEL_ATLAS = "1.png";
    public static final String PLAYER_ATLAS = "Soldier.png";
    public static final String SKELETON_SPRITE = "Skeleton.png";
    public static final String FINAL_BOSS_SPRITE = "Lancer.png";
    public static final String KNIGHT_SPRITE = "Knight.png";
    public static final String ORC_SPRITE = "Orc.png";
    public static final String LEVEL_ONE_LONG = "mapa.png";
    public static final String PLAYING_BG_IMG ="fondo1.png";
    public static final String FONDO_ARBOLES_IMG="fondo2.png";
    public static final String FONDO2_ARBOLES_IMG="fondo2.2.png";
    public static final String FONDO_PIEDRAS_IMG="fondo3.png";
    public static final String HEALTH_BAR_EMPTY = "health_bar_empty.png";
    public static final String HEALTH_BAR_FULL = "health_bar_full.png";
    public static final String HEART_SPRITESHEET = "heart_spritesheet.png";
    public static final String EXPLOSION_BLUE = "explosion_blue.png";
    public static final String VICTORY_SCREEN = "win.png";
    public static final String POSTE_DUENOS = "poste-duenos.png";
    public static final String PLATAFORMA_MOVIBLE = "plataforma-movible.png";
    
    
    public static BufferedImage GetSpriteAtlas(String name) {
        BufferedImage img = null;
        InputStream is = LoadSave.class
                .getResourceAsStream("/res/" + name);
        try {
            img = ImageIO.read(is);

        } catch (IOException e) {
            Logger.getLogger(PanelJuego.class.getName())
                    .log(Level.SEVERE, null, e);
        }
        return img;
    }

    public static int[][] GetLevelData() {

        BufferedImage img = LoadSave.GetSpriteAtlas(LEVEL_ONE_LONG);
        int[][] lvlData = new int[img.getHeight()][img.getWidth()];
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valor = color.getRed();
                if (valor >= 48)
                    valor = 0;
                lvlData[j][i] = valor;
            }
        }
        return lvlData;

    }

}
