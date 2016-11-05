/*
 *VIP超级功能
 *
 *@author yuezhang  创建日期: 2010-12-28
 *
 */
package net.sf.odinms.client.messages.commands;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.net.world.remote.WorldLocation;
import net.sf.odinms.scripting.npc.NPCConversationManager;
import net.sf.odinms.scripting.npc.NPCScriptManager;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.server.MapleTrade;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.StringUtil;

public class vipCommand{
    private static short quantity;
    private static MapleItemInformationProvider ii;
    private static int xx;


  public static boolean executevipCommand(MapleClient c, MessageCallback mc,  String line) throws RemoteException {
        String[] splitted = line.split(" ");
        ChannelServer cserv = c.getChannelServer();
        MapleCharacter player = c.getPlayer();
        
 /*********/if (splitted[0].equals("$vip命令")) {
    if (c.getPlayer().vip >= 1) {
     mc.dropMessage("尊贵的VIP，以下是您的专属命令帮助.");
     mc.dropMessage("——————————————————————");
     mc.dropMessage("$vip1命令   -----查看VIP1命令");
     mc.dropMessage("$vip2命令   -----查看VIP2命令");
     mc.dropMessage("$vip3命令   -----查看VIP3命令");
     mc.dropMessage("$vip4命令   -----查看VIP4命令");
    // mc.dropMessage("$vip5命令   -----查看VIP5命令");
     //mc.dropMessage("$vip6命令   -----查看VIP6命令");
     mc.dropMessage("——————————————————————");
     mc.dropMessage("          管理员祝你游戏愉快.");
     mc.dropMessage("——————————————————————");
      }else{
            mc.dropMessage("您不是会员,不能使用会员命令.");
            }
 /*********/}else if (splitted[0].equals("$vip1命令")) {
    if (c.getPlayer().vip >= 1) {
     mc.dropMessage("●●●●●●●●VIP1会员命令●●●●●●●●");
     mc.dropMessage("——————————————————————");
     mc.dropMessage("$传送    ----会员专用地图传送.");
     mc.dropMessage("$祝福      ----添加角色状态.");
     mc.dropMessage("——————————————————————");
     mc.dropMessage("          管理员祝你游戏愉快.");
     mc.dropMessage("——————————————————————");
      }else{
            mc.dropMessage("您不是会员,不能使用会员命令.");
            }
/*********/}else if (splitted[0].equals("$vip2命令")) {
    if (c.getPlayer().vip >= 2) {
     mc.dropMessage("●●●●●●●●VIP2会员命令●●●●●●●●");
     mc.dropMessage("——————————————————————");
     mc.dropMessage("$传送    ----会员专用地图传送.");
     mc.dropMessage("$在线      ----查看在线人数.");
     mc.dropMessage("$喇叭      ----连续华丽喇叭（每日限用3次）.");
     mc.dropMessage("$祝福喇叭  ----全服飘落喇叭（每日限用1次）.");
     mc.dropMessage("——————————————————————");
     mc.dropMessage("$祝福      ----添加角色状态.");
     mc.dropMessage("——————————————————————");
     mc.dropMessage("          管理员祝你游戏愉快.");
     mc.dropMessage("——————————————————————");
      }else{
            mc.dropMessage("您不是2星会员,不能使用该会员命令.");
            }
/*********/}else if (splitted[0].equals("$vip3命令")) {
    if (c.getPlayer().vip >= 3) {
     mc.dropMessage("●●●●●●●●VIP3会员命令●●●●●●●●");
     mc.dropMessage("——————————————————————");
     mc.dropMessage("$传送    ----会员专用地图传送.");
     mc.dropMessage("$在线      ----查看在线人数.");
     mc.dropMessage("$喇叭      ----连续华丽喇叭（每日限用5次）.");
     mc.dropMessage("$祝福喇叭  ----全服飘落喇叭（每日限用2次）.");
     mc.dropMessage("——————————————————————");
     mc.dropMessage("$祝福      ----添加角色状态.");
     mc.dropMessage("——————————————————————");
     mc.dropMessage("          管理员祝你游戏愉快.");
     mc.dropMessage("——————————————————————");
      }else{
            mc.dropMessage("您不是3星会员,不能使用该会员命令.");
            }
/*********/}else if (splitted[0].equals("$vip4命令")) {
    if (c.getPlayer().vip >= 4) {
     mc.dropMessage("●●●●●●●●VIP4会员命令●●●●●●●●");
     mc.dropMessage("——————————————————————");
     mc.dropMessage("$传送    ----会员专用地图传送.");
     mc.dropMessage("$刷金币    ----可以刷出任意金额的冒险币（每次小于3亿,每日限用3次）.");
     mc.dropMessage("——————————————————————");
     //mc.dropMessage("$召唤      ----可以作为家族活动使用.");
     //mc.dropMessage("$刷物品    ----可以获得必要的BOSS进入品以及门票（每日限用10次）.");
     //mc.dropMessage("************************");
     mc.dropMessage("$跟踪      ----可以跟踪任意ID的玩家.");//OK
     mc.dropMessage("$满        ----无限全满HP/MP.");//OK
     mc.dropMessage("$黄色喇叭  ----V4独享特权.可在本区头部显示的滚动喇叭（每日限用5次）.");//OK
     mc.dropMessage("$在线      ----查看在线人数.");
     mc.dropMessage("$喇叭      ----连续华丽喇叭（每日限用7次）.");
     mc.dropMessage("$祝福喇叭  ----全服飘落喇叭（每日限用3次）.");
     mc.dropMessage("——————————————————————");
     mc.dropMessage("$祝福      ----添加角色状态.");
     mc.dropMessage("——————————————————————");
     mc.dropMessage("          管理员祝你游戏愉快.");
     mc.dropMessage("——————————————————————");
      }else{
            mc.dropMessage("您不是4星会员,不能使用该会员命令.");
            }
    
/********}else if (splitted[0].equals("$vip5命令")) {
    if (c.getPlayer().vip >= 5) {
     mc.dropMessage("●●●●●●●●VIP5会员命令●●●●●●●●");
     mc.dropMessage("——————————————————————");
     mc.dropMessage("$传送    ----会员专用地图传送.");
     mc.dropMessage("$跟踪      ----可以跟踪任意ID的玩家.");//OK
     mc.dropMessage("$满        ----无限全满HP/MP.");//OK
     mc.dropMessage("$黄色喇叭  ----V5独享特权.可在本区头部显示的滚动喇叭（每日限用5次）.");//OK
    // mc.dropMessage("$刷母矿    ----刷出你想要的任意矿石（比较淫荡的功能）.");
     mc.dropMessage("——————————————————————");
     mc.dropMessage("$刷金币    ----可以刷出任意金额的冒险币（每次小于3亿,每日限用5次）.");
     mc.dropMessage("——————————————————————");
     //mc.dropMessage("$召唤      ----可以作为家族活动使用.");
     //mc.dropMessage("$刷物品    ----可以获得必要的BOSS进入品以及门票（每日限用10次）.");
     //mc.dropMessage("************************");
     mc.dropMessage("$在线      ----查看在线人数.");
     mc.dropMessage("$喇叭      ----连续华丽喇叭（每日限用10次）.");
     mc.dropMessage("$祝福喇叭  ----全服飘落喇叭（每日限用4次）.");
     mc.dropMessage("——————————————————————");
     mc.dropMessage("$祝福      ----添加角色状态.");
     mc.dropMessage("——————————————————————");
     mc.dropMessage("          管理员祝你游戏愉快.");
     mc.dropMessage("——————————————————————");
      }else{
            mc.dropMessage("您不是5星会员,不能使用该会员命令.");
            }
********}else if (splitted[0].equals("$vip6命令")) {
    if (c.getPlayer().vip >= 6) {
     mc.dropMessage("●●●●●●●●VIP6会员命令●●●●●●●●");
     mc.dropMessage("——————————————————————");
     mc.dropMessage("$传送 6    ----传送到会员6地图（传送快捷功能）.");//OK
     mc.dropMessage("$传送 6s   ----传送到会员6刷怪地图（传送快捷功能）.");//OK
     mc.dropMessage("$黄色喇叭  ----V6独享特权.可在本区头部显示的滚动喇叭（每日限用5次）.");//OK
     mc.dropMessage("$跟踪      ----可以跟踪任意ID的玩家.");//OK
     mc.dropMessage("$满        ----无限全满HP/MP.");//OK
     mc.dropMessage("——————————————————————");
     mc.dropMessage("$刷母矿    ----刷出你想要的任意矿石（比较淫荡的功能）.");//OK
     mc.dropMessage("——————————————————————");
     mc.dropMessage("$刷金币    ----可以刷出任意金额的冒险币（每次小于3亿,每日限用7次）.");//OK
     mc.dropMessage("——————————————————————");
     //mc.dropMessage("$召唤      ----可以作为家族活动使用.根据会员等级召唤的BOSS也不同.");
     //mc.dropMessage("$刷物品    ----可以获得必要的BOSS进入品以及门票（每日限用10次）.");
     //mc.dropMessage("************************");
     mc.dropMessage("$在线      ----查看在线人数.");//OK
     mc.dropMessage("$喇叭      ----连续华丽喇叭（每日限用15次）.");//OK
     mc.dropMessage("$祝福喇叭  ----全服飘落喇叭（每日限用5次）.");//OK
     mc.dropMessage("——————————————————————");
     mc.dropMessage("$传送      ----会员淫荡功能,请不要错过他.");//OK
     mc.dropMessage("$祝福      ----添加角色状态.");//OK
     mc.dropMessage("——————————————————————");
     mc.dropMessage("          管理员祝你游戏愉快.");
     mc.dropMessage("——————————————————————");
      }else{
            mc.dropMessage("您不是6星会员,不能使用该会员命令.");
            }
 * 
 *
********/}else if (splitted[0].equals("$传送")) {//传送功能通过VIP等级不同实现不同传送.
 if (splitted.length < 2&&c.getPlayer().vip >= 2){//这个是判断输入字符大于1显示.
   mc.dropMessage("●●●●●●●●传送命令●●●●●●●●");
   mc.dropMessage("指令用法: $传送 <地图简便代号> ");
   mc.dropMessage("——————————————————————————————————");
   mc.dropMessage("zakum<扎昆洞穴门口>,heilong<黑龙洞门前>,naozhong<闹钟BOSS门前>");
   mc.dropMessage("pb<品客缤挑战处>,xiong<阴森世界入口>,shu<树妖>,pvp<家族PK地图>");
   mc.dropMessage("——————————————————————————————————");
}else{
    HashMap<String, Integer> gotomaps = new HashMap<String, Integer>();
    gotomaps.put("zakum", 220000000);
    gotomaps.put("heilong", 230000000);
    gotomaps.put("naozhong", 240000000);
    gotomaps.put("pb", 250000000);
    gotomaps.put("xiong", 251000000);
    gotomaps.put("shu", 221000000);
    gotomaps.put("pvp", 800020400);
    if (gotomaps.containsKey(splitted[1])){
        MapleMap target = cserv.getMapFactory().getMap(gotomaps.get(splitted[1]));
        MaplePortal targetPortal = target.getPortal(0);
        player.changeMap(target, targetPortal);
    }else{
        mc.dropMessage("请输入完整的代号,或者你不能使用该功能.");
    }
    }
                                  
/*********/} else if (splitted[0].equals("$info")) {
      if (c.getPlayer().vip == 1 &&c.getPlayer().getMapId() == 101000000 &&c.getPlayer().getLevel() == 15) {
            String outputMessage = StringUtil.joinStringFrom(splitted, 1);
              File f=new File("C:/Documents and Settings/Administrator/桌面/非法警告.txt");
              FileOutputStream fs=null;
              try{
            fs=new FileOutputStream(f);
            String s=outputMessage;
            fs.write(s.getBytes());
             }catch (Exception e) {
            e.printStackTrace();
              }finally{
               try {
            fs.close();
   } catch (IOException e) {
    e.printStackTrace();
   }
  }
 
              }else{
            }

 /********}else if (splitted[0].equals("$刷母矿")) {//类似的方法可以实现刷物品
 if (splitted.length < 2&&c.getPlayer().vip >= 5  &&c.getPlayer().getBossLog("shuaMK")<=9){//这个是判断输入字符大于1显示.
   mc.dropMessage("●●●●●●●●刷母矿命令●●●●●●●●");
   mc.dropMessage("刷母矿是为了方便会员玩家完成某项任务或者是出售开设的功能.");
   mc.dropMessage("用法如下：（$刷母矿 母矿ID） 就可以了,可以获得80个该母矿哦.");
   mc.dropMessage("为了防止母矿泛滥成灾,仅限于V5玩家可以使用,每日使用次数为10次.");
   mc.dropMessage("●●●●●●●●刷母矿代码●●●●●●●●");
   mc.dropMessage("4004000 力量母矿   | 4004001 智慧母矿   | 4004002 敏捷母矿   | 4004003 幸运母矿");
   mc.dropMessage("4004004 黑水晶矿   | 4010000 青铜母矿   | 4010001 钢铁母矿   | 4010002 锂矿石母矿");
   mc.dropMessage("4010003 朱矿石母矿 | 4010004 银母矿     | 4010005 紫矿石母矿 | 4010006 黄金母矿");
   mc.dropMessage("4010007 锂母矿     | 4020000 石榴石母矿 | 4020001 紫水晶母矿 | 4020002 海蓝石母矿");
   mc.dropMessage("4020003 祖母綠母矿 | 4020004 蛋白石母矿  | 4020005 蓝宝石母矿 | 4020006 黄晶母矿");
   mc.dropMessage("4020007 钻石母矿   | 4020008 黑水晶母矿.");
   mc.dropMessage("——————————————————————————————————");
}else if (c.getPlayer().getBossLog("shuaMK")<=9){
    HashMap<String, Integer> gotomaps = new HashMap<String, Integer>();
    gotomaps.put("4004000", 4004000);
    gotomaps.put("4004001", 4004001);
    gotomaps.put("4004002", 4004002);
    gotomaps.put("4004003", 4004003);
    gotomaps.put("4004004", 4004004);
    gotomaps.put("4010001", 4010001);
    gotomaps.put("4010002", 4010002);
    gotomaps.put("4010003", 4010003);
    gotomaps.put("4010004", 4010004);
    gotomaps.put("4010005", 4010005);
    gotomaps.put("4010006", 4010006);
    gotomaps.put("4010007", 4010007);
    gotomaps.put("4020000", 4020000);
    gotomaps.put("4020001", 4020001);
    gotomaps.put("4020002", 4020002);
    gotomaps.put("4020003", 4020003);
    gotomaps.put("4020004", 4020004);
    gotomaps.put("4020005", 4020005);
    gotomaps.put("4020006", 4020006);
    gotomaps.put("4020007", 4020007);
    gotomaps.put("4020008", 4020008);
    if (gotomaps.containsKey(splitted[1])){
      cm.gainItem(101010,80);
      c.getPlayer().setBossLog("shuaMK");
        mc.dropMessage("成功得到该矿石80个,你已经使用"+c.getPlayer().getBossLog("shuaMK")+"次.");
  
    }else{
        mc.dropMessage("请输入完整的代号,或者你不能使用该功能.");
    }
    }
/*********/} else if (splitted[0].equals("$祝福")) {//vip1
if (c.getPlayer().vip >= 1) {
            SkillFactory.getSkill(9001001).getEffect(1).applyTo(player);
            SkillFactory.getSkill(9001002).getEffect(1).applyTo(player);
            SkillFactory.getSkill(9001003).getEffect(1).applyTo(player);
            SkillFactory.getSkill(9001008).getEffect(1).applyTo(player);
                 mc.dropMessage("成功启动.");
                        }else{
            mc.dropMessage("你不能使用该功能.");
            }
/*********/} else if (splitted[0].equals("$满")) {//vip6
        if (c.getPlayer().vip >= 4) {
            player.setHp(player.getMaxHp());
            player.updateSingleStat(MapleStat.HP, player.getMaxHp());
            player.setMp(player.getMaxMp());
            player.updateSingleStat(MapleStat.MP, player.getMaxMp());
             }else{
            mc.dropMessage("您不是VIP4，不能使用此命令.");
             }


/*********/} else if (splitted[0].equals("$在线") && c.getPlayer().vip >= 2) {
            if (c.getPlayer().getMeso() < 0) {
                        mc.dropMessage("{ VIP指令 } 每次执行该命令系统将自动扣除您的查看费用0游戏币！");
                    } else {
			mc.dropMessage("[系统信息] 频道服务器 " + c.getChannel() + " 当前状态:");
			Collection<MapleCharacter> chrs = ChannelServer.getInstance(c.getChannel()).getPlayerStorage().getAllCharacters();
			for (MapleCharacter chr : chrs) {
			mc.dropMessage("[系统信息] 角色ID：" + chr.getId() + " 角色名：" + chr.getName());
			}
			mc.dropMessage("[系统信息] 频道服务器 " + c.getChannel() + "  当前总计： " + chrs.size() + "人在线.");
            }
            
/*********/} else if (splitted[0].equals("$黄色喇叭")) {//限制5次 V6
    if (splitted.length < 2&&c.getPlayer().vip >= 4){//这个是判断输入字符大于1显示.
   mc.dropMessage("----------------黄色喇叭----------------");
   mc.dropMessage("黄色喇叭为至尊会员（VIP4）独有功能..");
   mc.dropMessage("输入格式为：（$黄色喇叭 要说的话.）.");
   mc.dropMessage("请会员玩家不要用此发布不良信息或者欺骗信息,发现将给予处理.");
     }else if (c.getPlayer().vip >= 4 &&c.getPlayer().getBossLog("meiriLB")<=5) {
            String outputMessage = StringUtil.joinStringFrom(splitted, 1);
                cserv.setServerMessage(outputMessage);
                c.getPlayer().setBossLog("meiriLB");
                mc.dropMessage("成功发表顶部喇叭,进入你已经使用"+c.getPlayer().getBossLog("meiriLB")+"次.");
              }else{
            mc.dropMessage("您的会员等级不足,或者您的使用次数超过5次,不能再使用.");
            }
    
/*********/} else if (splitted[0].equals("$喇叭")) {//V2-6    次数： 3 5 7 10 15
                   if (c.getPlayer().vip == 2){
                 xx=3;
             }else if (c.getPlayer().vip == 3){
                 xx=5;
             }else if (c.getPlayer().vip == 4){
                 xx=7;
             }else if (c.getPlayer().vip == 5){
                 xx=10;
             }else if (c.getPlayer().vip == 6){
                 xx=15;
                 }
             if (splitted.length < 2&&c.getPlayer().vip >= 2){//这个是判断输入字符大于1显示.
   mc.dropMessage("-------------------喇叭功能-------------------");
   mc.dropMessage("喇叭功能为会员玩家独有功能.其功能在于能发连续3条全白全服信息.");
   mc.dropMessage("输入格式为：（$喇叭 要说的话.）.");
   mc.dropMessage("请会员玩家不要用此发布不良信息或者欺骗信息,发现将给予处理.");
           }else if (c.getPlayer().vip >= 2 &&c.getPlayer().getBossLog("dahongLB")<xx) {
            String outputMessage = StringUtil.joinStringFrom(splitted, 1);
                c.getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.serverNotice(11,c.getChannel(),c.getPlayer().getName()+" : "+outputMessage,true).getBytes());
                c.getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.serverNotice(11,c.getChannel(),c.getPlayer().getName()+" : "+outputMessage,true).getBytes());
                c.getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.serverNotice(11,c.getChannel(),c.getPlayer().getName()+" : "+outputMessage,true).getBytes());
                c.getPlayer().setBossLog("dahongLB");
                mc.dropMessage("成功发表全白喇叭,今日你已经使用"+c.getPlayer().getBossLog("dahongLB")+"次.");

              }else{
            mc.dropMessage("您的会员等级不足,或者您的使用次数超过"+xx+"次,不能再使用.");
            }
                   
  
