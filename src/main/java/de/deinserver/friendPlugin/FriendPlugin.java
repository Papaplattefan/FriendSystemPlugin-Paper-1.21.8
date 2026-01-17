package de.deinserver.friendPlugin;

import org.bukkit.plugin.java.JavaPlugin;

public class FriendPlugin extends JavaPlugin {

    private static FriendPlugin instance;
    private FriendManager friendManager;

    @Override
    public void onEnable() {
        instance = this;
        this.friendManager = new FriendManager(this);

        getCommand("friend").setExecutor(new FriendCommand(friendManager));

        getLogger().info("FriendPlugin aktiviert!");
    }

    @Override
    public void onDisable() {
        friendManager.saveFriends();
        getLogger().info("FriendPlugin deaktiviert!");
    }

    public static FriendPlugin getInstance() {
        return instance;
    }

    public FriendManager getFriendManager() {
        return friendManager;
    }
}
