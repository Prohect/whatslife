package entity;

import property.*;
import property.properties.Energy;
import until.Lib;
import until.Vector_Math;

import java.util.ArrayList;

public class Entity implements Passable<Entity>, Mutation, Tick, Cloneable {


    public static ArrayList<Entity> entities = new ArrayList<>();


    @Passable4IntensiveProperty
    private Vector_Math pos;
    @Passable4IntensiveProperty
    private Vector_Math velocity;
    @Mutable(minValue = 1)
    @Passable4IntensiveProperty
    private double maxVelocity;
    @Passable4IntensiveProperty
    private Vector_Math acceleration;
    @Mutable(minValue = 1E-1)
    @Passable4IntensiveProperty
    private double maxAcceleration;

    @Mutable
    private double maxMass;
    @Mutable
    private double maxVolume;
    @Mutable
    @Passable4Class
    private Energy energy;

    @Passable4ExtensiveProperty
    private double mass;
    @Passable4ExtensiveProperty
    private double volume;
    @Passable4ExtensiveProperty
    @Mutable
    private PassType passType = PassType.A;
    @Passable4IntensiveProperty
    @Mutable
    private EntityType entityType;
    @Passable4IntensiveProperty
    @Mutable
    private double maxEnergyGenerateRate;

    private Entity() {
    }

    public Entity(double maxMass, double maxVolume) {
        this.maxMass = maxMass;
        this.maxVolume = maxVolume;
    }

    public void setAcceleration(Vector_Math acceleration) {
        if (acceleration.length() >= maxAcceleration) {
            acceleration.multi(maxAcceleration / acceleration.length());
        }
        this.acceleration = acceleration;
    }

    public Vector_Math getAcceleration() {
        return acceleration;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Entity clone = (Entity) super.clone();
        clone.energy = (Energy) this.energy.clone();
        clone.velocity = this.velocity.clone();
        clone.acceleration = this.acceleration.clone();
        clone.pos = this.pos.clone();
        return clone;
    }

    public Entity(double mass, double volume, PassType passType, EntityType entityType, Energy energy, double maxEnergyGenerateRate, double maxVelocity, double maxAcceleration) {
        this.mass = mass;
        this.maxMass = mass * 3;
        this.volume = volume;
        this.passType = passType;
        this.entityType = entityType;
        this.energy = energy;
        this.energy.setEntity(this);
        this.maxEnergyGenerateRate = maxEnergyGenerateRate;
        this.velocity = new Vector_Math(new double[2]);
        this.pos = new Vector_Math(new double[]{rand.nextDouble(2) - 1, rand.nextDouble(2) - 1});
        this.maxVelocity = maxVelocity;
        this.maxAcceleration = maxAcceleration;
    }

    public double getX() {
        return pos.getVector()[0];
    }

    public double getY() {
        return pos.getVector()[1];
    }

