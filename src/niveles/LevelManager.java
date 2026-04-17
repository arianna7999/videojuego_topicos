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
    }


    public void loadSpritesForCurrentLevel() {
        String atlasName = (lvlIndex == 0) ? LoadSave.LEVEL_ATLAS : LoadSave.LEVEL_ATLAS_2;
        BufferedImage img = LoadSave.GetSpriteAtlas(atlasName);
        levelSprite = new BufferedImage[48];
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 12; i++) {
                int index = j * 12 + i;
                levelSprite[index] = img.getSubimage(i * 32, j * 32, 32, 32);                
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
}