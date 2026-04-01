package elementos;

import static utils.Constantes.ConstantesObjetos.*;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import juego.Juego;
import utils.LoadSave;

public class ObjectManager {

    private ArrayList<Contenedor> contenedores = new ArrayList<>();
    private ArrayList<Recompensas> recompensas = new ArrayList<>();
    private ArrayList<PlataformaMovil> plataformas = new ArrayList<>();
    private BufferedImage plataformaImg;
    private utils.AudioPlayer audioPlayer;

    private BufferedImage[] barrilImgs;
    private BufferedImage[] cofreImgs;
    private BufferedImage[] corazonImgs;
    private BufferedImage[] explosionImgs;

    public ObjectManager(utils.AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        cargarSprites();
        contenedores.add(new Contenedor(1500, 300, COFRE));
        contenedores.add(new Contenedor(750, 155, COFRE));
        contenedores.add(new Contenedor(810, 585, COFRE));
        contenedores.add(new Contenedor(100, 485, BARRIL));
        contenedores.add(new Contenedor(1800, 200, BARRIL));
        // Plataforma Horizontal (false)
        plataformas.add(new PlataformaMovil(1900, 450, PLATAFORMA, 300, false)); 
        
        // Plataforma Vertical (true) - Se moverá de Y=300 hasta Y=500
        plataformas.add(new PlataformaMovil(2300, 300, PLATAFORMA, 200, true));
    }

    private void cargarSprites() {
        BufferedImage img = LoadSave.GetSpriteAtlas("objetos-sprite.png");
        plataformaImg = LoadSave.GetSpriteAtlas("plataforma-movible.png");

        barrilImgs = new BufferedImage[8];
        cofreImgs = new BufferedImage[8];

        for (int frame = 0; frame < 8; frame++) {
            cofreImgs[frame] = img.getSubimage(frame * 40, 0, 40, 30);
            barrilImgs[frame] = img.getSubimage(frame * 40, 30, 40, 30);
        }

        BufferedImage imgCorazon = LoadSave.GetSpriteAtlas("sprite-corazon.png");
        corazonImgs = new BufferedImage[12];
        int anchoFrame = imgCorazon.getWidth() / 12;
        int altoFrame = imgCorazon.getHeight();

        for (int i = 0; i < corazonImgs.length; i++) {
            corazonImgs[i] = imgCorazon.getSubimage(i * anchoFrame, 0, anchoFrame, altoFrame);
        }

        BufferedImage imgExplosion = LoadSave.GetSpriteAtlas("explosion.png");
        explosionImgs = new BufferedImage[8];
        int anchoExp = 256;
        int altoExp = 336;

        for (int i = 0; i < explosionImgs.length; i++) {
            explosionImgs[i] = imgExplosion.getSubimage(i * anchoExp, 0, anchoExp, altoExp);
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

                    // AQUI ESTÁ EL CAMBIO: Agregamos 'false' al final para que no suene el golpe
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

                // Si es un barril, reproducimos el sonido de explosión
                if (c.getTipoObjeto() == BARRIL) {
                    audioPlayer.reproducirEfecto("sonido-explosion.wav");
                }

                // Solo el cofre suelta el corazón cuando lo golpeas
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
                j.curarVida(25);
            }
        }
    }

    public void update() {
        for (Contenedor c : contenedores)
            if (c.isActivo())
                c.update();
        for (Recompensas r : recompensas)
            if (r.isActivo())
                r.update();
        for (PlataformaMovil p : plataformas)
            p.update();
    }

    public void draw(Graphics g, int xLvlOffset) {
        for (Contenedor c : contenedores) {
            if (c.isActivo()) {
                BufferedImage imgADibujar;

                if (c.getTipoObjeto() == BARRIL) {
                    imgADibujar = barrilImgs[c.getAnimInd()];
                } else {
                    imgADibujar = cofreImgs[c.getAnimInd()];
                }

                // Dibujar el cofre o barril
                g.drawImage(imgADibujar, (int) (c.getX() - xLvlOffset), c.getY(),
                        (int) (40 * Juego.SCALE), (int) (30 * Juego.SCALE), null);

                // Si es un BARRIL y se está rompiendo (ANIMACION), dibujamos la explosión
                // encima
                if (c.getTipoObjeto() == BARRIL && c.getEstado() == ANIMACION) {

                    int expAncho = (int) (100 * Juego.SCALE);
                    int expAlto = (int) (100 * Juego.SCALE);

                    int expX = (int) (c.getX() - xLvlOffset) - (expAncho / 2) + (int) ((40 * Juego.SCALE) / 2);
                    int expY = c.getY() - (expAlto / 2) + (int) ((30 * Juego.SCALE) / 2);

                    g.drawImage(explosionImgs[c.getAnimInd()], expX, expY, expAncho, expAlto, null);
                }
                
            }
            
        }

        for (PlataformaMovil p : plataformas) {
        if (p.isActivo()) {
            g.drawImage(plataformaImg, 
                (int) (p.getHitbox().x - xLvlOffset), 
                (int) (p.getHitbox().y), 
                Juego.TILES_SIZE*3, // Esto usa los 32 píxeles definidos en Juego
                Juego.TILES_SIZE, 
                null);
        }
    }
        for (Recompensas r : recompensas) {
            if (r.isActivo()) {
                g.drawImage(corazonImgs[r.getAnimInd()], (int) (r.getX() - xLvlOffset), r.getY(),
                        (int) (30 * Juego.SCALE), (int) (30 * Juego.SCALE), null);
            }
        }
    }

  public void actualizarJugadorEnPlataforma(Jugador j) {
        
        // ¡NUEVO! Si el airSpeed es menor a 0, significa que el jugador está saltando hacia arriba.
        // Lo ignoramos por completo para que la plataforma no cancele su salto.
        if (j.getAirSpeed() < 0) {
            j.setEnPlataforma(false);
            return;
        }

        for (PlataformaMovil p : plataformas) {
            if (p.isActivo()) {
                if (utils.MetodosAyuda.IsEntityOnGameObject(j.getHitbox(), p.getHitbox())) {
                    
                    // Movemos al jugador en X junto con la plataforma
                    j.getHitbox().x += p.getVelocidadX(); 
                    
                    // Lo pegamos a la Y de la plataforma
                    j.getHitbox().y = p.getHitbox().y - j.getHitbox().height;
                    
                    // Le avisamos que está a salvo
                    j.setEnPlataforma(true);
                    return;
                }
            }
        }
        // Si no tocó ninguna, no está en plataforma
        j.setEnPlataforma(false);
    }
}
