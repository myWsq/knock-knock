import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wsq
 * 2018/11/7 17:31
 */

interface KeyCode {
    int LEFT = 37;
    int RIGHT = 39;
}

class Panel extends JPanel implements Runnable, KeyListener {
    private static final int GROW_SPEED = 5; // 球增加的速率

    private Hero hero;
    private List<Ball> balls = new ArrayList<>();
    private static int HERO_Y;

    Panel() {
        this.hero = new Hero(100);
        this.balls.add(new Ball());
        HERO_Y = 575 - Hero.SIZE - 20;
    }

    @Override
    public void paint(Graphics g) {
        g.drawOval(this.hero.x, HERO_Y, Hero.SIZE, Hero.SIZE);
        paintState(g);
        paintSpeed(g);
        paintBall(g);
    }

    /**
     * 画出当前方向
     *
     * @param g 当前画笔
     */
    private void paintState(Graphics g) {
        g.drawString(String.valueOf(this.hero.state), 10, 20);
    }

    /**
     * 画出当前速度
     *
     * @param g 当前画笔
     */
    private void paintSpeed(Graphics g) {
        g.drawString(String.valueOf(this.hero.speed), 10, 40);
    }

    /**
     * 画出所有的球并判断是否应该增加球的个数
     *
     * @param g 当前画笔
     */
    private void paintBall(Graphics g) {
        int totalKnocks = 0;
        int ballNum = 0;
        for (Ball ball : this.balls) {
            g.drawOval(ball.x, ball.y, ball.size, ball.size);
            totalKnocks += (ball.knockXTimes + ball.knockYTimes);
            ballNum++;
        }

        if (totalKnocks / ballNum / ballNum > GROW_SPEED) {
            this.balls.add(new Ball());
        }

    }

    /**
     * 碰撞检测, 两点之间距离是否小于两球半径之和
     *
     * @param x1 球1 x
     * @param x2 球2 x
     * @param y1 球1 y
     * @param y2 球2 y
     * @param r1 球1 半径
     * @param r2 球2 半径
     * @return 是否碰撞
     */
    private boolean isKnocked(int x1, int x2, int y1, int y2, int r1, int r2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)) <= (r1 + r2);
    }

    @Override
    public void run() {
        while (true) {
            this.hero.move(this.getWidth());

            // 移动所有的球
            for (Ball ball : this.balls) {
                ball.move(this.getWidth(), this.getHeight());
                if (this.isKnocked(this.hero.x, ball.x, HERO_Y, ball.y, Hero.SIZE / 2, ball.size / 2)) {
                    return;
                }
            }


            try {
                // FPS 60
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.repaint();

        }
    }

    @Override
    public void keyTyped(KeyEvent event) {

    }

    @Override
    public void keyPressed(KeyEvent event) {
        int code = event.getKeyCode();
        switch (code) {
            // 根据按下的键改变方向
            case KeyCode.LEFT:
                this.hero.state = Hero.LEFT;
                break;
            case KeyCode.RIGHT:
                this.hero.state = Hero.RIGHT;
                break;

        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
        int code = event.getKeyCode();
        switch (code) {
            // 如果方向未被改变, 则停止
            case KeyCode.LEFT:
                if (this.hero.state == Hero.LEFT) {
                    this.hero.state = Hero.STOP;
                }
                break;
            case KeyCode.RIGHT:
                if (this.hero.state == Hero.RIGHT) {
                    this.hero.state = Hero.STOP;
                }
                break;
        }
    }
}