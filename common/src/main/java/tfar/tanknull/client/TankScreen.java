package tfar.tanknull.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import tfar.tanknull.MLFluidStack;
import tfar.tanknull.TankNull;
import tfar.tanknull.inventory.FluidSlot;
import tfar.tanknull.menu.AbstractTankMenu;
import tfar.tanknull.platform.Services;

import javax.annotation.Nullable;

public class TankScreen extends AbstractContainerScreen<AbstractTankMenu> {

    static final ResourceLocation main_background = new ResourceLocation("textures/gui/container/generic_54.png");
    static final ResourceLocation background7 = TankNull.id("textures/container/gui/dank7.png");
    private final ResourceLocation background;

    @Nullable
    protected FluidSlot hoveredFluidSlot;

    public TankScreen(AbstractTankMenu menu, Inventory $$1, Component $$2, ResourceLocation background) {
        super(menu, $$1, $$2);
        this.background = background;
        this.imageHeight = 114 + menu.rows * 18;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        if (menu.rows < 7) {
            int i = (this.width - this.imageWidth) / 2;
            int j = (this.height - this.imageHeight) / 2;
            guiGraphics.blit(background, i, j, 0, 0, this.imageWidth, menu.rows * 18 + 17);
            guiGraphics.blit(background, i, j + menu.rows * 18 + 17, 0, 126, this.imageWidth, 96);
        } else {

        }
    }

    private void renderFluidSlot(GuiGraphics pGuiGraphics, FluidSlot pSlot) {
        int x = pSlot.x;
        int y = pSlot.y;
        MLFluidStack stack = pSlot.getFluid();
        boolean flag = false;

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(0.0F, 0.0F, 100.0F);

        if (flag) {
            pGuiGraphics.fill(x, y, x + 16, y + 16, 0x80ffffff);
        }

        if (!stack.isEmpty()) {
            Services.PLATFORM.renderFluidInSlot(pGuiGraphics, x, y, stack);
        }

        pGuiGraphics.pose().popPose();
    }

    /**
     * Renders the graphical user interface (GUI) element.
     *
     * @param pGuiGraphics the GuiGraphics object used for rendering.
     * @param pMouseX      the x-coordinate of the mouse cursor.
     * @param pMouseY      the y-coordinate of the mouse cursor.
     * @param pPartialTick the partial tick time.
     */
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(leftPos,topPos,0);
        int j2;
        int k2;
        for (int k = 0; k < this.menu.fluidSlots.size(); ++k) {
            FluidSlot slot = this.menu.fluidSlots.get(k);
            this.renderFluidSlot(pGuiGraphics, slot);

            if (this.isHovering(slot, pMouseX, pMouseY)) {
                this.hoveredFluidSlot = slot;
                j2 = slot.x;
                k2 = slot.y;
                renderSlotHighlight(pGuiGraphics, j2, k2, 0);
            }
        }

        pGuiGraphics.pose().popPose();

        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    private boolean isHovering(FluidSlot $$0, double $$1, double $$2) {
        return this.isHovering($$0.x, $$0.y, 16, 16, $$1, $$2);
    }

    ///////////////////////////////////////////////
    public static TankScreen t1(AbstractTankMenu container, Inventory playerinventory, Component component) {
        return new TankScreen(container, playerinventory, component, main_background);
    }

    public static TankScreen t2(AbstractTankMenu container, Inventory playerinventory, Component component) {
        return new TankScreen(container, playerinventory, component, main_background);
    }

    public static TankScreen t3(AbstractTankMenu container, Inventory playerinventory, Component component) {
        return new TankScreen(container, playerinventory, component, main_background);
    }

    public static TankScreen t4(AbstractTankMenu container, Inventory playerinventory, Component component) {
        return new TankScreen(container, playerinventory, component, main_background);
    }

    public static TankScreen t5(AbstractTankMenu container, Inventory playerinventory, Component component) {
        return new TankScreen(container, playerinventory, component, main_background);
    }

    public static TankScreen t6(AbstractTankMenu container, Inventory playerinventory, Component component) {
        return new TankScreen(container, playerinventory, component, main_background);
    }

    public static TankScreen t7(AbstractTankMenu container, Inventory playerinventory, Component component) {
        return new TankScreen(container, playerinventory, component, background7);
    }

}
