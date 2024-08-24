package tfar.tanknull.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import tfar.tanknull.MLFluidStack;
import tfar.tanknull.inventory.StackListTooltip;

public class ClientFluidListTooltip extends ClientStackListTooltip<MLFluidStack> {
    public ClientFluidListTooltip(StackListTooltip<MLFluidStack> bundleTooltip) {
        super(bundleTooltip);
    }

    @Override
    protected void renderStack(GuiGraphics graphics, MLFluidStack stack, Font font, int x, int y) {
        if (!stack.isEmpty()) {
            ModClient.renderFluidTooltip(graphics, x, y, stack);
        }
    }
}
