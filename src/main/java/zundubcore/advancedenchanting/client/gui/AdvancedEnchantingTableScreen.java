package zundubcore.advancedenchanting.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;

import net.darkhax.bookshelf.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import zundubcore.advancedenchanting.AdvancedEnchanting;
import zundubcore.advancedenchanting.EnchantingLogic;
import zundubcore.advancedenchanting.api.event.InfoBoxEvent;
import zundubcore.advancedenchanting.container.AdvancedEnchantingTableContainer;
import zundubcore.advancedenchanting.network.EnchantPacket;

public class AdvancedEnchantingTableScreen extends
        ContainerScreen<AdvancedEnchantingTableContainer> {

    /**
     * The ResourceLocation containing the Enchantment GUI texture location
     */
    private static final ResourceLocation ADVANCED_TABLE_GUI_TEXTURE = new ResourceLocation(
            AdvancedEnchanting.MODID, "textures/gui/container/advanced_enchanting_table.png");

    private static final ItemStack SPOOKY_BONE = new ItemStack(Items.BONE);

    static {
        SPOOKY_BONE.addEnchantment(Enchantments.PROJECTILE_PROTECTION, 1);
    }

    private static final KeyBinding keyBindSneak =
            Minecraft.getInstance().gameSettings.keyBindSneak;

    public final List<EnchantmentLabelWidget> enchantmentListAll = new ArrayList<>();
    public final List<EnchantmentLabelWidget> enchantmentList = new ArrayList<>();

    public World world;
    public Minecraft minecraft;
    public AdvancedEnchantingTableContainer container;

    public EnchantmentLabelWidget selected;
    private ButtonItemStack enchantButton;
    public ButtonScroller scrollButton;
    public boolean isSliding;

    public int listOffset = 0;

    String[] tips = {"description", "books", "treasure", "curse", "storage", "inventory", "armor"};

    private final int currentTip;

    private static final Random rand = new Random();

    public AdvancedEnchantingTableScreen(AdvancedEnchantingTableContainer screenContainer,
            PlayerInventory inv, ITextComponent titleIn) {

        super(screenContainer, inv, titleIn);

        this.xSize = 235;
        this.ySize = 182;

        this.container = screenContainer;
        this.world = screenContainer.tileEntity.getWorld();
        this.minecraft = Minecraft.getInstance();

        this.currentTip = rand.nextInt(this.tips.length);
    }

    @Override
    protected void init() {

        super.init();

        this.isSliding = false;
        this.scrollButton = new ButtonScroller(this, this.guiLeft + 206, this.guiTop + 16, 12, 15);

        this.enchantButton = new ButtonItemStack(this.guiLeft + 35, this.guiTop + 38,
                EnchantingLogic.isWikedNight(this.world) ? SPOOKY_BONE
                        : new ItemStack(Items.ENCHANTED_BOOK), $ -> {

            container.enchantItem();
            AdvancedEnchanting.packetHandler.sendToServer(new EnchantPacket());
        });

        this.addButton(this.enchantButton);
        this.addButton(this.scrollButton);
    }

    @Override
    public void render(final int mouseX, final int mouseY, final float partialTicks) {
        this.renderBackground();

        this.updateLabels();
        this.populateEnchantmentSliders();

        super.render(mouseX, mouseY, partialTicks);

        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.minecraft.getTextureManager().bindTexture(ADVANCED_TABLE_GUI_TEXTURE);

        /*
         * Screen#blit draws a part of the current texture (assumed to be 256x256) to
         * the screen. The parameters are (x, y, u, v, width, height)
         */
        this.blit(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        for (final EnchantmentLabelWidget label : this.enchantmentList) {
            label.draw(this.font);
        }
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        this.font.drawString(this.title.getFormattedText(), 32, 5, 4210752);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {

        this.selected = this.getLabelUnderMouse(mouseX, mouseY);

        if (this.selected != null && !this.selected.isLocked() && this.selected.isVisible()) {
            this.selected.setDragging(true);
        }
        else {
            this.selected = null;
        }

        if (this.enchantmentListAll.size() > 4 && mouseX > this.guiLeft + 206
                && mouseX < this.guiLeft + 218) {

            if (mouseY > this.guiTop + 16 + this.scrollButton.sliderY
                    && mouseY < this.guiTop + 31 + this.scrollButton.sliderY) {

                this.isSliding = true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double oldX,
            double oldY) {

        if (this.selected != null) {

            if (mouseX < this.selected.getXPos()
                    || mouseX > this.selected.getXPos() + this.selected.getWidth()) {

                this.selected.setDragging(true);
                this.selected = null;
                return super.mouseDragged(mouseX, mouseY, mouseButton, oldX, oldY);
            }

            this.selected.updateSlider((int) (mouseX) - this.getGuiLeft() - 62);

            this.lockLabels();
        }

        if (this.isSliding) {

            this.scrollButton.sliderY = (int) (mouseY - this.scrollButton.y - 7);
            this.scrollButton.sliderY = Math.max(1, this.scrollButton.sliderY);
            this.scrollButton.sliderY = Math.min(56, this.scrollButton.sliderY);

            this.updateLabels();

            final int div = 50 / Math.max((Math.max(this.enchantmentListAll.size(), 1) - 4), 1);

            this.listOffset = this.scrollButton.sliderY / div;

            this.listOffset = Math.max(this.listOffset, 0);
            this.listOffset = Math.min(this.listOffset, this.enchantmentListAll.size() - 4);
        }

        return super.mouseDragged(mouseX, mouseY, mouseButton, oldX, oldY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {

        final int prevOff = this.listOffset;

        if (delta > 0) {
            this.listOffset -= 1;
        }
        else if (delta < 0) {
            this.listOffset += 1;
        }

        this.listOffset = Math.max(this.listOffset, 0);
        this.listOffset = Math.min(this.listOffset, this.enchantmentListAll.size() - 4);

        if (this.enchantmentList.size() < 4) {
            this.listOffset = 0;
        }

        // I don't know why I can't implement this in @link{ButtonScroller#mouseScrolled}
        final int div = 60 / Math.max((Math.max(this.enchantmentListAll.size(), 1) - 4), 1);

        if (this.listOffset != prevOff) {

            this.updateLabels();

            this.scrollButton.sliderY = div * this.listOffset;
            this.scrollButton.sliderY = Math.max(1, this.scrollButton.sliderY);
            this.scrollButton.sliderY = Math.min(56, this.scrollButton.sliderY);
        }

        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state) {

        if (this.selected != null) {

            this.selected.setDragging(false);
            this.selected = null;

            this.lockLabels();
        }

        if (this.isSliding) {
            this.isSliding = false;
        }

        return super.mouseReleased(mouseX, mouseY, state);
    }

    public void populateEnchantmentSliders() {

        this.enchantmentListAll.clear();

        int labelCount = 0;

        for (final Enchantment enchant : this.container.getValidEnchantments()) {

            final EnchantmentLabelWidget label = new EnchantmentLabelWidget(this, enchant,
                    this.container.getCurrentLevel(enchant), 35 + 26 + this.guiLeft,
                    15 + this.guiTop + labelCount++ * 18);

            label.setCurrentLevel(this.container.getCurrentLevel(enchant));

            this.enchantmentListAll.add(label);
        }
    }

    public void updateLabels() {

        this.enchantmentList.clear();

        int count = 0;

        for (int i = 0; i < this.enchantmentListAll.size(); i++) {

            if (i < this.listOffset) {
                continue;
            }

            final EnchantmentLabelWidget label = this.enchantmentListAll.get(i);

            label.setYPos(15 + this.guiTop + count++ * 18);
            label.setVisible(
                    label.getYPos() >= this.guiTop + 15 && label.getYPos() < this.guiTop + 87);

            this.enchantmentList.add(label);
        }

        this.lockLabels();
    }

    public void lockLabels() {

        for (final EnchantmentLabelWidget label : this.enchantmentListAll) {

            label.setLocked(false);

            final Enchantment enchantment = label.getEnchantment();

            for (final Entry<Enchantment, Integer> data :
                    this.container.getCurrentEnchantments().entrySet()) {

                final boolean isIncompatible = enchantment != data.getKey() && data.getValue() > 0
                        && !data.getKey().isCompatibleWith(enchantment);

                final boolean isOverLeveled =
                        enchantment == data.getKey() && data.getValue() > enchantment.getMaxLevel();

                if (isOverLeveled || isIncompatible) {

                    label.setLocked(true);

                    break;
                }
            }
        }
    }

    public EnchantmentLabelWidget getLabelUnderMouse(double mx, double my) {

        for (final EnchantmentLabelWidget label : this.enchantmentList) {

            if (label.isMouseOver(mx, my + this.listOffset * label.getHeight())) {

                return label;
            }
        }

        return null;
    }

    @Override
    protected void renderHoveredToolTip(int x, int y) {

        super.renderHoveredToolTip(x, y);

        // Info Box
        GuiUtils.drawHoveringText(this.getInfoBox(), this.guiLeft, this.guiTop + 27, this.xSize,
                this.guiTop + this.ySize, this.guiLeft - 18, this.font);

        // Enchant button tooltip
        if (this.enchantButton.isHovered()) {

            final List<String> text = new ArrayList<>();

            if (!this.canClientAfford()) {
                text.add(I18n.format("gui." + AdvancedEnchanting.MODID + ".tooltip.tooexpensive"));
            }
            else if (this.container.getCost() == 0) {
                text.add(I18n.format("gui." + AdvancedEnchanting.MODID + ".tooltip.nochange"));
            }
            else {
                text.add(I18n.format("gui." + AdvancedEnchanting.MODID + ".tooltip.enchant"));
            }

            GuiUtils.drawHoveringText(text, x, y, this.width, this.height, this.width / 4,
                    this.font);
        }

        // TODO: Make descriptions appear when Shift pressed
        if (this.minecraft.gameSettings.keyBindSneak.isKeyDown()) {

            final EnchantmentLabelWidget label = this.getLabelUnderMouse(x, y);

            if (label != null && label.isVisible()) {

                GuiUtils.drawHoveringText(Collections.singletonList(label.getDescription()), x, y,
                        this.width, this.height, this.width / 3, this.font);
            }
        }
    }

    private List<String> getInfoBox() {

        final List<String> info = new ArrayList<>();

        if (this.container.tableInventory.getStackInSlot(0).isEmpty()) {
            info.add(I18n.format("gui." + AdvancedEnchanting.MODID + ".info.noitem"));
        }
        else if (this.enchantmentListAll.isEmpty()) {
            info.add(I18n.format("gui." + AdvancedEnchanting.MODID + ".info.noench"));
        }
        else {

            final boolean isCreative = PlayerUtils.getClientPlayer().isCreative();

            final int playerXP = isCreative ? Integer.MAX_VALUE : EnchantingLogic.getExperience(
                    this.container.player);

            final int cost = this.container.getCost();

            info.add(isCreative ? I18n.format("gui." + AdvancedEnchanting.MODID + ".info.infinity")
                    : I18n.format("gui." + AdvancedEnchanting.MODID + ".info.playerxp", playerXP));

            info.add(I18n.format("gui." + AdvancedEnchanting.MODID + ".info.costxp", cost));
            info.add(I18n.format("gui." + AdvancedEnchanting.MODID + ".info.power",
                    this.container.getEnchantmentPower()) + "%");

            if (cost > playerXP) {
                info.add(" ");
                info.add(TextFormatting.RED + I18n.format(
                        "gui." + AdvancedEnchanting.MODID + ".info.tooexpensive"));
            }
        }

        info.add(" ");
        info.add(TextFormatting.YELLOW + I18n.format(
                "gui." + AdvancedEnchanting.MODID + ".info.tip.prefix") + TextFormatting.RESET
                + I18n.format(
                "gui." + AdvancedEnchanting.MODID + ".info.tip." + this.tips[this.currentTip],
                keyBindSneak.getLocalizedName()));

        MinecraftForge.EVENT_BUS.post(new InfoBoxEvent(this, info));

        return info;
    }

    public boolean canClientAfford() {
        return this.container.getCost() <= EnchantingLogic.getExperience(this.container.player)
                || this.container.player.isCreative();
    }
}
