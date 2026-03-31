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
        addEnemies();
    }

    private void addEnemies() {
        esqueletos.add(new Esqueleto(200, 50));
        esqueletos.add(new Esqueleto(400, 50));
        esqueletos.add(new Esqueleto(800, 50));
        orcos.add(new Orc(1300, 50));
        caballeros.add(new Caballero(600, 50));
        jefesFinales.add(new JefeFinal(2000, 200));
    }

    public void update(int[][] lvlData, Jugador jugador) {
        esqueletos.removeIf(esqueleto -> !esqueleto.isActivo());
        caballeros.removeIf(caballero -> !caballero.isActivo());
        jefesFinales.removeIf(jefeFinal -> !jefeFinal.isActivo());
        orcos.removeIf(orco -> !orco.isActivo());

        for (Esqueleto e : esqueletos) {
            e.update(lvlData, jugador);
        }
        for (Caballero c : caballeros) {
            c.update(lvlData, jugador);
        }
        for (Orc o : orcos) {
            o.update(lvlData, jugador);
        }
        for (JefeFinal j : jefesFinales) {
            j.update(lvlData, jugador);
        }
        for (TextoDaño td : textosDaño) {
            td.update();
        }

    }

    public void draw(Graphics g, int xLvlOffset) {
        for (Esqueleto e : esqueletos) {
            e.render(g, xLvlOffset);
        }
        for (Caballero c : caballeros) {
            c.render(g, xLvlOffset);
        }
        for (Orc o : orcos) {
            o.render(g, xLvlOffset);
        }
        for (JefeFinal j : jefesFinales) {
            j.render(g, xLvlOffset);
        }
        for (TextoDaño td : textosDaño) td.draw(g, xLvlOffset);
    }

    public void checkEnemyHit(java.awt.geom.Rectangle2D.Float attackBox, Jugador jugador) {
        int dañoJugador = jugador.getDañoAtaque();
        for (Esqueleto e : esqueletos) {
            if (e.getEnemyState() != MUERTO && attackBox.intersects(e.getHitbox())) {
                e.recibirDaño(jugador.getDañoAtaque());
                jugador.registrarGolpe();

                crearTextoDaño(e.getHitbox(), dañoJugador);

                if (e.getEnemyState() == MUERTO) {
                    jugador.registrarMuerte();
                }
                return;
            }
        }

        for (JefeFinal j : jefesFinales) {
            if (j.getEnemyState() != MUERTO && attackBox.intersects(j.getHitbox())) {
                j.recibirDaño(jugador.getDañoAtaque());
                jugador.registrarGolpe();

                crearTextoDaño(j.getHitbox(), dañoJugador);

                if (j.getEnemyState() == MUERTO) {
                    jugador.registrarMuerte();
                }
                return;
            }
        }
        for (Caballero c : caballeros) {
            if (c.getEnemyState() != MUERTO && attackBox.intersects(c.getHitbox())) {
                c.recibirDaño(jugador.getDañoAtaque());
                jugador.registrarGolpe();

                crearTextoDaño(c.getHitbox(), dañoJugador);

                if (c.getEnemyState() == MUERTO) {
                    jugador.registrarMuerte();
                }
                return;
            }
        }
        for (Orc o : orcos) {
            if (o.getEnemyState() != MUERTO && attackBox.intersects(o.getHitbox())) {
                o.recibirDaño(jugador.getDañoAtaque());
                jugador.registrarGolpe();

                crearTextoDaño(o.getHitbox(), dañoJugador);

                if (o.getEnemyState() == MUERTO) {
                    jugador.registrarMuerte();
                }
                return;
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

        public void draw(java.awt.Graphics g, int xLvlOffset) {
            if (vidaTotal <= 0)
                return;
            int alpha = (int) ((vidaTotal / 120.0f) * 255);
            alpha = Math.max(0, Math.min(255, alpha));

            g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, (int) (12 * juego.Juego.SCALE)));
            g.setColor(new java.awt.Color(0, 0, 0, alpha));
            g.drawString(String.valueOf(valorDaño), (int) (x - xLvlOffset) + 1, (int) y + 1);
            g.setColor(new java.awt.Color(255, 50, 50, alpha));
            g.drawString(String.valueOf(valorDaño), (int) (x - xLvlOffset), (int) y);
        }

        public boolean isMuerto() {
            return vidaTotal <= 0;
        }
    }
    public void resetAllEnemies() {
        esqueletos.clear();
        jefesFinales.clear();
        textosDaño.clear(); 
        orcos.clear();
        caballeros.clear();
        
        addEnemies(); 
    }
    public boolean todosDerrotados() {
        return esqueletos.isEmpty() && jefesFinales.isEmpty() && orcos.isEmpty() && caballeros.isEmpty(); 
    }
}