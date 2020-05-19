package zundubcore.advancedenchanting;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;

import zundubcore.advancedenchanting.api.Blacklist;
import zundubcore.advancedenchanting.api.event.EnchantmentCostEvent;
import zundubcore.advancedenchanting.config.AdvancedEnchantingConfig;

public final class EnchantingLogic {

    public static int calculateNewEnchCost(Enchantment enchantment, int level) {

        // Base cost is equal to roughly 2.5 levels of EXP.
        int cost = AdvancedEnchantingConfig.baseCost;

        // Cost is multiplied up to 10, based on rarity of the enchant.
        // Rarer the enchant, higher the cost.
        cost *= Math.max(11 - enchantment.getRarity().getWeight(), 1);

        // Linear cost increase based on level.
        cost *= level;

        // The cost factor is applied. Default is 1.5.
        cost *= AdvancedEnchantingConfig.costFactor;

        // Curses cost even more to apply
        if (enchantment.isCurse()) {

            cost *= AdvancedEnchantingConfig.curseFactor;
        }

        // Treasures cost more to apply
        else if (enchantment.isTreasureEnchantment()) {

            cost *= AdvancedEnchantingConfig.treasureFactor;
        }

        final EnchantmentCostEvent event = new EnchantmentCostEvent(cost, enchantment, level);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getCost();
    }

    public static List<Enchantment> getValidEnchantments(ItemStack stack, World world,
            BlockPos pos) {

        final List<Enchantment> enchList = new ArrayList<>();

        if (!stack.isEmpty() && (stack.isEnchantable() || stack.isEnchanted())) {

            for (final Enchantment enchantment : Registry.ENCHANTMENT) {

                if (Blacklist.isEnchantmentBlacklisted(enchantment) || !enchantment
                        .canApply(stack)) {

                    continue;
                }

                if (!enchantment.isTreasureEnchantment() || isCurse(world, enchantment)
                        || isTreasuresAvailable(enchantment, world, pos, pos.down())) {

                    enchList.add(enchantment);
                }
            }
        }

        return enchList;
    }

    public static boolean isCurse(World world, Enchantment enchantment) {

        return enchantment.isCurse() && isWikedNight(world);
    }

    public static boolean isTreasuresAvailable(Enchantment enchantment, World world, BlockPos pos,
            BlockPos down) {

        if (enchantment.isCurse() || !enchantment.isTreasureEnchantment() || !world.isDaytime()) {

            return false;
        }

        for (int x = -1; x <= 1; x++) {

            for (int z = -1; z <= 1; z++) {

                final BlockPos currentPos = down.add(x, 0, z);
                final BlockState block = world.getBlockState(currentPos);

                if (!block.isBeaconBase(world, currentPos, pos)) {

                    return false;
                }
            }
        }

        return true;
    }

    public static boolean isWikedNight(World world) {

        final float skyAngle = world.getCelestialAngle(1f);
        final boolean isNightRange = skyAngle > 0.40 && skyAngle < 0.60;

        return world.getMoonPhase() == 0 && isNightRange;
    }

    // The following code was adapted from OpenModsLib
    // https://github.com/OpenMods/OpenModsLib
    public static int getExperience(PlayerEntity player) {

        return (int) (getExperienceForLevels(player.experienceLevel) + player.experience * player
                .xpBarCap());
    }

    public static void removeExperience(PlayerEntity player, int amount) {

        addExperience(player, -amount);
    }

    public static void addExperience(PlayerEntity player, int amount) {

        final int experience = getExperience(player) + amount;
        player.experienceTotal = experience;
        player.experienceLevel = getLevelForExperience(experience);
        final int expForLevel = getExperienceForLevels(player.experienceLevel);
        player.experience = (float) (experience - expForLevel) / (float) player.xpBarCap();
    }

    public static int getExperienceForLevels(int level) {

        if (level == 0) {

            return 0;
        }

        if (level > 0 && level < 17) {

            return level * level + 6 * level;
        }
        else if (level > 16 && level < 32) {

            return (int) (2.5 * level * level - 40.5 * level + 360);
        }

        return (int) (4.5 * level * level - 162.5 * level + 2220);
    }

    public static int getLevelForExperience(int experience) {

        int level = 0;

        while (getExperienceForLevels(level) <= experience) {

            level++;
        }

        return level - 1;
    }
}