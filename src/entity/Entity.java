package entity;

import property.*;
import property.properties.Energy;

public class Entity implements Passable<Entity>, Mutation {


    @Passable4IntensiveProperty
    double x = 0;
    @Passable4IntensiveProperty
    double y = 0;
    @Passable4IntensiveProperty
    double[] velocity = {0, 0};
    @Passable4IntensiveProperty
    double[] acceleration = {0, 0};

    @Mutable
    double maxMass;
    @Mutable
    double maxVolume;
    @Mutable
    @Passable4Class
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


    public Entity reproduce() throws CloneNotSupportedException, IllegalAccessException {
        Entity e = (Entity) this.clone();
        this.pass(passType, e);
        e.mutate();
        return e;
    }

    @Override
    public String toString() {
        return "Entity" + "{" + "mass=" + maxMass + ", volume=" + maxVolume + '}';
    }
}
