package tfar.tanknull;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public record TankSettings(int frequency, Component name) {

    public static TankSettings fromNBT(CompoundTag settings) {
        return null;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        return tag;
    }
}
