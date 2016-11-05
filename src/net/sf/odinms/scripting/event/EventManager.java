
package net.sf.odinms.scripting.event;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Invocable;
import javax.script.ScriptException;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.MapleParty;
import net.sf.odinms.server.MapleSquad;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.server.maps.MapleMap;
import java.util.Iterator;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.tools.MaplePacketCreator;
import java.sql.ResultSet;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.scripting.npc.NPCConversationManager;
import net.sf.odinms.scripting.npc.NPCScriptManager;
/**
 *
 * @author Matze
 */
public class EventManager {

    private Invocable iv;
    private ChannelServer cserv;
    private Map<String, EventInstanceManager> instances = new HashMap<String, EventInstanceManager>();
    private Properties props = new Properties();
    private String name;
private MapleCharacter chr;

    public EventManager(ChannelServer cserv, Invocable iv, String name) {
        this.iv = iv;
        this.cserv = cserv;
        this.name = name;
    }
public int getHour() {
    Calendar cal = Calendar.getInstance();
    int hour = cal.get(11);
    return hour;
  }
/*public void zidonglaba(String Text)
        {
            if (Text.isEmpty())
		{
			chr.dropMessage("[注意]文字过长，不能发送，最长为20个字！");
			return;
		}
		for (Iterator n$ = ChannelServer.getAllInstances().iterator(); n$.hasNext();)
		{
			ChannelServer cservs = (ChannelServer)n$.next();
			Iterator i$ = cservs.getPlayerStorage().getAllCharacters().iterator();
			while (i$.hasNext())
			{
				MapleCharacter players = (MapleCharacter)i$.next();
                                if (getHour() >= 0 || getHour() >= 5 || getHour() >= 10 || getHour() >=15 || getHour() >= 20)
                                    Text = "活动多多精彩多多.祝大家游戏愉快!";
                                        players.getClient().getSession().write(MaplePacketCreator.startMapEffect((new StringBuilder()).append("祝福").append(":").append(Text).toString(), 5120027, true));
                                if (getHour() >= 1 || getHour() >= 6 || getHour() >= 11 || getHour() >=16 || getHour() >= 21)
                                    Text = "让我们一起拒绝外挂,一起携手打造美好的冒险环境!";
                                        players.getClient().getSession().write(MaplePacketCreator.startMapEffect((new StringBuilder()).append("祝福").append(":").append(Text).toString(), 5120021, true));
                                if (getHour() >= 2 || getHour() >= 7 || getHour() >= 12 || getHour() >= 17 || getHour() >= 22)
                                    Text = "祝大家能够在逆天冒险岛里玩的开心,好友成群!";
                                        players.getClient().getSession().write(MaplePacketCreator.startMapEffect((new StringBuilder()).append("祝福").append(":").append(Text).toString(), 5120021, true));
                                if (getHour() >= 3 || getHour() >= 8 || getHour() >= 13 || getHour() >= 18 || getHour() >= 23)
                                    Text = "有你们的支持,我们会做得更好!";
                                        players.getClient().getSession().write(MaplePacketCreator.startMapEffect((new StringBuilder()).append("祝福").append(":").append(Text).toString(), 5120025, true));
                                if (getHour() >= 4 || getHour() >= 9 || getHour() >= 14 || getHour() >= 19 || getHour() >= 24)
                                    Text = "5线为全线PK频道,PK死后要掉装备和钱钱哟!";
                                        players.getClient().getSession().write(MaplePacketCreator.startMapEffect((new StringBuilder()).append("注意").append(":").append(Text).toString(), 5120024, true));
			}
		}

	}*/

