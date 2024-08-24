package tfar.tanknull;

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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import tfar.tanknull.inventory.FluidInventory;
import tfar.tanknull.menu.AbstractTankMenu;
import tfar.tanknull.world.TankSavedData;

import javax.annotation.Nonnull;

public class TankItem extends Item {

    public static final String FREQUENCY = "tanknull:frequency";

    public final TankStats stats;

    public TankItem(Properties $$0, TankStats stats) {
        super($$0);
        this.stats = stats;
    }


    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack bag = player.getItemInHand(hand);

        if (getUseType(bag) == UseType.bag) {
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
            return stack.getDisplayName();
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

            switch (stats) {
                case zero -> {
                }
                case one -> {
                    return AbstractTankMenu.t1s(i,inventory,fluidInventory);
                }
                case two -> {
                    return AbstractTankMenu.t2s(i,inventory,fluidInventory);
                }
                case three -> {
                    return AbstractTankMenu.t3s(i,inventory,fluidInventory);
                }
                case four -> {
                    return AbstractTankMenu.t4s(i,inventory,fluidInventory);
                }
                case five -> {
                    return AbstractTankMenu.t5s(i,inventory,fluidInventory);
                }
                case six -> {
                    return AbstractTankMenu.t6s(i,inventory,fluidInventory);
                }
                case seven -> {
                    return AbstractTankMenu.t7s(i,inventory,fluidInventory);
                }
            }
            return null;
        }
    }

    ////////////////////////////////////////////////////////////////

    public static int getFrequency(ItemStack bag) {
        return bag.hasTag() && bag.getTag().contains(FREQUENCY) ? bag.getTag().getInt(FREQUENCY) : TankSavedData.INVALID;
    }

    public static void setFrequency(ItemStack bag,int frequency) {
        bag.getOrCreateTag().putInt(FREQUENCY,frequency);
    }


    public static UseType getUseType(ItemStack bag) {
        return bag.hasTag() && bag.getTag().contains("use_type") ? UseType.valueOf(bag.getTag().getString("use_type")) : UseType.bag;
    }

    public static void setUseType(ItemStack bag,UseType useType) {
        bag.getOrCreateTag().putString("use_type",useType.name());
    }


    void assignNextFreeId(MinecraftServer server,ItemStack stack) {
        int id = 0;
        while (true) {
            TankSavedData tankSavedData = TankSavedData.get(id,server);
            if (tankSavedData == null) {
                stack.getOrCreateTag().putInt(FREQUENCY,id);
                return;
            }
            id++;
        }
    }


    public static void toggleUseType(ServerPlayer player) {
        ItemStack stack = null;
        if (player.getMainHandItem().getItem() instanceof TankItem) {
            stack = player.getMainHandItem();
        }

        if (stack != null) {
            UseType useType = getUseType(stack);
            setUseType(stack,Utils.cycle(useType));
        }
    }
}
