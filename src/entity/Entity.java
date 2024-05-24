package entity;

import arg.*;
import arg.args.Energy;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

public class Entity implements Cloneable, Mutation {


    @Passable4IntensiveProperty
    double x = 0;
    @Passable4IntensiveProperty
    double y = 0;
    @Passable4IntensiveProperty
    double velocity = 0;
    @Passable4IntensiveProperty
    double acceleration = 0;

    @Mutable
    double maxMass;
    @Mutable
    double maxVolume;
    @Mutable
    @Passable4ExtensiveProperty
    Energy energy = new Energy();

    @Passable4ExtensiveProperty
    double mass;
    @Passable4ExtensiveProperty
    double volume;
    @Passable4ExtensiveProperty
    @Mutable
    PassType passType = PassType.A;

    private Entity() {
    }

    public Entity(double maxMass, double maxVolume) {
        this.maxMass = maxMass;
        this.maxVolume = maxVolume;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        new ArrayList<>().clone();
        Entity e1 = (Entity) super.clone();
        for (Field field : Arrays.stream(this.getClass().getDeclaredFields()).filter(f -> f.getAnnotation(Passable4ExtensiveProperty.class) != null).toList()) {
            boolean accessible = field.canAccess(this);
            field.setAccessible(true);
            try {
                if (field.get(e1) instanceof Double) {
                    switch (passType) {
                        case A:
                            field.set(e1, (double) field.get(this) / 2);
                            field.set(this, (double) field.get(this) / 2);
                            break;
                        case B:
                            field.set(e1, (double) field.get(this) / 20);
                            field.set(this, (double) field.get(this) * (19 / 20));
                            break;
                    }
                }
            } catch (IllegalAccessException ignored) {
            }
            field.setAccessible(accessible);
        }
        for (Field field : Arrays.stream(this.getClass().getDeclaredFields()).filter(f -> f.getAnnotation(Passable4IntensiveProperty.class) != null).toList()) {
            boolean accessible = field.canAccess(this);
            field.setAccessible(true);
            try {
                field.set(e1, field.get(this));
            } catch (IllegalAccessException ignored) {
            }
            field.setAccessible(accessible);
        }
        for (Field field : Arrays.stream(this.getClass().getDeclaredFields()).filter(f -> f.getAnnotation(Passable4Class.class) != null).toList()) {
            boolean accessible = field.canAccess(this);
            field.setAccessible(true);
            try {
                if (field.get(this) instanceof Passable) {
                    Passable son = (Passable) field.get(this);
                    (Passable)this.pass(son);
                }
            } catch (IllegalAccessException ignored) {
            }
            field.setAccessible(accessible);
        }

        return e1;
    }

    public Entity reproduce() throws CloneNotSupportedException {
        Entity e = (Entity) this.clone();
        e.mutate();
        return e;
    }

    @Override
    public String toString() {
        return "Entity" + "{" +
                "mass=" + maxMass +
                ", volume=" + maxVolume +
                '}';
    }
}
