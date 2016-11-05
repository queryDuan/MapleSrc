package net.sf.odinms.scripting.npc;

import java.awt.Point;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import java.sql.*;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.client.IItem;
import net.sf.odinms.client.Item;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventory;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.client.MapleJob;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.scripting.AbstractPlayerInteraction;
import net.sf.odinms.scripting.event.EventManager;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.quest.MapleQuest;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.net.world.guild.MapleGuild;
import net.sf.odinms.server.MapleSquad;
import net.sf.odinms.server.MapleSquadType;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.client.Equip;
import net.sf.odinms.client.ISkill;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.channel.handler.DueyActionHandler;
import net.sf.odinms.net.world.MaplePartyCharacter;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.server.MapleStatEffect;
import net.sf.odinms.server.life.MapleLifeFactory;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.life.MapleMonsterStats;
import net.sf.odinms.server.maps.MapleMapFactory;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.server.MapleMonsterCarnival;
import net.sf.odinms.server.MapleShopFactory;
import net.sf.odinms.server.SpeedRankings;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.server.ClanHolder;

/**
 *
 * @author Matze
 */
public class NPCConversationManager extends AbstractPlayerInteraction {

    private MapleClient c;
    private int npc;
    private String getText;
    private boolean isCash = false;
    private MapleCharacter chr;
    private List<MaplePartyCharacter> otherParty;


    public NPCConversationManager(MapleClient c, int npc) {
        super(c);
        this.c = c;
        this.npc = npc;
    }

    public NPCConversationManager(MapleClient c, int npc, MapleCharacter chr) {
        super(c);
        this.c = c;
        this.npc = npc;
        this.chr = chr;
    }

    public NPCConversationManager(MapleClient c, int npc, List<MaplePartyCharacter> otherParty, int b) { //CPQ
        super(c);
        this.c = c;
        this.npc = npc;
        this.otherParty = otherParty;
    }

    public void dispose() {
        NPCScriptManager.getInstance().dispose(this);
    }
    
    

