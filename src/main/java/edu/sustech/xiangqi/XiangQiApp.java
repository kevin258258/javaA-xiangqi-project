package edu.sustech.xiangqi;

import edu.sustech.xiangqi.scene.*;
import edu.sustech.xiangqi.scene.PixelatedButton;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.SpawnData;
import edu.sustech.xiangqi.model.AbstractPiece;
import edu.sustech.xiangqi.model.ChessBoardModel;
import edu.sustech.xiangqi.view.XiangQiFactory;
import edu.sustech.xiangqi.controller.InputHandler;
import edu.sustech.xiangqi.controller.boardController;
import javafx.geometry.Point2D;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


import static com.almasb.fxgl.dsl.FXGL.*;

public  class XiangQiApp extends GameApplication {

    //文本提示
    private Text checkText;
    private Text gameOverBanner;
    private Rectangle gameOverDimmingRect; // 新增遮罩
    private TurnIndicator  turnIndicator;

    public Text getGameOverBanner() { return gameOverBanner; }
    public Rectangle getGameOverDimmingRect() { return gameOverDimmingRect; }


    /**
     * 一个公共的辅助方法，用于将一个 Text 对象在整个应用窗口中居中。
     * @param text The Text node to be centered.
     */
    public void centerTextInApp(Text text) {
        // 计算文本的宽度和高度
        double textWidth = text.getLayoutBounds().getWidth();
        double textHeight = text.getLayoutBounds().getHeight();

        // 计算居中所需的 X 和 Y 坐标
        double centerX = (APP_WIDTH - textWidth) / 2;
        // 在Y轴上，我们通常需要向上偏移一点，因为坐标是基于文本基线的
        double centerY = (APP_HEIGHT - textHeight) / 2 + text.getFont().getSize() * 0.3;

        // 设置文本的位置
        text.setTranslateX(centerX);
        text.setTranslateY(centerY);
    }


    // --- 核心尺寸常量 (基于你的资源) ---
// 你需要根据你的 Board.png 和棋子素材精确测量和定义这些值
    public static final int CELL_SIZE = 90;   // 棋盘格的像素边长
    public static final int MARGIN = 31;      // 棋盘网格距离棋盘图片边缘的内部边距

    // --- 布局常量 (你可以随意调整这些值来改变外观) ---
    public static final int HORIZONTAL_PADDING  = 70; // 窗口边缘的全局留白
    public static final int UI_GAP = 250;         // 棋盘和右侧UI栏之间的间距
    public static final int UI_WIDTH = 200;        // 右侧UI栏的宽度

    // --- 根据上面常量自动计算的尺寸 ---
// 棋盘图片本身的尺寸
    public static final int BOARD_WIDTH = 796;
    public static final int BOARD_HEIGHT = 887;

    // 最终窗口的总尺寸
    public static final int APP_WIDTH = BOARD_WIDTH + UI_GAP + UI_WIDTH + HORIZONTAL_PADDING  * 2;
    public static final int APP_HEIGHT = BOARD_HEIGHT ;

    private ChessBoardModel model;
    private boardController boardController;
    private InputHandler inputHandler;


    /**
     * 【新增的辅助方法】
     * 将棋盘的逻辑行列坐标，转换为实体在屏幕上的视觉左上角坐标。
     * @param row 逻辑行 (0-9)
     * @param col 逻辑列 (0-8)
     * @return 包含实体应放置的X, Y像素坐标的Point2D对象
     */
    public static Point2D getVisualPosition(int row, int col) {
        // 1. 计算出格子交叉点（中心点）的坐标
        double centerX = HORIZONTAL_PADDING + MARGIN + col * CELL_SIZE;
        double centerY = MARGIN + row * CELL_SIZE; // Y坐标没有 HORIZONTAL_PADDING

        // 2. 根据棋子大小的一半，计算出左上角坐标
        // 我们的棋子视图大小是 CELL_SIZE - padding，所以半径是 (CELL_SIZE - padding) / 2
        // 为了简化，我们直接用 CELL_SIZE / 2
        double pieceRadius = (CELL_SIZE - 8) / 2.0; // 8 是我们在 Factory 里的 padding

        double topLeftX = centerX - pieceRadius;
        double topLeftY = centerY - pieceRadius;

        return new Point2D(topLeftX, topLeftY);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("中国象棋 1.0");
        settings.setVersion("1.0");
        settings.setWidth(APP_WIDTH);
        settings.setHeight(APP_HEIGHT);

        // --- 【关键配置】 ---
        // 1. 启用主菜单功能
        settings.setMainMenuEnabled(true);

        // 2. 设置我们自定义的场景工厂
        settings.setSceneFactory(new SceneFactory() {
            @Override
            public FXGLMenu newMainMenu() {
                // 当FXGL需要主菜单时，返回我们自己创建的 MainMenuScene 实例
                return new MainMenuScene();
            }
            @Override
            public FXGLMenu newGameMenu() {
                return new InGameMenuScene();
            }
        });

    }

