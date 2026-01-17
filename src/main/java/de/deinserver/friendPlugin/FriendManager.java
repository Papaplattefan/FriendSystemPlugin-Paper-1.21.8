package de.deinserver.friendPlugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FriendManager {

    private final Map<UUID, Set<UUID>> friends = new HashMap<>();
    private final Map<UUID, Set<UUID>> friendRequests = new HashMap<>();
    private final File file;
    private final FileConfiguration config;

    public FriendManager(JavaPlugin plugin) {
        file = new File(plugin.getDataFolder(), "friends.yml");
        if (!file.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        loadFriends();
    }

    // --- FRIENDS ---
    private void loadFriends() {
        for (String uuidStr : config.getKeys(false)) {
            UUID player = UUID.fromString(uuidStr);
            List<String> friendList = config.getStringList(uuidStr + ".friends");
            Set<UUID> friendSet = new HashSet<>();
            for (String f : friendList) {
                friendSet.add(UUID.fromString(f));
            }
            friends.put(player, friendSet);

            // Freundesanfragen laden
            List<String> requestList = config.getStringList(uuidStr + ".requests");
            Set<UUID> requestSet = new HashSet<>();
            for (String r : requestList) {
                requestSet.add(UUID.fromString(r));
            }
            friendRequests.put(player, requestSet);
        }
    }

    public void saveFriends() {
        for (Map.Entry<UUID, Set<UUID>> entry : friends.entrySet()) {
            List<String> friendList = new ArrayList<>();
            for (UUID f : entry.getValue()) {
                friendList.add(f.toString());
            }
            config.set(entry.getKey().toString() + ".friends", friendList);
        }
        for (Map.Entry<UUID, Set<UUID>> entry : friendRequests.entrySet()) {
            List<String> requestList = new ArrayList<>();
            for (UUID r : entry.getValue()) {
                requestList.add(r.toString());
            }
            config.set(entry.getKey().toString() + ".requests", requestList);
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- FRIENDS METHODEN ---
    public void addFriend(UUID player, UUID friend) {
        friends.computeIfAbsent(player, k -> new HashSet<>()).add(friend);
        saveFriends();
    }

    public void removeFriend(UUID player, UUID friend) {
        if(friends.containsKey(player)) {
            friends.get(player).remove(friend);
            saveFriends();
        }
    }

    public boolean areFriends(UUID p1, UUID p2) {
        return friends.getOrDefault(p1, Set.of()).contains(p2);
    }

    public Set<UUID> getFriends(UUID player) {
        return friends.getOrDefault(player, Set.of());
    }

    // --- FRIEND REQUESTS METHODEN ---
    public void sendFriendRequest(UUID from, UUID to) {
        friendRequests.computeIfAbsent(to, k -> new HashSet<>()).add(from);
        saveFriends();
    }

    public void removeFriendRequest(UUID from, UUID to) {
        if(friendRequests.containsKey(to)) {
            friendRequests.get(to).remove(from);
            saveFriends();
        }
    }

    public boolean hasFriendRequest(UUID from, UUID to) {
        return friendRequests.getOrDefault(to, Set.of()).contains(from);
    }

    public Set<UUID> getFriendRequests(UUID player) {
        return friendRequests.getOrDefault(player, Set.of());
    }
}
