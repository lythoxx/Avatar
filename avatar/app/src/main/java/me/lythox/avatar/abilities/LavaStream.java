package me.lythox.avatar.abilities;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.LavaAbility;
import com.projectkorra.projectkorra.util.TempBlock;

import me.lythox.avatar.listeners.AvatarListener;

public class LavaStream extends LavaAbility implements AddonAbility {
    private AvatarListener listener;
    private Location location;
    private Player player;
    private long duration;
    private long cooldown;
    private long time;
    private TempBlock tblock;
    private ArrayList<TempBlock> affectedBlocks;

    public LavaStream(Player player) {
        super(player);
        this.location = player.getLocation();
        this.player = player;
        this.duration = 5000;
        this.cooldown = 4000;
        this.tblock = null;
        this.affectedBlocks = new ArrayList<TempBlock>();
        start();
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public String getName() {
        return "LavaStream";
    }

    @Override
    public boolean isHarmlessAbility() {
        return false;
    }

    @Override
    public boolean isSneakAbility() {
        return true;
    }

    @Override
    public void progress() {
        this.time = System.currentTimeMillis();
        if (this.time - this.getStartTime() > this.duration) {
            this.remove();
            return;
        }
        Block block = player.getTargetBlockExact(10);
        if (block == null) {
            return;
        }
        if (this.isEarthbendable(block) && this.time - this.getStartTime() <= 1000
                && !block.getRelative(BlockFace.UP).getType().equals(Material.WATER)) {
            tblock = new TempBlock(block, Material.LAVA);
            this.affectedBlocks.add(tblock);
            this.bPlayer.addCooldown(this);
        }
    }

    @Override
    public void remove() {
        super.remove();
        for (int i = 0; i < affectedBlocks.size(); i++) {
            this.affectedBlocks.get(i).revertBlock();
        }
    }

    @Override
    public String getAuthor() {
        return "lythox";
    }

    @Override
    public String getVersion() {
        return "1.0.1";
    }

    @Override
    public void load() {
        this.listener = new AvatarListener();
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this.listener);
    }

    @Override
    public String getDescription() {
        return "Turn any Earthbendable block you are looking at into lava";
    }

    @Override
    public String getInstructions() {
        return "Press shift to activate";
    }
}