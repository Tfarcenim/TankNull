package tfar.tanknull.inventory;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.List;

public class StackListTooltip<T> implements TooltipComponent {
    private final List<T> stacks;
    private final int selected;

    public StackListTooltip(List<T> stacks, int selected) {
        this.stacks = stacks;
        this.selected = selected;
    }

    public List<T> stacks() {
        return stacks;
    }

    public int selected() {
        return selected;
    }

    @Override
    public String toString() {
        return "StackListTooltip[" +
                "fluids=" + stacks + ", " +
                "selected=" + selected + ']';
    }
}
