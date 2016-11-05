package net.sf.odinms.client;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Item implements IItem {

    private int id;
    private byte position;
    private short quantity;
    private int petid;
    private String owner = "";
    private int idfrom; //the character id that its from?
    private String sender = "";
    private String message = "";
    protected List<String> log;
    private byte flag;
    private Timestamp expiration;
    private int uniqueid;
    private List<Integer> petsCanConsume = new ArrayList<Integer>();
    private int sn;

    public Item(int id, byte position, short quantity) {
        super();
        this.id = id;
        this.position = position;
        this.quantity = quantity;
        this.flag = 0;
        this.petid = -1;
        this.log = new LinkedList<String>();
               /* MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance(); 
        if(ii.isCash(getItemId())==true){//检测是否是商城点装 
        setUniqueId(MapleCharacter.getNextUniqueId());//为物品ID设置UNIQUEID 
    }*/
    }

    public Item(int id, byte position, short quantity, int petid) {
        super();
        this.id = id;
        this.position = position;
        this.quantity = quantity;
        this.flag = 0;
        this.petid = petid;
        this.log = new LinkedList<String>();
               /* MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance(); 
        if(ii.isCash(getItemId())==true){//检测是否是商城点装 
        setUniqueId(MapleCharacter.getNextUniqueId());//为物品ID设置UNIQUEID 
    }*/
    }

    public IItem copy() {
        Item ret = new Item(id, position, quantity, petid);
        ret.owner = owner;
        ret.log = new LinkedList<String>(log);
        return ret;
    }

    public void setPosition(byte position) {
        this.position = position;
    }

    public void setQuantity(short quantity) {
        this.quantity = quantity;
    }

    @Override
    public int getItemId() {
        return id;
    }

    @Override
    public byte getPosition() {
        return position;
    }

    @Override
    public short getQuantity() {
        return quantity;
    }

    @Override
    public byte getType() {
        return IItem.ITEM;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public int getPetId() {
    return petid;
    }

    public byte getFlag() {
        return flag;
    }

    public void setFlag(byte b) {
        this.flag = b;
    }

    @Override
    public int compareTo(IItem other) {
        if (Math.abs(position) < Math.abs(other.getPosition())) {
            return -1;
        } else if (Math.abs(position) == Math.abs(other.getPosition())) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public String toString() {
        return "Item: " + id + " quantity: " + quantity;
    }

    // no op for now as it eats too much ram :( once we have persistent inventoryids we can reenable it in some form.
    public void log(String msg, boolean fromDB) {

    }

    public List<String> getLog() {
        return Collections.unmodifiableList(log);
    }

    @Override
    public Timestamp getExpiration() {
        return expiration;
    }

    public void setExpiration(Timestamp expire) {
        this.expiration = expire;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

    public int getSN() {
        return sn;
    }

    public void setSN(int sn) {
        this.sn = sn;
    }

    public int getUniqueId() {
        return uniqueid;
    }

    public void setUniqueId(int id) {
        this.uniqueid = id;
    }

    public void setMessage(String msg) {
        this.message = msg;
    }

    public String getMessage() {
        return message;
    }

    public void setPetsCanConsume(List<Integer> pets) {
        this.petsCanConsume = pets;
    }

    public List<Integer> getPetsCanConsume() {
        return Collections.unmodifiableList(petsCanConsume);
    }
}