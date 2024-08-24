package tfar.tanknull.network.server;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import tfar.tanknull.MLFluidStack;
import tfar.tanknull.inventory.ClickAction;
import tfar.tanknull.inventory.FluidInventory;
import tfar.tanknull.menu.AbstractTankMenu;
import tfar.tanknull.platform.Services;

import java.util.function.IntFunction;

public class C2SClickFluidSlotPacket implements C2SModPacket {


    private static final int MAX_SLOT_COUNT = 128;
    private final int containerId;
    private final int stateId;
    private final int slotNum;
    private final int buttonNum;
    private final ClickAction clickType;
    private final ItemStack carriedItem;
    private final Int2ObjectMap<MLFluidStack> changedSlots;

    public C2SClickFluidSlotPacket(int containerID, int $$1, int slotNum, int $$3, ClickAction $$4, ItemStack $$5, Int2ObjectMap<MLFluidStack> $$6) {
        this.containerId = containerID;
        this.stateId = $$1;
        this.slotNum = slotNum;
        this.buttonNum = $$3;
        this.clickType = $$4;
        this.carriedItem = $$5;
        this.changedSlots = Int2ObjectMaps.unmodifiable($$6);
    }

    public C2SClickFluidSlotPacket(FriendlyByteBuf buf) {
        this.containerId = buf.readByte();
        this.stateId = buf.readVarInt();
        this.slotNum = buf.readShort();
        this.buttonNum = buf.readByte();
        this.clickType = buf.readEnum(ClickAction.class);
        IntFunction<Int2ObjectOpenHashMap<MLFluidStack>> $$1 = FriendlyByteBuf.limitValue(Int2ObjectOpenHashMap::new, 128);
        this.changedSlots = Int2ObjectMaps.unmodifiable(buf.readMap($$1, $$0x -> Integer.valueOf($$0x.readShort()), MLFluidStack::readFromPacket));
        this.carriedItem = buf.readItem();
    }

    public void write(FriendlyByteBuf $$0) {
        $$0.writeByte(this.containerId);
        $$0.writeVarInt(this.stateId);
        $$0.writeShort(this.slotNum);
        $$0.writeByte(this.buttonNum);
        $$0.writeEnum(this.clickType);
        $$0.writeMap(this.changedSlots, FriendlyByteBuf::writeShort, (buf, stack) -> stack.writeToPacket(buf));
        $$0.writeItem(this.carriedItem);
    }

    public int getContainerId() {
        return this.containerId;
    }

    public int getSlotNum() {
        return this.slotNum;
    }

    public int getButtonNum() {
        return this.buttonNum;
    }

    public ItemStack getCarriedItem() {
        return this.carriedItem;
    }

    public Int2ObjectMap<MLFluidStack> getChangedSlots() {
        return this.changedSlots;
    }

    public ClickAction getClickType() {
        return this.clickType;
    }

    public int getStateId() {
        return this.stateId;
    }

    @Override
    public void handleServer(ServerPlayer player) {
        try {

            AbstractContainerMenu menu = player.containerMenu;
            if (menu instanceof AbstractTankMenu abstractTankMenu) {
                FluidInventory fluidInventory = abstractTankMenu.fluidInventory;
                switch (clickType) {
                    case PICKUP -> {

                    }
                    case DEPOSIT_ONE -> {
                        ItemStack carried = abstractTankMenu.getCarried();
                        MLFluidStack fluidStack = Services.PLATFORM.extractAnyFluid(carried, 1000, FluidInventory.Action.SIMULATE);
                        if (!fluidStack.isEmpty()) {
                            int fill = fluidInventory.fillSpecific(fluidStack, FluidInventory.Action.SIMULATE, slotNum);
                            if (fill == fluidStack.getAmount()) {//everything was filled, safe to transfer
                                MLFluidStack extract = Services.PLATFORM.extractAnyFluid(carried, 1000, FluidInventory.Action.EXECUTE);
                                fluidInventory.fillSpecific(extract, FluidInventory.Action.EXECUTE, slotNum);
                            }
                        }
                    }
                    case DEPOSIT_ALL -> {
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
