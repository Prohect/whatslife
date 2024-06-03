package render;

import entity.AbstractEntity;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyJPanel extends JPanel {

    private ArrayList<AbstractEntity> renders = new ArrayList<>();

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2D = (Graphics2D) g;
        g2D.setColor(Color.WHITE);
        g2D.fillRect(0, 0, 1920, 1080);
        g2D.setColor(Color.MAGENTA);
        g2D.drawLine(0, 540, 1920, 540);
        g2D.drawLine(960, 0, 960, 1080);
        renders.forEach(entityRenderer -> entityRenderer.paint(g2D));


//        var color = g2D.getColor();
//        g2D.setColor(Color.BLUE);
//        g2D.drawOval(900 + random.nextInt(120), 500 + random.nextInt(80), 20, 20);
//        g2D.setColor(color);
    }

    public void paint(Graphics g, AtomicBoolean frameUpdated) {
        frameUpdated.set(false);
        this.paint(g);
        frameUpdated.set(true);
    }

    public ArrayList<AbstractEntity> getRenders() {
        return renders;
    }

    public void setRenders(ArrayList<AbstractEntity> renders) {
        this.renders = renders;
    }
}
