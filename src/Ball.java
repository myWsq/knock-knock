import java.util.Random;

/**
 * @author wsq
 * 2018/11/7 21:20
 * 用于实例化躲避球
 */

class Ball {
    private static final int SPEED_GROW_RATE = 3; // 球速度加快的速率
    private static final int MAX_SPEED = 10; // 球的最快速度
    private int speedXSeed; // 速度种子
    private int speedYSeed;

    int speedX = 1; // X轴速度
    int speedY = 1; // Y轴速度
    int size; // 球的大小
    int knockXTimes = 1; // 碰撞两边次数
    int knockYTimes = 1; // 碰撞上下次数
    int x = -size;
    int y = -size;


    Ball() {
        Random r = new Random();
        // size 为 30 - 100
        this.size = 30 + r.nextInt(100);

        // 初始 seed 为 1 - 5;
        this.speedXSeed = 1 + r.nextInt(5);
        this.speedYSeed = 1 + r.nextInt(5);
        this.getSpeed();
    }

    /**
     * 获得当前的实际速度
     *
     * @param seed 速度种子
     * @return 当前的实际速度
     */
    private int getSpeed(int seed) {
        double result = MAX_SPEED / (1 + Math.pow(Math.E, -0.1 * seed + 1));
        return Math.round((float) result);
    }

    /**
     * 根据当前的种子计算速度
     */
    private void getSpeed() {
        this.speedX = this.speedX / Math.abs(this.speedX) * getSpeed(this.speedXSeed);
        this.speedY = this.speedY / Math.abs(this.speedY) * getSpeed(this.speedYSeed);
    }


    void move(int width, int height) {

        // 碰到了两边
        if ((this.x < 0 && this.speedX < 0) || (this.x > (width - this.size) && this.speedX > 0)) {
            speedX = -speedX;
            knockXTimes++;
            if (this.knockXTimes % SPEED_GROW_RATE == 0) {
                this.speedXSeed += 1;
                this.getSpeed();
            }

        }
        // 碰到了上下
        if ((this.y < 0 && this.speedY < 0) || (this.y > (height - this.size) && this.speedY > 0)) {
            this.speedY = -this.speedY;
            this.knockYTimes++;
            if (this.knockYTimes % SPEED_GROW_RATE == 0) {
                this.speedYSeed += 1;
                this.getSpeed();
            }
        }


        // 移动
        this.x += this.speedX;
        this.y += this.speedY;
    }


}
