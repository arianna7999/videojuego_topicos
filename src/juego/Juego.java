package juego;

import elementos.jugador.Jugador;
import elementos.componentes.*;
import elementos.managers.EnemyManager;
import elementos.managers.ObjectManager;
import elementos.managers.ScoreManager;
import elementos.pantallas.MenuSeleccion;
import elementos.pantallas.PantallaVictoria;

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
    private ScoreManager scoreManager;
    private Iluminacion iluminacion;
    private InventarioUI inventarioUI;
    private MenuSeleccion menuSeleccion;
    private PantallaVictoria pantallaVictoria;

    private int xLvlOffset;
    private int leftBorder = (int) (0.2 * Juego.GAME_WIDTH);
    private int rightBorder = (int) (0.8 * Juego.GAME_WIDTH);
    private int lvlTileWide;
    private int maxLvlOffset;
    private int maxLvlOffsetX;

    private int yLvlOffset;
    private int topBorder = (int) (0.3 * Juego.GAME_HEIGHT);
    private int bottomBorder = (int) (0.7 * Juego.GAME_HEIGHT);
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
    private BufferedImage bgImg, fondo1_1, fondo1_2, fondo1_3, posteInicio, posteDuenos, fondo2_1, fondo2_2, fondo2_3,
            fondo3_2, fondo3_3, fondo3_4;

    private int[] fondoArbolesPos;
    private BufferedImage[] capasBosque;
    private BufferedImage fondo3_1;
    private Random rnd = new Random();
    private boolean victoria = false;
    private boolean gameOver = false;
    private volatile boolean resetRequerido = false;
    private utils.AudioPlayer reproductorAudio;

    private java.awt.Font customFont;

    private java.awt.Font titleFont;
    private java.awt.Font subFont;
    private java.awt.Color redColor;
    private java.awt.Color shadowColor;

    private boolean enInicio = true;
    private boolean enSeleccion = false;

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
        menuSeleccion = new MenuSeleccion(this);
        pantallaVictoria = new PantallaVictoria(this);
        iluminacion = new Iluminacion();
        inventarioUI = new InventarioUI();
        scoreManager = new ScoreManager();
        titleFont = customFont.deriveFont(java.awt.Font.BOLD, (int) (48 * Juego.SCALE));
        subFont = customFont.deriveFont(java.awt.Font.PLAIN, (int) (16 * Juego.SCALE));
        redColor = new java.awt.Color(200, 50, 50);
        shadowColor = java.awt.Color.DARK_GRAY;

        reproductorAudio = new utils.AudioPlayer();
        player = new Jugador(250, 200, (int) (200 * SCALE), (int) (200 * SCALE), reproductorAudio);
        enemyManager = new EnemyManager();
        objectManager = new ObjectManager(reproductorAudio);
        enemyManager.setScoreManager(scoreManager);

        levelMan = new LevelManager(this);
        calcularCameraOffset();
        player.loadLvlData(levelMan.currentLevel().getLvlData(), levelMan.getLevelIndex());
        objectManager.cargarObjetosDeNivel(levelMan.getLevelIndex());
        enInicio = false;
        enSeleccion = true;
        cargarPista(5);
        player.setSpawn(levelMan.getLevelIndex());
    }

    public void inicializarObjetos() {
        posteDuenos = LoadSave.GetSpriteAtlas(LoadSave.POSTE_DUENOS);
    }

    private void comenzarJuego() {
        start();
    }

    private void inicializarFondosDeMundos() {
        // mundo 1
        bgImg = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_IMG);
        fondo1_1 = LoadSave.GetSpriteAtlas(LoadSave.FONDO_ARBOLES_IMG);
        fondo1_2 = LoadSave.GetSpriteAtlas(LoadSave.FONDO2_ARBOLES_IMG);
        fondo1_3 = LoadSave.GetSpriteAtlas(LoadSave.FONDO_PIEDRAS_IMG);

        // mundo 2
        fondo2_1 = LoadSave.GetSpriteAtlas(LoadSave.FONDO_CASTLE_IMG_1);
        fondo2_2 = LoadSave.GetSpriteAtlas(LoadSave.FONDO_CASTLE_IMG_2);
        fondo2_3 = LoadSave.GetSpriteAtlas(LoadSave.FONDO_CASTLE_IMG_3);

        // mundo3
        // mundo3
        fondo3_1 = LoadSave.GetSpriteAtlas(LoadSave.FONDO_MUNDO_3_CIELO);
        fondo3_2 = LoadSave.GetSpriteAtlas(LoadSave.FONDO_MUNDO_3_MONTANAS);
        fondo3_4 = LoadSave.GetSpriteAtlas(LoadSave.FONDO_MUNDO_3_SOMBRA);
        fondo3_3 = LoadSave.GetSpriteAtlas(LoadSave.FONDO_MUNDO_3_PIEDRAS);

        capasBosque = new BufferedImage[4];
        capasBosque[0] = LoadSave.GetSpriteAtlas(LoadSave.BOSQUE_FONDO1);
        capasBosque[1] = LoadSave.GetSpriteAtlas(LoadSave.BOSQUE_FONDO2);
        capasBosque[2] = LoadSave.GetSpriteAtlas(LoadSave.BOSQUE_FONDO4);
        capasBosque[3] = LoadSave.GetSpriteAtlas(LoadSave.BOSQUE_FONDO3);

        System.out.println("=== CAPAS BOSQUE ===");
        for (int i = 0; i < capasBosque.length; i++) {
            System.out.println("capa " + i + ": "
                    + (capasBosque[i] == null ? "NULL" : capasBosque[i].getWidth() + "x" + capasBosque[i].getHeight()));
        }

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
        if (levelMan == null || levelMan.currentLevel() == null)
            return;

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
            player.vaciarInventario();
            reproductorAudio.reproducirMusica("pista_game_over.wav");
            return;
        }

        if (victoria || gameOver) {
            player.vaciarInventario();
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
        player.update(enemyManager, objectManager, levelMan.getLevelIndex());
        levelMan.update();
        enemyManager.update(levelMan.currentLevel().getLvlData(), player, levelMan.getLevelIndex());
        objectManager.update(levelMan.getLevelIndex());
        objectManager.actualizarJugadorEnPlataforma(player);
        objectManager.checkPuertaInteraccion(player, levelMan.getLevelIndex());
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

    void render(Graphics g) {
        g.drawImage(bgImg, 0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT, null);
        drawFondoMundo3_Cielo(g);
        drawFondos(g);
      levelMan.draw(g, xLvlOffset, yLvlOffset); // Mapa de fondo[cite: 1, 2]

    // 1. Dibujar objetos normales (barriles, cofres, plataformas) detrás del player
    objectManager.draw(g, xLvlOffset, yLvlOffset, levelMan.getLevelIndex());

    // 2. Dibujar enemigos y al jugador
    enemyManager.draw(g, xLvlOffset, yLvlOffset);
    player.render(g, xLvlOffset, yLvlOffset); // El jugador se dibuja aquí[cite: 1, 2]

    // 3. Dibujar EXCLUSIVAMENTE las rejas/puertas después del player
    // Esto hace que REJASM3 se sobreponga al personaje
    objectManager.drawPuertasYRejas(g, xLvlOffset, yLvlOffset, levelMan.getLevelIndex());

    if (levelMan.getLevelIndex() == 3) {
        iluminacion.draw(g, player, xLvlOffset, yLvlOffset);
    }

        if (gameOver) {
            dibujarGameOver(g);
            return;
        }
        if (enSeleccion) {
            menuSeleccion.dibujar(g);
            return;
        }
        if (victoria) {
            pantallaVictoria.dibujar(g);
            return;
        }
        
        player.drawUI(g);
        inventarioUI.draw(g, player);
    }

    private void drawObjetosRandom(Graphics g, BufferedImage[] objetos, float[] posicionesX, float[] posicionesY,
            float[] alturas, float[] anchos) {
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
                drawFondoLevel(g, new BufferedImage[] { fondo1_1, fondo1_2, fondo1_3 }, Juego.GAME_WIDTH,
                        Juego.GAME_HEIGHT);
                drawObjetosRandom(g,
                        new BufferedImage[] { posteDuenos },
                        new float[] { 170f },
                        new float[] { 190f },
                        new float[] { (float) (POSTE_DUENOS_HEIGHT * 0.15) },
                        new float[] { (float) (POSTE_DUENOS_HEIGHT * 0.15) });
                break;
            case 1:
                drawFondoNivel2(g);
                System.out.println("Capas bosque en nivel 2: " + (capasBosque[0] != null) + ", " + (capasBosque[1] != null) + ", "
                        + (capasBosque[2] != null) + ", " + (capasBosque[3] != null));
                break;  
            case 2:
                drawFondoLevel(g, new BufferedImage[] { fondo3_2, fondo3_4, fondo3_3 }, Juego.GAME_WIDTH,
                        Juego.GAME_HEIGHT);
                break;
            case 3:
                drawFondoLevel(g, new BufferedImage[] { fondo2_1, fondo2_2 }, Juego.GAME_WIDTH, Juego.GAME_HEIGHT);
                break;
            default:
                break;
        }

    }

    private void drawFondoLevel(Graphics g, BufferedImage[] fondos, int width, int height) {
        for (BufferedImage fondo : fondos) {
            for (int i = 0; i < 5; i++) {
                g.drawImage(fondo,
                        width * i - (int) (xLvlOffset * 0.4),
                        0,
                        width,
                        height,
                        null);
            }
        }
    }

    private void drawFondoMundo3_Cielo(Graphics g) {
        if (levelMan.getLevelIndex() != 2 || fondo3_1 == null)
            return;

        int anchoCielo = Juego.GAME_WIDTH;
        int altoCielo = Juego.GAME_HEIGHT;

        for (int i = 0; i < 5; i++) {
            g.drawImage(fondo3_1,
                    anchoCielo * i - (int) (xLvlOffset * 0.6),
                    0,
                    anchoCielo,
                    altoCielo,
                    null);
        }
    }

    private void drawFondoNivel2(Graphics g) {
        int W = Juego.GAME_WIDTH;
        int H = Juego.GAME_HEIGHT;

        // Si por alguna razon capasBosque es null, poner fondo verde oscuro
        if (capasBosque == null) {
            g.setColor(new java.awt.Color(20, 40, 20));
            g.fillRect(0, 0, W, H);
            return;
        }

        // Capa 0: cielo - estatico, sin parallax
        if (capasBosque[0] != null) {
            g.drawImage(capasBosque[0], 0, 0, W, H, null);
        } else {
            g.setColor(new java.awt.Color(15, 10, 30));
            g.fillRect(0, 0, W, H);
        }

        // Capa 1: montanyas - parallax lento (0.1)
        if (capasBosque[1] != null) {
            int xPos = (int) (-(xLvlOffset * 0.1f)) % W;
            if (xPos > 0)
                xPos -= W;
            g.drawImage(capasBosque[1], xPos, 0, W, H, null);
            g.drawImage(capasBosque[1], xPos + W, 0, W, H, null);
        }

        // Capa 2: arboles lejanos - parallax medio (0.35)
        if (capasBosque[2] != null) {
            int xPos = (int) (-(xLvlOffset * 0.35f)) % W;
            if (xPos > 0)
                xPos -= W;
            g.drawImage(capasBosque[2], xPos, 0, W, H, null);
            g.drawImage(capasBosque[2], xPos + W, 0, W, H, null);
        }

        // Capa 3: arboles cercanos - parallax rapido (0.7)
        if (capasBosque[3] != null) {
            int xPos = (int) (-(xLvlOffset * 0.7f)) % W;
            if (xPos > 0)
                xPos -= W;
            g.drawImage(capasBosque[3], xPos, 0, W, H, null);
            g.drawImage(capasBosque[3], xPos + W, 0, W, H, null);
        }
    }

    public elementos.managers.EnemyManager getEnemyManager() {
        return enemyManager;
    }

    public Jugador getPlayer() {
        return player;
    }

    public void windowFocusLost() {
        player.resetDirBoolean();
    }

    private void reiniciarJuego() {
        player.resetAll(levelMan.getLevelIndex());
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

    public void cargarPista(int levelIndex) {
        reproductorAudio.detenerMusica();
        switch (levelIndex) {
            case 0:
                reproductorAudio.reproducirMusica("pista_cueva.wav");
                break;
            case 1:
                reproductorAudio.reproducirMusica("castle_soundtrack.wav");
                break;
            case 2:
                reproductorAudio.reproducirMusica("desert_soundtrack.wav");
                System.out.println("Cargando pista del desierto");
                break;
            case 3:
                reproductorAudio.reproducirMusica("castle_soundtrack.wav");
                break;
            case 4:
                reproductorAudio.reproducirMusica("pista_victory.wav");
                break;
            case 5:
                reproductorAudio.reproducirMusica("soundtrack.wav");
                System.out.println("Cargando pista de selección");
                break;
            default:
                reproductorAudio.reproducirMusica("pista_cueva.wav");
                break;
        }
    }

    public void cargarSiguienteNivel() {
        levelMan.loadNextLevel();

        if (levelMan.getLevelIndex() >= levelMan.getAmountOfLevels()) {
            victoria = true;
            cargarPista(4);
        } else {
            player.loadLvlData(levelMan.currentLevel().getLvlData(), levelMan.getLevelIndex());
            player.setSpawn(levelMan.getLevelIndex());
            calcularCameraOffset();
            player.resetAll(levelMan.getLevelIndex());
            xLvlOffset = 0;
            yLvlOffset = 0;
            cargarPista(levelMan.getLevelIndex());
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

    public boolean isEnSeleccion() {
        return enSeleccion;
    }

    public void irASeleccion() {
        victoria = false;
        gameOver = false;
        enInicio = false;
        enSeleccion = true;
        player.resetAll(levelMan.getLevelIndex());
        enemyManager.resetAllEnemies(levelMan.getLevelIndex());
        levelMan.resetToFirstLevel();
        player.loadLvlData(levelMan.currentLevel().getLvlData(), levelMan.getLevelIndex());
        calcularCameraOffset();
        objectManager.cargarObjetosDeNivel(levelMan.getLevelIndex());
        xLvlOffset = 0;
        yLvlOffset = 0;
        reproductorAudio.detenerMusica();
        cargarPista(5);
    }

    public void aplicarPersonajeElegido(elementos.jugador.Personaje personajeElegido) {
        levelMan.resetToFirstLevel();

        player.setPersonaje(personajeElegido);
        player.setSpawn(levelMan.getLevelIndex());

        player.resetAll(levelMan.getLevelIndex());
        player.loadLvlData(levelMan.currentLevel().getLvlData(), levelMan.getLevelIndex());
        calcularCameraOffset();
        enemyManager.resetAllEnemies(levelMan.getLevelIndex());
        objectManager.cargarObjetosDeNivel(levelMan.getLevelIndex());

        xLvlOffset = 0;
        yLvlOffset = 0;
        victoria = false;
        gameOver = false;
        enSeleccion = false;
        enInicio = false;

        reproductorAudio.detenerMusica();
        cargarPista(levelMan.getLevelIndex());
    }

    public MenuSeleccion getMenuSeleccion() {
        return menuSeleccion;
    }
    public ScoreManager getScoreManager() {
        return scoreManager;
    }
}
