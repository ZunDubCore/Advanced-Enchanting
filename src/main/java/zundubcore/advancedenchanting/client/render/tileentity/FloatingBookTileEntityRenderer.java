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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import zundubcore.advancedenchanting.AdvancedEnchanting;
import zundubcore.advancedenchanting.tileentity.FloatingBookTileEntity;

public abstract class FloatingBookTileEntityRenderer<T extends FloatingBookTileEntity> extends
        TileEntityRenderer<T> {

    /**
     * The ResourceLocation containing the texture for the Book rendered above the enchantment
     * table.
     * <p>
     * TODO: Make this work for diffenrent materials
     */
    public static final Material TEXTURE_BOOK = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE,
            new ResourceLocation(AdvancedEnchanting.MODID, "entity/decorative_book"));

    private final BookModel modelBook = new BookModel();

    public FloatingBookTileEntityRenderer(
            TileEntityRendererDispatcher rendererDispatcherIn) {

        super(rendererDispatcherIn);
    }

    /**
     * Render the book. From vanilla
     */
    public void render(T tileEntityIn, float partialTicks,
            MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn,
            int combinedOverlayIn) {

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
