package com.punwire.brain;

/**
 * Created by admin on 7/16/2017.
 */
public interface IActivation {

    public float activate(float weightedSum);

    public float derivative(float weightedSum);
}