    public void cancel() {
        try {
            iv.invokeFunction("cancelSchedule", (Object) null);
        } catch (ScriptException ex) {
            Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void schedule(final String methodName, long delay) {
        TimerManager.getInstance().schedule(new Runnable() {
            public void run() {
                try {
                    iv.invokeFunction(methodName, (Object) null);
                } catch (ScriptException ex) {
                    Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchMethodException ex) {
                    Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, delay);
    }

	public ScheduledFuture scheduleAtTimestamp(final String methodName, long timestamp)
	{
        return TimerManager.getInstance().scheduleAtTimestamp(new Runnable() {

            public void run() {
                try {
                    iv.invokeFunction(methodName, (Object) null);
                } catch (ScriptException ex) {
                    Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchMethodException ex) {
                    Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, timestamp);
    }


    public ChannelServer getChannelServer() {
        return cserv;
    }

    public EventInstanceManager getInstance(String name) {
        return instances.get(name);
    }

    public Collection<EventInstanceManager> getInstances() {
        return Collections.unmodifiableCollection(instances.values());
    }

    public EventInstanceManager newInstance(String name) {
        EventInstanceManager ret = new EventInstanceManager(this, name);
        instances.put(name, ret);
        return ret;
    }

    public void disposeInstance(String name) {
        instances.remove(name);
    }

    public Invocable getIv() {
        return iv;
    }

    public void setProperty(String key, String value) {
        props.setProperty(key, value);
    }
    public void openNpc(){
	NPCConversationManager cm = null;
	cm.openNpcs(1002005);
    }
    public String getProperty(String key) {
        return props.getProperty(key);
    }

    public String getName() {
        return name;
    }

    //PQ method: starts a PQ
    public void startInstance(MapleParty party, MapleMap map) {
        try {
            EventInstanceManager eim = (EventInstanceManager) (iv.invokeFunction("setup", (Object) null));
            eim.registerParty(party, map);
        } catch (ScriptException ex) {
            Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void startInstance(MapleParty party, MapleMap map, boolean partyid) {
        try {
            EventInstanceManager eim;
            if (partyid) {
                eim = (EventInstanceManager) (iv.invokeFunction("setup", party.getId()));
            } else {
                eim = (EventInstanceManager) (iv.invokeFunction("setup", (Object) null));
            }
            eim.registerParty(party, map);
        } catch (ScriptException ex) {
            Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void startInstance(MapleSquad squad, MapleMap map) {
        try {
            EventInstanceManager eim = (EventInstanceManager) (iv.invokeFunction("setup", squad.getLeader().getId()));
            eim.registerSquad(squad, map);
        } catch (ScriptException ex) {
            Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //non-PQ method for starting instance
    public void startInstance(EventInstanceManager eim, String leader) {
        try {
            iv.invokeFunction("setup", eim);
            eim.setProperty("leader", leader);
        } catch (ScriptException ex) {
            Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //returns EventInstanceManager
    public EventInstanceManager startEventInstance(MapleParty party, MapleMap map, boolean partyid) {
        try {
            EventInstanceManager eim;
            if (partyid) {
                eim = (EventInstanceManager) (iv.invokeFunction("setup", party.getId()));
            } else {
                eim = (EventInstanceManager) (iv.invokeFunction("setup", (Object) null));
            }
            eim.registerParty(party, map);
            return eim;
        } catch (ScriptException ex) {
            Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(EventManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
            public void zidongGC()//自动攻城
	{
		Collection chrs = cserv.getPlayerStorage().getAllCharacters();
		MapleCharacter chr;
		for (Iterator is = chrs.iterator(); is.hasNext(); chr.getClient().getSession().write(MaplePacketCreator.startMapEffect((new StringBuilder()).append("【怪物攻城】:自由市场出现了异样,岛民快来消灭怪物,即可获得大量奖励!").toString(), 5120021, true)))
		{
			chr = (MapleCharacter)is.next();
			int boss[] = {
				9600063, 9600064, 9600065, 9600066, 9600067, 9600068, 9600069, 9600070, 9600071, 9600072,
				9600073, 9600074
			};
			int rand = (int)Math.floor(Math.random() * 10D);
			if (chr.getVip() > 3)
				chr.spawnMob(910000000, boss[rand], 1247, 4);
			chr.getClient().getSession().write(MaplePacketCreator.serverNotice(6, (new StringBuilder()).append("[系统提示]:由于您是尊贵的帝王vIp").append(chr.getVip()).append("，在线30分钟出现了怪物攻城.").toString()));
		}

                
           }public void autoXiulian() {//原修炼。改用为在线得BOSS召唤卡.代码模式:item未定
       Collection<MapleCharacter> chrs = cserv.getPlayerStorage().getAllCharacters();
       for (MapleCharacter chr : chrs) {
       int xiulian = chr.getVip() * 2 + 2;
       if (chr.getVip() > 3){
      // chr.gainitem(11111,11);
       int timeMin = chr.TotalLoginTimeMin();
       chr.gainnld(xiulian);
       chr.saveToDB(true);
       chr.getClient().getSession().write(MaplePacketCreator.serverNotice(5, "[在线修炼]:在线时间" + timeMin + "分钟,获得了"+xiulian+"能量点,能量点使用请找:NPC:."));
       }else{
       chr.saveToDB(true);

        }
       }

                
	} public void autoExp() {
            Collection<MapleCharacter> chrs = cserv.getPlayerStorage().getAllCharacters();

            String xx = null;
            for (MapleCharacter chr : chrs) {
            int giveExp = chr.getLevel()*cserv.getExpRate()*(chr.getVip()+1);
            //int givenld= (chr.getVip()+1);
            int giveNX= (chr.getVip()+1)*4;
            int rand = (int)Math.floor(Math.random() * 20000D) ;
             if (chr.getVip() == 0){
                 xx="普通玩家";
             }else if (chr.getVip() == 1){
                 xx="玉兔会员①";
             }else if (chr.getVip() == 2){
                 xx="玉兔会员②";
             }else if (chr.getVip() == 3){
                 xx="玉兔会员③";
             }else if (chr.getVip() == 4){
                 xx="玉兔会员④";
             }else if (chr.getVip() == 5){
                 xx="玉兔会员⑤";
             }else if (chr.getVip() == 6){
                 xx="玉兔会员⑥";
             }
            
            if (chr.getMapId() == 910000000 &&chr.getClient().getChannel() == 1 && !chr.isGM()) {
                chr.gainExp (giveExp, true,true);
                //chr.gainnld(+givenld);
                chr.gainNX(+giveNX);
                if (chr.getVip() > 0){
                    chr.getClient().getSession().write(MaplePacketCreator.serverNotice(5, "[●市场泡点]:["+ xx +"]挂机奖励[" + giveExp + "]经验、[" + giveNX + "]点券!"));
                } else {
                    chr.getClient().getSession().write(MaplePacketCreator.serverNotice(5, "[●市场泡点]:["+ xx +"]挂机奖励[" + giveExp + "]经验、[" + giveNX + "]点券!"));
               }
           }else if (chr.getMapId() >= 195000000 &&chr.getMapId() <= 195030000) {
               if (chr.getMeso() > rand){
                   
                   chr.gainMeso(-rand, true);
                   chr.getClient().getSession().write(MaplePacketCreator.serverNotice(5, "[网吧信息]:扣除"+rand+"金币."));
                }else{
                   chr.changeMap(193000000,0);
                   chr.getClient().getSession().write(MaplePacketCreator.serverNotice(5, "[网吧信息]:由于金币不足,被自动踢出网吧."));
                
               }
               
             } 
         }
      }
      
      }


