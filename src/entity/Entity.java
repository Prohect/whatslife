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

    @Override
    public void tick() throws CloneNotSupportedException, IllegalAccessException {
        //TODO:acceleration process needed by brain
        switch (getEntityType()) {
            case PRODUCER:
                double result = Math.min(getMaxEnergyGenerateRate() * rand.nextFloat(), Lib.currentEnergyFromSun);
                this.getEnergy().add(result);
                Lib.currentEnergyFromSun -= result;
                AbstractEntity e = getClosestConsumerEntity();
                if (e != null) {
                    Vector_Math deltaPos = this.getPos().sub(e.getPos());
                    if (deltaPos.length() < this.getSafeDistance()) this.setAcceleration(deltaPos);
                    else
                        this.setAcceleration(getAcceleration().clone().add((new Vector_Math(new double[]{(rand.nextDouble(2) - 1) * 0.2 * getMaxAcceleration(), (rand.nextDouble(2) - 1) * 0.2 * getMaxAcceleration()}))));
                } else
                    this.setAcceleration(getAcceleration().clone().add((new Vector_Math(new double[]{(rand.nextDouble(2) - 1) * 0.2 * getMaxAcceleration(), (rand.nextDouble(2) - 1) * 0.2 * getMaxAcceleration()}))));

                break;
            case CONSUMER:
                if (this.tryEat((Entity) this.getTargetOfConsumer())) {
                    this.setAcceleration(getTargetOfConsumer().getPos().sub(this.getPos()));
                    ((Entity) getTargetOfConsumer()).die();
                    setTargetOfConsumer(getClosestProducerEntity());
                } else {
                    if (getTargetOfConsumer() == null || !producerEntities.contains(getTargetOfConsumer())) {
                        this.setTargetOfConsumer(getClosestProducerEntity());
                    }
                    if (getTargetOfConsumer() == null) {
                        //there's no producer at all, so just do random move
                        this.setAcceleration(getAcceleration().clone().add((new Vector_Math(new double[]{(rand.nextDouble(2) - 1) * 0.2 * getMaxAcceleration(), (rand.nextDouble(2) - 1) * 0.2 * getMaxAcceleration()}))));
                        this.getAcceleration().multi(getRateOfMaxAccelerationOnChasingTarget());
                    } else {
                        this.setTargetOfConsumer(getClosestProducerEntity());
                        this.setAcceleration(getTargetOfConsumer().getPos().sub(this.getPos()));
                        this.getAcceleration().multi(getRateOfMaxAccelerationOnChasingTarget());
                    }

                }
                break;
        }

        //velocity & energy
        this.getEnergy().tick();
        this.getVelocity().multi(0.7d);
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


//        this.velocity.multi(Math.min(maxVelocity / velocity.length(), 1));
        if (getVelocity().length() > getMaxVelocity()) {
            getVelocity().multi(getMaxVelocity() / getVelocity().length());
        }
        //pos
        getPos().add(getVelocity());
        posCheck();


        if (this.getEnergy().getAllEnergy4AllType() / this.getEnergy().getMaxEnergyVolume() > 0.3D && (this.getEnergy().getValue4Type((int) getEnergy().getPreferEnergyType()) / getEnergy().getMaxEnergyVolume4Type((int) getEnergy().getPreferEnergyType())) > 0.5D) {
            double d = this.setMass(Math.min(this.getMass() + ((this.getEnergy().getValue4Type((int) getEnergy().getPreferEnergyType())) / 5), this.getMaxMass()));
            this.getEnergy().get(d);
            this.getEnergy().get(0.5f * d * getVelocity().dot(getVelocity()));
        }


        if (getEnergy().getAllEnergy4AllType() / getEnergy().getMaxEnergyVolume() <= 0.11d) {
            this.die();
        }
        if (this.getMass() <= 0.0001) {
            this.die();
        }
        if (this.getEnergy().getAllEnergy4AllType() <= (this.getEntityType() == EntityType.CONSUMER ? 0.9 : 0.1)) {
            this.die();
        }
        if (this.getVelocity().length() <= 0.001) {
            this.die();
        }
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
                        e.tick();
                    }
                    break;
                case PRODUCER:
                    producerEntities.add(e);
                    e.tick();
                    break;
            }

        }
        ArrayList<AbstractEntity> abstractEntities = AbstractEntity.entitiesHistory.get(this.getUuid());
        if (abstractEntities != null) {
            abstractEntities.add(this.clone());
        } else {
            ArrayList<AbstractEntity> abstractEntities1 = new ArrayList<>();
            abstractEntities1.add(this);
            AbstractEntity.entitiesHistory.put(this.getUuid(), abstractEntities1);
        }
    }

    private void die() {
        producerEntities.remove(this);
        consumerEntities.remove(this);
    }

    private void posCheck() {
        if (getPos().getY() > Entrance.map.getMaxY()) {
            getPos().setY(Entrance.map.getMaxY());
            setVelocity(getVelocity().getRotateVector2D(-2 * getVelocity().getAngle()));
        } else if (getPos().getY() < Entrance.map.getMinY()) {
            getPos().setY(Entrance.map.getMinY());
            setVelocity(getVelocity().getRotateVector2D(-2 * getVelocity().getAngle()));
        }
        if (getPos().getX() > Entrance.map.getMaxX()) {
            getPos().setX(Entrance.map.getMaxX());
            setVelocity(getVelocity().getRotateVector2D(-2 * (getVelocity().getAngle() - (Math.PI / 2))));
        } else if (getPos().getX() < Entrance.map.getMinX()) {
            getPos().setX(Entrance.map.getMinX());
            setVelocity(getVelocity().getRotateVector2D(-2 * (getVelocity().getAngle() - (Math.PI / 2))));
        }
    }

    private boolean tryEat(Entity entity) {
        if (entity == null) return false;
        Vector_Math distanceVector = entity.getPos().clone();
        distanceVector.sub(this.getPos());
        if (distanceVector.length() < this.getReachOfKillAura()) {
            this.getEnergy().add(entity.getEnergy().getAllEnergy4AllType() * this.getEnergyTransferRate());
            return true;
        }
        return false;
    }

    private AbstractEntity getClosestProducerEntity() {
        return getClosestEntityFromList(producerEntities);
    }

    private AbstractEntity getClosestConsumerEntity() {
        return getClosestEntityFromList(consumerEntities);
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

}
