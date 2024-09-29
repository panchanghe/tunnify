package top.javap.tunnify;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import top.javap.tunnify.utils.Assert;
import top.javap.tunnify.utils.FileUtil;

import java.io.File;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/29
 **/
public class TunnifyStarter {
    public static void main(String[] args) throws ParseException {
        CommandLine commandLine = buildCommandLine(args);
        final String configPath = commandLine.getOptionValue("c");
        Assert.hasText(configPath, "You can specify the configuration file using the -c parameter");
        JSONObject config = JSON.parseObject(FileUtil.readUtf8(configPath));
        check(config);
    }

    private static CommandLine buildCommandLine(String[] args) throws ParseException {
        final Options options = new Options();
        options.addOption("c", true, "config");
        return new DefaultParser().parse(options, args);
    }

    private static void check(JSONObject config) {

    }
}