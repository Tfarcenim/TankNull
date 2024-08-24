package tfar.tanknull.datagen;


import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import tfar.tanknull.datagen.tags.ModBlockTagsProvider;
import tfar.tanknull.datagen.tags.ModItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ModDatagen {

    public static void start(GatherDataEvent e) {
        DataGenerator generator = e.getGenerator();
        ExistingFileHelper helper = e.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = e.getLookupProvider();
        PackOutput packOutput = generator.getPackOutput();
        if (e.includeServer()) {
            BlockTagsProvider blockTagsProvider = new ModBlockTagsProvider(packOutput,lookupProvider,helper);
            generator.addProvider(true,blockTagsProvider);
            generator.addProvider(true,new ModItemTagsProvider(packOutput,lookupProvider,blockTagsProvider.contentsGetter(),helper));
            generator.addProvider(true,new ModRecipeProvider(packOutput));
        }
        if (e.includeClient()) {
        }
    }
}
