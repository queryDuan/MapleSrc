package net.sf.odinms.server.maps;

import java.awt.Point;

import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.anticheat.CheatingOffense;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.scripting.portal.PortalScriptManager;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.server.fourthjobquests.FourthJobQuestsPortalHandler;
import net.sf.odinms.tools.MaplePacketCreator;

public class MapleGenericPortal implements MaplePortal {

    private String name;
    private String target;
    private Point position;
    private int targetmap;
    private int type;
    private boolean status = true;
    private int id;
    private String scriptName;
    private boolean portalState;

    public MapleGenericPortal(int type) {
        this.type = type;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    public void setPortalStatus(boolean newStatus) {
        this.status = newStatus;
    }

    @Override
    public boolean getPortalStatus() {
        return status;
    }

    @Override
    public int getTargetMapId() {
        return targetmap;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public String getScriptName() {
        return scriptName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setTargetMapId(int targetmapid) {
        this.targetmap = targetmapid;
    }

    @Override
    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    @Override
    public void enterPortal(MapleClient c) {
        MapleCharacter player = c.getPlayer();
        double distanceSq = getPosition().distanceSq(player.getPosition());
        if (distanceSq > 22500) {
            player.getCheatTracker().registerOffense(CheatingOffense.USING_FARAWAY_PORTAL, "D" + Math.sqrt(distanceSq));
        }
        boolean changed = false;
        if (getScriptName() != null) {
            if (!FourthJobQuestsPortalHandler.handlePortal(getScriptName(), c.getPlayer())) {
                changed = PortalScriptManager.getInstance().executePortalScript(this, c);
            }
        } else if (getTargetMapId() != 999999999) {
            MapleMap to;
            if (player.getEventInstance() == null) {
                to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(getTargetMapId());
            } else {
                to = player.getEventInstance().getMapInstance(getTargetMapId());
            }
            MaplePortal pto = to.getPortal(getTarget());
            if (pto == null) { // fallback for missing portals - no real life case anymore - intresting for not implemented areas
                pto = to.getPortal(0);
            }
            if (getTargetMapId() ==30001 ||getTargetMapId() ==910000019 ||getTargetMapId() ==910000020 ||getTargetMapId() ==910000021 ||getTargetMapId() ==910000022 ||getTargetMapId() ==541010100 ||getTargetMapId() ==541010050 ||getTargetMapId() ==541010040 ||getTargetMapId() ==541010010) {
            c.getSession().write(MaplePacketCreator.enableActions());
            player.getClient().getSession().write(MaplePacketCreator.serverNotice(1, "[公告]请通过npc进入!"));
            } else if (getTargetMapId() == 910000004 && player.getVip() < 2) {
            c.getSession().write(MaplePacketCreator.enableActions());
            player.getClient().getSession().write(MaplePacketCreator.serverNotice(5, "会员等级不足,不能进入更高级的刷怪地图."));
            } else if (getTargetMapId() == 910000005 && player.getVip() < 3) {
            c.getSession().write(MaplePacketCreator.enableActions());
            player.getClient().getSession().write(MaplePacketCreator.serverNotice(5, "会员等级不足,不能进入更高级的刷怪地图."));
            } else if (getTargetMapId() == 910000006 && player.getVip() < 4) {
            c.getSession().write(MaplePacketCreator.enableActions());
            player.getClient().getSession().write(MaplePacketCreator.serverNotice(5, "会员等级不足,不能进入更高级的刷怪地图."));
            
            } else {
            c.getPlayer().changeMap(to, pto); //late resolving makes this harder but prevents us from loading the whole world at once
            }
            changed = true;
        }
        if (!changed) {
            c.getSession().write(MaplePacketCreator.enableActions());
        }
    }

    public void setPortalState(boolean state) {
        this.portalState = state;
    }

    public boolean getPortalState() {
        return portalState;
    }
}
