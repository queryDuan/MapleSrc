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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.client.MapleInventory;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class ItemSortHandler extends AbstractMaplePacketHandler {

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {     
    MapleCharacter player = c.getPlayer();
    player.setCurrenttime(System.currentTimeMillis());
    if (player.getCurrenttime() - player.getLasttime() < player.getDeadtime()) {
      player.dropMessage("悠着点!");
      c.getSession().write(MaplePacketCreator.enableActions());
      return;
    }
    c.getSession().write(MaplePacketCreator.enableActions());

        slea.skip(4);
        byte sort = slea.readByte();

        if (sort < 1 || sort > 5) {
            return;
        }

        List<Integer> items = new ArrayList<Integer>();

        MapleInventoryType type = MapleInventoryType.getByType(sort);
        MapleInventory inventory = c.getPlayer().getInventory(type);

        for (byte i = 0; i < 96; i = (byte)(i + 1)) {
            if (inventory.getItem(i) == null) {
                continue;
            } else {
                if (inventory.getItem(i).getItemId() == 5110000) {
                    c.getSession().write(MaplePacketCreator.enableActions());
                } else {
                    if (!items.contains(inventory.getItem(i).getItemId())) {
                        items.add(inventory.getItem(i).getItemId());
                    }
                }
            }
        }

        Collections.sort(items);

        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

        // Stacking
        for (Integer item : items) {
      List stack = new ArrayList();
      stack = inventory.findAllById(item.intValue());
      if ((stack.size() > 1) && (!ii.isRechargable(item.intValue()))) {
        for (byte j = 1; j < stack.size(); j = (byte)(j + 1)) {
          if ((inventory.getItem(((Byte)stack.get(j)).byteValue()) != null) && (inventory.getItem(((Byte)stack.get(j)).byteValue()).getQuantity() < ii.getSlotMax(c, item.intValue()))) {
            for (byte k = 0; k < j; k = (byte)(k + 1)) {
              if ((inventory.getItem(((Byte)stack.get(k)).byteValue()) != null) && (inventory.getItem(((Byte)stack.get(k)).byteValue()).getQuantity() < ii.getSlotMax(c, item.intValue()))) {
                MapleInventoryManipulator.move(c, type, ((Byte)stack.get(j)).byteValue(), ((Byte)stack.get(k)).byteValue());
                break;
                            }
                        }
                    }
                }
            }
        }

        items.clear();

        // Re-create array after stacking
        for (byte i = 0; i < 96; i++) {
            if (inventory.getItem(i) == null) {
                continue;
            } else {
                if (inventory.getItem(i).getItemId() == 5110000) {
                    c.getSession().write(MaplePacketCreator.enableActions());
                } else {
                    if (!items.contains(inventory.getItem(i).getItemId())) {
                        items.add(inventory.getItem(i).getItemId());
                    }
                }
            }
        }

        byte current_slot = 1;

        // Sorting
        Collections.sort(items);
    for (Integer item : items) {
      List stack = new ArrayList();
      stack = inventory.findAllById(item.intValue());
      for (byte j = 0; j < stack.size(); j = (byte)(j + 1)) {
        List new_stack = new ArrayList();
        new_stack = inventory.findAllById(item.intValue());
        if (((Byte)new_stack.get(j)).byteValue() != current_slot) {
          MapleInventoryManipulator.move(c, type, ((Byte)new_stack.get(j)).byteValue(), current_slot);
        }
        current_slot = (byte)(current_slot + 1);
      }
    }
  }
}