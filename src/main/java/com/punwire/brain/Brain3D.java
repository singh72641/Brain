package com.punwire.brain;

import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.media.j3d.*;
import javax.swing.*;
import java.awt.*;
import java.io.FileReader;
import java.util.HashMap;

/**
 * Created by admin on 20/07/17.
 */
public class Brain3D {

    Brain brain;
    public HashMap<String, Shape> shapes = new HashMap<>();
    JsonObject brainObject;

    public final Canvas3D canvas;
    public Brain3D(Brain b) {
        this.brain = b;
        //setPreferredSize(new Dimension(1200, 800));
        brainObject = brain.toJsonBrain();

        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        canvas = new Canvas3D(config);
        BranchGroup scene = createScene();
        scene.compile();

        SimpleUniverse su = new SimpleUniverse(canvas);
        su.getViewingPlatform().setNominalViewingTransform();
        su.addBranchGraph(scene);
    }

    private BranchGroup createScene(){
        BranchGroup root = new BranchGroup();
        ColoringAttributes ca = new ColoringAttributes();
        ca.setColor(1.0f, 1.0f, 0.0f);
        Appearance app = new Appearance();
        app.setColoringAttributes(ca);

        Transform3D rotate = new Transform3D();
        rotate.rotX(Math.PI/4.0d);

        TransformGroup tg = new TransformGroup(rotate);
        tg.addChild(new Sphere(0.1f,app));

        root.addChild(tg);
        return root;
    }
}
