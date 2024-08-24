package tfar.tanknull.platform;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import tfar.tanknull.FluidSpriteCache;
import tfar.tanknull.MLFluidStack;
import tfar.tanknull.Utils;
import tfar.tanknull.client.StackSizeRenderer;
import tfar.tanknull.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public void renderFluidInSlot(GuiGraphics matrices, int x, int y, MLFluidStack fluidStack) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        FluidRenderHandler fluidRenderHandler = FluidRenderHandlerRegistry.INSTANCE.get(fluidStack.getFluid());
        int color = fluidRenderHandler.getFluidColor(null,null,null);
        TextureAtlasSprite sprite = FluidSpriteCache.getStillTexture(fluidStack);
        RenderSystem.setShaderColor((color >> 16 & 0xff) / 255f, (color >> 8 & 0xff) / 255f, (color & 0xff) / 255f, 1);
        RenderSystem.enableDepthTest();

        matrices.blit(x, y, 0, 16, 16, sprite);
        RenderSystem.setShaderColor(1,1,1,1);

        String s = Utils.formatLargeNumber(fluidStack.getAmount()/81);
        StackSizeRenderer.renderSizeLabel(matrices, Minecraft.getInstance().font,x,y,s);
    }

    ////////////////////////////

}
