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
    private int lvlTileWide;
    private int maxLvlOffset;
    private int maxLvlOffsetX;

    private int yLvlOffset;
    private int topBorder = (int) (0.3 * Juego.GAME_HEIGHT); // Borde superior (30% de la pantalla)
    private int bottomBorder = (int) (0.7 * Juego.GAME_HEIGHT); // Borde inferior (70% de la pantalla)
    private int lvlTileHeight;
    private int maxLvlOffsetY_tiles;
    private int maxLvlOffsetY;
    public final static int TILES_DEF_SIZE = 32;
    public final static float SCALE = 1.5f;
    public final static int TILES_WIDTH = 26;
    public final static int TILES_HEIGHT = 14;
    public final static int TILES_SIZE = (int) (TILES_DEF_SIZE * SCALE);
    public final static int GAME_WIDTH = TILES_SIZE * TILES_WIDTH;
    public final static int GAME_HEIGHT = TILES_SIZE * TILES_HEIGHT;
    private BufferedImage bgImg, fondo1_1, fondo1_2, fondo1_3, posteInicio, posteDuenos, fondo2_1, fondo2_2,fondo3_1,fondo3_2,fondo3_3;
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
    private boolean enSeleccion = false;
    private int seleccionIndice = 0;

    // Personajes disponibles: nombre, vida, vel, daño, salto, sprite, cols, filas, celdaW, celdaH, habilidad, drawOffX, drawOffY, tiempoParaCurar, cantCura
    private final elementos.Personaje[] PERSONAJES = {
        new elementos.Personaje("Hank",   120, 2.0f, 25, -2.25f, "Soldier.png", 9, 7, 100, 100, "golpe_brutal", 87f, 80f, 500, 3),
        new elementos.Personaje("Frank",  200, 1.5f, 20, -2.0f,  "Frank.png",   9, 7, 100, 100, "coraza",       87f, 80f, 400, 4),
        new elementos.Personaje("Saori",   70, 3.0f,  8, -2.6f,  "Saori.png",   9, 7, 100, 100, "robo_vida",    87f, 80f, 180, 6),
        new elementos.Personaje("Lucerys", 80, 1.4f, 20, -2.4f,  "Lucerys.png", 9, 7, 100, 100, "lluvia",       87f, 80f, 400, 3)
    };

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
        inicializarFondosDeMundos();
        fondoArbolesPos = new int[8];
        inicializarObjetos();
        titleFont = customFont.deriveFont(java.awt.Font.BOLD, (int) (48 * Juego.SCALE));
        subFont = customFont.deriveFont(java.awt.Font.PLAIN, (int) (16 * Juego.SCALE));
        redColor = new java.awt.Color(200, 50, 50);
        goldColor = new java.awt.Color(212, 175, 55);
        shadowColor = java.awt.Color.DARK_GRAY;
        
        reproductorAudio = new utils.AudioPlayer();
        
        player = new Jugador(250, 200, (int) (200 * SCALE), (int) (200 * SCALE), reproductorAudio);
        enemyManager = new EnemyManager();
        objectManager = new ObjectManager(reproductorAudio);
        
        levelMan = new LevelManager(this);
        calcularCameraOffset();
        player.loadLvlData(levelMan.currentLevel().getLvlData());
        objectManager.cargarObjetosDeNivel(levelMan.getLevelIndex());
        enInicio    = false;
        enSeleccion = true;
        // La música se inicia al confirmar personaje
    }

    public void inicializarObjetos(){
        posteDuenos = LoadSave.GetSpriteAtlas(LoadSave.POSTE_DUENOS);
    }

    private void comenzarJuego() {
        start();
    }
    private void inicializarFondosDeMundos(){
        //mundo 1
        bgImg = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_IMG);
        fondo1_1 = LoadSave.GetSpriteAtlas(LoadSave.FONDO_ARBOLES_IMG);
        fondo1_2 = LoadSave.GetSpriteAtlas(LoadSave.FONDO2_ARBOLES_IMG);
        fondo1_3 = LoadSave.GetSpriteAtlas(LoadSave.FONDO_PIEDRAS_IMG);
        
        //mundo 2
        fondo2_1 = LoadSave.GetSpriteAtlas(LoadSave.FONDO_CASTLE_IMG_1);
        fondo2_2 = LoadSave.GetSpriteAtlas(LoadSave.FONDO_CASTLE_IMG_2);
       
        //mundo3
         fondo3_1 = LoadSave.GetSpriteAtlas(LoadSave.FONDO_MUNDO_3_CIELO);
         fondo3_2 = LoadSave.GetSpriteAtlas(LoadSave.FONDO_MUNDO_3_MONTANAS);
         fondo3_3 = LoadSave.GetSpriteAtlas(LoadSave.FONDO_MUNDO_3_PIEDRAS);

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

        int playerY = (int) player.getHitbox().y;
        int diffY = playerY - yLvlOffset;
        
        if (diffY > bottomBorder)
            yLvlOffset += diffY - bottomBorder;
        else if (diffY < topBorder)
            yLvlOffset += diffY - topBorder;

        if (yLvlOffset > maxLvlOffsetY)
            yLvlOffset = maxLvlOffsetY;
        else if (yLvlOffset < 0)
            yLvlOffset = 0;
    }
    private void calcularCameraOffset() {
        if (levelMan == null || levelMan.currentLevel() == null) return;
        
        lvlTileWide = levelMan.currentLevel().getLvlData()[0].length;
        maxLvlOffset = lvlTileWide - Juego.TILES_WIDTH;
        maxLvlOffsetX = maxLvlOffset * Juego.TILES_SIZE;

        lvlTileHeight = levelMan.currentLevel().getLvlData().length;
        maxLvlOffsetY_tiles = lvlTileHeight - Juego.TILES_HEIGHT;
        maxLvlOffsetY = maxLvlOffsetY_tiles * Juego.TILES_SIZE;
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
        if (enSeleccion) {
            return;
        }
        if (enInicio) {
            if (player.isReadyToRestart()) {
                enInicio = false;
            }
            return;
        }
        if (enemyManager.todosDerrotados() && !victoria) {
            cargarSiguienteNivel();
            return; 
        }
        player.update(enemyManager, objectManager);
        levelMan.update();
        enemyManager.update(levelMan.currentLevel().getLvlData(), player);
        objectManager.update();
        objectManager.actualizarJugadorEnPlataforma(player);
        objectManager.checkPuertaInteraccion(player);
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
        drawFondos(g);
        levelMan.draw(g, xLvlOffset, yLvlOffset);
        // drawObjetosRandom(g);
        objectManager.draw(g, xLvlOffset, yLvlOffset);
        player.render(g, xLvlOffset, yLvlOffset);
        enemyManager.draw(g, xLvlOffset, yLvlOffset);
        player.drawUI(g);
        
        if (gameOver)  {
            dibujarGameOver(g);
            return;
        }
        if (enSeleccion) {
            dibujarSeleccion(g);
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
    
    private void drawObjetosRandom(Graphics g, BufferedImage[] objetos, float[] posicionesX, float[] posicionesY, float[] alturas, float[] anchos) {
       for (int i = 0; i < objetos.length; i++) {
            int x = (int) (posicionesX[i] * SCALE) - xLvlOffset;
            int y = (int) (posicionesY[i] * SCALE);
            int width = (int) (anchos[i] * SCALE);
            int height = (int) (alturas[i] * SCALE);
            
            g.drawImage(objetos[i], x, y, width, height, null);
        }
    }

    private void drawFondos(Graphics g) {

        switch (levelMan.getLevelIndex()) {
            case 0:
                drawFondoLevel(g, new BufferedImage[]{fondo1_1, fondo1_2, fondo1_3});
                drawObjetosRandom(g, 
                    new BufferedImage[]{posteDuenos}, 
                    new float[]{170f},
                    new float[]{190f}, 
                    new float[]{(float)(POSTE_DUENOS_HEIGHT * 0.15)},
                    new float[]{(float)(POSTE_DUENOS_HEIGHT * 0.15)}
                );
                break;
            case 1:
                drawFondoLevel(g, new BufferedImage[]{fondo2_1, fondo2_2});
                break;
            case 2:
                drawFondoLevel(g, new BufferedImage[]{fondo3_1, fondo3_2,fondo3_3});    
            default:
                break;
        }

    }
    private void drawFondoLevel(Graphics g, BufferedImage [] fondos) {

        for (BufferedImage fondo: fondos) {
            for (int i = 0; i < fondoArbolesPos.length; i++) {
            g.drawImage(fondo, FONDO_ARBOLES_WIDTH * i - (int) (xLvlOffset * 0.4),
                    fondoArbolesPos[i], FONDO_ARBOLES_WIDTH, FONDO_ARBOLES_HEIGHT, null);
        }
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
        enemyManager.resetAllEnemies(levelMan.getLevelIndex());
        objectManager.cargarObjetosDeNivel(levelMan.getLevelIndex());
        xLvlOffset = 0;
        yLvlOffset = 0;
    }

    public void reiniciarDesdePantalla() {
        if (victoria || gameOver || enInicio || enSeleccion) {
            irASeleccion();
        }
    }

    public void cargarSiguienteNivel() {
        levelMan.loadNextLevel();
        
        if (levelMan.getLevelIndex() >= levelMan.getAmountOfLevels()) {
            victoria = true;
            reproductorAudio.reproducirMusica("pista_victory.wav");
        } else {
            player.loadLvlData(levelMan.currentLevel().getLvlData());
            calcularCameraOffset();
            player.resetAll(); 
            xLvlOffset = 0;
            yLvlOffset = 0;
            enemyManager.resetAllEnemies(levelMan.getLevelIndex());
            objectManager.cargarObjetosDeNivel(levelMan.getLevelIndex());
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

    public boolean isEnSeleccion() { return enSeleccion; }
    public int getSeleccionIndice() { return seleccionIndice; }

    public void moverSeleccion(int delta) {
        seleccionIndice = (seleccionIndice + delta + PERSONAJES.length) % PERSONAJES.length;
    }

    public void confirmarSeleccion() {
       
        levelMan.resetToFirstLevel();
        // 2. Aplicar el personaje elegido
        player.setPersonaje(PERSONAJES[seleccionIndice]);
        // 3. Resetear estado del jugador y enemigos
        player.resetAll();
        player.loadLvlData(levelMan.currentLevel().getLvlData());
        calcularCameraOffset();
        enemyManager.resetAllEnemies(levelMan.getLevelIndex());
        objectManager.cargarObjetosDeNivel(levelMan.getLevelIndex());
        xLvlOffset = 0;
        yLvlOffset = 0;
        victoria   = false;
        gameOver   = false;
        enSeleccion = false;
        enInicio    = false;
        reproductorAudio.detenerMusica();
        reproductorAudio.reproducirMusica("pista_cueva.wav");
    }

    public void irASeleccion() {
        victoria    = false;
        gameOver    = false;
        enInicio    = false;
        enSeleccion = true;
        player.resetAll();
        enemyManager.resetAllEnemies(levelMan.getLevelIndex());
        levelMan.resetToFirstLevel();
        player.loadLvlData(levelMan.currentLevel().getLvlData());
        calcularCameraOffset();
        objectManager.cargarObjetosDeNivel(levelMan.getLevelIndex());
        xLvlOffset = 0;
        yLvlOffset = 0;
        reproductorAudio.detenerMusica();
    }

    void dibujarSeleccion(Graphics g) {
        g.setColor(java.awt.Color.BLACK);
        g.fillRect(0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT);
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
        g2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
                java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        java.awt.Font selTitleFont = customFont.deriveFont(java.awt.Font.BOLD, (int)(32 * Juego.SCALE));
        java.awt.Font selNameFont  = customFont.deriveFont(java.awt.Font.BOLD, (int)(20 * Juego.SCALE));
        java.awt.Font selStatFont  = customFont.deriveFont(java.awt.Font.PLAIN, (int)(11 * Juego.SCALE));
        java.awt.Font selHintFont  = customFont.deriveFont(java.awt.Font.PLAIN, (int)(13 * Juego.SCALE));

        g2.setFont(selTitleFont);
        String titulo = "ELIGE TU PERSONAJE";
        java.awt.FontMetrics fmT = g2.getFontMetrics();
        g2.setColor(new java.awt.Color(212, 175, 55));
        g2.drawString(titulo, (Juego.GAME_WIDTH - fmT.stringWidth(titulo)) / 2, (int)(60 * Juego.SCALE));

        int cardW  = (int)(90 * Juego.SCALE);
        int cardH  = (int)(110 * Juego.SCALE);
        int gap    = (int)(20 * Juego.SCALE);
        int totalW = PERSONAJES.length * cardW + (PERSONAJES.length - 1) * gap;
        int startX = (Juego.GAME_WIDTH - totalW) / 2;
        int cardY  = (int)(90 * Juego.SCALE);

        int[][] stats = { {100,20,15,22}, {160,15,25,20}, {70,30,12,26}, {80,22,30,24} };
        String[] roles = {"Guerrero", "Tanque", "Veloz", "Mago"};
        java.awt.Color[] roleColors = {
            new java.awt.Color(100,180,255), new java.awt.Color(100,220,100),
            new java.awt.Color(255,180,60),  new java.awt.Color(200,100,255)
        };

        for (int i = 0; i < PERSONAJES.length; i++) {
            int cx = startX + i * (cardW + gap);
            boolean sel = (i == seleccionIndice);
            if (sel) {
                g2.setColor(new java.awt.Color(60, 50, 20));
                g2.fillRoundRect(cx - 4, cardY - 4, cardW + 8, cardH + 8, 16, 16);
                g2.setColor(goldColor);
                g2.setStroke(new java.awt.BasicStroke(3));
                g2.drawRoundRect(cx - 4, cardY - 4, cardW + 8, cardH + 8, 16, 16);
            } else {
                g2.setColor(new java.awt.Color(30, 30, 40));
                g2.fillRoundRect(cx, cardY, cardW, cardH, 12, 12);
                g2.setColor(new java.awt.Color(80, 80, 100));
                g2.setStroke(new java.awt.BasicStroke(1.5f));
                g2.drawRoundRect(cx, cardY, cardW, cardH, 12, 12);
            }
            g2.setFont(selNameFont);
            java.awt.FontMetrics fmN = g2.getFontMetrics();
            g2.setColor(sel ? goldColor : java.awt.Color.WHITE);
            String nombre = PERSONAJES[i].nombre;
            g2.drawString(nombre, cx + (cardW - fmN.stringWidth(nombre)) / 2, cardY + (int)(22 * Juego.SCALE));
            g2.setFont(selStatFont);
            java.awt.FontMetrics fmS = g2.getFontMetrics();
            g2.setColor(roleColors[i]);
            String rol = "[" + roles[i] + "]";
            g2.drawString(rol, cx + (cardW - fmS.stringWidth(rol)) / 2, cardY + (int)(34 * Juego.SCALE));

            String[] statLabels = {"VID","VEL","DAÑ","SAL"};
            int[] statVals = stats[i];
            int[] statMax  = {160,30,30,26};
            java.awt.Color[] barColors = {
                new java.awt.Color(220,80,80), new java.awt.Color(80,200,120),
                new java.awt.Color(255,160,40), new java.awt.Color(80,160,255)
            };
            int barY = cardY + (int)(44 * Juego.SCALE);
            int barAreaW  = cardW - (int)(20 * Juego.SCALE);
            int barH2     = (int)(7 * Juego.SCALE);
            int barSpacing = (int)(16 * Juego.SCALE);
            for (int s = 0; s < 4; s++) {
                int by = barY + s * barSpacing;
                g2.setColor(new java.awt.Color(180,180,180));
                g2.drawString(statLabels[s], cx + (int)(6 * Juego.SCALE), by + barH2);
                int bx = cx + (int)(26 * Juego.SCALE);
                int bw = barAreaW - (int)(20 * Juego.SCALE);
                g2.setColor(new java.awt.Color(50,50,60));
                g2.fillRoundRect(bx, by, bw, barH2, 4, 4);
                int fill = (int)((float) statVals[s] / statMax[s] * bw);
                g2.setColor(barColors[s]);
                g2.fillRoundRect(bx, by, fill, barH2, 4, 4);
            }
        }
        g2.setFont(selTitleFont);
        g2.setColor(new java.awt.Color(212,175,55,180));
        int arrowY = cardY + cardH / 2 + (int)(10 * Juego.SCALE);
        g2.drawString("<", startX - (int)(30 * Juego.SCALE), arrowY);
        g2.drawString(">", startX + totalW + (int)(10 * Juego.SCALE), arrowY);
        g2.setFont(selHintFont);
        java.awt.FontMetrics fmH = g2.getFontMetrics();
        String hint = "LEFT/RIGHT para navegar    ENTER para confirmar";
        g2.setColor(new java.awt.Color(180,180,180));
        g2.drawString(hint, (Juego.GAME_WIDTH - fmH.stringWidth(hint)) / 2, cardY + cardH + (int)(30 * Juego.SCALE));
    }
}