/**
 * @author wsq
 * 2018/11/7 18:59
 * 用于实例化下方移动的单位
 */

class Hero {
    static final int SIZE = 30;
    private static final int MAX_SPEED = 20; // 最大速度
    private static final int FRICTION = 2; // 摩擦力加速度

    static final int LEFT = -5;
    static final int RIGHT = 5; // 加速度
    static final int STOP = 0;
    int speed = 0;
    int state = STOP;
    int x;

    Hero(int x) {
        this.x = x;
    }

    // 模拟摩擦力
    private void speedLower() {
        if (this.speed > 0) {
            this.speed -= FRICTION;
        } else {
            this.speed += FRICTION;
        }
    }

    void move(int maxWidth) {

        // 有加速度
        if (this.state != STOP) {
            if (Math.abs(speed) < MAX_SPEED) {
                this.speed += this.state;
            }
        }

        // 模拟摩擦力
        this.speedLower();


        // 速度过小
        if (this.state == STOP && Math.abs(this.speed) < 3) {
            this.speed = 0;
        }

        // 位置移动
        this.x += this.speed / 2;

        // 超出边界
        if (this.x <= 0 || this.x >= (maxWidth - SIZE)) {
            if (this.x < maxWidth / 2) {
                this.x = 0;
            } else {
                this.x = maxWidth - SIZE;
            }
        }
    }
}