    public Vector_Math getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector_Math velocity) {
        this.velocity = velocity;
    }

    public double getMaxMass() {
        return maxMass;
    }

    public void setMaxMass(double maxMass) {
        this.maxMass = maxMass;
    }

    public double getMaxVolume() {
        return maxVolume;
    }

    public void setMaxVolume(double maxVolume) {
        this.maxVolume = maxVolume;
    }

    public Energy getEnergy() {
        return energy;
    }

    public void setEnergy(Energy energy) {
        this.energy = energy;
    }

    public double getMass() {
        return mass;
    }

    public double setMass(double mass) {
        double result = mass - this.mass;
        this.mass = mass;
        return result;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public PassType getPassType() {
        return passType;
    }

    public void setPassType(PassType passType) {
        this.passType = passType;
    }

    public Entity reproduce() throws CloneNotSupportedException, IllegalAccessException {
        Entity e = (Entity) this.clone();
        this.pass(passType, e);
        e.mutate();
        return e;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "velocity=" + velocity.length() +
                ", acceleration=" + getAcceleration().length() +
                ", energy=" + energy +
                ", mass=" + mass +
                ", maxEnergyGenerateRate=" + maxEnergyGenerateRate +
                '}';
    }

    @Override
    public void tick() throws CloneNotSupportedException, IllegalAccessException {
        String string = "";
        //TODO:acceleration process needed by brain
        if (getAcceleration() == null || getAcceleration().length() == 0) {
            setAcceleration(new Vector_Math(new double[]{(rand.nextDouble(2) - 1) * 0.2 * maxAcceleration, (rand.nextDouble(2) - 1) * 0.2 * maxAcceleration}));
            string = String.valueOf(getAcceleration().length());
            if (string.equals("NaN")) {
                string = "null";
            }
        } else {
            string = String.valueOf(getAcceleration().length());
            if (string.equals("NaN")) {
                string = "null";
            }
            this.getAcceleration().add(new Vector_Math(new double[]{(rand.nextDouble(2) - 1) * 0.2 * maxAcceleration, (rand.nextDouble(2) - 1) * 0.2 * maxAcceleration}));
//            this.acceleration.multi(Math.min(maxAcceleration / acceleration.length(), 1));
            string = String.valueOf(getAcceleration().length());
            if (string.equals("NaN")) {
                string = "null";
            }
            Vector_Math oldAcceleration = this.getAcceleration();
            //causing NaN
            if (this.getAcceleration().length() > maxAcceleration) {
                this.setAcceleration(oldAcceleration.clone());
                this.getAcceleration().multi(maxAcceleration / getAcceleration().length());
            }
            string = String.valueOf(getAcceleration().length());
            if (string.equals("NaN")) {
                string = "null";
            }
        }


        //velocity & energy
        this.energy.tick();
        this.velocity.multi(0.8d);
        Vector_Math velocity1 = this.velocity.clone();
        velocity1.add(getAcceleration());
        string = String.valueOf(getVelocity().length());
        if (string.equals("NaN")) {
            string = "null";
        }

        //TODO:the value of the arg of this get could be negative
        if (this.energy.get(0.5f * (this.mass * (velocity1.dot(velocity1) - velocity.dot(velocity))))) {
            velocity.add(getAcceleration());
        } else {
            double e = this.energy.getAll4currentType();
            if (e > 0) {
                Vector_Math newVelocity = this.velocity.clone();
                string = String.valueOf(newVelocity.length());
                if (string.equals("NaN")) {
                    string = "null";
                }
                newVelocity.multi(((0.5 * this.getMass() * this.velocity.dot(this.velocity)) + e) / (0.5 * this.getMass() * this.velocity.dot(this.velocity)));
                string = String.valueOf(newVelocity.length());
                if (string.equals("NaN")) {
                    string = "null";
                }
                this.setAcceleration(newVelocity.clone());
                this.getAcceleration().sub(this.velocity);
                string = String.valueOf(getAcceleration().length());
                if (string.equals("NaN")) {
                    string = "null";
                }
                this.velocity = newVelocity;
            }
        }


//        this.velocity.multi(Math.min(maxVelocity / velocity.length(), 1));
        if (velocity.length() > maxVelocity) {
            velocity.multi(maxVelocity / velocity.length());
        }
        //pos
        pos.add(velocity);


        switch (entityType) {
            case PRODUCER:
                double result = Math.min(maxEnergyGenerateRate * rand.nextFloat(), Lib.currentEnergyFromSun);
                if (entities.size() > 2E3) {
//                    System.out.println(currentEnergyFromSun);
                }
                this.energy.add(result);
                Lib.currentEnergyFromSun -= result;
                break;
            case CONSUMER:
                break;
        }

        if (this.energy.getAllEnergy4AllType() / this.energy.getMaxEnergyVolume() > 0.3D && (this.energy.getValue4Type((int) energy.getPreferEnergyType()) / energy.getMaxEnergyVolume4Type((int) energy.getPreferEnergyType())) > 0.5D) {
            double d = this.setMass(Math.min(this.getMass() + ((this.energy.getValue4Type((int) energy.getPreferEnergyType())) / 2), this.getMaxMass()));
            this.energy.get(d);
        }

        if (this.energy.getAllEnergy4AllType() / this.energy.getMaxEnergyVolume() > 0.3D && (this.energy.getValue4Type((int) energy.getPreferEnergyType()) / energy.getMaxEnergyVolume4Type((int) energy.getPreferEnergyType())) > 0.5D && this.getMass() / this.getMaxMass() > 0.6) {
            Entity e = this.reproduce();
            entities.add(e);
            e.tick();
        }


        if (energy.getAllEnergy4AllType() / energy.getMaxEnergyVolume() <= 0.19d) {
            entities.remove(this);
        }
        if (this.mass <= 0) {
            entities.remove(this);
        }
    }
}
