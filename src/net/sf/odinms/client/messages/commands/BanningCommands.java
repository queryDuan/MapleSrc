package net.sf.odinms.client.messages.commands;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collection;

import java.util.Iterator;
import java.util.LinkedHashSet;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleCharacterUtil;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.messages.Command;
import net.sf.odinms.client.messages.CommandDefinition;
import net.sf.odinms.client.messages.IllegalCommandSyntaxException;
import net.sf.odinms.client.messages.MessageCallback;
import static net.sf.odinms.client.messages.CommandProcessor.getNamedIntArg;
import static net.sf.odinms.client.messages.CommandProcessor.joinAfterString;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.server.MaplePortal;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.StringUtil;

public class BanningCommands implements Command {

    @Override
    public void execute(MapleClient c, MessageCallback mc, String[] splitted) throws Exception {
        ChannelServer cserv = c.getChannelServer();
        if ((splitted[0].equals("!封号")) || (splitted[0].equals("!封号2"))) {
      String playerid = splitted[1];
      String playerName = "";
      if (splitted.length < 3)
        throw new IllegalCommandSyntaxException(3);

      MapleCharacter target = null;
            Collection<ChannelServer> cservs = ChannelServer.getAllInstances();
            for (ChannelServer cserver : cservs) {
        if (splitted[0].equals("!封号"))
          target = cserver.getPlayerStorage().getCharacterById(Integer.parseInt(playerid));
        else
          target = cserver.getPlayerStorage().getCharacterByName(splitted[1]);

        if (target != null) {
          playerName = target.getName();
          break;
        }
      }
      String originalReason = StringUtil.joinStringFrom(splitted, 2);
      String reason = c.getPlayer().getName() + " 使用权限封停 " + playerName + "理由: " + originalReason;
      if (target != null) {
        if (target.getGMLevel() <= c.getPlayer().getGMLevel()) {
          String readableTargetName = MapleCharacterUtil.makeMapleReadable(target.getName());
          String ip = target.getClient().getSession().getRemoteAddress().toString().split(":")[0];
          reason = reason + " (IP: " + ip + ")";
          target.ban(reason);
          try {
            cserv.getWorldInterface().broadcastMessage(null, MaplePacketCreator.serverNotice(6, "[公告事项]" + readableTargetName + " 由于使用非法程序被永久封停处理。").getBytes());
          } catch (RemoteException e) {
            cserv.reconnectWorld();
          }
          mc.dropMessage(readableTargetName + "'连接IP： " + ip + ".");
        } else {
          mc.dropMessage("不可封停的管理员");
        }
      }
      else {
        int status = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        PreparedStatement psb = null;
        ResultSet rsb = null;
        try {
          Connection con = DatabaseConnection.getConnection();
          ps = con.prepareStatement("SELECT accountid, name FROM characters WHERE name = ?");
          ps.setString(1, playerName);
          rs = ps.executeQuery();
          if (rs.next()) {
            int accountid = rs.getInt("accountid");
            playerName = rs.getString("name");
            psb = con.prepareStatement("SELECT banned FROM accounts WHERE id = ?");
            psb.setInt(1, accountid);
            rsb = psb.executeQuery();
            rsb.next();
            if (rsb.getInt("banned") == 1)
              status = 1;

            rsb.close();
            psb.close();
          } else {
            status = -1;
          }
          rs.close();
          ps.close();
        } catch (SQLException ex) {
        } finally {
          try {
            if (rsb != null)
              rsb.close();

            if (psb != null)
              psb.close();

            if (rs != null)
              rs.close();

            if (ps != null)
              ps.close();
          }
          catch (SQLException ex) {
          }
        }
        if (status != 0) {
          if (status == 1)
            mc.dropMessage(playerName + "'帐号已成功封停。");
          else if (status == -1)
            mc.dropMessage("玩家： '" + playerName + "' 不存在。");

          return;
        }
        if (MapleCharacter.ban(playerName, reason, false)) {
          mc.dropMessage(playerName + "'帐号已成功离线封停。");
          try {
            cserv.getWorldInterface().broadcastMessage(c.getPlayer().getName(), MaplePacketCreator.serverNotice(6, playerName + " 已被禁止登陆游戏.").getBytes());
          } catch (RemoteException e) {
            cserv.reconnectWorld();
          }
        }
      }
    } else if (splitted[0].equalsIgnoreCase("!掉线")) {
            MapleCharacter victim = cserv.getPlayerStorage().getCharacterById(Integer.parseInt(splitted[1]));
            victim.getClient().disconnect();
            victim.getClient().getSession().close();
} else if (splitted[0].equals("!全部过来")) {
      Collection csss = ChannelServer.getAllInstances();
      for (Iterator i$ = csss.iterator(); i$.hasNext(); ) { ChannelServer cservers = (ChannelServer)i$.next();
        Collection cmc = new LinkedHashSet(cservers.getPlayerStorage().getAllCharacters());
        for (i$ = cmc.iterator(); i$.hasNext(); ) { MapleCharacter mch = (MapleCharacter)i$.next();
          if ((mch != null) && (mch.getMapId() != c.getPlayer().getMapId()))
            try {
              mch.changeMap(c.getPlayer().getMap(), c.getPlayer().getPosition());
            } catch (Exception e) {
            }
        }
      }
 } else if (splitted[0].equals("!解封")) {
      String playerName = splitted[1];

      PreparedStatement ps = null;
      ResultSet rs = null;
      PreparedStatement psb = null;
      ResultSet rsb = null;
      try {
        Connection con = DatabaseConnection.getConnection();
        ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ?");
        ps.setString(1, playerName);
        rs = ps.executeQuery();
        if (rs.next()) {
          int accountid = rs.getInt("accountid");
          psb = con.prepareStatement("SELECT banned, tempban FROM accounts WHERE id = ?");
          psb.setInt(1, accountid);
          rsb = psb.executeQuery();
          rsb.next();
          if ((rsb.getInt("banned") != 1) && (rsb.getLong("tempban") == 0L)) {
            rsb.close();
            psb.close();
            mc.dropMessage(playerName + " account is not banned.");

            return;
          }
          rsb.close();
          psb.close();
          psb = con.prepareStatement("UPDATE accounts SET banned = 0, banreason = null, tempban = '2008-08-08 00:00:00', greason = null WHERE id = ?");
          psb.setInt(1, accountid);
          psb.executeUpdate();
          psb.close();
          mc.dropMessage(playerName + "'：帐号已经成功取消封停.请检测账号是否能使用！.");
        } else {
          mc.dropMessage(playerName + " 不存在!");
        }
        rs.close();
        ps.close();
      } catch (SQLException ex) {
        System.out.println("SQL Exception: " + ex);
      }
      finally {
        try {
          if (rsb != null)
            rsb.close();

          if (psb != null)
            psb.close();

          if (rs != null)
            rs.close();

          if (ps != null)
            ps.close();
        }
        catch (SQLException ex)
        {
        }
      }
    }
  }


    @Override
    public CommandDefinition[] getDefinition() {
        return new CommandDefinition[]{
             new CommandDefinition("封号", "charname reason", "Permanently ip, mac and account ban the given character", 1),//巡查
             new CommandDefinition("封号2", "charname reason", "Permanently ip, mac and account ban the given character", 1),//巡查
             new CommandDefinition("全部过来", "charname reason", "Permanently ip, mac and account ban the given character", 1),//巡查
            // new CommandDefinition("掉线", "charname reason", "Permanently ip, mac and account ban the given character", 1),//巡查
             new CommandDefinition("解封", "<character name>", "Unbans the character's account", 5)
                };
    }
}