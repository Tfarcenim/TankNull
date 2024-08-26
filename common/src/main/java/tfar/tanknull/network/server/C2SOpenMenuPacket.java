package tfar.tanknull.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import tfar.tanknull.TankItem;
import tfar.tanknull.platform.Services;

public class C2SOpenMenuPacket implements C2SModPacket {

    private final InteractionHand hand;

    public C2SOpenMenuPacket(InteractionHand hand) {
        this.hand = hand;
    }

    public C2SOpenMenuPacket(FriendlyByteBuf buf) {
        hand = buf.readEnum(InteractionHand.class);
    }

    public static void send(InteractionHand hand) {
        Services.PLATFORM.sendToServer(new C2SOpenMenuPacket(hand));
    }

    public void handleServer(ServerPlayer player) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof TankItem tankItem) {
            player.openMenu(tankItem.createProvider(stack));
        }
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeEnum(hand);
    }

}
