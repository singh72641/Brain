package com.punwire.brain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 7/14/2017.
 */
public class Neuron extends Node{

    public Neuron(int id, NodeType type, int layer) {
        super(id,type,layer);
    }

    public float calcOutput(int s){
        if( s == this.seq) return this.output;
        if( type == NodeType.NP ) {
            this.output = 1.0f;
            this.seq = s;
        }
        else if( type == NodeType.NN ) {
            this.output = -1.0f;
            this.seq = s;
        }
        //System.out.println("Calculating Output for " + this.key());
        this.output = 0.0f;
        this.seq = s;
        for(NodeConnection nc: connections)
        {
            this.output +=  nc.weight * nc.n.calcOutput(s);
        }
        if( activation != null ) this.output = activation.activate(this.output);
        return this.output;
    }

}
