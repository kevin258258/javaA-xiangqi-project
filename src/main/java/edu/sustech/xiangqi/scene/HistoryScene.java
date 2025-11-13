package edu.sustech.xiangqi.scene;
import com.almasb.fxgl.scene.SubScene;
import javafx.geometry.Point2D;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.util.List;
import static com.almasb.fxgl.dsl.FXGL.*;
public class HistoryScene extends SubScene {
    private final double PANEL_WIDTH = 300;

    public HistoryScene() {
        // 1. 【正确方式】手动创建一个覆盖全屏的、半透明的 Rectangle 作为遮罩
        var dimmingBg = new Rectangle(getAppWidth(), getAppHeight(), Color.web("000", 0.5));

        // 2. 创建我们的历史记录面板 (VBox)
        var panel = new VBox();
        panel.setPrefSize(PANEL_WIDTH, getAppHeight());
        panel.setStyle(
                "-fx-padding: 10; " + // 添加一些内边距
                        "-fx-background-image: url('assets/textures/history_bg.png'); " +
                        "-fx-background-size: cover;" // 使用 cover 来确保图片填满，可能会裁剪
        );
        panel.setAlignment(javafx.geometry.Pos.TOP_CENTER); // 内容顶部居中

        // 3. 创建面板的内容
        var title = new Text("历史记录");
        title.setFill(Color.WHITE);
        // 你需要为 title 设置字体
        // title.setFont(FXGL.getAssetLoader().loadFont("your_font.ttf").newFont(30));

        var historyListView = new ListView<String>();
        historyListView.setPrefHeight(getAppHeight() - 100);

        panel.getChildren().addAll(title, historyListView);

        // 4. 【关键】将遮罩和面板都添加到 SubScene 的根节点
        getContentRoot().getChildren().addAll(dimmingBg, panel);

        // 5. 设置初始位置
        // 遮罩 dimmingBg 在 (0,0) 不动。我们只移动 panel。
        panel.setTranslateX(getAppWidth());

        // 6. 播放进入动画
        animationBuilder()
                .duration(Duration.seconds(0.3))
                .translate(panel)
                .to(new Point2D(getAppWidth() - PANEL_WIDTH, 0))
                .buildAndPlay();

        // 7. 添加关闭事件
        // 点击遮罩时关闭
        dimmingBg.setOnMouseClicked(e -> close());

    }

    public void updateHistory(List<String> moves) {
        // 这个方法暂时不需要，因为 ListView 还没有被创建
        // var historyListView = (ListView<String>) ((VBox)getContentRoot().getChildren().get(1)).getChildren().get(1);
        // historyListView.getItems().setAll(moves);
    }

    public void close() {
        // 注销ESC键监听，防止在其他地方误触发

        // 获取面板 (它是根节点的第二个子元素)
        var panel = getContentRoot().getChildren().get(1);


        runOnce(() -> {
            // 这段代码会在 0.3 秒后执行
            getSceneService().popSubScene();
        }, Duration.seconds(0.3));

        animationBuilder()
                .duration(Duration.seconds(0.3))
                .translate(panel)
                .to(new Point2D(getAppWidth(), 0))
                .buildAndPlay();


    }

}
