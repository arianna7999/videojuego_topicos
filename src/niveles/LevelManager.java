package niveles;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import juego.Juego;
import utils.LoadSave;

public class LevelManager {
    private Juego game;
    private BufferedImage[] levelSprite;
    private Level levelOne;

    public LevelManager(Juego game) {
        this.game = game;
        importOutsideSprite();
        levelOne=new Level(LoadSave.GetLevelData());
    }

    public Level currentLevel(){
        return levelOne;
    }

    private void importOutsideSprite() {
        BufferedImage img=LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
        levelSprite=new BufferedImage[48];
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 12; i++) {
                int index=j*12+i;
                levelSprite[index]=img.getSubimage(i*32, j*32, 32, 32);                
            }
        }
        
    }

    public void draw(Graphics g, int LvlOffset){
        for (int j = 0; j < juego.Juego.TILES_HEIGHT; j++) {
            for (int i = 0; i < levelOne.getLvlData()[0].length; i++) {
                int index=levelOne.getSpriteIndex(i, j);
                g.drawImage(levelSprite[index],juego.Juego.TILES_SIZE*i-LvlOffset,
                     juego.Juego.TILES_SIZE*j,juego.Juego.TILES_SIZE,
                     juego.Juego.TILES_SIZE, null);
            }
            
        }

       // g.drawImage(levelSprite, 0, 0, null);

    }
    public void update(){}

}
