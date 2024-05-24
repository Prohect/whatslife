package property.properties;

import property.*;

@Tick
public class Energy implements Mutation, Passable<Energy> {
    @Mutable(minValue = 0, maxValue = 8)//there's now only 8 energy types
    @Passable4IntensiveProperty
    double preferEnergyType;
    //preferEnergyType works as the index of the follow array
    @Passable4ExtensiveProperty
    double[] energy = new double[8];
    @Mutable
    double[] energyUsedThisTick = new double[8];
    @Mutable
    @Passable4IntensiveProperty
    double[] maxPower = new double[8];
    @Mutable
    @Passable4IntensiveProperty
    double[] energy2massRate = new double[8];

    public Energy(double[] energy2massRate, double[] maxPower, double preferEnergyType) {
        this.energy2massRate = energy2massRate;
        this.maxPower = maxPower;
        this.preferEnergyType = preferEnergyType;
    }

    public Energy() {
    }

    public boolean get(double d) {
        int i = (int) Math.floor(preferEnergyType);
        if (able(d, i)) {
            return get(d, i);
        }
        for (int j = 0; j < 6; j++) {
            if (i == j) continue;
            if (able(d, j)) {
                return get(d, j);
            }
        }
        return false;
    }

    private boolean get(double d, int i) {
        energyUsedThisTick[i] += d;
        energy[i] -= d;
        return true;
    }

    private boolean able(double v, int i) {
        return energyUsedThisTick[i] + v < maxPower[i] && energy[i] >= v;
    }

    @Tick
    void Tick() {
        for (double d : energyUsedThisTick) {
            d = 0;
        }
    }

}
