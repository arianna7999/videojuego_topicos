package juego;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import javax.swing.JFrame;
import javax.swing.JPanel;

import elementos.pantallas.MenuPrincipal;

public class VtaJuego extends JFrame {

    private JFrame vta;
    private JPanel contenedor;
    private CardLayout cardLayout;

    public VtaJuego(PanelJuego n) {
        vta = new JFrame();
        vta.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        vta.setUndecorated(true); // 1. Quitar bordes primero

        vta.setExtendedState(JFrame.MAXIMIZED_BOTH);
        cardLayout = new CardLayout();
        contenedor = new JPanel(cardLayout);
        MenuPrincipal menu = new MenuPrincipal(this);
        contenedor.add(menu, "MENU");
        contenedor.add(n, "JUEGO");

        vta.add(contenedor);
        vta.setVisible(true);

        vta.revalidate();
        vta.repaint();
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

    public void mostrarMenu() {
        this.getContentPane().removeAll();
        elementos.pantallas.MenuPrincipal menu = new elementos.pantallas.MenuPrincipal(this);
        this.add(menu);
        this.revalidate();
        this.repaint();
        menu.requestFocusInWindow();
    }

    public void mostrarJuego() {
        cardLayout.show(contenedor, "JUEGO");
        contenedor.getComponent(1).requestFocusInWindow();
    }

}