package edu.sustech.xiangqi;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.Node; // 1. 导入Node类，模型会被加载成这个类型
import javafx.scene.PointLight;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.*;

public class XiangQiApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("My Xiangqi Piece!");
        settings.set3D(true);
    }

    @Override
    protected void initGame() {
        // --- 1. 加载你的3D模型 ---
        // AssetLoader会去 "src/main/resources/" 目录下寻找
        // 所以我们提供的路径是相对于resources目录的
        // 假设你的象棋子模型文件是 elephant.obj
        // 并且你已经将“象棋”文件夹改名为“xiangqi_pieces”
        Node elephantModel = getAssetLoader().loadModel3D("象棋.obj");

        // (可选) 调整模型大小和位置，因为加载进来的模型可能过大或过小
        elephantModel.setScaleX(2.0);
        elephantModel.setScaleY(2.0);
        elephantModel.setScaleZ(2.0);
        elephantModel.setTranslateY(-2); // 比如模型底部不在原点，可以微调Y轴让它“站”在地上

        // --- 2. 使用加载好的模型来创建游戏实体 ---
        Entity pieceEntity = entityBuilder()
                .at(0, 0, 0)
                .view(elephantModel) // 2. 将加载的模型作为实体的外观
                .buildAndAttach();

        // --- 3. 添加光源 (依然需要) ---
        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateZ(-20);
        getGameScene().getContentRoot().getChildren().add(light);

        // --- 4. 设置相机位置 (依然需要) ---
        getGameScene().getCamera3D().getTransform().setY(60);
        getGameScene().getCamera3D().getTransform().setX(10);
        getGameScene().getCamera3D().getTransform().setZ(10);

        // 将相机围绕X轴旋转90度，使其镜头垂直朝下
        getGameScene().getCamera3D().getTransform().setRotationX(90);
    }

    public static void main(String[] args) {
        launch(args);
    }
}