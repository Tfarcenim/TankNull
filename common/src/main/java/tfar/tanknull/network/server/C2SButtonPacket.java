package tfar.tanknull.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import tfar.tanknull.TankItem;
import tfar.tanknull.platform.Services;

public class C2SButtonPacket implements C2SModPacket {

    private final KeybindAction keybindAction;

    public C2SButtonPacket(KeybindAction keybindAction) {
        this.keybindAction = keybindAction;
    }

    public C2SButtonPacket(FriendlyByteBuf buf) {
        keybindAction = KeybindAction.values()[buf.readInt()];
    }

    public static void send(KeybindAction keybindAction) {
        Services.PLATFORM.sendToServer(new C2SButtonPacket(keybindAction));
    }

    public void handleServer(ServerPlayer player) {
        switch (keybindAction) {
            case TOGGLE_PICKUP -> {}//CommonUtils.togglePickupMode(player);
            case TOGGLE_USE_MODE -> TankItem.toggleUseMode(player);
            case PICK_BLOCK -> {
                HitResult hit = player.pick(5, 0, true);
                if (hit instanceof BlockHitResult blockHit && hit.getType() != HitResult.Type.MISS) {

                }
            }
        }
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(keybindAction.ordinal());
    }

}
