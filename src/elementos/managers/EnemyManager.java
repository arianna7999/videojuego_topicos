package elementos.managers;

import static utils.Constantes.ConstantesEnemigos.*;
import java.awt.Graphics;
import java.util.ArrayList;

import elementos.enemigos.Caballero;
import elementos.enemigos.Enemigo;
import elementos.enemigos.Enemy1;
import elementos.enemigos.Enemy2;
import elementos.enemigos.JefeFinal;
import elementos.jugador.Jugador;

public class EnemyManager {
    private ArrayList<Enemigo> enemigos = new ArrayList<>();
    private ArrayList<TextoDaño> textosDaño = new ArrayList<>();
    private java.awt.Font fuenteDaño;

    public EnemyManager() {
        fuenteDaño = new java.awt.Font("Arial", java.awt.Font.BOLD, (int) (12 * juego.Juego.SCALE));
    }

    public void addEnemies(int nivelActual) {
        enemigos.clear();
        textosDaño.clear();

        int[][] enemyData = utils.LoadSave.GetEnemyData(nivelActual + 1);

        for (int j = 0; j < enemyData.length; j++) {
            for (int i = 0; i < enemyData[0].length; i++) {
                int valorVerde = enemyData[j][i];
                if (valorVerde == 255) continue; 
                
                int xPos = i * juego.Juego.TILES_SIZE;
                int yPos = j * juego.Juego.TILES_SIZE;

                switch (valorVerde) {
                    case 0:
                        enemigos.add(new Enemy1(xPos, yPos, nivelActual));
                        break;
                    case 1:
                        enemigos.add(new Caballero(xPos, yPos));
                        break;
                    case 2:
                        enemigos.add(new Enemy2(xPos, yPos, nivelActual));
                        break;
                    case 3:
                        enemigos.add(new JefeFinal(xPos, yPos));
                        break;
                    
                }
            }
        }
    }

    public void update(int[][] lvlData, Jugador jugador, int levelIndex) {
        enemigos.removeIf(enemigo -> !enemigo.isActivo());
        textosDaño.removeIf(td -> !td.isActivo());
        for (Enemigo e : enemigos) e.update(lvlData, jugador, levelIndex);
        for (TextoDaño td : textosDaño) td.update();
    }

    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
        for (Enemigo e : enemigos) e.render(g, xLvlOffset, yLvlOffset);
        for (TextoDaño td : textosDaño) td.draw(g, xLvlOffset, yLvlOffset);
    }

    public void checkEnemyHit(java.awt.geom.Rectangle2D.Float attackBox, Jugador jugador) {
        int dañoJugador = jugador.getDañoActual();
        boolean robó = false;
        
        for (Enemigo e : enemigos) {
            if (e.getEnemyState() != MUERTO && attackBox.intersects(e.getHitbox())) {
                e.recibirDaño(dañoJugador);
                jugador.registrarGolpe();
                crearTextoDaño(e.getHitbox(), dañoJugador);
                
                if (!robó) { 
                    jugador.procesarRoboVida(dañoJugador); 
                    robó = true; 
                }
                
                if (e.getEnemyState() == MUERTO) {
                    jugador.registrarMuerte();
                }
                return;
            }
        }
    }

    public void checkEnemyHitArea(java.awt.geom.Rectangle2D.Float area, Jugador jugador) {
        int dañoBase = jugador.getDañoAtaque();
        
        for (Enemigo e : enemigos) {
            if (e.getEnemyState() != MUERTO && area.intersects(e.getHitbox())) {
                e.recibirDaño(dañoBase); 
                jugador.registrarGolpe();
                crearTextoDaño(e.getHitbox(), dañoBase);
                
                if (e.getEnemyState() == MUERTO) {
                    jugador.registrarMuerte();
                }
            }
        }
    }

    // Versión para proyectiles: aplica daño con el valor dado y retorna true si golpeó algo
    public boolean checkEnemyHitAreaFlecha(java.awt.geom.Rectangle2D.Float area, Jugador jugador, int daño) {
        boolean golpeo = false;
        for (Enemigo e : enemigos) {
            if (e.getEnemyState() != MUERTO && area.intersects(e.getHitbox())) {
                e.recibirDaño(daño);
                jugador.registrarGolpe();
                crearTextoDaño(e.getHitbox(), daño);
                if (e.getEnemyState() == MUERTO) {
                    jugador.registrarMuerte();
                }
                golpeo = true;
            }
        }
        return golpeo;
    }

    public java.util.ArrayList<JefeFinal> getJefesFinales() {
        java.util.ArrayList<JefeFinal> listaJefes = new java.util.ArrayList<>();
        for (Enemigo e : enemigos) {
            if (e instanceof JefeFinal) {
                listaJefes.add((JefeFinal) e);
            }
        }
        return listaJefes;
    }

    private void crearTextoDaño(java.awt.geom.Rectangle2D.Float hitboxEnemigo, int daño) {
        float centroX = hitboxEnemigo.x + (hitboxEnemigo.width / 2);
        float parteSuperiorY = hitboxEnemigo.y;
        textosDaño.add(new TextoDaño(centroX, parteSuperiorY, daño));
    }

    private class TextoDaño {
        float x, y;
        int valorDaño;
        int vidaTotal = 120;
        float velocidadY = -1.0f * juego.Juego.SCALE;

        public TextoDaño(float x, float y, int valorDaño) {
            this.x = x + (float) (Math.random() * 20 - 10) * juego.Juego.SCALE;
            this.y = y - 10 * juego.Juego.SCALE;
            this.valorDaño = valorDaño;
        }

        public void update() {
            y += velocidadY;
            vidaTotal--;
        }

        public boolean isActivo() {
            return vidaTotal > 0;
        }

        public void draw(java.awt.Graphics g, int xLvlOffset, int yLvlOffset) {
            if (vidaTotal <= 0) return;
            int alpha = (int) ((vidaTotal / 120.0f) * 255);
            alpha = Math.max(0, Math.min(255, alpha));

            g.setFont(fuenteDaño); // Se utiliza la fuente cacheada
            
            g.setColor(new java.awt.Color(0, 0, 0, alpha));
            g.drawString(String.valueOf(valorDaño), (int) (x - xLvlOffset) + 1, (int) (y - yLvlOffset) + 1);
            g.setColor(new java.awt.Color(255, 50, 50, alpha));
            g.drawString(String.valueOf(valorDaño), (int) (x - xLvlOffset), (int) (y - yLvlOffset));
        }
    }

    public void resetAllEnemies(int nivelActual) {
        addEnemies(nivelActual); 
    }

    public boolean todosDerrotados() {
        return enemigos.isEmpty(); 
    }
}