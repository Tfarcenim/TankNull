package tfar.tanknull.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import tfar.tanknull.inventory.FluidInventory;
import tfar.tanknull.menu.AbstractTankMenu;
import tfar.tanknull.platform.Services;

public class C2SLockSlotPacket implements C2SModPacket {

    private final int slot;

    public C2SLockSlotPacket(int slot) {
        this.slot = slot;
    }

    public C2SLockSlotPacket(FriendlyByteBuf buf) {
        slot = buf.readInt();
    }

    public static void send(int slot) {
        Services.PLATFORM.sendToServer(new C2SLockSlotPacket(slot));
    }

    public void handleServer(ServerPlayer player) {
        AbstractContainerMenu container = player.containerMenu;
        if (container instanceof AbstractTankMenu dankContainer) {
            FluidInventory inventory = dankContainer.fluidInventory;
          //  inventory.toggleGhostItem(slot);
        }
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(slot);
    }
}

