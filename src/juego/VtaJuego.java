package juego;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import javax.swing.JFrame;

public class VtaJuego {

    public VtaJuego(PanelJuego n) {
        JFrame vta = new JFrame();
        vta.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        vta.add(n);
        vta.setResizable(true);
        vta.setExtendedState(JFrame.MAXIMIZED_BOTH);
        vta.setUndecorated(true);
        vta.pack();
        vta.setLocationRelativeTo(null);
        vta.setVisible(true);
        
        vta.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                n.getGame().windowFocusLost();
            }            
        });
    }
}