package zundubcore.advancedenchanting.tileentity;

import net.minecraft.nbt.CompoundNBT;

import java.awt.*;

import zundubcore.advancedenchanting.init.AdvancedEnchantingTileEntityTypes;

public class DecorativeBookTileEntity extends FloatingBookTileEntity {

    public float height = 0f;
    public int color = Color.WHITE.getRGB();

    public DecorativeBookTileEntity() {
        super(AdvancedEnchantingTileEntityTypes.DECORATIVE_BOOK.get());
    }

    public void decreaseHeight() {

        this.height -= 0.05f;

        if (this.height < -0.35f) {
            this.height = -0.35f;
        }
    }

    public void increaseHeight() {

        this.height += 0.05f;

        if (this.height > 0.35f) {
            this.height = 0.35f;
        }
    }

    @Override
    public void read(CompoundNBT dataTag) {

        this.height = dataTag.getFloat("Height");
        this.color = dataTag.getInt("Color");
    }

    @Override
    public CompoundNBT write(CompoundNBT dataTag) {

        dataTag.putFloat("Height", this.height);
        dataTag.putInt("Color", this.color);

        return dataTag;
    }
}
