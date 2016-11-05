package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.scripting.npc.NPCConversationManager;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.server.maps.*;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
import org.apache.mina.common.IoSession;

public class PlayerUpdateHandler extends AbstractMaplePacketHandler
{

	public PlayerUpdateHandler()
	{
	}

	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c,NPCConversationManager cc)
	{
		if (c.getPlayer().getMapId() == 910000000)
		{
                      
			c.getPlayer().startMapEffect1("自由市场欢迎您.", 5120027);
			c.getPlayer().saveToDB(true);
		} else
		if (c.getPlayer().getMapId() == 910000001)
		{
		//	c.getPlayer().startMapEffect1("进入:自由市场<1>洞 雇佣商店可开启", 5120027);
			c.getPlayer().saveToDB(true);
		} else
		if (c.getPlayer().getMapId() == 910000002)
		{
		//	c.getPlayer().startMapEffect1("进入:自由市场<2>洞 雇佣商店可开启", 5120027);
			c.getPlayer().saveToDB(true);
		} else
		if (c.getPlayer().getMapId() == 910000003)
		{
		//	c.getPlayer().startMapEffect1("进入:自由市场<3>洞 雇佣商店可开启", 5120027);
			c.getPlayer().saveToDB(true);
		} else
		if (c.getPlayer().getMapId() == 910000004)
		{
			c.getPlayer().startMapEffect1("进入:VIP2刷怪地图,这里的怪物经验是平常的两倍.", 5120027);
			c.getPlayer().saveToDB(true);
		} else
		if (c.getPlayer().getMapId() == 910000005)
		{
			c.getPlayer().startMapEffect1("进入:VIP3刷怪地图,这里的怪物验是平常的三倍.", 5120027);
			c.getPlayer().saveToDB(true);
		} else
		if (c.getPlayer().getMapId() == 910000006)
		{
			c.getPlayer().startMapEffect1("进入:VIP4刷怪地图,这里的怪物验是平常的四倍.", 5120027);
			c.getPlayer().saveToDB(true);
		} else
		if (c.getPlayer().getMapId() == 910000007)
		{
			//c.getPlayer().startMapEffect1("进入:自由市场<7>洞.", 5120027);
			c.getPlayer().saveToDB(true);
		} else
		if (c.getPlayer().getMapId() == 910000008)
		{
			//c.getPlayer().startMapEffect1("进入:自由市场<8>洞", 5120027);
			c.getPlayer().saveToDB(true);
		} else
		if (c.getPlayer().getMapId() == 910000009)
		{
		//	c.getPlayer().startMapEffect1("进入:自由市场<9>洞", 5120027);
			c.getPlayer().saveToDB(true);
		} else
		if (c.getPlayer().getMapId() == 910000010)
		{
			//c.getPlayer().startMapEffect1("进入:自由市场<10>洞", 5120027);
			c.getPlayer().saveToDB(true);
		} else
		if (c.getPlayer().getMapId() == 910000011)
		{
			//c.getPlayer().startMapEffect1("进入:自由市场<11>洞", 5120027);
			c.getPlayer().saveToDB(true);
		} else
		if (c.getPlayer().getMapId() == 910000012)
		{
			//c.getPlayer().startMapEffect1("进入:自由市场<12>洞", 5120027);
			c.getPlayer().saveToDB(true);
		} else
		if (c.getPlayer().getMapId() == 910000013)
		{
			//c.getPlayer().startMapEffect1("进入:自由市场<13>洞", 5120027);
			c.getPlayer().saveToDB(true);
		} else
		if (c.getPlayer().getMapId() == 910000014)
		{
			//c.getPlayer().startMapEffect1("进入:自由市场<14>洞", 5120027);
			c.getPlayer().saveToDB(true);
		} else
		if (c.getPlayer().getMapId() == 910000015)
		{
			//c.getPlayer().startMapEffect1("进入:自由市场<15>洞", 5120027);
			c.getPlayer().saveToDB(true);
		} else
		if (c.getPlayer().getMapId() == 910000016)
		{
			//c.getPlayer().startMapEffect1("进入:自由市场<16>洞", 5120027);
			c.getPlayer().saveToDB(true);
		} else
		if (c.getPlayer().getMapId() == 910000017)
		{
			//c.getPlayer().startMapEffect1("进入:自由市场<17>洞", 5120027);
			c.getPlayer().saveToDB(true);
		} else
		if (c.getPlayer().getMapId() == 910000018)
		{
			//c.getPlayer().startMapEffect1("进入:自由市场<18>洞", 5120027);
			c.getPlayer().saveToDB(true);
		} else
		if (c.getPlayer().getMapId() == 910000019)
		{
			//c.getPlayer().startMapEffect1("进入:自由市场<19>洞", 5120027);
			c.getPlayer().saveToDB(true);
		} else
		if (c.getPlayer().getMapId() == 910000020)
		{
		///	c.getPlayer().startMapEffect1("进入:自由市场<20>洞", 5120027);
			c.getPlayer().saveToDB(true);
		} else
		if (c.getPlayer().getMapId() == 910000021)
		{
			//c.getPlayer().startMapEffect1("进入:自由市场<21>洞", 5120027);
			c.getPlayer().saveToDB(true);
		} else
		if (c.getPlayer().getMapId() == 0 || c.getPlayer().getMapId() == 130030000 || c.getPlayer().getMapId() == 914000000)
		{
			c.getSession().write(MaplePacketCreator.enableActions());
			MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(0x363d7f96);
			c.getPlayer().saveLocation(SavedLocationType.FREE_MARKET);
			net.sf.odinms.server.MaplePortal pto = to.getPortal("out00");
			c.getPlayer().changeMap(to, pto);
			//c.getPlayer().startMapEffect1("进入:新人集中营 ,这里是安全地带.", 5120027);
			c.getPlayer().saveToDB(true);
             
		} else
		if (c.getPlayer().getMapId() == 913040001)
		{
                     c.getPlayer().getClient().getSession().write(MaplePacketCreator.sendHint("欢迎来到#r"+cc.ms()+"冒险岛#k!", 60, 20));
			//c.getPlayer().startMapEffect1("进入:新人集中营 ,这里是安全地带.", 5121020);
			c.getPlayer().saveToDB(true);
		} else
		{
			MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
		//	c.getPlayer().startMapEffect1((new StringBuilder()).append("进入:").append(c.getPlayer().getMap().getMapName()).append("").toString(), 5120027);
			c.getPlayer().saveToDB(true);
		}
	}

    public void handlePacket(SeekableLittleEndianAccessor arg0, MapleClient arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
