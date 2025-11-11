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

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(2080);
        settings.setHeight(2100);
        settings.setTitle("My Xiangqi Piece!");
        settings.set3D(true);
    }

    @Override
    protected void initGame() {
        // (这部分是新增的)
        Image pieceTexture = getAssetLoader().loadTexture("textures/XQ_M1_Diffuse.png").getImage();
        PhongMaterial pieceMaterial = new PhongMaterial();
        pieceMaterial.setDiffuseMap(pieceTexture); // 将图片设置为材质的漫反射贴图

        // --- 1. 加载你的3D模型 ---
        // AssetLoader会去 "src/main/resources/" 目录下寻找
        // 所以我们提供的路径是相对于resources目录的
        // 假设你的象棋子模型文件是 elephant.obj
        // 并且你已经将“象棋”文件夹改名为“xiangqi_pieces”
        Node elephantModel = getAssetLoader().loadModel3D("象棋.obj");

        applyMaterialRecursive(elephantModel, pieceMaterial);


        // (可选) 调整模型大小和位置，因为加载进来的模型可能过大或过小
        elephantModel.setScaleX(2.0);
        elephantModel.setScaleY(2.0);
        elephantModel.setScaleZ(2.0);
        elephantModel.setTranslateY(-2); // 比如模型底部不在原点，可以微调Y轴让它“站”在地上

        // --- 2. 使用加载好的模型来创建游戏实体 ---
        Entity pieceEntity = entityBuilder()
                .at(0, 0, 0)
                .view(elephantModel)
                .buildAndAttach();

        // --- 3. 添加光源 (依然需要) ---
        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateZ(-20);
        getGameScene().getContentRoot().getChildren().add(light);

        // --- 4. 设置相机位置 (依然需要) ---
        getGameScene().getCamera3D().getTransform().setY(90);
        getGameScene().getCamera3D().getTransform().setX(0);
        getGameScene().getCamera3D().getTransform().setZ(0);

        // 将相机围绕X轴旋转90度，使其镜头垂直朝下
        getGameScene().getCamera3D().getTransform().setRotationX(90);
    }
    /**
     * 这是一个辅助方法，用于递归遍历一个节点及其所有子节点，
     * 并将指定的材质应用到所有找到的 MeshView 部件上。
     *
     * @param node     要开始遍历的节点 (你加载的模型)
     * @param material 要应用的材质
     */
    private void applyMaterialRecursive(Node node, PhongMaterial material) {
        // 检查当前节点本身是不是一个可以上色的 MeshView
        if (node instanceof MeshView) {
            ((MeshView) node).setMaterial(material);
        }

        // 检查当前节点是不是一个“容器”（Parent），如果是，就遍历它的所有孩子
        if (node instanceof Parent) {
            // 遍历该容器的所有子节点
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                // 对每一个子节点，再次调用这个方法本身（这就是递归）
                applyMaterialRecursive(child, material);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}