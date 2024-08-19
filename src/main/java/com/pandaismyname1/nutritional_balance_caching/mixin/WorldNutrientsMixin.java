package com.pandaismyname1.nutritional_balance_caching.mixin;

import com.dannyandson.nutritionalbalance.nutrients.Nutrient;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import com.pandaismyname1.nutritional_balance_caching.NutritionalBalance_Caching;
import com.pandaismyname1.nutritional_balance_caching.db.NutrientCache;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Mixin(WorldNutrients.class)
public class WorldNutrientsMixin {

    @Shadow
    @Final
    private static Map<Item,List<Nutrient>> nutrientMap;

    @Inject(method = "register", at = @At("RETURN"), remap = false)
    private static void registerNutrients(CallbackInfo ci) {
        NutritionalBalance_Caching.LOGGER.info("WorldNutrients: Registering Nutrients From Cache");
        var tempMap = NutrientCache.getAllNutrients();
        var itemRegistry = ForgeRegistries.ITEMS;
        for (var item : itemRegistry) {
            var itemName = item.toString();
            if (tempMap.containsKey(itemName)) {
                nutrientMap.put(item, tempMap.get(itemName));
            }
        }
        NutritionalBalance_Caching.LOGGER.info("WorldNutrients: Registered Nutrients From Cache");
    }

    @Inject(method = "getNutrients(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;I)Ljava/util/List;",
            at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;<init>()V", shift = At.Shift.BEFORE, ordinal = 0), cancellable = true, remap = false)
    private static void getNutrientsMixin(ItemStack item, Level level, int i, CallbackInfoReturnable<List<Nutrient>> cir) {
        var nutrients = NutrientCache.getNutrients(item.getItem().toString());
        if (nutrients != null && !nutrients.isEmpty()) {
            if (nutrients.size() == 1 && nutrients.get(0).name.equals("NONE"))
            {
                nutrientMap.put(item.getItem(), new ArrayList<>());
                cir.setReturnValue(new ArrayList<>());
                cir.cancel();
            }
            nutrientMap.put(item.getItem(), nutrients);
            cir.setReturnValue(nutrients);
            cir.cancel();
        }
    }


    @Inject(method = "getNutrients(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;I)Ljava/util/List;",
            at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;<init>()V", shift = At.Shift.AFTER, ordinal = 0), remap = false)
    private static void printItemName(ItemStack item, Level level, int i, CallbackInfoReturnable<List<Nutrient>> cir) {
        NutritionalBalance_Caching.LOGGER.info("Uncached Item: " + item.getItem().toString());
    }

    @Inject(method = "getNutrients(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;I)Ljava/util/List;",
            at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"), remap = false, cancellable = true)
    private static void handleSaveNutrients(ItemStack item, Level level, int i, CallbackInfoReturnable<List<Nutrient>> cir) {
        var result = (nutrientMap.containsKey(item.getItem()))?nutrientMap.get(item.getItem()): new ArrayList<Nutrient>();
        NutrientCache.setNutrients(item.getItem().toString(), result);
        cir.setReturnValue(result);
        cir.cancel();
    }
}
