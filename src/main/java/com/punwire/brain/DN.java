package com.punwire.brain;

/**
 * Created by admin on 7/16/2017.
 */
public class DN extends Node {

    public DN(int id, int layer) {
        super(id,NodeType.DN, layer);
        this.activation = new LimiterActivation();
    }

    public float calcOutput(int s){
        if( s == this.seq) return this.output;
        //System.out.println("Calculating Output for " + this.key());
        this.output = 0.0f;
        this.seq = s;
        for(NodeConnection nc: connections)
        {
            this.output += nc.weight * nc.n.calcOutput(s);
        }
        if(activation != null) this.output =activation.activate(this.output);
        return this.output;
    }


}
