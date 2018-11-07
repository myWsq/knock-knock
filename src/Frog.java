/**
 * @author wsq
 * 2018/11/7 16:15
 */

class Frog {
    private static final double JUMP_UNIT = 0.7;
    private static final int G = 1; // 模拟的重力加速度
    private int speed = 0;
    private int height = 0;

    void jump() {
        if (this.speed == 0) {
            this.speed += 20;
        } else if (this.speed < 0) {
            this.speed = 15;
        } else {
            this.speed += 5;
        }
    }

    int getHeight() {
        this.height += speed / 2;
        this.speed -= G;

        // 触底
        if (this.height < 1) {
            // 如果速度到达0的边界值, 则清空速度与高度
            if (Math.abs(this.speed) <= 1) {
                this.height = 0;
                this.speed = 0;
            } else { // 反弹
                if (this.speed < 0) {
                    this.speed = -(this.speed / 2);

                }
            }
        }

        return this.height;
    }
}
