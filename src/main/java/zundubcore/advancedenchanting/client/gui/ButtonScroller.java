package zundubcore.advancedenchanting.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.ResourceLocation;

import zundubcore.advancedenchanting.AdvancedEnchanting;

public class ButtonScroller extends ImageButton {

    /**
     * The ResourceLocation containing the Enchantment GUI texture location
     */
    private static final ResourceLocation ADVANCED_TABLE_GUI_TEXTURE = new ResourceLocation(
            AdvancedEnchanting.MODID, "textures/gui/container/advanced_enchanting_table.png");

    public AdvancedEnchantingTableScreen parent;

    public int sliderY = 1;

    public ButtonScroller(AdvancedEnchantingTableScreen parent, int xIn, int yIn, int widthIn,
            int heightIn) {

        super(xIn, yIn, widthIn, heightIn, 0, 0, 0, ADVANCED_TABLE_GUI_TEXTURE, onPressIn -> {});

        this.parent = parent;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {

        if (this.visible) {

            Minecraft minecraft = Minecraft.getInstance();

            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

            this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width
                    && mouseY < this.y + this.height;

            minecraft.getTextureManager().bindTexture(ADVANCED_TABLE_GUI_TEXTURE);

            this.blit(this.x, this.y + this.sliderY,
                    this.parent.isSliding || this.parent.enchantmentListAll.size() <= 4
                            ? this.getWidth() : 0, 182, this.getWidth(), this.height);
        }
    }

    // I don't know why this method is never called
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double oldX,
            double oldY) {

        return super.mouseDragged(mouseX, mouseY, mouseButton, oldX, oldY);
    }

}
