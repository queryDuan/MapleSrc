package net.sf.odinms.scripting.npc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.script.Invocable;

import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.world.MaplePartyCharacter;
import net.sf.odinms.scripting.AbstractScriptManager;

/**
 *
 * @author Matze
 */
public class NPCScriptManager extends AbstractScriptManager {

    private Map<MapleClient, NPCConversationManager> cms = new HashMap<MapleClient, NPCConversationManager>();
    private Map<MapleClient, NPCScript> scripts = new HashMap<MapleClient, NPCScript>();
    private static NPCScriptManager instance = new NPCScriptManager();

    public synchronized static NPCScriptManager getInstance() {
        return instance;
    }

    public void start(MapleClient c, int npc) {
         try {
        if (c.getPlayer().isGM())
         System.out.println("NPC:"+npc+"已执行.");
         NPCConversationManager cm = new NPCConversationManager(c, npc);
         cm.dispose(); //加这个可防止NPC假死
         if (cms.containsKey(c)) {
         return;
          }
            cms.put(c, cm);
            Invocable iv = getInvocable("npc/" + npc + ".js", c);
            if (iv == null || NPCScriptManager.getInstance() == null) {
                if (iv == null) { // wow this is horrible
                    cm.sendOk("#b乐章网络,因为专业,所以信赖.");
                }
                cm.dispose();
                return;
            }
            engine.put("cm", cm);
            NPCScript ns = iv.getInterface(NPCScript.class);
            scripts.put(c, ns);
            ns.start();
        } catch (Exception e) {
            System.out.println("npc脚本错误请检查"+npc+"的脚本.");
            dispose(c);
            cms.remove(c);
        }
    }
  public void starts(MapleClient c, int npc) {
        try {
        if (c.getPlayer().isGM())
            System.out.println("NPC:"+npc+"已执行.");
            NPCConversationManager cm = new NPCConversationManager(c, npc);
            cm.dispose();
            
            if (cms.containsKey(c)) {
                return;
            }
            cms.put(c, cm);
            Invocable iv = getInvocable("npc/" + npc + "-.js", c);
            if (iv == null || NPCScriptManager.getInstance() == null) {
                if (iv == null) { // wow this is horrible
                    cm.sendOk("#b乐章网络,因为专业,所以信赖.");
                }
                cm.dispose();
                return;
            }
            engine.put("cm", cm);
            NPCScript ns = iv.getInterface(NPCScript.class);
            scripts.put(c, ns);
            ns.start();
        } catch (Exception e) {
            System.out.println("npc脚本错误请检查"+npc+"的脚本.");
            dispose(c);
            cms.remove(c);
        }
    }
    public void start(String filename, MapleClient c, int npc, List<MaplePartyCharacter> chars) { // CPQ start
        try {
            NPCConversationManager cm = new NPCConversationManager(c, npc, chars, 0);
            cm.dispose();
            
            if (cms.containsKey(c)) {
                return;
            }
            cms.put(c, cm);
            Invocable iv = getInvocable("npc/" + filename + ".js", c);
            NPCScriptManager npcsm = NPCScriptManager.getInstance();
            if (iv == null || NPCScriptManager.getInstance() == null || npcsm == null) {
                cm.dispose();
                return;
            }
            engine.put("cm", cm);
            NPCScript ns = iv.getInterface(NPCScript.class);
            scripts.put(c, ns);
            ns.start(chars);
        } catch (Exception e) {
            log.error("npc脚本错误 " + filename, e);
            dispose(c);
            cms.remove(c);

        }
    }

    public void action(MapleClient c, byte mode, byte type, int selection) {
        NPCScript ns = scripts.get(c);
        if (ns != null) {
            try {
                ns.action(mode, type, selection);
            } catch (Exception e) {
                log.error("npc脚本错误 " + c.getCM().getNpc(), e);
                dispose(c);
            }
        }
    }

    public void dispose(NPCConversationManager cm) {
        MapleClient c = cm.getC();
        cms.remove(c);
        scripts.remove(c);
        resetContext("npc/" + cm.getNpc() + ".js", c);
    }
    
   public void disposes(NPCConversationManager cm) {
        MapleClient c = cm.getC();
        cms.remove(c);
        scripts.remove(c);
        resetContext("npc/" + cm.getNpc() + "-.js", c);
    }

    public void dispose(MapleClient c) {
        NPCConversationManager npccm = cms.get(c);
        if (npccm != null) {
            dispose(npccm);
        }
    }

    public NPCConversationManager getCM(MapleClient c) {
        return cms.get(c);
    }
}