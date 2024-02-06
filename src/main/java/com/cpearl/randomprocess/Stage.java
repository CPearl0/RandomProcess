package com.cpearl.randomprocess;

import dev.latvian.mods.kubejs.recipe.RecipesEventJS;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public class Stage {
    public String ID;
    public ItemStack endItem;
    public List<ItemStack> defaultStartItems;
    public BiConsumer<RecipesEventJS, ItemStack[]> startWith;
    Stage(String ID, ItemStack endItem, List<ItemStack> defaultStartItems, BiConsumer<RecipesEventJS, ItemStack[]> startWith) {
        this.ID = ID;
        this.endItem = endItem;
        this.defaultStartItems = defaultStartItems;
        this.startWith = startWith;
    }
    Stage(Builder builder) {
        this(builder.ID, builder.endItem, builder.defaultStartItems, builder.startWith);
    }
    public static class Builder {
        public String ID;
        public ItemStack endItem;
        public List<ItemStack> defaultStartItems;
        public BiConsumer<RecipesEventJS, ItemStack[]> startWith;
        Builder(String ID) {
            this.ID = ID;
            this.defaultStartItems = new ArrayList<>();
        }
        public static Builder create(String ID) {
            return new Builder(ID);
        }
        public Builder endItem(ItemStack endItem) {
            this.endItem = endItem;
            return this;
        }
        public Builder defaultStartItems(ItemStack ...defaultStartItems) {
            this.defaultStartItems.addAll(Arrays.asList(defaultStartItems));
            return this;
        }
        public Builder startWith(BiConsumer<RecipesEventJS, ItemStack[]> startWith) {
            this.startWith = startWith;
            return this;
        }
        public Stage build() {
            return new Stage(this);
        }
    }
}
