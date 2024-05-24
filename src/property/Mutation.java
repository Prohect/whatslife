package property;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public interface Mutation {
    Random rand = new Random();

    default double nextDouble2() {
        return (2 * rand.nextDouble() - 1);
    }

    default void mutate() {
        List<Field> fields = Arrays.stream(this.getClass().getDeclaredFields()).toList();
        for (Field field : fields) {
            boolean accessible = field.canAccess(this);
            field.setAccessible(true);
            if (field.getAnnotation(Mutable.class) != null) {
                try {
                    if (field.get(this) instanceof Mutation) {//非基本数据类型
                        ((Mutation) field.get(this)).mutate();
                    } else if (field.get(this) instanceof double[] array) {
                        Mutable mutable = field.getAnnotation(Mutable.class);
                        for (int i = 0; i < array.length; i++) {
                            double v = array[i] + mutable.step() * nextDouble2();
                            v = Math.max(mutable.minValue(), v);
                            v = Math.min(mutable.maxValue(), v);
                            array[i] = v;
                        }
                    } else if (field.get(this) instanceof Double) {
                        Mutable mutable = field.getAnnotation(Mutable.class);
                        double d = (double) field.get(this) + mutable.step() * nextDouble2();
                        d = Math.max(mutable.minValue(), d);
                        d = Math.min(mutable.maxValue(), d);
                        field.set(this, d);
                    } else if (field.get(this).getClass().isEnum()) {
                        if (rand.nextDouble() < field.getAnnotation(Mutable.class).step()) {
                            field.set(this, field.get(this).getClass().getEnumConstants()[rand.nextInt(field.get(this).getClass().getEnumConstants().length)]);
                        }
                    }
                } catch (Exception e) {
                }
            }
            field.setAccessible(accessible);
        }
    }
}
