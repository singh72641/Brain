package com.punwire.brain.web;

import com.punwire.brain.*;

import javax.swing.*;
import java.awt.*;

/**
 * Created by admin on 22/07/17.
 */
public class SidePanel extends JPanel  {
    Brain brain;
    String currentNode=null;
    public SidePanel(Brain brain) {
        this.brain = brain;
        setPreferredSize(new Dimension(300,600));
    }

    public void setNode(String key)
    {
        this.currentNode = key;
        updateUI();
    }



    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);  // paint background
        Graphics2D g2 = (Graphics2D) g;
        float x = 10;
        float y = 10;
        if(currentNode == null || brain == null) return;
        Node n = brain.nodes.get(currentNode);
        g2.drawString(n.key() + "  ->  " + n.getOutput(), x,y);
        y += 20;
        g2.drawString("       ->  " + n.getLastAction(), x,y);
        y += 20;
        for(NodeConnection nc: n.connections){
            String stat = "  <-  " + NumberUtil.round( nc.weight, 4)+"," + NumberUtil.round(nc.prevWeight,4) + "----- " + nc.n.key() + "( " + nc.n.getOutput() + " )";
            g2.drawString(stat, x,y);
            y += 20;
        }
    }
}
