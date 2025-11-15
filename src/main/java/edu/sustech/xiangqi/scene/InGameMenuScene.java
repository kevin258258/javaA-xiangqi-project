package edu.sustech.xiangqi.scene;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

public class InGameMenuScene extends FXGLMenu {

    private ListView<String> historyListView = new ListView<>();

    public InGameMenuScene() {
        super(MenuType.GAME_MENU);

        // 1. 创建一个半透明的背景遮罩
        var bg = new Rectangle(getAppWidth(), getAppHeight(), Color.web("000", 0.7));

        // 2. 创建历史记录面板
        var title = new Text("菜单/历史记录");
        title.setFill(Color.WHITE);
        // ... (设置字体)

        historyListView.setPrefHeight(getAppHeight() - 200);

        // 3. 创建其他菜单按钮
        var btnResume = getUIFactoryService().newButton("返回游戏");
        btnResume.setOnAction(e -> fireResume()); // fireResume() 是关闭游戏菜单的内置方法

        var btnExit = getUIFactoryService().newButton("退出到主菜单");
        btnExit.setOnAction(e -> fireExitToMainMenu()); // fireExitToMainMenu() 是内置方法

        // 4. 布局
        var historyBox = new VBox(10, title, historyListView);
        var menuBox = new VBox(15, historyBox, btnResume, btnExit);
        menuBox.setAlignment(Pos.CENTER);

        menuBox.setTranslateX(getAppWidth() / 2.0 - 150); // 居中
        menuBox.setTranslateY(50); // 顶部对齐

        // 5. 添加到场景
        getContentRoot().getChildren().addAll(bg, menuBox);
    }

    /**
     * 当菜单被打开时，FXGL会自动调用这个方法。
     * 这是一个绝佳的时机来更新历史记录。
     */

    public void onOpen() {
        // 从你的模型中获取历史记录
        // ChessBoardModel model = ((XiangqiApp) FXGL.getApp()).getModel();
        // List<String> moves = model.getMoveHistory();
        // historyListView.getItems().setAll(moves);

        // 临时的测试数据
        historyListView.getItems().setAll("1. 炮二平五", "2. 马八进七", "3. 车一平二");
    }
}