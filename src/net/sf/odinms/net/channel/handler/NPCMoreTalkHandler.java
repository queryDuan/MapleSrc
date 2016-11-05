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
/*    */
/*    */ import net.sf.odinms.client.MapleClient;
/*    */ import net.sf.odinms.net.AbstractMaplePacketHandler;
/*    */ import net.sf.odinms.scripting.npc.NPCConversationManager;
/*    */ import net.sf.odinms.scripting.npc.NPCScriptManager;
/*    */ import net.sf.odinms.scripting.quest.QuestActionManager;
/*    */ import net.sf.odinms.scripting.quest.QuestScriptManager;
/*    */ import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;
/*    */
/*    */ public class NPCMoreTalkHandler extends AbstractMaplePacketHandler
/*    */ {
/*    */   public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c)
/*    */   {
/* 36 */    
/* 37 */     byte lastMsg = slea.readByte();
/* 38 */     byte action = slea.readByte();
/*    */
/* 40 */     if (lastMsg == 2) {
/* 41 */       if (action != 0) {
/* 42 */         String returnText = slea.readMapleAsciiString();
/* 43 */         if (c.getQM() != null) {
/* 44 */           c.getQM().setGetText(returnText);
/* 45 */           if (c.getQM().isStart())
/* 46 */             QuestScriptManager.getInstance().start(c, action, lastMsg, -1);
/*    */           else
/* 48 */             QuestScriptManager.getInstance().end(c, action, lastMsg, -1);
/*    */         }
/*    */         else {
/* 51 */           c.getCM().setGetText(returnText);
/* 52 */           NPCScriptManager.getInstance().action(c, action, lastMsg, -1);
/*    */         }
/*    */       }
/* 55 */       else if (c.getQM() != null) {
/* 56 */         c.getQM().dispose();
/*    */       } else {
/* 58 */         c.getCM().dispose();
/*    */       }
/*    */     }
/*    */     else {
/* 62 */       int selection = -1;
/* 63 */       if (slea.available() >= 4L) {
/* 64 */         selection = slea.readInt();
/* 65 */         if (selection < 0) {
/* 66 */           if (c.getQM() != null)
/* 67 */             c.getQM().dispose();
/*    */           else {
/* 69 */             c.getCM().dispose();
/*    */           }
/* 71 */           return;
/*    */         }
/* 73 */       } else if (slea.available() > 0L) {
/* 74 */         selection = slea.readByte();
/*    */       }
/*    */
/* 77 */       if (c.getQM() != null) {
/* 78 */         if (c.getQM().isStart())
/* 79 */           QuestScriptManager.getInstance().start(c, action, lastMsg, selection);
/*    */         else
/* 81 */           QuestScriptManager.getInstance().end(c, action, lastMsg, selection);
/*    */       }
/* 83 */       else if (c.getCM() != null)
/* 84 */         NPCScriptManager.getInstance().action(c, action, lastMsg, selection);
/*    */     }
/*    */   }
/*    */ }