package com.timem.game2048javafx;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game2048Controller {

    @FXML
    private
    Label tile00, tile01, tile02, tile03;
    @FXML private
    Label tile10, tile11, tile12, tile13;
    @FXML private
    Label tile20, tile21, tile22, tile23;
    @FXML private
    Label tile30, tile31, tile32, tile33;

    @FXML
    BorderPane root;

    /**
     * 设置当前游戏的最大撤销次数
     */
    private static final int MAX_UNDO_TIMES = 5;

    /**
     * 游戏行列数
     */
    private int rowColNum = 4;

    /**
     * 当前撤销次数
     */
    private int currentUndoTime = 0;

    /**
     * 2048 单元格 -- 表格
     */
    private int[][] cells = new int[rowColNum][rowColNum];

    /**
     * 2048 单元格
     */
    private Label[][] tiles = new Label[rowColNum][rowColNum];

    /**
     * 初始化方法
     */
    @FXML
    public void initialize() {
        tiles[0][0] = tile00;
        tiles[0][1] = tile01;
        tiles[0][2] = tile02;
        tiles[0][3] = tile03;

        tiles[1][0] = tile10;
        tiles[1][1] = tile11;
        tiles[1][2] = tile12;
        tiles[1][3] = tile13;

        tiles[2][0] = tile20;
        tiles[2][1] = tile21;
        tiles[2][2] = tile22;
        tiles[2][3] = tile23;

        tiles[3][0] = tile30;
        tiles[3][1] = tile31;
        tiles[3][2] = tile32;
        tiles[3][3] = tile33;

        // 重置游戏
        clearGame();

        // 使用 Platform.runLater 延迟请求焦点
        Platform.runLater(() -> {
            root.setFocusTraversable(true);
            root.requestFocus();
        });

        // 设置方向键监听
        root.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case UP: moveUp(null); break;
                case DOWN: moveDown(null); break;
                case LEFT: moveLeft(null); break;
                case RIGHT: moveRight(null); break;
                case BACK_SPACE: undo(null); break;
            }
        });
    }

    /**
     * 重置游戏
     */
    private void clearGame() {
        // 重置计数
        this.currentUndoTime = 0;

        // 重置表格
        clearTile();

        // 生成两个随机数
        nextNum();
        nextNum();
    }

    /**
     * 重置游戏
     * @param event 按钮事件
     */
    @FXML
    private void clear(ActionEvent event) {
        // 显示确认对话框
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认操作");
        alert.setHeaderText(null);
        alert.setContentText("确认要重新开始游戏吗？");

        // 显示对话框并获取用户的选择结果
        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);

        // 确认重置游戏
        if (result == ButtonType.OK) {
            clearGame();
        }
    }

    /**
     * 判断表格是否满了
     * @return 是否满了
     */
    private boolean isFull() {
        for (int i = 0; i < rowColNum * rowColNum; i++) {
            if (this.cells[i / rowColNum][i % rowColNum] == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断当前游戏是否已经结束
     * @return 游戏是否结束
     */
    private boolean isOver() {
        // 判断游戏是否结束，两个条件：1.表格已满，2.相邻元素各不相同
        for (int i = 0; i < rowColNum * rowColNum; i++) {
            // 1. 判断表格是否已满
            if (this.cells[i / rowColNum][i % rowColNum] == 0) {
                return false;
            }
            // 2. 判断相邻元素是否相同
            // 与上面的元素是否相同
            if (i >= rowColNum && this.cells[i / rowColNum][i % rowColNum] == this.cells[(i - rowColNum) / rowColNum][(i - rowColNum) % rowColNum]) {
                return false;
            }
            // 与下面的元素是否相同
            if (i < rowColNum * (rowColNum - 1) && this.cells[i / rowColNum][i % rowColNum] == this.cells[(i + rowColNum) / rowColNum][(i + rowColNum) % rowColNum]) {
                return false;
            }
            // 与左面的元素是否相同
            if (i % rowColNum != 0 && this.cells[i / rowColNum][i % rowColNum] == this.cells[(i - 1) / rowColNum][(i - 1) % rowColNum]) {
                return false;
            }
            // 与面的元素是否相同
            if ((i + 1) % rowColNum != 0 && this.cells[i / rowColNum][i % rowColNum] == this.cells[(i + 1) / rowColNum][(i + 1) % rowColNum]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 处理操作后的下一步
     * @param cell 当前行或列
     */
    private void nextStep(int[] cell) {
        List<Integer> cellList = new ArrayList<>();
        for (int c : cell) {
            if (c != 0) {
                cellList.add(c);
            }
        }

        for (int i = 0; i < cellList.size() - 1; i++) {
            if (cellList.get(i).equals(cellList.get(i + 1))) {
                cellList.set(i, cellList.get(i) * 2);
                cellList.remove(i + 1);
            }
        }

        for (int i = 0; i < rowColNum; i++) {
            if (i < cellList.size()) {
                cell[i] = cellList.get(i);
            } else {
                cell[i] = 0;
            }
        }
    }

    /**
     * 生成下一个数字
     */
    private void nextNum() {
        // 表格满了，不再生成数字
        if (isFull()) {
            return;
        }

        // 生成一个 0 - 15 的随机数
        Random random = new Random();
        int ran = random.nextInt(rowColNum * rowColNum);
        // index 表示表格中的空白位置，从左到右，从上到下，数到第 ran 个空白位置（只数空白位置，数到结束后从头开始）
        int index = -1;
        while (ran >= 0) {
            index++;
            index %= rowColNum * rowColNum;
            while (this.cells[index / rowColNum][index % rowColNum] != 0) {
                index++;
                index %= rowColNum * rowColNum;
            }
            ran --;
        }

        // 在 index 位置生成随机数
        setTile(index / rowColNum, index % rowColNum, 2);
    }

    @FXML
    public void undo(ActionEvent event) {

    }

    /**
     * 设置某个格子的数值并更新样式
     * @param row 行
     * @param col 列
     * @param value 值
     */
    private void setTile(int row, int col, int value)
    {
        this.cells[row][col] = value;
        Label label = this.tiles[row][col];
        label.setText(value == 0 ? "" : String.valueOf(value));
        String styleClass = "tile-";

        switch (value) {
            case 2: styleClass += 2; break;
            case 4: styleClass += 4; break;
            case 8: styleClass += 8; break;
            case 16: styleClass += 16; break;
            case 32: styleClass += 32; break;
            case 64: styleClass += 64; break;
            case 128: styleClass += 128; break;
            case 256: styleClass += 256; break;
            case 512: styleClass += 512; break;
            case 1024: styleClass += 1024; break;
            default: styleClass += 2048;
        }

        label.getStyleClass().clear();
        label.getStyleClass().add("tile");

        if (value != 0) {
            label.getStyleClass().add(styleClass);
        }
    }

    /**
     * 清除游戏表格
     */
    private void clearTile() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.cells[i][j] = 0;
                this.tiles[i][j].setText("");
                this.tiles[i][j].getStyleClass().clear();
                this.tiles[i][j].getStyleClass().add("tile");
            }
        }
    }

    /**
     * 向上移动
     * @param event 移动事件
     */
    @FXML
    public void moveUp(ActionEvent event) {
        for (int j = 0; j < rowColNum; j++) {
            int[] currentCell = new int[rowColNum];
            for (int i = 0; i < rowColNum; i++) {
                currentCell[i] = this.cells[i][j];
            }
            nextStep(currentCell);
            for (int i = 0; i < rowColNum; i++) {
                this.cells[i][j] = currentCell[i];
                setTile(i, j, this.cells[i][j]);
            }
        }
        // 生成下一个随机数
        nextNum();
    }

    @FXML
    public void moveDown(ActionEvent event) {
        for (int j = 0; j < rowColNum; j++) {
            int[] currentCell = new int[rowColNum];
            for (int i = rowColNum; i > 0; i--) {
                currentCell[rowColNum - i] = this.cells[i - 1][j];
            }
            nextStep(currentCell);
            for (int i = rowColNum; i > 0; i--) {
                this.cells[i - 1][j] = currentCell[rowColNum - i];
                setTile(i - 1, j, this.cells[i - 1][j]);
            }
        }
        // 生成下一个随机数
        nextNum();
    }

    @FXML
    public void moveLeft(ActionEvent event) {
        for (int i = 0; i < rowColNum; i++) {
            int[] currentCell = new int[rowColNum];
            for (int j = 0; j < rowColNum; j++) {
                currentCell[j] = this.cells[i][j];
            }
            nextStep(currentCell);
            for (int j = 0; j < rowColNum; j++) {
                this.cells[i][j] = currentCell[j];
                setTile(i, j, this.cells[i][j]);
            }
        }
        // 生成下一个随机数
        nextNum();
    }

    @FXML
    public void moveRight(ActionEvent event) {
        for (int i = 0; i < rowColNum; i++) {
            int[] currentCell = new int[rowColNum];
            for (int j = rowColNum; j > 0; j--) {
                currentCell[rowColNum - j] = this.cells[i][j - 1];
            }
            nextStep(currentCell);
            for (int j = rowColNum; j > 0; j--) {
                this.cells[i][j - 1] = currentCell[rowColNum - j];
                setTile(i, j - 1, this.cells[i][j - 1]);
            }
        }
        // 生成下一个随机数
        nextNum();
    }

}