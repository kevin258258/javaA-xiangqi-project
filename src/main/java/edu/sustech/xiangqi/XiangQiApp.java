package edu.sustech.xiangqi;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.SpawnData;
import edu.sustech.xiangqi.model.AbstractPiece;
import edu.sustech.xiangqi.model.ChessBoardModel;
import edu.sustech.xiangqi.view.XiangQiFactory;

import static com.almasb.fxgl.dsl.FXGL.*;

public class XiangQiApp extends GameApplication {

    // 确保这些常量与你的 Board.png 图片精确匹配
    public static final int CELL_SIZE = 64; // 棋盘格的像素边长
    public static final int MARGIN = 40;    // 棋盘网格距离图片边缘的像素距离

    private ChessBoardModel model;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("中国象棋 1.0");
        settings.setVersion("1.0");
        settings.setWidth(CELL_SIZE * (ChessBoardModel.getCols() - 1) + MARGIN * 2);
        settings.setHeight(CELL_SIZE * (ChessBoardModel.getRows() - 1) + MARGIN * 2);
    }

    @Override
    protected void initGame() {
        // 1. 注册我们的工厂
        getGameWorld().addEntityFactory(new XiangQiFactory());

        // 2. 创建我们的逻辑模型。
        // 构造函数会自动调用 initializePieces(), 此刻 model.getPieces() 已是完整列表。
        this.model = new ChessBoardModel();

        // 3. 生成棋盘背景实体，它应该有一个 MARGIN 的偏移
        spawn("board", MARGIN, MARGIN);

        // 4. 【最终正确的方式】直接遍历模型中已初始化好的棋子列表
        for (AbstractPiece pieceLogic : model.getPieces()) {

            // a. 构建与工厂中 @Spawns 注解完全匹配的字符串ID
            String colorPrefix = pieceLogic.isRed() ? "Red" : "Black";
            String pieceTypeName = pieceLogic.getClass().getSimpleName().replace("Piece", "");
            String entityID = colorPrefix + pieceTypeName;

            // b. 从 pieceLogic 对象中直接获取其正确的行列坐标
            int row = pieceLogic.getRow();
            int col = pieceLogic.getCol();

            // c. 计算棋子在屏幕上的精确像素坐标
            int x = col * CELL_SIZE + MARGIN;
            int y = row * CELL_SIZE + MARGIN;

            // d. 调用 spawn！并把逻辑棋子对象传进去
            spawn(entityID, new SpawnData(x, y).put("pieceLogic", pieceLogic));
        }

        // 5. (之后在这里创建控制器...)
    }

    public static void main(String[] args) {
        launch(args);
    }
}