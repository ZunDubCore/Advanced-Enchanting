package zundubcore.advancedenchanting.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.BookModel;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import zundubcore.advancedenchanting.AdvancedEnchanting;
import zundubcore.advancedenchanting.EnchantingLogic;
import zundubcore.advancedenchanting.tileentity.AdvancedEnchantingTableTileEntity;
import zundubcore.advancedenchanting.util.MathUtils;

public class AdvancedEnchantingTableTileEntityRenderer extends
        TileEntityRenderer<AdvancedEnchantingTableTileEntity> {

    /**
     * The ResourceLocation containing the texture for the Book rendered above the enchantment
     * table
     */
    public static final Material TEXTURE_BOOK = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE,
            new ResourceLocation(AdvancedEnchanting.MODID, "entity/decorative_book"));

    private final BookModel modelBook = new BookModel();

    public AdvancedEnchantingTableTileEntityRenderer(
            TileEntityRendererDispatcher rendererDispatcherIn) {

        super(rendererDispatcherIn);
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

        // Render the book
        // From vanilla

        matrixStackIn.push();

        matrixStackIn.translate(0.5D, 0.75D, 0.5D);

        final float f = tileEntityIn.tickCount + partialTicks;

        matrixStackIn.translate(0.0F, 0.1F + MathHelper.sin(f * 0.1F) * 0.01F, 0.0F);

        float f1;

        for (f1 = tileEntityIn.bookRotation - tileEntityIn.bookRotationPrev; f1 >= (float) Math.PI;
                f1 -= (float) Math.PI * 2F) {}

        while (f1 < -(float) Math.PI) {
            f1 += (float) Math.PI * 2F;
        }

        final float f2 = tileEntityIn.bookRotationPrev + f1 * partialTicks;

        matrixStackIn.rotate(Vector3f.YP.rotation(-f2));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(80.0F));

        float f3 = MathHelper.lerp(partialTicks, tileEntityIn.pageFlip, tileEntityIn.pageFlipPrev);
        float f4 = MathHelper.frac(f3 + 0.25F) * 1.6F - 0.3F;
        float f5 = MathHelper.frac(f3 + 0.75F) * 1.6F - 0.3F;
        float f6 = MathHelper.lerp(partialTicks, tileEntityIn.bookSpread,
                tileEntityIn.bookSpreadPrev);

        this.modelBook.func_228247_a_(f, MathHelper.clamp(f4, 0.0F, 1.0F),
                MathHelper.clamp(f5, 0.0F, 1.0F), f6);

        IVertexBuilder ivertexbuilder = TEXTURE_BOOK.getBuffer(bufferIn,
                RenderType::getEntitySolid);

        this.modelBook.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn,
                1.0F, 1.0F, 1.0F, 1.0F);

        matrixStackIn.pop();
    }
}
