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
    
    // --- TILESETS DE LOS NIVELES ---
    public static final String LEVEL_ATLAS = "1.png";
    public static final String LEVEL_ATLAS_2 = "2.png";
    public static final String LEVEL_ATLAS_3 = "3.png";
    public static final String LEVEL_ATLAS_4 = "4.png";
    
    // --- MAPAS (ROJO = TERRENO) ---
    public static final String LEVEL_ONE_LONG = "mapa.png";
    public static final String LEVEL_TWO_LONG = "mapa2.png";
    public static final String LEVEL_THREE_LONG = "mapa3.png";
    public static final String LEVEL_FOUR_LONG = "mapa4.png";

    // --- MAPAS (VERDE = ENEMIGOS, AZUL = OBJETOS) ---
    public static final String LEVEL_ONE_OBJECTS = "objetosm12.png";
    public static final String LEVEL_TWO_OBJECTS = "objetosm2.png";
    public static final String LEVEL_THREE_OBJECTS = "objetosm3.png";
    public static final String LEVEL_FOUR_OBJECTS = "objetosm4.png";

    // --- SPRITES ---
    public static final String PLAYER_ATLAS = "Soldier.png";
    public static final String SKELETON_SPRITE = "Skeleton.png";
    public static final String FINAL_BOSS_SPRITE = "Lancer.png";
    public static final String KNIGHT_SPRITE = "Knight.png";
    public static final String ORC_SPRITE = "Orc.png";
    public static final String ELITE_ORC = "elite_orc.png";
    public static final String ARMORED_ORC = "Armored_orc.png";
    public static final String WEREBEAR = "Werebear.png";
    public static final String SLIME = "Slime.png";
    public static final String WEREWOLF = "Werewolf.png";
    public static final String ARMORED_AXEMAN = "armored_axeman.png";

    public static final String BOSQUE_FONDO1    = "sky.png";
    public static final String BOSQUE_FONDO2    = "mountains.png";
    public static final String BOSQUE_FONDO3    = "near-trees.png";
    public static final String BOSQUE_FONDO4    = "far-trees.png";

    public static final String PLAYING_BG_IMG ="fondo1.png";
    public static final String FONDO_ARBOLES_IMG="fondo2.png";
    public static final String FONDO2_ARBOLES_IMG="fondo2.2.png";
    public static final String FONDO_PIEDRAS_IMG="fondo3.png";
    public static final String FONDO_CASTLE_IMG_1="layer_1.png";
    public static final String FONDO_CASTLE_IMG_2="layer_2.png";
    public static final String FONDO_CASTLE_IMG_3="layer_3.png";
    public static final String FONDO_MUNDO_3_CIELO="mundo3_cielo.png";
    public static final String FONDO_MUNDO_3_PIEDRAS="mundo3_piedras.png";
    public static final String FONDO_MUNDO_3_MONTANAS="mundo3_montanas.png";
    public static final String FONDO_MUNDO_3_SOMBRA="sombra.png";

    public static final String HEALTH_BAR_EMPTY = "health_bar_empty.png";
    public static final String HEALTH_BAR_FULL = "health_bar_full.png";
    public static final String HEART_SPRITESHEET = "heart_spritesheet.png";
    public static final String EXPLOSION_BLUE = "explosion_blue.png";
    public static final String VICTORY_SCREEN = "win.png";
    public static final String POSTE_DUENOS = "poste-duenos.png";
    public static final String PLATAFORMA_MOVIBLE = "plataforma-movible.png";
    public static final String CANDELABRO_SPRITE = "candelabro.png";
    public static final String AGUILA_SPRITE = "aguila.png";

    public static int[][] GetEnemyData(int levelNumber) {
        String mapName = LEVEL_ONE_OBJECTS;
        
        switch (levelNumber) {
            case 1: mapName = LEVEL_ONE_OBJECTS; break;
            case 2: mapName = LEVEL_TWO_OBJECTS; break;
            case 3: mapName = LEVEL_THREE_OBJECTS; break;
            case 4: mapName = LEVEL_FOUR_OBJECTS; break;
        }

        BufferedImage img = GetSpriteAtlas(mapName);
        int[][] enemyData = new int[img.getHeight()][img.getWidth()];
        
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                enemyData[j][i] = color.getGreen();
            }
        }
        return enemyData;
    }

    public static int[][] GetObjectData(int levelNumber) {
        String mapName = LEVEL_ONE_OBJECTS;
        
        switch (levelNumber) {
            case 1: mapName = LEVEL_ONE_OBJECTS; break;
            case 2: mapName = LEVEL_TWO_OBJECTS; break;
            case 3: mapName = LEVEL_THREE_OBJECTS; break;
            case 4: mapName = LEVEL_FOUR_OBJECTS; break;
        }

        BufferedImage img = GetSpriteAtlas(mapName);
        int[][] objData = new int[img.getHeight()][img.getWidth()];
        
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                objData[j][i] = color.getBlue();
            }
        }
        return objData;
    }
    
    public static BufferedImage GetSpriteAtlas(String name) {
        BufferedImage img = null;
        InputStream is = LoadSave.class.getResourceAsStream("/res/" + name);
        try {
            img = ImageIO.read(is);
        } catch (IOException e) {
            System.out.println("Error al cargar la imagen: " + name);
            Logger.getLogger(PanelJuego.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                System.out.println("Cerrando InputStream para: " + name);
                if (is != null) is.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error al cerrar el InputStream: " + name);
            }
        }
        return img;
    }

    public static int[][] GetLevelData(int levelNumber) {
        String mapName = LEVEL_ONE_LONG;
        
        switch (levelNumber) {
            case 1: mapName = LEVEL_ONE_LONG; break;
            case 2: mapName = LEVEL_TWO_LONG; break;
            case 3: mapName = LEVEL_THREE_LONG; break;
            case 4: mapName = LEVEL_FOUR_LONG; break;
        }

        BufferedImage img = LoadSave.GetSpriteAtlas(mapName);
        int[][] lvlData = new int[img.getHeight()][img.getWidth()];
        
        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                Color color = new Color(img.getRGB(i, j));
                int valor = color.getRed();
                
                if (valor >= 255) {
                    valor = 11;
                }
                
                lvlData[j][i] = valor;
            }
        }
        return lvlData;
    }
}