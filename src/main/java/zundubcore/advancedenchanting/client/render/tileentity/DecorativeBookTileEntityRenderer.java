package zundubcore.advancedenchanting.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

import zundubcore.advancedenchanting.tileentity.DecorativeBookTileEntity;

public class DecorativeBookTileEntityRenderer extends
        FloatingBookTileEntityRenderer<DecorativeBookTileEntity> {

    public DecorativeBookTileEntityRenderer(
            TileEntityRendererDispatcher rendererDispatcherIn) {

        super(rendererDispatcherIn);
    }

    @Override
    public void render(DecorativeBookTileEntity tileEntityIn, float partialTicks,
            MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn,
            int combinedOverlayIn) {

        super.render(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn,
                combinedOverlayIn);
    }
}
