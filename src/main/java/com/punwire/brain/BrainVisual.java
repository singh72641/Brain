package com.punwire.brain;

import com.punwire.brain.web.SidePanel;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.imageio.ImageIO;
import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by admin on 7/16/2017.
 */
public class BrainVisual extends JFrame implements ActionListener {

    Brain brain = new Brain(1,1,0);
    Train train;
    BrainPanel panel;
    public BrainVisual()
    {
        train = new Train(brain);
        train.setInputs();
        train.setOutputs();
        brain.init();
        brain.toJsonBrain();
        SidePanel sp = new SidePanel(brain);
        panel = new BrainPanel(brain,sp);
        JPanel root = new JPanel(new BorderLayout());
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton btnNext = new JButton("Next");
        JButton btnStep = new JButton("Step");
        btnPanel.add(btnNext);
        btnPanel.add(btnStep);
        root.add(btnPanel, BorderLayout.NORTH);
        root.add(panel,BorderLayout.CENTER);
        root.add(sp,BorderLayout.EAST);


        JMenuBar menuBar;
        JMenu menu, submenu;
        JMenuItem menuItem;

        //Create the menu bar.
        menuBar = new JMenuBar();
        menu = new JMenu("File");
        menuBar.add(menu);
        menuItem = new JMenuItem("Load", KeyEvent.VK_I);
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem = new JMenuItem("Save", KeyEvent.VK_I);
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menu = new JMenu("Connections");
        menuBar.add(menu);
        menuItem = new JMenuItem("DN", KeyEvent.VK_I);
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem = new JMenuItem("Select", KeyEvent.VK_I);
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem = new JMenuItem("Connect", KeyEvent.VK_I);
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem = new JMenuItem("NewLayer", KeyEvent.VK_I);
        menuItem.addActionListener(this);
        menu.add(menuItem);
        //Build the Neuron menu.
        menu = new JMenu("Neurons");
        menuBar.add(menu);
        menuItem = new JMenuItem("Input", KeyEvent.VK_I);
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem = new JMenuItem("Output", KeyEvent.VK_O);
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem = new JMenuItem("Hidden", KeyEvent.VK_H);
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem = new JMenuItem("Positive", KeyEvent.VK_P);
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem = new JMenuItem("Negative", KeyEvent.VK_N);
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem = new JMenuItem("Layer");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menu = new JMenu("Training");
        menuBar.add(menu);

        this.setJMenuBar(menuBar);

        btnNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(int i=0; i<1000; i++) {
                    train.next();
                }
                panel.refresh();
            }
        });
        btnStep.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                train.next();
                panel.refresh();
            }
        });
        //Brain3D panel3d = new Brain3D(brain);
        //root.add(panel3d.canvas);


        add(root);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if( action == null || panel == null) return;
        System.out.println("Action: " + action);
        if( action.equals("Input") ) {

            panel.setCurrentCommand("NI");
        }
        else if( action.equals("Output") ) {
            panel.setCurrentCommand("NO");
        }
        else if( action.equals("Hidden") ) {
            panel.setCurrentCommand("NH");
        }
        else if( action.equals("Positive") ) {
            panel.setCurrentCommand("NP");
        }
        else if( action.equals("Negative") ) {
            panel.setCurrentCommand("NN");
        }
        else if( action.equals("Layer") ) {
            brain.currentLayer = brain.addLayer();
        }
        else if( action.equals("Save") ) {
            brain.save();
        }
        else if( action.equals("Load") ) {
            //brain = new Brain(1,1,0);
            brain.load();
            panel.refresh();
        } else {
            panel.setCurrentCommand(action);
            panel.refresh();
        }
    }

    public static void main(String[] args) {
        BrainVisual bv = new BrainVisual();
    }
}
