package zundubcore.advancedenchanting.api.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

import zundubcore.advancedenchanting.client.gui.AdvancedEnchantingTableScreen;

/**
 * This event is fired on the client when the info box to the left of the advanced table gui is
 * rendered. This event can be used to add/remove content from this box.
 */
@OnlyIn(Dist.CLIENT)
public class InfoBoxEvent extends Event {

    private final AdvancedEnchantingTableScreen screen;
    private final List<String> info;

    public InfoBoxEvent(AdvancedEnchantingTableScreen screen, List<String> info) {
        super();
        this.screen = screen;
        this.info = info;
    }

    public AdvancedEnchantingTableScreen getScreen() {
        return this.screen;
    }

    public List<String> getInfo() {
        return this.info;
    }
}
