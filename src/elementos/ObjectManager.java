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
    
    private BufferedImage[] barrilImgs;
    private BufferedImage[] cofreImgs; 
    private BufferedImage[] corazonImgs; 
    private BufferedImage[] explosionImgs; 

    public ObjectManager() {
        cargarSprites();
        // Ponemos unos cofres y un BARRIL de prueba para que veas la diferencia
        contenedores.add(new Contenedor(1500, 300, COFRE));
        contenedores.add(new Contenedor(750, 155, COFRE));
        contenedores.add(new Contenedor(810, 585, COFRE));
        contenedores.add(new Contenedor(100, 485, BARRIL)); 
        contenedores.add(new Contenedor(1800, 200, BARRIL));
    }

    private void cargarSprites() {
        BufferedImage img = LoadSave.GetSpriteAtlas("objetos-sprite.png"); 
        
        barrilImgs = new BufferedImage[8]; 
        cofreImgs = new BufferedImage[8]; 
        
        for(int frame = 0; frame < 8; frame++) {
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
            // Si el barril está explotando y todavía no le ha hecho daño al jugador
            if (c.isActivo() && c.getTipoObjeto() == BARRIL && c.getEstado() == ANIMACION && !c.danoAplicado) {
                
                // Creamos una caja invisible del tamaño de la explosión (100x100) centrada en el barril
                int expAncho = (int)(100 * Juego.SCALE);
                int expAlto = (int)(100 * Juego.SCALE);
                int expX = (int)(c.getHitbox().x) - (expAncho / 2) + (int)(c.getHitbox().width / 2);
                int expY = (int)(c.getHitbox().y) - (expAlto / 2) + (int)(c.getHitbox().height / 2);
                
                java.awt.geom.Rectangle2D.Float cajaExplosion = new java.awt.geom.Rectangle2D.Float(expX, expY, expAncho, expAlto);
                
                // Si la caja de explosión toca al jugador
                if (cajaExplosion.intersects(j.getHitbox())) {
                    // Calculamos de qué lado está el jugador para empujarlo en la dirección correcta
                    int direccionEmpuje = (j.getHitbox().x < c.getHitbox().x) ? -1 : 1;
                    
                    j.recibirDaño(25, direccionEmpuje); // Le quitamos 25 de vida y lo empujamos
                    c.danoAplicado = true; // Marcamos que ya le hizo daño para que no le siga bajando vida
                }
            }
        }
    }
    public void checkObjectHit(java.awt.geom.Rectangle2D.Float attackBox) {
        for (Contenedor c : contenedores) {
            if (c.isActivo() && c.getEstado() == INACTIVO && attackBox.intersects(c.getHitbox())) {
                c.recibirGolpe(); 
                
                // Solo el cofre suelta el corazón cuando lo golpeas
                if (c.getTipoObjeto() == COFRE) {
                    recompensas.add(new Recompensas((int)c.getHitbox().x, (int)c.getHitbox().y, CORAZON));
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
        for (Contenedor c : contenedores) if (c.isActivo()) c.update();
        for (Recompensas r : recompensas) if (r.isActivo()) r.update();
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
                g.drawImage(imgADibujar, (int)(c.getX() - xLvlOffset), c.getY(), 
                            (int)(40 * Juego.SCALE), (int)(30 * Juego.SCALE), null);

                // Si es un BARRIL y se está rompiendo (ANIMACION), dibujamos la explosión encima
                if (c.getTipoObjeto() == BARRIL && c.getEstado() == ANIMACION) {
                    
                    int expAncho = (int)(100 * Juego.SCALE);
                    int expAlto = (int)(100 * Juego.SCALE);
                    
                    int expX = (int)(c.getX() - xLvlOffset) - (expAncho / 2) + (int)((40 * Juego.SCALE) / 2);
                    int expY = c.getY() - (expAlto / 2) + (int)((30 * Juego.SCALE) / 2);

                    g.drawImage(explosionImgs[c.getAnimInd()], expX, expY, expAncho, expAlto, null);
                }
            }
        }

        for (Recompensas r : recompensas) {
            if (r.isActivo()) {
                g.drawImage(corazonImgs[r.getAnimInd()], (int)(r.getX() - xLvlOffset), r.getY(), 
                            (int)(30 * Juego.SCALE), (int)(30 * Juego.SCALE), null);
            }
        }
    }
}