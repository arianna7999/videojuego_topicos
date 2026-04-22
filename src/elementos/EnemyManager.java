package elementos;

import static utils.Constantes.ConstantesEnemigos.*;

import java.awt.Graphics;
import java.util.ArrayList;

public class EnemyManager {
    private ArrayList<Esqueleto> esqueletos = new ArrayList<>();
    private ArrayList<Caballero> caballeros = new ArrayList<>();
    private ArrayList<JefeFinal> jefesFinales = new ArrayList<>();
    private ArrayList<TextoDaño> textosDaño = new ArrayList<>();
    private ArrayList<Orc> orcos = new ArrayList<>();

    public EnemyManager() {
    }

    public void addEnemies(int nivelActual) {
        esqueletos.clear();
        caballeros.clear();
        jefesFinales.clear();
        orcos.clear();
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
                        esqueletos.add(new Esqueleto(xPos, yPos));
                        break;
                    case 1:
                        caballeros.add(new Caballero(xPos, yPos));
                        break;
                    case 2:
                        orcos.add(new Orc(xPos, yPos));
                        break;
                    case 3:
                        jefesFinales.add(new JefeFinal(xPos, yPos));
                        break;
                }
            }
        }
    }

    public void update(int[][] lvlData, Jugador jugador) {
        esqueletos.removeIf(esqueleto -> !esqueleto.isActivo());
        caballeros.removeIf(caballero -> !caballero.isActivo());
        jefesFinales.removeIf(jefeFinal -> !jefeFinal.isActivo());
        orcos.removeIf(orco -> !orco.isActivo());

        for (Esqueleto e : esqueletos) e.update(lvlData, jugador);
        for (Caballero c : caballeros) c.update(lvlData, jugador);
        for (Orc o : orcos) o.update(lvlData, jugador);
        for (JefeFinal j : jefesFinales) j.update(lvlData, jugador);
        for (TextoDaño td : textosDaño) td.update();
    }

    public void draw(Graphics g, int xLvlOffset, int yLvlOffset) {
        for (Esqueleto e : esqueletos) e.render(g, xLvlOffset, yLvlOffset);
        for (Caballero c : caballeros) c.render(g, xLvlOffset, yLvlOffset);
        for (Orc o : orcos) o.render(g, xLvlOffset, yLvlOffset);
        for (JefeFinal j : jefesFinales) j.render(g, xLvlOffset, yLvlOffset);
        for (TextoDaño td : textosDaño) td.draw(g, xLvlOffset, yLvlOffset);
    }

    public void checkEnemyHit(java.awt.geom.Rectangle2D.Float attackBox, Jugador jugador) {
        int dañoJugador = jugador.getDañoActual(); // Golpe Brutal de Hank
        boolean robó = false;
        for (Esqueleto e : esqueletos) {
            if (e.getEnemyState() != MUERTO && attackBox.intersects(e.getHitbox())) {
                e.recibirDaño(dañoJugador);
                jugador.registrarGolpe();
                crearTextoDaño(e.getHitbox(), dañoJugador);
                if (!robó) { jugador.procesarRoboVida(dañoJugador); robó = true; }
                if (e.getEnemyState() == MUERTO) jugador.registrarMuerte();
                return;
            }
        }
        for (JefeFinal j : jefesFinales) {
            if (j.getEnemyState() != MUERTO && attackBox.intersects(j.getHitbox())) {
                j.recibirDaño(dañoJugador);
                jugador.registrarGolpe();
                crearTextoDaño(j.getHitbox(), dañoJugador);
                if (!robó) { jugador.procesarRoboVida(dañoJugador); robó = true; }
                if (j.getEnemyState() == MUERTO) jugador.registrarMuerte();
                return;
            }
        }
        for (Caballero c : caballeros) {
            if (c.getEnemyState() != MUERTO && attackBox.intersects(c.getHitbox())) {
                c.recibirDaño(dañoJugador);
                jugador.registrarGolpe();
                crearTextoDaño(c.getHitbox(), dañoJugador);
                if (!robó) { jugador.procesarRoboVida(dañoJugador); robó = true; }
                if (c.getEnemyState() == MUERTO) jugador.registrarMuerte();
                return;
            }
        }
        for (Orc o : orcos) {
            if (o.getEnemyState() != MUERTO && attackBox.intersects(o.getHitbox())) {
                o.recibirDaño(dañoJugador);
                jugador.registrarGolpe();
                crearTextoDaño(o.getHitbox(), dañoJugador);
                if (!robó) { jugador.procesarRoboVida(dañoJugador); robó = true; }
                if (o.getEnemyState() == MUERTO) jugador.registrarMuerte();
                return;
            }
        }
    }

    /** Lluvia de Lucerys: daña a TODOS los enemigos dentro del área. */
    public void checkEnemyHitArea(java.awt.geom.Rectangle2D.Float area, Jugador jugador) {
        int dañoBase = jugador.getDañoAtaque();
        for (Esqueleto e : esqueletos) {
            if (e.getEnemyState() != MUERTO && area.intersects(e.getHitbox())) {
                e.recibirDaño(dañoBase); jugador.registrarGolpe();
                crearTextoDaño(e.getHitbox(), dañoBase);
                if (e.getEnemyState() == MUERTO) jugador.registrarMuerte();
            }
        }
        for (JefeFinal j : jefesFinales) {
            if (j.getEnemyState() != MUERTO && area.intersects(j.getHitbox())) {
                j.recibirDaño(dañoBase); jugador.registrarGolpe();
                crearTextoDaño(j.getHitbox(), dañoBase);
                if (j.getEnemyState() == MUERTO) jugador.registrarMuerte();
            }
        }
        for (Caballero c : caballeros) {
            if (c.getEnemyState() != MUERTO && area.intersects(c.getHitbox())) {
                c.recibirDaño(dañoBase); jugador.registrarGolpe();
                crearTextoDaño(c.getHitbox(), dañoBase);
                if (c.getEnemyState() == MUERTO) jugador.registrarMuerte();
            }
        }
        for (Orc o : orcos) {
            if (o.getEnemyState() != MUERTO && area.intersects(o.getHitbox())) {
                o.recibirDaño(dañoBase); jugador.registrarGolpe();
                crearTextoDaño(o.getHitbox(), dañoBase);
                if (o.getEnemyState() == MUERTO) jugador.registrarMuerte();
            }
        }
    }

    public java.util.ArrayList<JefeFinal> getJefesFinales() {
        return jefesFinales;
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

        public void draw(java.awt.Graphics g, int xLvlOffset, int yLvlOffset) {
            if (vidaTotal <= 0) return;
            int alpha = (int) ((vidaTotal / 120.0f) * 255);
            alpha = Math.max(0, Math.min(255, alpha));

            g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, (int) (12 * juego.Juego.SCALE)));
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
        return esqueletos.isEmpty() && jefesFinales.isEmpty() && orcos.isEmpty() && caballeros.isEmpty(); 
    }
}