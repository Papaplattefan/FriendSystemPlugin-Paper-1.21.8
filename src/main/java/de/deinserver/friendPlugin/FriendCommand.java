package de.deinserver.friendPlugin;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FriendCommand implements CommandExecutor {

    private final FriendManager friendManager;

    public FriendCommand(FriendManager friendManager) {
        this.friendManager = friendManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Dieser Befehl kann nur von Spielern ausgeführt werden!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Verwendung: /friend <add|remove|list|request|accept|deny> [Spieler]");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "add" -> handleAdd(player, args);
            case "remove" -> handleRemove(player, args);
            case "list" -> FriendGUI.openFriendGUI(player, friendManager);
            case "request" -> handleRequest(player, args);
            case "accept" -> handleAccept(player, args);
            case "deny" -> handleDeny(player, args);
            default -> player.sendMessage("Unbekannter Unterbefehl! /friend <add|remove|list|request|accept|deny>");
        }

        return true;
    }

    private void handleAdd(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Verwendung: /friend add <Spieler>");
            return;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage("Du kannst dich nicht selbst als Freund hinzufügen!");
            return;
        }
        if (friendManager.areFriends(player.getUniqueId(), target.getUniqueId())) {
            player.sendMessage(target.getName() + " ist bereits dein Freund!");
        } else {
            friendManager.addFriend(player.getUniqueId(), target.getUniqueId());
            player.sendMessage(target.getName() + " wurde zu deinen Freunden hinzugefügt!");
        }
    }

    private void handleRemove(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Verwendung: /friend remove <Spieler>");
            return;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (!friendManager.areFriends(player.getUniqueId(), target.getUniqueId())) {
            player.sendMessage(target.getName() + " ist nicht dein Freund!");
        } else {
            friendManager.removeFriend(player.getUniqueId(), target.getUniqueId());
            player.sendMessage(target.getName() + " wurde aus deinen Freunden entfernt!");
        }
    }

    private void handleRequest(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Verwendung: /friend request <Spieler>");
            return;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        UUID from = player.getUniqueId();
        UUID to = target.getUniqueId();

        if (friendManager.areFriends(from, to)) {
            player.sendMessage(target.getName() + " ist bereits dein Freund!");
            return;
        }
        if (friendManager.hasFriendRequest(from, to)) {
            player.sendMessage("Du hast bereits eine Anfrage an " + target.getName() + " gesendet!");
            return;
        }

        friendManager.sendFriendRequest(from, to);
        player.sendMessage("Freundesanfrage an " + target.getName() + " gesendet!");

        if (target.isOnline()) {
            target.getPlayer().sendMessage("§a" + player.getName() + " hat dir eine Freundesanfrage gesendet! Nutze §e/friend accept " + player.getName() + "§a oder §e/friend deny " + player.getName());
        }
    }

    private void handleAccept(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Verwendung: /friend accept <Spieler>");
            return;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        UUID from = target.getUniqueId();
        UUID to = player.getUniqueId();

        if (!friendManager.hasFriendRequest(from, to)) {
            player.sendMessage("Keine Anfrage von " + target.getName() + " gefunden!");
            return;
        }

        friendManager.addFriend(from, to);
        friendManager.addFriend(to, from);
        friendManager.removeFriendRequest(from, to);

        player.sendMessage("Du bist jetzt mit " + target.getName() + " befreundet!");
        if (target.isOnline()) target.getPlayer().sendMessage(player.getName() + " hat deine Freundesanfrage akzeptiert!");
    }

    private void handleDeny(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("Verwendung: /friend deny <Spieler>");
            return;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        UUID from = target.getUniqueId();
        UUID to = player.getUniqueId();

        if (!friendManager.hasFriendRequest(from, to)) {
            player.sendMessage("Keine Anfrage von " + target.getName() + " gefunden!");
            return;
        }

        friendManager.removeFriendRequest(from, to);
        player.sendMessage("Du hast die Anfrage von " + target.getName() + " abgelehnt.");
        if (target.isOnline()) target.getPlayer().sendMessage(player.getName() + " hat deine Freundesanfrage abgelehnt.");
    }
}
