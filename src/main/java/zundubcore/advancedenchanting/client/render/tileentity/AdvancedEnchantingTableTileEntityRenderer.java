package zundubcore.advancedenchanting.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.world.World;

import zundubcore.advancedenchanting.EnchantingLogic;
import zundubcore.advancedenchanting.tileentity.AdvancedEnchantingTableTileEntity;
import zundubcore.advancedenchanting.util.MathUtils;

public class AdvancedEnchantingTableTileEntityRenderer extends
        FloatingBookTileEntityRenderer<AdvancedEnchantingTableTileEntity> {

    public AdvancedEnchantingTableTileEntityRenderer(
            TileEntityRendererDispatcher rendererDispatcherIn) {

        super(rendererDispatcherIn);
    }

    @Override
    public void render(AdvancedEnchantingTableTileEntity tileEntityIn, float partialTicks,
            MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn,
            int combinedOverlayIn) {

        if (EnchantingLogic.isWikedNight(tileEntityIn.getWorld()) && tileEntityIn.bookSpread != 0) {

            spawnParticleRing(tileEntityIn.getWorld(), ParticleTypes.PORTAL,
                    tileEntityIn.getPos().getX() + 0.5, tileEntityIn.getPos().getY() + 1,
                    tileEntityIn.getPos().getZ() + 0.5, 0, -1, 0, 0.45);
        }
        else if (MathUtils.tryPercentage(0.5) && EnchantingLogic.isTreasuresAvailable(
                Enchantments.MENDING, tileEntityIn.getWorld(), tileEntityIn.getPos(),
                tileEntityIn.getPos().down())) {

            int red = tileEntityIn.getWorld().getGameTime() % 2 == 0 ? -1 : 0;

            spawnParticleRing(tileEntityIn.getWorld(), RedstoneParticleData.REDSTONE_DUST,
                    tileEntityIn.getPos().getX() + 0.5, tileEntityIn.getPos().getY() + 1,
                    tileEntityIn.getPos().getZ() + 0.5, red, 1, 0, 0.45);
        }

        super.render(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn,
                combinedOverlayIn);
    }

    /**
     * Spawns particles in a ring, centered around a certain position.
     *
     * @param world     The world to spawn the particles in.
     * @param particle  The type of particle to spawn.
     * @param x         The x position to spawn the particle around.
     * @param y         The y position to spawn the particle around.
     * @param z         The z position to spawn the particle around.
     * @param velocityX The velocity of the particle, in the x direction.
     * @param velocityY The velocity of the particle, in the y direction.
     * @param velocityZ The velocity of the particle, in the z direction.
     * @param step      The distance in degrees, between each particle. The maximum is 2 * PI, which
     *                  will create 1 particle per ring. 0.15 is a nice value.
     */
    public static void spawnParticleRing(World world, IParticleData particle, double x, double y,
            double z, double velocityX, double velocityY, double velocityZ, double step) {

        for (double degree = 0.0d; degree < 2 * Math.PI; degree += step) {
            world.addParticle(particle, x + Math.cos(degree), y, z + Math.sin(degree), velocityX,
                    velocityY, velocityZ);
        }
    }
}
