import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        JFrame jf = new JFrame();
        jf.setResizable(false);
        jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE); // 退出后清空内存
        jf.setAlwaysOnTop(true); //设置窗口前置
        jf.setLocationRelativeTo(jf.getOwner());
        jf.setSize(800, 600);
        Panel panel = new Panel();
        jf.add(panel);
        jf.addKeyListener(panel);
        jf.setVisible(true);


        Thread th = new Thread(panel);
        th.start();
    }
}
