package com.punwire.brain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by admin on 7/16/2017.
 */
abstract public class Node {
    NodeType type;
    IActivation activation;
    float output=0.0f;
    int id;
    int layer=0;
    int seq = -1;
    float utility=0.0f;
    public List<NodeConnection> connections = new ArrayList<>();
    Random random = new Random();
    String lastAction="";

    protected Node(int id, NodeType t, int lay){
        this.id = id;
        this.type = t;
        this.layer = lay;
    }

    public NodeType getType() {
        return type;
    }

    public float getOutput() {
        return output;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    abstract public float calcOutput(int s);

    public float positive(float r) {
       lastAction = "Positive: " + r;
       if( type == NodeType.DN)
       {
           //This is DN
           utility += r;
       }
        for(NodeConnection nc: connections)
        {
            nc.n.positive(r);
        }
        return 0;
    }

    public String getLastAction() {
        return lastAction;
    }

    public float getUtility() {
        return utility;
    }

    public float negative(float p) {
        lastAction = "Negative: " + p;
        if( type == NodeType.DN) {
            //This is DN
            utility -= p;
        }
        for(NodeConnection nc: connections)
        {
            nc.n.negative(p);
        }
        return 0;
    }

    public String key()
    {
        return type.name() + "-" + id;
    }

    public void setOutput(int seq, float o){
        this.seq = seq;
        this.output = o;
    }
}
