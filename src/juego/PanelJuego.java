package juego;

import javax.swing.JPanel;

import eventos.EntraTeclado;
import eventos.EventoMouse;

import static juego.Juego.*;

import java.awt.*;

public class PanelJuego extends JPanel {
    private Juego  game;
    private EntraTeclado et;
    private EventoMouse em;

    public PanelJuego(Juego game) {
        em=new EventoMouse(this);
        et=new EntraTeclado(this);
        this.game=game;
        setPanelSize();        
        addKeyListener(et);  
        addMouseListener(em);   
        setFocusable(true);          
    }

    private void setPanelSize() {
        Dimension size=new Dimension(GAME_WIDTH,GAME_HEIGHT);
        //this.setSize(size);
        this.setPreferredSize(size);
       // this.setMaximumSize(size); 
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
        g2.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        double scaleX = (double) getWidth() / Juego.GAME_WIDTH;
        double scaleY = (double) getHeight() / Juego.GAME_HEIGHT;
        g2.scale(scaleX, scaleY);
        game.render(g);
       
    }
    void updateGame(){}

    public Juego getGame() {
        return game;
    }   
}
