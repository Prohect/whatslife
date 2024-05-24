import entity.Entity;
import property.PassType;

import java.util.Arrays;

//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
public class Main {
    double d = 1;

    Main(double d) {
        this.d = d;
    }

    public static void main(String[] args) throws IllegalAccessException, CloneNotSupportedException {

        Entity e1 = new Entity(1, 1);
        Entity e2 = e1.reproduce();

        PassType a = PassType.A;
        PassType b = a;
        a = PassType.B;
        System.out.println(b);


        System.out.println(e1);
        System.out.println(e2);
        Arrays.stream(e1.getClass().getDeclaredFields()).filter(f -> !f.getName().equals("preferEnergyType")).forEach(field -> {
            try {
                field.setAccessible(true);
                System.out.println(field.getName() + " " + (field.get(e1).getClass()) + " " + (field.get(e1).getClass().isEnum()));
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        });

    }
}