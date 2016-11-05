package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author
 */
public final class AcceptFamilyHandler extends AbstractMaplePacketHandler {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AcceptFamilyHandler.class);

	public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c)
	{
		//c.doneedlog(this, c.getPlayer());
		//System.out.println(slea);
	}

}