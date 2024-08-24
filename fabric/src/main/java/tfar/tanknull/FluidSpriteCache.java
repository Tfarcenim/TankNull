package tfar.tanknull;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

public class FluidSpriteCache {

    private static final LoadingCache<ResourceLocation, TextureAtlasSprite> SPRITE_CACHE = buildCache();

    public static TextureAtlasSprite getStillTexture(MLFluidStack fluid) {
        return FluidRenderHandlerRegistry.INSTANCE.get(fluid.getFluid())
                .getFluidSprites(null,null,null)[0];
    }

    public static TextureAtlasSprite getFlowingTexture(MLFluidStack fluid) {
        return FluidRenderHandlerRegistry.INSTANCE.get(fluid.getFluid())
                .getFluidSprites(null,null,null)[1];
    }

    public static void invalidateSpriteCache() {
        SPRITE_CACHE.invalidateAll();
    }

    private static LoadingCache<ResourceLocation, TextureAtlasSprite> buildCache() {
        //noinspection deprecation
        return CacheBuilder.newBuilder()
                .maximumSize(100)
                .build(CacheLoader.from(key -> Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(key)));
    }

}
