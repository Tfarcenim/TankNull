package tfar.tanknull.platform;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.wrappers.BucketPickupHandlerWrapper;
import net.minecraftforge.fluids.capability.wrappers.FluidBlockWrapper;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tfar.tanknull.*;
import tfar.tanknull.inventory.FluidInventory;
import tfar.tanknull.inventory.ForgeFluidInventory;
import tfar.tanknull.network.client.S2CModPacket;
import tfar.tanknull.network.server.C2SModPacket;
import tfar.tanknull.platform.services.IPlatformHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import tfar.tanknull.world.TankSavedData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static tfar.tanknull.client.FluidSpriteCache.SPRITE_CACHE;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    @Override
    public Component getDisplayName(MLFluidStack fluidStack) {
        return convert(fluidStack).getDisplayName();
    }

    @Override
    public String getTranslationKey(MLFluidStack fluidStack) {
        return convert(fluidStack).getTranslationKey();
    }

    @Override
    public FluidInventory create(TankStats stats, TankSavedData data) {
        return new ForgeFluidInventory(stats, data);
    }

    @Override
    public DockBlockEntity create(BlockPos pos, BlockState state) {
        return new DockBlockEntityForge(pos, state);
    }

    @Override
    public <F> void registerAll(Map<String, ? extends F> map, Registry<F> registry, Class<? extends F> filter) {
        List<Pair<ResourceLocation, Supplier<?>>> list = TankNullForge.registerLater.computeIfAbsent(registry, k -> new ArrayList<>());
        for (Map.Entry<String, ? extends F> entry : map.entrySet()) {
            list.add(Pair.of(TankNull.id(entry.getKey()), entry::getValue));
        }
    }

    int i;

    @Override
    public <MSG extends S2CModPacket> void registerClientPacket(Class<MSG> packetLocation, Function<FriendlyByteBuf, MSG> reader) {
        PacketHandlerForge.INSTANCE.registerMessage(i++, packetLocation, MSG::write, reader, PacketHandlerForge.wrapS2C());
    }

    @Override
    public <MSG extends C2SModPacket> void registerServerPacket(Class<MSG> packetLocation, Function<FriendlyByteBuf, MSG> reader) {
        PacketHandlerForge.INSTANCE.registerMessage(i++, packetLocation, MSG::write, reader, PacketHandlerForge.wrapC2S());
    }


    @Override
    public void sendToClient(S2CModPacket msg, ServerPlayer player) {
        PacketHandlerForge.sendToClient(msg, player);
    }

    @Override
    public void sendToServer(C2SModPacket msg) {
        PacketHandlerForge.sendToServer(msg);
    }

    @Override
    public <F> void unfreeze(Registry<F> registry) {
        ((MappedRegistry<F>) registry).unfreeze();
    }

    ////////////////Static helpers

    public static FluidStack convert(MLFluidStack fluidStack) {
        if (fluidStack.isEmpty()) {
            return FluidStack.EMPTY;
        }
        return new FluidStack(fluidStack.getFluid(), fluidStack.getAmount(), fluidStack.getTag());
    }

    public static MLFluidStack convert(FluidStack fluidStack) {
        if (fluidStack.isEmpty()) {
            return MLFluidStack.EMPTY;
        }
        return new MLFluidStack(fluidStack.getFluid(), fluidStack.getAmount(), fluidStack.getTag());
    }

    public static FluidInventory.Action action(IFluidHandler.FluidAction action) {
        return switch (action) {
            case EXECUTE -> FluidInventory.Action.EXECUTE;
            case SIMULATE -> FluidInventory.Action.SIMULATE;
        };
    }

    public static IFluidHandler.FluidAction action(FluidInventory.Action action) {
        return switch (action) {
            case EXECUTE -> IFluidHandler.FluidAction.EXECUTE;
            case SIMULATE -> IFluidHandler.FluidAction.SIMULATE;
        };
    }

    @Override
    public MLFluidStack extractFluid(ItemStack stack, MLFluidStack target) {
        return MLFluidStack.EMPTY;
    }

    @Override
    public MLFluidStack extractAnyFluid(ItemStack stack, int max, FluidInventory.Action action) {
        if (!stack.isEmpty()) {
            IFluidHandlerItem iFluidHandlerItem = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
            if (iFluidHandlerItem != null) {
                FluidStack drained = iFluidHandlerItem.drain(max, action(action));
                return convert(drained);
            }
        }
        return MLFluidStack.EMPTY;
    }

    @Override
    public MLFluidStack getStoredFluid(ItemStack stack) {
        if (!stack.isEmpty()) {
            IFluidHandlerItem iFluidHandlerItem = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
            if (iFluidHandlerItem != null) {
                FluidStack drained = iFluidHandlerItem.drain(Integer.MAX_VALUE, action(FluidInventory.Action.SIMULATE));
                return convert(drained);
            }
        }
        return MLFluidStack.EMPTY;
    }

    @Override
    public int simulateFill(ItemStack stack, MLFluidStack fluid) {
        IFluidHandlerItem iFluidHandlerItem = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
        if (iFluidHandlerItem != null) {
            int filled = iFluidHandlerItem.fill(convert(fluid), action(FluidInventory.Action.SIMULATE));
            return filled;
        }
        return 0;
    }

    @Override
    public void transferContainerToTank(ItemStack container, FluidInventory fluidInventory, int maxFill, int tank, Player player, boolean simulate) {
        FluidActionResult fluidActionResult = FluidUtil.tryEmptyContainerAndStow(container, (ForgeFluidInventory.ForgeSlot) fluidInventory.getWrapper(tank), new InvWrapper(player.getInventory()), maxFill, player, !simulate);
        if (fluidActionResult.isSuccess()) {
            player.containerMenu.setCarried(fluidActionResult.getResult());
        }
    }

    @Override
    public void transferTankToContainer(ItemStack container, FluidInventory fluidInventory, int maxDrain, int tank, Player player, boolean simulate) {
        FluidActionResult fluidActionResult = FluidUtil.tryFillContainerAndStow(container, (ForgeFluidInventory.ForgeSlot) fluidInventory.getWrapper(tank), new InvWrapper(player.getInventory()), maxDrain, player, !simulate);
        if (fluidActionResult.isSuccess()) {
            player.containerMenu.setCarried(fluidActionResult.getResult());
        }
    }

    public ResourceLocation getSpriteLocation(MLFluidStack stack) {
        FluidStack forgeStack = convert(stack);
        return IClientFluidTypeExtensions.of(stack.getFluid()).getStillTexture(forgeStack);
    }

    @Override
    public TextureAtlasSprite getSprite(MLFluidStack stack) {
        return SPRITE_CACHE.getUnchecked(getSpriteLocation(stack));
    }

    @Override
    public int getTint(MLFluidStack stack) {
        FluidStack forgeStack = convert(stack);
        return IClientFluidTypeExtensions.of(stack.getFluid()).getTintColor(forgeStack);
    }

    @Override
    public FluidInventory.Slot createWrapper(FluidInventory inventory, int tank) {
        return ((ForgeFluidInventory) inventory).makeWrapper(tank);
    }

    @Override
    public void tryPickUpFluid(@NotNull FluidInventory destination, @Nullable Player playerIn, Level level, BlockPos pos, Direction side) {

        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        IFluidHandler targetFluidHandler;
        if (block instanceof IFluidBlock) {
            targetFluidHandler = new FluidBlockWrapper((IFluidBlock) block, level, pos);
        } else if (block instanceof BucketPickup) {
            targetFluidHandler = new BucketPickupHandlerWrapper((BucketPickup) block, level, pos);
        } else {
            Optional<IFluidHandler> fluidHandler = FluidUtil.getFluidHandler(level, pos, side).resolve();
            if (fluidHandler.isEmpty()) {
                return;
            }
            targetFluidHandler = fluidHandler.get();
        }
        FluidStack fluidStack = FluidUtil.tryFluidTransfer((IFluidHandler) destination, targetFluidHandler, Integer.MAX_VALUE, true);

        if (fluidStack.isEmpty()) {

        }
        // FluidUtil.tryPickUpFluid(destination, playerIn, level, pos, side);
    }

    @Override
    public boolean tryPlaceFluid(@Nullable Player player, Level level, InteractionHand hand, BlockPos pos, FluidInventory fluidSource, MLFluidStack resource) {
        return FluidUtil.tryPlaceFluid(player, level, hand, pos, (IFluidHandler) fluidSource, convert(resource));
    }
}