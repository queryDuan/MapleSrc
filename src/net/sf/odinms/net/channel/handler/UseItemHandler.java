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

/*
 * UseItemHandler.java
 *
 * Created on 27. November 2007, 16:51
 */
package net.sf.odinms.net.channel.handler;

import java.util.List;
import net.sf.odinms.client.IItem;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.world.MapleParty;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Matze
 */
public class UseItemHandler extends AbstractMaplePacketHandler {

    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        if (!c.getPlayer().isAlive()) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        MapleCharacter chr = c.getPlayer();
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        slea.readInt();
        byte slot = (byte) slea.readShort();
        int itemId = slea.readInt();
        IItem toUse = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
        if (toUse != null && toUse.getQuantity() > 0 && toUse.getItemId() == itemId) {
            if (itemId == 2022178 || itemId == 2022433 || itemId == 2050004) {
                c.getPlayer().dispelDebuffs();
                remove(c, slot);
                return;
            } else if (itemId == 2050003) {
                c.getPlayer().dispelDebuffsi();
                remove(c, slot);
                return;
                
                
////////////////////////////////////////////////////////////////////
                
            }else if (itemId == 2022162) { //组队超级药水
                int Meso =chr.getMeso();
                String nam =chr.getName();

             MapleParty party = chr.getParty();
             if (party != null) {
                List<MapleCharacter> partymems = party.getPartyMembers();
                int sw =0;
                int sy =1;
                for (int i = 0; i < partymems.size(); i++) {
                     if(partymems.get(i).getMapId() == chr.getMapId()){
                       ii.getItemEffect(toUse.getItemId()).applyTo(partymems.get(i));
                    if(partymems.get(i).isAlive()){
                      if(!partymems.get(i).getName().equals(nam)){
                          sy += 1;
                     partymems.get(i).dropMessage(5, "“"+nam+ "”使用了组队超级药水，您所在组队成员的HP/MP全满！");
                    }
                     }else{
                         sw += 1;
                         partymems.get(i).dropMessage(5, "“"+nam+ "”使用了组队超级药水，将您复活，这将消耗对方100W金币！");
                     }
                     }
                }
                int jg =sy*15000+sw*1000000;
                 if(Meso>= jg){
                     chr.gainMeso(-jg, false);
                    ii.getItemEffect(toUse.getItemId()).applyTo(chr);
                    chr.dropMessage(5, "对("+sy+")名组队成员使用了药水，复活("+sw+")名成员。减少"+jg+"金币，目前还有"+chr.getMeso()+"金币。");
               // remove(c, slot);
                 }else{
                     chr.dropMessage(5, "金币不足，无法使用组队超级药水。价格："+jg);
                 }
            } else {
              if(Meso >= 15000){
               // remove(c, slot);
               chr.gainMeso(-15000, false);
               ii.getItemEffect(toUse.getItemId()).applyTo(chr);
               chr.dropMessage(5, "您对自己使用了超级药水，减少您15000金币，目前你还有"+chr.getMeso()+"金币。");
                }else{
                    chr.dropMessage(5, "金币不足，无法使用组队超级药水。价格：组队人数*1.5W");
                }
            }
                c.getSession().write(MaplePacketCreator.enableActions());
            return;
            }else if (itemId == 2022163) { //组队万能疗伤药
                int Meso =chr.getMeso();
                String nam =chr.getName();
             MapleParty party = chr.getParty();
             if (party != null) {
                List<MapleCharacter> partymems = party.getPartyMembers();
                int sy =1;
                for (int i = 0; i < partymems.size(); i++) {
                     if(partymems.get(i).getMapId() == chr.getMapId()){
                    if(partymems.get(i).isAlive()){
                    if(!partymems.get(i).getName().equals(nam)){
                       partymems.get(i).dispelDebuffs();
                          sy += 1;
                     partymems.get(i).dropMessage(5, "“"+nam+ "”使用了组队万能疗伤药，您所在组队成员的异常状态全部解除！");
                    }
                     }
                     }
                }
                int jg =sy*8000;
                 if(Meso>= jg){
                    chr.gainMeso(-jg, false);
                    chr.dispelDebuffs();
                    chr.dropMessage(5, "您对("+sy+")名组队成员使用了万能疗伤药。减少您"+jg+"金币，目前你还有"+chr.getMeso()+"金币。");
               // remove(c, slot);
                 }else{
                     chr.dropMessage(5, "金币不足，无法使用组队超级药水。价格："+jg);
                 }
            } else {
              if(Meso >= 8000){
               // remove(c, slot);
               chr.gainMeso(-8000, false);
               chr.dispelDebuffs();
               chr.dropMessage(5, "您对自己使用了万能疗效药，减少您8000金币，目前你还有"+chr.getMeso()+"金币。");
                }else{
                    chr.dropMessage(5, "金币不足，无法使用组队超级药水。价格：组队人数*8000");
                }
            }
                c.getSession().write(MaplePacketCreator.enableActions());
            return;
            
            
/////////////////////////////////////////////////
            
            
            
            
                
            }

            if (isTownScroll(itemId)) {
                if (ii.getItemEffect(toUse.getItemId()).applyTo(c.getPlayer())) {
                    remove(c, slot);
                }
                c.getSession().write(MaplePacketCreator.enableActions());
                return;
            }
            remove(c, slot);
            ii.getItemEffect(toUse.getItemId()).applyTo(c.getPlayer());
            c.getPlayer().checkBerserk();
        }
    }

    private final void remove(MapleClient c, byte slot) {
        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
        c.getSession().write(MaplePacketCreator.enableActions());
    }

    private boolean isTownScroll(int itemId) {
        return itemId >= 2030000 && itemId < 2030021;
    }
}