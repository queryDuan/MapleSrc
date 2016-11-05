package net.sf.odinms.net.login.handler;

import java.net.InetAddress;
import java.net.UnknownHostException;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.login.LoginServer;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class CharSelectedHandler extends AbstractMaplePacketHandler {

    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CharSelectedHandler.class);

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        if (c.getLoginState() != MapleClient.LOGIN_LOGGEDIN) {
            return; // Tryst
        }
        int charId = slea.readInt();
        try {
            if (c.getIdleTask() != null) {
                c.getIdleTask().cancel(true);
            }
            if (c.hascheckIP()) {
                c.getSession().close();
                return;
            }      //防万能的
            c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION);
            String channelServerIP = MapleClient.getChannelServerIPFromSubnet(c.getSession().getRemoteAddress().toString().replace("/", "").split(":")[0], c.getChannel());
            if (channelServerIP.equals("0.0.0.0")) {
                String[] socket = LoginServer.getInstance().getIP(c.getChannel()).split(":");
                c.getSession().write(MaplePacketCreator.getServerIP(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1]), charId));
            } else {
                String[] socket = LoginServer.getInstance().getIP(c.getChannel()).split(":");
                c.getSession().write(MaplePacketCreator.getServerIP(InetAddress.getByName(channelServerIP), Integer.parseInt(socket[1]), charId));
            }
        } catch (UnknownHostException e) {
            log.error("Host not found", e);
        }
    }
}