package tfar.tanknull;

import net.minecraft.block.Blocks;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
    RegistryObjects.ITEMS.register(iEventBus);
    RegistryObjects.BLOCKS.register(iEventBus);
    RegistryObjects.BLOCK_ENTITIES.register(iEventBus);
    RegistryObjects.MENUS.register(iEventBus);
    EVENT_BUS.addListener(this::sponge);
  }

  private void setup(final FMLCommonSetupEvent event) {
    Messages.registerMessages(MODID);
  }

  private void doClientStuff(final FMLClientSetupEvent event) {
    ScreenManager.registerFactory(RegistryObjects.block_container.get(), TileTankNullScreen::new);
    ScreenManager.registerFactory(RegistryObjects.item_container.get(), ItemStackTankNullScreen::new);
    TankNullClient.MODE = new KeyBinding("key.tanknull.mode", GLFW.GLFW_KEY_I, "key.categories.tanknull");
    ClientRegistry.registerKeyBinding(TankNullClient.MODE);
  }

  private void sponge(TickEvent.PlayerTickEvent e) {
    PlayerEntity player = e.player;
    World world = player.world;
    ItemStack stack = player.getHeldItemMainhand();
    if (stack.getItem() instanceof TankNullItem) {
      if (Utils.isSponge(stack,player)){
      if (e.phase == TickEvent.Phase.END && !world.isRemote) {
        BlockPos origin = player.getPosition();
        final BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int x = -4; x < 4; x++)
          for (int y = -4; y < 4; y++)
            for (int z = -4; z < 4; z++) {
              mutable.setPos(x + origin.getX(), y + origin.getY(), z + origin.getZ());
              IFluidState fluidState = world.getFluidState(mutable);
              if (fluidState.isSource()) {
                world.setBlockState(mutable, Blocks.AIR.getDefaultState());
                TankNullItemStackFluidStackHandler.create(stack).fill1000(IFluidHandler.FluidAction.EXECUTE,
                        new FluidStack(fluidState.getFluid(), 1000));
              }
            }
      }

      }
    }
  }
}