    @Override
    protected void initGame() {
        // 1. 注册我们的工厂
        getGameWorld().addEntityFactory(new XiangQiFactory());

        // 2. 创建我们的逻辑模型。
        // 构造函数会自动调用 initializePieces(), 此刻 model.getPieces() 已是完整列表。
        this.model = new ChessBoardModel();

        spawn("background", 0, 0);


        // 3. 生成棋盘背景实体，它应该有一个 MARGIN 的偏移
        spawn("board", HORIZONTAL_PADDING, 0);

        // 4. 【最终正确的方式】直接遍历模型中已初始化好的棋子列表
        for (AbstractPiece pieceLogic : model.getPieces()) {

            // a. 构建与工厂中 @Spawns 注解完全匹配的字符串ID
            String colorPrefix = pieceLogic.isRed() ? "Red" : "Black";
            String pieceTypeName = pieceLogic.getClass().getSimpleName().replace("Piece", "");
            String entityID = colorPrefix + pieceTypeName;

            // b. 从 pieceLogic 对象中直接获取其正确的行列坐标
            int row = pieceLogic.getRow();
            int col = pieceLogic.getCol();

            Point2D visualPos = getVisualPosition(pieceLogic.getRow(), pieceLogic.getCol());

            spawn(entityID, new SpawnData(visualPos).put("pieceLogic", pieceLogic));
        }

        // 4. 【关键步骤】创建并连接控制器
        this.boardController = new boardController(this.model);
        this.inputHandler = new InputHandler(this.boardController);



    }




    @Override
    protected void initUI() {
        // 创建按钮
        var btnUndo = new PixelatedButton("悔棋", "Button1", () -> {
            if (boardController != null) {
                boardController.undo();
            }
        });        var btnSurrender = new PixelatedButton("投降", "Button1", () -> {
            // 直接调用 boardController 的 surrender 方法
            if (boardController != null) {
                boardController.surrender();
            }
        });
        var btnAIHint = new PixelatedButton("AI提示", "Button1", () -> {
            System.out.println("AI提示...");
            // 在这里调用 AI 分析并高亮最佳走法
        });
        var btnHistory = new PixelatedButton("历史记录", "Button1", () -> {
            // 【关键】直接调用内置方法打开游戏菜单
            getGameController().gotoGameMenu();
        });




        // 使用 VBox 垂直排列按钮
        VBox buttons = new VBox(10, btnUndo, btnSurrender,btnAIHint,btnHistory);
        buttons.setPrefWidth(150); // 给VBox一个宽度

        // 将按钮组合放置在右侧的UI区域
        int uiX = HORIZONTAL_PADDING + BOARD_WIDTH + UI_GAP;
        addUINode(buttons, uiX, 50);

        // --- 新增回合指示器 ---
        // 1. 创建 Text 对象并设置样式
        turnIndicator = new TurnIndicator();
        addUINode(turnIndicator, uiX, 750);

        gameOverDimmingRect = new Rectangle(APP_WIDTH, APP_HEIGHT, Color.web("000", 0.0));
        gameOverDimmingRect.setVisible(false); // 默认不可见
        gameOverDimmingRect.setMouseTransparent(true); // 不拦截鼠标
        // --- 【新增代码】创建游戏结束报幕 ---
        gameOverBanner = new Text(); // 初始内容为空
        gameOverBanner.setFont(Font.font("MNewsMPro-Light.ttf", FontWeight.BOLD, 100));
        gameOverBanner.setFill(Color.BROWN);
        gameOverBanner.setStroke(Color.BLACK);
        gameOverBanner.setStrokeWidth(3);
        gameOverBanner.setEffect(new DropShadow(15, Color.BLACK));

        // 默认让它不可见
        gameOverBanner.setVisible(false);

        // 添加到UI层
        addUINode(gameOverDimmingRect);
        addUINode(gameOverBanner);
    }


    public TurnIndicator getTurnIndicator() {
        return turnIndicator;
    }


    @Override
    protected void initInput() {
        // 【新增】在 Controller 创建后，手动调用一次更新来设置初始状态




        // 1. 获取FXGL的输入服务
        Input input = getInput();

        // 2. 创建一个新的 UserAction，并命名为 "Click"
        UserAction clickAction = new UserAction("Click") {
            @Override
            protected void onActionEnd() {
                // 3. 当动作结束时（即鼠标按键被释放），
                //    调用我们的 InputHandler 来处理。
                //    使用 input.getMousePositionWorld() 来获取正确的游戏世界坐标。
                inputHandler.handleMouseClick(input.getMousePositionWorld());
            }
        };

        // 4. 将我们创建的 "Click" 动作，绑定到鼠标左键 (PRIMARY) 上
        input.addAction(clickAction, MouseButton.PRIMARY);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public ChessBoardModel getModel() {
        return model;
    }
}

