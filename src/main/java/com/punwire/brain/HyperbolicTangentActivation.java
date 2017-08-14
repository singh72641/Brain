package com.punwire.brain;

/**
 * Created by admin on 7/16/2017.
 */
public class HyperbolicTangentActivation implements IActivation {
    @Override
    public float activate(float weightedSum) {
        double a = Math.exp(weightedSum);
        double b = Math.exp(-weightedSum);
        return (float)((a-b)/(a+b));
    }

    @Override
    public float derivative(float weightedSum) {
        return (float)(1 - Math.pow(activate(weightedSum), 2.0));
    }
}
