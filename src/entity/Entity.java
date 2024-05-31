package entity;

import property.EntityType;
import property.PassType;
import property.properties.Energy;
import until.Lib;
import until.Vector_Math;

public class Entity extends AbstractEntity {
    public Entity(double maxMass, double maxVolume) {
        super(maxMass, maxVolume);
    }

    @Override
    public AbstractEntity reproduce() throws CloneNotSupportedException, IllegalAccessException {
        Entity e = (Entity) this.clone();
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
        if (getAcceleration() == null || getAcceleration().length() == 0) {
            setAcceleration(new Vector_Math(new double[]{(rand.nextDouble(2) - 1) * 0.2 * getMaxAcceleration(), (rand.nextDouble(2) - 1) * 0.2 * getMaxAcceleration()}));
        } else {
            this.getAcceleration().add(new Vector_Math(new double[]{(rand.nextDouble(2) - 1) * 0.2 * getMaxAcceleration(), (rand.nextDouble(2) - 1) * 0.2 * getMaxAcceleration()}));
            Vector_Math oldAcceleration = this.getAcceleration();
            if (this.getAcceleration().length() > getMaxAcceleration()) {
                this.setAcceleration(oldAcceleration.clone());
                this.getAcceleration().multi(getMaxAcceleration() / getAcceleration().length());
            }
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


        switch (getEntityType()) {
            case PRODUCER:
                double result = Math.min(getMaxEnergyGenerateRate() * rand.nextFloat(), Lib.currentEnergyFromSun);
                if (entities.size() > 2E3) {
//                    System.out.println(currentEnergyFromSun);
                }
                this.getEnergy().add(result);
                Lib.currentEnergyFromSun -= result;
                break;
            case CONSUMER:
                break;
        }

        if (this.getEnergy().getAllEnergy4AllType() / this.getEnergy().getMaxEnergyVolume() > 0.3D && (this.getEnergy().getValue4Type((int) getEnergy().getPreferEnergyType()) / getEnergy().getMaxEnergyVolume4Type((int) getEnergy().getPreferEnergyType())) > 0.5D) {
            double d = this.setMass(Math.min(this.getMass() + ((this.getEnergy().getValue4Type((int) getEnergy().getPreferEnergyType())) / 5), this.getMaxMass()));
            this.getEnergy().get(d);
            this.getEnergy().get(0.5f * d * getVelocity().dot(getVelocity()));
        }

        if (this.getEnergy().getAllEnergy4AllType() / this.getEnergy().getMaxEnergyVolume() > 0.3D && (this.getEnergy().getValue4Type((int) getEnergy().getPreferEnergyType()) / getEnergy().getMaxEnergyVolume4Type((int) getEnergy().getPreferEnergyType())) > 0.5D && this.getMass() / this.getMaxMass() > 0.6) {
            Entity e = (Entity) this.reproduce();
            entities.add(e);
            e.tick();
        }


        if (getEnergy().getAllEnergy4AllType() / getEnergy().getMaxEnergyVolume() <= 0.19d) {
            entities.remove(this);
        }
        if (this.getMass() <= 0) {
            entities.remove(this);
        }
        if (this.getVelocity().length() <= 0.001) {
            entities.remove(this);
        }
    }
}
