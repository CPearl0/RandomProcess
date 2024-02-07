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
    public List<Stage> dependencies;
    public List<Stage> parent;
    public List<Stage> child;
    Stage(String ID, ItemStack endItem, List<ItemStack> defaultStartItems, BiConsumer<RecipesEventJS, ItemStack[]> startWith, List<Stage> dependencies) {
        this.ID = ID;
        this.endItem = endItem;
        this.defaultStartItems = defaultStartItems;
        this.startWith = startWith;
        this.dependencies = dependencies;
        this.parent = new ArrayList<>();
        this.child = new ArrayList<>();
    }
    Stage(Builder builder) {
        this(builder.ID, builder.endItem, builder.defaultStartItems, builder.startWith, builder.dependencies);
    }
    public static class Builder {
        public String ID;
        public ItemStack endItem;
        public List<ItemStack> defaultStartItems;
        public BiConsumer<RecipesEventJS, ItemStack[]> startWith;
        public List<Stage> dependencies;
        Builder(String ID) {
            this.ID = ID;
            this.defaultStartItems = new ArrayList<>();
            this.dependencies = new ArrayList<>();
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
        public Builder dependencies(Stage ...dependencies) {
            this.dependencies.addAll(Arrays.asList(dependencies));
            return this;
        }
        public Stage build() {
            return new Stage(this);
        }
    }

    public void doStartWith(RecipesEventJS event) {
        ItemStack[] startItems = new ItemStack[defaultStartItems.size()];
        int i = 0;
        for (; i < child.size(); i++)
            startItems[i] = child.get(i).endItem;
        for (; i < startItems.length; i++)
            startItems[i] = defaultStartItems.get(i);
        startWith.accept(event, startItems);
    }
}
