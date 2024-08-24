package tfar.tanknull.platform;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import tfar.tanknull.MLFluidStack;
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
    public TextureAtlasSprite getSprite(MLFluidStack stack) {
        return FluidRenderHandlerRegistry.INSTANCE.get(stack.getFluid())
                .getFluidSprites(null,null,null)[0];
    }

    ////////////////////////////

}
