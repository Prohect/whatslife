package property.properties;

import entity.AbstractEntity;
import property.*;

import java.util.Arrays;

public class Energy implements Mutation, Passable<Energy>, Tick, Cloneable {
    @Mutable(minValue = 0, maxValue = 7)//there's now only 8 energy types
//    @Passable4IntensiveProperty
    private double preferEnergyType;
    //    @Passable4IntensiveProperty
    private int currentEnergyType;
    //preferEnergyType works as the index of the follow array
    @Passable4ExtensiveProperty
    private double[] energy = new double[8];
    @Mutable
    private double[] energyUsedThisTick = new double[8];
    @Mutable
//    @Passable4IntensiveProperty
    private double[] maxPower = new double[8];
    @Mutable(minValue = 1E3, maxValue = 5E6, step = 1E-2)
//    @Passable4IntensiveProperty
    private double[] mass2energyRate = new double[8];
    private AbstractEntity abstractEntity;

    public Energy(double[] mass2energyRate, double[] maxPower, int preferEnergyType, AbstractEntity abstractEntity) {
        this.mass2energyRate = mass2energyRate;
        this.maxPower = maxPower;
        this.preferEnergyType = preferEnergyType;
        this.abstractEntity = abstractEntity;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Energy energy1 = (Energy) super.clone();
        energy1.energy = this.energy.clone();
        energy1.energyUsedThisTick = this.energyUsedThisTick.clone();
        energy1.maxPower = this.maxPower.clone();
        energy1.mass2energyRate = this.mass2energyRate.clone();

        return energy1;
    }

    public void setCurrentEnergyType(int currentEnergyType) {
        this.currentEnergyType = currentEnergyType;
    }

    public Energy(double energy) {
        this.energy[0] = energy;
        this.maxPower = new double[]{1, 1, 1, 1, 1, 1, 1, 1};
        this.mass2energyRate = new double[]{1E5, 0.5E5, 0.5E5, 0.5E5, 0.5E5, 0.5E5, 0.5E5, 0.5E5};
        this.preferEnergyType = 0;
    }

    public double getValue4Type(int i) {
        return energy[i];
    }

    public void setEntity(AbstractEntity abstractEntity) {
        this.abstractEntity = abstractEntity;
    }

    public Energy(AbstractEntity abstractEntity) {
        this.abstractEntity = abstractEntity;
    }

    public double getPreferEnergyType() {
        return preferEnergyType;
    }

    public double[] getEnergy() {
        return energy;
    }

    public double[] getEnergyUsedThisTick() {
        return energyUsedThisTick;
    }

    public double[] getMaxPower() {
        return maxPower;
    }

    public double[] getMass2energyRate() {
        return mass2energyRate;
    }

    public double getMaxEnergyVolume() {
        double result = 0;
        for (int i = 0; i < energy.length; i++) {
            result += mass2energyRate[i] * abstractEntity.getMass();
        }
        return result;
    }

    public double getMaxEnergyVolume4Type(int i) {

        return abstractEntity.getMass() * mass2energyRate[i];
    }

    public double getAllEnergy4AllType() {
        double result = 0;
        for (double v : energy) {
            result += v;
        }
        return result;
    }

    public boolean get(double d) {
        if (d <= 0) {
            this.add(-d);
            return true;
        }
//        var stackTrace = Thread.currentThread().getStackTrace();
//        if (stackTrace.length - 1 >= 0)
//            System.out.println(stackTrace[stackTrace.length - 1] + " invoke Energy.get() " + d);
        int i = (int) Math.floor(preferEnergyType);
        if (able(d, i)) {
            setCurrentEnergyType(i);
            return get(d, i);
        }
        for (int j = 0; j < 8; j++) {
            if (i == j) continue;
            if (able(d, j)) {
                setCurrentEnergyType(j);
                return get(d, j);
            }
        }
        return false;
    }

    public void hardGet(double d) {
        if (d <= 0) {
            this.add(-d);
            return;
        }
        double d2 = d;
        int i = (int) Math.floor(preferEnergyType);
        if (able(d, i)) {
            setCurrentEnergyType(i);
            get(d, i);
            return;
        } else {
            d2 -= getValue4Type(i);
            get(d, i);
            d = d2;
        }
        for (int j = 0; j < 8; j++) {
            if (i == j) continue;
            if (able(d, j)) {
                setCurrentEnergyType(j);
                get(d, j);
                return;
            } else {
                d2 -= getValue4Type(i);
                get(d, j);
                d = d2;
                setCurrentEnergyType(j);
            }
        }
    }

    @Override
    public String toString() {
        return String.valueOf(getAllEnergy4AllType());
    }

    public double getAll4currentType() {
        double result = Math.min(energy[currentEnergyType], maxPower[currentEnergyType] - energyUsedThisTick[currentEnergyType]);
        energy[currentEnergyType] -= result;
        if (energy[currentEnergyType] <= 0) {
            if (currentEnergyType + 1 < 8) {
                setCurrentEnergyType(currentEnergyType + 1);
            } else {
                setCurrentEnergyType(0);
            }
        }
        return result;
    }

    private boolean get(double d, int i) {
        energyUsedThisTick[i] += d;
        energy[i] -= d;
        return true;
    }

    public void add(double d) {
        if (energyPreferred() + d < maxEnergyPreferred()) {
            energy[(int) Math.floor(preferEnergyType)] += d;
            setCurrentEnergyType((int) Math.floor(preferEnergyType));
            return;
        } else if (energyPreferred() < maxEnergyPreferred()) {
            d -= mass2energyRate[(int) Math.floor(preferEnergyType)] * abstractEntity.getMass() - energy[(int) Math.floor(preferEnergyType)];
            energy[(int) Math.floor(preferEnergyType)] = mass2energyRate[(int) Math.floor(preferEnergyType)] * abstractEntity.getMass();
            setCurrentEnergyType((int) Math.floor(preferEnergyType));
        }
        for (int i = 0; i < energy.length; i++) {
            if (i == (int) Math.floor(preferEnergyType)) continue;
            else {
                if (energy[i] + d < maxEnergy(i)) {
                    energy[i] += d;
                    break;
                } else if (energy[i] < maxEnergy(i)) {
                    d -= maxEnergy(i) - energy[i];
                    energy[i] = maxEnergy(i);
                }
            }
        }
    }

    private double maxEnergy(int i) {
        return mass2energyRate[i] * abstractEntity.getMass();
    }

    private double maxEnergyPreferred() {
        return maxEnergy((int) Math.floor(preferEnergyType));
    }

    private double energyPreferred() {
        return energy[(int) Math.floor(preferEnergyType)];
    }


    private boolean able(double v, int i) {
        return energyUsedThisTick[i] + v < maxPower[i] && energy[i] >= v;
    }

    @Override
    public void tick(long t) {
        Arrays.fill(energyUsedThisTick, 0);
        for (int i = 0; i < energy.length; i++) {
            if (i == (int) Math.floor(preferEnergyType)) continue;
            if (able(getMaxPower()[i] * 0.2, i)) {
                energy[(int) Math.floor(preferEnergyType)] += getMaxPower()[i] * 0.2;
                get(getMaxPower()[i] * 0.2, i);
            }
        }
        currentEnergyType = (int) Math.floor(preferEnergyType);
    }
}
