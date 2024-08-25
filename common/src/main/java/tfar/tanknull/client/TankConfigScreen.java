package tfar.tanknull.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import tfar.tanknull.menu.TankConfigMenu;

public class TankConfigScreen extends AbstractContainerScreen<TankConfigMenu> {

    private static final ResourceLocation DEMO_BACKGROUND_LOCATION = new ResourceLocation("textures/gui/demo_background.png");


    public TankConfigScreen(TankConfigMenu $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2);
        imageWidth += 78;
    }

    @Override
    protected void init() {
        super.init();
        DynamicTooltip tooltip = DynamicTooltip.dynamic(() -> Component.translatable("tanknull.sorting_type."+menu.getSortingType()));

        this.addRenderableWidget(Button.builder(Component.literal("Sort: "), b -> {
            sendButtonToServer(TankConfigMenu.ButtonAction.CHANGE_SORT_TYPE);
            tooltip.dirty = true;
        })
                .pos(leftPos + 8, topPos + 24)
                .size( 90, 16)
                .tooltip(tooltip).build());
    }

    private void sendButtonToServer(TankConfigMenu.ButtonAction action) {
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, action.ordinal());
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        super.renderBackground(guiGraphics);
        int x = (this.width - 248) / 2;
        int y = (this.height - 166) / 2;
        guiGraphics.blit(DEMO_BACKGROUND_LOCATION, x, y, 0, 0, 248, 166);
    }
}
