package tfar.tanknull.platform.services;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import tfar.tanknull.DockBlockEntity;
import tfar.tanknull.MLFluidStack;
import tfar.tanknull.TankStats;
import tfar.tanknull.inventory.FluidInventory;
import tfar.tanknull.network.client.S2CModPacket;
import tfar.tanknull.network.server.C2SModPacket;
import tfar.tanknull.world.TankSavedData;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {

        return isDevelopmentEnvironment() ? "development" : "production";
    }

    Component getDisplayName(MLFluidStack fluidStack);
    String getTranslationKey(MLFluidStack fluidStack);

    FluidInventory create(TankStats stats, TankSavedData data);
    DockBlockEntity create(BlockPos pos, BlockState state);

    default  <F> void registerAll(Class<?> clazz, Registry<F> registry, Class<? extends F> filter) {
        Map<String,F> map = new HashMap<>();
        unfreeze(registry);
        for (Field field : clazz.getFields()) {
            try {
                Object o = field.get(null);
                if (filter.isInstance(o)) {
                    map.put(field.getName().toLowerCase(Locale.ROOT),(F)o);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
        registerAll(map,registry,filter);
    }

    default <F> void unfreeze(Registry<F> registry) {

    }

    <F> void registerAll(Map<String,? extends F> map, Registry<F> registry, Class<? extends F> filter);

    <MSG extends S2CModPacket> void registerClientPacket(Class<MSG> packetLocation, Function<FriendlyByteBuf,MSG> reader);

    <MSG extends C2SModPacket> void registerServerPacket(Class<MSG> packetLocation, Function<FriendlyByteBuf,MSG> reader);


    void sendToClient(S2CModPacket msg, ServerPlayer player);
    void sendToServer(C2SModPacket msg);

    void renderFluidInSlot(GuiGraphics matrices, int x, int y, MLFluidStack fluidStack);
    MLFluidStack extractFluid(ItemStack stack,MLFluidStack target);
    MLFluidStack extractAnyFluid(ItemStack stack, int max, FluidInventory.Action action);
    MLFluidStack getStoredFluid(ItemStack stack);
    int simulateFill(ItemStack stack, MLFluidStack fluid);

    void transferContainerToTank(ItemStack container, FluidInventory fluidInventory, int fluid, int tank, Player player);
    void transferTankToContainer(ItemStack container, FluidInventory fluidInventory, int fluid, int tank, Player player);
}