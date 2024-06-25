package render;

import javax.swing.*;
import java.awt.*;

public class MyFrame extends JFrame {

    static long counter = 0;

    private int[] size;
    private MyJPanel panel;


    public MyFrame(int[] size) {
        this.size = size;
        init();
    }

    public void init() {
        this.setSize(size[0], size[1]);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new MyJPanel();
        this.setContentPane(panel);
        this.setTitle("MyFrame");
    }

    public void setSize(int[] size) {
        this.size = size;
        this.setSize(size[0], size[1]);
        this.repaint();
    }


    public MyJPanel getPanel() {
        return panel;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        counter++;
//        System.out.println("MyFrame.paint() + " + counter);
        panel.paint(g);
    }
}
