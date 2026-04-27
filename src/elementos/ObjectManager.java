package elementos;

import static utils.Constantes.ConstantesObjetos.*;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

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

    private utils.AudioPlayer audioPlayer;

    private BufferedImage plataformaImg;
    private BufferedImage puertaImg;
    private BufferedImage[] aguilaImgs;
    private BufferedImage[] barrilImgs;
    private BufferedImage[] cofreImgs;
    private BufferedImage[] corazonImgs;
    private BufferedImage[] explosionImgs;
    private BufferedImage[] llaveImgs;
    private BufferedImage[] candelabroImgs;

    private ArrayList<java.awt.geom.Rectangle2D.Float> aguas = new ArrayList<>();
    private ArrayList<java.awt.geom.Rectangle2D.Float> picos = new ArrayList<>();

    private BufferedImage[] marSprites;
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
        for (int i = 0; i < aguilaImgs.length; i++) {
            // Cortamos la imagen (1 fila x 6 columnas)
            // Asegúrate de que Constantes.Ambiente.AGUILA_WIDTH_DEFAULT sea el tamaño
            // correcto
            aguilaImgs[i] = temp.getSubimage(i * Constantes.Ambiente.AGUILA_WIDTH_DEFAULT, 0,
                    Constantes.Ambiente.AGUILA_WIDTH_DEFAULT,
                    Constantes.Ambiente.AGUILA_HEIGHT_DEFAULT);
        }

        aguilas.add(new Aguila(100, 50));
        aguilas.add(new Aguila(150, 120));
        for (int frame = 0; frame < 8; frame++) {
            cofreImgs[frame] = img.getSubimage(frame * 40, 0, 40, 30);
            barrilImgs[frame] = img.getSubimage(frame * 40, 30, 40, 30);
        }

        plataformaImg = LoadSave.GetSpriteAtlas("plataforma-movible.png");
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
            for (int i = 0; i < 3; i++) {
                marSprites[i] = imgMar.getSubimage(i * 100, 0, 100, 100);
            }
        } catch (Exception e) {
            System.out.println("Aviso: No se encontró mar.png");
        }

    }

    public void cargarObjetosDeNivel(int nivelActual) {
        contenedores.clear();
        recompensas.clear();
        plataformas.clear();
        puertas.clear();
        picos.clear();
        candelabros.clear();

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
        for (Contenedor c : contenedores) {
            if (c.isActivo() && c.getEstado() == INACTIVO && attackBox.intersects(c.getHitbox())) {
                c.recibirGolpe();

                if (c.getTipoObjeto() == BARRIL) {
                    audioPlayer.reproducirEfecto("sonido-explosion.wav");
                }

                if (c.getTipoObjeto() == COFRE) {
                    recompensas.add(new Recompensas((int) c.getHitbox().x, (int) c.getHitbox().y, CORAZON));
                }
                return;
            }
        }
    }

    public void checkPicking(Jugador j) {
        for (Recompensas r : recompensas) {
            if (r.isActivo() && j.getHitbox().intersects(r.getHitbox())) {
                r.activo = false;

                if (r.getTipoObjeto() == CORAZON) {
                    j.curarVida(25);
                } else if (r.getTipoObjeto() == LLAVE) {
                    j.recogerLlave();
                }
            }
        }
    }

    public void update() {
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
        for (Aguila a : aguilas) {
            a.update();
            if (a.getX() > 4000) {
                a.resetPocision(-100);
            }
        }
    }

    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
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
                g.drawImage(plataformaImg,
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

        if (marSprites != null) {
            for (java.awt.geom.Rectangle2D.Float agua : aguas) {
                g.drawImage(marSprites[marAniIndex],
                        (int) (agua.x - xLvlOffset),
                        (int) (agua.y - yLvlOffset),
                        Juego.TILES_SIZE, Juego.TILES_SIZE, null);
            }
        }

        // aguila
        for (Aguila a : aguilas) {
            // Restamos el xLvlOffset si quieres que el águila se mueva con la cámara
            // Si quieres que sea parte del "fondo lejano", puedes no restarle el offset o
            // restarle una fracción (efecto parallax)
            g.drawImage(aguilaImgs[a.getAniIndex()],
                    (int) a.getX() - xLvlOffset, // <-- Ahora solo usa su propia coordenada X
                    (int) a.getY(), (int) (Constantes.Ambiente.AGUILA_WIDTH * .07),
                    (int) (Constantes.Ambiente.AGUILA_HEIGHT * .07),
                    null);
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
}