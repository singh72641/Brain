package com.punwire.brain.game;


import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.settings.GameSettings;
import com.punwire.brain.Brain;
import com.punwire.brain.Neuron;
import com.punwire.brain.NodeConnection;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 7/17/2017.
 */
public class BrainGame extends GameApplication {
    Brain brain = new Brain(2,1,3);

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setHeight(800);
        settings.setWidth(1200);
        settings.setTitle("First Game");
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        brain.init();
    }

    @Override
    protected void initInput() {
        Input input = getInput();
        input.addAction(new UserAction("Shoot") {
            @Override
            protected void onActionBegin() {
                getGameWorld().spawn("Bullet", input.getMouseXWorld(), getHeight() -10);
            }
        }, MouseButton.PRIMARY);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("enemies", 0);
    }

    @Override
    protected void initPhysics() {
        PhysicsWorld physicsWorld = getPhysicsWorld();
        physicsWorld.addCollisionHandler(new CollisionHandler(ShooterType.BULLET, ShooterType.ENEMY) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity enemy) {
                System.out.println("Collision");
                bullet.removeFromWorld();
                enemy.removeFromWorld();
                getGameState().increment("enemies",-1);
            }
        });
    }

    @Override
    protected void initGame() {
//        getMasterTimer().runAtInterval( ()->{
//            int numEnemies = getGameState().getInt("enemies");
//            if( numEnemies < 5 ) {
//                getGameWorld().spawn("Enemy", FXGLMath.random(0, getWidth() - 40), FXGLMath.random(0, getHeight() / 2 - 40));
//                getGameState().increment("enemies", +1);
//            }
//        }, Duration.seconds(1));

//        for(Node n: brain.nodes.values()){
//            getGameWorld().spawn("Neuron", FXGLMath.random(0, getWidth() - 40), FXGLMath.random(0, getHeight()-40));
//
//            for(NodeConnection nc: n.connections){
//                getGameWorld().spawn("DN", FXGLMath.random(0, getWidth() - 40), FXGLMath.random(0, getHeight()-40));
//            }
//        }

    }

    public static void main(String[] args){
        launch(args);
    }
}