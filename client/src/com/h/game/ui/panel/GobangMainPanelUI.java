package com.h.game.ui.panel;

import com.h.game.GobangNet;
import com.h.game.event.GobangPanelMouseEvents;
import com.h.game.event.GobangPanelMouseCallback;
import com.h.game.msg.ConsultMessage;
import com.h.game.msg.ExitRoomMessage;
import com.h.game.msg.Message;
import com.h.game.msg.PieceMessage;
import com.h.game.util.GameUtils;
import sun.font.FontDesignMetrics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 棋盘UI
 */
public class GobangMainPanelUI extends JPanel implements GobangPanelMouseCallback {
    public static final int CELL_NUMBER = 18;
    private static final int MARGIN = 20;
    private final Point mouseCurrentPoint = new Point();
    //格子大小
    private int cellSize = 0;
    //左边边据
    private int marginLeft = 0;
    //上面边据
    private int marginTop = 0;
    //网络管理
    private final GobangNet gobangNet;
    //游戏状态
    private volatile int state = 0;
    //棋盘数据
    private final int[][] cellData = new int[CELL_NUMBER + 1][CELL_NUMBER + 1];
    //我是不是可以走
    private boolean canNext = false;
    //先手
    private int whoFirst = 0;
    private final Image successImage = Toolkit.getDefaultToolkit().getImage(GobangMainPanelUI.class.getResource("/resource/success.png"));
    private final Image failImage = Toolkit.getDefaultToolkit().getImage(GobangMainPanelUI.class.getResource("/resource/fail.png"));
    private volatile int currentImageSize = 0;
    private volatile boolean drawTip = false;
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
    private Font tipFont = new Font("宋体", Font.PLAIN, 50);

    public GobangMainPanelUI(GobangNet gobangNet) {
        this.gobangNet = gobangNet;
        this.setSize(850, 850);
        this.setBackground(Color.decode("#bf9678"));
        GobangPanelMouseEvents gobangPanelMouseEvents = new GobangPanelMouseEvents(this);
        this.addMouseListener(gobangPanelMouseEvents);
        this.addMouseMotionListener(gobangPanelMouseEvents);
        gobangNet.getMessageHandlers().removeIf(messageHandler -> messageHandler instanceof ConsultMessage || messageHandler instanceof PieceMessage || messageHandler instanceof ExitRoomMessage);
        gobangNet.getMessageHandlers().add(new ConsultMessage(this));
        gobangNet.getMessageHandlers().add(new PieceMessage(this));
        gobangNet.getMessageHandlers().add(new ExitRoomMessage(this));
    }


    public void start(int whoFirst) {
        if (this.state == 1) return;
        this.state = 1;  //状态为1表示正在游戏中
        this.whoFirst = whoFirst; //0表示先手
        this.canNext = whoFirst == 0; //我是否可以走
        this.drawTip = true;// 绘制提示
        this.scheduledThreadPoolExecutor.schedule(() -> {
            this.drawTip = false;
            repaint();
        }, 1, TimeUnit.SECONDS);
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseCurrentPoint.setLocation(e.getX(), e.getY());
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (this.state == 2) {
            again(); //如果游戏结束后还单击布局，则准备下一次对局
            return;
        }
        //如果不轮我走，则终止
        if (!this.canNext) return;
        this.canNext = false;
        mouseCurrentPoint.setLocation(e.getX(), e.getY());
        int indexX = getNearbyIndex(e.getX(), marginLeft);
        int indexY = getNearbyIndex(e.getY(), marginTop);
        cellData[indexX][indexY] = this.whoFirst == 0 ? 1 : 2;
        //向服务器发送我走的位置
        gobangNet.sendMessage(Message.PIECE.getMessageName() + indexX + ":" + indexY);
        //是否游戏结束
        if (GameUtils.gameOver(this.cellData, this.whoFirst == 0 ? 1 : 2)) {
            this.state = 2;
            //开始图片动画
            startImageAnimation();
        }
        repaint();
    }

    /**
     * 重置棋盘
     */
    private void reset() {
        this.state = 0;
        this.canNext = false;
        this.currentImageSize = 0;
        for (int i = 0; i < CELL_NUMBER + 1; i++) {
            for (int j = 0; j < CELL_NUMBER + 1; j++) {
                cellData[i][j] = 0;
            }
        }
        repaint();
    }

    /**
     * 准备下一轮对局
     */
    private void again() {
        boolean isSuccess = GameUtils.gameOver(this.cellData, this.whoFirst == 0 ? 1 : 2);
        reset(); //重置数据，布局进入等待对手状态
        //如果先手输了，切换,赢了颜色不变，同样继续先手
        gobangNet.sendMessage("again" + ((this.whoFirst == 0 && !isSuccess) ? 1 : 0));

    }

    /**
     * 显示对方棋
     *
     * @param x
     * @param y
     * @param value
     */
    public void setCellData(int x, int y, int value) {
        cellData[x][y] = this.whoFirst == 0 ? 2 : 1;
        if (GameUtils.gameOver(this.cellData, this.whoFirst == 0 ? 2 : 1)) {
            this.state = 2;
            startImageAnimation();
        }
        repaint();
        this.canNext = true;
    }