    public void sendNext(String text) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "00 01", (byte) 0));
    }

    public void sendNext(String text, int speaker) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "00 01", (byte) speaker));
    }

    public void sendPrev(String text) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "01 00", (byte) 0));
    }

    public void sendPrev(String text, int speaker) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "01 00", (byte) speaker));
    }

    public void sendNextPrev(String text) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "01 01", (byte) 0));
    }

    public void sendNextPrev(String text, int speaker) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "01 01", (byte) speaker));
    }

    public void sendOk(String text) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "00 00", (byte) 0));
    }
    
    public void sendOk(String text, int speaker) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "00 00", (byte) speaker));
    }

    public void sendYesNo(String text) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 1, text, "", (byte) 0));
    }

    public void sendYesNo(String text, int speaker) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 1, text, "", (byte) speaker));
    }

    public void sendAcceptDecline(String text) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 0x0B, text, "", (byte) 0));
    }

    public void sendAcceptDecline(String text, int speaker) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 0x0C, text, "", (byte) speaker));
    }

    public void sendSimple(String text) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 4, text, "", (byte) 0));
    }

    public void sendSimple(String text, int speaker) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalk(npc, (byte) 4, text, "", (byte) speaker));
    }

    public void sendStyle(String text, int styles[], int card) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalkStyle(npc, text, styles, card));
    }

    public void sendGetNumber(String text, int def, int min, int max) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalkNum(npc, text, def, min, max));
    }

    public void sendGetText(String text) {
        getClient().getSession().write(MaplePacketCreator.getNPCTalkText(npc, text));
    }
    public void setGetText(String text) {
        this.getText = text;
    }

    public String getText() {
        return this.getText;
    }

    public void setCash(boolean bool) {
        this.isCash = bool;
    }

    public boolean isCash() {
        return this.isCash;
    }

    public void openShop(int id) {
        MapleShopFactory.getInstance().getShop(id).sendShop(getClient());
    }   

    public void openNpc(int id) {
        dispose();
        NPCScriptManager.getInstance().start(getClient(), id);
    }

    public void changeJob(MapleJob job) {
        getPlayer().changeJob(job);
    }

    public MapleJob getJob() {
        return getPlayer().getJob();
    }



    public void startQuest(int id) {
        startQuest(id, false);
    }

    public void startQuest(int id, boolean force) {
        MapleQuest.getInstance(id).start(getPlayer(), npc, force);
    }

    public void completeQuest(int id) {
        completeQuest(id, false);
    }

    public void completeQuest(int id, boolean force) {
        MapleQuest.getInstance(id).complete(getPlayer(), npc, force);
    }

    public void forfeitQuest(int id) {
        MapleQuest.getInstance(id).forfeit(getPlayer());
    }
    /*
     *king to the hidding.
     */
    public void openNpcs(int id) {
        dispose();
        NPCScriptManager.getInstance().starts(getClient(), id);
    }
    
    /**
     * use getPlayer().getMeso() instead
     * @return
     */
    @Deprecated
    public int getMeso() {
        return getPlayer().getMeso();
    }

    public void gainMeso(int gain) {
        getPlayer().gainMeso(gain, true, false, true);
    }

    public void gainExp(int gain) {
        getPlayer().gainExp(gain, true, true);
    }

    public int getNpc() {
        return npc;
    }

    /**
     * use getPlayer().getLevel() instead
     * @return
     */
    @Deprecated
    public int getLevel() {
        return getPlayer().getLevel();
    }

    public void unequipEverything() {
        MapleInventory equipped = getPlayer().getInventory(MapleInventoryType.EQUIPPED);
        MapleInventory equip = getPlayer().getInventory(MapleInventoryType.EQUIP);
        List<Byte> ids = new LinkedList<Byte>();
        for (IItem item : equipped.list()) {
            ids.add(item.getPosition());
        }
        for (byte id : ids) {
            MapleInventoryManipulator.unequip(getC(), id, equip.getNextFreeSlot());
        }
    }

    public void teachSkill(int id, int level, int masterlevel) {
        getPlayer().changeSkillLevel(SkillFactory.getSkill(id), level, masterlevel);
    }

    public void clearSkills() {
        Map<ISkill, MapleCharacter.SkillEntry> skills = getPlayer().getSkills();
        for (Entry<ISkill, MapleCharacter.SkillEntry> skill : skills.entrySet()) {
            getPlayer().changeSkillLevel(skill.getKey(), 0, 0);
        }
    }

    /**
     * Use getPlayer() instead (for consistency with MapleClient)
     * @return
     */
    @Deprecated
	public MapleCharacter getChar()
	{
		return getPlayer();
	}

    public MapleClient getC() {
        return getClient();
    }

    public EventManager getEventManager(String event) {
        return getClient().getChannelServer().getEventSM().getEventManager(event);
    }

	public void showEffect(String effect) {
		getPlayer().getMap().broadcastMessage(MaplePacketCreator.showEffect(effect));
	}

	public void playSound(String sound) {
		getClient().getPlayer().getMap().broadcastMessage(MaplePacketCreator.playSound(sound));
	}

    @Override
    public String toString() {
        return "Conversation with NPC: " + npc;
    }

    public void updateBuddyCapacity(int capacity) {
        getPlayer().setBuddyCapacity(capacity);
    }

    public int getBuddyCapacity() {
        return getPlayer().getBuddyCapacity();
    }

    public void setHair(int hair) {
        getPlayer().setHair(hair);
        getPlayer().updateSingleStat(MapleStat.HAIR, hair);
        getPlayer().equipChanged();
    }

    public void setFace(int face) {
        getPlayer().setFace(face);
        getPlayer().updateSingleStat(MapleStat.FACE, face);
        getPlayer().equipChanged();
    }

    public void setSkin(int color) {
        getPlayer().setSkinColor(c.getPlayer().getSkinColor().getById(color));
        getPlayer().updateSingleStat(MapleStat.SKIN, color);
        getPlayer().equipChanged();
    }

    public void warpParty(int mapId) {
        MapleMap target = getMap(mapId);
        for (MaplePartyCharacter chrs : getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = c.getChannelServer().getPlayerStorage().getCharacterByName(chrs.getName());
            if ((curChar.getEventInstance() == null && c.getPlayer().getEventInstance() == null) || curChar.getEventInstance() == getPlayer().getEventInstance()) {
                curChar.changeMap(target, target.getPortal(0));
            }
        }
    }

    public void warpPartyWithExp(int mapId, int exp) {
        MapleMap target = getMap(mapId);
        for (MaplePartyCharacter chrs : getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = c.getChannelServer().getPlayerStorage().getCharacterByName(chrs.getName());
            if ((curChar.getEventInstance() == null && c.getPlayer().getEventInstance() == null) || curChar.getEventInstance() == getPlayer().getEventInstance()) {
                curChar.changeMap(target, target.getPortal(0));
                curChar.gainExp(exp, true, false, true);
            }
        }
    }

    public void givePartyExp(int exp) {
        for (MaplePartyCharacter chrs : getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = c.getChannelServer().getPlayerStorage().getCharacterByName(chrs.getName());
            curChar.gainExp(exp, true, false, true);
        }
    }

    public void warpPartyWithExpMeso(int mapId, int exp, int meso) {
        MapleMap target = getMap(mapId);
        for (MaplePartyCharacter chrs : getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = c.getChannelServer().getPlayerStorage().getCharacterByName(chrs.getName());
            if ((curChar.getEventInstance() == null && c.getPlayer().getEventInstance() == null) || curChar.getEventInstance() == getPlayer().getEventInstance()) {
                curChar.changeMap(target, target.getPortal(0));
                curChar.gainExp(exp, true, false, true);
                curChar.gainMeso(meso, true);
            }
        }
    }

    public void warpRandom(int mapid) {
        MapleMap target = c.getChannelServer().getMapFactory().getMap(mapid);
        MaplePortal portal = target.getPortal((int) (Math.random() * (target.getPortals().size()))); //generate random portal
        getPlayer().changeMap(target, portal);
    }

	public List<MapleCharacter> getPartyMembers() {
		return c.getPlayer().getParty().getPartyMembers();
	}

    public int itemQuantity(int itemid) {
        return getPlayer().getInventory(MapleItemInformationProvider.getInstance().getInventoryType(itemid)).countById(itemid);
    }

    public MapleSquad createMapleSquad(MapleSquadType type) {
        MapleSquad squad = new MapleSquad(c.getChannel(), getPlayer());
        if (getSquadState(type) == 0) {
            c.getChannelServer().addMapleSquad(squad, type);
        } else {
            return null;
        }
        return squad;
    }

    public MapleCharacter getSquadMember(MapleSquadType type, int index) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        MapleCharacter ret = null;
        if (squad != null) {
            ret = squad.getMembers().get(index);
        }
        return ret;
    }

    public int getSquadState(MapleSquadType type) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            return squad.getStatus();
        } else {
            return 0;
        }
    }

    public void setSquadState(MapleSquadType type, int state) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            squad.setStatus(state);
        }
    }

    public boolean checkSquadLeader(MapleSquadType type) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            if (squad.getLeader().getId() == getPlayer().getId()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void removeMapleSquad(MapleSquadType type) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            if (squad.getLeader().getId() == getPlayer().getId()) {
                squad.clear();
                c.getChannelServer().removeMapleSquad(squad, type);
            }
        }
    }


    public int numSquadMembers(MapleSquadType type) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        int ret = 0;
        if (squad != null) {
            ret = squad.getSquadSize();
        }
        return ret;
    }

    public boolean isSquadMember(MapleSquadType type) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        boolean ret = false;
        if (squad.containsMember(getPlayer())) {
            ret = true;
        }
        return ret;
    }

    public void addSquadMember(MapleSquadType type) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            squad.addMember(getPlayer());
        }
    }

    public void addRandomItem(int id) {
        MapleItemInformationProvider i = MapleItemInformationProvider.getInstance();
        MapleInventoryManipulator.addFromDrop(getClient(), i.randomizeStats((Equip) i.getEquipById(id)), true);
    }

    public void removeSquadMember(MapleSquadType type, MapleCharacter chr, boolean ban) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            squad.banMember(chr, ban);
        }
    }

    public void removeSquadMember(MapleSquadType type, int index, boolean ban) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            MapleCharacter chrs = squad.getMembers().get(index);
            squad.banMember(chrs, ban);
        }
    }

    public boolean canAddSquadMember(MapleSquadType type) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            if (squad.isBanned(getPlayer())) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public void warpSquadMembers(MapleSquadType type, int mapId) {
        MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        MapleMap map = c.getChannelServer().getMapFactory().getMap(mapId);
        if (squad != null) {
            if (checkSquadLeader(type)) {
                for (MapleCharacter chrs : squad.getMembers()) {
                    chrs.changeMap(map, map.getPortal(0));
                }
            }
        }
    }

    public MapleSquad getMapleSquad(MapleSquadType type) {
        return c.getChannelServer().getMapleSquad(type);
    }
     
    
        public void setnld(int Nset) {
            getPlayer().setnld(Nset);
        }
        public int getnld() {
            return getPlayer().getnld();
        }

        public void gainnld(int gain) {
            getPlayer().gainnld(gain);
        }

    
    public void setSquadBossLog(MapleSquadType type, String boss) {
    if (getMapleSquad(type) != null) {
      MapleSquad squad = getMapleSquad(type);
        for (Iterator i$ = squad.getMembers().iterator(); i$.hasNext(); ) { MapleCharacter chrs = (MapleCharacter)i$.next();
        chrs.setBossLog(boss);
      }
    }
  }

    public MapleCharacter getCharByName(String name) {
        try {
            return c.getChannelServer().getPlayerStorage().getCharacterByName(name);
        } catch (Exception e) {
            return null;
        }
    }

    public void resetReactors() {
        getPlayer().getMap().resetReactors();
    }

    public void showlvl() { 
        MapleGuild.showlvl(getClient(), npc); 
    } 
     
    public void showfame() { 
        MapleGuild.showfame(getClient(), npc); 
    } 
    
        public void showreborns() { 
        MapleGuild.showreborns(getClient(), npc); 
    }

         public void showcsb() {
        MapleGuild.showcsb(getClient(), npc);
    } 

    public void showpvpkills() {
        MapleGuild.showpvpkills(getClient(), npc);
    }

        public void showpvpdeaths() {
        MapleGuild.showpvpdeaths(getClient(), npc);
    }

    
    public void displayGuildRanks() {
        MapleGuild.displayGuildRanks(getClient(), npc);
    }

        public void gainFame(int amount) {
        getPlayer().addFame(amount);
        if (amount > 0) {
            getPlayer().dropMessage(1, "你得到了" + amount + "点人气");
        } else {
            getPlayer().dropMessage(1, "你扣掉了" + -amount + "点人气");
        }
    }

    public MapleCharacter getCharacter() {
        return chr;
    }

    public int getCharQuantity(int ismap) {
MapleMap map = c.getChannelServer().getMapFactory().getMap(ismap) ;
double range = Double.POSITIVE_INFINITY;
List<MapleMapObject> players = map.getMapObjectsInRange(c.getPlayer().getPosition (), range, Arrays.asList(MapleMapObjectType.PLAYER));
return players.size();
}
public void Charnotice(int type, String text) {
c.getPlayer().getClient().getSession().write(MaplePacketCreator.serverNotice(type, text));
}


    public void warpAllInMap(int mapid, int portal) {
        MapleMap outMap;
        MapleMapFactory mapFactory;
        mapFactory = ChannelServer.getInstance(c.getChannel()).getMapFactory();
        outMap = mapFactory.getMap(mapid);
        for (MapleCharacter aaa : outMap.getCharacters()) {
            //Warp everyone out
            mapFactory = ChannelServer.getInstance(aaa.getClient().getChannel()).getMapFactory();
            aaa.getClient().getPlayer().changeMap(outMap, outMap.getPortal(portal));
            outMap = mapFactory.getMap(mapid);
            aaa.getClient().getPlayer().getEventInstance().unregisterPlayer(aaa.getClient().getPlayer()); //Unregister them all
        }
    }

    public int countMonster() {
        MapleMap map = c.getPlayer().getMap();
        double range = Double.POSITIVE_INFINITY;
        List<MapleMapObject> monsters = map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER));
        return monsters.size();
    }

    public int countReactor() {
        MapleMap map = c.getPlayer().getMap();
        double range = Double.POSITIVE_INFINITY;
        List<MapleMapObject> reactors = map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.REACTOR));
        return reactors.size();
    }

    public int getDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        int dayy = cal.get(Calendar.DAY_OF_WEEK);
        return dayy;
    }

    public void giveNPCBuff(MapleCharacter chr, int itemID) {
        MapleItemInformationProvider mii = MapleItemInformationProvider.getInstance();
        MapleStatEffect statEffect = mii.getItemEffect(itemID);
        statEffect.applyTo(chr);
    }

                    public void giveWonkyBuff(MapleCharacter chr) {
                        long what = Math.round(Math.random() * 4);
                        int what1 = (int) what;
                        int Buffs[] = {2022090, 2022091, 2022092, 2022093};
                        int buffToGive = Buffs[what1];
                        MapleItemInformationProvider mii = MapleItemInformationProvider.getInstance();
                        MapleStatEffect statEffect = mii.getItemEffect(buffToGive);
                        MapleCharacter character = chr;
                        statEffect.applyTo(character);
                    }

                    public boolean hasSkill(int skillid) {
                        ISkill theSkill = SkillFactory.getSkill(skillid);
                        if (theSkill != null) {
            return c.getPlayer().getSkillLevel(theSkill) > 0;
                        } else {
                            return false;
                        }
                    }
                    public int getboss() {
                        int boss = 0;
                        try {
                            int cid = getPlayer().getAccountID();
                            Connection con = DatabaseConnection.getConnection();
                            PreparedStatement limitCheck = con.prepareStatement("SELECT * FROM accounts WHERE id=" + cid + "");
                            ResultSet rs = limitCheck.executeQuery();
                            if (rs.next())
                            {
                                boss = rs.getInt("boss");
                            }
                            limitCheck.close();
                            rs.close();
                        } catch (SQLException ex) {
                            ex.getStackTrace();
                        }
                        return boss; }

                    public void setboss(int slot) {
                        int cid;
                        try {
                            cid = getPlayer().getAccountID();
                            Connection con = DatabaseConnection.getConnection();
                            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET boss =boss+ " + slot + " WHERE id = " + cid + "");
                            ps.executeUpdate();
                            ps.close(); } catch (SQLException ex) {
                                ex.getStackTrace();
                            }
                    }

                    public void summonMob(int mobid, int customHP, int customEXP, int amount)
                    {
                        MapleMonsterStats newStats = new MapleMonsterStats();
                        if (customHP > 0)
                            newStats.setHp(customHP);

                        if (customEXP >= 0)
                            newStats.setExp(customEXP);

                        if (amount <= 1) {
                            MapleMonster npcmob = MapleLifeFactory.getMonster(mobid);
                            npcmob.setOverrideStats(newStats);
                            npcmob.setHp(npcmob.getMaxHp());
                            getPlayer().getMap().spawnMonsterOnGroudBelow(npcmob, getPlayer().getPosition());
                        } else {
                            for (int i = 0; i < amount; ++i) {
                                MapleMonster npcmob = MapleLifeFactory.getMonster(mobid);
                                npcmob.setOverrideStats(newStats);
                                npcmob.setHp(npcmob.getMaxHp());
                                getPlayer().getMap().spawnMonsterOnGroudBelow(npcmob, getPlayer().getPosition());
                            }
                        }
                    }

                    public void summonMobAtPosition(int mobid, int customHP, int customEXP, int amount, int posx, int posy) {
                        MapleMonsterStats newStats = new MapleMonsterStats();
                        if (customHP > 0)
                            newStats.setHp(customHP);
                        if (customEXP >= 0)
                            newStats.setExp(customEXP);
                        if (amount <= 1) {
                            MapleMonster npcmob = MapleLifeFactory.getMonster(mobid);
                            npcmob.setOverrideStats(newStats);
                            npcmob.setHp(npcmob.getMaxHp());
                            getPlayer().getMap().spawnMonsterOnGroudBelow(npcmob, new Point(posx, posy));
                        } else {
                            for (int i = 0; i < amount; ++i) {
                                MapleMonster npcmob = MapleLifeFactory.getMonster(mobid);
                                npcmob.setOverrideStats(newStats);
                                npcmob.setHp(npcmob.getMaxHp());
                                getPlayer().getMap().spawnMonsterOnGroudBelow(npcmob, new Point(posx, posy));
                            }
                        }
                    }

                    public void summonMobAtPosition(int mobid, int amount, int posx, int posy) {
                        if (amount <= 1) {
                            MapleMonster npcmob = MapleLifeFactory.getMonster(mobid);
                            npcmob.setHp(npcmob.getMaxHp());
                            getPlayer().getMap().spawnMonsterOnGroudBelow(npcmob, new Point(posx, posy));
                        } else {
                            for (int i = 0; i < amount; ++i) {
                                MapleMonster npcmob = MapleLifeFactory.getMonster(mobid);
                                npcmob.setHp(npcmob.getMaxHp());
                                getPlayer().getMap().spawnMonsterOnGroudBelow(npcmob, new Point(posx, posy));
                            }
                        }
                    }
    public int getExpRate() {
        return getClient().getChannelServer().getExpRate();
    }

    public int getDropRate() {
        return getClient().getChannelServer().getDropRate();
    }

    public int getBossDropRate() {
        return getClient().getChannelServer().getBossDropRate();
    }

    public int getzb()
	{
		int money = 0;
		try
		{
			int cid = getPlayer().getAccountID();
			Connection con = DatabaseConnection.getConnection();
			PreparedStatement limitCheck = con.prepareStatement((new StringBuilder()).append("SELECT * FROM accounts WHERE id=").append(cid).append("").toString());
			ResultSet rs = limitCheck.executeQuery();
			if (rs.next())
				money = rs.getInt("money");
			limitCheck.close();
			rs.close();
		}
		catch (SQLException ex) { }
		return money;
	}


	public void setzb(int slot)
	{
		try
		{
			int cid = getPlayer().getAccountID();
			Connection con = DatabaseConnection.getConnection();
			PreparedStatement ps = con.prepareStatement((new StringBuilder()).append("UPDATE accounts SET money =money+ ").append(slot).append(" WHERE id = ").append(cid).append("").toString());
			ps.executeUpdate();
			ps.close();
		}
		catch (SQLException ex) { }
	}
        
        
            public int getjf()
	{
		int jfmoney = 0;
		try
		{
			int cid = getPlayer().getAccountID();
			Connection con = DatabaseConnection.getConnection();
			PreparedStatement limitCheck = con.prepareStatement((new StringBuilder()).append("SELECT * FROM accounts WHERE id=").append(cid).append("").toString());
			ResultSet rs = limitCheck.executeQuery();
			if (rs.next())
				jfmoney = rs.getInt("jfmoney");
			limitCheck.close();
			rs.close();
		}
		catch (SQLException ex) { }
		return jfmoney;
	}


	public void setjf(int slot)
	{
		try
		{
			int cid = getPlayer().getAccountID();
			Connection con = DatabaseConnection.getConnection();
			PreparedStatement ps = con.prepareStatement((new StringBuilder()).append("UPDATE accounts SET jfmoney =jfmoney+ ").append(slot).append(" WHERE id = ").append(cid).append("").toString());
			ps.executeUpdate();
			ps.close();
		}
		catch (SQLException ex) { }
	}
        
   public int getyh()
	{
		int yinhang = 0;
		try
		{
			int cid = getPlayer().getAccountID();
			Connection con = DatabaseConnection.getConnection();
			PreparedStatement limitCheck = con.prepareStatement((new StringBuilder()).append("SELECT * FROM accounts WHERE id=").append(cid).append("").toString());
			ResultSet rs = limitCheck.executeQuery();
			if (rs.next())
				yinhang = rs.getInt("yinhang");
			limitCheck.close();
			rs.close();
		}
		catch (SQLException ex) { }
		return yinhang;
	}


	public void setyh(int slot)
	{
		try
		{
			int cid = getPlayer().getAccountID();
			Connection con = DatabaseConnection.getConnection();
			PreparedStatement ps = con.prepareStatement((new StringBuilder()).append("UPDATE accounts SET yinhang =yinhang+ ").append(slot).append(" WHERE id = ").append(cid).append("").toString());
			ps.executeUpdate();
			ps.close();
		}
		catch (SQLException ex) { }
	}

    public int getMesoRate() {
        return getClient().getChannelServer().getMesoRate();
    }

    public void gainNX(int paypalnx) {
    getPlayer().gainNX(paypalnx); }

    public boolean removePlayerFromInstance() {
        if (getClient().getPlayer().getEventInstance() != null) {
            getClient().getPlayer().getEventInstance().removePlayer(getClient().getPlayer());
            return true;
        }
        return false;
    }
    

    public boolean isPlayerInstance() {
        if (getClient().getPlayer().getEventInstance() != null) {
            return true;
        }
        return false;
    }

    public void openDuey() {
        c.getSession().write(MaplePacketCreator.sendDuey((byte) 9, DueyActionHandler.loadItems(c.getPlayer())));
    }

    public void finishAchievement(int id) {
        getPlayer().finishAchievement(id);
    }

    public void changeStat(byte slot, int type, short amount) {
        Equip sel = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slot);
        switch (type) {
            case 0:
                sel.setStr(amount);
                break;
            case 1:
                sel.setDex(amount);
                break;
            case 2:
                sel.setInt(amount);
                break;
            case 3:
                sel.setLuk(amount);
                break;
            case 4:
                sel.setHp(amount);
                break;
            case 5:
                sel.setMp(amount);
                break;
            case 6:
                sel.setWatk(amount);
                break;
            case 7:
                sel.setMatk(amount);
                break;
            case 8:
                sel.setWdef(amount);
                break;
            case 9:
                sel.setMdef(amount);
                break;
            case 10:
                sel.setAcc(amount);
                break;
            case 11:
                sel.setAvoid(amount);
                break;
            case 12:
                sel.setHands(amount);
                break;
            case 13:
                sel.setSpeed(amount);
                break;
            case 14:
                sel.setJump(amount);
                break;
            default:
                break;
        }
        c.getPlayer().equipChanged();
    }

    public void removeHiredMerchantItem(int id) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM hiredmerchant WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException se) {
        }
    }

    public boolean hasTemp() {
        if (!getPlayer().hasMerchant() && getPlayer().tempHasItems()) {
            return true;
        } else {
            return false;
        }
    }

    public void removeHiredMerchantItem(boolean tempItem, int itemId) {
        String Table = "hiredmerchant";
        if (tempItem) {
            Table = "hiredmerchanttemp";
        }
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM " + Table + " WHERE itemid = ? AND ownerid = ? LIMIT 1");
            ps.setInt(1, itemId);
            ps.setInt(2, getPlayer().getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException se) {
        }
    }

    public long getHiredMerchantMesos() {
        Connection con = DatabaseConnection.getConnection();
        long mesos;
        try {
            PreparedStatement ps = con.prepareStatement("SELECT MerchantMesos FROM characters WHERE id = ?");
            ps.setInt(1, getPlayer().getId());
            ResultSet rs = ps.executeQuery();
            rs.next();
            mesos = rs.getLong("MerchantMesos");
            rs.close();
            ps.close();
        } catch (SQLException se) {
            return 0;
        }
        return mesos;
    }

    public void setHiredMerchantMesos(long set) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("UPDATE characters SET MerchantMesos = ? WHERE id = ?");
            ps.setLong(1, set);
            ps.setInt(2, getPlayer().getId());
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Pair<Integer, IItem>> getStoredMerchantItems() {
        Connection con = DatabaseConnection.getConnection();
        List<Pair<Integer, IItem>> items = new ArrayList<Pair<Integer, IItem>>();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM hiredmerchant WHERE ownerid = ? AND onSale = false");
            ps.setInt(1, getPlayer().getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getInt("type") == 1) {
                    Equip eq = new Equip(rs.getInt("itemid"), (byte) 0);
                    eq.setUpgradeSlots((byte) rs.getInt("upgradeslots"));
                    eq.setLevel((byte) rs.getInt("level"));
                    eq.setStr((short) rs.getInt("str"));
                    eq.setDex((short) rs.getInt("dex"));
                    eq.setInt((short) rs.getInt("int"));
                    eq.setLuk((short) rs.getInt("luk"));
                    eq.setHp((short) rs.getInt("hp"));
                    eq.setMp((short) rs.getInt("mp"));
                    eq.setWatk((short) rs.getInt("watk"));
                    eq.setMatk((short) rs.getInt("matk"));
                    eq.setWdef((short) rs.getInt("wdef"));
                    eq.setMdef((short) rs.getInt("mdef"));
                    eq.setAcc((short) rs.getInt("acc"));
                    eq.setAvoid((short) rs.getInt("avoid"));
                    eq.setHands((short) rs.getInt("hands"));
                    eq.setSpeed((short) rs.getInt("speed"));
                    eq.setJump((short) rs.getInt("jump"));
                    eq.setOwner(rs.getString("owner"));
                    items.add(new Pair<Integer, IItem>(rs.getInt("id"), eq));
                } else if (rs.getInt("type") == 2) {
                    Item newItem = new Item(rs.getInt("itemid"), (byte) 0, (short) rs.getInt("quantity"));
                    newItem.setOwner(rs.getString("owner"));
                    items.add(new Pair<Integer, IItem>(rs.getInt("id"), newItem));
                }
            }
            ps.close();
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
            return null;
        }
        return items;
    }

    public int getAverageLevel(int mapid) {
        int count = 0, total = 0;
        for (MapleMapObject mmo : c.getChannelServer().getMapFactory().getMap(mapid).getAllPlayers()) {
            total += ((MapleCharacter) mmo).getLevel();
            count++;
        }
        return (total / count);
    }

    public void sendCPQMapLists() {
        String msg = "Pick a field:\\r\\n";
        for (int i = 0; i < 6; i++) {
            if (fieldTaken(i)) {
                if (fieldLobbied(i)) {
                    msg += "#b#L" + i + "#Monster Carnival Field " + (i + 1) + " Avg Lvl: " + getAverageLevel(980000100 + i * 100) + "#l\\r\\n";
                } else {
                    continue;
                }
            } else {
                msg += "#b#L" + i + "#Monster Carnival Field " + (i + 1) + "#l\\r\\n";
            }
        }
        sendSimple(msg);
    }

    public boolean fieldLobbied(int field) {
        if (c.getChannelServer().getMapFactory().getMap(980000100 + field * 100).getAllPlayers().size() >= 2) {
            return true;
        } else {
            return false;
        }
    }

    public boolean fieldTaken(int field) {
        MapleMapFactory mf = c.getChannelServer().getMapFactory();
        if ((mf.getMap(980000100 + field * 100).getAllPlayers().size() != 0) ||
                (mf.getMap(980000101 + field * 100).getAllPlayers().size() != 0) ||
                (mf.getMap(980000102 + field * 100).getAllPlayers().size() != 0)) {
            return true;
        } else {
            return false;
        }
    }

    public void CPQLobby(int field) {
        try {
            MapleMap map;
            ChannelServer cs = c.getChannelServer();
            map = cs.getMapFactory().getMap(980000100 + 100 * field);
            for (MaplePartyCharacter mpc : c.getPlayer().getParty().getMembers()) {
                MapleCharacter mc;
                mc = cs.getPlayerStorage().getCharacterByName(mpc.getName());
                if (mc != null) {
                    mc.changeMap(map, map.getPortal(0));
                    String msg = "You will now receive challenges from other parties. If you do not accept a challenge in 3 minutes, you will be kicked out.";
                    mc.getClient().getSession().write(MaplePacketCreator.serverNotice(5, msg));
                    mc.getClient().getSession().write(MaplePacketCreator.getClock(3 * 60));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
public String paiMing()
        {
            String result="";

        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from characters order by reborns desc limit 0, 10");
            ResultSet rs = ps.executeQuery();
            while(rs.next())
            {
                result = result+rs.getString ("name")+" #e#kvIp#r"+rs.getInt("vip")+"#k 轉身次数：#r"+rs.getInt("reborns")+ " #k等級：#r"+rs.getInt("level")+"  #k人氣：#r "+rs.getInt("fame")+" #k杀人次数: #r"+rs.getInt("pvpkills")+" #k被杀次数:#r"+rs.getInt("pvpdeaths")+"#b\r\n______________________________________________#k\r\n";
            }

        } catch (SQLException ex) {
            return "";
        }
            return result;
        }
    public void challengeParty(int field) {
        MapleCharacter leader = null;
        MapleMap map = c.getChannelServer().getMapFactory().getMap(980000100 + 100 * field);
        for (MapleMapObject mmo : map.getAllPlayers()) {
            MapleCharacter mc = (MapleCharacter) mmo;
            if (mc.getParty().getLeader().getId() == mc.getId()) {
                leader = mc;
                break;
            }
        }
        if (leader != null) {
            if (!leader.isCPQChallenged()) {
                List<MaplePartyCharacter> challengers = new LinkedList<MaplePartyCharacter>();
                for (MaplePartyCharacter member : c.getPlayer().getParty().getMembers()) {
                    challengers.add(member);
                }
                NPCScriptManager.getInstance().start("cpqchallenge", leader.getClient(), npc, challengers);
            } else {
                sendOk("The other party is currently taking on a different challenge.");
            }
        } else {
            sendOk("Could not find leader!");
        }
    }

    public void startCPQ(final MapleCharacter challenger, int field) {
        try {
            if (challenger != null) {
                if (challenger.getParty() == null) {
                    throw new RuntimeException("ERROR: CPQ Challenger's party was null!");
                }
                for (MaplePartyCharacter mpc : challenger.getParty().getMembers()) {
                    MapleCharacter mc;
                    mc = c.getChannelServer().getPlayerStorage().getCharacterByName(mpc.getName());
                    if (mc != null) {
                        mc.changeMap(c.getPlayer().getMap(), c.getPlayer().getMap().getPortal(0));
                        mc.getClient().getSession().write(MaplePacketCreator.getClock(10));
                    }
                }
            }
            final int mapid = c.getPlayer().getMap().getId() + 1;
            TimerManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    MapleMap map;
                    ChannelServer cs = c.getChannelServer();
                    map = cs.getMapFactory().getMap(mapid);
                    new MapleMonsterCarnival(getPlayer().getParty(), challenger.getParty(), mapid);
                    map.broadcastMessage(MaplePacketCreator.serverNotice(5, "The Monster Carnival has begun!"));
                }
            }, 10000);
            mapMessage(5, "The Monster Carnival will begin in 10 seconds!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int partyMembersInMap() {
        int inMap = 0;
        for (MapleCharacter char2 : getPlayer().getMap().getCharacters()) {
            if (char2.getParty() == getPlayer().getParty()) {
                inMap++;
            }
        }
        return inMap;
    }


	public int getReborns()
	{
		return getPlayer().getReborns();
	}

	public void gainReborns(int amount)
	{
		getPlayer().gainReborns(amount);
		getPlayer().getClient().getSession().write(MaplePacketCreator.serverNotice(6, (new StringBuilder()).append("你已成功转生了：").append(getPlayer().getReborns()).append("次."+ xiuzhen(c) +"").toString()));
	}

	public void setReborns(int amount)
	{
		getPlayer().setReborns(amount);
		getPlayer().getClient().getSession().write(MaplePacketCreator.serverNotice(6, (new StringBuilder()).append("你已成功转生了：").append(getPlayer().getReborns()).append("次."+ xiuzhen(c) +"").toString()));
	}


    public boolean gotoEvent() {
        ChannelServer cserv = c.getChannelServer();
        MapleMap map = cserv.getMapFactory().getMap(cserv.eventmap);
        int level = getPlayer().getLevel();
        if (level >= cserv.level[0] && level <= cserv.level[1]) {
            c.getPlayer().changeMap(map, map.getPortal(0));
            return true;
        }
        return false;
    }

    public boolean partyMemberHasItem(int iid) {
        List<MapleCharacter> lmc = this.getPartyMembers();
        if (lmc == null) {
            return this.haveItem(iid);
        }
        for (MapleCharacter mc : lmc) {
            if (mc.haveItem(iid, 1, false, false)) {
                return true;
            }
        }
        return false;
    }

    public void spawnMonster(int mobid, int x, int y) {
        Point spawnPos = new Point(x, y);
        MapleMonster npcmob = MapleLifeFactory.getMonster(mobid);
        getPlayer().getMap().spawnMonsterOnGroundBelow(npcmob, spawnPos);
    }

    public void partyNotice(String message) {
        List<MapleCharacter> lmc = this.getPartyMembers();
        if (lmc == null) {
            this.playerMessage(5, message);
            return;
        } else {
            for (MapleCharacter mc : lmc) {
                mc.dropMessage(5, message);
            }
        }
    }

    public String showSpeedRankings(int type) {
        StringBuilder ranks = new StringBuilder("#b#eRankings for ");
        ranks.append(type == 0 ? "Zakum" : "Papulatus");
        ranks.append("#k#n\r\n\r\n");
        for (int i = 0; i < 10; i++) {
            long time = SpeedRankings.getTime(i, type);
            long mins = time / 1000 / 60;
            time -= mins * 1000 * 60;
            long seconds = time / 1000;
            ranks.append(i + 1);
            ranks.append(")#r ");
            ranks.append(SpeedRankings.getTeamMembers(i, type));
            ranks.append("#k ~ #g");
            ranks.append(mins);
            ranks.append("#km#d ");
            ranks.append(seconds);
            ranks.append("#ks");
            ranks.append("\r\n");
        }
        return ranks.toString();
    }

    public void serverNotice(String Text) {
        getClient().getChannelServer().broadcastPacket(MaplePacketCreator.serverNotice(6, Text));
    }

    public boolean getHiredMerchantItems(boolean tempTable) {
        boolean temp = false, compleated = false;
        String Table = "hiredmerchant";
        if (tempTable) {
            Table = "hiredmerchanttemp";
        }
        if (tempTable) {
            temp = true;
        }
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM " + Table + " WHERE ownerid = ?");
            ps.setInt(1, getPlayer().getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getInt("type") == 1) {
                    Equip spItem = new Equip(rs.getInt("itemid"), (byte) 0, false);
                    spItem.setUpgradeSlots((byte) rs.getInt("upgradeslots"));
                    spItem.setLevel((byte) rs.getInt("level"));
                    spItem.setStr((short) rs.getInt("str"));
                    spItem.setDex((short) rs.getInt("dex"));
                    spItem.setInt((short) rs.getInt("int"));
                    spItem.setLuk((short) rs.getInt("luk"));
                    spItem.setHp((short) rs.getInt("hp"));
                    spItem.setMp((short) rs.getInt("mp"));
                    spItem.setWatk((short) rs.getInt("watk"));
                    spItem.setMatk((short) rs.getInt("matk"));
                    spItem.setWdef((short) rs.getInt("wdef"));
                    spItem.setMdef((short) rs.getInt("mdef"));
                    spItem.setAcc((short) rs.getInt("acc"));
                    spItem.setAvoid((short) rs.getInt("avoid"));
                    spItem.setHands((short) rs.getInt("hands"));
                    spItem.setSpeed((short) rs.getInt("speed"));
                    spItem.setJump((short) rs.getInt("jump"));
                    spItem.setOwner(rs.getString("owner"));
                    spItem.setVicious((short) rs.getInt("vicious"));
                    spItem.setExpiration(rs.getTimestamp("expiredate"));                    
                    if (!getPlayer().getInventory(MapleInventoryType.EQUIP).isFull()) {
                        MapleInventoryManipulator.addFromDrop(c, spItem, true);
                        removeHiredMerchantItem(temp, spItem.getItemId());
                    } else {
                        rs.close();
                        ps.close();
                        return false;
                    }
                } else {
                    Item spItem = new Item(rs.getInt("itemid"), (byte) 0, (short) rs.getInt("quantity"));
                    MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                    MapleInventoryType type = ii.getInventoryType(spItem.getItemId());
                    if (!getPlayer().getInventory(type).isFull()) {
                        MapleInventoryManipulator.addFromDrop(c, spItem, true);
                        removeHiredMerchantItem(temp, spItem.getItemId());
                    } else {
                        rs.close();
                        ps.close();
                        return false;
                    }
                }
            }
            rs.close();
            ps.close();
            compleated = true;
        } catch (SQLException se) {
            se.printStackTrace();
            return compleated;
        }
        return compleated;
    }

    @Override
    public void gainItem(int id, short quantity) {
        if (quantity >= 0) {
            StringBuilder logInfo = new StringBuilder(c.getPlayer().getName());
            logInfo.append(" 收到数据 ");
            logInfo.append(quantity);
            logInfo.append(" 从脚本 PlayerInteraction (");
            logInfo.append(this.toString());
            logInfo.append(")");
            MapleInventoryManipulator.addById(c, id, quantity, logInfo.toString());
        } else {
            MapleInventoryManipulator.removeById(c, MapleItemInformationProvider.getInstance().getInventoryType(id), id, -quantity, true, false);
        }
        c.getSession().write(MaplePacketCreator.getShowItemGain(id, quantity, true));
    }

    @Override
    public void resetMap(int mapid) {
        getClient().getChannelServer().getMapFactory().getMap(mapid).resetReactors();
    }
