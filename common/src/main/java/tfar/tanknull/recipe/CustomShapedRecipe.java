package tfar.tanknull.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class CustomShapedRecipe extends ShapedRecipe {
    private final RecipeSerializer<?> serializer;

    public CustomShapedRecipe(ResourceLocation $$0, String $$1, CraftingBookCategory $$2, int $$3, int $$4, NonNullList<Ingredient> $$5, ItemStack $$6, boolean $$7, RecipeSerializer<?> serializer) {
        super($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
        this.serializer = serializer;
    }

    public CustomShapedRecipe(ShapedRecipe recipe,RecipeSerializer<?> serializer) {
        this(recipe.getId(),recipe.getGroup(),recipe.category(),recipe.getWidth(),recipe.getHeight(),recipe.getIngredients(),
                recipe.getResultItem(null),recipe.showNotification(),serializer);
    }

    @Override
    public final RecipeSerializer<?> getSerializer() {
        return serializer;
    }

    public static class CustomSerializer extends ShapedRecipe.Serializer {

    }
}
