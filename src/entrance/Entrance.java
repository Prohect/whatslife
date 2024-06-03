package entrance;

import entity.AbstractEntity;
import entity.Entity;
import event.MyMouseInputListener4MainPanel;
import map.Map;
import property.EntityType;
import property.PassType;
import property.properties.Energy;
import render.MyFrame;
import render.MyJPanel;
import until.Lib;

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
    public static final long maxTime = (long) 1E4;

    public static void main(String[] args) throws IllegalAccessException, CloneNotSupportedException, IOException {
        //logs init
        File logRoot = new File("log");
        if (!logRoot.exists()) logRoot.mkdir();
        File logFile = new File(logRoot, System.currentTimeMillis() + ".log");
        File historyLogFile = new File(logRoot, "history" + System.currentTimeMillis() + ".csv");
        logFile.createNewFile();
        historyLogFile.createNewFile();
        PrintWriter writer = new PrintWriter(logFile);
        PrintWriter historyWriter = new PrintWriter(historyLogFile);

        //statistics
        ArrayList<AbstractEntity> producerEntities = AbstractEntity.producerEntities;
        ArrayList<AbstractEntity> consumerEntities = AbstractEntity.consumerEntities;
        producerEntities.add(new Entity(0.2, 3, PassType.A, EntityType.PRODUCER, new Energy(1d), 0.7d, 10, 10));
        double sumEnergyLastTick = 0;
        AtomicBoolean flag = new AtomicBoolean(true);

        //GUI init
        MyFrame myFrame = new MyFrame(new int[]{1920, 1080});
        MyJPanel panel = myFrame.getPanel();
        AtomicBoolean running = new AtomicBoolean(true), pressed = new AtomicBoolean(false), hasWheelMove = new AtomicBoolean(false);
        panel.addMouseListener(new MyMouseInputListener4MainPanel(running, pressed));
        panel.addMouseWheelListener(e -> hasWheelMove.set(true));

        //the every first tick and then draw the GUI
        tick(producerEntities, flag, consumerEntities, sumEnergyLastTick, writer, panel, myFrame);
        myFrame.setVisible(true);
        myFrame.paint(myFrame.getGraphics());

        //tick processing loop
        while (running.get() && time < maxTime && !producerEntities.isEmpty()) {
            if (pressed.get() || hasWheelMove.get()) {
                try {
                    tickAndPaint(producerEntities, flag, consumerEntities, sumEnergyLastTick, writer, panel, myFrame);
                } catch (CloneNotSupportedException | IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                } finally {
                    hasWheelMove.set(false);
                }
            }
        }

        //History log outPut
        AbstractEntity.entitiesHistory.forEach((aLong, abstractEntities) -> {
            abstractEntities.forEach(abstractEntity -> {
                historyWriter.print(aLong + ",");
                historyWriter.println(abstractEntity.toString());
            });
        });
        historyWriter.flush();

        //exit
        System.out.println("on close");
        myFrame.dispose();
        historyWriter.close();
        writer.close();

        //old tests, fold them pls
/*
        Vector_Math vector_math1 = new Vector_Math(new double[]{-1d, 2d, 0});
        Vector_Math vector_math2 = new Vector_Math(new double[]{1d, -1d, 0});
        var vector_math3 = vector_math1.cross(vector_math2);
        System.out.println(vector_math1.dot(vector_math2));
        System.out.println(vector_math3.dot(vector_math1));
        System.out.println(vector_math3.dot(vector_math2));
        System.out.println(vector_math3);


        System.out.println(vector_math1.dot(vector_math3));


        Short short1 = 15;
        Double b = Double.valueOf(short1) ;
        Entity e1 = new Entity(1, 1);
        Entity e2 = e1.reproduce();

        PassType a = PassType.A;
        PassType b2 = a;
        a = PassType.B;
        System.out.println(b2);
        System.out.println(a);


        System.out.println(e1);
        System.out.println(e2);
        Arrays.stream(e1.getClass().getDeclaredFields()).filter(f -> !f.getName().equals("preferEnergyType")).forEach(field -> {
            try {
                field.setAccessible(true);
                System.out.println(field.getName() + " " + (field.get(e1).getClass()) + " " + (field.get(e1).getClass().isEnum()));
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        });*/

    }

    private static void tick(ArrayList<AbstractEntity> producerEntities, AtomicBoolean flag, ArrayList<AbstractEntity> consumerEntities, double sumEnergyLastTick, PrintWriter writer, MyJPanel panel, MyFrame myFrame) throws CloneNotSupportedException, IllegalAccessException {
        if (flag.get()) {
            ++time;
            Lib.currentEnergyFromSun = 10;
            int producersSize = producerEntities.size();
            for (int i = 0; i < producersSize; i++) {
                AbstractEntity producer = producerEntities.get(producersSize - 1 - i);
                producer.tick(time);
            }
            int consumersSize = consumerEntities.size();
            for (int i = 0; i < consumersSize; i++) {
                AbstractEntity consumer = consumerEntities.get(consumersSize - 1 - i);
                consumer.tick(time);
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
                flag.set(false);
            }
            writer.println(time + "\t" + producerEntities.size() + "\t" + consumerEntities.size() + "\t" + sum);
            sumEnergyLastTick = sum;
            writer.flush();

            //render update
            ArrayList<AbstractEntity> copy = (ArrayList<AbstractEntity>) producerEntities.clone();
            copy.addAll(consumerEntities);
            panel.setRenders(copy);
//            myFrame.paint(myFrame.getGraphics());
//            panel.paint(myFrame.getGraphics());

//            System.out.println("test frame");

        }
    }

    private static void tickAndPaint(ArrayList<AbstractEntity> producerEntities, AtomicBoolean flag, ArrayList<AbstractEntity> consumerEntities, double sumEnergyLastTick, PrintWriter writer, MyJPanel panel, MyFrame myFrame) throws CloneNotSupportedException, IllegalAccessException {
        tick(producerEntities, flag, consumerEntities, sumEnergyLastTick, writer, panel, myFrame);
        panel.paint(myFrame.getGraphics());
    }
}