package eventos;

import static utils.Constantes.Direccion.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import elementos.JefeFinal;
import juego.PanelJuego;

public class EntraTeclado implements KeyListener {
  private PanelJuego pan;

  public EntraTeclado(PanelJuego pan) {
    this.pan = pan;
  }

  @Override
  public void keyTyped(KeyEvent e) {
    // char c = e.getKeyChar();
    // for (JefeFinal j : pan.getGame().getEnemyManager().getJefesFinales()) {
    // if (j.isActivo() && Character.isDigit(c)) {
    // j.agregarCaracter(c);
    // }
    // }
  }

  @Override
  public void keyPressed(KeyEvent e) {
    switch (e.getKeyCode()) {
      case KeyEvent.VK_ESCAPE:
        System.out.println("Juego pausado. Presiona ESC nuevamente para continuar.");
        break;
      case KeyEvent.VK_UP:
        pan.getGame().getPlayer().setUp(true);
        break;
      case KeyEvent.VK_DOWN:
        pan.getGame().getPlayer().setDown(true);
        break;
      case KeyEvent.VK_LEFT:
      case KeyEvent.VK_A:
        pan.getGame().getPlayer().setLeft(true);
        break;
      case KeyEvent.VK_RIGHT:
      case KeyEvent.VK_D:
        pan.getGame().getPlayer().setRight(true);
        break;
      case KeyEvent.VK_NUMPAD5:
        pan.getGame().getPlayer().setAttacking(true);
        break;
      case KeyEvent.VK_SPACE:
      case KeyEvent.VK_W:
        pan.getGame().getPlayer().setJump(true);
        break;
      // case KeyEvent.VK_BACK_SPACE:
      // for (JefeFinal j : pan.getGame().getEnemyManager().getJefesFinales()) {
      // if (j.isActivo())
      // j.borrarCaracter();
      // }
      // break;
      case KeyEvent.VK_ENTER:
        if (pan.getGame().isVictoria() || pan.getGame().isGameOver() || pan.getGame().isEnInicio()) {
          pan.getGame().reiniciarDesdePantalla();
        }

        // else {
        // for (JefeFinal j : pan.getGame().getEnemyManager().getJefesFinales()) {
        // if (j.isActivo())
        // j.confirmarRespuesta();
        // }
        // }
        break;
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    switch (e.getKeyCode()) {
      case KeyEvent.VK_UP:
        pan.getGame().getPlayer().setUp(false);
        break;
      case KeyEvent.VK_DOWN:
        pan.getGame().getPlayer().setDown(false);
        break;
      case KeyEvent.VK_LEFT:
      case KeyEvent.VK_A:
        pan.getGame().getPlayer().setLeft(false);
        break;
      case KeyEvent.VK_RIGHT:
      case KeyEvent.VK_D:
        pan.getGame().getPlayer().setRight(false);
        break;
      case KeyEvent.VK_SPACE:
      case KeyEvent.VK_W:
        pan.getGame().getPlayer().setJump(false);
        break;
      case KeyEvent.VK_NUMPAD5:
        pan.getGame().getPlayer().setAttacking(false);
        break;
    }
  }
}