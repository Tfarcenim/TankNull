package tfar.tanknull.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import tfar.tanknull.MLFluidStack;
import tfar.tanknull.TankNull;
import tfar.tanknull.inventory.ClickAction;
import tfar.tanknull.inventory.FluidSlot;
import tfar.tanknull.menu.AbstractTankMenu;
import tfar.tanknull.network.server.C2SClickFluidSlotPacket;
import tfar.tanknull.platform.Services;

import javax.annotation.Nullable;
import java.util.List;

public class TankScreen extends AbstractContainerScreen<AbstractTankMenu> {

    static final ResourceLocation main_background = new ResourceLocation("textures/gui/container/generic_54.png");
    static final ResourceLocation background7 = TankNull.id("textures/container/gui/dank7.png");
    private final ResourceLocation background;

    @Nullable
    protected FluidSlot hoveredFluidSlot;

    @Nullable
    protected FluidSlot lastClickFluidSlot;

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

    /**
     * Called when a mouse button is clicked within the GUI element.
     * <p>
     *
     * @param pMouseX the X coordinate of the mouse.
     * @param pMouseY the Y coordinate of the mouse.
     * @param pButton the button that was clicked.
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     */
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        super.mouseClicked(pMouseX, pMouseY, pButton);
        InputConstants.Key mouseKey = InputConstants.Type.MOUSE.getOrCreate(pButton);
        FluidSlot slot = this.findFluidSlot(pMouseX, pMouseY);
        if (slot != null) {
            MLFluidStack fluidStack = slot.getFluid();

            ItemStack carried = menu.getCarried();

            ClickAction action = null;

            if (fluidStack.isEmpty()) {
                if (pButton == GLFW.GLFW_MOUSE_BUTTON_1) {//left click

                } else if (pButton == GLFW.GLFW_MOUSE_BUTTON_2) {//right click
                    action = ClickAction.DEPOSIT_ONE;
                }
            }
            if (action != null) {
                handleFluidSlotClick(menu.containerId, slot.index, pButton, action, Minecraft.getInstance().player);
            }
        }
        return true;
    }


    public void handleFluidSlotClick(int pContainerId, int pSlotId, int pMouseButton, ClickAction pClickType, Player player) {
        AbstractTankMenu abstractcontainermenu = (AbstractTankMenu) player.containerMenu;
        if (pContainerId != abstractcontainermenu.containerId) {
         //   LOGGER.warn("Ignoring click in mismatching container. Click in {}, player has {}.", pContainerId, abstractcontainermenu.containerId);
        } else {
            NonNullList<FluidSlot> nonnulllist = abstractcontainermenu.fluidSlots;
            int i = nonnulllist.size();
            List<MLFluidStack> list = Lists.newArrayListWithCapacity(i);

            for (FluidSlot slot : nonnulllist) {
                list.add(slot.getFluid().copy());
            }

          //  abstractcontainermenu.clicked(pSlotId, pMouseButton, pClickType, player);
            Int2ObjectMap<MLFluidStack> int2objectmap = new Int2ObjectOpenHashMap<>();

            for (int j = 0; j < i; ++j) {
                MLFluidStack fluidStack = list.get(j);
                MLFluidStack fluidStack1 = nonnulllist.get(j).getFluid();
                if (!fluidStack.isFluidStackIdentical(fluidStack1)) {
                    int2objectmap.put(j, fluidStack1.copy());
                }
            }

            Services.PLATFORM.sendToServer(new C2SClickFluidSlotPacket(pContainerId, abstractcontainermenu.getStateId(), pSlotId, pMouseButton, pClickType, abstractcontainermenu.getCarried().copy(), int2objectmap));
         //   lastClickFluidSlot = slot;
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

    @Nullable
    private FluidSlot findFluidSlot(double pMouseX, double pMouseY) {
        for (int i = 0; i < this.menu.fluidSlots.size(); ++i) {
            FluidSlot slot = this.menu.fluidSlots.get(i);
            if (this.isHovering(slot, pMouseX, pMouseY)) {
                return slot;
            }
        }

        return null;
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
        pGuiGraphics.pose().translate(leftPos, topPos, 0);
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
