

package net.sf.odinms.server.playerinteractions;

import net.sf.odinms.client.IItem;

/**
 *
 * @author Matze
 */
public class MaplePlayerShopItem {

    private IItem item;
    private short bundles;
    private int price;

    public MaplePlayerShopItem(IItem item, short bundles, int price) {
        this.item = item;
        this.bundles = bundles;
        this.price = price;
    }

    public IItem getItem() {
        return item;
    }

    public short getBundles() {
        return bundles;
    }

    public int getPrice() {
        return price;
    }

    public void setBundles(short bundles) {
        this.bundles = bundles;
    }
}
