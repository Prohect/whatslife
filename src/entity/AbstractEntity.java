package entity;

import property.*;
import property.properties.Energy;
import until.Vector_Math;

import java.util.ArrayList;

public abstract class AbstractEntity implements Passable<AbstractEntity>, Mutation, Tick, Cloneable {


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

    private AbstractEntity() {
    }

    public AbstractEntity(double maxMass, double maxVolume) {
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
        AbstractEntity clone = (AbstractEntity) super.clone();
        clone.energy = (Energy) this.energy.clone();
        clone.velocity = this.velocity.clone();
        clone.acceleration = this.acceleration.clone();
        clone.pos = this.pos.clone();
        return clone;
    }

    public AbstractEntity(double mass, double volume, PassType passType, EntityType entityType, Energy energy, double maxEnergyGenerateRate, double maxVelocity, double maxAcceleration) {
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

    public AbstractEntity reproduce() throws CloneNotSupportedException, IllegalAccessException {
        AbstractEntity e = (AbstractEntity) this.clone();
        this.pass(passType, e);
        e.mutate();
        return e;
    }

    public static ArrayList<Entity> getEntities() {
        return entities;
    }

    public static void setEntities(ArrayList<Entity> entities) {
        AbstractEntity.entities = entities;
    }

    public Vector_Math getPos() {
        return pos;
    }

    public void setPos(Vector_Math pos) {
        this.pos = pos;
    }

    public double getMaxVelocity() {
        return maxVelocity;
    }

    public void setMaxVelocity(double maxVelocity) {
        this.maxVelocity = maxVelocity;
    }

    public double getMaxAcceleration() {
        return maxAcceleration;
    }

    public void setMaxAcceleration(double maxAcceleration) {
        this.maxAcceleration = maxAcceleration;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public double getMaxEnergyGenerateRate() {
        return maxEnergyGenerateRate;
    }

    public void setMaxEnergyGenerateRate(double maxEnergyGenerateRate) {
        this.maxEnergyGenerateRate = maxEnergyGenerateRate;
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


}
