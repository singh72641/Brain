package com.punwire.brain;

import java.util.Random;

/**
 * Created by admin on 7/17/2017.
 */
public class NodeConnection {
    public Node n;
    public float weight = 0.01f;
    public float prevWeight = 0.01f;
    static Random random = new Random();

    public NodeConnection(Node n, float weight) {
        this.n = n;
        this.weight = weight;
        this.prevWeight = this.weight;
    }

    public static NodeConnection create(Node n){
        float mult = 1;
        if( random.nextBoolean() ) mult = -1.0f;
        return  new NodeConnection(n,random.nextFloat() * mult);
    }
}
