package entrance;

import entity.AbstractEntity;
import entity.Entity;
import map.Map;
import property.EntityType;
import property.PassType;
import property.properties.Energy;
import render.MyFrame;
import render.MyJPanel;
import until.Lib;

import javax.swing.event.MouseInputListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
public class Entrance {


    public static Map map = new Map();


    static int sameStatsTicks = 0;

    public static long time = 0;


    public static void main(String[] args) throws IllegalAccessException, CloneNotSupportedException, IOException {
        ArrayList<AbstractEntity> producerEntities = AbstractEntity.producerEntities;
        ArrayList<AbstractEntity> consumerEntities = AbstractEntity.consumerEntities;

        for (int i = 0; i < 1; i++) {
            producerEntities.add(new Entity(0.2, 3, PassType.A, EntityType.PRODUCER, new Energy(1d), 0.7d, 10, 10));
        }

        File logFile = new File("log" + System.currentTimeMillis() + ".txt");
        if (!logFile.exists()) {
            logFile.createNewFile();
        }
        PrintWriter writer = new PrintWriter(logFile);

        double sumEnergyLastTick = 0;

        boolean flag = true;

        MyFrame myFrame = new MyFrame(new int[]{1920, 1080});

        MyJPanel panel = myFrame.getPanel();

        AtomicBoolean pressed = new AtomicBoolean(false);
        AtomicBoolean hasWheelMove = new AtomicBoolean(false);
        AtomicBoolean running = new AtomicBoolean(true);
        panel.addMouseListener(new MouseInputListener() {


            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == 3) {
                    running.set(false);
                }
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
        });

        panel.addMouseWheelListener(e -> {
            hasWheelMove.set(true);
        });

        myFrame.setVisible(true);
        //the every first tick
        tick(producerEntities, flag, consumerEntities, sumEnergyLastTick, writer, panel, myFrame);


        while (running.get() && time < 1E4 && !producerEntities.isEmpty()) {
            if (pressed.get() || hasWheelMove.get()) {
                try {
//                    System.out.println("mousePressed");
                    tick(producerEntities, flag, consumerEntities, sumEnergyLastTick, writer, panel, myFrame);
                } catch (CloneNotSupportedException | IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
                hasWheelMove.set(false);
            }
        }

        System.out.println("on close");

        myFrame.dispose();

        writer.close();

//        Vector_Math vector_math1 = new Vector_Math(new double[]{-1d, 2d, 0});
//        Vector_Math vector_math2 = new Vector_Math(new double[]{1d, -1d, 0});
//        var vector_math3 = vector_math1.cross(vector_math2);
//        System.out.println(vector_math1.dot(vector_math2));
//        System.out.println(vector_math3.dot(vector_math1));
//        System.out.println(vector_math3.dot(vector_math2));
//        System.out.println(vector_math3);


//        System.out.println(vector_math1.dot(vector_math3));


//        Short short1 = 15;
//        Double b = Double.valueOf(short1) ;
//        Entity e1 = new Entity(1, 1);
//        Entity e2 = e1.reproduce();
//
//        PassType a = PassType.A;
//        PassType b2 = a;
//        a = PassType.B;
//        System.out.println(b2);
//        System.out.println(a);
//
//
//        System.out.println(e1);
//        System.out.println(e2);
//        Arrays.stream(e1.getClass().getDeclaredFields()).filter(f -> !f.getName().equals("preferEnergyType")).forEach(field -> {
//            try {
//                field.setAccessible(true);
//                System.out.println(field.getName() + " " + (field.get(e1).getClass()) + " " + (field.get(e1).getClass().isEnum()));
//            } catch (IllegalAccessException ex) {
//                ex.printStackTrace();
//            }
//        });

    }

    private static void tick(ArrayList<AbstractEntity> producerEntities, boolean flag, ArrayList<AbstractEntity> consumerEntities, double sumEnergyLastTick, PrintWriter writer, MyJPanel panel, MyFrame myFrame) throws CloneNotSupportedException, IllegalAccessException {
        if ((!producerEntities.isEmpty()) && time < 1E4 && flag) {
            ++time;
            Lib.currentEnergyFromSun = 10;
            int producersSize = producerEntities.size();
            for (int i = 0; i < producersSize; i++) {
                AbstractEntity producer = producerEntities.get(producersSize - 1 - i);
                producer.tick();
            }
            int consumersSize = consumerEntities.size();
            for (int i = 0; i < consumersSize; i++) {
                AbstractEntity consumer = consumerEntities.get(consumersSize - 1 - i);
                consumer.tick();
            }
            if (producerEntities.size() >= 1E3) {
                AbstractEntity e = producerEntities.get((int) (producerEntities.size() * Math.random()) - 1);
                System.out.println(e);
            }
            if (consumerEntities.size() >= 1E3) {
                AbstractEntity e = consumerEntities.get((int) (consumerEntities.size() * Math.random()) - 1);
                System.out.println(e);
            }
            double sum = 0;

            for (AbstractEntity producerEntity : producerEntities) {
                sum += producerEntity.getEnergy().getAllEnergy4AllType();
            }
            for (AbstractEntity consumerEntity : consumerEntities) {
                sum += consumerEntity.getEnergy().getAllEnergy4AllType();
            }
            if (sum - sumEnergyLastTick == 0) {
                sameStatsTicks++;
            } else sameStatsTicks = 0;
            if (sameStatsTicks >= 100) {
                flag = false;
            }
            writer.println(time + "\t" + producerEntities.size() + "\t" + consumerEntities.size() + "\t" + sum);
            sumEnergyLastTick = sum;
            writer.flush();

            //render update
            ArrayList<AbstractEntity> copy = (ArrayList<AbstractEntity>) producerEntities.clone();
            copy.addAll(consumerEntities);
            panel.setRenders(copy);
//            myFrame.paint(myFrame.getGraphics());
            panel.paint(myFrame.getGraphics());

//            System.out.println("test frame");

        }
    }
}