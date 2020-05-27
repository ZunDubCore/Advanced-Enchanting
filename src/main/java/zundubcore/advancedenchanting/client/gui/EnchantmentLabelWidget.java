package zundubcore.advancedenchanting.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import zundubcore.advancedenchanting.AdvancedEnchanting;

public class EnchantmentLabelWidget extends Widget {

    private static final ResourceLocation ADVANCED_TABLE_GUI_TEXTURE = new ResourceLocation(
            AdvancedEnchanting.MODID, "textures/gui/container/advanced_enchanting_table.png");

    private static final int HEIGHT = 18;
    private static final int WIDTH = 143;

    private static final int COLOR_BACKGROUND_LOCKED = 0x44d10841;
    private static final int COLOR_BACKGROUND_AVAILABLE = 0x445aaeae;

    private final Enchantment enchantment;

    private final int initialLevel;
    private int currentLevel;

    public AdvancedEnchantingTableScreen parent;

    private int startingXPos;
    private int startingYPos;
    private int xPos;
    private int yPos;

    private int sliderX;

    private boolean dragging = false;
    private boolean visible = true;
    private boolean locked = false;

    public EnchantmentLabelWidget(AdvancedEnchantingTableScreen parent, Enchantment enchant,
            int level, int xIn, int yIn) {

        super(xIn, yIn, WIDTH, HEIGHT, "");

        this.parent = parent;

        this.enchantment = enchant;

        this.currentLevel = level;
        this.initialLevel = level;

        this.xPos = this.startingXPos = x;
        this.yPos = this.startingYPos = y;

        this.sliderX = this.xPos + 1;

        if (this.currentLevel > this.enchantment.getMaxLevel()) {
            this.locked = true;
        }
    }

    private static String getTranslationKey(Enchantment enchant) {

        if (enchant != null && enchant.getRegistryName() != null) {

            return String.format("enchantment.%s.%s.desc", enchant.getRegistryName().getNamespace(),
                    enchant.getRegistryName().getPath());
        }

        return "NULL";
    }

    /**
     * Handles the rendering of an enchantment label.
     *
     * @param font The font renderer. Allows rendering font.
     */
    public void draw(FontRenderer font) {

        if (!this.visible) {
            return;
        }

        final int indexX = this.dragging ? this.sliderX
                : this.currentLevel <= this.enchantment.getMaxLevel() ? (int) (this.xPos + 1
                        + (EnchantmentLabelWidget.WIDTH - 6) * (this.currentLevel
                        / (double) this.enchantment.getMaxLevel()))
                        : this.xPos + 1 + EnchantmentLabelWidget.WIDTH - 6;

        fill(this.xPos + 1, this.yPos + 2, this.xPos + EnchantmentLabelWidget.WIDTH,
                this.yPos + EnchantmentLabelWidget.HEIGHT,
                this.locked ? COLOR_BACKGROUND_LOCKED : COLOR_BACKGROUND_AVAILABLE);

        RenderSystem.color4f(1, 1, 1, 1);

        this.parent.minecraft.getTextureManager().bindTexture(ADVANCED_TABLE_GUI_TEXTURE);

        this.blit(indexX, this.yPos + 2, this.isSelected() ? 5 : 0, 197, 5, 16);

        font.drawString(this.getDisplayName(), this.xPos + 7, this.yPos + 6, 0x55aaff00);
    }

    /**
     * Updates the state of the slider. This is used to handle changing the current level of the
     * enchantment being represented.
     *
     * @param xPos  The xPos of the slider.
     */
    public void updateSlider(double xPos) {

        // If the slider is locked, prevent it from updating.
        if (this.locked) {
            return;
        }

        final int min = this.xPos + 1;
        final int max = min + EnchantmentLabelWidget.WIDTH - 6;

        // Updates the position of the slider. -2 to center the cursor on the mouse
        // position.
        this.sliderX = (int) (min + xPos - 2);

        // Prevents the slider from being dragged less than the lowest position.
        if (this.sliderX < min) {
            this.sliderX = min;
        }
        // Prevents the slider from being dragged
        else if (this.sliderX > max) {
            this.sliderX = max;
        }

        final float index = (float) (xPos / (float) (EnchantmentLabelWidget.WIDTH - 10));

        final int updatedLevel = Math.round(
                this.initialLevel > this.enchantment.getMaxLevel() ? this.initialLevel * index
                        : this.enchantment.getMaxLevel() * index);

        // Checks if the updated level can be applied.
        if (updatedLevel > this.initialLevel
                || !this.parent.container.tableInventory.getStackInSlot(0).isDamaged()) {

            this.currentLevel = updatedLevel;
        }

        // Adjust to range.
        if (this.currentLevel < 0) {
            this.currentLevel = 0;
        }

        // Adjust to range.
        else if (this.currentLevel > this.enchantment.getMaxLevel()) {
            this.currentLevel = this.enchantment.getMaxLevel();
        }

        // Send the update to the server.
        // EnchantingPlus.NETWORK.sendToServer(new MessageSliderUpdate(new
        // EnchantData(this.enchantment, this.currentLevel)));
        this.parent.container.updateEnchantment(this.enchantment, this.currentLevel);
    }

    public boolean isSelected() {
        return this.parent.selected != null && this.parent.selected.enchantment == this.enchantment;
    }

    /**
     * Used to get the translated name of the enchantment. If the enchantment is of level 0, the
     * level bit is cut off.
     *
     * @return The name to display for the label.
     */
    public String getDisplayName() {

        String s = I18n.format(this.enchantment.getName());

        if (this.enchantment.isCurse()) {
            s = TextFormatting.RED + s;
        }

        return this.currentLevel <= 0 ? s : s + " " + I18n.format(
                "enchantment.level." + this.currentLevel);
    }

    public String getDescription() {
        return I18n.format(getTranslationKey(this.enchantment));
    }

    public int getCurrentLevel() {
        return this.currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public int getStartingXPos() {
        return this.startingXPos;
    }

    public void setStartingXPos(int startingXPos) {
        this.startingXPos = startingXPos;
    }

    public int getStartingYPos() {
        return this.startingYPos;
    }

    public void setStartingYPos(int startingYPos) {
        this.startingYPos = startingYPos;
    }

    public int getXPos() {
        return this.xPos;
    }

    public void setXPos(int xPos) {
        this.xPos = xPos;
    }

    public int getYPos() {
        return this.yPos;
    }

    public void setYPos(int yPos) {
        this.yPos = yPos;
    }

    public int getSliderX() {
        return this.sliderX;
    }

    public void setSliderX(int sliderX) {
        this.sliderX = sliderX;
    }

    public boolean isDragging() {
        return this.dragging;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public int getWidth() {
        return WIDTH;
    }

    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Sets whether or not the label should be shown.
     *
     * @param isVisible Whether or not the label is visible.
     */
    public void setVisible(boolean isVisible) {
        this.visible = isVisible;
    }

    public int getHeight() {
        return HEIGHT;
    }

    public Enchantment getEnchantment() {
        return this.enchantment;
    }

    public int getInitialLevel() {
        return this.initialLevel;
    }
}
