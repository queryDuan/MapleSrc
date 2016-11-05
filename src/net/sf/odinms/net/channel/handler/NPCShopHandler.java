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

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 * 
 * @author Matze
 */
public class NPCShopHandler extends AbstractMaplePacketHandler {

    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        byte bmode = slea.readByte();
        if (bmode == 0) { // mode 0 = buy
            slea.readShort();//?
            int itemId = slea.readInt();
            short quantity = slea.readShort();
                if(itemId == 2022162){
                if(c.getPlayer().getItemQuantity(2022162, true)> 0){
                   c.getPlayer().dropMessage(1, "组队超级药水只能拥有一个，且使用后不会消失！");
                   c.getSession().write(MaplePacketCreator.confirmShopTransaction((byte) 0));
                   return;
                }else if(quantity == 1){
                   c.getPlayer().dropMessage(1, "组队超级药水使用说明:\r\n组队超级药水只能拥有一个，且使用后不会消失！\r\n每次使用将自动扣除[组队人数×1.5W]金币\r\n必须在同一地图的组队成员才能享有效果\r\n如果组队成员死亡，可以复活他们。复活后将自动扣除[死亡人数×100W]");
                }else {
                   c.getPlayer().dropMessage(1, "组队超级药水只能拥有一个，且使用后不会消失！");
                   c.getSession().write(MaplePacketCreator.confirmShopTransaction((byte) 0));
                   return;
                }
            }else if(itemId == 2022163){
                if(c.getPlayer().getItemQuantity(2022163, true)> 0){
                   c.getPlayer().dropMessage(1, "组队万能疗伤药只能拥有一个，且使用后不会消失！");
                   c.getSession().write(MaplePacketCreator.confirmShopTransaction((byte) 0));
                   return;
                }else if(quantity == 1){
                   c.getPlayer().dropMessage(1, "组队万能疗伤药使用说明:\r\n组队万能疗伤药只能拥有一个，且使用后不会消失！\r\n每次使用将自动扣除[组队人数×8000]金币\r\n必须在同一地图的组队成员才能享有效果");
                }else {
                   c.getPlayer().dropMessage(1, "组队万能疗伤药只能拥有一个，且使用后不会消失！");
                   c.getSession().write(MaplePacketCreator.confirmShopTransaction((byte) 0));
                   return;
                }
            }
            
            c.getPlayer().getShop().buy(c, itemId, quantity);
        } else if (bmode == 1) { // sell
            byte slot = (byte) slea.readShort();
            int itemId = slea.readInt();
            MapleInventoryType type = MapleItemInformationProvider.getInstance().getInventoryType(itemId);
            short quantity = slea.readShort();
            c.getPlayer().getShop().sell(c, type, slot, quantity);
        } else if (bmode == 2) { // recharge
            byte slot = (byte) slea.readShort();
            c.getPlayer().getShop().recharge(c, slot);
        }
    }
}