package net.sf.odinms.client.messages.commands;

import java.util.Arrays;
import java.util.List;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import net.sf.odinms.server.life.MapleMonster;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.server.maps.MapleMapObjectType;

public class MonsterInfoCommands implements Command {

    @Override
    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception, IllegalCommandSyntaxException {
        if (splitted[0].equals("!杀") || splitted[0].equals("!monsterdebug")) {
            String mapMessage = "";
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;
            List<MapleMapObject> monsters = map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER));
            boolean kill = splitted[0].equals("!杀");
            for (MapleMapObject monstermo : monsters) {
                MapleMonster monster = (MapleMonster) monstermo;
                if (kill) {
                    map.killMonster(monster, c.getPlayer(), true);
                    monster.giveExpToCharacter(c.getPlayer(), monster.getExp(), true, 1);
                } else {
                    mc.dropMessage("Monster " + monster.toString());
                }
            }
            if (kill) {
                mc.dropMessage("杀死了 " + monsters.size() + " 个怪物" + mapMessage + ".");
            }
        } else if (splitted[0].equals("!killalldrops")) {
            MapleMap map = c.getPlayer().getMap();
            map.killAllMonsters();
        }
    }

    @Override
    public CommandDefinition[] getDefinition() {
        return new CommandDefinition[]{
                    new CommandDefinition("杀", "", "Kills all monsters", 3),
                    new CommandDefinition("killalldrops", "", "Kills all monsters with drops", 5),
                    new CommandDefinition("monsterdebug", "", "", 5)
                };
    }
}