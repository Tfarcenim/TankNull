package tfar.tanknull.world;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import tfar.dankstorage.inventory.DankInterface;
import tfar.dankstorage.platform.Services;
import tfar.dankstorage.utils.DankStats;

public class TankSavedData extends SavedData {

    protected final ServerLevel level;
    CompoundTag storage = defaultTag();
    DankInterface cache;

    public TankSavedData(ServerLevel level) {
        this.level = level;
    }
    

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        compoundTag.put("contents", storage);
        return compoundTag;
    }

    public DankInterface createInventory(HolderLookup.Provider provider,int frequency) {
        if (cache == null) {
            cache = Services.PLATFORM.createInventory(DankStats.zero,frequency);
            cache.read(provider, storage);
            cache.setServer(level.getServer());
        }
        return cache;
    }

    public DankInterface createFreshInventory(DankStats defaults, int frequency) {
        if (cache == null) {
            cache = Services.PLATFORM.createInventory(defaults, frequency);
            cache.setServer(level.getServer());
        }
        return cache;
    }

    public void setStats(DankStats stats, int frequency) {
        DankInterface dankInventory = createInventory(level.registryAccess(),frequency);
        dankInventory.setDankStats(stats);
        write(dankInventory.save(level.registryAccess()));
    }


    public void write(CompoundTag tag) {
        storage = tag;
        setDirty();
    }

    protected void load(CompoundTag compoundTag) {
        storage = compoundTag.getCompound("contents");
    }

    public static TankSavedData loadStatic(CompoundTag compoundTag, ServerLevel level) {
        TankSavedData tankSavedData = new TankSavedData(level);
        tankSavedData.load(compoundTag);
        return tankSavedData;
    }

    public static CompoundTag defaultTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("DankStats",DankStats.zero.name());
        return tag;
    }

    public boolean clear() {
        storage = defaultTag();
        return true;
    }
}
