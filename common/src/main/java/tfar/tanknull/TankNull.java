package tfar.tanknull;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import tfar.tanknull.init.ModBlockEntityTypes;
import tfar.tanknull.init.ModBlocks;
import tfar.tanknull.init.ModItems;
import tfar.tanknull.init.ModMenuTypes;
import tfar.tanknull.network.PacketHandler;
import tfar.tanknull.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class TankNull {

    public static final String MOD_ID = "tanknull";
    public static final String MOD_NAME = "TankNull";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This example has some
    // code that gets invoked by the entry point of the loader specific projects.
    public static void init() {
        Services.PLATFORM.registerAll(ModBlocks.class,BuiltInRegistries.BLOCK, Block.class);

        Class<BlockEntityType<?>> typeClass =(Class<BlockEntityType<?>>)(Object) BlockEntityType.class;
        Class<MenuType<?>> typeClass1 =(Class<MenuType<?>>)(Object) MenuType.class;
        Class<RecipeSerializer<?>> typeClass2 =(Class<RecipeSerializer<?>>)(Object) RecipeSerializer.class;

        Services.PLATFORM.registerAll(ModBlockEntityTypes.class,BuiltInRegistries.BLOCK_ENTITY_TYPE, typeClass);
        Services.PLATFORM.unfreeze(BuiltInRegistries.ITEM);
        Services.PLATFORM.registerAll(ModItems.class, BuiltInRegistries.ITEM, Item.class);
      //  Services.PLATFORM.registerAll(ModCreativeTabs.class,BuiltInRegistries.CREATIVE_MODE_TAB, CreativeModeTab.class);
        Services.PLATFORM.registerAll(ModMenuTypes.class,BuiltInRegistries.MENU, typeClass1);
      //  Services.PLATFORM.registerAll(ModRecipeSerializers.class,BuiltInRegistries.RECIPE_SERIALIZER,typeClass2);

        // It is common for all supported loaders to provide a similar feature that can not be used directly in the
        // common code. A popular way to get around this is using Java's built-in service loader feature to create
        // your own abstraction layer. You can learn more about this in our provided services class. In this example
        // we have an interface in the common code and use a loader specific implementation to delegate our call to
        // the platform specific approach.
        PacketHandler.registerPackets();
    }

    public static ResourceLocation id(String key) {
        return new ResourceLocation(MOD_ID,key);
    }
}