package zundubcore.advancedenchanting.container;

import com.mojang.datafixers.util.Pair;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.IContainerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.annotation.Nonnull;

import zundubcore.advancedenchanting.AdvancedEnchanting;
import zundubcore.advancedenchanting.EnchantingLogic;
import zundubcore.advancedenchanting.init.AdvancedEnchantingBlocks;
import zundubcore.advancedenchanting.init.AdvancedEnchantingContainerTypes;
import zundubcore.advancedenchanting.tileentity.AdvancedEnchantingTableTileEntity;

public class AdvancedEnchantingTableContainer extends Container {

    public final static EquipmentSlotType[] VALID_EQUIPMENT_SLOTS =
            new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST,
                    EquipmentSlotType.LEGS, EquipmentSlotType.FEET};

    private static final ResourceLocation[] ARMOR_SLOT_TEXTURES =
            new ResourceLocation[]{PlayerContainer.EMPTY_ARMOR_SLOT_BOOTS,
                    PlayerContainer.EMPTY_ARMOR_SLOT_LEGGINGS,
                    PlayerContainer.EMPTY_ARMOR_SLOT_CHESTPLATE,
                    PlayerContainer.EMPTY_ARMOR_SLOT_HELMET};

    public final AdvancedEnchantingTableTileEntity tileEntity;
    private final IWorldPosCallable canInteractWithCallable;

    private final World world;
    private final BlockPos pos;
    public PlayerEntity player;
    private ItemStack inputStack;

    // The new enchantments that will go on the item.
    // TODO: Fix static, should be done through network
    private static Map<Enchantment, Integer> itemEnchantments;

    // All valid enchantments for the current item.
    private List<Enchantment> validEnchantments;

    // The original enchantment map.
    private Map<Enchantment, Integer> initialEnchantments;

    private float enchantmentPower;
    private int cost;

    public IInventory tableInventory = new Inventory(1) {
        /**
         * For tile entities, ensures the chunk containing the tile entity is saved to
         * disk later - the game won't think it hasn't changed and skip it.
         */
        @Override
        public void markDirty() {
            super.markDirty();
            onCraftMatrixChanged(this);
        }
    };

    /**
     * Logical-client-side constructor, called from {@link ContainerType#create(IContainerFactory)}
     * Calls the logical-server-side constructor with the TileEntity at the pos in the PacketBuffer
     */
    public AdvancedEnchantingTableContainer(final int windowId,
            final PlayerInventory playerInventory, final PacketBuffer data) {

        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    /**
     * Constructor called logical-server-side from
     * {@link AdvancedEnchantingTableTileEntity#createMenu}
     * and logical-client-side from {@link #AdvancedEnchantingTableContainer(int, PlayerInventory,
     * PacketBuffer)}
     */
    public AdvancedEnchantingTableContainer(final int windowId,
            final PlayerInventory playerInventory,
            final AdvancedEnchantingTableTileEntity tileEntity) {

        super(AdvancedEnchantingContainerTypes.ADVANCED_ENCHANTING_TABLE.get(), windowId);

        this.tileEntity = tileEntity;
        this.player = playerInventory.player;

        this.world = tileEntity.getWorld();
        this.pos = tileEntity.getPos();

        this.canInteractWithCallable = IWorldPosCallable.of(this.world, this.pos);

        // Enchantment slot
        this.addSlot(new Slot(this.tableInventory, 0, 37, 17) {

            @Override
            public int getSlotStackLimit() {
                return 1;
            }

            @Override
            public boolean isItemValid(ItemStack stack) {
                return AdvancedEnchanting.TEST_ENCHANTABILITY.test(stack);
            }
        });

        // Hotbar
        for (int x = 0; x < 9; ++x) {
            this.addSlot(new Slot(playerInventory, x, 43 + 18 * x, 149));
        }

        // Inventory
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 43 + 18 * x, 91 + y * 18));
            }
        }

        // Armor slots
        for (int y = 0; y < 4; y++) {

            final EquipmentSlotType equipmentslottype = VALID_EQUIPMENT_SLOTS[y];

            this.addSlot(new Slot(playerInventory, 39 - y, 7, 24 + y * 19) {
                /**
                 * Returns the maximum stack size for a given slot (usually the same as
                 * getInventoryStackLimit(), but 1 in the case of armor slots)
                 */
                @Override
                public int getSlotStackLimit() {
                    return 1;
                }

                /**
                 * Check if the stack is allowed to be placed in this slot, used for armor slots
                 * as well as furnace fuel.
                 */
                @Override
                public boolean isItemValid(ItemStack stack) {
                    return stack.canEquip(equipmentslottype, playerInventory.player);
                }

                /**
                 * Return whether this slot's stack can be taken from this slot.
                 */
                @Override
                public boolean canTakeStack(PlayerEntity playerIn) {

                    ItemStack itemstack = this.getStack();

                    return (itemstack.isEmpty() || playerIn.isCreative()
                            || !EnchantmentHelper.hasBindingCurse(itemstack)) && super.canTakeStack(
                            playerIn);
                }

                // From vanilla
                @OnlyIn(Dist.CLIENT)
                @Override
                public Pair<ResourceLocation, ResourceLocation> getBackground() {

                    return Pair.of(PlayerContainer.LOCATION_BLOCKS_TEXTURE,
                            ARMOR_SLOT_TEXTURES[equipmentslottype.getIndex()]);
                }
            });
        }
    }

    private static AdvancedEnchantingTableTileEntity getTileEntity(
            final PlayerInventory playerInventory, final PacketBuffer data) {

        Objects.requireNonNull(playerInventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null!");

        final TileEntity tileAtPos = playerInventory.player.world.getTileEntity(
                data.readBlockPos());

        if (tileAtPos instanceof AdvancedEnchantingTableTileEntity) {
            return (AdvancedEnchantingTableTileEntity) tileAtPos;
        }

        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory inventoryIn) {

        this.inputStack = this.tableInventory.getStackInSlot(0);

        initialEnchantments = EnchantmentHelper.getEnchantments(this.inputStack);
        itemEnchantments = new HashMap<>(initialEnchantments);

        validEnchantments = EnchantingLogic.getValidEnchantments(this.inputStack, this.world,
                this.pos);

        this.calculateState();
    }

    public void calculateState() {

        this.world.getBlockState(this.pos).getEnchantPowerBonus(this.world, this.pos);
        this.enchantmentPower = getEnchantingPower(this.world, this.pos);
        this.cost = 0;

        // Calculate cost of new enchantments
        for (final Entry<Enchantment, Integer> newEntry : itemEnchantments.entrySet()) {

            final int original = initialEnchantments.getOrDefault(newEntry.getKey(), 0);
            final int newLevels = newEntry.getValue() - original;

            // If you want to enable giving exp back to the player, this check can be
            // modified.
            if (newLevels > 0) {
                this.cost += EnchantingLogic.calculateNewEnchCost(newEntry.getKey(), newLevels);
            }
        }

        // Calculate cost of removed curses
        for (final Entry<Enchantment, Integer> existingEnch : initialEnchantments.entrySet()) {

            if (existingEnch.getKey().isCurse() && existingEnch.getValue() > 0) {

                final int currentCurseLevel = itemEnchantments.getOrDefault(existingEnch.getKey(),
                        0);

                if (currentCurseLevel < existingEnch.getValue()) {

                    this.cost += EnchantingLogic.calculateNewEnchCost(existingEnch.getKey(),
                            existingEnch.getValue() - currentCurseLevel);
                }
            }
        }

        // Apply bookshelf discount
        if (this.enchantmentPower > 0) {
            this.cost -= this.cost * this.enchantmentPower / 100f;
        }
    }

    public void enchantItem() {

        // If player doesn't have enough exp, ignore them.
        if (!player.isCreative() && EnchantingLogic.getExperience(player) < this.cost) {
            return;
        }

        // Only no creative players get charged
        if (!player.isCreative() && this.cost > 0) {
            EnchantingLogic.removeExperience(player, this.cost);
        }

        // Clear all existing enchantments
        EnchantmentHelper.setEnchantments(new HashMap<>(), this.inputStack);

        // Apply new enchantments
        for (final Entry<Enchantment, Integer> entry : itemEnchantments.entrySet()) {
            if (entry.getValue() > 0) {
                this.inputStack.addEnchantment(entry.getKey(), entry.getValue());
            }
        }

        // Update the logic.
        this.tableInventory.markDirty();
    }

    public void updateEnchantment(Enchantment enchantment, int level) {

        // If the level is set to below 0, remove it from the item.
        if (level < 1) {
            itemEnchantments.remove(enchantment);
        }

        // If the enchantment is valid for item, update it.
        else if (validEnchantments.contains(enchantment)) {
            itemEnchantments.put(enchantment, level);
        }

        this.calculateState();
    }

    public Map<Enchantment, Integer> getCurrentEnchantments() {
        return itemEnchantments;
    }

    public List<Enchantment> getValidEnchantments() {
        return validEnchantments;
    }

    public int getCurrentLevel(Enchantment enchant) {
        return itemEnchantments.getOrDefault(enchant, 0);
    }

    public int getCost() {
        return this.cost;
    }

    /**
     * Gets the enchantment power for a given position in a world. This uses the enchantment table
     * logic, so it searches for blocks that are two blocks out from the position passed.
     *
     * @param world The world to check in.
     * @param pos   The position to get the enchanting power of.
     * @return The enchantment power for a given position in the world.
     */
    public float getEnchantingPower(World world, BlockPos pos) {

        final int x = pos.getX();
        final int y = pos.getY();
        final int z = pos.getZ();

        float power = 0;

        for (int zOffset = -1; zOffset <= 1; zOffset++) {

            for (int xOffset = -1; xOffset <= 1; xOffset++) {

                if ((zOffset != 0 || xOffset != 0) && world.isAirBlock(
                        new BlockPos(x + xOffset, y, z + zOffset)) && world.isAirBlock(
                        new BlockPos(x + xOffset, y + 1, z + zOffset))) {

                    power += getPower(world, new BlockPos(x + xOffset * 2, y, z + zOffset * 2));
                    power += getPower(world, new BlockPos(x + xOffset * 2, y + 1, z + zOffset * 2));

                    if (xOffset != 0 && zOffset != 0) {

                        power += getPower(world, new BlockPos(x + xOffset * 2, y, z + zOffset));
                        power += getPower(world, new BlockPos(x + xOffset * 2, y + 1, z + zOffset));
                        power += getPower(world, new BlockPos(x + xOffset, y, z + zOffset * 2));
                        power += getPower(world, new BlockPos(x + xOffset, y + 1, z + zOffset * 2));
                    }
                }
            }
        }

        return power;
    }

    public float getPower(World world, BlockPos pos) {
        return world.getBlockState(pos).getEnchantPowerBonus(world, pos);
    }

    public float getEnchantmentPower() {
        return Math.min(this.enchantmentPower, 30f);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity entityPlayer, int idx) {

        ItemStack itemStack;
        final Slot clickSlot = this.inventorySlots.get(idx);

        if (clickSlot != null && clickSlot.getHasStack()) {

            itemStack = clickSlot.getStack();

            if (itemStack == ItemStack.EMPTY) {
                return ItemStack.EMPTY;
            }
            final List<Slot> selectedSlots = new ArrayList<>();

            if (clickSlot.inventory instanceof PlayerInventory) {

                for (int x = 0; x < this.inventorySlots.size(); x++) {

                    final Slot advSlot = this.inventorySlots.get(x);

                    if (advSlot.isItemValid(itemStack)) {
                        selectedSlots.add(advSlot);
                    }
                }
            }
            else {
                for (int x = 0; x < this.inventorySlots.size(); x++) {

                    final Slot advSlot = this.inventorySlots.get(x);

                    if (advSlot.inventory instanceof PlayerInventory) {

                        if (advSlot.isItemValid(itemStack)) {
                            selectedSlots.add(advSlot);
                        }
                    }
                }
            }

            if (!itemStack.isEmpty()) {

                for (final Slot slot : selectedSlots) {

                    if (slot.isItemValid(itemStack) && !itemStack.isEmpty()) {

                        if (slot.getHasStack()) {

                            final ItemStack stack = slot.getStack();

                            if (!itemStack.isEmpty() && itemStack.isItemEqual(stack)) {

                                int maxSize = stack.getMaxStackSize();

                                if (maxSize > slot.getSlotStackLimit()) {
                                    maxSize = slot.getSlotStackLimit();
                                }

                                int placeAble = maxSize - stack.getCount();

                                if (itemStack.getCount() < placeAble) {
                                    placeAble = itemStack.getCount();
                                }

                                stack.grow(placeAble);
                                itemStack.shrink(placeAble);

                                if (itemStack.getCount() <= 0) {

                                    clickSlot.putStack(ItemStack.EMPTY);
                                    slot.onSlotChanged();
                                    this.detectAndSendChanges();

                                    return ItemStack.EMPTY;
                                }

                                this.detectAndSendChanges();
                            }
                        }
                        else {
                            int maxSize = itemStack.getMaxStackSize();

                            if (maxSize > slot.getSlotStackLimit()) {
                                maxSize = slot.getSlotStackLimit();
                            }

                            final ItemStack tmp = itemStack.copy();

                            if (tmp.getCount() > maxSize) {
                                tmp.setCount(maxSize);
                            }

                            itemStack.shrink(tmp.getCount());
                            slot.putStack(tmp);

                            if (itemStack.getCount() <= 0) {

                                clickSlot.putStack(ItemStack.EMPTY);
                                slot.onSlotChanged();
                                this.detectAndSendChanges();

                                return ItemStack.EMPTY;
                            }

                            this.detectAndSendChanges();
                        }
                    }
                }
            }

            clickSlot.putStack(!itemStack.isEmpty() ? itemStack.copy() : ItemStack.EMPTY);
        }

        this.detectAndSendChanges();

        return ItemStack.EMPTY;
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(PlayerEntity playerIn) {

        super.onContainerClosed(playerIn);
        this.clearContainer(playerIn, playerIn.world, this.tableInventory);
    }

    @Override
    public boolean canInteractWith(@Nonnull final PlayerEntity player) {

        return isWithinUsableDistance(canInteractWithCallable, player,
                AdvancedEnchantingBlocks.ADVANCED_ENCHANTING_TABLE.get());
    }
}
