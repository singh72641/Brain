package com.punwire.brain;

public class ReversalActivation implements IActivation  {
    @Override
    public float activate(float weightedSum) {
        return 1 - weightedSum;
    }

    @Override
    public float derivative(float weightedSum) {
        return 1 - weightedSum;
    }
}
