package tfar.tanknull.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import tfar.tanknull.TankItem;
import tfar.tanknull.platform.Services;

public class C2SScrollSlotPacket implements C2SModPacket {

    private final boolean right;

    public C2SScrollSlotPacket(boolean right) {
        this.right = right;
    }

    public C2SScrollSlotPacket(FriendlyByteBuf buf) {
        right = buf.readBoolean();
    }

    public static void send(boolean right) {
        Services.PLATFORM.sendToServer(new C2SScrollSlotPacket(right));
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(right);
    }

    public void handleServer(ServerPlayer player) {
    //    if (player.getMainHandItem().getItem() instanceof TankItem)
    //        CommonUtils.changeSelectedSlot(player.getMainHandItem(), right,player);
    //    else if (player.getOffhandItem().getItem() instanceof TankItem)
    //        CommonUtils.changeSelectedSlot(player.getOffhandItem(), right,player);
    }
}

