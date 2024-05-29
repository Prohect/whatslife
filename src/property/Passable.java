package property;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public interface Passable<T> extends Cloneable {
    default void pass(PassType type, T son) throws IllegalAccessException {
        List<Field> fields = Arrays.stream(this.getClass().getDeclaredFields()).filter(field -> field.getAnnotation(Passable4Class.class) != null || field.getAnnotation(Passable4IntensiveProperty.class) != null).toList();
        for (Field field : fields) {
            boolean accessible = field.canAccess(this);
            field.setAccessible(true);
            if (field.get(this) instanceof Passable) {//Passable4Class
                ((Passable) field.get(this)).pass(type, field.get(son));
            }
            field.setAccessible(accessible);

        }
        fields = Arrays.stream(this.getClass().getDeclaredFields()).filter(field -> field.getAnnotation(Passable4ExtensiveProperty.class) != null).toList();
        for (Field field : fields) {
            boolean accessible = field.canAccess(this);
            field.setAccessible(true);
            if (field.get(this) instanceof double[]) {
                double[] array1 = (double[]) field.get(this);
                double[] array2 = (double[]) field.get(son);
                for (int i = 0; i < array1.length; i++) {
                    switch (type) {
                        case A:
                            array1[i] *= 0.5;
                            array2[i] *= 0.5;
                            break;
                        case B:
                            array2[i] = array2[i] / 20;
                            array1[i] = array1[i] * 19 / 20;
                            break;
                    }
                }
            } else if (field.get(this).getClass().isEnum()) {
                field.set(son, field.get(this));
            } else {
                switch (type) {
                    case A:
                        field.set(son, (double) field.get(son) / 2);
                        field.set(this, (double) field.get(this) / 2);
                        break;
                    case B:
                        field.set(son, (double) field.get(son) / 20);
                        field.set(this, (double) field.get(this) * (19 / 20));
                        break;
                }
            }
            field.setAccessible(accessible);
        }

    }
}
