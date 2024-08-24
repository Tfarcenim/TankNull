package tfar.tanknull;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public class FluidSpriteCache {
    private static final LoadingCache<ResourceLocation, TextureAtlasSprite> SPRITE_CACHE = buildCache();

    public static TextureAtlasSprite getStillTexture(FluidStack fluid) {
        return SPRITE_CACHE.getUnchecked(IClientFluidTypeExtensions.of(fluid.getFluid()).getStillTexture(fluid));
    }

    public static TextureAtlasSprite getFlowingTexture(FluidStack fluid) {
        return SPRITE_CACHE.getUnchecked(IClientFluidTypeExtensions.of(fluid.getFluid()).getFlowingTexture(fluid));
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
