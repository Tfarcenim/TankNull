package tfar.tanknull.datagen;

import net.minecraft.client.KeyMapping;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import org.codehaus.plexus.util.StringUtils;
import tfar.tanknull.TankNull;
import tfar.tanknull.TextComponents;
import tfar.tanknull.UseMode;
import tfar.tanknull.client.ModKeybinds;
import tfar.tanknull.init.ModBlocks;
import tfar.tanknull.init.ModItems;
import tfar.tanknull.inventory.SortingType;

import java.util.function.Supplier;

public class ModLangProvider extends LanguageProvider {
    public ModLangProvider(PackOutput output) {
        super(output, TankNull.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addDefaultItem(() -> ModItems.TANK_1);
        addDefaultItem(() -> ModItems.TANK_2);
        addDefaultItem(() -> ModItems.TANK_3);
        addDefaultItem(() -> ModItems.TANK_4);
        addDefaultItem(() -> ModItems.TANK_5);
        addDefaultItem(() -> ModItems.TANK_6);
        addDefaultItem(() -> ModItems.TANK_7);

        addDefaultBlock(() -> ModBlocks.DOCK);

        addSortingTypes();
        addUseModes();
        addTranslatableComponent(TextComponents.OPEN_CONFIG,"Open Tank Config");
        addTranslatableComponent(TextComponents.SORT,"Sort");
        addTranslatableComponent(TextComponents.FREQUENCY,"Frequency:");
        addTranslatableComponent(TextComponents.BUCKET_SIZE,"Buckets:");
        add(ModKeybinds.CATEGORY,"TankNull");

        add("tanknull.auto_sort","Auto Sort:");

        add("tooltip.tanknull.tankitem.stacklimit","Fluid Limit: %smB");
        add("tooltip.tanknull.tank.current_use_mode","Current Use Mode: %s");

        addKeybind(ModKeybinds.CYCLE_USE_MODE,"Cycle Use Mode");
    }

    protected void addSortingTypes() {
        for (SortingType sortingType : SortingType.values()) {
            add("tanknull.sorting_type."+ sortingType,"Sort: "+StringUtils.capitalise(sortingType.name()));
        }
    }

    void addUseModes() {
        for (UseMode useMode : UseMode.values()) {
            add(useMode.translation(), useMode.name());
        }
    }

    protected void addKeybind(KeyMapping keyMapping,String value) {
        add(keyMapping.getName(),value);
    }

    protected void addDefaultItem(Supplier<? extends Item> supplier) {
        addItem(supplier,getNameFromItem(supplier.get()));
    }

    protected void addDefaultBlock(Supplier<? extends Block> supplier) {
        addBlock(supplier,getNameFromBlock(supplier.get()));
    }

    protected void addDefaultEnchantment(Supplier<? extends Enchantment> supplier) {
        addEnchantment(supplier,getNameFromEnchantment(supplier.get()));
    }

    protected void addDefaultEntityType(Supplier<EntityType<?>> supplier) {
        addEntityType(supplier,getNameFromEntity(supplier.get()));
    }

    public static String getNameFromItem(Item item) {
        return StringUtils.capitaliseAllWords(item.getDescriptionId().split("\\.")[2].replace("_", " "));
    }

    public static String getNameFromBlock(Block block) {
        return StringUtils.capitaliseAllWords(block.getDescriptionId().split("\\.")[2].replace("_", " "));
    }

    public static String getNameFromEnchantment(Enchantment enchantment) {
        return StringUtils.capitaliseAllWords(enchantment.getDescriptionId().split("\\.")[2].replace("_", " "));
    }

    public static String getNameFromEntity(EntityType<?> entity) {
        return StringUtils.capitaliseAllWords(entity.getDescriptionId().split("\\.")[2].replace("_", " "));
    }

    protected void addTranslatableComponent(MutableComponent component, String text) {
        ComponentContents contents = component.getContents();
        if (contents instanceof TranslatableContents translatableContents) {
            add(translatableContents.getKey(),text);
        } else {
            throw new UnsupportedOperationException(component +" is not translatable");
        }
    }

}
