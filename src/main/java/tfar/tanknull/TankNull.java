package tfar.tanknull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import tfar.tanknull.client.ItemStackTankNullScreen;
import tfar.tanknull.client.TankNullClient;
import tfar.tanknull.inventory.TankNullItemStackFluidStackHandler;
import tfar.tanknull.network.Messages;

import static net.minecraftforge.common.MinecraftForge.EVENT_BUS;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TankNull.MODID)
public class TankNull {
    // Directly reference a log4j logger.

    public static final String MODID = "tanknull";

    private static final Logger LOGGER = LogManager.getLogger();

    public TankNull() {
        IEventBus iEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Register the setup method for modloading
        iEventBus.addListener(this::setup);
        // Register the doClientStuff method for modloading
        iEventBus.addListener(this::doClientStuff);
        EVENT_BUS.addListener(this::sponge);
        iEventBus.addGenericListener(Item.class, this::items);
        iEventBus.addGenericListener(Block.class, this::blocks);
        iEventBus.addGenericListener(ContainerType.class, this::menus);
        iEventBus.addGenericListener(TileEntityType.class, this::blockentities);
        iEventBus.addGenericListener(IRecipeSerializer.class, this::recipes);
    }

    private void blocks(RegistryEvent.Register<Block> event) {
        register(event.getRegistry(), "dock", RegistryObjects.dock);
    }

    private void items(RegistryEvent.Register<Item> event) {
        register(event.getRegistry(), "dock", RegistryObjects.dock_item);
        register(event.getRegistry(), "tank_1", RegistryObjects.tank_1);
        register(event.getRegistry(), "tank_2", RegistryObjects.tank_2);
        register(event.getRegistry(), "tank_3", RegistryObjects.tank_3);
        register(event.getRegistry(), "tank_4", RegistryObjects.tank_4);
        register(event.getRegistry(), "tank_5", RegistryObjects.tank_5);
        register(event.getRegistry(), "tank_6", RegistryObjects.tank_6);
        register(event.getRegistry(), "tank_7", RegistryObjects.tank_7);
    }

    private void menus(RegistryEvent.Register<ContainerType<?>> event) {
        register(event.getRegistry(), "tank_1", RegistryObjects.tank_1_container);
        register(event.getRegistry(), "tank_2", RegistryObjects.tank_2_container);
        register(event.getRegistry(), "tank_3", RegistryObjects.tank_3_container);
        register(event.getRegistry(), "tank_4", RegistryObjects.tank_4_container);
        register(event.getRegistry(), "tank_5", RegistryObjects.tank_5_container);
        register(event.getRegistry(), "tank_6", RegistryObjects.tank_6_container);
        register(event.getRegistry(), "tank_7", RegistryObjects.tank_7_container);

        register(event.getRegistry(), "tank_1_portable", RegistryObjects.tank_1_portable);
        register(event.getRegistry(), "tank_2_portable", RegistryObjects.tank_2_portable);
        register(event.getRegistry(), "tank_3_portable", RegistryObjects.tank_3_portable);
        register(event.getRegistry(), "tank_4_portable", RegistryObjects.tank_4_portable);
        register(event.getRegistry(), "tank_5_portable", RegistryObjects.tank_5_portable);
        register(event.getRegistry(), "tank_6_portable", RegistryObjects.tank_6_portable);
        register(event.getRegistry(), "tank_7_portable", RegistryObjects.tank_7_portable);
    }

    private void blockentities(RegistryEvent.Register<TileEntityType<?>> event) {
        register(event.getRegistry(), "dock", RegistryObjects.blockentity);
    }

    private void recipes(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        register(event.getRegistry(), "upgrade", RegistryObjects.upgrade);
    }

    private static <T extends IForgeRegistryEntry<T>> void register(IForgeRegistry<T> registry, ResourceLocation name, T obj) {
        registry.register(obj.setRegistryName(name));
    }

    private static <T extends IForgeRegistryEntry<T>> void register(IForgeRegistry<T> registry, String name, T obj) {
        register(registry, new ResourceLocation(MODID, name), obj);
    }

    private void setup(final FMLCommonSetupEvent event) {
        Messages.registerMessages(MODID);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        ScreenManager.registerFactory(RegistryObjects.tank_1_container, BlockTankNullScreen::new);
        ScreenManager.registerFactory(RegistryObjects.tank_2_container, BlockTankNullScreen::new);
        ScreenManager.registerFactory(RegistryObjects.tank_3_container, BlockTankNullScreen::new);
        ScreenManager.registerFactory(RegistryObjects.tank_4_container, BlockTankNullScreen::new);
        ScreenManager.registerFactory(RegistryObjects.tank_5_container, BlockTankNullScreen::new);
        ScreenManager.registerFactory(RegistryObjects.tank_6_container, BlockTankNullScreen::new);
        ScreenManager.registerFactory(RegistryObjects.tank_7_container, BlockTankNullScreen::new);

        ScreenManager.registerFactory(RegistryObjects.tank_1_portable, ItemStackTankNullScreen::new);
        ScreenManager.registerFactory(RegistryObjects.tank_2_portable, ItemStackTankNullScreen::new);
        ScreenManager.registerFactory(RegistryObjects.tank_3_portable, ItemStackTankNullScreen::new);
        ScreenManager.registerFactory(RegistryObjects.tank_4_portable, ItemStackTankNullScreen::new);
        ScreenManager.registerFactory(RegistryObjects.tank_5_portable, ItemStackTankNullScreen::new);
        ScreenManager.registerFactory(RegistryObjects.tank_6_portable, ItemStackTankNullScreen::new);
        ScreenManager.registerFactory(RegistryObjects.tank_7_portable, ItemStackTankNullScreen::new);

        TankNullClient.MODE = new KeyBinding("key.tanknull.mode", GLFW.GLFW_KEY_O, "key.categories.tanknull");
        ClientRegistry.registerKeyBinding(TankNullClient.MODE);
    }

    private void sponge(TickEvent.PlayerTickEvent e) {
        PlayerEntity player = e.player;
        World world = player.world;
        ItemStack stack = player.getHeldItemMainhand();
        if (stack.getItem() instanceof TankNullItem) {
            if (Utils.isSponge(stack, player)) {
                if (e.phase == TickEvent.Phase.END && !world.isRemote) {
                    BlockPos origin = player.getPosition();
                    final BlockPos.Mutable mutable = new BlockPos.Mutable();
                    for (int x = -4; x < 4; x++)
                        for (int y = -4; y < 4; y++)
                            for (int z = -4; z < 4; z++) {
                                mutable.setPos(x + origin.getX(), y + origin.getY(), z + origin.getZ());
                                BlockState state = world.getBlockState(mutable);
                                if (state.getBlock() instanceof IBucketPickupHandler) {
                                    Fluid fluid = ((IBucketPickupHandler) state.getBlock()).pickupFluid(world, mutable, state);
                                    TankNullItemStackFluidStackHandler.create(stack).fill1000(IFluidHandler.FluidAction.EXECUTE, fluid);
                                    }
                                }
                }
            }
        }
    }
}
