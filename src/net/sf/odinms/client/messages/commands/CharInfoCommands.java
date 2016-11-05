package net.sf.odinms.client.messages.commands;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;

public class CharInfoCommands implements Command {

    @Override
    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception, IllegalCommandSyntaxException {
        if (splitted[0].equalsIgnoreCase("!坐标")) {
            mc.dropMessage("你当前的坐标是: " + c.getPlayer().getPosition().x + " x   " + c.getPlayer().getPosition().y + " y ");
        }
    }

    @Override
    public CommandDefinition[] getDefinition() {
        return new CommandDefinition[]{
                    new CommandDefinition("坐标", "", "Shows your character's coordinates", 1)
                };
    }
}