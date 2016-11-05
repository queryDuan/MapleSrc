package net.sf.odinms.net.channel.handler;

import java.awt.Point;

import java.util.concurrent.ScheduledFuture;

import net.sf.odinms.client.ISkill;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleCharacter.CancelCooldownAction;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleStat;
import net.sf.odinms.client.SkillFactory;
import net.sf.odinms.client.messages.ServernoticeMapleClientMessageCallback;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.server.MapleStatEffect;
import net.sf.odinms.server.TimerManager;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.SavedLocationType;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class SpecialMoveHandler extends AbstractMaplePacketHandler {

    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SpecialMoveHandler.class);

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        slea.readShort();
        slea.readShort();
        int skillid = slea.readInt();     
        MapleCharacter chr = c.getPlayer();
        if ((skillid == 4001003 || skillid == 4221006 || skillid == 5101007) && !c.getPlayer().isGM() && c.getPlayer().getMap().cannotInvincible()) {
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        Point pos = null;
        int __skillLevel = slea.readByte();
        ISkill skill = SkillFactory.getSkill(skillid);
        int skillLevel = c.getPlayer().getSkillLevel(skill);
        MapleStatEffect effect = skill.getEffect(skillLevel);
        c.getPlayer().resetAfkTimer();
        int beforeMp = c.getPlayer().getMp();
        if (skillid % 10000000 == 1010 || skillid % 10000000 == 1011) {
            skillLevel = 1;
            c.getPlayer().setDojoEnergy(0);
            c.getSession().write(MaplePacketCreator.getEnergy(0));
        }
        if (effect.getCooldown() > 0) {
            if (c.getPlayer().skillisCooling(skillid)) {
                return;
            } else {
                if (skillid != 5221006) {
                    c.getSession().write(MaplePacketCreator.skillCooldown(skillid, effect.getCooldown()));
                    ScheduledFuture<?> timer = TimerManager.getInstance().schedule(new CancelCooldownAction(c.getPlayer(), skillid), effect.getCooldown() * 1000);
                    c.getPlayer().addCooldown(skillid, System.currentTimeMillis(), effect.getCooldown() * 1000, timer);
                }
            }
        }
        //monster magnet
        try {
            switch (skillid) {
                case 1121001:
                case 1221001:
                case 1321001:
                    int num = slea.readInt();
                    int mobId;
                    byte success;
                    for (int i = 0; i < num; i++) {
                        mobId = slea.readInt();
                        success = slea.readByte();
                        c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.showMagnet(mobId, success), false);
                        MapleMonster monster = c.getPlayer().getMap().getMonsterByOid(mobId);
                        if (monster != null) {
                            monster.switchController(c.getPlayer(), monster.isControllerHasAggro());
                        }
                    }
                    byte direction = slea.readByte();
                    c.getPlayer().getMap().broadcastMessage(c.getPlayer(), MaplePacketCreator.showBuffeffect(c.getPlayer().getId(), skillid, 1, direction), false);
                    c.getSession().write(MaplePacketCreator.enableActions());
                    break;
            }
        } catch (Exception e) {
            log.warn("Failed to handle monster magnet..", e);
        }
        if (skillid % 20000000 == 1004) {
            slea.readShort();
        }
        if (slea.available() == 5) {
            pos = new Point(slea.readShort(), slea.readShort());
            //log.info("新位置,X-Y");
        }
        if ((skillid == 1111002) && (((c.getPlayer().getJob().getId() < 111) || (c.getPlayer().getJob().getId() > 112))))
     {
       c.getPlayer().cancelAllBuffs();

       c.getPlayer().dropMessage(6, "由于职业限制,无法使用这个技能.");
       c.getSession().write(MaplePacketCreator.enableActions());
       return;
     }

     if ((skillid == 5111005) && (((c.getPlayer().getJob().getId() < 511) || (c.getPlayer().getJob().getId() > 512))))
     {
       c.getPlayer().cancelAllBuffs();
       c.getPlayer().dropMessage(6, "由于职业限制,无法使用这个技能.");
       c.getSession().write(MaplePacketCreator.enableActions());
       return;
     }        
        
      if ((skillid == 9001004) && (((c.getPlayer().getGMLevel() == 0))))
     {
       c.getPlayer().cancelAllBuffs();
       c.getPlayer().dropMessage(6, "你不是GM,不能使用.");
       c.getSession().write(MaplePacketCreator.enableActions());
       return;
     }    
        
     if ((skillid == 5101007) && (((c.getPlayer().getGMLevel() == 0))))
     {
       c.getPlayer().cancelAllBuffs();
       c.getPlayer().dropMessage(6, "此技能有BUG,不能使用.");
       c.getSession().write(MaplePacketCreator.enableActions());
       return;
     } 
        
       if ((skillid == 1111002) && (((c.getPlayer().getGMLevel() == 0))))
     {
       c.getPlayer().cancelAllBuffs();
       c.getPlayer().dropMessage(6, "此技能有BUG,不能使用.");
       c.getSession().write(MaplePacketCreator.enableActions());
       return;
     } 
        
      if ((skillid == 11111001) && (((c.getPlayer().getGMLevel() == 0))))
     {
       c.getPlayer().cancelAllBuffs();
       c.getPlayer().dropMessage(6, "此技能有BUG,不能使用.");
       c.getSession().write(MaplePacketCreator.enableActions());
       return;
     } 

     if ((skillid == 5121003) && (c.getPlayer().getJob().getId() != 512))
     {
       c.getPlayer().cancelAllBuffs();
       c.getPlayer().dropMessage(1, "由于职业限制,无法使用这个技能.");
       c.getSession().write(MaplePacketCreator.enableActions());
       return;
     }
        
        if (skillLevel == 0 || skillLevel != __skillLevel) {
            log.info("[异常] {} 玩家使用了等级为0的技能", chr.getName());
            c.disconnect();
            return;
        } else {
            if (c.getPlayer().isAlive()) {
                if (skillid == 9001004 && c.getPlayer().isGM()) {
                    c.getPlayer().setHidden(!c.getPlayer().isHidden());
                }
                if (skill.getId() != 2311002 || c.getPlayer().canDoor()) {
                    skill.getEffect(skillLevel).applyTo(c.getPlayer(), pos);
                    if (skill.getId() != 2301002 && effect != null && effect.getMpCon() != 0) {
                        if (c.getPlayer().getMp() - beforeMp < skill.getEffect(skillLevel).getMpCon()) {
                            int remainingMp = beforeMp - skill.getEffect(skillLevel).getMpCon();
                            c.getPlayer().setMp(remainingMp);
                            c.getPlayer().updateSingleStat(MapleStat.MP, remainingMp);
                        }
                    }
                } else {
                    new ServernoticeMapleClientMessageCallback(5, c).dropMessage("请等候5秒再使用时空门!");
                    c.getSession().write(MaplePacketCreator.enableActions());
                }
            } else {
                c.getSession().write(MaplePacketCreator.enableActions());
            }
        }
        c.getSession().write(MaplePacketCreator.enableActions());
    }
}