package tfar.tanknull.network;

import net.minecraft.resources.ResourceLocation;
import tfar.tanknull.TankNull;
import tfar.tanknull.network.client.*;
import tfar.tanknull.network.server.*;
import tfar.tanknull.platform.Services;

import java.util.Locale;

public class PacketHandler {

    public static void registerPackets() {

        Services.PLATFORM.registerServerPacket(C2SScrollSlotPacket.class, C2SScrollSlotPacket::new);
        Services.PLATFORM.registerServerPacket(C2SLockSlotPacket.class, C2SLockSlotPacket::new);
        Services.PLATFORM.registerServerPacket(C2SButtonPacket.class, C2SButtonPacket::new);
        Services.PLATFORM.registerServerPacket(C2SSetFrequencyPacket.class, C2SSetFrequencyPacket::new);
        Services.PLATFORM.registerServerPacket(C2SRequestContentsPacket.class, C2SRequestContentsPacket::new);

        ///////server to client

        Services.PLATFORM.registerClientPacket(S2CSendGhostSlotPacket.class, S2CSendGhostSlotPacket::new);
        Services.PLATFORM.registerClientPacket(S2CSyncSelectedFluidPacket.class, S2CSyncSelectedFluidPacket::new);
        Services.PLATFORM.registerClientPacket(S2CInitialSyncFluidInventoryPacket.class, S2CInitialSyncFluidInventoryPacket::new);
        Services.PLATFORM.registerClientPacket(S2CContentsForDisplayPacket.class, S2CContentsForDisplayPacket::new);
    }

    public static ResourceLocation packet(Class<?> clazz) {
        return TankNull.id(clazz.getName().toLowerCase(Locale.ROOT));
    }
}
