package tfar.tanknull.platform;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.apache.commons.lang3.tuple.Pair;
import tfar.tanknull.*;
import tfar.tanknull.client.StackSizeRenderer;
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
import java.util.function.Function;
import java.util.function.Supplier;

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
        return convertToForge(fluidStack).getDisplayName();
    }

    @Override
    public String getTranslationKey(MLFluidStack fluidStack) {
        return convertToForge(fluidStack).getTranslationKey();
    }

    @Override
    public FluidInventory create(TankStats stats, TankSavedData data) {
        return new ForgeFluidInventory(stats,data);
    }

    @Override
    public DockBlockEntity create(BlockPos pos, BlockState state) {
        return new DockBlockEntity(pos, state);
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
    public void renderFluidInSlot(GuiGraphics matrices, int x, int y, MLFluidStack stack) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(stack.getFluid());
        FluidStack fluidStack = ForgePlatformHelper.convertToForge(stack);
        int color = renderProperties.getTintColor(fluidStack);
        TextureAtlasSprite sprite = FluidSpriteCache.getStillTexture(fluidStack);
        RenderSystem.setShaderColor((color >> 16 & 0xff) / 255f, (color >> 8 & 0xff) / 255f, (color & 0xff) / 255f, 1);
        RenderSystem.enableDepthTest();

        matrices.blit(x, y, 0, 16, 16, sprite);

        String amount = stack.getAmount() > 1 ? Utils.formatLargeNumber(stack.getAmount()) : "";
        StackSizeRenderer.renderSizeLabel(matrices,Minecraft.getInstance().font, x,y,amount);
    }

    @Override
    public <F> void unfreeze(Registry<F> registry) {
        ((MappedRegistry<F>)registry).unfreeze();
    }

    ////////////////Static helpers


    public static FluidStack convertToForge(MLFluidStack fluidStack) {
        if (fluidStack.isEmpty()) {
            return FluidStack.EMPTY;
        } return new FluidStack(fluidStack.getFluid(),fluidStack.getAmount(),fluidStack.getTag());
    }

    public static MLFluidStack convert(FluidStack fluidStack) {
        if (fluidStack.isEmpty()) {
            return MLFluidStack.EMPTY;
        } return new MLFluidStack(fluidStack.getFluid(),fluidStack.getAmount(),fluidStack.getTag());
    }

    public static FluidInventory.Action action(IFluidHandler.FluidAction action) {
        switch (action){
            case EXECUTE -> {
                return FluidInventory.Action.EXECUTE;
            }
            case SIMULATE -> {
                return FluidInventory.Action.SIMULATE;
            }
        }
        return null;
    }

    public static IFluidHandler.FluidAction action(FluidInventory.Action action) {
        switch (action){
            case EXECUTE -> {
                return IFluidHandler.FluidAction.EXECUTE;
            }
            case SIMULATE -> {
                return IFluidHandler.FluidAction.SIMULATE;
            }
        }
        return null;
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
                FluidStack drained = iFluidHandlerItem.drain(max,action(action));
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
                FluidStack drained = iFluidHandlerItem.drain(Integer.MAX_VALUE,action(FluidInventory.Action.SIMULATE));
                return convert(drained);
            }
        }
        return MLFluidStack.EMPTY;
    }

    @Override
    public int simulateFill(ItemStack stack, MLFluidStack fluid) {
        IFluidHandlerItem iFluidHandlerItem = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
        if (iFluidHandlerItem != null) {
            int filled = iFluidHandlerItem.fill(convertToForge(fluid),action(FluidInventory.Action.SIMULATE));
            return filled;
        }
        return 0;
    }

    @Override
    public void transferContainerToTank(ItemStack container, FluidInventory fluidInventory, int maxFill, int tank, Player player) {
        FluidActionResult fluidActionResult = FluidUtil.tryEmptyContainerAndStow(container, (ForgeFluidInventory) fluidInventory, new InvWrapper(player.getInventory()), maxFill, player, true);
        if (fluidActionResult.isSuccess()) {
            player.containerMenu.setCarried(fluidActionResult.getResult());
        }
    }

    @Override
    public void transferTankToContainer(ItemStack container, FluidInventory fluidInventory, int maxDrain, int tank, Player player) {
        FluidActionResult fluidActionResult = FluidUtil.tryFillContainerAndStow(container, (ForgeFluidInventory) fluidInventory, new InvWrapper(player.getInventory()), maxDrain, player, true);
        if (fluidActionResult.isSuccess()) {
            player.containerMenu.setCarried(fluidActionResult.getResult());
        }
    }
}