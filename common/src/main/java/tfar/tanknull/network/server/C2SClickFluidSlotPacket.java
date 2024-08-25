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
import tfar.tanknull.menu.TankMenu;
import tfar.tanknull.network.PacketHandler;
import tfar.tanknull.platform.Services;

import java.util.function.IntFunction;

public class C2SClickFluidSlotPacket implements C2SModPacket {


    private static final int MAX_SLOT_COUNT = 128;
    private final int containerId;
    private final int stateId;
    private final int slotNum;
    private final int buttonNum;
    private final ClickAction clickType;
    private final Int2ObjectMap<MLFluidStack> changedSlots;

    public C2SClickFluidSlotPacket(int containerID, int $$1, int slotNum, int $$3, ClickAction $$4, Int2ObjectMap<MLFluidStack> $$6) {
        this.containerId = containerID;
        this.stateId = $$1;
        this.slotNum = slotNum;
        this.buttonNum = $$3;
        this.clickType = $$4;
        this.changedSlots = Int2ObjectMaps.unmodifiable($$6);
    }

    public C2SClickFluidSlotPacket(FriendlyByteBuf buf) {
        this.containerId = buf.readByte();
        this.stateId = buf.readVarInt();
        this.slotNum = buf.readShort();
        this.buttonNum = buf.readByte();
        this.clickType = buf.readEnum(ClickAction.class);
        IntFunction<Int2ObjectOpenHashMap<MLFluidStack>> $$1 = FriendlyByteBuf.limitValue(Int2ObjectOpenHashMap::new, MAX_SLOT_COUNT);
        this.changedSlots = Int2ObjectMaps.unmodifiable(buf.readMap($$1, $$0x -> Integer.valueOf($$0x.readShort()), PacketHandler.FLUID_READER));
    }

    public void write(FriendlyByteBuf $$0) {
        $$0.writeByte(this.containerId);
        $$0.writeVarInt(this.stateId);
        $$0.writeShort(this.slotNum);
        $$0.writeByte(this.buttonNum);
        $$0.writeEnum(this.clickType);
        $$0.writeMap(this.changedSlots, FriendlyByteBuf::writeShort, PacketHandler.FLUID_WRITER);
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
            if (menu instanceof TankMenu tankMenu) {
                FluidInventory fluidInventory = tankMenu.fluidInventory;
                ItemStack carried = tankMenu.getCarried();
                MLFluidStack fluidInSlot = fluidInventory.fluids.get(slotNum);

                switch (clickType) {
                    case PICKUP_ALL -> {
                        int filled = Services.PLATFORM.simulateFill(carried,fluidInSlot);
                        if (filled > 0) {
                            Services.PLATFORM.transferTankToContainer(carried,fluidInventory,filled,slotNum, player, false);
                        }
                    }
                    case DEPOSIT_ONE -> {
                        MLFluidStack fluidStack = Services.PLATFORM.extractAnyFluid(carried, 1000, FluidInventory.Action.SIMULATE);
                        if (!fluidStack.isEmpty()) {
                            int fill = fluidInventory.fillSpecific(fluidStack, FluidInventory.Action.SIMULATE, slotNum);
                            if (fill == fluidStack.getAmount()) {//everything was filled, safe to transfer
                                Services.PLATFORM.transferContainerToTank(carried,fluidInventory,fill,slotNum,player, false);
                            }
                        }
                    }
                    case DEPOSIT_ALL -> {
                        MLFluidStack fluidStack = Services.PLATFORM.extractAnyFluid(carried, Integer.MAX_VALUE, FluidInventory.Action.SIMULATE);
                        if (!fluidStack.isEmpty()) {
                            int fill = fluidInventory.fillSpecific(fluidStack, FluidInventory.Action.SIMULATE, slotNum);
                            if (fill == fluidStack.getAmount()) {//everything was filled, safe to transfer
                                Services.PLATFORM.transferContainerToTank(carried,fluidInventory,fill,slotNum,player, false);
                            }
                        }
                    }
                    case PICKUP_HALF -> {
                        int drain = fluidInSlot.getAmount() / 2;
                        int buckets = (int) Math.ceil(drain/1000d);
                        MLFluidStack drained = fluidInSlot.copyWithAmount(buckets * 1000);
                        int filled = Services.PLATFORM.simulateFill(carried,drained);
                        if (filled > 0) {
                            Services.PLATFORM.transferTankToContainer(carried,fluidInventory,filled,slotNum, player, false);
                        }
                    }
                    case LOCK_SLOT -> {
                        if (!fluidInSlot.isEmpty()) {
                            if (fluidInventory.ghostFluids.get(slotNum).isEmpty()) {
                                fluidInventory.ghostFluids.set(slotNum, fluidInSlot.copyWithAmount(1000));
                            } else {
                                fluidInventory.ghostFluids.set(slotNum,MLFluidStack.EMPTY);
                            }
                        } else {
                            fluidInventory.ghostFluids.set(slotNum, MLFluidStack.EMPTY);
                        }
                        fluidInventory.setDirty();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
