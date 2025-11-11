package edu.sustech.xiangqi;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.Node; // 1. 导入Node类，模型会被加载成这个类型
import javafx.scene.Parent;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;

import static com.almasb.fxgl.dsl.FXGL.*;

public class XiangQiApp extends GameApplication {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings gameSettings) {

    }
}