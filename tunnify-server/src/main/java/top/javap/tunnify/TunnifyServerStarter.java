package top.javap.tunnify;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/27
 **/
public class TunnifyServerStarter {
    public static void main(String[] args) throws Exception {
        new TunnifyServer(9000).start();
    }
}