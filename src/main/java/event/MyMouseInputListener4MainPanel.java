package event;

import javax.swing.event.MouseInputListener;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyMouseInputListener4MainPanel implements MouseInputListener {

    private final AtomicBoolean running;
    private final AtomicBoolean pressed;

    public MyMouseInputListener4MainPanel(AtomicBoolean running, AtomicBoolean pressed) {
        this.running = running;
        this.pressed = pressed;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            running.set(false);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        pressed.set(true);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        pressed.set(false);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
