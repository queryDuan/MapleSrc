package net.sf.odinms.client.messages.commands;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.tools.performance.CPUSampler;

public class ProfilingCommands implements Command {

    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProfilingCommands.class);

    @Override
    public void execute(MapleClient c, MessageCallback mc, String[] splitted) {
        if (splitted[0].equalsIgnoreCase("!startProfiling")) {
            CPUSampler sampler = CPUSampler.getInstance();
            sampler.addIncluded("net.sf.odinms");
            sampler.start();
        } else if (splitted[0].equalsIgnoreCase("!stopProfiling")) {
            CPUSampler sampler = CPUSampler.getInstance();
            try {
                String filename = "odinprofile.txt";
                if (splitted.length > 1) {
                    filename = splitted[1];
                }
                File file = new File(filename);
                if (file.exists()) {
                    mc.dropMessage("输入的文件名已存在。");
                    return;
                }
                sampler.stop();
                FileWriter fw = new FileWriter(file);
                sampler.save(fw, 1, 10);
                fw.close();
            } catch (IOException e) {
                log.error("THROW", e);
            }
            sampler.reset();
        }
    }

    @Override
    public CommandDefinition[] getDefinition() {
        return new CommandDefinition[]{
                    new CommandDefinition("startProfiling", "", "启动基于CPU的采样分析器", 5),
                    new CommandDefinition("stopProfiling", "<文件名>", "停止采样分析器,给出最后结果", 5)
                };
    }
}