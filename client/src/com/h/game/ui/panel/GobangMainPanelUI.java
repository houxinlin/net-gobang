package com.h.game.ui.panel;

import com.h.game.GobangNet;
import com.h.game.event.GobangPanelMouseEvents;
import com.h.game.event.GobangPanelMoushCallback;
import com.h.game.msg.ConsultMessage;
import com.h.game.msg.Message;
import com.h.game.msg.MessageHandler;
import com.h.game.msg.PieceMessage;
import com.h.game.ui.GobangMainUI;
import com.h.game.util.GameUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.ImageObserver;
import java.util.Iterator;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GobangMainPanelUI extends JPanel implements GobangPanelMoushCallback {
    public static final int CELL_NUMBER = 18;
    private static final int MARGIN = 20;
    private final Point mouseCurrentPoint = new Point();
    private int cellSize = 0;
    private int marginLeft = 0;
    private int marginTop = 0;
    private final GobangNet gobangNet;
    private volatile int state = 0;
    private final int[][] cellData = new int[CELL_NUMBER + 1][CELL_NUMBER + 1];
    private int color = 0;
    private boolean canNext = false;
    private int whoFirst = 0;
    private final Image successImage = Toolkit.getDefaultToolkit().getImage(GobangMainPanelUI.class.getResource("/resource/success.png"));
    private final Image failImage = Toolkit.getDefaultToolkit().getImage(GobangMainPanelUI.class.getResource("/resource/fail.png"));
    private volatile int currentImageSize = 0;

    public GobangMainPanelUI(GobangNet gobangNet) {
        this.gobangNet = gobangNet;
        this.setSize(850, 850);
        this.setBackground(Color.decode("#bf9678"));
        GobangPanelMouseEvents gobangPanelMouseEvents = new GobangPanelMouseEvents(this);
        this.addMouseListener(gobangPanelMouseEvents);
        this.addMouseMotionListener(gobangPanelMouseEvents);


        gobangNet.getMessageHandlers().removeIf(messageHandler -> messageHandler instanceof ConsultMessage || messageHandler instanceof PieceMessage);
        gobangNet.getMessageHandlers().add(new ConsultMessage(this));
        gobangNet.getMessageHandlers().add(new PieceMessage(this));
    }


    public void start(int whoFirst) {
        if (this.state == 1) return;
        this.state = 1;  //状态为1表示正在游戏中
        this.whoFirst = whoFirst; //0表示先手
        this.canNext = whoFirst == 0; //我是否可以走
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseCurrentPoint.setLocation(e.getX(), e.getY());
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!this.canNext) return;
        this.canNext = false;
        mouseCurrentPoint.setLocation(e.getX(), e.getY());
        int indexX = getNearbyIndex(e.getX(), marginLeft);
        int indexY = getNearbyIndex(e.getY(), marginTop);
        cellData[indexX][indexY] = this.whoFirst == 0 ? 1 : 2;
        gobangNet.sendMessage(Message.PIECE.getMessageName() + indexX + ":" + indexY);
        if (GameUtils.gameOver(this.cellData, this.whoFirst == 0 ? 1 : 2)) {
            this.state = 2;
            startImageAnimation();
        }
        repaint();
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
        System.out.println("开始动画");
        new Thread(() -> {
            while (currentImageSize <= 300) {
                currentImageSize += 10;
                try {
                    repaint();
                    Thread.sleep(5);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
            }
            repaint();
        }).start();
    }

    private int getNearbyIndex(int mouse, int direction) {
        int index = (mouse - direction) / cellSize;
        //左边一个坐标
        int left = index != 0 ? ((index) * cellSize + direction) : direction;
        int right = index <= CELL_NUMBER ? (index + 1) * cellSize + direction : CELL_NUMBER * cellSize + direction;
        return mouse - left <= right - mouse ? index : index + 1;


    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawString(this.whoFirst + " ", 10, 10);
        drawLayout(g);
        drawState(g);
        if (this.state == 0) return;
        drawMouseTarget(g);
        drawPiece(g);
        drawImage(g);
    }

    private void drawImage(Graphics g){
        if (this.state == 2) {
            g.drawImage(GameUtils.gameOver(this.cellData, this.whoFirst == 0 ? 1 : 2) ? successImage : failImage, getWidth() / 2 - currentImageSize / 2, getHeight() / 2 - currentImageSize / 2, currentImageSize, currentImageSize, null);
        }
    }
    private void drawState(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("黑体", Font.PLAIN, 30));
        int fontWidth = 60;
        if (this.state == 0) g.drawString("等待对手...", getWidth() / 2 - fontWidth / 2, getHeight() / 2);
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


}
