package de.deinserver.friendPlugin;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Set;
import java.util.UUID;

public class FriendGUI {

    public static void openFriendGUI(Player player, FriendManager friendManager) {

        Set<UUID> friends = friendManager.getFriends(player.getUniqueId());

        int size = ((friends.size() / 9) + 1) * 9; // Chest-Größe auf nächstes Vielfaches von 9
        Inventory inv = Bukkit.createInventory(null, size, "Deine Freunde");

        for (UUID friendUUID : friends) {
            OfflinePlayer friend = Bukkit.getOfflinePlayer(friendUUID);

            ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();

            if (meta != null) {
                meta.setOwningPlayer(friend);
                meta.setDisplayName(friend.getName());

                // Online/Offline Status
                if (friend.isOnline()) {
                    meta.setLore(java.util.List.of("§aOnline"));
                } else {
                    meta.setLore(java.util.List.of("§7Offline"));
                }

                skull.setItemMeta(meta);
            }

            inv.addItem(skull);
        }

        player.openInventory(inv);
    }
}