public int getHour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    public int getMin() {
        return Calendar.getInstance().get(Calendar.MINUTE);
    }

    public int getSec() {
        return Calendar.getInstance().get(Calendar.SECOND);
    }
public int gethour() {
Calendar cal = Calendar.getInstance();
int hour = cal.get(Calendar.HOUR_OF_DAY);
return hour;}
public int getmin() {
Calendar cal = Calendar.getInstance();
int min = cal.get(Calendar.MINUTE);
return min;}
public int getsec() {
Calendar cal = Calendar.getInstance();
int sec = cal.get(Calendar.SECOND);
return sec; }
  public void summonBean(int mobid, int amount) {
    Point pos;
    MapleMonsterStats newStats = new MapleMonsterStats();
    if (amount <= 1) {
      MapleMonster npcmob = MapleLifeFactory.getMonster(mobid);
      npcmob.setOverrideStats(newStats);
      npcmob.setHp(npcmob.getMaxHp());
      pos = new Point(8, -42);
      getPlayer().getMap().spawnMonsterOnGroundBelow(npcmob, pos);
    } else {
      for (int i = 0; i < amount; ++i) {
        pos = new Point(8, -42);
        MapleMonster npcmob = MapleLifeFactory.getMonster(mobid);
        npcmob.setOverrideStats(newStats);
        npcmob.setHp(npcmob.getMaxHp());
        getPlayer().getMap().spawnMonsterOnGroundBelow(npcmob, pos); }
    }
  }

  public void deleteItem(int inventorytype) { 
        try { 
            Connection con = DatabaseConnection.getConnection(); 
            PreparedStatement ps = con.prepareStatement("Select * from inventoryitems where characterid=? and inventorytype=?"); 
            ps.setInt(1, getPlayer().getId()); 
            ps.setInt(2, inventorytype); 
            ResultSet re = ps.executeQuery(); 
            MapleInventoryType type = null; 
            switch (inventorytype) { 
                case 1: 
                    type=MapleInventoryType.EQUIP; 
                    break; 
                case 2: 
                    type=MapleInventoryType.USE; 
                    break; 
                case 3: 
                    type=MapleInventoryType.SETUP; 
                    break; 
                case 4: 
                    type=MapleInventoryType.ETC; 
                    break; 
                case 5: 
                    type=MapleInventoryType.CASH; 
                    break; 
            } 
            while (re.next()) { 
            MapleInventoryManipulator.removeById(getC(),type, re.getInt("itemid"),1,true, true); 
            } 
            re.close(); 
            ps.close(); 
            
        } catch (SQLException ex) { 
            // Logger.getLogger(NPCConversationManager.class.getName()).log(Level.SEVERE, null, ex); 
        } 
    }

            public void maxAllSkills(int amount) {
            getPlayer().maxAllSkills(amount);
      //  getPlayer().getClient().getSession().write(MaplePacketCreator.serverNotice(1, "激活成功！"));
    }

  public void closePortal(int mapid, String pName)
  {
    getClient().getChannelServer().getMapFactory().getMap(mapid).getPortal(pName).setPortalState(false);
  }

  public void openPortal(int mapid, String pName)
  {
    getClient().getChannelServer().getMapFactory().getMap(mapid).getPortal(pName).setPortalState(true);
  }