    private void startImageAnimation() {
        new Thread(() -> {
            while (currentImageSize <= 300) {
                currentImageSize += 10;
                try {
                    repaint();
                    Thread.sleep(25);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
                repaint();
            }
            repaint();
        }).start();
    }


    private int getNearbyIndex(int mouse, int direction) {
        int index = (mouse - direction) / cellSize;
        //左边一个坐标
        int left = index != 0 ? ((index) * cellSize + direction) : direction;
        //右边一个坐标
        int right = index <= CELL_NUMBER ? (index + 1) * cellSize + direction : CELL_NUMBER * cellSize + direction;

        //比较谁近
        return mouse - left <= right - mouse ? index : index + 1;

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawLayout(g); //绘制棋盘
        drawState(g);  //绘制状态
        if (this.state == 0) return; //如果游戏没开始，下面都不需要绘制
        if (this.state != 2 && this.canNext) drawMouseTarget(g); //如果游戏没有结束并且是我走，则绘制鼠标所选位置
        drawPiece(g);   //绘制棋子
        drawImage(g);   //绘制胜利或者失败图片
    }

    /**
     * 绘制成功胜利还是失败图片
     *
     * @param g
     */
    private void drawImage(Graphics g) {
        if (this.state == 2) {
            g.drawImage(GameUtils.gameOver(this.cellData, this.whoFirst == 0 ? 1 : 2) ? successImage : failImage, getWidth() / 2 - currentImageSize / 2, getHeight() / 2 - currentImageSize / 2, currentImageSize, currentImageSize, null);
        }
    }

    /**
     * 绘制状态
     *
     * @param g
     */
    private void drawState(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(tipFont);
        if (drawTip && this.whoFirst == 0)
            g.drawString("先手", getWidth() / 2 - getFontWidth(tipFont, "先手") / 2, getHeight() / 2);
        if (this.state == 0)
            g.drawString("等待对手...", getWidth() / 2 - getFontWidth(tipFont, "等待对手...") / 2, getHeight() / 2);
    }

    /**
     * 获取字体宽度
     *
     * @param font
     * @param str
     * @return
     */
    private int getFontWidth(Font font, String str) {
        FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);
        return metrics.stringWidth(str);
    }

    /**
     * 绘制棋子
     *
     * @param g
     */
    private void drawPiece(Graphics g) {
        for (int i = 0; i < CELL_NUMBER + 1; i++) {
            for (int j = 0; j < CELL_NUMBER + 1; j++) {
                if (cellData[i][j] != 0) {
                    if (cellData[i][j] == 1) g.setColor(Color.BLACK);
                    if (cellData[i][j] == 2) g.setColor(Color.WHITE);
                    g.fillOval(((i * cellSize) + marginLeft) - (cellSize / 2), (j * cellSize + marginTop) - (cellSize / 2), cellSize, cellSize);
                }
            }
        }

    }

    /**
     * 绘制鼠标位置
     *
     * @param g
     */
    private void drawMouseTarget(Graphics g) {

        if (mouseCurrentPoint.x == 0 || mouseCurrentPoint.y == 0) return;
        int indexX = getNearbyIndex(mouseCurrentPoint.x, marginLeft);
        int indexY = getNearbyIndex(mouseCurrentPoint.y, marginTop);
        g.setColor(Color.decode("#59763f"));
        ((Graphics2D) g).setStroke(new BasicStroke(3.0f));
        g.drawRoundRect(((indexX * cellSize) + marginLeft) - (cellSize / 2), (indexY * cellSize + marginTop) - (cellSize / 2), cellSize, cellSize, 10, 10);
    }

    /**
     * 绘制布局
     *
     * @param g
     */
    private void drawLayout(Graphics g) {
        //最小面板大小
        int gridSize = Math.min(getWidth(), getHeight()) - MARGIN;
        //确保大小能正除
        while (gridSize % CELL_NUMBER != 0) gridSize--;
        //横竖格子的大小（高宽）
        cellSize = gridSize / CELL_NUMBER;
        int widthSurplusSize = (getWidth() - (cellSize * CELL_NUMBER));
        int heightSurplusSize = (getHeight() - (cellSize * CELL_NUMBER));
        marginLeft = widthSurplusSize / 2;
        marginTop = heightSurplusSize / 2;
        g.drawRect(marginLeft, marginTop, CELL_NUMBER * cellSize, CELL_NUMBER * cellSize);

        for (int i = 0; i < CELL_NUMBER; i++) {
            int startY = (i * cellSize) + marginTop;
            g.drawLine(marginLeft, startY, CELL_NUMBER * cellSize + marginLeft, startY);
        }
        for (int i = 0; i < CELL_NUMBER; i++) {
            g.drawLine(marginLeft + (i * cellSize), marginTop, marginLeft + (i * cellSize), cellSize * CELL_NUMBER + marginTop);
        }
    }

    private void log(int... arg) {
        for (int i : arg) {
            System.out.print(i + "   ");
        }
        System.out.println();
    }

    /**
     * 对手退出
     */
    public void opponentExit() {
        this.reset();
    }
}
