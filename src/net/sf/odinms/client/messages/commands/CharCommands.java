package net.sf.odinms.client.messages.commands;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

import java.util.List;
import net.sf.odinms.client.Equip;
import net.sf.odinms.client.IItem;
import net.sf.odinms.client.Item;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.client.MapleJob;
import net.sf.odinms.client.MaplePet;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.CommandProcessor;
import static net.sf.odinms.client.messages.CommandProcessor.getOptionalIntArg;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.scripting.npc.NPCScriptManager;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.server.MapleShopFactory;
import net.sf.odinms.server.life.MapleLifeFactory;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.life.MapleNPC;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.StringUtil;

public class CharCommands implements Command {

    @SuppressWarnings("static-access")
    @Override
    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception, IllegalCommandSyntaxException {
        MapleCharacter player = c.getPlayer();
      MapleCharacter victim;
    ChannelServer cserv = c.getChannelServer();
        if (splitted[0].equals("!maxall")) {
            player.setMaxHp(30000);
            player.setMaxMp(30000);
            player.setStr(Short.MAX_VALUE);
            player.setDex(Short.MAX_VALUE);
            player.setInt(Short.MAX_VALUE);
            player.setLuk(Short.MAX_VALUE);
            player.updateSingleStat(MapleStat.MAXHP, 30000);
            player.updateSingleStat(MapleStat.MAXMP, 30000);
            player.updateSingleStat(MapleStat.STR, Short.MAX_VALUE);
            player.updateSingleStat(MapleStat.DEX, Short.MAX_VALUE);
            player.updateSingleStat(MapleStat.INT, Short.MAX_VALUE);
            player.updateSingleStat(MapleStat.LUK, Short.MAX_VALUE);
        } else if (splitted[0].equals("!minall")) {
            player.setMaxHp(50);
            player.setMaxMp(5);
            player.setStr(4);
            player.setDex(4);
            player.setInt(4);
            player.setLuk(4);
            player.updateSingleStat(MapleStat.MAXHP, 50);
            player.updateSingleStat(MapleStat.MAXMP, 5);
            player.updateSingleStat(MapleStat.STR, 4);
            player.updateSingleStat(MapleStat.DEX, 4);
            player.updateSingleStat(MapleStat.INT, 4);
            player.updateSingleStat(MapleStat.LUK, 4);
        } else if (splitted[0].equals("!fullskill")) {
            c.getPlayer().maxAllSkills();
        } else if (splitted[0].equals("!maxhp")) {
            int stat = Integer.parseInt(splitted[1]);
            player.setMaxHp(stat);
            player.updateSingleStat(MapleStat.MAXHP, stat);
                } else if (splitted[0].equals("!hp")) {
            int stat = Integer.parseInt(splitted[1]);
            player.setHp(stat);
            player.updateSingleStat(MapleStat.HP, stat);
        } else if (splitted[0].equals("!mp")) {
            int stat = Integer.parseInt(splitted[1]);
            player.setMp(stat);
            player.updateSingleStat(MapleStat.MP, stat);
        } else if (splitted[0].equals("!str")) {
            int stat = Integer.parseInt(splitted[1]);
            player.setStr(stat);
            player.updateSingleStat(MapleStat.STR, stat);
        } else if (splitted[0].equals("!dex")) {
            int stat = Integer.parseInt(splitted[1]);
            player.setDex(stat);
            player.updateSingleStat(MapleStat.DEX, stat);
        } else if (splitted[0].equals("!int")) {
            int stat = Integer.parseInt(splitted[1]);
            player.setInt(stat);
            player.updateSingleStat(MapleStat.INT, stat);
        } else if (splitted[0].equals("!luk")) {
            int stat = Integer.parseInt(splitted[1]);
            player.setLuk(stat);
            player.updateSingleStat(MapleStat.LUK, stat);
            
        } else if (splitted[0].equals("!maxmp")) {
            int stat = Integer.parseInt(splitted[1]);
            player.setMaxMp(stat);
            player.updateSingleStat(MapleStat.MAXMP, stat);
          } else if (splitted[0].equals("!ap")) {
            int sp = Integer.parseInt(splitted[1]);
            if (sp + player.getRemainingSp() > Short.MAX_VALUE) {
                sp = Short.MAX_VALUE;
            }
            player.setRemainingSp(sp);
            player.updateSingleStat(MapleStat.AVAILABLESP, player.getRemainingSp());
        } else if (splitted[0].equals("!ap")) {
            int ap = Integer.parseInt(splitted[1]);
            if (ap + player.getRemainingAp() > Short.MAX_VALUE) {
                ap = Short.MAX_VALUE;
            }
            player.setRemainingAp(ap);
            player.updateSingleStat(MapleStat.AVAILABLEAP, player.getRemainingAp());
        } else if (splitted[0].equals("!job")) {
            int jobId = Integer.parseInt(splitted[1]);
            if (MapleJob.getById(jobId) != null) {
                player.changeJob(MapleJob.getById(jobId));
            }
     } else if (splitted[0].equals("!meso")) {
            if (Integer.MAX_VALUE - (player.getMeso() + Integer.parseInt(splitted[1])) >= 0) {
                player.gainMeso(Integer.parseInt(splitted[1]), true);
            } else {
                player.gainMeso(Integer.MAX_VALUE - player.getMeso(), true);
            }
     } else if (splitted[0].equals("!level")) {
            int quantity = Integer.parseInt(splitted[1]);
            c.getPlayer().setLevel(quantity);
            c.getPlayer().levelUp();
            int newexp = c.getPlayer().getExp();
            if (newexp < 0) {
                c.getPlayer().gainExp(-newexp, false, false);
            }
        } else if (splitted[0].equals("!whereami")) {
            int currentMap = player.getMapId();
            mc.dropMessage("你所在的地图代码为：" + currentMap + ".");
            }else if (splitted[0].equals("!全掉")) {
                ChannelServer cserv1 = c.getChannelServer();
                int level = 0;
                for (MapleCharacter mch : cserv1.getPlayerStorage().getAllCharacters()){
                    mch.getClient().getSession().close();
                    mch.getClient().disconnect();
                }

             
       } else if (splitted[0].equals("!刷新地图")) {
            boolean custMap = splitted.length >= 2;
            int mapid = custMap ? Integer.parseInt(splitted[1]) : player.getMapId();
            MapleMap map = custMap ? player.getClient().getChannelServer().getMapFactory().getMap(mapid) : player.getMap();
            if (player.getClient().getChannelServer().getMapFactory().destroyMap(mapid)) {
                MapleMap newMap = player.getClient().getChannelServer().getMapFactory().getMap(mapid);
                MaplePortal newPor = newMap.getPortal(0);
                LinkedHashSet<MapleCharacter> mcs = new LinkedHashSet<MapleCharacter>(map.getCharacters()); // do NOT remove, fixing ConcurrentModificationEx.
                outerLoop:
                for (MapleCharacter m : mcs) {
                    for (int x = 0; x < 5; x++) {
                        try {
                            m.changeMap(newMap, newPor);
                            continue outerLoop;
                        } catch (Throwable t) {
                        }
                    }
                    mc.dropMessage("Failed warping " + m.getName() + " to the new map. Skipping...");
                }
                mc.dropMessage("地图刷新完毕，如还出现NPC不见请使用此命令.");
                return;
            }
            mc.dropMessage("Unsuccessful reset!");
        } else if (splitted[0].equals("!equip")) {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            short quantity = (short) getOptionalIntArg(splitted, 2, 1);
            if (Integer.parseInt(splitted[1]) >= 5000000 && Integer.parseInt(splitted[1]) <= 5000100) {
                if (quantity > 1) {
                    quantity = 1;
                }
                int petId = MaplePet.createPet(Integer.parseInt(splitted[1]));
                MapleInventoryManipulator.addById(c, Integer.parseInt(splitted[1]), quantity, player.getName() + "used !item with quantity " + quantity, player.getName(), petId);
                return;
            } else if (ii.isRechargable(Integer.parseInt(splitted[1]))) {
                quantity = ii.getSlotMax(c, Integer.parseInt(splitted[1]));
                MapleInventoryManipulator.addById(c, Integer.parseInt(splitted[1]), quantity, "Rechargable item created.", player.getName(), -1);
                return;
            }
            MapleInventoryManipulator.addById(c, Integer.parseInt(splitted[1]), quantity, player.getName() + "used !item with quantity " + quantity, player.getName(), -1);
        } else if (splitted[0].equals("!item")) {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            short quantity = (short) getOptionalIntArg(splitted, 2, 1);
            if (Integer.parseInt(splitted[1]) >= 5000000 && Integer.parseInt(splitted[1]) <= 5000100) {
                if (quantity > 1) {
                    quantity = 1;
                }
                int petId = MaplePet.createPet(Integer.parseInt(splitted[1]));
                return;
            } else if (ii.isRechargable(Integer.parseInt(splitted[1]))) {
                quantity = ii.getSlotMax(c, Integer.parseInt(splitted[1]));
                return;
            }
            MapleInventoryManipulator.addById(c, Integer.parseInt(splitted[1]), quantity, player.getName() + "used !nonameitem with quantity " + quantity);
       } else if ((splitted[0].equals("!警告")) || (splitted[0].equals("!警告s"))) {
          String playerid = splitted[1];
          if ((splitted.length < 2) || (splitted.length > 2))
            throw new IllegalCommandSyntaxException(2);

          MapleCharacter target = null;
          Collection cservs = ChannelServer.getAllInstances();
          for (Iterator i$ = cservs.iterator(); i$.hasNext(); ) { ChannelServer cserver = (ChannelServer)i$.next();
            if (splitted[0].equals("!警告"))
              target = cserver.getPlayerStorage().getCharacterById(Integer.parseInt(playerid));
            else
              target = cserver.getPlayerStorage().getCharacterByName(splitted[1]);

            if (target != null)
              target.gainWarning(true, 1);
          }
        } else if (splitted[0].equals("!dropequip")) {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            int itemId = Integer.parseInt(splitted[1]);
            short quantity = (short) getOptionalIntArg(splitted, 2, 1);
            IItem toDrop;
            if (ii.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
                toDrop = ii.randomizeStats((Equip) ii.getEquipById(itemId));
            } else {
                toDrop = new Item(itemId, (byte) 0, quantity);
            }
            toDrop.log("Created by " + player.getName() + " using !drop. Quantity: " + quantity, false);
            toDrop.setOwner(player.getName());
            player.getMap().spawnItemDrop(player, player, toDrop, player.getPosition(), true, true);
        } else if (splitted[0].equals("!drop")) {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            int itemId = Integer.parseInt(splitted[1]);
            short quantity = (short) getOptionalIntArg(splitted, 2, 1);
            IItem toDrop;
            if (ii.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
                toDrop = ii.randomizeStats((Equip) ii.getEquipById(itemId));
            } else {
                toDrop = new Item(itemId, (byte) 0, quantity);
            }
            player.getMap().spawnItemDrop(player, player, toDrop, player.getPosition(), true, true);

        } else if (splitted[0].equals("!position")) {
            mc.dropMessage("你所在的坐标为： " + c.getPlayer().getPosition().x + " X轴、" + c.getPlayer().getPosition().y + " Y轴.");
        } else if (splitted[0].equals("!clear")) {
            if (splitted.length < 2) {
                mc.dropMessage("如果你想全部清空的话,请使用：!clear all");
            } else {
                String type = splitted[1];
                boolean pass = false;
                if (type.equals("equip") || type.equals("all")) {
                    if (!pass) {
                        pass = true;
                    }
                    for (int i = 0; i < 101; i++) {
                        IItem tempItem = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) i);
                        if (tempItem == null) {
                            continue;
                        }
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.EQUIP, (byte) i, tempItem.getQuantity(), false, true);
                    }
                }
                if (type.equals("use") || type.equals("all")) {
                    if (!pass) {
                        pass = true;
                    }
                    for (int i = 0; i < 101; i++) {
                        IItem tempItem = c.getPlayer().getInventory(MapleInventoryType.USE).getItem((byte) i);
                        if (tempItem == null) {
                            continue;
                        }
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, (byte) i, tempItem.getQuantity(), false, true);
                    }
                }
                if (type.equals("etc") || type.equals("all")) {
                    if (!pass) {
                        pass = true;
                    }
                    for (int i = 0; i < 101; i++) {
                        IItem tempItem = c.getPlayer().getInventory(MapleInventoryType.ETC).getItem((byte) i);
                        if (tempItem == null) {
                            continue;
                        }
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, (byte) i, tempItem.getQuantity(), false, true);
                    }
                }
                if (type.equals("setup") || type.equals("all")) {
                    if (!pass) {
                        pass = true;
                    }
                    for (int i = 0; i < 101; i++) {
                        IItem tempItem = c.getPlayer().getInventory(MapleInventoryType.SETUP).getItem((byte) i);
                        if (tempItem == null) {
                            continue;
                        }
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.SETUP, (byte) i, tempItem.getQuantity(), false, true);
                    }
                }
                if (type.equals("cash") || type.equals("all")) {
                    if (!pass) {
                        pass = true;
                    }
                    for (int i = 0; i < 101; i++) {
                        IItem tempItem = c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) i);
                        if (tempItem == null || tempItem.getUniqueId() != 0) {
                            continue;
                        }
                        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, (byte) i, tempItem.getQuantity(), false, true);
                    }
                }
                if (!pass) {
                    mc.dropMessage("!clear " + type + " 不存在");
                } else {
                    mc.dropMessage("成功清理.");
                }
            }
        } else if (splitted[0].equals("!godmode")) {
            player.setGodmode(!player.hasGodmode());
            mc.dropMessage("你现在为 " + (player.hasGodmode() ? "" : "非 ") + "无敌模式.");
 } else if (splitted[0].equals("!heal")) {
            player.setHp(player.getMaxHp());
            player.updateSingleStat(MapleStat.HP, player.getMaxHp());
            player.setMp(player.getMaxMp());
            player.updateSingleStat(MapleStat.MP, player.getMaxMp());
 } else if (splitted[0].equalsIgnoreCase("!healmap")) {
            for (MapleCharacter map : player.getMap().getCharacters()) {
                if (map != null) {
                    map.setHp(map.getCurrentMaxHp());
                    map.updateSingleStat(MapleStat.HP, map.getHp());
                    map.setMp(map.getCurrentMaxMp());
                    map.updateSingleStat(MapleStat.MP, map.getMp());
                }
            }
        } else if (splitted[0].equals("!killall") || splitted[0].equals("!monsterdebug")) {
            String mapMessage = "";
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;
            List<MapleMapObject> monsters = map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER));
            boolean kill = splitted[0].equals("!killall");
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
                mc.dropMessage("杀死了 " + monsters.size() + " 个怪物" + mapMessage + ".");
            }
        } else if (splitted[0].equals("!killalldrops")) {
            MapleMap map = c.getPlayer().getMap();
            map.killAllMonsters();
        } else if (splitted[0].equalsIgnoreCase("!giftnx")) {
            MapleCharacter victim3 = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim3 != null) {
                int amount;
                try {
                    amount = Integer.parseInt(splitted[2]);
                } catch (NumberFormatException fuckme) {
                    return;
                }
                int type = getOptionalIntArg(splitted, 3, 1);
                victim3.modifyCSPoints(type, amount);
                victim3.dropMessage(5, player.getName() + " 已经给你 " + amount + " 商城点卷.");
                mc.dropMessage("成功.");
            } else {
                mc.dropMessage("玩家未找到.");
            }
   } else if (splitted[0].equalsIgnoreCase("!fame")) {
            MapleCharacter victim4 = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim4 != null) {
                victim4.setFame(getOptionalIntArg(splitted, 2, 1));
                victim4.updateSingleStat(MapleStat.FAME, victim4.getFame());
            } else {
                mc.dropMessage("玩家未找到.");
            }
         } else if (splitted[0].equalsIgnoreCase("!unhide")) {
            MapleCharacter victim5 = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim5 != null) {
                victim5.dispelSkill(9101004);
                mc.dropMessage("成功.");
            } else {
                mc.dropMessage("玩家未找到.");
            }    
       } else if (splitted[0].equalsIgnoreCase("!unbuff")) {
            MapleCharacter victim5 = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim5 != null) {
                victim5.cancelAllBuffs();
                mc.dropMessage("成功.");
            } else {
                mc.dropMessage("玩家未找到.");
            }
        } else if (splitted[0].equalsIgnoreCase("!dc")) {
            MapleCharacter victim1 = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            victim1.getClient().disconnect();
            victim1.getClient().getSession().close();      
    } else if (splitted[0].equalsIgnoreCase("!charinfo")) {
            StringBuilder builder = new StringBuilder();
            MapleCharacter victim1 = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim1 == null) {
                return;
            }
            builder.append(MapleClient.getLogMessage(victim1, "")); // Could use null i think ?
            mc.dropMessage(builder.toString());

            builder = new StringBuilder();
            builder.append("坐标: X: ");
            builder.append(victim1.getPosition().x);
            builder.append(" Y: ");
            builder.append(victim1.getPosition().y);
            builder.append(" | RX0: ");
            builder.append(victim1.getPosition().x + 50);
            builder.append(" | RX1: ");
            builder.append(victim1.getPosition().x - 50);
            builder.append(" | FH: ");
            builder.append(victim1.getMap().getFootholds().findBelow(player.getPosition()).getId());
            mc.dropMessage(builder.toString());
            builder = new StringBuilder();
            builder.append("HP: ");
            builder.append(victim1.getHp());
            builder.append("/");
            builder.append(victim1.getCurrentMaxHp());
            builder.append(" | MP: ");
            builder.append(victim1.getMp());
            builder.append("/");
            builder.append(victim1.getCurrentMaxMp());
            builder.append(" | EXP: ");
            builder.append(victim1.getExp());
            builder.append(" | 在一个组队里: ");
            builder.append(victim1.getParty() != null);
            builder.append(" | 在一个商城里: ");
            builder.append(victim1.getTrade() != null);
            mc.dropMessage(builder.toString());
            builder = new StringBuilder();
            builder.append("远程地址: ");
            builder.append(victim1.getClient().getSession().getRemoteAddress());
            mc.dropMessage(builder.toString());
            victim1.getClient().dropDebugMessage(mc);
     } else if (splitted[0].equals("!add")) {
             mc.dropMessage("============GM刷装备或道具的命令=============");
             mc.dropMessage("!addgm        刷一套GM装备");
mc.dropMessage("!addboss      刷一套BOSS装备");
mc.dropMessage("!addqichong   刷骑宠:皮鞍子、野猪、银猪、赤羚龙");
mc.dropMessage("!adddaoju     刷道具:魔法石100、召回石100、火眼20、D片20");
mc.dropMessage("!addyaoshui   刷药水:超级药水200、万能药水50");
mc.dropMessage("!addbianshen  刷5种变身卡各5个");
mc.dropMessage("!addlaba      刷3种中文喇叭各10个");
mc.dropMessage("!addjuan      刷全部100%GM卷各1张");
mc.dropMessage("!adderhuan    刷耳环");
mc.dropMessage("!adddun       刷盾牌");
mc.dropMessage("!addwanju     刷玩具武器");
mc.dropMessage("!addyizi      刷椅子");
mc.dropMessage("!addwuqi1     刷全部高级战士武器");
mc.dropMessage("!addwuqi2     刷全部高级弓箭武器");
mc.dropMessage("!addwuqi3     刷全部高级法师武器");
mc.dropMessage("!addwuqi4     刷全部高级飞侠武器");
mc.dropMessage("!addtao     刷祝福");
}else if  (splitted[0].equals("!addgm")) {
            MapleInventoryManipulator.addById(c, 1002140, (short) 1, c.getPlayer().getName() + " got Wizet hat by using !addgm");//hat 维泽特帽
            MapleInventoryManipulator.addById(c, 1322013, (short) 1, c.getPlayer().getName() + " got Wizet weapon by using !addgm");//weapon 维泽特提包
            MapleInventoryManipulator.addById(c, 1042003, (short) 1, c.getPlayer().getName() + " got Wizet shirt by using !addgm");//shirt 维泽特西装
            MapleInventoryManipulator.addById(c, 1062007, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addgm");//pants 维泽特西裤
            MapleInventoryManipulator.addById(c, 1082230, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addgm");//白色发光手套

            } else if  (splitted[0].equals("!addboss")) {
            MapleInventoryManipulator.addById(c, 1122000, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addboss");//黑龙项链
            MapleInventoryManipulator.addById(c, 2041200, (short) 3, c.getPlayer().getName() + " got Wizet pants by using !addboss");//暗黑龙王石(给黑龙项链升级)
            MapleInventoryManipulator.addById(c, 1002357, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addboss");//扎昆头盔1
            MapleInventoryManipulator.addById(c, 1002430, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addboss");//扎昆头盔3

            } else if  (splitted[0].equals("!addqichong")) {
            MapleInventoryManipulator.addById(c, 1912000, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addqichong");//皮鞍子(!skill 1004 0 骑宠技能)
            MapleInventoryManipulator.addById(c, 1902000, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addqichong");//野猪
            MapleInventoryManipulator.addById(c, 1902001, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addqichong");//银猪
            MapleInventoryManipulator.addById(c, 1902002, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addqichong");//赤羚龙(尾数0-2是3种坐骑)

            } else if  (splitted[0].equals("!adddaoju")) {
            MapleInventoryManipulator.addById(c, 4006000, (short) 100, c.getPlayer().getName() + " got Wizet pants by using !adddaoju");//魔法石
            MapleInventoryManipulator.addById(c, 4006001, (short) 100, c.getPlayer().getName() + " got Wizet pants by using !adddaoju");//召回石
            MapleInventoryManipulator.addById(c, 4001017, (short) 20, c.getPlayer().getName() + " got Wizet pants by using !adddaoju");//火眼(召唤扎坤)
            MapleInventoryManipulator.addById(c, 4031179, (short) 20, c.getPlayer().getName() + " got Wizet pants by using !adddaoju");//D片(召唤闹钟)

            } else if  (splitted[0].equals("!addyaoshui")) {
            MapleInventoryManipulator.addById(c, 2000005, (short) 200, c.getPlayer().getName() + " got Wizet pants by using !addyaoshui");//超级药水(血蓝全满)
            MapleInventoryManipulator.addById(c, 2050004, (short) 50, c.getPlayer().getName() + " got Wizet pants by using !addyaoshui");//万能药水(恢复异常状态)

            } else if  (splitted[0].equals("!addbianshen")) {
            MapleInventoryManipulator.addById(c, 5300000, (short) 5, c.getPlayer().getName() + " got Wizet pants by using !addlaba");//蘑菇的雕像
            MapleInventoryManipulator.addById(c, 5300001, (short) 5, c.getPlayer().getName() + " got Wizet pants by using !addlaba");//漂漂猪的雕像
            MapleInventoryManipulator.addById(c, 5300002, (short) 5, c.getPlayer().getName() + " got Wizet pants by using !addlaba");//白外星人的雕像
            MapleInventoryManipulator.addById(c, 5300003, (short) 5, c.getPlayer().getName() + " got Wizet pants by using !addlaba");//龙族变身秘药
            MapleInventoryManipulator.addById(c, 5300005, (short) 5, c.getPlayer().getName() + " got Wizet pants by using !addlaba");//提干变身药

            } else if  (splitted[0].equals("!addlaba")) {
            MapleInventoryManipulator.addById(c, 5390000, (short) 10, c.getPlayer().getName() + " got Wizet pants by using !addlaba");//炽热情景喇叭
            MapleInventoryManipulator.addById(c, 5390001, (short) 10, c.getPlayer().getName() + " got Wizet pants by using !addlaba");//绚烂情景喇叭
            MapleInventoryManipulator.addById(c, 5390002, (short) 10, c.getPlayer().getName() + " got Wizet pants by using !addlaba");//爱心情景喇叭

            } else if  (splitted[0].equals("!adderhuan")) {
            MapleInventoryManipulator.addById(c, 1032042, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adderhuan");//冒险岛耳环
            MapleInventoryManipulator.addById(c, 1032030, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adderhuan");//勇气耳环
            MapleInventoryManipulator.addById(c, 1032031, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adderhuan");//坚毅耳环
            MapleInventoryManipulator.addById(c, 1032053, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adderhuan");//四叶草耳环
            MapleInventoryManipulator.addById(c, 1032038, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adderhuan");//雪花耳钉
            MapleInventoryManipulator.addById(c, 1032029, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adderhuan");//925银耳环

            } else if  (splitted[0].equals("!adddun")) {
            MapleInventoryManipulator.addById(c, 1092049, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adddun");//热情剑盾
            MapleInventoryManipulator.addById(c, 1092050, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adddun");//冷艳剑盾
            MapleInventoryManipulator.addById(c, 1092047, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adddun");//冒险岛飞侠盾牌
            MapleInventoryManipulator.addById(c, 1092018, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adddun");//飞侠盾牌(后2位尾数18-20都是)
            MapleInventoryManipulator.addById(c, 1092036, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adddun");//绿色臂盾
            MapleInventoryManipulator.addById(c, 1092037, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adddun");//紫色臂盾
            MapleInventoryManipulator.addById(c, 1092038, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adddun");//蓝色臂盾
            MapleInventoryManipulator.addById(c, 1092033, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adddun");//四叶草盾牌
            MapleInventoryManipulator.addById(c, 1092044, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adddun");//爱心盾牌
            MapleInventoryManipulator.addById(c, 1092031, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adddun");//七星瓢虫盾牌

            } else if  (splitted[0].equals("!addwanju")) {
            MapleInventoryManipulator.addById(c, 1302063, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwanju");//燃烧的火焰刀
            MapleInventoryManipulator.addById(c, 1402044, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwanju");//南瓜灯笼
            MapleInventoryManipulator.addById(c, 1422036, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwanju");//玩具匠人的锤子(有特效)
            MapleInventoryManipulator.addById(c, 1382016, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwanju");//香菇
            MapleInventoryManipulator.addById(c, 1442018, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwanju");//冻冻鱼
            MapleInventoryManipulator.addById(c, 1432039, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwanju");//钓鱼竿
            MapleInventoryManipulator.addById(c, 1442023, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwanju");//黑拖把
            MapleInventoryManipulator.addById(c, 1302013, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwanju");//红鞭子
            MapleInventoryManipulator.addById(c, 1432009, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwanju");//木精灵枪
            MapleInventoryManipulator.addById(c, 1302058, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwanju");//冒险岛伞
            MapleInventoryManipulator.addById(c, 1302066, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwanju");//枫叶庆典旗
            MapleInventoryManipulator.addById(c, 1302049, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwanju");//光线鞭子
            MapleInventoryManipulator.addById(c, 1322051, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwanju");//七夕
            MapleInventoryManipulator.addById(c, 1372017, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwanju");//领路灯
            MapleInventoryManipulator.addById(c, 1322026, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwanju");//彩虹游泳圈
            MapleInventoryManipulator.addById(c, 1332020, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwanju");//太极扇
            MapleInventoryManipulator.addById(c, 1332053, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwanju");//野外烧烤串
            MapleInventoryManipulator.addById(c, 1332054, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwanju");//闪电飞刀
            MapleInventoryManipulator.addById(c, 1402017, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwanju");//船长佩剑
            MapleInventoryManipulator.addById(c, 1402029, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwanju");//鬼刺狼牙棒
            MapleInventoryManipulator.addById(c, 1302080, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwanju");//闪光彩灯鞭
            MapleInventoryManipulator.addById(c, 1442046, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwanju");//超级雪板
            MapleInventoryManipulator.addById(c, 1442061, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwanju");//仙人掌之矛
            MapleInventoryManipulator.addById(c, 1432046, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwanju");//圣诞枪
            MapleInventoryManipulator.addById(c, 1442047, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwanju");//玫瑰(47-50)

            } else if  (splitted[0].equals("!adddnew")) {
            MapleInventoryManipulator.addById(c, 1702165, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adddnew");//同班男生
            MapleInventoryManipulator.addById(c, 1702169, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adddnew");//同班女生
            MapleInventoryManipulator.addById(c, 1702174, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adddnew");//蝴蝶扇
            MapleInventoryManipulator.addById(c, 1702155, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adddnew");//星星
            MapleInventoryManipulator.addById(c, 1702114, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adddnew");//荷叶青蛙
            MapleInventoryManipulator.addById(c, 1702140, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adddnew");//花
            MapleInventoryManipulator.addById(c, 1702081, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adddnew");//星球大战紫色的光剑
            MapleInventoryManipulator.addById(c, 1702151, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adddnew");//绿色的杖
            MapleInventoryManipulator.addById(c, 1702025, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adddnew");//蓝色的竖琴(弓)
            MapleInventoryManipulator.addById(c, 1702161, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adddnew");//狗(拳套)
            MapleInventoryManipulator.addById(c, 1102142, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adddnew");//火焰披风
            MapleInventoryManipulator.addById(c, 1102148, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adddnew");//熔岩披风
            MapleInventoryManipulator.addById(c, 1102149, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adddnew");//冰凌披风
            MapleInventoryManipulator.addById(c, 1082229, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adddnew");//飘飘手套
            MapleInventoryManipulator.addById(c, 1102159, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !adddnew");//猴子气球

            } else if  (splitted[0].equals("!addwuqi1")) {
            MapleInventoryManipulator.addById(c, 1302056, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwuqi1");//一刀两段(单手剑)
            MapleInventoryManipulator.addById(c, 1402037, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwuqi1");//龙背刃
            MapleInventoryManipulator.addById(c, 1402005, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwuqi1");//斩魔刀
            MapleInventoryManipulator.addById(c, 1402035, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwuqi1");//斩天刀
            MapleInventoryManipulator.addById(c, 1312015, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwuqi1");//战魂之斧
            MapleInventoryManipulator.addById(c, 1312031, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwuqi1");//狂龙怒斩斧
            MapleInventoryManipulator.addById(c, 1412010, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwuqi1");//项羽之斧
            MapleInventoryManipulator.addById(c, 1322052, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwuqi1");//狂龙地锤
            MapleInventoryManipulator.addById(c, 1432011, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwuqi1");//寒冰破魔枪
            MapleInventoryManipulator.addById(c, 1432030, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwuqi1");//红莲落神枪
            MapleInventoryManipulator.addById(c, 1432038, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwuqi1");//盘龙七冲枪
            MapleInventoryManipulator.addById(c, 1442045, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwuqi1");//血龙神斧

            } else if  (splitted[0].equals("!addwuqi2")) {
            MapleInventoryManipulator.addById(c, 1452044, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwuqi2");//金龙震翅弓
            MapleInventoryManipulator.addById(c, 1462039, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwuqi2");//黄金飞龙弩
            MapleInventoryManipulator.addById(c, 1452019, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwuqi2");//天鹰弓白
            MapleInventoryManipulator.addById(c, 1462015, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwuqi2");//光圣鹞弩白
            MapleInventoryManipulator.addById(c, 1452056, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwuqi2");//鸟弓
            MapleInventoryManipulator.addById(c, 1462049, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwuqi2");//鸟弩

            } else if  (splitted[0].equals("!addwuqi3")) {
            MapleInventoryManipulator.addById(c, 1372031, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwuqi3");//圣贤短杖
            MapleInventoryManipulator.addById(c, 1382037, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwuqi3");//偃月之杖
            MapleInventoryManipulator.addById(c, 1382035, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwuqi3");//冰肌玲珑杖
            MapleInventoryManipulator.addById(c, 1382036, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwuqi3");//黑精灵王杖

            } else if (splitted[0].equals("!addwuqi4")) {
            MapleInventoryManipulator.addById(c, 1332050, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwuqi4");//半月龙鳞裂
            MapleInventoryManipulator.addById(c, 1472051, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwuqi4");//寒木升龙拳
            MapleInventoryManipulator.addById(c, 2070016, (short) 4000, c.getPlayer().getName() + " got Wizet pants by using !addwuqi4");//水晶齿轮5组 //(2070006 齿轮镖，2070007 月牙镖)
            MapleInventoryManipulator.addById(c, 1472067, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addwuqi4");//鸟拳

            } else if (splitted[0].equals("!addjuan")) {
            MapleInventoryManipulator.addById(c, 2340000, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addjuan");//祝福卷轴
            MapleInventoryManipulator.addById(c, 2043303, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addjuan");//短剑攻击诅咒卷轴
            MapleInventoryManipulator.addById(c, 2040303, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addjuan");//耳环智力诅咒卷轴
            MapleInventoryManipulator.addById(c, 2040506, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addjuan");//全身铠甲敏捷诅咒卷轴
            MapleInventoryManipulator.addById(c, 2040710, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addjuan");//鞋子跳跃诅咒卷轴
            MapleInventoryManipulator.addById(c, 2040807, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addjuan");//手套攻击诅咒卷轴
            MapleInventoryManipulator.addById(c, 2043003, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addjuan");//单手剑攻击诅咒卷轴
            MapleInventoryManipulator.addById(c, 2043103, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addjuan");//单手斧攻击诅咒卷轴
            MapleInventoryManipulator.addById(c, 2043203, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addjuan");//单手钝器攻击诅咒卷轴
            MapleInventoryManipulator.addById(c, 2043703, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addjuan");//短杖魔力诅咒卷轴
            MapleInventoryManipulator.addById(c, 2043803, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addjuan");//长杖魔力诅咒卷轴
            MapleInventoryManipulator.addById(c, 2044003, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addjuan");//双手剑攻击诅咒卷轴
            MapleInventoryManipulator.addById(c, 2044103, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addjuan");//双手斧攻击诅咒卷轴
            MapleInventoryManipulator.addById(c, 2044303, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addjuan");//枪攻击诅咒卷轴
            MapleInventoryManipulator.addById(c, 2044403, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addjuan");//矛攻击诅咒卷轴
            MapleInventoryManipulator.addById(c, 2044503, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addjuan");//弓攻击诅咒卷轴
            MapleInventoryManipulator.addById(c, 2044603, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addjuan");//弩攻击诅咒卷轴
            MapleInventoryManipulator.addById(c, 2044703, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addjuan");//拳套攻击诅咒卷轴

            } else if (splitted[0].equals("!addyizi")) {
            MapleInventoryManipulator.addById(c, 3010000, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addyizi");//休闲椅
            MapleInventoryManipulator.addById(c, 3010001, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addyizi");//蓝色木椅
            MapleInventoryManipulator.addById(c, 3010003, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addyizi");//红色时尚转椅
            MapleInventoryManipulator.addById(c, 3010007, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addyizi");//粉色海狗靠垫
            MapleInventoryManipulator.addById(c, 3010008, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addyizi");//藍色海狗靠垫
            MapleInventoryManipulator.addById(c, 3010010, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addyizi");//白色海狗靠垫
            MapleInventoryManipulator.addById(c, 3010012, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addyizi");//蓝色高靠背椅
            MapleInventoryManipulator.addById(c, 3010014, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addyizi");//月亮弯弯
            MapleInventoryManipulator.addById(c, 3011000, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addyizi");//钓鱼椅
            MapleInventoryManipulator.addById(c, 3010009, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addyizi");//粉色爱心马桶

             } else if (splitted[0].equals("!addtao")) {
            MapleInventoryManipulator.addById(c, 5120000, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addtao22");//下雪了
            MapleInventoryManipulator.addById(c, 5120001, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addtao22");//浪漫樱花
            MapleInventoryManipulator.addById(c, 5120002, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addtao22");//泡泡飞舞
            MapleInventoryManipulator.addById(c, 5120003, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addtao22");//雪绒花
            MapleInventoryManipulator.addById(c, 5120004, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addtao22");//飘礼物
            MapleInventoryManipulator.addById(c, 5120005, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addtao22");//甜心
            MapleInventoryManipulator.addById(c, 5120006, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addtao22");//花瓣雨
            MapleInventoryManipulator.addById(c, 5120007, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addtao22");//棒棒糖
            MapleInventoryManipulator.addById(c, 5120008, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addtao22");//飘枫叶
            MapleInventoryManipulator.addById(c, 5120009, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addtao22");//鞭炮
            MapleInventoryManipulator.addById(c, 5120010, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addtao22");//漂可乐
            MapleInventoryManipulator.addById(c, 5120011, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addtao22");//飘恐惧
            MapleInventoryManipulator.addById(c, 5120012, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addtao22");//圣诞袜子
            MapleInventoryManipulator.addById(c, 5120015, (short) 1, c.getPlayer().getName() + " got Wizet pants by using !addtao22");//放鞭炮
            } else if (splitted[0].equalsIgnoreCase("!shop")) {
            if (splitted.length != 2) {
                return;
            }
            int shopid;
            try {
                shopid = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException asd) {
                return;
            }
            MapleShopFactory.getInstance().getShop(shopid).sendShop(c);
        } else if (splitted[0].equalsIgnoreCase("!opennpc")) {
            if (splitted.length != 2) {
                return;
            }
            int npcid;
            try {
                npcid = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException asd) {
                return;
            }
            MapleNPC npc = MapleLifeFactory.getNPC(npcid);
            if (npc != null && !npc.getName().equalsIgnoreCase("MISSINGNO")) {
                NPCScriptManager.getInstance().start(c, npcid);
            } else {
                mc.dropMessage("NPC未找到.");
            }
    } else if (splitted[0].equalsIgnoreCase("!clock")) {
            player.getMap().broadcastMessage(MaplePacketCreator.getClock(getOptionalIntArg(splitted, 1, 60)));
     } else if (splitted[0].equalsIgnoreCase("!kill")) {
            MapleCharacter victim1 = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim1 != null) {
                victim1.setHp(0);
                victim1.setMp(0);
                victim1.updateSingleStat(MapleStat.HP, 0);
                victim1.updateSingleStat(MapleStat.MP, 0);
            } else {
                mc.dropMessage("玩家未找到!");
            }       
           } else if (splitted[0].equalsIgnoreCase("!jobperson")) {
            MapleCharacter victim1 = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            int job;
            try {
                job = Integer.parseInt(splitted[2]);
            } catch (NumberFormatException blackness) {
                return;
            }
            if (victim1 != null) {
                victim1.setJob(job);
            } else {
                mc.dropMessage("玩家未找到!");
            }         
        } else if (splitted[0].equalsIgnoreCase("!speak")) {
            MapleCharacter victim1 = cserv.getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim1 != null) {
                String text = StringUtil.joinStringFrom(splitted, 2);
                victim1.getMap().broadcastMessage(MaplePacketCreator.getChatText(victim1.getId(), text, false, 0));
            } else {
                mc.dropMessage("玩家未找到.");
            }
  } else if (splitted[0].equalsIgnoreCase("!help")) {
                mc.dropMessage("==============GM命令帮助===============");
                mc.dropMessage("!char  --------------------- GM个人命令");
                mc.dropMessage("!GMcoms --------------------- 管理类命令");
                mc.dropMessage("!setting ------------------- 服务器命令");
                mc.dropMessage("!prtycom ------------------- 活动类命令");
                mc.dropMessage("=======================================");
                mc.dropMessage("!GMcom --------------------- 见习GM命令");
                mc.dropMessage("=======================================");
  } else if (splitted[0].equalsIgnoreCase("!char")) {
                mc.dropMessage("==============这里是GM个人命令列表=================");
                mc.dropMessage("!equip <装备代码> --------------------- [刷装备到背包上]");
                mc.dropMessage("!item <物品代码> <数量> --------------- [刷物品到背包上]");
                mc.dropMessage("!dropequip <装备代码> ----------------- [丢装备到地上]");
                mc.dropMessage("!drop <物品代码> <数量> --------------- [丢物品到地上]");
                mc.dropMessage("!maxall  ----------------------------- [属性最大化]");
                mc.dropMessage("!minall  ----------------------------- [属性初始化]");
                mc.dropMessage("!maxhp <数值>  ----------------------- [设置最大HP]");
                mc.dropMessage("!maxmp <数值>  ----------------------- [设置最大MP]");
                mc.dropMessage("!hp <数值>  -------------------------- [设置血量]");
                mc.dropMessage("!mp <数值>  -------------------------- [设置魔量]");
                mc.dropMessage("!add     ----------------------------- [快速刷装备命令]");
                mc.dropMessage("!str <数值>  ------------------------- [设置自己的力量值]");
                mc.dropMessage("!dex <数值>  ------------------------- [设置自己的敏捷值]");
                mc.dropMessage("!int <数值>  ------------------------- [设置自己的智力值]");
                mc.dropMessage("!luk <数值>  ------------------------- [设置自己的运气值]");
                mc.dropMessage("!sp <数值>   ------------------------- [设置自己的技能点]");
                mc.dropMessage("!ap <数值>   ------------------------- [设置自己的能力点]");
                mc.dropMessage("!job <职业代码> ---------------------- [转变职业,管理员代码：900]");
                mc.dropMessage("!meso <数值> ------------------------- [增加自己的金币]");
                mc.dropMessage("!level <等级> ------------------------ [设置自己的等级]");
                mc.dropMessage("!whereami  --------------------------- [查看自己的地图ID]");
                mc.dropMessage("!position  --------------------------- [查看自己的坐标]");
                mc.dropMessage("!clear <equip|use|etc|setup|cash|all>  [清空背包,如清空全部:!clear all]");
                mc.dropMessage("!godmode ----------------------------- [无敌模式,再按一次关闭]");
                mc.dropMessage("!heal -------------------------------- [满血满魔,信春哥原地复活]");
    } else if (splitted[0].equalsIgnoreCase("!GMcoms")) {  
                mc.dropMessage("==============这里是GM管理类令列表=================");
                mc.dropMessage("!shop <代码> ------------------------- [打开指定商店]");
                mc.dropMessage("!opennpc <代码> ---------------------- [打开指定NPC]");
                mc.dropMessage("!临时npc <代码> ---------------------- [安放一个临时NPC，服务器重启消失.]");
                mc.dropMessage("!永久npc <代码> ---------------------- [安放一个永久NPC，服务器重启不消失.]");
                mc.dropMessage("!notice <p|l|nv|v|b> <要说的内容> ---- [公告命令,<>内为可选参数,自己试下效果]");
                mc.dropMessage("!me <要说的内容> --------------------- [以自己的名字作为公告]");
                mc.dropMessage("!gmtalk <要说的内容> ----------------- [全服GM说话]");
                mc.dropMessage("!servermessage <要说的内容> ---------- [设置顶部黄色滚动内容]");
                mc.dropMessage("===================================================");
                mc.dropMessage("!clock <数值> ------------------------ [在当前地图添加一个计时器]");
                mc.dropMessage("!warp <玩家名字> --------------------- [将自己传送到指定名字的玩家身边]");
                mc.dropMessage("!warpid	<玩家ID> --------------------- [将自己传送到指定ID的玩家身边]");
                mc.dropMessage("!!warpallhere ------------------------ [将全服人拉到身边]");
                mc.dropMessage("!warpall <地图ID> -------------------- [将当前地图所有玩家传送到指定地图ID的地图]");
                mc.dropMessage("!warphere <玩家名字> ----------------- [将指定名字的玩家传送到自己身边]");
                mc.dropMessage("!传送 <地图代码> <洞口> -------------- [传送地图,洞口可以不填写]");
                mc.dropMessage("===================================================");
                mc.dropMessage("!刷新地图 ---------------------------- [对所在地图进行刷新]");
                mc.dropMessage("!警告 <玩家ID> ----------------------- [给玩家一次警告,3次自动封号]");
                mc.dropMessage("!警告s <玩家名字> -------------------- [给玩家一次警告,3次自动封号]");
                mc.dropMessage("!封号 <玩家ID> <封号原因>-------------- [使用数字ID封号]");
                mc.dropMessage("!封号2 <角色名字> <封号原因>----------- [使用角色名字封号]");
                mc.dropMessage("!全掉 -------------------------------- [让全部玩家掉线]");
                mc.dropMessage("!全部过来 ---------------------------- [把全服务器的玩家拉过来]");
                mc.dropMessage("!解封 <人物名> ----------------------- [解封帐号]");
                mc.dropMessage("!online ----------------------------- [查看在线玩家]");
                mc.dropMessage("!onlinegm --------------------------- [查看在线GM]");
                mc.dropMessage("!connected -------------------------- [查看服务器连接状态]");

} else if (splitted[0].equalsIgnoreCase("!setting")) {  
                mc.dropMessage("==============这里是GM服务器设置列表=================");
                mc.dropMessage("!ratesee ---------------------------- [查看当前游戏系统配置]");
                mc.dropMessage("!rate ------------------------------- [活动使用，临时改变服务器经验.金钱配置]");
                mc.dropMessage("!reloadguilds ----------------------- [重新加载家族数据]");
                mc.dropMessage("!reloadportals ---------------------- [重新加载门的脚本]");
                mc.dropMessage("!reloadreactors --------------------- [重新加载反应脚本]");
                mc.dropMessage("!reloadevents ----------------------- [重新加载event脚本]");
} else if (splitted[0].equalsIgnoreCase("!prtycom")) {  
                mc.dropMessage("==============这里是GM活动命令列表=================");
                mc.dropMessage("!givemap <item|meso|exp> <数值> ----- [给当前地图所有玩家道具或金币或经验.如给当前地图所有玩空祝福卷 !givemap item 2340000 给游戏币100W用 !givemap meso 1000000]");
                mc.dropMessage("!healmap ---------------------------- [当前地图所有人满血满蓝，死亡状态可复活.]");
                mc.dropMessage("!giftnx <玩家名字> <数量> ------------ [给玩家点卷]");
                mc.dropMessage("!fame <玩家名字> <数量> -------------- [设置玩家的人气]");
                mc.dropMessage("!unhide <玩家名字> ------------------- [取消指定玩家的隐身辅助技能]");
                mc.dropMessage("!unbuff <玩家名字> ------------------- [取消指定玩家的全部BUFF辅助技能]");
                mc.dropMessage("!dc <玩家名字> ----------------------- [把指定玩家踢下线]");
                mc.dropMessage("!charinfo <玩家名字> ----------------- [读取指定玩家信息]");
                mc.dropMessage("!kill <玩家名字> --------------------- [杀死指定玩家]");
                mc.dropMessage("!jobperson <玩家名字> <职业ID> ------- [给指定玩家换职业]");
                mc.dropMessage("!speak <玩家名字>  ------------------- [给指定玩家说话背景]");
                mc.dropMessage("===================================================");
                mc.dropMessage("!killall ---------------------------- [杀死所有该地图怪物]");
                mc.dropMessage("!killalldrops ----------------------- [杀死地图怪物，并且不掉落东西.]");
                mc.dropMessage("!monsterdebug ----------------------- [得到该地图怪物信息]");
                mc.dropMessage("!spawn <怪物代码> <数量>-------------- [刷指定怪,数量不写时默认为1]");
                mc.dropMessage("!攻城活动 ---------------------------- [召唤BOSS,可做活动使用]");
                mc.dropMessage("!bossname --------------------------- [查看召唤BOSS简便命令]");
   } else if (splitted[0].equalsIgnoreCase("!bossname")) {  
                mc.dropMessage("===================================================");
                mc.dropMessage("!papulatus -------------------------- [刷闹钟]");
                mc.dropMessage("!jrbalrog --------------------------- [刷蝙蝠怪]");
                mc.dropMessage("!balrog ----------------------------- [刷蝙蝠魔]");
                mc.dropMessage("!bossfamily ------------------------- [刷御姐&老板&日本BOSS]");
                mc.dropMessage("!mushmom ---------------------------- [刷蘑菇王]");
                mc.dropMessage("!zombiemushmom ---------------------- [僵尸蘑菇王]");
                mc.dropMessage("!bluemushmom ------------------------ [蓝蘑菇王]");
                mc.dropMessage("!shark ------------------------------ [刷鲨鱼]");
                mc.dropMessage("!pianus ----------------------------- [刷鱼王 右]");
                mc.dropMessage("!zakum ------------------------------ [刷整只扎昆]");
                mc.dropMessage("!horntail --------------------------- [刷整只黑龙]");
      } else if (splitted[0].equalsIgnoreCase("!GMcom")) {            
                mc.dropMessage("==================见习GM命令=====================");
                mc.dropMessage("!刷新地图 ---------------------------- [对所在地图进行刷新]");
                mc.dropMessage("!警告 <玩家ID> ----------------------- [给玩家一次警告,3次自动封号]");
                mc.dropMessage("!警告s <玩家名字> -------------------- [给玩家一次警告,3次自动封号]");
                mc.dropMessage("!全部过来 ---------------------------- [把全服务器的玩家拉过来]");
                mc.dropMessage("!封号 <玩家ID> <封号原因>-------------- [使用数字ID封号]");
                mc.dropMessage("!封号2 <角色名字> <封号原因>----------- [使用角色名字封号]");
                mc.dropMessage("!online ----------------------------- [查看在线玩家]");
                mc.dropMessage("!unhide <玩家名字> ------------------- [取消指定玩家的隐身辅助技能]");
                mc.dropMessage("!unbuff <玩家名字> ------------------- [取消指定玩家的全部BUFF辅助技能]");
                mc.dropMessage("!notice <p|l|nv|v|b> <要说的内容> ---- [公告命令,<>内为可选参数,自己试下效果]");
                mc.dropMessage("!job <职业代码> ---------------------- [转变职业,管理员代码：900]");
                mc.dropMessage("!godmode ----------------------------- [无敌模式,再按一次关闭]");
                mc.dropMessage("!heal -------------------------------- [满血满魔,信春哥原地复活]");
     } else {
                    Iterator i$;
                    MapleCharacter player1;
                    if (splitted[0].equalsIgnoreCase("!givemap")) {
                      LinkedHashSet cmc = new LinkedHashSet(c.getPlayer().getMap().getCharacters());
                      String type = splitted[1];
                      if (type.equalsIgnoreCase("item")) {
                        int itemid = Integer.parseInt(splitted[2]);
                        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                        short quantity = (short)CommandProcessor.getOptionalIntArg(splitted, 3, 1);
                        boolean pet = (itemid >= 5000000) && (itemid <= 5000100);
                        for (i$ = cmc.iterator(); i$.hasNext(); ) { player1 = (MapleCharacter)i$.next();
                          if (pet) {
                            if (quantity > 1)
                              quantity = 1;

                            int petId = MaplePet.createPet(itemid);
                            MapleInventoryManipulator.addById(player1.getClient(), itemid, quantity, "from !givemap", c.getPlayer().getName(), petId);
                            return; }
                          if (ii.isRechargable(itemid)) {
                            quantity = ii.getSlotMax(c, itemid);
                            MapleInventoryManipulator.addById(player1.getClient(), itemid, quantity, "Rechargable item created.", c.getPlayer().getName(), -1);
                            return;
                          }
                          MapleInventoryManipulator.addById(player1.getClient(), itemid, quantity, player1.getName() + "got from !givemap with quantity " + quantity, c.getPlayer().getName(), -1); }
                      } else {
                        int amt;
                        MapleCharacter player2;
                        if (type.equalsIgnoreCase("meso")) {
                          amt = Integer.parseInt(splitted[2]);

                          for (i$ = cmc.iterator(); i$.hasNext(); ) { 
                              player2 = (MapleCharacter)i$.next();
                              player2.gainMeso(amt, true, true, true);
                          }
                        } else if (type.equalsIgnoreCase("exp")) {
                          amt = Integer.parseInt(splitted[2]);

                          for (i$ = cmc.iterator(); i$.hasNext(); ) {
                            player2 = (MapleCharacter)i$.next();
                            player2.gainExp(amt, true, true, false); }
                        }
                      }
     }
 } 
    }
    @Override
    public CommandDefinition[] getDefinition() {
        return new CommandDefinition[]{
                    new CommandDefinition("maxall", "", "", 5),
                    new CommandDefinition("minall", "", "", 5),
                    new CommandDefinition("fullskill", "", "", 5),
                    new CommandDefinition("maxhp", "", "", 5),
                    new CommandDefinition("maxmp", "", "", 5),
                    new CommandDefinition("hp", "", "", 5),
                    new CommandDefinition("mp", "", "", 5),
                    new CommandDefinition("str", "", "", 5),
                    new CommandDefinition("dex", "", "", 5),
                    new CommandDefinition("int", "", "", 5),
                    new CommandDefinition("luk", "", "", 5),
                    new CommandDefinition("ap", "", "", 5),
                    new CommandDefinition("sp", "", "", 5),
                    new CommandDefinition("job", "", "", 1),//巡查
                    new CommandDefinition("meso", "", "", 5),
                    new CommandDefinition("level", "", "", 5),
                    new CommandDefinition("whereami", "", "", 5),
                    new CommandDefinition("全掉", "", "", 5),
                    new CommandDefinition("刷新地图", "", "", 1),//巡查
                    new CommandDefinition("equip", "", "", 5),
                    new CommandDefinition("item", "", "", 5),
                    new CommandDefinition("警告", "", "", 1),//巡查
                    new CommandDefinition("警告s", "", "", 1),//巡查
                    new CommandDefinition("dropequip", "", "", 5),
                    new CommandDefinition("drop", "", "", 5),
                    new CommandDefinition("position", "", "", 5),
                    new CommandDefinition("dropequip", "", "", 5),
                    new CommandDefinition("clear", "", "", 5),
                    new CommandDefinition("godmode", "", "", 1),//巡查
                    new CommandDefinition("heal", "", "", 1),//巡查
                    new CommandDefinition("healmap", "", "", 5),
                    new CommandDefinition("giftnx", "", "", 5),
                    new CommandDefinition("fame", "", "", 5),
                    new CommandDefinition("unhide", "", "", 1),//巡查
                    new CommandDefinition("unbuff", "", "", 1),//巡查
                    new CommandDefinition("dc", "", "", 5),
                    new CommandDefinition("charinfo", "", "", 5),
                    new CommandDefinition("add", "", "", 5),
                    new CommandDefinition("addgm", "", "", 5),
                    new CommandDefinition("addboss", "", "", 5),
                    new CommandDefinition("addqichong", "", "", 5),
                    new CommandDefinition("adddaoju", "", "", 5),
                    new CommandDefinition("addyaoshui", "", "", 5),
                    new CommandDefinition("addbianshen", "", "", 5),
                    new CommandDefinition("addlaba", "", "", 5),
                    new CommandDefinition("adderhuan", "", "", 5),
                    new CommandDefinition("adddun", "", "", 5),
                    new CommandDefinition("addwanju", "", "", 5),
                    new CommandDefinition("adddnew", "", "", 5),
                    new CommandDefinition("addwuqi1", "", "", 5),
                    new CommandDefinition("addwuqi2", "", "", 5),
                    new CommandDefinition("addwuqi3", "", "", 5),
                    new CommandDefinition("addwuqi4", "", "", 5),
                    new CommandDefinition("addjuan", "", "", 5),
                    new CommandDefinition("addyizi", "", "", 5),
                    new CommandDefinition("addtao", "", "", 5),
                    new CommandDefinition("shop", "", "", 5),
                    new CommandDefinition("opennpc", "", "", 5),
                    new CommandDefinition("clock", "", "", 5),
                    new CommandDefinition("kill", "", "", 5),
                    new CommandDefinition("jobperson", "", "", 5),
                    new CommandDefinition("speak", "", "", 5),
                    new CommandDefinition("help", "", "", 5),
                    new CommandDefinition("char", "", "", 5),
                    new CommandDefinition("help", "", "", 5),
                    new CommandDefinition("GMcoms", "", "", 5),
                    new CommandDefinition("setting", "", "", 5),
                    new CommandDefinition("bossname", "", "", 5),
                    new CommandDefinition("GMcom", "", "", 5),
                    new CommandDefinition("prtycom", "", "", 5),
                    new CommandDefinition("givemap", "", "", 5),
                    new CommandDefinition("help", "", "", 5),
                    new CommandDefinition("killall", "", "Kills all monsters", 5),
                    new CommandDefinition("killalldrops", "", "Kills all monsters with drops", 5),
                    new CommandDefinition("monsterdebug", "", "", 5)};
    }
}