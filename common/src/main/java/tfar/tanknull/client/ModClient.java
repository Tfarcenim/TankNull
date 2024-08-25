package tfar.tanknull.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import tfar.tanknull.MLFluidStack;
import tfar.tanknull.Utils;
import tfar.tanknull.inventory.FluidListTooltip;
import tfar.tanknull.inventory.StackListTooltip;
import tfar.tanknull.init.ModMenuTypes;
import tfar.tanknull.platform.Services;

public class ModClient {

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

    public static void renderFluidInGui(GuiGraphics matrices, int x, int y, MLFluidStack stack) {
        String amount = stack.getAmount() > 1 ? Utils.formatLargeNumber(stack.getAmount()) : "";
        renderFluidInGui(matrices,x,y,stack,amount);
    }

    public static void renderFluidInGui(GuiGraphics matrices, int x, int y, MLFluidStack stack,String text) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        int color = Services.PLATFORM.getTint(stack);
        TextureAtlasSprite sprite = FluidSpriteCache.getStillTexture(stack);
        RenderSystem.setShaderColor((color >> 16 & 0xff) / 255f, (color >> 8 & 0xff) / 255f, (color & 0xff) / 255f, 1);
        RenderSystem.enableDepthTest();
        matrices.blit(x, y, 0, 16, 16, sprite);
        RenderSystem.setShaderColor(1,1,1,1);
        StackSizeRenderer.renderSizeLabel(matrices,Minecraft.getInstance().font, x,y,text);
        RenderSystem.disableBlend();
    }

    public static void renderFluidTooltip(GuiGraphics matrices, int x, int y, MLFluidStack fluidStack) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        int color = Services.PLATFORM.getTint(fluidStack);
        TextureAtlasSprite sprite = FluidSpriteCache.getStillTexture(fluidStack);
        RenderSystem.setShaderColor((color >> 16 & 0xff) / 255f, (color >> 8 & 0xff) / 255f, (color & 0xff) / 255f, 1);

        RenderSystem.enableDepthTest();

        matrices.blit(x, y, 400, 16, 16, sprite);

        RenderSystem.setShaderColor(1,1,1, 1);


        String amount = fluidStack.getAmount() > 1 ? Utils.formatLargeNumber(fluidStack.getAmount()) : "";
        StackSizeRenderer.renderSizeLabel(matrices,Minecraft.getInstance().font, x,y,amount);
    }

}
