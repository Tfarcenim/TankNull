package tfar.tanknull.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import tfar.tanknull.MLFluidStack;
import tfar.tanknull.TankItem;
import tfar.tanknull.UseMode;
import tfar.tanknull.Utils;
import tfar.tanknull.inventory.FluidListTooltip;
import tfar.tanknull.inventory.StackListTooltip;
import tfar.tanknull.init.ModMenuTypes;
import tfar.tanknull.network.server.C2SButtonPacket;
import tfar.tanknull.network.server.C2SScrollSlotPacket;
import tfar.tanknull.network.server.KeybindAction;
import tfar.tanknull.platform.Services;
import tfar.tanknull.world.ClientData;

public class ModClient {

    public static double lastMouseX;
    public static double lastMouseY;

    public static void saveLastPos(MouseHandler mouseHandler) {
        lastMouseX = mouseHandler.xpos();
        lastMouseY = mouseHandler.ypos();
    }

    public static void keyPressed() {
        if (ModKeybinds.CYCLE_USE_MODE.consumeClick()) {
            C2SButtonPacket.send(KeybindAction.TOGGLE_USE_MODE);
        }
    }

    public static ClientTooltipComponent tooltipImage(TooltipComponent data) {
        if (data instanceof FluidListTooltip) {
            StackListTooltip<MLFluidStack> dankTooltip = (FluidListTooltip) data;
            return new ClientFluidListTooltip(dankTooltip);
        }
        return null;
    }

    public static Player getLocalPlayer() {
        return Minecraft.getInstance().player;
    }

    public static void setup() {
        MenuScreens.register(ModMenuTypes.TANK_1, TankScreen::t1);
        MenuScreens.register(ModMenuTypes.TANK_2, TankScreen::t2);
        MenuScreens.register(ModMenuTypes.TANK_3, TankScreen::t3);
        MenuScreens.register(ModMenuTypes.TANK_4, TankScreen::t4);
        MenuScreens.register(ModMenuTypes.TANK_5, TankScreen::t5);
        MenuScreens.register(ModMenuTypes.TANK_6, TankScreen::t6);
        MenuScreens.register(ModMenuTypes.TANK_7, TankScreen::t7);

        MenuScreens.register(ModMenuTypes.CONFIG, TankConfigScreen::new);
    }

    public static void renderFluidInGui(GuiGraphics guiGraphics, int x, int y, MLFluidStack stack) {
        String amount = stack.getAmount() > 1 ? Utils.formatLargeNumber(stack.getAmount()) : "";
        renderFluidInGui(guiGraphics,x,y,stack,amount);
    }

    public static void renderFluidInGui(GuiGraphics guiGraphics, int x, int y, MLFluidStack stack,String text) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        int color = Services.PLATFORM.getTint(stack);
        TextureAtlasSprite sprite = FluidSpriteCache.getStillTexture(stack);
        RenderSystem.setShaderColor((color >> 16 & 0xff) / 255f, (color >> 8 & 0xff) / 255f, (color & 0xff) / 255f, 1);
        RenderSystem.enableDepthTest();
        guiGraphics.blit(x, y, 0, 16, 16, sprite);
        RenderSystem.setShaderColor(1,1,1,1);
        StackSizeRenderer.renderSizeLabel(guiGraphics,Minecraft.getInstance().font, x,y,text,200);
        RenderSystem.disableBlend();
    }

    public static void renderFluidTooltip(GuiGraphics guiGraphics, int x, int y, MLFluidStack fluidStack) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        int color = Services.PLATFORM.getTint(fluidStack);
        TextureAtlasSprite sprite = FluidSpriteCache.getStillTexture(fluidStack);
        RenderSystem.setShaderColor((color >> 16 & 0xff) / 255f, (color >> 8 & 0xff) / 255f, (color & 0xff) / 255f, 1);

        RenderSystem.enableDepthTest();
        guiGraphics.blit(x, y, 400, 16, 16, sprite);
        RenderSystem.setShaderColor(1,1,1, 1);
        RenderSystem.disableDepthTest();

        String amount = fluidStack.getAmount() > 1 ? Utils.formatLargeNumber(fluidStack.getAmount()) : "";
        StackSizeRenderer.renderSizeLabel(guiGraphics,Minecraft.getInstance().font, x,y,amount,700);
    }

    public static void render(Gui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null)
            return;
        if (!(player.containerMenu instanceof InventoryMenu)) return;
        ItemStack bag = player.getMainHandItem();
        if (!(bag.getItem() instanceof TankItem)) {
            bag = player.getOffhandItem();
            if (!(bag.getItem() instanceof TankItem))
                return;
        }
        int xStart = screenWidth / 2 + previewX();
        int yStart = screenHeight + previewY();

        MLFluidStack toPlace = TankItem.getSelectedFluid(bag);

        if (!toPlace.isEmpty() && shouldPreview()) {
            renderFluidInGui(guiGraphics, xStart, yStart, toPlace);
        }
        UseMode mode = TankItem.getUseMode(bag);
        MutableComponent translate = Component.translatable(mode.translation());

        final int stringX = xStart + 8 - mc.font.width(translate) / 2;
        final int stringY = yStart + 16;
        guiGraphics.drawString(mc.font, translate, stringX, stringY, 0xffffff);
    }

    public static boolean onScroll(double delta) {
        Player player = Minecraft.getInstance().player;
        if (player!=null) {
            ItemStack main = player.getMainHandItem();
            ItemStack off = player.getOffhandItem();
            if (player.isCrouching() && (TankItem.isInteractive(main) || TankItem.isInteractive(off))) {
                boolean right = delta < 0;
                C2SScrollSlotPacket.send(right);
                return true;
            }
        }
        return false;
    }

    private static boolean shouldPreview() {
        return true;//Services.PLATFORM.showPreview();
    }

    private static int previewX() {
        return -150;//Services.PLATFORM.previewX();
    }

    private static int previewY() {
        return -30;//Services.PLATFORM.previewY();
    }

}
