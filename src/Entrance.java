import entity.AbstractEntity;
import entity.Entity;
import property.EntityType;
import property.PassType;
import property.properties.Energy;
import until.Lib;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
public class Entrance {


    double d = 1;

    public static long time = 0;

    Entrance(double d) {
        this.d = d;
    }

    public static void main(String[] args) throws IllegalAccessException, CloneNotSupportedException, IOException {

        ArrayList<Entity> entities = AbstractEntity.entities;

        for (int i = 0; i < 5; i++) {
            entities.add(new Entity(0.2, 3, PassType.A, EntityType.PRODUCER, new Energy(1d), 0.5d, 10, 10));
        }

        File logFile = new File("log" + System.currentTimeMillis() + ".txt");
        if (!logFile.exists()) {
            logFile.createNewFile();
        }
        PrintWriter writer = new PrintWriter(logFile);

        double sumEnergyLastTick = 0;

        while (!entities.isEmpty() && time < 1E6) {
            ++time;
            Lib.currentEnergyFromSun = 1;
            int size = entities.size();
            for (int i = 0; i < size; i++) {
                entities.get(size - i - 1).tick();
            }
            if (entities.size() >= 8000) {
                AbstractEntity e = entities.get((int) (entities.size() * Math.random()) - 1);
                System.out.println(e);
            }
            double sum = 0;
            for (AbstractEntity abstractEntity : entities) {
                sum += abstractEntity.getEnergy().getAllEnergy4AllType();
            }
            writer.println(time + "\t" + entities.size() + "\t" + sum + "\t" + (sum - sumEnergyLastTick));
            sumEnergyLastTick = sum;
        }
        writer.flush();
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
}