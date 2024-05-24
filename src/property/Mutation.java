package arg;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public interface Mutation {
    Random rand = new Random();

    default double nextDouble2() {
        return (2 * rand.nextDouble() - 1);
    }

    default public void mutate() {
        List<Field> fields = Arrays.stream(this.getClass().getDeclaredFields()).toList();
        for (Field field : fields) {
            boolean accessible = field.canAccess(this);
            field.setAccessible(true);
            if (field.getAnnotation(Mutable.class) != null) {
                try {
                    if (field.get(this) instanceof Mutation) {
                        ((Mutation) field.get(this)).mutate();
                    } else if (field.get(this) instanceof double[]) {
                        for (double v : ((double[]) field.get(this))) {
                            v += field.getAnnotation(Mutable.class).step() * nextDouble2();
                        }
                    } else if (field.get(this) instanceof Double) {
                        field.set(this, (double) field.get(this) + field.getAnnotation(Mutable.class).step() * nextDouble2());
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
