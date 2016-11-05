package net.sf.odinms.net.channel.handler;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.CommandProcessor;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class GeneralchatHandler extends AbstractMaplePacketHandler
{

	public GeneralchatHandler()
	{
	}

	public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c)
	{
		String text = slea.readMapleAsciiString();
		int show = slea.readByte();
		if (c.getPlayer().getCheatTracker().textSpam(text) && !c.getPlayer().isGM())
		{
			c.getSession().write(MaplePacketCreator.serverNotice(5, "请勿刷屏"));
			return;
		}
		if (text.length() > 70 && !c.getPlayer().isGM())
			return;
		if (!CommandProcessor.getInstance().processCommand(c, text))
		{
			if (c.getPlayer().isMuted() || c.getPlayer().getMap().getMuted() && !c.getPlayer().isGM())
			{
				c.getPlayer().dropMessage(5, c.getPlayer().isMuted() ? "You are " : "The map is muted, therefore you are unable to talk.");
				return;
			}
			if (!c.getPlayer().isGM())
			{
				switch (c.getPlayer().getVip())
				{
                             
				case 1: // '\002'
                                        c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.serverNotice(2, (new StringBuilder()).append("{vIp①}").append(c.getPlayer().getName()).append(" : ").append(text).toString()));
                                        c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getChatText(c.getPlayer().getId(),  text , false, 1));
					break;

				case 2: // '\003'
                                        c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.serverNotice(2, (new StringBuilder()).append("{vIp②}").append(c.getPlayer().getName()).append(" : ").append(text).toString()));
                                        c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getChatText(c.getPlayer().getId(), text, false, 1));
					break;

				case 3: // '\004'
                                        c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.serverNotice(2, (new StringBuilder()).append("{vIp③}").append(c.getPlayer().getName()).append(" : ").append(text).toString()));
                                        c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getChatText(c.getPlayer().getId(), text, false, 1));
					break;

				case 4: // '\005'
                                        c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.serverNotice(2, (new StringBuilder()).append("{vIp④}").append(c.getPlayer().getName()).append(" : ").append(text).toString()));
                                        c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getChatText(c.getPlayer().getId(), text, false, 1));
					break;

				
				default:
					c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getChatText(c.getPlayer().getId(), text, c.getPlayer().hasGMLevel(3) && c.getChannelServer().allowGmWhiteText(), show));
					break;
				}
                        } else
			if (c.getPlayer().getGMLevel() >= 3)
			{
                                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getChatText(c.getPlayer().getId(), text, true, show));
				c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getChatText(c.getPlayer().getId(), text, false, 1));
			} else
			{
                                 c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getChatText(c.getPlayer().getId(), text, true, show));
				 c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.getChatText(c.getPlayer().getId(), text, false, 1));
			}
                }
	}


}




