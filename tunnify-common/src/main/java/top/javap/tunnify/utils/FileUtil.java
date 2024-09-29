package top.javap.tunnify.utils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/29
 **/
public class FileUtil {

    public static String readUtf8(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                FileInputStream inputStream = new FileInputStream(file);
                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
                return new String(bytes, Charset.forName("utf-8"));
            }
        } catch (Exception e) {
        }
        return null;
    }
}