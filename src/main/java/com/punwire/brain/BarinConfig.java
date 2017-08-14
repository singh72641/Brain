package com.punwire.brain;

/**
 * Created by admin on 7/18/2017.
 */
public class BarinConfig {
    public static Brain brain=null;
    public static Train train=null;
    public static Brain get(){
        if(brain != null) return brain;
        brain = new Brain(1,1,1);
        brain.init();
        System.out.println("+++++++++++ Created Brain ++++++++++++++");
        train = new Train(brain);
        train.setInputs();
        train.setOutputs();
        return brain;
    }
}