public String EquipList1(MapleClient c) {
StringBuilder str = new StringBuilder();
MapleInventory equip = c.getPlayer().getInventory(MapleInventoryType.EQUIP);
List<String> stra = new LinkedList<String>();
for (IItem item : equip.list()) {
stra.add("#L"+item.getPosition()+"##v"+item.getItemId()+"##l");
}
for (String strb : stra) {
str.append(strb);
}
return str.toString();
}
    public int getwqqhok() {
        return getPlayer().getwqqhok();
    }

    public int getwqqhfull() {
        return getPlayer().getwqqhfull();
    }

    public int getwqqhfail() {
        return getPlayer().getwqqhfail();
    }

    public void gainwqqhfail(int gain) {
        getPlayer().gainwqqhfail(gain);
    }

    public void gainwqqhfull(int gain) {
        getPlayer().gainwqqhfull(gain);
    }
    public void gainwqqhok(int gain) {
        getPlayer().gainwqqhok(gain);
    }

public int getMoney() 
        { 
            int money=0; 
            try { 
                int cid = getPlayer().getId(); 
                Connection con = DatabaseConnection.getConnection(); 
                PreparedStatement limitCheck = con.prepareStatement("SELECT * FROM Bank WHERE charid="+cid+""); 
                ResultSet rs = limitCheck.executeQuery(); 
                if(rs.next()) 
                { 
                    money=rs.getInt("money"); 
                } 
                limitCheck.close(); 
                rs.close(); 
                } catch (SQLException ex) { 
                } 
                return money; 
        } 
        
        public int addMoney(int money,int type) 
        {    
            try { 
                int cid = getPlayer().getId(); 
              
                Connection con = DatabaseConnection.getConnection(); 
                PreparedStatement ps = con.prepareStatement("select * from bank where charid=?"); 
                ps.setInt(1, cid); 
                ResultSet rs=ps.executeQuery(); 
                if(rs.next()) 
                { 
                    if(type==1) 
                    { 
                        if(money>rs.getInt("money")) 
                        { 
                            return -1; 
                        } 
                    } 
                    ps = con.prepareStatement("UPDATE Bank SET money =money+ " + money + " WHERE charid = " + cid + ""); 
                    return ps.executeUpdate();                    
                } 
                
            } catch (SQLException ex) {                
            } 
              return 0; 
        } 
     public String ms(){
            String result="玉兔";
            return result;
        }        
        public int addBank() 
        { 
            int r=0; 
        try { 
            Connection con = DatabaseConnection.getConnection(); 
            PreparedStatement ps = con.prepareStatement("insert into bank (charid,money) values (?,0)"); 
            ps.setInt(1, getPlayer().getId());            
            r=ps.executeUpdate(); 
            ps.close(); 
            
        } catch (SQLException ex) { 
          r=0; 
        } 
            
            return r; 
        }
    public void setClan(int set) {
        getPlayer().setClan(set);
        try {
            getPlayer().getClient().getChannelServer().getWorldInterface().broadcastToClan((getPlayer().getName() + " has entered the clan ! Give him a nice welcome.").getBytes(), set);
        } catch (RemoteException e) {
            c.getChannelServer().reconnectWorld();
        }
        c.getChannelServer().addToClan(getPlayer());
    }

    public String getAllOnlineNamesFromClan(int set) {
        StringBuilder sb = new StringBuilder();
        for (MapleCharacter names : c.getChannelServer().getClanHolder().getAllOnlinePlayersFromClan(set)) {
            sb.append(names.getName() + "\r\n");
        }
        return sb.toString();
    }

    public String getAllOfflineNamesFromClan(int set) {
        StringBuilder sb = new StringBuilder();
        for (String names : c.getChannelServer().getClanHolder().getAllOfflinePlayersFromClan(set)) {
            sb.append(names + "\r\n");
        }
        return sb.toString();
    }

    public int getOfflineClanCount(int menpai) {
        return ClanHolder.countOfflineByClan(menpai);
    }

    public int getOnlineClanCount(int menpai) {
        try {
            return c.getChannelServer().getWorldInterface().onlineClanMembers(menpai);
        } catch (RemoteException re) {
            c.getChannelServer().reconnectWorld();
        }
        return -1;
    }
            public String xiuzhen(MapleClient c) { // 各种定义
                 if (c.getPlayer().getReborns() <= 0) {
            return "修真等级:[凡人]";
                 } else if (c.getPlayer().getReborns() <= 10 && c.getPlayer().getReborns() >= 1) {
            return "修真等级:[筑基期]";
                 } else if (c.getPlayer().getReborns() <= 30 && c.getPlayer().getReborns() >= 11) {
            return "修真等级:[开光期]";
                 } else if (c.getPlayer().getReborns() <= 60 && c.getPlayer().getReborns() >= 31) {
            return "修真等级:[胎息期]";
                 } else if (c.getPlayer().getReborns() <= 80 && c.getPlayer().getReborns() >= 61) {
            return "修真等级:[辟谷期]";
                 } else if (c.getPlayer().getReborns() <= 100 && c.getPlayer().getReborns() >= 81) {
            return "修真等级:[金丹期]";
                 } else if (c.getPlayer().getReborns() <= 150 && c.getPlayer().getReborns() >= 101) {
            return "修真等级:[元婴期]";
                 } else if (c.getPlayer().getReborns() <= 250 && c.getPlayer().getReborns() >= 151) {
            return "修真等级:[分神期]";
                 } else if (c.getPlayer().getReborns() <= 400 && c.getPlayer().getReborns() >= 251) {
            return "修真等级:[合体期]";
                 } else if (c.getPlayer().getReborns() <= 600 && c.getPlayer().getReborns() >= 401) {
            return "修真等级:[大乘期]";
                 } else if (c.getPlayer().getReborns() >=601) {
            return "恭喜你修炼到[渡劫期大圆满]";
                 } else {
            return ""; // 没有则返回空白
                 }
            }
            
            	public void viplaba(String Text, int idk)
	{
		if (Text.isEmpty())
		{
			chr.dropMessage("[注意]文字过长，不能发送，最长为20个字！");
			return;
		}
		for (Iterator ns = ChannelServer.getAllInstances().iterator(); ns.hasNext();)
		{
			ChannelServer cservs = (ChannelServer)ns.next();
			Iterator is = cservs.getPlayerStorage().getAllCharacters().iterator();
			while (is.hasNext())
			{
				MapleCharacter players = (MapleCharacter)is.next();
				if (players.getGMLevel() < 5)
					players.getClient().getSession().write(MaplePacketCreator.startMapEffect((new StringBuilder()).append(c.getPlayer().getName()).append(":").append(Text).toString(), idk, true));
				else
					players.dropMessage((new StringBuilder()).append("[").append(c.getPlayer().getName()).append("]刷出了屏幕祝福喇叭:").append(Text).append("").toString());
			}
		}

	}
                            	public void viplaba(String Text)
	{
		if (Text.isEmpty())
		{
			chr.dropMessage("[注意]文字过长，不能发送，最长为20个字！");
			return;
		}
		for (Iterator ns = ChannelServer.getAllInstances().iterator(); ns.hasNext();)
		{
			ChannelServer cservs = (ChannelServer)ns.next();
			Iterator is = cservs.getPlayerStorage().getAllCharacters().iterator();
			while (is.hasNext())
			{
				MapleCharacter players = (MapleCharacter)is.next();
				if (players.getGMLevel() < 5)
					players.getClient().getSession().write(MaplePacketCreator.startMapEffect((new StringBuilder()).append(c.getPlayer().getName()).append(":").append(Text).toString(), 0x4e2006, true));
				else
					players.dropMessage((new StringBuilder()).append("[").append(c.getPlayer().getName()).append("]刷出了屏幕祝福喇叭:").append(Text).append("").toString());
			}
		}

	}
}