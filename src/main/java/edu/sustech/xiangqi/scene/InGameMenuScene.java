package edu.sustech.xiangqi.scene;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import edu.sustech.xiangqi.XiangQiApp;
import edu.sustech.xiangqi.model.ChessBoardModel;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

public class InGameMenuScene extends FXGLMenu {


    public InGameMenuScene() {
        super(MenuType.GAME_MENU);

        var historyListView = new ListView<String>();
        ChessBoardModel model = ((XiangQiApp) FXGL.getApp()).getModel();

        if (model != null) {
            // b. 将 ListView 的 items 属性和 model 的 ObservableList 绑定
            historyListView.setItems(model.getMoveHistoryAsObservableList());
        }

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


}