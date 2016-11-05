package net.sf.odinms.net.channel.handler;

import java.awt.Point;
import java.util.List;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.MaplePacket;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.life.MobSkill;
import net.sf.odinms.server.life.MobSkillFactory;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;
import net.sf.odinms.server.movement.LifeMovementFragment;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.Pair;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

public class MoveLifeHandler extends AbstractMovementPacketHandler {

    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MoveLifeHandler.class);

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        int objectid = slea.readInt();
        short moveid = slea.readShort();

        MapleMapObject mmo = c.getPlayer().getMap().getMapObject(objectid);
        if (mmo == null || mmo.getType() != MapleMapObjectType.MONSTER) {
            return;
        }
        MapleMonster monster = (MapleMonster) mmo;
        boolean noPacket = monster.isMoveLocked();

        List<LifeMovementFragment> res = null;
        int skillByte = slea.readByte();
        int skill = slea.readByte();
        int skill_1 = slea.readByte() & 0xFF;
        int skill_2 = slea.readByte();
        int skill_3 = slea.readByte();
        slea.readByte();

        MobSkill toUse = null;

        if (skillByte == 1 && monster.getNoSkills() > 0) {
            int random = (int) (Math.random() * monster.getNoSkills());
            Pair<Integer, Integer> skillToUse = monster.getSkills().get(random);
            toUse = MobSkillFactory.getMobSkill(skillToUse.getLeft(), skillToUse.getRight());
            if (!monster.canUseSkill(toUse)) {
                toUse = null;
            }
        }

        if (skill_1 >= 100 && skill_1 <= 200 && monster.hasSkill(skill_1, skill_2)) {
            MobSkill skillData = MobSkillFactory.getMobSkill(skill_1, skill_2);
            if (skillData != null && monster.canUseSkill(skillData)) {
                skillData.applyEffect(c.getPlayer(), monster, true);
            }
        }

        slea.readByte();
        slea.readInt(); // whatever
        slea.readLong();
        int start_x = slea.readShort(); // hmm.. startpos?
        int start_y = slea.readShort(); // hmm...
        Point startPos = new Point(start_x, start_y);

        res = parseMovement(slea);

        if (monster.getController() != c.getPlayer()) {
            if (monster.isAttackedBy(c.getPlayer())) { // aggro and controller change
                monster.switchController(c.getPlayer(), true);
            } else {
                return;
            }
        } else {
            if (skill == -1 && monster.isControllerKnowsAboutAggro() && !monster.isMobile()) {
                monster.setControllerHasAggro(false);
                monster.setControllerKnowsAboutAggro(false);
            }
            if (!monster.isFirstAttack()) {
                monster.setControllerHasAggro(true);
                monster.setControllerKnowsAboutAggro(true);
            }
        }
        boolean aggro = monster.isControllerHasAggro();
        if (toUse != null) {
            if (!noPacket) {
                c.getSession().write(MaplePacketCreator.moveMonsterResponse(objectid, moveid, monster.getMp(), aggro, toUse.getSkillId(), toUse.getSkillLevel()));
            }
        } else {
            if (!noPacket) {
                c.getSession().write(MaplePacketCreator.moveMonsterResponse(objectid, moveid, monster.getMp(), aggro));
            }
        }
        if (aggro) {
            monster.setControllerKnowsAboutAggro(true);
        }
        if (res != null) {
            //if (slea.available() != 9) {
                //log.warn("slea.available != 9 (movement parsing error)");
                //return;
            //}
            MaplePacket packet = MaplePacketCreator.moveMonster(skillByte, skill, skill_1, skill_2, skill_3, objectid, startPos, res);
            c.getPlayer().getMap().broadcastMessage(c.getPlayer(), packet, monster.getPosition());
            updatePosition(res, monster, -1);
            c.getPlayer().getMap().moveMonster(monster, monster.getPosition());
            c.getPlayer().getCheatTracker().checkMoveMonster(monster.getPosition());
        }
    }
}