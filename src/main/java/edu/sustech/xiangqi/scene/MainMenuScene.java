package edu.sustech.xiangqi.scene;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType; // 需要 import MenuType
import com.almasb.fxgl.dsl.FXGL;
import javafx.animation.FillTransition;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

public class MainMenuScene extends FXGLMenu {

    public MainMenuScene() {
        // 【关键修正】调用父类的构造函数，并指明这是一个主菜单
        super(MenuType.MAIN_MENU);

        // --- 1. 背景 ---
        var bgStops = List.of(new Stop(0, Color.web("#D3B08C")), new Stop(1, Color.web("#4A2C12")));
        var bgGradient = new RadialGradient(0.5, 0.5, 0.5, 0.5, 1, true, CycleMethod.NO_CYCLE, bgStops);
        getContentRoot().setBackground(new javafx.scene.layout.Background(new javafx.scene.layout.BackgroundFill(bgGradient, null, null)));

        var rect = new Rectangle(getAppWidth(), getAppHeight(), Color.web("000", 0.0));
        rect.setMouseTransparent(true);
        FillTransition ft = new FillTransition(Duration.seconds(3), rect, Color.TRANSPARENT, Color.web("000", 0.2));
        ft.setCycleCount(-1);
        ft.setAutoReverse(true);
        ft.play();

        // --- 2. 游戏标题 ---
        var title = new Text("中国象棋");
        title.setFont(FXGL.getAssetLoader().loadFont("HYPixel11pxU-2.ttf").newFont(120));
        title.setFill(Color.web("#F0E68C"));
        title.setStroke(Color.web("#5C3A1A"));
        title.setStrokeWidth(3);
        title.setEffect(new DropShadow(15, Color.BLACK));

        // --- 3. 创建自定义的像素化按钮 ---
        var btnNewGame = new PixelatedButton("新的游戏", "Button1",this::fireNewGame);
        var btnLoadGame = new PixelatedButton("读取存档", "Button1",() -> System.out.println("读取存档功能待实现..."));
        var btnExit = new PixelatedButton("退出游戏","Button1", this::fireExit);
        var btnOnline = new PixelatedButton("联网对战", "Button1", () -> {
            System.out.println("联网对战功能待实现...");
            // 在这里切换到联机大厅场景
        });

        // --- 4. 整体布局 ---
        var titleBox = new VBox(title);
        titleBox.setAlignment(Pos.CENTER);
        var menuBox = new VBox(15, btnNewGame, btnLoadGame, btnOnline,btnExit);
        menuBox.setAlignment(Pos.CENTER);
        var mainLayout = new VBox(50, titleBox, menuBox);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setTranslateX((getAppWidth() - mainLayout.getBoundsInLocal().getWidth()) / 2);
        mainLayout.setTranslateY((getAppHeight() - mainLayout.getBoundsInLocal().getHeight()) / 2 - 300);

        // --- 5. 将所有元素添加到场景中 ---
        getContentRoot().getChildren().addAll(rect, mainLayout);
    }
}