package com.punwire.brain;

/**
 * Created by admin on 7/17/2017.
 */
public class LimiterActivation implements IActivation {


    @Override
    public float activate(float weightedSum) {
        return weightedSum > 1 ? 1 : ( weightedSum < -1 ? -1: weightedSum );
    }

    @Override
    public float derivative(float weightedSum) {
        return 0;
    }
}