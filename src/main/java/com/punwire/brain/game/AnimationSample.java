package com.punwire.brain.game;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class AnimationSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("RenderLayerSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initGame() {
        Entities.builder()
                .at(100, 100)
                .viewFromNode(new Rectangle(40, 40))
                .buildAndAttach(getGameWorld());

        EntityView view = new EntityView(new Rectangle(400, 400, Color.RED));

        // 1. predefine or create dynamically like below
        view.setRenderLayer(new RenderLayer() {
            @Override
            public String name() {
                // 2. specify the unique name for that layer
                return "LAYER_BELOW_PLAYER";
            }

            @Override
            public int index() {
                // 3. specify layer index, higher values will be drawn above lower values
                return 1000;
            }
        });

        // we have added box after player but because of the render layer we specified
        // the red box will be drawn below the player
        Entities.builder()
                .at(10, 10)
                .viewFromNode(view)
                .buildAndAttach(getGameWorld());
    }

    public static void main(String[] args) {
        launch(args);
    }
}