package entity;

import entrance.Entrance;
import property.EntityType;
import property.PassType;
import property.properties.Energy;
import until.Lib;
import until.Vector_Math;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Entity extends AbstractEntity {


    public Entity(double maxMass, double maxVolume) {
        super(maxMass, maxVolume);
    }

    @Override
    public AbstractEntity reproduce() throws CloneNotSupportedException, IllegalAccessException {
        Entity e = (Entity) super.reproduce();
        this.pass(getPassType(), e);
        e.mutate();
        return e;
    }

    public Entity(double mass, double volume, PassType passType, EntityType entityType, Energy energy, double maxEnergyGenerateRate, double maxVelocity, double maxAcceleration) {
        super(mass, volume, passType, entityType, energy, maxEnergyGenerateRate, maxVelocity, maxAcceleration);
    }

    //note:because the tick of a consumer may cause a producer die,
    //consumers can only be updated after all the producers have been updated, otherwise u get ConcurrentModificationException
    @Override
    public void tick(long t) throws CloneNotSupportedException, IllegalAccessException {
        super.tick(t);
        //TODO:acceleration process needed by brain
        switch (getEntityType()) {
            case PRODUCER:
                double result = Math.min(getMaxEnergyGenerateRate() * rand.nextFloat(), getEnergyGenerateRatio() * Lib.currentEnergyFromSun / AbstractEntity.producerEntities.size());
                this.getEnergy().add(result);
                Lib.currentEnergyFromSun -= result;
                AbstractEntity e = getClosestConsumerEntity();
                if (e != null) {
                    Vector_Math deltaPos = this.getPos().clone().sub(e.getPos());
                    if (deltaPos.length() < this.getSafeDistance()) this.setAcceleration(deltaPos);
                    else
                        this.setAcceleration(getAcceleration().clone().add((new Vector_Math(new double[]{(rand.nextDouble(2) - 1) * 0.2 * getMaxAcceleration(), (rand.nextDouble(2) - 1) * 0.2 * getMaxAcceleration()}))));
                } else
                    this.setAcceleration(getAcceleration().clone().add((new Vector_Math(new double[]{(rand.nextDouble(2) - 1) * 0.2 * getMaxAcceleration(), (rand.nextDouble(2) - 1) * 0.2 * getMaxAcceleration()}))));

                break;
            case CONSUMER:
                if (getTargetOfConsumer() == null || !producerEntities.contains(getTargetOfConsumer()))
                    setTargetOfConsumer(getClosestProducerEntity());
                if (this.tryEat(this.getTargetOfConsumer())) {
                    tryEatAllNearProducer();
                    setTargetOfConsumer(getClosestProducerEntity());
                    if (getTargetOfConsumer() == null)
                        this.setAcceleration(getAcceleration().clone().add((new Vector_Math(new double[]{(rand.nextDouble(2) - 1) * 0.2 * getMaxAcceleration(), (rand.nextDouble(2) - 1) * 0.2 * getMaxAcceleration()}))));
                    else this.setAcceleration(getTargetOfConsumer().getPos().clone().sub(this.getPos()));
                } else {
                    if (getTargetOfConsumer() == null) {
                        //there's no producer at all, so just do random move
                        this.setAcceleration(getAcceleration().clone().add((new Vector_Math(new double[]{(rand.nextDouble(2) - 1) * 0.2 * getMaxAcceleration(), (rand.nextDouble(2) - 1) * 0.2 * getMaxAcceleration()}))));
                        this.getAcceleration().multi(getRateOfMaxAccelerationOnChasingTarget());
                    } else {
                        this.setAcceleration(getTargetOfConsumer().getPos().clone().sub(this.getPos()));
                        this.getAcceleration().multi(getRateOfMaxAccelerationOnChasingTarget());
                    }

                }
                break;
        }

        //velocity & energy
        this.getEnergy().tick(getCurrentTick());
        if (getEntityType() == EntityType.PRODUCER) {
            this.getVelocity().multi(0.8d);
        }
        if (getEntityType() == EntityType.CONSUMER && getTargetOfConsumer() != null) {
            double vi = getVelocity().dot(getTargetOfConsumer().getPos().clone().sub(this.getPos())) / getTargetOfConsumer().getPos().clone().sub(this.getPos()).length();
            if (vi < 0) {
                vi *= -0.35D;
                this.setVelocity(getTargetOfConsumer().getPos().clone().sub(this.getPos()));
                this.getVelocity().multi(vi / this.getVelocity().length());
            }
            this.getVelocity().multi(0.95D);
        }
        Vector_Math velocity1 = this.getVelocity().clone();
        velocity1.add(getAcceleration());

        //TODO:the value of the arg of this get could be negative
        if (this.getEnergy().get(0.5f * (this.getMass() * (velocity1.dot(velocity1) - getVelocity().dot(getVelocity()))))) {
            getVelocity().add(getAcceleration());
        } else {
            double e = this.getEnergy().getAll4currentType();
            if (e > 0) {
                Vector_Math newVelocity = this.getVelocity().clone();
                newVelocity.multi(Math.sqrt(((0.5 * this.getMass() * this.getVelocity().dot(this.getVelocity())) + e) / (0.5 * this.getMass() * this.getVelocity().dot(this.getVelocity()))));
                this.setAcceleration(newVelocity.clone());
                this.getAcceleration().sub(this.getVelocity());
                this.setVelocity(newVelocity);
            }
        }
        if (getVelocity().length() > getMaxVelocity()) {
            getVelocity().multi(getMaxVelocity() / getVelocity().length());
        }

        //pos update
        getPos().add(getVelocity());
        posCheck();

        //mass update
        if (this.getEnergy().getAllEnergy4AllType() / this.getEnergy().getMaxEnergyVolume() > 0.2D && (this.getEnergy().getValue4Type((int) getEnergy().getPreferEnergyType()) / getEnergy().getMaxEnergyVolume4Type((int) getEnergy().getPreferEnergyType())) > 0.5D) {
            double d = this.setMass(Math.min(this.getMass() + ((this.getEnergy().getValue4Type((int) getEnergy().getPreferEnergyType())) / 5), this.getMaxMass()));
            this.getEnergy().hardGet(d);
            this.getEnergy().hardGet(0.5f * d * getVelocity().dot(getVelocity()));
        }

        //die
        if (getEnergy().getAllEnergy4AllType() / getEnergy().getMaxEnergyVolume() <= (getEntityType() == EntityType.PRODUCER ? 0.1D : 0.05D)) {
            this.die();
            return;
        }
        if (this.getMass() <= 0.0001) {
            this.die();
            return;
        }
        if (this.getEnergy().getAllEnergy4AllType() <= (getEntityType() == EntityType.PRODUCER ? 0.1D : 0.05D)) {
            this.die();
            return;
        }
        if (this.getVelocity().length() <= 0.1) {
            this.die();
            return;
        }

        //reproduce
        if (this.getEnergy().getAllEnergy4AllType() / this.getEnergy().getMaxEnergyVolume() > 0.3D && (this.getEnergy().getValue4Type((int) getEnergy().getPreferEnergyType()) / getEnergy().getMaxEnergyVolume4Type((int) getEnergy().getPreferEnergyType())) > 0.5D && this.getMass() / this.getMaxMass() > 0.6) {
            Entity e = (Entity) this.reproduce();
            switch (e.getEntityType()) {
                case CONSUMER:
                    consumerEntities.add(e);
                    // when this is producer while the son is a consumer,
                    // the son would eat this for they have equal pos ,
                    // making the producerEntities list remove this ,
                    // causing ConcurrentModificationException where the list is iterated ,
                    // so e.tick() cant be invoke that way !
                    // also thinking about this e.tick() may reproduce another consumer causing the problem
                    if (this.getEntityType() == EntityType.CONSUMER) {
                        e.tick(this.getCurrentTick());
                    }
                    break;
                case PRODUCER:
                    producerEntities.add(e);
                    e.tick(this.getCurrentTick());
                    break;
            }
        }

        //add this instant state to history log
        ArrayList<AbstractEntity> abstractEntities = AbstractEntity.entitiesHistory.get(this.getUuid());
        if (abstractEntities != null) {
            abstractEntities.add(this.clone());
        } else {
            abstractEntities = new ArrayList<>();
            abstractEntities.add(this);
            AbstractEntity.entitiesHistory.put(this.getUuid(), abstractEntities);
        }
    }

    private void posCheck() {
        if (getPos().getY() > Entrance.map.getMaxY()) {
            getPos().setY(Entrance.map.getMaxY());
            setVelocity(getVelocity().getRotateVector2D(-2 * getVelocity().getAngle()));
            setAcceleration(getAcceleration().getRotateVector2D(-2 * getAcceleration().getAngle()));
        } else if (getPos().getY() < Entrance.map.getMinY()) {
            getPos().setY(Entrance.map.getMinY());
            setVelocity(getVelocity().getRotateVector2D(-2 * getVelocity().getAngle()));
            setAcceleration(getAcceleration().getRotateVector2D(-2 * getAcceleration().getAngle()));
        }
        if (getPos().getX() > Entrance.map.getMaxX()) {
            getPos().setX(Entrance.map.getMaxX());
            setVelocity(getVelocity().getRotateVector2D(-2 * (getVelocity().getAngle() - (Math.PI / 2))));
            setAcceleration(getAcceleration().getRotateVector2D(-2 * (getAcceleration().getAngle() - (Math.PI / 2))));
        } else if (getPos().getX() < Entrance.map.getMinX()) {
            getPos().setX(Entrance.map.getMinX());
            setVelocity(getVelocity().getRotateVector2D(-2 * (getVelocity().getAngle() - (Math.PI / 2))));
            setAcceleration(getAcceleration().getRotateVector2D(-2 * (getAcceleration().getAngle() - (Math.PI / 2))));
        }
    }

    private boolean tryEat(AbstractEntity entity) {
        if (entity == null) return false;
        Vector_Math deltaPos = entity.getPos().clone().sub(this.getPos());
        if (deltaPos.length() < this.getReachOfKillAura() || deltaPos.length() < this.getReachOfKillAura() + (this.getVelocity().dot(deltaPos) / deltaPos.length())) {
            this.getEnergy().add(entity.getEnergy().getAllEnergy4AllType() * this.getEnergyTransferRate());
            entity.die();
            return true;
        }
        return false;
    }

    private void tryEatAllNearProducer() {
        if (producerEntities.isEmpty()) return;
        AbstractEntity entity = getClosestEntityFromList(producerEntities);
        Vector_Math deltaPos = entity.getPos().clone().sub(this.getPos());
        while (deltaPos.length() <= this.getReachOfKillAura()) {
            tryEat(entity);
            entity = getClosestEntityFromList(producerEntities);
            if (entity == null) return;
            deltaPos = entity.getPos().clone().sub(this.getPos());
        }
    }

    private AbstractEntity getClosestEntityFromList(List<AbstractEntity> list) {
        AtomicReference<AbstractEntity> target = new AtomicReference<>();
        AtomicReference<Double> minDistance = new AtomicReference<>(Double.MAX_VALUE);
        list.forEach(entity -> {
            Vector_Math distanceVector = entity.getPos().clone();
            distanceVector.sub(this.getPos());
            if (distanceVector.length() < minDistance.get()) {
                target.set(entity);
                minDistance.set(distanceVector.length());
            }
        });
        return target.get();
    }

    private AbstractEntity getClosestProducerEntity() {
        return getClosestEntityFromList(producerEntities);
    }

    private AbstractEntity getClosestConsumerEntity() {
        return getClosestEntityFromList(consumerEntities);
    }
}
