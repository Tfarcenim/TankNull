package tfar.tanknull.datagen;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CustomShapedRecipeBuilder extends ShapedRecipeBuilder {

    private RecipeSerializer<?> serializer;

    public CustomShapedRecipeBuilder(RecipeCategory pCategory, ItemLike pResult, int pCount) {
        super(pCategory, pResult, pCount);
    }

    public static CustomShapedRecipeBuilder shapedCustom(RecipeCategory pCategory, ItemLike pResult) {
        return shapedCustom(pCategory, pResult, 1);
    }

    public static CustomShapedRecipeBuilder shapedCustom(RecipeCategory pCategory, ItemLike pResult, int pCount) {
        return new CustomShapedRecipeBuilder(pCategory, pResult, pCount);
    }

    public CustomShapedRecipeBuilder serializer(RecipeSerializer<?> serializer) {
        this.serializer = serializer;
        return this;
    }

    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        this.ensureValid(pRecipeId);
        this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(pRecipeId)).rewards(AdvancementRewards.Builder.recipe(pRecipeId)).requirements(RequirementsStrategy.OR);
        pFinishedRecipeConsumer.accept(new CustomResult(pRecipeId, this.result, this.count, this.group == null ? "" : this.group, determineBookCategory(this.category), this.rows, this.key, this.advancement, pRecipeId.withPrefix("recipes/" + this.category.getFolderName() + "/"), this.showNotification, serializer));
    }

    public static class CustomResult extends Result {

        private final RecipeSerializer<?> serializer;

        public CustomResult(ResourceLocation pId, Item pResult, int pCount, String pGroup, CraftingBookCategory pCategory, List<String> pPattern, Map<Character, Ingredient> pKey, Advancement.Builder pAdvancement, ResourceLocation pAdvancementId, boolean pShowNotification, RecipeSerializer<?> serializer) {
            super(pId, pResult, pCount, pGroup, pCategory, pPattern, pKey, pAdvancement, pAdvancementId, pShowNotification);
            this.serializer = serializer;
        }

        public RecipeSerializer<?> getType() {
            return serializer;
        }
    }
}
