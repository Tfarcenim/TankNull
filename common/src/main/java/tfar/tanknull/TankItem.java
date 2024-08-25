package tfar.tanknull;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import tfar.tanknull.init.ModDataComponentTypes;
import tfar.tanknull.inventory.FluidInventory;
import tfar.tanknull.inventory.FluidListTooltip;
import tfar.tanknull.menu.TankMenu;
import tfar.tanknull.network.server.C2SRequestContentsPacket;
import tfar.tanknull.world.ClientData;
import tfar.tanknull.world.TankSavedData;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class TankItem extends Item {

    public final TankStats stats;

    public TankItem(Properties $$0, TankStats stats) {
        super($$0);
        this.stats = stats;
    }


    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack bag = player.getItemInHand(hand);

        if (getUseType(bag) == UseMode.bag) {
            if (!level.isClientSide) {
                player.openMenu(createProvider(bag));
            }
            return InteractionResultHolder.success(bag);
        } else {
            if (!level.isClientSide) {
            }
            return new InteractionResultHolder<>(InteractionResult.PASS, player.getItemInHand(hand));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level $$1, List<Component> tooltip, TooltipFlag $$3) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {

            if (tag.contains(ModDataComponentTypes.FREQUENCY)) {
                tooltip.add(TextComponents.FREQUENCY.copy().append(Component.literal(" "+tag.getInt(ModDataComponentTypes.FREQUENCY))
                        .withStyle(ChatFormatting.AQUA)));
            } else {
                tooltip.add(Component.translatable("Frequency: Unbound"));
            }
        }

       // tooltip.add(CommonUtils.translatable("text.dankstorage.changeusetype", DankKeybinds.CONSTRUCTION.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GRAY));
        UseMode useMode = getUseType(stack);
        tooltip.add(Component.translatable("tooltip.tanknull.tank.current_use_mode", Component.translatable(
                       "tooltip.tanknull.tank.use_mode." + useMode.name().toLowerCase(Locale.ROOT))
                        .withStyle(ChatFormatting.YELLOW))
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.tanknull.tankitem.stacklimit", Component.literal(stats.stacklimit + "").withStyle(ChatFormatting.GREEN)).withStyle(ChatFormatting.GRAY));

    }

    static long lastRequest;

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack itemStack) {

        int id = getFrequency(itemStack);

        if (id > TankSavedData.INVALID) {
            if (Util.getMillis() - lastRequest > 50) {
                //don't spam the server with requests
                C2SRequestContentsPacket.send(id);
                lastRequest = Util.getMillis();
            }
            return Optional.of(new FluidListTooltip(ClientData.cached, getSelectedSlot(itemStack)));
        }
        return Optional.empty();
    }

    public MenuProvider createProvider(ItemStack stack) {
        return new PortableTankProvider(stack);
    }

    public class PortableTankProvider implements MenuProvider{

        private final ItemStack stack;

        public PortableTankProvider(ItemStack stack) {

            this.stack = stack;
        }

        @Override
        public Component getDisplayName() {
            return stack.getHoverName();
        }

        @Nullable
        @Override
        public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {

            MinecraftServer server = player.getServer();
            if (getFrequency(stack) == TankSavedData.INVALID) {
                assignNextFreeId(server,stack);
                TankSavedData tankSavedData = TankSavedData.getOrCreate(getFrequency(stack),server);
                tankSavedData.setStats(stats);
            }

            TankSavedData tankSavedData = TankSavedData.getOrCreate(getFrequency(stack),server);

            FluidInventory fluidInventory = tankSavedData.getOrCreateInventory();

            if (stats != tankSavedData.getStats()) {
                if (stack.getTag().contains(ModDataComponentTypes.UPGRADE) && stats.ordinal() > tankSavedData.getStats().ordinal()) {
                    tankSavedData.setStats(stats);
                    stack.getTag().remove(ModDataComponentTypes.UPGRADE);
                } else {
                    player.displayClientMessage(Component.literal("Backing inventory is tier "+tankSavedData.getStats().ordinal()
                            +" while item is tier "+stats.ordinal()),false);
                    return null;
                }
            }

            switch (stats) {
                case zero -> {
                }
                case one -> {
                    return TankMenu.t1s(i,inventory,fluidInventory,stack);
                }
                case two -> {
                    return TankMenu.t2s(i,inventory,fluidInventory,stack);
                }
                case three -> {
                    return TankMenu.t3s(i,inventory,fluidInventory,stack);
                }
                case four -> {
                    return TankMenu.t4s(i,inventory,fluidInventory,stack);
                }
                case five -> {
                    return TankMenu.t5s(i,inventory,fluidInventory,stack);
                }
                case six -> {
                    return TankMenu.t6s(i,inventory,fluidInventory,stack);
                }
                case seven -> {
                    return TankMenu.t7s(i,inventory,fluidInventory,stack);
                }
            }
            return null;
        }
    }

    ////////////////////////////////////////////////////////////////

    public static int getFrequency(ItemStack bag) {
        return bag.hasTag() && bag.getTag().contains(ModDataComponentTypes.FREQUENCY) ? bag.getTag().getInt(ModDataComponentTypes.FREQUENCY) : TankSavedData.INVALID;
    }

    public static void setFrequency(ItemStack bag,int frequency) {
        bag.getOrCreateTag().putInt(ModDataComponentTypes.FREQUENCY,frequency);
    }

    public static int getSelectedSlot(ItemStack bag) {
        return bag.hasTag() && bag.getTag().contains(ModDataComponentTypes.SELECTED) ? bag.getTag().getInt(ModDataComponentTypes.SELECTED) : TankSavedData.INVALID;
    }


    public static void setSelectedSlot(ItemStack bag,int frequency) {
        bag.getOrCreateTag().putInt(ModDataComponentTypes.SELECTED,frequency);
    }

    public static FluidInventory getInventoryFrom(ItemStack bag,MinecraftServer server) {
        int frequency = getFrequency(bag);
        if (frequency < 0) return null;
        return TankSavedData.get(frequency,server).getOrCreateInventory();
    }


    public static UseMode getUseType(ItemStack bag) {
        return bag.hasTag() && bag.getTag().contains(ModDataComponentTypes.USE_MODE) ? UseMode.valueOf(bag.getTag().getString(ModDataComponentTypes.USE_MODE)) : UseMode.bag;
    }

    public static void setUseMode(ItemStack bag, UseMode useMode) {
        bag.getOrCreateTag().putString(ModDataComponentTypes.USE_MODE, useMode.name());
    }


    void assignNextFreeId(MinecraftServer server,ItemStack stack) {
        int id = 0;
        while (true) {
            TankSavedData tankSavedData = TankSavedData.get(id,server);
            if (tankSavedData == null) {
                stack.getOrCreateTag().putInt(ModDataComponentTypes.FREQUENCY,id);
                return;
            }
            id++;
        }
    }


    public static void toggleUseMode(ServerPlayer player) {
        ItemStack stack = null;
        if (player.getMainHandItem().getItem() instanceof TankItem) {
            stack = player.getMainHandItem();
        }

        if (stack != null) {
            UseMode useMode = getUseType(stack);
            setUseMode(stack,Utils.cycle(useMode));
        }
    }
}
