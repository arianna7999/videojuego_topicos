package juego;

import elementos.EnemyManager;
import elementos.Jugador;
import elementos.ObjectManager;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;
import niveles.LevelManager;
import static utils.Constantes.Enviroment.*;
import utils.LoadSave;

public class Juego extends Thread {
    private int prueba;
    private VtaJuego vta;
    private PanelJuego pan;
    private int FPS_SET = 60;
    private int UPS_SET = 200;
    private Jugador player;
    private LevelManager levelMan;
    private EnemyManager enemyManager;
    private ObjectManager objectManager;
    private int xLvlOffset;
    private int leftBorder = (int) (0.2 * Juego.GAME_WIDTH);
    private int rightBorder = (int) (0.8 * Juego.GAME_WIDTH);
    private int lvlTileWide = LoadSave.GetLevelData()[0].length;
    private int maxLvlOffset = lvlTileWide - Juego.TILES_WIDTH;
    private int maxLvlOffsetX = maxLvlOffset * Juego.TILES_SIZE;
    public final static int TILES_DEF_SIZE = 32;
    public final static float SCALE = 1.5f;
    public final static int TILES_WIDTH = 26;
    public final static int TILES_HEIGHT = 14;
    public final static int TILES_SIZE = (int) (TILES_DEF_SIZE * SCALE);
    public final static int GAME_WIDTH = TILES_SIZE * TILES_WIDTH;
    public final static int GAME_HEIGHT = TILES_SIZE * TILES_HEIGHT;
    private BufferedImage bgImg, fondoArboles, fondoArboles2, fondoPiedras, posteInicio, posteDuenos;
    private int[] fondoArbolesPos;
    private Random rnd = new Random();
    private boolean victoria = false;
    private boolean gameOver = false;
    private volatile boolean resetRequerido = false;
    private utils.AudioPlayer reproductorAudio;

    private java.awt.Font customFont;
    
    private java.awt.Font titleFont;
    private java.awt.Font subFont;
    private java.awt.Color redColor;
    private java.awt.Color goldColor;
    private java.awt.Color shadowColor;

    private boolean enInicio = true;

    public boolean isResetRequerido() {
        return resetRequerido;
    }

    public void setResetRequerido(boolean resetRequerido) {
        this.resetRequerido = resetRequerido;
    }

