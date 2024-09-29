package top.javap.tunnify;

import top.javap.tunnify.server.TunnifyServer;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/27
 **/
public class TunnifyServerStarter {
    public static void main(String[] args) throws Exception {
        new TunnifyServer(9000, "password").start();
    }
}