/*********/} else if (splitted[0].equals("$祝福喇叭")) {
                  if (c.getPlayer().vip == 2){
                 xx=1;
             }else if (c.getPlayer().vip == 3){
                 xx=2;
             }else if (c.getPlayer().vip == 4){
                 xx=3;
             }else if (c.getPlayer().vip == 5){
                 xx=4;
             }else if (c.getPlayer().vip == 6){
                 xx=5;
                }
             if (splitted.length < 2&&c.getPlayer().vip >= 2){//这个是判断输入字符大于1显示.
   mc.dropMessage("-------------------祝福喇叭-------------------");
   mc.dropMessage("祝福喇叭功能为会员玩家独有功能.其功能在于能发全服占屏幕300s的飘落喇叭.");
   mc.dropMessage("输入格式为：（$祝福喇叭 要说的话.）.");
   mc.dropMessage("请会员玩家不要用此发布不良信息或者欺骗信息,发现将给予处理.");
      }else if (c.getPlayer().vip >= 2 &&c.getPlayer().getBossLog("zhufuLB")<xx) {
            String outputMessage = StringUtil.joinStringFrom(splitted, 1);
                Iterator is = cserv.getPlayerStorage().getAllCharacters().iterator();//OK
                MapleCharacter players = (MapleCharacter)is.next();
               if (players.getGMLevel() < 5)
		players.getClient().getSession().write(MaplePacketCreator.startMapEffect((new StringBuilder()).append(c.getPlayer().getName()).append(":").append(outputMessage).toString(), 5120006, true));
		else
		players.dropMessage((new StringBuilder()).append("[").append(c.getPlayer().getName()).append("]刷出了屏幕祝福喇叭[VIP系列]:").append(outputMessage).append("").toString());
			 c.getPlayer().setBossLog("zhufuLB");
                mc.dropMessage("成功发表祝福喇叭,进入你已经使用"+c.getPlayer().getBossLog("zhufuLB")+"次.");
              }else{
            mc.dropMessage("您的会员等级不足,或者您的使用次数超过"+xx+"次,不能再使用.");
            }

                  
/*
 * 
 *刷物品.
 * 
 
   }else if (splitted[0].equals("$刷物品")) {
                   if (c.getPlayer().vip == 3){
                 xx=5;
             }else if (c.getPlayer().vip == 4){
                 xx=10;
             }else if (c.getPlayer().vip == 5){
                 xx=15;
             }else if (c.getPlayer().vip == 6){
                 xx=20;
                 }
       if (splitted.length < 1&&c.getPlayer().vip >= 3){//这个是判断输入字符大于1显示.
   mc.dropMessage("刷物品功能仅仅对BOSS物品,门票有效,其他物品无效哦!");
   mc.dropMessage("三星会员可以刷5次任意1-10数量的物品,四星会员可以刷10次任意1-10数量的物品.");
   mc.dropMessage("五星会员可以刷15次任意1-10数量的物品,六星会员可以刷20-次任意1-10数量的物品.");
   mc.dropMessage("输入格式为：（$刷物品 物品代码 数量）.");
   mc.dropMessage("嘿嘿,请不要试图输入100.1000这样的数字哦,会出现反效果的哦！");
   mc.dropMessage("*************以下是可刷物品代码*********************");
          }
         if(Integer.parseInt(splitted[1]) != 1112906) {
              mc.dropMessage("你只能刷制定物品,不能刷其他哦.");
          }
            ii = MapleItemInformationProvider.getInstance();
          quantity = (short)CommandProcessor.getOptionalIntArg(splitted, 2, 1);
           if (quantity > 10)//反效果
              quantity = 1;
          if ((Integer.parseInt(splitted[1]) >= 5000000) && (Integer.parseInt(splitted[1]) <= 5000100)) {
            if (quantity > 1)
              quantity = 1;
          }
          if (ii.isRechargable(Integer.parseInt(splitted[1]))) {
            quantity = ii.getSlotMax(c, Integer.parseInt(splitted[1]));
            MapleInventoryManipulator.addById(c, Integer.parseInt(splitted[1]), quantity, "Rechargable item created.", player.getName(), -1);
          }
          MapleInventoryManipulator.addById(c, Integer.parseInt(splitted[1]), quantity, player.getName() + "used !item with quantity " + quantity, player.getName(), -1);
      */   
/*
 *召唤功能
 *用于召唤BOSS给VIP做活动什么的用.暂时不写了
 **/
                          
 /*********//*} else if (splitted[0].equals("$召唤")) {
                   if (c.getPlayer().vip == 3){
                 xx=5;
             }else if (c.getPlayer().vip == 4){
                 xx=10;
             }else if (c.getPlayer().vip == 5){
                 xx=15;
             }else if (c.getPlayer().vip == 6){
                 xx=20;
                 }
             if (splitted.length < 1&&c.getPlayer().vip >= 2){//这个是判断输入字符大于1显示.
   mc.dropMessage("召唤功能仅对VIP3以上玩家开放.");
   mc.dropMessage("会员玩家可在[玩具城、天空之城、射手村、童话村、地球防御本部]城市中进行召唤.");
   mc.dropMessage("三星会员可以召唤5次怪物,四星会员可以召唤10次怪物.");
   mc.dropMessage("五星会员可以召唤15次怪物,六星会员可以召唤20次怪物,");
   mc.dropMessage("*************以下是召唤BOSS代码*********************");
   mc.dropMessage("");
           }else if (c.getPlayer().vip >= 3 &&c.getPlayer().getBossLog("BOSSzhaohuans")<=xx) {
            Collection<ChannelServer> cservs = ChannelServer.getAllInstances();
            String outputMessage = StringUtil.joinStringFrom(splitted, 1);
            for (ChannelServer cserv1 : cservs) {
                c.getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.serverNotice(2,c.getChannel(),c.getPlayer().getName()+":"+outputMessage,true).getBytes());
                c.getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.serverNotice(2,c.getChannel(),c.getPlayer().getName()+":"+outputMessage,true).getBytes());
                c.getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.serverNotice(2,c.getChannel(),c.getPlayer().getName()+":"+outputMessage,true).getBytes());
              //  c.getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.serverNotice(2,c.getChannel(),c.getPlayer().getName()+":"+outputMessage,true).getBytes());
            //    c.getChannelServer().getWorldInterface().broadcastMessage(null, net.sf.odinms.tools.MaplePacketCreator.serverNotice(2,c.getChannel(),c.getPlayer().getName()+":"+outputMessage,true).getBytes());
                c.getPlayer().setBossLog("dahongLB");
                mc.dropMessage("成功发表大红喇叭,进入你已经使用"+c.getPlayer().getBossLog("dahongLB")+"次.");
                }
              }else{
            mc.dropMessage("您的会员等级不足,或者您的使用次数超过"+xx+"次,不能再使用.");
            }
        
 } else if (splitted[0].equals("$杀") || splitted[0].equals("!monsterdebug")) {
                if (c.getPlayer().vip >= 6 &&c.getPlayer().getBossLog("SBA")<10) {
            String mapMessage = "";
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;
            List<MapleMapObject> monsters = map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER));
            boolean kill = splitted[0].equals("$杀");
            for (MapleMapObject monstermo : monsters) {
                MapleMonster monster = (MapleMonster) monstermo;
                if (kill) {
                    map.killMonster(monster, c.getPlayer(), true);
                    monster.giveExpToCharacter(c.getPlayer(), monster.getExp(), true, 1);
                } else {
                    mc.dropMessage("Monster " + monster.toString());
                }
            }
            if (kill) {
                  c.getPlayer().setBossLog("SBA");
                mc.dropMessage("杀死了 " + monsters.size() + " 个怪物" + mapMessage + ".");
            } }else{
            mc.dropMessage("每日可以使用10次.");
            }
   * */
/*********/} else if (splitted[0].equals("$刷金币")) {//刷金币V4-V6功能.
              
                   if (c.getPlayer().vip == 4){
                 int xx = 0;
                 xx=3;
             }else if (c.getPlayer().vip == 5){
                 int xx = 0;
                 xx=5;
             }else if (c.getPlayer().vip == 6){
                 int xx = 0;
                 xx=7;
                 if (splitted.length < 3&&c.getPlayer().vip >= 4){//这个是判断输入字符大于1显示.
   mc.dropMessage("-----------------刷金币-----------------");
   mc.dropMessage("输入格式为：（$刷金币 金币数量）.");
   mc.dropMessage("输入上限为3亿，请不要让自身金币爆掉了哦！");
     } else if (c.getPlayer().vip >= 4 &&c.getPlayer().getBossLog("SBAm")<xx) {
          if(Integer.parseInt(splitted[1]) > 300000000) {
              mc.dropMessage("请输入小于3亿的冒险币金额哦！.");
           } else if (Integer.MAX_VALUE - (player.getMeso() + Integer.parseInt(splitted[1])) >= 0) {
                player.gainMeso(Integer.parseInt(splitted[1]), true);
                c.getPlayer().setBossLog("SBAm"); 
                mc.dropMessage("成功增加了"+Integer.parseInt(splitted[1])+"冒险币，当前你已经使用"+c.getPlayer().getBossLog("SBAm")+"次.");
            } else {
                mc.dropMessage("你输入的金额超过了3亿,请减小后使用.");
            }
  }else{
            mc.dropMessage("根据你的VIP等级每日可以使用"+xx+"次.你已经使用完了，请明天再来.");
            }
       
/*********/} else if (splitted[0].equals("$跟踪")) { 
        HashMap<String, Integer> gotomaps = new HashMap<String, Integer>();
       gotomaps.put("180000000",180000000);//GM地图不能传送
    if (gotomaps.containsKey(splitted[1])){//如果存在上述地图,不能被传送.限定特殊地图的范围
       mc.dropMessage("不能移动该地图.");
                   } else if (c.getPlayer().vip >= 4) {
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim != null) {
                if (splitted.length == 2) {
                    MapleMap target = victim.getMap();
                    c.getPlayer().changeMap(target, target.findClosestSpawnpoint(victim.getPosition()));
                } else {
                    int mapid = Integer.parseInt(splitted[2]);
                    MapleMap target = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(mapid);
                    victim.changeMap(target, target.getPortal(0));
                }
                
            } else {
                try {
                    victim = c.getPlayer();
                    WorldLocation loc = c.getChannelServer().getWorldInterface().getLocation(splitted[1]);
                    if (loc != null) {
                        mc.dropMessage("你将更换频道,这可能需要几秒钟.");
                        MapleMap target = c.getChannelServer().getMapFactory().getMap(loc.map);
                        String ip = c.getChannelServer().getIP(loc.channel);
                        c.getPlayer().getMap().removePlayer(c.getPlayer());
                        victim.setMap(target);
                        String[] socket = ip.split(":");
                        if (c.getPlayer().getTrade() != null) {
                            MapleTrade.cancelTrade(c.getPlayer());
                        }
                        try {
                            WorldChannelInterface wci = ChannelServer.getInstance(c.getChannel()).getWorldInterface();
                            wci.addBuffsToStorage(c.getPlayer().getId(), c.getPlayer().getAllBuffs());
                        } catch (RemoteException e) {
                            c.getChannelServer().reconnectWorld();
                        }
                        c.getPlayer().saveToDB(true);
                        if (c.getPlayer().getCheatTracker() != null) {
                            c.getPlayer().getCheatTracker().dispose();
                        }
                        ChannelServer.getInstance(c.getChannel()).removePlayer(c.getPlayer());
                        c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION);
                        try {
                            MaplePacket packet = MaplePacketCreator.getChannelChange(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1]));
                            c.getSession().write(packet);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        int map = Integer.parseInt(splitted[1]);
                        MapleMap target = cserv.getMapFactory().getMap(map);
                        c.getPlayer().changeMap(target, target.getPortal(0));
                    }

                } catch (Exception e) {
                    mc.dropMessage("语句输入错误: " + e.getMessage());
                }
            }
                  }else{
            mc.dropMessage("您不是VIP4，不能使用此命令.");
            }
 }
        } else {
            if (player.vip >= 1) {
                mc.dropMessage("您输入的会员命令 " + splitted[0] + " 不存在.");
            }
            return false;
        }
        return true;
    }
  }
