package tfar.tanknull.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import tfar.tanknull.TankNull;
import tfar.tanknull.TankStats;
import tfar.tanknull.inventory.FluidInventory;
import tfar.tanknull.platform.Services;

public class TankSavedData extends SavedData {

    protected final ServerLevel level;
    FluidInventory cache;
    final int frequency;
    TankStats stats = TankStats.zero;
    CompoundTag tag = new CompoundTag();

    public static final int INVALID = -1;

    public TankSavedData(ServerLevel level, int frequency) {
        this.level = level;
        this.frequency = frequency;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.putString("Stats",stats.name());
        compoundTag.put("contents", cache.save(level.registryAccess()));
        return compoundTag;
    }

    public FluidInventory getOrCreateInventory() {
        if (cache == null) {
            cache = Services.PLATFORM.create(stats,this);
            cache.load(level.registryAccess(), tag);
        }
        if (cache.fluids.size() != stats.slots) {
            cache.updateStats(stats);
        }
        return cache;
    }

    public void setStats(TankStats stats) {
        this.stats = stats;
        setDirty();
    }

    protected void load(CompoundTag compoundTag) {
        stats = compoundTag.contains("Stats") ? TankStats.valueOf(compoundTag.getString("Stats")) : TankStats.zero;
        tag = compoundTag.getCompound("contents");
    }

    public static TankSavedData loadStatic(CompoundTag compoundTag, ServerLevel level,int frequency) {
        TankSavedData tankSavedData = new TankSavedData(level,frequency);
        tankSavedData.load(compoundTag);
        return tankSavedData;
    }


    public static TankSavedData getOrCreate(int id, MinecraftServer server) {
        TankSavedData tankSavedData = get(id,server);
        if (tankSavedData != null) {
            return tankSavedData;
        }

        ServerLevel overworld = server.overworld();
        return overworld.getDataStorage()
                .computeIfAbsent(compoundTag -> loadStatic(compoundTag,overworld,id), () -> new TankSavedData(overworld,id),
                        TankNull.MOD_ID+"/"+id);
    }

    public static TankSavedData get(int id, MinecraftServer server) {
        if (id <= INVALID) throw new RuntimeException("Invalid frequency: "+id);
        ServerLevel overworld = server.overworld();
        return overworld.getDataStorage()
                .get(compoundTag -> loadStatic(compoundTag,overworld,id), TankNull.MOD_ID+"/"+id);
    }

    public boolean clear() {
        tag = new CompoundTag();
        return true;
    }
}
