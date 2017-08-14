package com.punwire.brain;

/**
 * Created by admin on 7/16/2017.
 */
public class SigmoidActivation implements IActivation {
    @Override
    public float activate(float weightedSum) {
        return (float)(1.0 / (1 + Math.exp(-1.0 * weightedSum)));
    }

    @Override
    public float derivative(float weightedSum) {
        return (float)(weightedSum * (1.0 - weightedSum));
    }
}
