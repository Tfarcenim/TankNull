package tfar.tanknull.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import tfar.tanknull.init.ModRecipeSerializers;

public class UpgradeRecipe extends CustomShapedRecipe {
    public UpgradeRecipe(ResourceLocation $$0, String $$1, CraftingBookCategory $$2, int $$3, int $$4, NonNullList<Ingredient> $$5, ItemStack $$6, boolean $$7) {
        super($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, ModRecipeSerializers.UPGRADE);
    }

    public UpgradeRecipe(ShapedRecipe recipe) {
        super(recipe, ModRecipeSerializers.UPGRADE);
    }
    public static class UpgradeSerializer extends CustomSerializer {

        @Override
        public UpgradeRecipe fromJson(ResourceLocation $$0, JsonObject $$1) {
            return new UpgradeRecipe(super.fromJson($$0, $$1));
        }

        @Override
        public UpgradeRecipe fromNetwork(ResourceLocation $$0, FriendlyByteBuf $$1) {
            return new UpgradeRecipe(super.fromNetwork($$0, $$1));
        }
    }

}
