package property;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface Passable<T> extends Cloneable {
    default void pass(PassType type, T son) throws IllegalAccessException {
        List<Field> fields = new ArrayList<>();
        Class<?> clazz = this.getClass();
        do {
            fields.addAll(Arrays.stream(clazz.getDeclaredFields()).filter(field -> field.getAnnotation(Passable4Class.class) != null
//                            || field.getAnnotation(Passable4IntensiveProperty.class) != null
            ).toList());
            clazz = clazz.getSuperclass();
        } while (clazz != Object.class);
        for (Field field : fields) {
            boolean accessible = field.canAccess(this);
            field.setAccessible(true);
            if (field.get(this) instanceof Passable) {//Passable4Class
                ((Passable) field.get(this)).pass(type, field.get(son));
            }
/*            else if (field.getAnnotation(Passable4IntensiveProperty.class)!=null) {
                if (field.get(this) instanceof Cloneable cloneable){
//                    field.set(son,((Object) cloneable).clone());
                }
            }*/
            field.setAccessible(accessible);

        }
        fields = new ArrayList<>();
        clazz = this.getClass();
        do {
            fields.addAll(Arrays.stream(clazz.getDeclaredFields()).filter(field -> field.getAnnotation(Passable4ExtensiveProperty.class) != null).toList());
            clazz = clazz.getSuperclass();
        } while (clazz != Object.class);
        for (Field field : fields) {
            boolean accessible = field.canAccess(this);
            field.setAccessible(true);
            if (field.get(this) instanceof double[] array1) {
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
