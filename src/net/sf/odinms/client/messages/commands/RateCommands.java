package net.sf.odinms.client.messages.commands;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.tools.MaplePacketCreator;

public class RateCommands implements Command {

    @Override
    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception, IllegalCommandSyntaxException {
        if (splitted[0].equals("!rate")) {
            if (splitted.length > 2) {
                int arg = Integer.parseInt(splitted[2]);
                int seconds = Integer.parseInt(splitted[3]);
                int mins = Integer.parseInt(splitted[4]);
                int hours = Integer.parseInt(splitted[5]);
                int time = seconds + (mins * 60) + (hours * 60 * 60);
                boolean bOk = true;
                for (final ChannelServer cservs : ChannelServer.getAllInstances()) {
                    if (splitted[1].equals("exp")) {
                        if (arg <= 500000) {
                            cservs.setExpRate(arg);
                            cservs.broadcastPacket(MaplePacketCreator.serverNotice(6, "经验已经成功修改为 " + arg + "倍。祝大家游戏开心！"));
                        } else {
                            mc.dropMessage("操作已被系统限制。");
                        }
                    } else if (splitted[1].equals("drop")) {
                        if (arg <= 5) {
                            cservs.setDropRate(arg);
                            cservs.broadcastPacket(MaplePacketCreator.serverNotice(6, "爆率已经成功修改为 " + arg + "倍。祝大家游戏开心！"));
                        } else {
                            mc.dropMessage("操作已被系统限制。");
                        }
                    } else if (splitted[1].equals("meso")) {
                        if (arg <= 500000) {
                            cservs.setMesoRate(arg);
                            cservs.broadcastPacket(MaplePacketCreator.serverNotice(6, "金币已经成功修改为 " + arg + "倍。祝大家游戏开心！"));
                        } else {
                            mc.dropMessage("操作已被系统限制。");
                        }
                    } else if (splitted[1].equals("bossdrop")) {
                        if (arg <= 500000) {
                            cservs.setBossDropRate(arg);
                            cservs.broadcastPacket(MaplePacketCreator.serverNotice(6, "BOSS掉宝已经成功修改为 " + arg + "倍。祝大家游戏开心！"));
                        } else {
                            mc.dropMessage("操作已被系统限制。");
                        }
                    } else if (splitted[1].equals("petexp")) {
                        if (arg <= 500000) {
                            cservs.setPetExpRate(arg);
                            cservs.broadcastPacket(MaplePacketCreator.serverNotice(6, "宠物经验已经成该修改为 " + arg + "倍。祝大家游戏开心！"));
                        } else {
                            mc.dropMessage("操作已被系统限制。");
                        }
                    } else {
                        bOk = false;
                    }
                    final String rate = splitted[1];
                    TimerManager.getInstance().schedule(new Runnable() {

                        @Override
                        public void run() {
                            if (rate.equals("exp")) {
                                cservs.setExpRate(30);
                            } else if (rate.equals("drop")) {
                                cservs.setDropRate(1);
                            } else if (rate.equals("meso")) {
                                cservs.setMesoRate(5);
                            } else if (rate.equals("bossdrop")) {
                                cservs.setBossDropRate(1);
                            } else if (rate.equals("petexp")) {
                                cservs.setPetExpRate(2);
                            }
                            cservs.broadcastPacket(MaplePacketCreator.serverNotice(6, " 系统经验活动已经结束。系统已成功自动切换为正常游戏模式！"));
                        }
                    }, time * 1000);
                }
                if (bOk == false) {
                    mc.dropMessage("使用方法: !rate <exp|drop|meso|boss|pet> <类> <秒> <分> <时> 如:!rate exp 2008 3 2 1 就是,改变经验为2008倍,1小时2分3秒,系统时间过后,将自动重置到原本设置.");
                }
            } else {
                mc.dropMessage("使用方法: !rate <exp|drop|meso|boss|pet> <类> <秒> <分> <时> 如:!rate exp 2008 3 2 1 就是,改变经验为2008倍,1小时2分3秒,系统时间过后,将自动重置到原本设置.");
                }
        } else if (splitted[0].equals("!ratesee")) {
            ChannelServer cserv = c.getChannelServer();
            mc.dropMessage("目前服务器经验爆率信息:");
            mc.dropMessage("经验: " + cserv.getExpRate() + "倍 | 宠物: " + cserv.getPetExpRate() + "倍");
            mc.dropMessage("爆率: " + cserv.getDropRate() + "倍 | BOSS爆率: " + cserv.getBossDropRate() + "倍");
            mc.dropMessage("金币: " + cserv.getMesoRate() + "倍");
        }
    }

    @Override
    public CommandDefinition[] getDefinition() {
        return new CommandDefinition[]{
                    new CommandDefinition("rate", "<exp|drop|meso|bossdrop|petexp> <amount> <seconds> <minutes> <hours>", "Changes the specified rate", 5),
                    new CommandDefinition("ratesee", "", "Shows each rate", 5)
                };
    }
}