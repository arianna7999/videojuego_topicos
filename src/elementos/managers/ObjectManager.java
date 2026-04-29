package elementos.managers;

import static utils.Constantes.ConstantesObjetos.*;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import elementos.jugador.Jugador;
import elementos.objetos.Aguila;
import elementos.objetos.BotonGolpeable;
import elementos.objetos.Candelabro;
import elementos.objetos.Contenedor;
import elementos.objetos.MaquinaGolpeable;
import elementos.objetos.PlataformaMovil;
import elementos.objetos.PuertaMovil;
import elementos.objetos.Recompensas;
import juego.Juego;
import utils.Constantes;
import utils.LoadSave;

public class ObjectManager {

    private ArrayList<Contenedor> contenedores = new ArrayList<>();
    private ArrayList<Recompensas> recompensas = new ArrayList<>();
    private ArrayList<PlataformaMovil> plataformas = new ArrayList<>();
    private ArrayList<PuertaMovil> puertas = new ArrayList<>();
    private ArrayList<Candelabro> candelabros = new ArrayList<>();
    private ArrayList<Aguila> aguilas = new ArrayList<>();
    private java.util.ArrayList<MaquinaGolpeable> maquinas = new java.util.ArrayList<>();
    private java.awt.image.BufferedImage[][] maquinaSprites;
    private java.util.ArrayList<BotonGolpeable> botones = new java.util.ArrayList<>();
    private java.awt.image.BufferedImage botonImg;

    private utils.AudioPlayer audioPlayer;

    private BufferedImage plataformaImg;
    private BufferedImage puertaImg, piramides, craneoSimpleImg, craneoCuernosImg;
    private BufferedImage[] aguilaImgs;
    private BufferedImage[] barrilImgs;
    private BufferedImage[] cofreImgs;
    private BufferedImage[] corazonImgs;
    private BufferedImage[] explosionImgs;
    private BufferedImage[] llaveImgs;
    private BufferedImage[] candelabroImgs;
    private BufferedImage[] marFuegoSprites;

    private ArrayList<java.awt.geom.Rectangle2D.Float> aguas = new ArrayList<>();
    private ArrayList<java.awt.geom.Rectangle2D.Float> picos = new ArrayList<>();

    private BufferedImage[] marSprites;
    private BufferedImage plataformaDesiertoImg;
    private int marAniTick = 0;
    private int marAniIndex = 0;
    private int marAniSpeed = 30;

    public ObjectManager(utils.AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        cargarSprites();
    }

    private void cargarSprites() {
        BufferedImage img = LoadSave.GetSpriteAtlas("objetos-sprite.png");
        barrilImgs = new BufferedImage[8];
        cofreImgs = new BufferedImage[8];
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.AGUILA_SPRITE);
        aguilaImgs = new BufferedImage[6]; // 6 fotogramas
        piramides = utils.LoadSave.GetSpriteAtlas("piramide.png");
        for (int i = 0; i < aguilaImgs.length; i++) {
            // Cortamos la imagen (1 fila x 6 columnas)
            // Asegúrate de que Constantes.Ambiente.AGUILA_WIDTH_DEFAULT sea el tamaño
            // correcto
            aguilaImgs[i] = temp.getSubimage(i * Constantes.Ambiente.AGUILA_WIDTH_DEFAULT, 0,
                    Constantes.Ambiente.AGUILA_WIDTH_DEFAULT,
                    Constantes.Ambiente.AGUILA_HEIGHT_DEFAULT);
        }
        try {
            botonImg = utils.LoadSave.GetSpriteAtlas("imagen_boton.png");
        } catch (Exception e) {
            System.out.println("No se encontró imagen_boton.png");
        }

        aguilas.add(new Aguila(100, 50));
        aguilas.add(new Aguila(150, 120));
        for (int frame = 0; frame < 8; frame++) {
            cofreImgs[frame] = img.getSubimage(frame * 40, 0, 40, 30);
            barrilImgs[frame] = img.getSubimage(frame * 40, 30, 40, 30);
        }

