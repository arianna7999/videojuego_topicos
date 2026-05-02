package eventos;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import juego.PanelJuego;

public class EventoMouse extends MouseAdapter{

    private PanelJuego pan;
    
    public EventoMouse(PanelJuego pan) {
        this.pan = pan;
    }
    public void mouseClicked(MouseEvent e){
      System.out.println("click");
      pan.getGame().getPlayer().setAttacking(true);

    }
    public void mousePressed(MouseEvent e){
      System.out.println("click");
       pan.getGame().getPlayer().setAttacking(true);
       

    }
    public void mouseReleased(MouseEvent e){
      System.out.println("click");
       pan.getGame().getPlayer().setAttacking(false);

    }
}
