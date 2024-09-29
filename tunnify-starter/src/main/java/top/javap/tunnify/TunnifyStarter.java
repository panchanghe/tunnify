package top.javap.tunnify;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import top.javap.tunnify.client.TunnifyClient;
import top.javap.tunnify.config.BaseConfiguration;
import top.javap.tunnify.config.ClientConfiguration;
import top.javap.tunnify.config.ConfigValueConstant;
import top.javap.tunnify.config.ServerConfiguration;
import top.javap.tunnify.exceptions.TunnifyException;
import top.javap.tunnify.server.TunnifyServer;
import top.javap.tunnify.utils.Assert;
import top.javap.tunnify.utils.FileUtil;

import java.io.File;

/**
 * @author: pch
 * @description:
 * @date: 2024/9/29
 **/
public class TunnifyStarter {
    public static void main(String[] args) throws Exception {
        BaseConfiguration baseConfiguration = readConfiguration(args);
        if (baseConfiguration.isServer()) {
            startServer((ServerConfiguration) baseConfiguration);
        } else {
            startClient((ClientConfiguration) baseConfiguration);
        }
    }

    private static void startClient(ClientConfiguration clientConfiguration) throws Exception {
        final TunnifyClient client = new TunnifyClient(clientConfiguration.getServerHost(), clientConfiguration.getServerPort(), clientConfiguration.getPassword());
        client.connect().addListener(f -> {
            clientConfiguration.getPortMappings().forEach(m -> client.openProxy(m.getServerPort(), m.getTargetHost(), m.getTargetPort()));
        });
    }

    private static void startServer(ServerConfiguration serverConfiguration) throws Exception {
        new TunnifyServer(serverConfiguration.getPort(), serverConfiguration.getPassword()).start();
    }

    private static BaseConfiguration readConfiguration(String[] args) throws ParseException {
        final CommandLine commandLine = buildCommandLine(args);
        final String configPath = commandLine.getOptionValue("c");
        Assert.hasText(configPath, "You can specify the configuration file using the -c parameter");
        JSONObject configJsonObject = JSON.parseObject(FileUtil.readUtf8(configPath));
        Assert.notNull(configJsonObject, "Configuration cannot be empty");
        BaseConfiguration baseConfiguration = toConfiguration(configJsonObject);
        baseConfiguration.check();
        return baseConfiguration;
    }

    private static CommandLine buildCommandLine(String[] args) throws ParseException {
        final Options options = new Options();
        options.addOption("c", true, "config");
        return new DefaultParser().parse(options, args);
    }

    private static BaseConfiguration toConfiguration(JSONObject configJsonObject) {
        if (ConfigValueConstant.MODE_SERVER.equalsIgnoreCase(configJsonObject.getString("mode"))) {
            return configJsonObject.toJavaObject(ServerConfiguration.class);
        } else if (ConfigValueConstant.MODE_CLIENT.equalsIgnoreCase(configJsonObject.getString("mode"))) {
            return configJsonObject.toJavaObject(ClientConfiguration.class);
        } else {
            throw new TunnifyException("mode must be SERVER or CLIENT");
        }
    }
}