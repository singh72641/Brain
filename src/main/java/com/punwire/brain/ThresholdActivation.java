package com.punwire.brain;

import sun.rmi.server.Activation;

/**
 * Created by admin on 7/16/2017.
 */
public class ThresholdActivation implements IActivation {

    private double threshold;

    public ThresholdActivation(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public float activate(float weightedSum) {
        return weightedSum > threshold ? 1 : 0;
    }

    @Override
    public float derivative(float weightedSum) {
        return 0;
    }
}
