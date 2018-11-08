import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author wsq
 * 2018/11/7 17:31
 */

interface KeyCode {
    int LEFT = 37;
    int RIGHT = 39;
    int SPACE = 32;
    int ENTER = 10;
}

class Panel extends JPanel implements Runnable, KeyListener {
    private static final int GROW_SPEED = 5; // 球增加的速率

    // 当前游戏状态
    private static final int READY = 0;
    private static final int RUNNING = 1;
    private static final int PAUSE = 2;
    private static final int OVER = 3;

    private Image[] images = {
            Toolkit.getDefaultToolkit().getImage("basketball.png"),
            Toolkit.getDefaultToolkit().getImage("soccer-ball.png"),
            Toolkit.getDefaultToolkit().getImage("tennis-ball.png"),
            Toolkit.getDefaultToolkit().getImage("yoga-ball.png"),
            Toolkit.getDefaultToolkit().getImage("ball.png")
    };

    private Image titleImage = Toolkit.getDefaultToolkit().getImage("title.png");
    private Image startImage = Toolkit.getDefaultToolkit().getImage("start-text.png");
    private Image heroImage = Toolkit.getDefaultToolkit().getImage("happy.png");
    private Image pauseImage = Toolkit.getDefaultToolkit().getImage("pause.png");
    private Image overTextImage = Toolkit.getDefaultToolkit().getImage("over-text.png");
    private Image restartImage = Toolkit.getDefaultToolkit().getImage("restart.png");

    private Hero hero;
    private List<Ball> balls = new ArrayList<>();
    private static int HERO_Y;
    private Score score = new Score();
    private int curScore = 0; // 玩家当前得分
    private Random random = new Random();
    private int state = READY;

    Panel() {
        this.setBackground(new Color(40, 42, 54));
        this.hero = new Hero(100);
        this.generateBall();
        HERO_Y = 575 - Hero.SIZE - 20;
    }

    @Override
    public void paint(Graphics g) {
        switch (this.state) {
            case READY:
                g.drawImage(this.titleImage, (this.getWidth() - this.titleImage.getWidth(null)) / 2, 40, null);
                g.drawImage(
                        this.startImage,
                        (this.getWidth() - this.startImage.getWidth(null)) / 2,
                        (this.getHeight() - this.startImage.getHeight(null) + 80) / 2,
                        null
                );
                g.drawImage(this.heroImage, this.hero.x, HERO_Y, Hero.SIZE, Hero.SIZE, new Color(0, 0, 0, 0), null);

                break;
            case RUNNING:
                g.drawImage(this.heroImage, this.hero.x, HERO_Y, Hero.SIZE, Hero.SIZE, new Color(0, 0, 0, 0), null);
                g.setColor(Color.GRAY);
                paintState(g);
                paintSpeed(g);
                g.setColor(Color.orange);
                paintScore(g);
                g.setColor(Color.GREEN);
                paintCurScore(g);
                paintBall(g);
                break;
            case PAUSE:
                g.drawImage(this.heroImage, this.hero.x, HERO_Y, Hero.SIZE, Hero.SIZE, new Color(0, 0, 0, 0), null);
                g.setColor(Color.GRAY);
                paintState(g);
                paintSpeed(g);
                g.setColor(Color.orange);
                paintScore(g);
                g.setColor(Color.GREEN);
                paintCurScore(g);
                paintBall(g);
                // 画出暂停图标
                this.paintPause(g);
            case OVER:
                g.drawImage(this.overTextImage,
                        (this.getWidth() - this.overTextImage.getWidth(null)) / 2,
                        80,
                        null
                );
                String scoreString = String.valueOf(this.curScore);
                Font font = new Font("Verdana", Font.BOLD, 64);
                FontMetrics fontMetrics = g.getFontMetrics(font);
                int textWidth = fontMetrics.stringWidth(scoreString);
                g.setFont(font);
                g.setColor(new Color(255, 121, 198));
                g.drawString(scoreString, (this.getWidth() - textWidth) / 2, (80 + this.overTextImage.getHeight(null) + 100));
                g.drawImage(this.restartImage,
                        (this.getWidth() - this.restartImage.getWidth(null)) / 2,
                        400,
                        null
                );
                break;
        }


    }


    private void paintPause(Graphics g) {
        g.drawImage(this.pauseImage, 0, 0, null);
    }

    private void paintCurScore(Graphics g) {
        g.drawString("当前得分: " + String.valueOf(this.curScore), 10, 60);
    }

    private void paintScore(Graphics g) {
        int step = 20; // 向下绘制的步长
        int y = 80; // 初始纵坐标
        int i = 0;

        for (Item item : this.score.items) {
            i++;
            g.drawString(String.valueOf(i) + ". " + " 得分: " + String.valueOf(item.score), 10, y);
            y += step;
        }

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
            g.drawImage(ball.image, ball.x, ball.y, ball.size, ball.size, new Color(0, 0, 0, 0), null);
            totalKnocks += (ball.knockXTimes + ball.knockYTimes);
            ballNum++;
        }

        if (totalKnocks / ballNum / ballNum > GROW_SPEED) {
            this.generateBall();
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

    private void generateBall() {
        this.balls.add(new Ball(this.images[random.nextInt(this.images.length - 1)]));
    }

    @Override
    public void run() {
        while (true) {
            this.hero.move(this.getWidth());
            switch (this.state) {
                case RUNNING:
                    int totalKnock = 0;
                    // 计算球的状态
                    for (Ball ball : this.balls) {
                        // 移动球
                        ball.move(this.getWidth(), this.getHeight());
                        // 统计得分
                        totalKnock += (ball.knockXTimes + ball.knockYTimes);
                        // 碰撞检测
                        if (this.isKnocked(this.hero.x, ball.x, HERO_Y, ball.y, Hero.SIZE / 2, ball.size / 2)) {
                            // 存储分数
                            this.score.push(this.curScore);
                            this.state = OVER;
                            break;
                        }
                    }
                    // 如果游戏未结束
                    if (this.state == RUNNING) {
                        this.curScore = totalKnock;
                    }
                    break;
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

        // 控制英雄移动
        if (this.state == RUNNING || this.state == READY) {
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
    }

    @Override
    public void keyReleased(KeyEvent event) {
        int code = event.getKeyCode();
        // 控制英雄移动
        if (this.state == RUNNING || this.state == READY) {
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

        // 控制游戏状态
        switch (this.state) {
            case READY:
                if (code == KeyCode.ENTER) {
                    this.state = RUNNING;
                }
                break;
            case RUNNING:
                if (code == KeyCode.SPACE) {
                    this.state = PAUSE;
                    // 强制英雄停止
                    this.hero.state = Hero.STOP;
                    this.hero.speed = 0;
                }
                break;
            case PAUSE:
                if (code == KeyCode.SPACE) {
                    this.state = RUNNING;
                }
                break;
            case OVER:
                if (code == KeyCode.ENTER) {
                    this.balls.clear();
                    this.generateBall();
                    this.curScore = 0;
                    this.state = RUNNING;
                }
        }
    }
}