package me.lythox.avatar.abilities;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.SandAbility;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;

import me.lythox.avatar.listeners.AvatarListener;

public class Quicksand extends SandAbility implements AddonAbility {
    private AvatarListener listener;
    private Location pLocation;
    private Location cLocation;
    private long duration;
    private long cooldown;
    private long time;
    private TempBlock tblock;
    private ArrayList<TempBlock> affectedBlocks;
    private ArrayList<FallingBlock> fallingBlocks;
    private Vector direction;
    private int maxRange;
    private int range;
    private boolean progressing;

    public Quicksand(Player player) {
        super(player);
        this.pLocation = player.getLocation();
        this.cLocation = player.getLocation();
        this.direction = pLocation.getDirection().setY(0);
        this.duration = 10000;
        this.cooldown = 10000;
        this.tblock = null;
        this.affectedBlocks = new ArrayList<TempBlock>();
        this.fallingBlocks = new ArrayList<FallingBlock>();
        this.maxRange = 20;
        this.range = 0;
        this.progressing = true;
        start();
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public Location getLocation() {
        return pLocation;
    }

    @Override
    public String getName() {
        return "Quicksand";
    }

    @Override
    public boolean isHarmlessAbility() {
        return false;
    }

    @Override
    public boolean isSneakAbility() {
        return false;
    }

    @Override
    public void progress() {
        if (!progressing) {
            this.time = System.currentTimeMillis();
            if (this.time - this.getStartTime() > this.duration) {
                for (int i = 0; i < fallingBlocks.size(); i++) {
                    this.fallingBlocks.get(i).remove();
                }
                for (int i = 0; i < affectedBlocks.size(); i++) {
                    this.affectedBlocks.get(i).revertBlock();
                }
                this.remove();
                return;
            }
        } else {
            if (this.range > this.maxRange) {
                this.bPlayer.addCooldown(this);
                this.remove();
                return;
            }

            this.range++;
            this.cLocation.add(this.direction.normalize());
            Block top = GeneralMethods.getTopBlock(cLocation, 2);
            while (!this.isEarthbendable(top)) {
                if (this.isTransparent(top)) {
                    top = top.getRelative(BlockFace.DOWN);
                } else {
                    this.bPlayer.addCooldown(this);
                    this.remove();
                    return;
                }
            }

            if (!this.isTransparent(top.getRelative(BlockFace.UP))) {
                this.bPlayer.addCooldown(this);
                this.remove();
                return;
            }

            this.cLocation.setY(top.getY() + 1);
            ParticleEffect particle = ParticleEffect.CRIT;
            particle.display(this.cLocation, 5, 0.4, 0, 0.4, 0.001);

            for (final Entity entity : GeneralMethods.getEntitiesAroundPoint(cLocation, 0.5)) {
                if (!(entity instanceof LivingEntity) || entity.getEntityId() == this.player.getEntityId()) {
                    continue;
                }
                LivingEntity target = (LivingEntity) entity;
                if (this.player.getLocation().getBlock().getBiome().equals(Biome.DESERT)) {
                    target.damage(3);
                } else {
                    target.damage(1.5);
                }
                Biome biome = this.player.getLocation().getBlock().getBiome();
                target.damage(biome.equals(Biome.DESERT) ? 3 : 1.5);
                target.addPotionEffect(
                        new PotionEffect(PotionEffectType.SLOW_FALLING, 200, 1, false, false, false));
                target.addPotionEffect(
                        new PotionEffect(PotionEffectType.SLOW, 200, 10, false, false, false));
                for (Block block : GeneralMethods.getBlocksAroundPoint(cLocation.subtract(0, 1, 0), 2)) {
                    if (this.isEarthbendable(block)) {
                        FallingBlock fallingBlock = this.player.getWorld().spawnFallingBlock(
                                block.getLocation().add(0.5, 0, 0.5),
                                Bukkit.createBlockData(Material.SAND));
                        fallingBlock.setGravity(false);
                        this.fallingBlocks.add(fallingBlock);
                    }
                    tblock = new TempBlock(block, Material.AIR);
                    affectedBlocks.add(tblock);
                }
                this.bPlayer.addCooldown(this);
                progressing = false;
            }
        }
    }

    @Override
    public void remove() {
        super.remove();
    }

    @Override
    public String getAuthor() {
        return "lythox";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
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
        return "Turn the ground below an entity into quicksand making them unable to move. This move is stronger when used in deserts";
    }

    @Override
    public String getInstructions() {
        return "Left-click on any earthbendable block";
    }

}
