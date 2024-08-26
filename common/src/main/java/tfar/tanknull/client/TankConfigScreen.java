package tfar.tanknull.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import tfar.tanknull.menu.TankConfigMenu;

import java.util.function.Supplier;

public class TankConfigScreen extends AbstractContainerScreen<TankConfigMenu> {

    private static final ResourceLocation DEMO_BACKGROUND_LOCATION = new ResourceLocation("textures/gui/demo_background.png");

    public TankConfigScreen(TankConfigMenu $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2);
        imageWidth += 78;
    }

    @Override
    protected void init() {
        super.init();

        Button button = new Button(leftPos + 9, topPos + 24, 90, 16,Component.empty(), b -> sendButtonToServer(TankConfigMenu.ButtonAction.CYCLE_SORT_TYPE), Supplier::get) {
            @Override
            public Component getMessage() {
                return Component.translatable("tanknull.sorting_type."+menu.getSortingType());
            }
        };
        this.addRenderableWidget(button);

        Button autoSort = new Button(leftPos + 9, topPos + 44, 90, 16,Component.empty(), b -> sendButtonToServer(TankConfigMenu.ButtonAction.TOGGLE_AUTO_SORT), Supplier::get) {
            @Override
            public Component getMessage() {
                return Component.translatable("tanknull.auto_sort").append(" ")
                        .append(menu.autoSort() ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
            }
        };
        this.addRenderableWidget(autoSort);

     /*   Button autoBucket = new Button(leftPos + 9, topPos + 44, 90, 16,Component.empty(), b -> sendButtonToServer(TankConfigMenu.ButtonAction.CYCLE_BUCKET_SIZE), Supplier::get) {
            @Override
            public Component getMessage() {
                return Component.translatable("tanknull.auto_sort").append(" ")
                        .append(menu.autoSort() ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
            }
        };
        this.addRenderableWidget(autoBucket);*/



        Button xButton = new Button(leftPos + 235,topPos + 4, 12, 12,Component.literal("x"), b -> {
            sendButtonToServer(TankConfigMenu.ButtonAction.CLOSE);
        }, Supplier::get) {};
        addRenderableWidget(xButton);
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
