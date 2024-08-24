package tfar.tanknull.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import tfar.tanknull.inventory.StackListTooltip;

public class ClientItemListTooltip extends ClientStackListTooltip<ItemStack> {
    public ClientItemListTooltip(StackListTooltip<ItemStack> bundleTooltip) {
        super(bundleTooltip);
    }

    @Override
    protected void renderStack(GuiGraphics graphics, ItemStack stack, Font font, int x, int y) {
        graphics.renderItem(stack, x, y, 0);
        graphics.renderItemDecorations(font, stack, x, y);
    }
}
