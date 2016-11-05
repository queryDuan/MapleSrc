package net.sf.odinms.net.channel.handler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Timestamp;
import net.sf.odinms.client.IItem;
import net.sf.odinms.client.MapleCSInventoryItem;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.client.MapleInventory;
import net.sf.odinms.client.MapleInventoryType;
import net.sf.odinms.client.MaplePet;
import net.sf.odinms.client.MapleRing;
import net.sf.odinms.database.DatabaseConnection;
import net.sf.odinms.net.AbstractMaplePacketHandler;
import net.sf.odinms.net.channel.set;
import net.sf.odinms.server.AutobanManager;
import net.sf.odinms.server.CashItemFactory;
import net.sf.odinms.server.CashItemInfo;
import net.sf.odinms.server.MapleInventoryManipulator;
import net.sf.odinms.server.MapleItemInformationProvider;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Acrylic (Terry Han)
 */
public class CashShopHandler extends AbstractMaplePacketHandler {

    private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CashShopHandler.class);

    @Override
    public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
    set item_id = new set();
    String[] itembp_id = item_id.getItempb_id();
        int action = slea.readByte();
        int accountId = c.getAccID();
        if (action == 3) { //购买物品
            int useNX = slea.readByte();
            int snCS = slea.readInt();
            CashItemInfo item = CashItemFactory.getItem(snCS);
      for (int i = 0; i < itembp_id.length; i++)
      {
        if (item.getItemId() != Integer.parseInt(itembp_id[i]))
          continue;
        c.getPlayer().getCSInventory().saveToDB();
        c.getSession().write(MaplePacketCreator.enableCSorMTS());
        c.getSession().write(MaplePacketCreator.showNXMapleTokens(c.getPlayer()));
        c.getSession().write(MaplePacketCreator.enableActions());
        c.getPlayer().dropMessage(1, "这个物品是禁止购买的.");
        return;
      }
            if (c.getPlayer().getCSPoints(useNX) >= item.getPrice()) {
                c.getPlayer().modifyCSPoints(useNX, -item.getPrice());
            } else {
                c.getSession().write(MaplePacketCreator.enableActions());
                AutobanManager.getInstance().autoban(c, "试图购买现金物品，但是没有足够的点券。");
                return;
            }
            if(item.getPrice() < 80){//检测物品的价格 低于80就不让购买。但只限于点装类，如果不是点装好像就不好用了~
                c.getPlayer().getClient().getSession().write(MaplePacketCreator.serverNotice(1, "您购买的物品非法\r\n请勿使用非法的WZ文件."));
                         return;
              }
            if(item.getPrice() == 0){//检测物品的价格为0就返回不处理，你可以自定义添加其它的了。
                               c.getPlayer().getClient().getSession().write(MaplePacketCreator.serverNotice(1, "[提示:]此物品已经被管理员限制购买."));
                    return;
            }//这里屏掉了经验卡和小黑板  大家可以自由发挥~
            if (item.getItemId() == 5370000 || item.getItemId() == 5000028|| item.getItemId() == 5000029 || item.getItemId() == 5570000 || item.getItemId() == 5370001 || item.getItemId() == 5221000 || item.getItemId() ==5281000 || item.getItemId() ==5200000 || item.getItemId() ==5210001 || item.getItemId() ==5200002 ||  item.getItemId() ==5200001 || item.getItemId() ==5210005 || item.getItemId() ==5220003 || item.getItemId() ==1112012 || item.getItemId() ==1112006 || item.getItemId() ==1112801 || item.getItemId() ==1112802 || item.getItemId() ==5580000 || item.getItemId() ==5281001 || item.getItemId() ==5360016 || item.getItemId() ==1112001 || item.getItemId() ==1112002 || item.getItemId() ==5000047 || item.getItemId() ==5000048 || item.getItemId() >= 4100000 && item.getItemId() <= 4100006) {
               c.getPlayer().getClient().getSession().write(MaplePacketCreator.serverNotice(1, "[提示:]此物品已经被管理员限制购买."));
                    return;
               }
            if (item.getItemId() >= 5000000 && item.getItemId() <= 5000100) {
                int petId = MaplePet.createPet(item.getItemId(), c.getPlayer());
                if (petId == -1) {
                    return;
                }
                MapleCSInventoryItem citem = new MapleCSInventoryItem(petId, item.getItemId(), snCS, (short) item.getCount(), false);
                long period = 90;
                Timestamp ExpirationDate = new Timestamp(System.currentTimeMillis() + (period * 24 * 60 * 60 * 1000));
                citem.setExpire(ExpirationDate);
                c.getPlayer().getCSInventory().addItem(citem);
                c.getSession().write(MaplePacketCreator.enableCSorMTS());
                c.getSession().write(MaplePacketCreator.showBoughtCSItem(c, citem));
            } else {
                MapleCSInventoryItem citem = new MapleCSInventoryItem(MapleCharacter.getNextUniqueId(), item.getItemId(), snCS, (short) item.getCount(), false);
                long period = item.getPeriod();
                Timestamp ExpirationDate = new Timestamp(System.currentTimeMillis());
                ExpirationDate = new Timestamp(((System.currentTimeMillis() / 1000) + (period * 24 * 60 * 60)) * 1000);
                if (period == 0) {
                    ExpirationDate = null;
                }
                if (item.getItemId() == 5211047 || item.getItemId() == 5360014) {
                    ExpirationDate = new Timestamp(((System.currentTimeMillis() / 1000) + (3 * 60 * 60)) * 1000);
                }
                citem.setExpire(ExpirationDate);
                c.getPlayer().getCSInventory().addItem(citem);
                c.getSession().write(MaplePacketCreator.showBoughtCSItem(c, citem));
            }
            c.getPlayer().saveToDB(true);
            c.getSession().write(MaplePacketCreator.showNXMapleTokens(c.getPlayer()));
            c.getSession().write(MaplePacketCreator.enableActions());
        } else if (action == 4) { //赠送礼物
            int snCS = slea.readInt();
            int type = slea.readByte();
            String recipient = slea.readMapleAsciiString();
            String message = slea.readMapleAsciiString();
            CashItemInfo item = CashItemFactory.getItem(snCS);
            if (item.getItemId() == 5370000 || item.getItemId() == 5000028|| item.getItemId() == 5000029 || item.getItemId() == 5370001 || item.getItemId() == 5221000 || item.getItemId() == 5570000 || item.getItemId() ==5281000 || item.getItemId() ==5200000 || item.getItemId() ==5210001 || item.getItemId() ==5200002 ||  item.getItemId() ==5200001 || item.getItemId() ==5210005 || item.getItemId() ==5220003 || item.getItemId() ==1112012 || item.getItemId() ==1112006 || item.getItemId() ==1112801 || item.getItemId() ==1112802 || item.getItemId() ==5580000 || item.getItemId() ==5281001 || item.getItemId() ==5360016 || item.getItemId() ==1112001 || item.getItemId() ==1112002 || item.getItemId() ==5000047 || item.getItemId() ==5000048 || item.getItemId() >= 4100000 && item.getItemId() <= 4100006) {
               c.getPlayer().getClient().getSession().write(MaplePacketCreator.serverNotice(1, "[提示:]此物品已经被管理员限制赠送.\r\n请退出商城在进吧！"));
                return;
               }
             if (item.getItemId() != 123456 ) {
               c.getPlayer().getClient().getSession().write(MaplePacketCreator.serverNotice(1, "[提示:]赠送功能已经被屏蔽\r\n请退出商城在进吧！"));
                return;
               }
            if (c.getPlayer().getCSPoints(type) >= item.getPrice()) {
                if (MapleCharacter.getAccountIdByName(recipient) != -1) {
                    if (MapleCharacter.getAccountIdByName(recipient) == c.getPlayer().getAccountID()) {
                        c.getSession().write(MaplePacketCreator.showCannotToMe());

                    } else {
                        c.getPlayer().modifyCSPoints(type, -item.getPrice());
                        MapleCSInventoryItem gift = new MapleCSInventoryItem(0, item.getItemId(), snCS, (short) item.getCount(), true);
                        gift.setSender(c.getPlayer().getName());
                        gift.setMessage(message);
                        gift.setExpire(new Timestamp(((System.currentTimeMillis() / 1000) + (item.getPeriod() * 24 * 60 * 60)) * 1000));
                        try {
                            Connection con = DatabaseConnection.getConnection();
                            PreparedStatement ps = con.prepareStatement("INSERT INTO csgifts (accountid, itemid, sn, quantity, sender, message, expiredate) VALUES (?, ?, ?, ?, ?, ?, ?)");
                            ps.setInt(1, MapleCharacter.getAccountIdByName(recipient));
                            ps.setInt(2, item.getItemId());
                            ps.setInt(3, snCS);
                            ps.setInt(4, item.getCount());
                            ps.setString(5, c.getPlayer().getName());
                            ps.setString(6, message);
                            Timestamp ExpirationDate = new Timestamp(System.currentTimeMillis());
                            if ((item.getItemId() >= 5000000 && item.getItemId() <= 5000100) || item.getItemId() == 1112906 || item.getItemId() == 1112905) {
                                ExpirationDate = new Timestamp(((System.currentTimeMillis() / 1000) + (90 * 24 * 60 * 60)) * 1000);
                            } else if (item.getItemId() == 5211047 || item.getItemId() == 5360014) {
                                ExpirationDate = new Timestamp(((System.currentTimeMillis() / 1000) + (3 * 60 * 60)) * 1000);
                            } else if (item.getPeriod() != 0) {
                                ExpirationDate = new Timestamp(((System.currentTimeMillis() / 1000) + (item.getPeriod() * 24 * 60 * 60)) * 1000);
                            } else {
                                ExpirationDate = null;
                            }
                            ps.setTimestamp(7, ExpirationDate);
                            ps.executeUpdate();
                            ps.close();
                        } catch (SQLException se) {
                            log.error("Error saving gift to database", se);
                        }
                        c.getSession().write(MaplePacketCreator.getGiftFinish(c.getPlayer().getName(), item.getItemId(), (short) item.getCount()));
                    }
                } else {
                    c.getSession().write(MaplePacketCreator.showCheckName());
                }
            } else {
                c.getSession().write(MaplePacketCreator.enableActions());
                AutobanManager.getInstance().autoban(c, "试图购买现金物品，但是没有足够的点券。");
                return;
            }

            c.getPlayer().saveToDB(true);
            c.getSession().write(MaplePacketCreator.enableCSorMTS());
            c.getSession().write(MaplePacketCreator.showNXMapleTokens(c.getPlayer()));
            c.getSession().write(MaplePacketCreator.enableActions());
        } else if (action == 5) {
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("DELETE FROM wishlist WHERE charid = ?");
                ps.setInt(1, c.getPlayer().getId());
                ps.executeUpdate();
                ps.close();

                int i = 10;
                while (i > 0) {
                    int sn = slea.readInt();
                    if (sn != 0) {
                        ps = con.prepareStatement("INSERT INTO wishlist(charid, sn) VALUES(?, ?) ");
                        ps.setInt(1, c.getPlayer().getId());
                        ps.setInt(2, sn);
                        ps.executeUpdate();
                        ps.close();
                    }
                    i--;
                }
            } catch (SQLException se) {
                log.error("Wishlist SQL Error", se);
            }
            c.getSession().write(MaplePacketCreator.updateWishList(c.getPlayer().getId()));
        } else if (action == 6) { //扩充仓库
            int useNX = slea.readByte();
            byte add = slea.readByte();
            if (add == 0) {
                byte type = slea.readByte();
                MapleInventoryType invtype = MapleInventoryType.getByType(type);
                byte slots = c.getPlayer().getInventory(invtype).getSlots();
                if (c.getPlayer().getCSPoints(useNX) < 600) {
                    c.getSession().write(MaplePacketCreator.enableCSorMTS());
                    c.getSession().write(MaplePacketCreator.showNXMapleTokens(c.getPlayer()));
                    c.getSession().write(MaplePacketCreator.enableActions());
                    return;
                }
                if (slots <= 92) {
                    c.getPlayer().modifyCSPoints(useNX, -600);
                    c.getPlayer().getInventory(invtype).setSlotLimit((byte) (slots + 4));
                    c.getSession().write(MaplePacketCreator.serverNotice(1, "扩充成功."));
                } else {
                    c.getSession().write(MaplePacketCreator.serverNotice(1, "您无法继续进行扩充."));
                }
            } else if (add == 1) {
                int sn = slea.readInt();
                byte type = 1;
                switch (sn) {
                    case 50200018:
                        type = 1;
                        break;
                    case 50200019:
                        type = 2;
                        break;
                    case 50200020:
                        type = 3;
                        break;
                    case 50200021:
                        type = 4;
                        break;
                    case 50200043:
                        type = 5;
                        break;
                }
                MapleInventoryType invtype = MapleInventoryType.getByType(type);
                byte slots = c.getPlayer().getInventory(invtype).getSlots();
                if (c.getPlayer().getCSPoints(useNX) < 1100) {
                    c.getSession().write(MaplePacketCreator.enableCSorMTS());
                    c.getSession().write(MaplePacketCreator.showNXMapleTokens(c.getPlayer()));
                    c.getSession().write(MaplePacketCreator.enableActions());
                    return;
                }
                if (slots <= 86) {
                    c.getPlayer().modifyCSPoints(useNX, -1100);
                    c.getPlayer().getInventory(invtype).setSlotLimit((byte) (slots + 8));
                    c.getSession().write(MaplePacketCreator.serverNotice(1, "扩充成功."));
                } else {
                    c.getSession().write(MaplePacketCreator.serverNotice(1, "您无法继续进行扩充."));
                }
            }
            c.getPlayer().saveToDB(true);
            c.getSession().write(MaplePacketCreator.enableCSorMTS());
            c.getSession().write(MaplePacketCreator.showNXMapleTokens(c.getPlayer()));
            c.getSession().write(MaplePacketCreator.enableActions());
        } else if (action == 0x0D) { //商城=>包裹
            int uniqueid = slea.readInt(); //csid.. not like we need it anyways
            slea.readInt();//0
            slea.readByte();//0
            byte type = slea.readByte();
            byte unknown = slea.readByte();
            IItem item = c.getPlayer().getCSInventory().getItem(uniqueid).toItem();
            if (item != null) {
                byte slot = c.getPlayer().getInventory(MapleItemInformationProvider.getInstance().getInventoryType(item.getItemId())).getNextFreeSlot();
                if (slot == -1) {
                    c.getSession().write(MaplePacketCreator.serverNotice(1, "您的包裹已满."));
                } else {
                    c.getPlayer().getInventory(MapleItemInformationProvider.getInstance().getInventoryType(item.getItemId())).addItem(item);
                    c.getPlayer().getCSInventory().removeItem(uniqueid);
                    c.getSession().write(MaplePacketCreator.transferFromCSToInv(item, slot));
                }
            } else {
            }
            c.getPlayer().saveToDB(true);
            c.getSession().write(MaplePacketCreator.enableCSorMTS());
            c.getSession().write(MaplePacketCreator.showNXMapleTokens(c.getPlayer()));
            c.getSession().write(MaplePacketCreator.enableActions());
        } else if (action == 0x0E) { //包裹=>商城
            int uniqueid = slea.readInt();
            slea.readInt();//0
            slea.readByte(); //1?

            IItem item = null;
            for (MapleInventory inventory : c.getPlayer().getAllInventories()) {
                item = inventory.findByUniqueId(uniqueid);
                if (item != null) {

                    MapleCSInventoryItem citem = new MapleCSInventoryItem(item.getUniqueId(), item.getItemId(), CashItemFactory.getSnFromId(item.getItemId()), item.getQuantity(), false);
                    citem.setExpire(item.getExpiration());
                    c.getPlayer().getCSInventory().addItem(citem);

                    inventory.removeItem(item.getPosition(), item.getQuantity(), false);
                    c.getSession().write(MaplePacketCreator.transferFromInvToCS(c.getPlayer(), citem));
                    break;
                }
            }
            c.getPlayer().saveToDB(true);
            c.getSession().write(MaplePacketCreator.enableCSorMTS());
            c.getSession().write(MaplePacketCreator.showNXMapleTokens(c.getPlayer()));
            c.getSession().write(MaplePacketCreator.enableActions());
          } else if (action == 0x21) { //购买任务物品
            int snCS = slea.readInt();
            CashItemInfo item = CashItemFactory.getItem(snCS);
            if (c.getPlayer().getCSPoints(0) >= 999000000) {//抵用券,你也可以改成其它的
                c.getPlayer().modifyCSPoints(0, -999000000);
                MapleInventoryManipulator.addById(c, item.getItemId(), (short) item.getCount(), "购买了任务物品");
                MapleInventory etcInventory = c.getPlayer().getInventory(MapleInventoryType.ETC);
                byte slot = etcInventory.findById(item.getItemId()).getPosition();
                c.getSession().write(MaplePacketCreator.showBoughtCSQuestItem(slot, item.getItemId()));
            } else {
                c.getSession().write(MaplePacketCreator.enableActions());
                c.getPlayer().getClient().getSession().write(MaplePacketCreator.serverNotice(1, "您购买的物品非法\r\n请勿使用非法的WZ文件."));
                return;
            }
            c.getSession().write(MaplePacketCreator.showNXMapleTokens(c.getPlayer()));
            c.getSession().write(MaplePacketCreator.enableCSorMTS());
            c.getSession().write(MaplePacketCreator.enableActions());
            return;
        } else if (action == 0x1F) { //购买礼包
            int type = slea.readByte();
            int snCS = slea.readInt();
            CashItemInfo cashPackage = CashItemFactory.getItem(snCS);
            if (c.getPlayer().getCSPoints(type) >= cashPackage.getPrice()) {
                c.getPlayer().modifyCSPoints(type, -cashPackage.getPrice());
            } else {
                c.getSession().write(MaplePacketCreator.enableActions());
                return;
            }
            for (CashItemInfo item : CashItemFactory.getPackageItems(cashPackage.getItemId())) {
                if (item.getItemId() >= 5000000 && item.getItemId() <= 5000100) {
                    int petId = MaplePet.createPet(item.getItemId(), c.getPlayer());
                    if (petId == -1) {
                        return;
                    }
                    MapleCSInventoryItem citem = new MapleCSInventoryItem(petId, item.getItemId(), snCS, (short) item.getCount(), false);
                    long period = 90;
                    Timestamp ExpirationDate = new Timestamp(System.currentTimeMillis() + (period * 24 * 60 * 60 * 1000));
                    citem.setExpire(ExpirationDate);
                    c.getPlayer().getCSInventory().addItem(citem);
                    c.getSession().write(MaplePacketCreator.showBoughtCSItem(c, citem));
                } else {
                    MapleCSInventoryItem citem = new MapleCSInventoryItem(MapleCharacter.getNextUniqueId(), item.getItemId(), snCS, (short) item.getCount(), false);
                    long period = item.getPeriod();
                    Timestamp ExpirationDate = new Timestamp(System.currentTimeMillis());
                    ExpirationDate = new Timestamp(((System.currentTimeMillis() / 1000) + (period * 24 * 60 * 60)) * 1000);
                    if (period == 0) {
                        ExpirationDate = null;
                    }
                    if (item.getItemId() == 5211047 || item.getItemId() == 5360014) {
                        ExpirationDate = new Timestamp(((System.currentTimeMillis() / 1000) + (3 * 60 * 60)) * 1000);
                    }
                    if (item.getItemId() == 1112906 && item.getItemId() == 1112905) {
                        ExpirationDate = new Timestamp(((System.currentTimeMillis() / 1000) + (period * 24 * 60 * 60)) * 1000);
                    }
                    citem.setExpire(ExpirationDate);
                    c.getPlayer().getCSInventory().addItem(citem);
                    c.getSession().write(MaplePacketCreator.showBoughtCSItem(c, citem));
                }
                c.getPlayer().getCSInventory().saveToDB();
            }
            c.getPlayer().saveToDB(true);
            c.getSession().write(MaplePacketCreator.enableCSorMTS());
            c.getSession().write(MaplePacketCreator.showNXMapleTokens(c.getPlayer()));
            c.getSession().write(MaplePacketCreator.enableActions());
               } else if (action == 0x1D) { //购买结婚戒指相关

			c.getSession().write(MaplePacketCreator.serverNotice(1, "恋人戒指无法使用,已被管理员禁止购买"));
			c.getSession().write(MaplePacketCreator.enableActions());
	    } else if (action == 36) {
             int snCS = slea.readInt();
       String recipient = slea.readMapleAsciiString();
       String message = slea.readMapleAsciiString();
       CashItemInfo item = CashItemFactory.getItem(snCS);
       int tempid = MapleCharacter.getIdByName(recipient);
       try {
         int cid = c.getPlayer().getId();
         int temp = 0;
         Connection con1 = DatabaseConnection.getConnection();
         PreparedStatement limitCheck = con1.prepareStatement("SELECT * FROM rings WHERE partnerChrId = " + cid + "|| partnerChrId =" + tempid);
         ResultSet rs = limitCheck.executeQuery();
         if (rs.next())
         {
           temp = rs.getInt("id");
        }
         if (temp > 0) {
           rs.close();
           limitCheck.close();
           c.getSession().write(MaplePacketCreator.serverNotice(1, "您或您的同伴已经有一个戒指了"));
           c.getSession().write(MaplePacketCreator.enableActions());
           return;
         }
        rs.close();
         limitCheck.close();
      } catch (SQLException se) {
         log.error("Error saving gift to database", se);
       }

       if (c.getPlayer().getCSPoints(0) >= item.getPrice()) {
         if (MapleCharacter.getAccountIdByName(recipient) != -1) {
           c.getPlayer().modifyCSPoints(0, -item.getPrice());
          MapleCSInventoryItem gift = new MapleCSInventoryItem(0, item.getItemId(), snCS, (short)item.getCount(), true);
          MapleCSInventoryItem citem = new MapleCSInventoryItem(MapleCharacter.getNextUniqueId(), item.getItemId(), snCS, (short)item.getCount(), false);
          gift.setSender(c.getPlayer().getName());
           gift.setMessage(message);
           gift.setExpire(new Timestamp((System.currentTimeMillis() / 1000L + item.getPeriod() * 24 * 60 * 60) * 1000L));
          try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO csgifts (accountid, itemid, sn, quantity, sender, message, expiredate) VALUES (?, ?, ?, ?, ?, ?, ?)");
            ps.setInt(1, MapleCharacter.getAccountIdByName(recipient));
             ps.setInt(2, item.getItemId());
             ps.setInt(3, snCS);
             ps.setInt(4, item.getCount());
             ps.setString(5, c.getPlayer().getName());
            ps.setString(6, message);
             ps.setTimestamp(7, null);
            ps.executeUpdate();
             ps.close();
           } catch (SQLException se) {
             log.error("Error saving gift to database", se);
          }
          c.getPlayer().getCSInventory().addItem(citem);
          c.getSession().write(MaplePacketCreator.showBoughtCSItem(c, citem));
           MapleRing.createRing(MapleCharacter.getNextUniqueId(), MapleCharacter.getNextUniqueId() + 1, c.getPlayer().getId(), MapleCharacter.getIdByName(recipient), c.getPlayer().getName(), recipient);
         } else {
           c.getSession().write(MaplePacketCreator.serverNotice(1, "你输入的角色名不存在"));
        }
       } else {
         c.getSession().write(MaplePacketCreator.serverNotice(1, "您没有足够的点券用来购买."));
         c.getSession().write(MaplePacketCreator.enableActions());
         return;
       }
       c.getPlayer().getCSInventory().saveToDB();
      c.getSession().write(MaplePacketCreator.enableCSorMTS());
      c.getSession().write(MaplePacketCreator.showNXMapleTokens(c.getPlayer()));
       c.getSession().write(MaplePacketCreator.enableActions());
     }
     c.getPlayer().getCSInventory().saveToDB();
     c.getSession().write(MaplePacketCreator.enableCSorMTS());
    c.getSession().write(MaplePacketCreator.showNXMapleTokens(c.getPlayer()));
    c.getSession().write(MaplePacketCreator.enableActions());
   }
 }