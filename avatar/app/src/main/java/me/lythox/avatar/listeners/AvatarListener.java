package me.lythox.avatar.listeners;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.EarthAbility;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import me.lythox.avatar.Avatar;
import me.lythox.avatar.abilities.LavaStream;
import me.lythox.avatar.abilities.Quicksand;

public class AvatarListener implements Listener {
    private Avatar plugin;

    public AvatarListener() {

    }

    public AvatarListener(Avatar plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
        String ability = bPlayer.getBoundAbilityName();
        if (bPlayer == null || bPlayer.isOnCooldown(bPlayer.getBoundAbilityName()) || !bPlayer.isToggled()) {
            event.setCancelled(true);
            return;
        }
        if (!player.isSneaking()) {
            if (ability.equalsIgnoreCase("LavaStream")) {
                new LavaStream(player);
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onLeftClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }
        Player player = event.getPlayer();
        BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
        String ability = bPlayer.getBoundAbilityName();
        Block block = event.getClickedBlock();
        if (bPlayer == null || bPlayer.isOnCooldown(ability) || !bPlayer.isToggled()) {
            return;
        }
        if (ability.equalsIgnoreCase("Quicksand") && EarthAbility.isEarthbendable(player, block)) {
            new Quicksand(player);
            return;
        }
    }
}