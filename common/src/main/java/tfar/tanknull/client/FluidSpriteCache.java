package tfar.tanknull.client;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import tfar.tanknull.MLFluidStack;
import tfar.tanknull.platform.Services;

public class FluidSpriteCache {
    public static final LoadingCache<ResourceLocation, TextureAtlasSprite> SPRITE_CACHE = buildCache();

    public static TextureAtlasSprite getStillTexture(MLFluidStack fluid) {
        return Services.PLATFORM.getSprite(fluid);
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
