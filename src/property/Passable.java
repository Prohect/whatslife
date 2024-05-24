package arg;

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
            } else {
                field.set(son, field.get(this));
            }
            field.setAccessible(accessible);
        }
        fields = Arrays.stream(this.getClass().getDeclaredFields()).filter(field -> field.getAnnotation(Passable4ExtensiveProperty.class) != null).toList();
        for (Field field : fields) {
            switch (type) {
                case A:
                    field.set(son, (double) field.get(this) / 2);
                    field.set(this, (double) field.get(this) / 2);
                    break;
                case B:
                    field.set(son, (double) field.get(this) / 20);
                    field.set(this, (double) field.get(this) * (19 / 20));
                    break;
            }
        }

    }
}
