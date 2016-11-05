package net.sf.odinms.server;

import java.util.ArrayList;
import java.util.List;
import net.sf.odinms.tools.Pair;

/**
 *
 * @author Patrick/PurpleMadness
 */
public class MapleAchievements {

    private List<Pair<Integer, MapleAchievement>> achievements = new ArrayList<Pair<Integer, MapleAchievement>>();
    private static MapleAchievements instance = null;

    protected MapleAchievements() {
        achievements.add(new Pair<Integer, MapleAchievement>(3, new MapleAchievement("等级到达30级", 300, true, false)));
        achievements.add(new Pair<Integer, MapleAchievement>(4, new MapleAchievement("等级到达70级", 500, true, false)));
        achievements.add(new Pair<Integer, MapleAchievement>(5, new MapleAchievement("等级到达120级", 1000, true, false)));
        achievements.add(new Pair<Integer, MapleAchievement>(7, new MapleAchievement("首次成功创建家族", 800, true, false)));
        achievements.add(new Pair<Integer, MapleAchievement>(9, new MapleAchievement("首次人气达到50点", 1000, true, false)));
        achievements.add(new Pair<Integer, MapleAchievement>(12, new MapleAchievement("首次成功砸卷", 500, true, false)));
        achievements.add(new Pair<Integer, MapleAchievement>(22, new MapleAchievement("到达200级", 2000, true, false)));
        achievements.add(new Pair<Integer, MapleAchievement>(26, new MapleAchievement("首次击败暗黑龙王", 2000, true, false)));
    }

    public static MapleAchievements getInstance() {
        if (instance == null) {
            instance = new MapleAchievements();
        }
        return instance;
    }

    public MapleAchievement getById(int id) {
        for (Pair<Integer, MapleAchievement> achievement : this.achievements) {
            if (achievement.getLeft() == id) {
                return achievement.getRight();
            }
        }
        return null;
    }

    public Integer getByMapleAchievement(MapleAchievement ma) {
        for (Pair<Integer, MapleAchievement> achievement : this.achievements) {
            if (achievement.getRight() == ma) {
                return achievement.getLeft();
            }
        }
        return null;
    }
}