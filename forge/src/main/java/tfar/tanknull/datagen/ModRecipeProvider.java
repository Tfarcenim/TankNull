package tfar.tanknull.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import tfar.tanknull.init.ModItems;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.TANK_1)
                .define('c', ItemTags.COALS)
                .define('b', Tags.Items.BARRELS_WOODEN)
                .pattern("ccc")
                .pattern("cbc")
                .pattern("ccc")
                .unlockedBy(getHasName(Blocks.BARREL),has(Tags.Items.BARRELS_WOODEN))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.DOCK)
                .define('c', Blocks.BLACK_CONCRETE)
                .pattern("ccc")
                .pattern("c c")
                .pattern("ccc")
                .unlockedBy(getHasName(Blocks.BLACK_CONCRETE),has(Blocks.BLACK_CONCRETE))
                .save(pWriter);

        createDankAndUpgrade(ModItems.TANK_2, ModItems.TANK_1,
                Ingredient.of(Blocks.REDSTONE_BLOCK),Ingredient.of(Blocks.REDSTONE_BLOCK),pWriter);

        createDankAndUpgrade(ModItems.TANK_3, ModItems.TANK_2,
                Ingredient.of(Blocks.GOLD_BLOCK),Ingredient.of(Blocks.GOLD_BLOCK),pWriter);

        createDankAndUpgrade(ModItems.TANK_4, ModItems.TANK_3,
                Ingredient.of(Blocks.EMERALD_BLOCK),Ingredient.of(Blocks.EMERALD_BLOCK),pWriter);

        createDankAndUpgrade(ModItems.TANK_5, ModItems.TANK_4,
                Ingredient.of(Blocks.DIAMOND_BLOCK),Ingredient.of(Blocks.DIAMOND_BLOCK),pWriter);

        createDankAndUpgrade(ModItems.TANK_6, ModItems.TANK_5,
                Ingredient.of(Blocks.CRYING_OBSIDIAN),Ingredient.of(Blocks.AMETHYST_BLOCK),pWriter);

        createDankAndUpgrade(ModItems.TANK_7, ModItems.TANK_6,
                Ingredient.of(Items.NETHER_STAR),Ingredient.of(Items.NETHER_STAR),pWriter);
    }

    protected void createDankAndUpgrade(Item dank, Item previousDank, Ingredient around,Ingredient around2,Consumer<FinishedRecipe> pWriter) {
        ShapedRecipeBuilderCustom.shaped(RecipeCategory.TOOLS, dank)
                .define('c', around)
                .define('d', around2)
                .define('b', previousDank)
                .pattern("dcd")
                .pattern("cbc")
                .pattern("dcd")
                //.serializer(ModRecipeSerializers.upgrade)
                .unlockedBy(getHasName( previousDank),has(previousDank))
                .save(pWriter);
    }

}