        plataformaImg = LoadSave.GetSpriteAtlas("plataforma-movible.png");
        plataformaDesiertoImg = LoadSave.GetSpriteAtlas("plataformaDesierto.png");
        puertaImg = utils.LoadSave.GetSpriteAtlas("pared-abrir.png");

        BufferedImage imgCorazon = LoadSave.GetSpriteAtlas("sprite-corazon.png");
        corazonImgs = new BufferedImage[12];
        int anchoCorazon = imgCorazon.getWidth() / 12;
        for (int i = 0; i < corazonImgs.length; i++) {
            corazonImgs[i] = imgCorazon.getSubimage(i * anchoCorazon, 0, anchoCorazon, imgCorazon.getHeight());
        }

        BufferedImage imgExplosion = LoadSave.GetSpriteAtlas("explosion.png");
        explosionImgs = new BufferedImage[8];
        for (int i = 0; i < explosionImgs.length; i++) {
            explosionImgs[i] = imgExplosion.getSubimage(i * 256, 0, 256, 336);
        }

        BufferedImage imgCompletaLlave = utils.LoadSave.GetSpriteAtlas("llave.png");
        llaveImgs = new BufferedImage[7];
        int anchoUnFrameLlave = imgCompletaLlave.getWidth() / 7;
        for (int i = 0; i < llaveImgs.length; i++) {
            llaveImgs[i] = imgCompletaLlave.getSubimage(i * anchoUnFrameLlave, 0, anchoUnFrameLlave,
                    imgCompletaLlave.getHeight());
        }

        candelabroImgs = new BufferedImage[7];
        BufferedImage imgCompletaCandelabro = LoadSave.GetSpriteAtlas("candelabro_1.png");
        int anchoFrameCandelabro = imgCompletaCandelabro.getWidth() / 7;
        int altoFrameCandelabro = imgCompletaCandelabro.getHeight();
        for (int i = 0; i < candelabroImgs.length; i++) {
            candelabroImgs[i] = imgCompletaCandelabro.getSubimage(i * anchoFrameCandelabro, 0, anchoFrameCandelabro,
                    altoFrameCandelabro);
        }

        try {
            BufferedImage imgMar = utils.LoadSave.GetSpriteAtlas("mar.png");
            marSprites = new BufferedImage[3];
            for (int i = 0; i < 4; i++) {
                marSprites[i] = imgMar.getSubimage(i * 100, 0, 100, 100);
            }
        } catch (Exception e) {
            System.out.println("Aviso: No se encontró mar.png");
        }
        try {
            BufferedImage imgMar = utils.LoadSave.GetSpriteAtlas("marFuego.png");
            marFuegoSprites = new BufferedImage[4];
            for (int i = 0; i < 4; i++) {
                marSprites[i] = imgMar.getSubimage(i * 100, 0, 100, 100);
            }
        } catch (Exception e) {
            System.out.println("Aviso: No se encontró mar.png");
        }

        // Dentro de cargarSprites() en ObjectManager.java
        try {
            java.awt.image.BufferedImage imgM = utils.LoadSave.GetSpriteAtlas("maquinas.png");
            maquinaSprites = new java.awt.image.BufferedImage[3][3];
            for (int fila = 0; fila < 3; fila++) {
                for (int col = 0; col < 3; col++) {
                    maquinaSprites[fila][col] = imgM.getSubimage(col * 333, fila * 341, 333, 341);
                }
            }
        } catch (Exception e) {
            System.out.println("No se encontró la imagen de las máquinas");
        }

