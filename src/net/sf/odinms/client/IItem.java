
package net.sf.odinms.client;

import java.sql.Timestamp;
import java.util.List;

public interface IItem extends Comparable<IItem> {

    public final int PET = 3;
    public final int ITEM = 2;
    public final int EQUIP = 1;
    public void setFlag(byte b);

    byte getFlag();

    byte getType();

    byte getPosition();

    void setPosition(byte position);

    int getItemId();

    short getQuantity();

    String getOwner();

    int getPetId();

    IItem copy();

    void setOwner(String owner);

    void setQuantity(short quantity);

    public void log(String msg, boolean fromDB);

    List<String> getLog();

    Timestamp getExpiration();

    void setExpiration(Timestamp expire);

    int getSN();

    int getUniqueId();

    void setUniqueId(int id);

    void setSN(int sn);
}