    public Juego() {
        try {
            java.io.InputStream is = getClass().getResourceAsStream("/res/Goudy_Mediaeval_Regular.ttf");
            customFont = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, is);
        } catch (Exception e) {
            customFont = new java.awt.Font("Georgia", java.awt.Font.BOLD, 12);
            e.printStackTrace();
        }
        inicializar();
        pan = new PanelJuego(this);
        vta = new VtaJuego(pan);
        pan.requestFocus();
        comenzarJuego();
    }

    private void inicializar() {
        bgImg = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_IMG);
        fondoArboles = LoadSave.GetSpriteAtlas(LoadSave.FONDO_ARBOLES_IMG);
        fondoArboles2 = LoadSave.GetSpriteAtlas(LoadSave.FONDO2_ARBOLES_IMG);
        fondoArbolesPos = new int[8];
        fondoPiedras = LoadSave.GetSpriteAtlas(LoadSave.FONDO_PIEDRAS_IMG);
        inicializarObjetos();
        
        // INICIALIZAR FUENTES Y COLORES UNA SOLA VEZ
        titleFont = customFont.deriveFont(java.awt.Font.BOLD, (int) (48 * Juego.SCALE));
        subFont = customFont.deriveFont(java.awt.Font.PLAIN, (int) (16 * Juego.SCALE));
        redColor = new java.awt.Color(200, 50, 50);
        goldColor = new java.awt.Color(212, 175, 55);
        shadowColor = java.awt.Color.DARK_GRAY;

        player = new Jugador(250, 200, (int) (200 * SCALE), (int) (200 * SCALE));
        enemyManager = new EnemyManager();
        objectManager = new ObjectManager();
        levelMan = new LevelManager(this);
        player.loadLvlData(levelMan.currentLevel().getLvlData());
        reproductorAudio = new utils.AudioPlayer();
        reproductorAudio.reproducirMusica("pista_cueva.wav");
    }

    public void inicializarObjetos(){
        posteDuenos = LoadSave.GetSpriteAtlas(LoadSave.POSTE_DUENOS);
    }

    private void comenzarJuego() {
        start();
    }

    private void checkCloseToBorder() {
        int playerX = (int) player.getHitbox().x;
        int diff = playerX - xLvlOffset;
        if (diff > rightBorder)
            xLvlOffset += diff - rightBorder;
        else if (diff < leftBorder)
            xLvlOffset += diff - leftBorder;

        if (xLvlOffset > maxLvlOffsetX)
            xLvlOffset = maxLvlOffsetX;
        else if (xLvlOffset < 0)
            xLvlOffset = 0;
    }

    public void run() {
        double framePorTiempo = 1000000000.0 / FPS_SET;
        double updatePorTiempo = 1000000000.0 / UPS_SET;
        int update = 0;
        int frame = 0;
        long previusTime = System.nanoTime();
        double deltaU = 0, deltaF = 0;
        long lastCheck = System.currentTimeMillis();
        
        while (true) {
            long currentTime = System.nanoTime();
            deltaU += (currentTime - previusTime) / updatePorTiempo;
            deltaF += (currentTime - previusTime) / framePorTiempo;
            previusTime = currentTime;
            
            if (deltaU >= 1) {
                update();
                update++;
                deltaU--;
            }
            if (deltaF >= 1) {
                pan.repaint();
                frame++;
                deltaF--;
            }
            if (System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
                System.out.println("FPS " + frame + " UPS " + update);
                frame = 0;
                update = 0;
            }
            
            if (deltaU < 1 && deltaF < 1) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void update() {
        if (resetRequerido) {
            victoria = false;
            gameOver = false;
            enInicio = false;
            reiniciarJuego();
            resetRequerido = false;
            return;
        }

        if (player.isReadyToRestart() && !gameOver) {
            gameOver = true;
            reproductorAudio.reproducirMusica("pista_game_over.wav");
            return;
        }

        if (victoria || gameOver) {
            return;
        }
        if (enInicio) {
            if (player.isReadyToRestart()) {
                enInicio = false;
            }
            return;
        }
        if (enemyManager.todosDerrotados() && !victoria) {
            victoria = true;
            reproductorAudio.reproducirMusica("pista_victory.wav");
            return; 
        }
        player.update(enemyManager, objectManager);
        levelMan.update();
        enemyManager.update(levelMan.currentLevel().getLvlData(), player);
        objectManager.update();
        objectManager.checkPicking(player);
        objectManager.checkExplosionHit(player);
        checkCloseToBorder();
    }
    
    void dibujarGameOver(Graphics g) {
        g.setColor(java.awt.Color.BLACK);
        g.fillRect(0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT);
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
        g2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
                java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        String tituloText = "¡GAME OVER!";
        String subText = "[Presiona ENTER para reiniciar]";

        g2.setFont(titleFont);
        java.awt.FontMetrics metricsTitle = g2.getFontMetrics(titleFont);
        int xTitle = (Juego.GAME_WIDTH - metricsTitle.stringWidth(tituloText)) / 2;
        int yTitle = (Juego.GAME_HEIGHT / 2) - (metricsTitle.getHeight() / 2);

        g2.setFont(subFont);
        java.awt.FontMetrics metricsSub = g2.getFontMetrics(subFont);
        int ySub = yTitle + metricsTitle.getHeight() + (int) (20 * Juego.SCALE);
        int xSub = (Juego.GAME_WIDTH - metricsSub.stringWidth(subText)) / 2;

        g2.setFont(titleFont);
        g2.setColor(shadowColor);
        g2.drawString(tituloText, xTitle + 3, yTitle + 3);
        g2.setColor(redColor);
        g2.drawString(tituloText, xTitle, yTitle);
        
        g2.setFont(subFont);
        g2.setColor(java.awt.Color.WHITE);
        g2.drawString(subText, xSub, ySub);
    }
    void dibujarInicio(Graphics g) {
        g.setColor(java.awt.Color.BLACK);
        g.fillRect(0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT);
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
        g2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
                java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        String tituloText = "¡LIMPIA LA CUEVA!";
        String subText = "[Presiona ENTER para comenzar]";

        g2.setFont(titleFont);
        java.awt.FontMetrics metricsTitle = g2.getFontMetrics(titleFont);
        int xTitle = (Juego.GAME_WIDTH - metricsTitle.stringWidth(tituloText)) / 2;
        int yTitle = (Juego.GAME_HEIGHT / 2) - (metricsTitle.getHeight() / 2);

        g2.setFont(subFont);
        java.awt.FontMetrics metricsSub = g2.getFontMetrics(subFont);
        int xSub = (Juego.GAME_WIDTH - metricsSub.stringWidth(subText)) / 2;
        int ySub = yTitle + metricsTitle.getHeight() + (int) (20 * Juego.SCALE);

        g2.setFont(titleFont);
        g2.setColor(shadowColor);
        g2.drawString(tituloText, xTitle + 3, yTitle + 3);
        g2.setColor(goldColor);
        g2.drawString(tituloText, xTitle, yTitle);
        
        g2.setFont(subFont);
        g2.setColor(java.awt.Color.WHITE);
        g2.drawString(subText, xSub, ySub);
    }

    void render(Graphics g) {
        g.drawImage(bgImg, 0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT, null);
        drawArboles(g);
        levelMan.draw(g, xLvlOffset);
        drawObjetosRandom(g);
        objectManager.draw(g, xLvlOffset);
        player.render(g, xLvlOffset);
        enemyManager.draw(g, xLvlOffset);
        player.drawUI(g);
        
        if (gameOver)  {
            dibujarGameOver(g);
            return;
        }
        if (enInicio) {
            dibujarInicio(g);
            return;
        }
        
        if (victoria) {
            g.setColor(java.awt.Color.BLACK);
            g.fillRect(0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT);
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
            g2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            String tituloText = "¡VICTORIA CUEVA LIMPIA!";
            String subText = "[Presiona ENTER para reiniciar]";

            g2.setFont(titleFont);
            java.awt.FontMetrics metricsTitle = g2.getFontMetrics(titleFont);
            int xTitle = (Juego.GAME_WIDTH - metricsTitle.stringWidth(tituloText)) / 2;
            int yTitle = (Juego.GAME_HEIGHT / 2) - (metricsTitle.getHeight() / 2);

            g2.setFont(subFont);
            java.awt.FontMetrics metricsSub = g2.getFontMetrics(subFont);
            int xSub = (Juego.GAME_WIDTH - metricsSub.stringWidth(subText)) / 2;
            int ySub = yTitle + metricsTitle.getHeight() + (int) (20 * Juego.SCALE);

            g2.setFont(titleFont);
            g2.setColor(shadowColor);
            g2.drawString(tituloText, xTitle + 3, yTitle + 3);
            g2.setColor(goldColor);
            g2.drawString(tituloText, xTitle, yTitle);
            
            g2.setFont(subFont);
            g2.setColor(java.awt.Color.WHITE);
            g2.drawString(subText, xSub, ySub);
        }
    }
    
    private void drawObjetosRandom(Graphics g){
        g.drawImage(posteDuenos,(int)(270 * SCALE) - xLvlOffset, 
                (int)( 127* SCALE), (int)(POSTE_DUENOS_HEIGHT *.15*SCALE), 
                (int)(POSTE_DUENOS_HEIGHT*.15* SCALE), null);
    }

    private void drawArboles(Graphics g) {
        for (int i = 0; i < fondoArbolesPos.length; i++) {
            g.drawImage(fondoArboles, FONDO_ARBOLES_WIDTH * i - (int) (xLvlOffset * 0.4),
                    fondoArbolesPos[i], FONDO_ARBOLES_WIDTH, FONDO_ARBOLES_HEIGHT, null);
        }
        for (int i = 0; i < fondoArbolesPos.length; i++) {
            g.drawImage(fondoArboles2, FONDO_ARBOLES_WIDTH * i - (int) (xLvlOffset * 0.7),
                    fondoArbolesPos[i], FONDO_ARBOLES_WIDTH, FONDO_ARBOLES_HEIGHT, null);
        }
        for (int i = 0; i < 3; i++) {
            g.drawImage(fondoPiedras, i * FONDO_ARBOLES_WIDTH - (int) (xLvlOffset * 0.7),
                    (int) (Juego.SCALE), FONDO_ARBOLES_WIDTH, FONDO_ARBOLES_HEIGHT, null);
        }
    }

    public elementos.EnemyManager getEnemyManager() {
        return enemyManager;
    }

    public Jugador getPlayer() {
        return player;
    }

    public void windowFocusLost() {
        player.resetDirBoolean();
    }

    private void reiniciarJuego() {
        player.resetAll();
        enemyManager.resetAllEnemies();
        xLvlOffset = 0;
    }

    public void reiniciarDesdePantalla() {
        if (victoria || gameOver || enInicio) {
            resetRequerido = true;
            reproductorAudio.detenerMusica();
            reproductorAudio.reproducirMusica("pista_cueva.wav");
        }
    }

    public boolean isVictoria() {
        return victoria;
    }
    public boolean isGameOver() {
        return gameOver;
    }
    public boolean isEnInicio() {
        return enInicio;
    }
}