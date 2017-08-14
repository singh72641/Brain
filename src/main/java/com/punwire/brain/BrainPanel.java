package com.punwire.brain;

import com.almasb.fxgl.core.math.FXGLMath;
import com.punwire.brain.web.SidePanel;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.core.util.FileUtils;
import org.neo4j.csv.reader.SourceTraceability;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created by admin on 7/17/2017.
 */
public class BrainPanel extends JPanel {
    Brain brain;
    public HashMap<String, Shape> shapes = new HashMap<>();
    public HashMap<String, Shape> commands = new HashMap<>();
    JsonObject brainObject;
    SidePanel sidePanel;
    String currentCommand="NH";
    String currentNode=null;
    public BrainPanel(Brain b, SidePanel sp)
    {
        this.brain = b;
        this.sidePanel = sp;
        setPreferredSize(new Dimension(1200, 800));
        try {
            JsonReader reader = Json.createReader(new FileReader("D:\\projects\\Barin\\public\\brain.json"));
            //brainObject = reader.readObject();
            brainObject = brain.toJsonBrain();
        } catch (Exception e) {
            e.printStackTrace();
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if( e.getButton() == 3) {
                    if( currentCommand.equals("Select")){
                        System.out.println("Selecting Node");
                        for (Map.Entry<String, Shape> ent : shapes.entrySet()) {
                            Shape s = ent.getValue();
                            if (s.contains(e.getX(), e.getY())) {
                                currentNode = ent.getKey();
                                break;
                            }
                        }
                    }
                    else if( currentCommand.equals("Connect")){
                        String node = findNode(e.getX(), e.getY());
                        if( node != null) {
                            //Connect Current Node to this
                            brain.connectNodes( brain.getNode(currentNode),brain.getNode(node) );
                        }
                    }
                    else if( currentCommand.equals("DN")){
                        currentNode = findNode(e.getX(), e.getY());
                        Node dn = brain.addNode("DN");
                        brain.connectNodes( brain.getNode(currentNode),dn);
                    }
                    else if( currentCommand.equals("NewLayer")){
                        String node = findNode(e.getX(), e.getY());
                        brain.newLayer( brain.getNode(node));
                    }
                    else {
                        brain.addNode(currentCommand);
                    }
                }
                else {
                    String node = findNode(e.getX(), e.getY());
                    if( node != null) sidePanel.setNode(node);
                }
                refresh();
            }
        });
    }

    public String findNode(double x, double y){
        for (Map.Entry<String, Shape> ent : shapes.entrySet()) {
            Shape s = ent.getValue();
            if (s.contains(x, y)) {
                return ent.getKey();
            }
        }
        return null;
    }

    public void setCurrentCommand(String currentCommand) {
        this.currentCommand = currentCommand;
    }

    public void refresh(){
        brainObject = brain.toJsonBrain();
        updateUI();
    }

    private Color getColor(String key){
        if( key.equals("NI")) return Color.RED;
        else if( key.equals("NO")) return Color.BLUE;
        else if( key.equals("NH")) return Color.GREEN;
        else  return Color.MAGENTA;
    }


    @Override
    public void paintComponent(Graphics g) {
        //System.out.println(brainObject.toString());
        super.paintComponent(g);  // paint background
        Graphics2D g2 = (Graphics2D) g;
        setBackground(new Color(190,190,160));

        Stroke s =new BasicStroke(1);
        g2.setStroke(s);
        int width = getWidth();
        int height = getHeight();
        double center = width /2.0;
        double x = center;
        double y = 100;

        JsonArray layers = brainObject.getJsonArray("layers");
        for(JsonObject layer: layers.getValuesAs(JsonObject.class)){
            JsonArray nodes = layer.getJsonArray("nodes");
            x = center - 60 * (nodes.size() - 1);
            for(JsonObject node: nodes.getValuesAs(JsonObject.class)){
                drawNode(g2, node,x,y);
                x += 120;
            }
            y += 80;
        }

        JsonArray edges = brainObject.getJsonArray("edges");
        if( edges != null) {
            for (JsonObject edge : edges.getValuesAs(JsonObject.class)) {
                drawEdge(g2, edge.getString("source"), edge.getString("target"));
            }
        }
    }

    public void drawEdge(Graphics2D g2, String source, String target) {
        g2.setColor(Color.darkGray);
        Ellipse2D s = (Ellipse2D)shapes.get(source);
        Ellipse2D t = (Ellipse2D)shapes.get(target);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine( (int)s.getCenterX(), (int)s.getMinY(), (int)t.getCenterX(), (int)t.getMaxY()  );
    }
    public void drawNode(Graphics2D g2, JsonObject node, double x, double y) {
        Font f = new Font("Arial", Font.BOLD, 16);
        double output = NumberUtil.round(node.getJsonNumber("output").doubleValue(),4);

        Color fillColor = new Color(80,80,80);

        if( output > 0 ) {
            fillColor = new Color(20,130,10);
        }
        else if( output < 0 ) {
            fillColor = new Color(220,10,10);
        }
        Color borderColor = new Color(90,90,97);
        Shape shape = new Ellipse2D.Double(x -10, y,35,35);
        shapes.put(node.getString("key"),shape);
        g2.setColor(fillColor);
        g2.fill(shape);
        g2.setColor(borderColor);
        g2.draw(shape);
        g2.setColor(Color.DARK_GRAY);
        g2.drawString( node.getString("key"), (float)x - 40, (float)y );

        g2.drawString( "" + NumberUtil.round(node.getJsonNumber("output").doubleValue(),4), (float)x - 50, (float)y + 15);
    }


}
