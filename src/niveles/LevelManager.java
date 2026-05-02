package niveles;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import juego.Juego;
import utils.LoadSave;

public class LevelManager {
    private Juego game;
    private BufferedImage[] levelSprite;
    private ArrayList<Level> levels;
    private int lvlIndex = 0;

    public LevelManager(Juego game) {
        this.game = game;
        buildAllLevels();
        loadSpritesForCurrentLevel();
    }

    private void buildAllLevels() {
        levels = new ArrayList<>();
        levels.add(new Level(LoadSave.GetLevelData(1)));
        levels.add(new Level(LoadSave.GetLevelData(2)));
        levels.add(new Level(LoadSave.GetLevelData(3)));
        levels.add(new Level(LoadSave.GetLevelData(4)));
        
    }


    public void loadSpritesForCurrentLevel() {
        String atlasName = (lvlIndex == 0) ? LoadSave.LEVEL_ATLAS 
                         : (lvlIndex == 1) ? LoadSave.LEVEL_ATLAS_2 
                         : (lvlIndex == 2) ? LoadSave.LEVEL_ATLAS_3 
                         : (lvlIndex == 3) ? LoadSave.LEVEL_ATLAS_4 
                         : null;
        BufferedImage img = LoadSave.GetSpriteAtlas(atlasName);

        int tileW = 32;
        int tileH = 32;
        int columnas = img.getWidth() / tileW;
        int filas = img.getHeight() / tileH;
        int totalSprites = columnas * filas;

        levelSprite = new BufferedImage[totalSprites];
        for (int j = 0; j < filas; j++) {
            for (int i = 0; i < columnas; i++) {
                int index = j * columnas + i;
                levelSprite[index] = img.getSubimage(i * tileW, j * tileH, tileW, tileH);
            }
        }
    }

    public void loadNextLevel() {
        lvlIndex++;
        if (lvlIndex < levels.size()) {
            loadSpritesForCurrentLevel();
        }
    }

    public Level currentLevel(){
        if (lvlIndex >= levels.size()) {
            return levels.get(levels.size() - 1);
        }
        return levels.get(lvlIndex);
    }
    
    public int getLevelIndex() {
        return lvlIndex;
    }
    
    public int getAmountOfLevels() {
        return levels.size();
    }

    public void draw(Graphics g, int xLvlOffset, int yLvlOffset){
        
        int mapHeight = currentLevel().getLvlData().length;
        
        for (int j = 0; j < mapHeight; j++) {
            for (int i = 0; i < currentLevel().getLvlData()[0].length; i++) {
                int index = currentLevel().getSpriteIndex(i, j);
                
                g.drawImage(levelSprite[index], 
                     juego.Juego.TILES_SIZE * i - xLvlOffset,
                     juego.Juego.TILES_SIZE * j - yLvlOffset, 
                     juego.Juego.TILES_SIZE,
                     juego.Juego.TILES_SIZE, null);
            }
        }
    }

    public void update(){}

    public void resetToFirstLevel() {
        lvlIndex = 3;
        loadSpritesForCurrentLevel();
    }
}