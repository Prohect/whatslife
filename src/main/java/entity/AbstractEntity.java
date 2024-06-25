package entity;

import property.*;
import property.properties.Energy;
import render.EntityRenderer;
import util.Vector_Math;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public abstract class AbstractEntity implements Passable<AbstractEntity>, Mutation, Tick, Cloneable {

    //statics
    public static ArrayList<AbstractEntity> consumerEntities = new ArrayList<>();
    public static HashMap<Long, ArrayList<AbstractEntity>> entitiesHistory = new HashMap<>();
    public static ArrayList<AbstractEntity> producerEntities = new ArrayList<>();
    final Random random = new Random();
    private long uuid = random.nextLong();
    private long currentTick;

    private EntityRenderer entityRenderer = new EntityRenderer(this);

    //below r properties that really matter

    //note: basic vars would be 'deep cloned' in clone() automatically
    //note: other vars would be 'deep cloned' in clone(), or pass() if marked by @Passable4Class or @Passable4ExtensiveProperty, check the override clone()
    //note: @Passable4ExtensiveProperty means it's an extensive property which needs to be divided by the entity and its child in reproducing

    private Vector_Math pos;
    private Vector_Math velocity;
    @Mutable(minValue = 1)
    private double maxVelocity;
    private Vector_Math acceleration;
    @Mutable(minValue = 1E-1)
    private double maxAcceleration;
    @Mutable(minValue = 1.02E-9, maxValue = 1E9)
    private double maxMass = 5E-4;
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
    @Mutable(step = 1E-2)
    private PassType passType = PassType.A;
    @Mutable
    private EntityType entityType;
    @Mutable(minValue = 1E-1)
    private double maxEnergyGenerateRate = 1;
    @Mutable(minValue = 5E-1, maxValue = 1E2)
    private float energyGenerateRatio = 10;
    @Mutable(minValue = 1E-2, maxValue = 20)
    private double reachOfKillAura = 1;
    @Mutable(minValue = 1E-2, maxValue = 1)
    private double energyTransferRate = 0.7;
    @Mutable(minValue = 1E-2, maxValue = 40)
    private double safeDistance = 3;
    @Mutable(minValue = 0.4, maxValue = 0.9)
    private double rateOfMaxAccelerationOnChasingTarget = 0.4;
    private AbstractEntity targetOfConsumer;

    private AbstractEntity() {
        if (getAcceleration() == null || getAcceleration().length() == 0) {
            setAcceleration(new Vector_Math(new double[]{(rand.nextDouble(2) - 1) * 0.2 * getMaxAcceleration(), (rand.nextDouble(2) - 1) * 0.2 * getMaxAcceleration()}));
        }
    }

    public AbstractEntity(double maxMass, double maxVolume) {
        this.maxMass = maxMass;
        this.maxVolume = maxVolume;
        if (getAcceleration() == null || getAcceleration().length() == 0) {
            setAcceleration(new Vector_Math(new double[]{(rand.nextDouble(2) - 1) * 0.2 * getMaxAcceleration(), (rand.nextDouble(2) - 1) * 0.2 * getMaxAcceleration()}));
        }
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
        if (getAcceleration() == null || getAcceleration().length() == 0) {
            setAcceleration(new Vector_Math(new double[]{(rand.nextDouble(2) - 1) * 0.2 * getMaxAcceleration(), (rand.nextDouble(2) - 1) * 0.2 * getMaxAcceleration()}));
        }
        this.velocity = getAcceleration().clone();
    }

    @Override
    public void tick(long t) throws CloneNotSupportedException, IllegalAccessException {
        this.currentTick = t;
    }

    @Override
    protected AbstractEntity clone() throws CloneNotSupportedException {
        AbstractEntity clone = (AbstractEntity) super.clone();
        clone.energy = (Energy) this.energy.clone();
        clone.velocity = this.velocity.clone();
        clone.acceleration = this.acceleration.clone();
        clone.pos = this.pos.clone();
        clone.entityRenderer = this.entityRenderer.clone(clone);
        return clone;
    }

    public AbstractEntity reproduce() throws CloneNotSupportedException, IllegalAccessException {
        AbstractEntity e = this.clone();
        e.uuid = e.random.nextLong();
        this.pass(passType, e);
        e.mutate();
        return e;
    }

    public void die() {
        if (this.getEntityType() == EntityType.PRODUCER) {
            producerEntities.remove(this);
        } else {
            consumerEntities.remove(this);
        }
    }

    @Override
    public String toString() {
        //why this is not clazz{a[num],bs[num1, num2]} pattern?
        //to output these thing into a csv, then I could us excel to process these data

        //if some property is ignored, probably it's mechanism not coded yet or not helpful in data processing
        return "AbstractEntity" +
//                ", uuid," + uuid +
                ", tick," + currentTick +
                ", pos," + pos +
                ", velocity," + velocity +
                ", maxVelocity," + maxVelocity +
                ", acceleration," + acceleration +
                ", maxAcceleration," + maxAcceleration +
                ", maxMass," + maxMass +
//                ", maxVolume," + maxVolume +
                ", energy," + energy +
                ", mass," + mass +
//                ", volume," + volume +
                ", passType," + passType +
                ", entityType," + entityType +
                ", maxEnergyGenerateRate," + maxEnergyGenerateRate +
                ", reachOfKillAura," + reachOfKillAura +
                ", energyTransferRate," + energyTransferRate +
                ", safeDistance," + safeDistance +
                ", rateOfMaxAccelerationOnChasingTarget," + rateOfMaxAccelerationOnChasingTarget +
//                ", targetOfConsumer," + targetOfConsumer+
                ""
                ;
    }

    public void paint(Graphics g) {
        entityRenderer.paint(g);
    }

    public void setAcceleration(Vector_Math acceleration) {
        if (acceleration.length() >= maxAcceleration) {
            acceleration.multi(maxAcceleration / acceleration.length());
        }
        this.acceleration = acceleration;
    }

    public void setVelocity(Vector_Math velocity) {
        if (velocity.length() >= maxVelocity) {
            velocity.multi(maxVelocity / velocity.length());
        }
        this.velocity = velocity;
    }

    public double setMass(double mass) {
        double result = mass - this.mass;
        this.mass = mass;
        if (mass <= 1E-9) {
            System.out.println("AbstractEntity.setMass");
        }
        return result;
    }


    //below r just automatically generated getters and setters, so no real mechanism inside
    //don't need to read them

    //if unexpected things happens, these getters and setters could help u find why in debug for they just limit the access to these private properties
    //that's why I write all properties here in an abstract clazz

    public Random getRandom() {
        return random;
    }

    public Vector_Math getAcceleration() {
        return acceleration;
    }

    public long getUuid() {
        return uuid;
    }

    public void setUuid(long uuid) {
        this.uuid = uuid;
    }

    public void setCurrentTick(long currentTick) {
        this.currentTick = currentTick;
    }

    public EntityRenderer getEntityRenderer() {
        return entityRenderer;
    }

    public void setEntityRenderer(EntityRenderer entityRenderer) {
        this.entityRenderer = entityRenderer;
    }

    public Vector_Math getPos() {
        return pos;
    }

    public void setPos(Vector_Math pos) {
        this.pos = pos;
    }

    public Vector_Math getVelocity() {
        return velocity;
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
//        if (mass <= 1E-9) {
//            System.out.println("AbstractEntity.getMass");
//        }
        return mass;
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

    public float getEnergyGenerateRatio() {
        return energyGenerateRatio;
    }

    public void setEnergyGenerateRatio(float energyGenerateRatio) {
        this.energyGenerateRatio = energyGenerateRatio;
    }

    public double getReachOfKillAura() {
        return reachOfKillAura;
    }

    public void setReachOfKillAura(double reachOfKillAura) {
        this.reachOfKillAura = reachOfKillAura;
    }

    public double getEnergyTransferRate() {
        return energyTransferRate;
    }

    public void setEnergyTransferRate(double energyTransferRate) {
        this.energyTransferRate = energyTransferRate;
    }

    public double getSafeDistance() {
        return safeDistance;
    }

    public void setSafeDistance(double safeDistance) {
        this.safeDistance = safeDistance;
    }

    public double getRateOfMaxAccelerationOnChasingTarget() {
        return rateOfMaxAccelerationOnChasingTarget;
    }

    public void setRateOfMaxAccelerationOnChasingTarget(double rateOfMaxAccelerationOnChasingTarget) {
        this.rateOfMaxAccelerationOnChasingTarget = rateOfMaxAccelerationOnChasingTarget;
    }

    public long getCurrentTick() {
        return currentTick;
    }

    public AbstractEntity getTargetOfConsumer() {
        return targetOfConsumer;
    }

    public void setTargetOfConsumer(AbstractEntity targetOfConsumer) {
        this.targetOfConsumer = targetOfConsumer;
    }
}
