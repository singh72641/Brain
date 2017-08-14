package com.punwire.brain;

import org.neo4j.csv.reader.SourceTraceability;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 7/16/2017.
 */
public class Train {
    Brain brain;
    List<List<Float>> inputs = new ArrayList<>();
    List<Float> outputs = new ArrayList<>();
    int seq=0;
    int currentInput=0;
    float[] prevErrors;
    public Train(Brain brain){
        this.brain = brain;
    }

    public void setInputs(){
        List<Float> i = new ArrayList<>();
        i.add(0.0f);
        //i.add(1.0f);
        inputs.add(i);
        i = new ArrayList<>();
        i.add(1.0f);
        //i.add(1.0f);
        inputs.add(i);
    }

    public void setOutputs(){
        outputs.add(1.0f);
        outputs.add(0.0f);
    }

    public void start(){
        List<Float> input = inputs.get(0);
        Float output = outputs.get(0);
        brain.in(input);
    }


    public void next(){
        if( currentInput > inputs.size() -1) {
            System.out.println("Batch Complete");
            currentInput = 0;
        }
        List<Float> input = inputs.get(currentInput);

        Float output = outputs.get(currentInput);
        float out = brain.in(input);
        for(float i: input)
        {
            System.out.print ("INPUT:  " + i + " , ");
        }
        System.out.println(currentInput + " :> " +  "OUT:  " + out + "   ->   " + output);
        float error = Math.abs(output - out);

        if( out > 0 ) {
            //brain.reward( error/10);
        }
        else {
            //brain.punish(error/10);
        }
        currentInput++;
        seq++;
    }
    public static void main(String[] args){
        Brain brain = new Brain(1,1,2);
        brain.load();
        //brain.dump();
        System.out.println(brain.toJsonSave().toString());

//        Train train = new Train(brain);
//        train.setInputs();
//        train.setOutputs();
//        train.start();
//        brain.toJson();
//        brain.upload();
    }
}