        craneoSimpleImg = utils.LoadSave.GetSpriteAtlas("craneoHueso.png"); //
        craneoCuernosImg = utils.LoadSave.GetSpriteAtlas("coyoteHueso.png");
    }

    public void cargarObjetosDeNivel(int nivelActual) {
        contenedores.clear();
        recompensas.clear();
        plataformas.clear();
        puertas.clear();
        picos.clear();
        candelabros.clear();
        aguas.clear();
        maquinas.clear();

        int[][] datosObjetos = LoadSave.GetObjectData(nivelActual + 1);

        for (int j = 0; j < datosObjetos.length; j++) {
            for (int i = 0; i < datosObjetos[0].length; i++) {

                int valorAzul = datosObjetos[j][i];

                if (valorAzul == 255)
                    continue;

                int xPos = i * Juego.TILES_SIZE;
                int yPos = j * Juego.TILES_SIZE;

                switch (valorAzul) {
                    case 0:
                        contenedores.add(new Contenedor(xPos, yPos, COFRE));
                        break;
                    case 1:
                        contenedores.add(new Contenedor(xPos, yPos, BARRIL));
                        break;
                    case 2:
                        recompensas.add(new Recompensas(xPos, yPos, LLAVE));
                        break;
                    case 3:
                        puertas.add(new PuertaMovil(xPos, yPos, PUERTA, 150));
                        break;
                    case 4:
                        plataformas.add(new PlataformaMovil(xPos, yPos, PLATAFORMA, 300, false));
                        break;
                    case 5:
                        plataformas.add(new PlataformaMovil(xPos, yPos, PLATAFORMA, 200, true));
                        break;
                    case 8:
                        aguas.add(new java.awt.geom.Rectangle2D.Float(xPos, yPos, Juego.TILES_SIZE, Juego.TILES_SIZE));
                        break;
                    case 9:
                        picos.add(new java.awt.geom.Rectangle2D.Float(xPos, yPos - 16, Juego.TILES_SIZE, 8));
                        break;
                    case 10:
                        candelabros.add(new Candelabro(xPos, yPos));
                        break;
                    case 11:
                        recompensas.add(new Recompensas(xPos, yPos, PIRAMIDE));
                        break;
                    case 12: // Máquina de Esferas
                        if (nivelActual == 2)
                            maquinas.add(new MaquinaGolpeable(xPos, yPos, 0));
                        break;
                    case 13: // Máquina de Cuadrados
                        if (nivelActual == 2)
                            maquinas.add(new MaquinaGolpeable(xPos, yPos, 1));
                        break;
                    case 14: // Máquina de Triángulos
                        if (nivelActual == 2)
                            maquinas.add(new MaquinaGolpeable(xPos, yPos, 2));
                        break;
                    case 15: // Botón del puzzle
                        if (nivelActual == 2)
                            botones.add(new BotonGolpeable(xPos, yPos));
                        break;
                    case 16:
                        recompensas.add(new Recompensas(xPos, yPos, CRANEO_SIMPLE)); //
                        break;
                    case 17:
                        recompensas.add(new Recompensas(xPos, yPos, CRANEO_CUERNOS)); //
                        break;
                    default:
                        System.out.println("Valor desconocido en datosObjetos: " + valorAzul);
                }
            }
        }
    }

    public void checkExplosionHit(Jugador j) {
        for (Contenedor c : contenedores) {
            if (c.isActivo() && c.getTipoObjeto() == BARRIL && c.getEstado() == ANIMACION && !c.danoAplicado) {
                int expAncho = (int) (100 * Juego.SCALE);
                int expAlto = (int) (100 * Juego.SCALE);
                int expX = (int) (c.getHitbox().x) - (expAncho / 2) + (int) (c.getHitbox().width / 2);
                int expY = (int) (c.getHitbox().y) - (expAlto / 2) + (int) (c.getHitbox().height / 2);

                java.awt.geom.Rectangle2D.Float cajaExplosion = new java.awt.geom.Rectangle2D.Float(expX, expY,
                        expAncho, expAlto);

                if (cajaExplosion.intersects(j.getHitbox())) {
                    int direccionEmpuje = (j.getHitbox().x < c.getHitbox().x) ? -1 : 1;
                    j.recibirDaño(25, direccionEmpuje, false);
                    c.danoAplicado = true;
                }
            }
        }
    }

    public void checkObjectHit(java.awt.geom.Rectangle2D.Float attackBox) {

        // 1. Revisar los contenedores (cofres y barriles)
        for (Contenedor c : contenedores) {
            if (c.isActivo() && c.getEstado() == INACTIVO && attackBox.intersects(c.getHitbox())) {
                c.recibirGolpe();

                if (c.getTipoObjeto() == BARRIL) {
                    audioPlayer.reproducirEfecto("sonido-explosion.wav");
                }

                if (c.getTipoObjeto() == COFRE) {
                    recompensas.add(new Recompensas((int) c.getHitbox().x, (int) c.getHitbox().y, CORAZON));
                }
            } // <--- ESTA LLAVE CIERRA EL IF DEL COFRE
        } // <--- ESTA LLAVE CIERRA EL FOR DE LOS CONTENEDORES

        // 2. Revisar las máquinas (Completamente AFUERA de lo anterior)
        for (MaquinaGolpeable m : maquinas) {
            if (attackBox.intersects(m.getHitbox())) {
                m.golpear(); // Cambia el color
                audioPlayer.reproducirEfecto("sonido-golpe.wav"); // Suena el golpe
                return; // Salimos para solo golpear una a la vez
            }
        }
        for (BotonGolpeable b : botones) {
            if (attackBox.intersects(b.getHitbox())) {

                // Comprobamos si las 3 máquinas están en la posición correcta
                boolean m1OK = false, m2OK = false, m3OK = false;

                for (MaquinaGolpeable m : maquinas) {
                    // Tipo 0 (Esferas) en la primera imagen (Color 0)
                    if (m.getTipoMaquina() == 0 && m.getColorActual() == 0)
                        m1OK = true;
                    // Tipo 1 (Cuadrados) en la segunda imagen (Color 1)
                    if (m.getTipoMaquina() == 1 && m.getColorActual() == 1)
                        m2OK = true;
                    // Tipo 2 (Triángulos) en la tercera imagen (Color 2)
                    if (m.getTipoMaquina() == 2 && m.getColorActual() == 2)
                        m3OK = true;
                }

                // Si las 3 están bien...
                if (m1OK && m2OK && m3OK) {
                    System.out.println("¡PUZZLE RESUELTO!");
                    audioPlayer.reproducirEfecto("sonido-acertado.wav"); // Sonido especial de éxito

                    // (Opcional) ¡Aquí puedes hacer que aparezca una llave o se abra una puerta!
                    // recompensas.add(new Recompensas((int) b.getX(), (int) b.getY() - 50, LLAVE));

                } else {
                    // Si te equivocaste en alguna máquina...
                    audioPlayer.reproducirEfecto("sonido incorrecto.wav"); // Sonido de error normal
                }

                return; // Salimos para no golpear otra cosa al mismo tiempo
            }
        }
    }

    public void checkPicking(Jugador j) {
        for (Recompensas r : recompensas) {
            if (r.isActivo() && j.getHitbox().intersects(r.getHitbox())) {
                r.setActivo(false);

                if (r.getTipoObjeto() == CORAZON) {
                    j.curarVida(25);
                } else if (r.getTipoObjeto() == LLAVE) {
                    j.recogerLlave();
                }
            }
        }
    }

    public void update(int levelIndex) {
        updateMar(); // Actualiza la animación del mar
        for (Contenedor c : contenedores)
            if (c.isActivo())
                c.update();
        for (Recompensas r : recompensas)
            if (r.isActivo())
                r.updateAnimation();
        for (PlataformaMovil p : plataformas)
            p.update();
        for (PuertaMovil p : puertas)
            p.update();
        for (Candelabro c : candelabros)
            if (c.isActivo())
                c.updateAnimation();
        if (levelIndex == 2) {
            for (Aguila a : aguilas) {
                a.update();
                if (a.getX() > 4000) {
                    a.resetPocision(-100);
                }
            }
        }
    }

    public void draw(Graphics g, int xLvlOffset, int yLvlOffset, int levelIndex) {
        // 1. Contenedores y explosiones
        for (Contenedor c : contenedores) {
            if (c.isActivo()) {
                BufferedImage img = (c.getTipoObjeto() == BARRIL) ? barrilImgs[c.getAnimInd()]
                        : cofreImgs[c.getAnimInd()];
                g.drawImage(img, (int) (c.getX() - xLvlOffset), c.getY() - yLvlOffset + 12, (int) (40 * Juego.SCALE),
                        (int) (30 * Juego.SCALE), null);

                if (c.getTipoObjeto() == BARRIL && c.getEstado() == 1) {
                    g.drawImage(explosionImgs[c.getAnimInd()], (int) (c.getX() - xLvlOffset - 30),
                            c.getY() - 35 - yLvlOffset, (int) (100 * Juego.SCALE), (int) (100 * Juego.SCALE), null);
                }
            }
        }

        // 2. Plataformas móviles
        for (PlataformaMovil p : plataformas) {
            if (p.isActivo()) {
                // Selecciona la imagen: si el nivel es 2 (Mundo 3), usa la del desierto. Si no,
                // usa la normal.
                BufferedImage imgActual = (levelIndex == 2) ? plataformaDesiertoImg : plataformaImg;

                g.drawImage(imgActual,
                        (int) (p.getHitbox().x - xLvlOffset),
                        (int) (p.getHitbox().y - yLvlOffset),
                        Juego.TILES_SIZE * 3, Juego.TILES_SIZE, null);
            }
        }

        // 3. Recompensas
        for (Recompensas r : recompensas) {
            if (r.isActivo()) {
                if (r.getTipoObjeto() == CORAZON) {
                    g.drawImage(corazonImgs[r.getAnimInd()],
                            (int) (r.getX() - xLvlOffset), (int) (r.getY() - yLvlOffset + 12),
                            (int) (30 * Juego.SCALE), (int) (30 * Juego.SCALE), null);
                } else if (r.getTipoObjeto() == LLAVE) {
                    g.drawImage(llaveImgs[r.getAniIndex()],
                            (int) (r.getHitbox().x - xLvlOffset),
                            (int) (r.getHitbox().y - yLvlOffset + 15),
                            (int) (24 * Juego.SCALE), (int) (24 * Juego.SCALE), null);
                }
            }
            if (r.getTipoObjeto() == CRANEO_SIMPLE) {
                g.drawImage(craneoSimpleImg,
                        (int) (r.getHitbox().x - xLvlOffset),
                        (int) (r.getHitbox().y - yLvlOffset),
                        (int) (32 * Juego.SCALE), (int) (32 * Juego.SCALE), null);
            } else if (r.getTipoObjeto() == CRANEO_CUERNOS) {
                g.drawImage(craneoCuernosImg,
                        (int) (r.getHitbox().x - xLvlOffset),
                        (int) (r.getHitbox().y - yLvlOffset),
                        (int) (32 * Juego.SCALE), (int) (32 * Juego.SCALE), null);
            }
        }

        // 4. Puertas
        for (PuertaMovil p : puertas) {
            if (p.isActivo()) {
                g.drawImage(puertaImg,
                        (int) (p.getHitbox().x - xLvlOffset),
                        (int) (p.getHitbox().y - yLvlOffset),
                        (int) (32 * Juego.SCALE), (int) (96 * Juego.SCALE), null);
            }
        }

        // 5. Candelabros
        for (Candelabro c : candelabros) {
            if (c.isActivo()) {
                g.drawImage(candelabroImgs[c.getAnimInd()],
                        (int) (c.getHitbox().x - xLvlOffset),
                        (int) (c.getHitbox().y - yLvlOffset),
                        (int) (16 * Juego.SCALE),
                        (int) (41 * Juego.SCALE),
                        null);
            }
        }
        // agua mar
        if (marSprites != null) {
            for (java.awt.geom.Rectangle2D.Float agua : aguas) {

                // Cambiamos a levelIndex == 1 para que sea en el Segundo Mundo
                BufferedImage[] spritesActuales = (levelIndex == 1 && marFuegoSprites != null) ? marFuegoSprites
                        : marSprites;

                g.drawImage(spritesActuales[marAniIndex],
                        (int) (agua.x - xLvlOffset),
                        (int) (agua.y - yLvlOffset),
                        Juego.TILES_SIZE, Juego.TILES_SIZE, null);
            }
        }

        // aguila
        if (levelIndex == 2) {
            for (Aguila a : aguilas) {
                g.drawImage(aguilaImgs[a.getAniIndex()],
                        (int) a.getX() - xLvlOffset,
                        (int) a.getY(),
                        (int) (utils.Constantes.Ambiente.AGUILA_WIDTH * .07),
                        (int) (utils.Constantes.Ambiente.AGUILA_HEIGHT * .07),
                        null);
            }

        }
        for (Recompensas r : recompensas) {
            if (r.isActivo()) {
                // ... (otros if de pociones o llaves)
                if (r.getTipoObjeto() == PIRAMIDE) {
                    g.drawImage(piramides,
                            (int) (r.getHitbox().x - xLvlOffset - (45 * Juego.SCALE)),
                            (int) (r.getHitbox().y - yLvlOffset - (90 * Juego.SCALE)),
                            250, 200, null);
                }
            }
        }

        if (maquinaSprites != null) {
            for (MaquinaGolpeable m : maquinas) {
                java.awt.image.BufferedImage imgActual = maquinaSprites[m.getTipoMaquina()][m.getColorActual()];

                // Usamos el ancho y alto directamente de la hitbox que ya agrandamos
                int ancho = (int) m.getHitbox().width;
                int alto = (int) m.getHitbox().height;

                g.drawImage(imgActual,
                        (int) (m.getHitbox().x - xLvlOffset),
                        (int) (m.getHitbox().y - yLvlOffset),
                        ancho, alto, // <--- Aquí aplicamos el nuevo tamaño
                        null);
            }
        }
        if (botonImg != null) {
            for (BotonGolpeable b : botones) {
                g.drawImage(botonImg,
                        (int) (b.getHitbox().x - xLvlOffset),
                        (int) (b.getHitbox().y - yLvlOffset),
                        Juego.TILES_SIZE, Juego.TILES_SIZE, null);
            }
        }
    }

    public void actualizarJugadorEnPlataforma(Jugador j) {
        if (j.getAirSpeed() < 0) {
            j.setEnPlataforma(false);
            return;
        }

        for (PlataformaMovil p : plataformas) {
            if (p.isActivo()) {
                if (utils.MetodosAyuda.IsEntityOnGameObject(j.getHitbox(), p.getHitbox())) {
                    j.getHitbox().x += p.getVelocidadX();
                    j.getHitbox().y = p.getHitbox().y - j.getHitbox().height;
                    j.setEnPlataforma(true);
                    return;
                }
            }
        }
        j.setEnPlataforma(false);
    }

    public void checkPuertaInteraccion(Jugador j) {
        for (PuertaMovil p : puertas) {
            if (j.getHitbox().intersects(p.getHitbox())) {
                if (j.getTieneLlave() && !p.estaAbierta() && !p.estaAbriendo()) {
                    p.abrir();
                    audioPlayer.reproducirEfecto("sonido-abertura.wav");
                }

                if (j.getHitbox().x < p.getHitbox().x) {
                    j.getHitbox().x = p.getHitbox().x - j.getHitbox().width - 1;
                } else {
                    j.getHitbox().x = p.getHitbox().x + p.getHitbox().width + 1;
                }
            }
        }
    }

    public boolean checkMuertePorAgua(java.awt.geom.Rectangle2D.Float hitboxJugador) {
        for (java.awt.geom.Rectangle2D.Float agua : aguas) {
            // Revisar si el centro del jugador toca el agua
            int centerX = (int) (hitboxJugador.x + hitboxJugador.width / 2);
            int centerY = (int) (hitboxJugador.y + hitboxJugador.height / 2);
            if (agua.contains(centerX, centerY)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkDañoPorPicos(Jugador j) {
        for (java.awt.geom.Rectangle2D.Float pico : picos) {
            if (pico.intersects(j.getHitbox())) {
                return true;
            }
        }
        return false;
    }

    private void updateMar() {
        marAniTick++;

        if (marAniTick >= marAniSpeed) {
            marAniTick = 0;
            marAniIndex++;

            if (marAniIndex >= 3) {
                marAniIndex = 0;
            }
        }
    }

}