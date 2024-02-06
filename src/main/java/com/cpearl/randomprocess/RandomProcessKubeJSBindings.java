package com.cpearl.randomprocess;

import dev.latvian.mods.kubejs.recipe.RecipesEventJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class RandomProcessKubeJSBindings {
    public List<Stage> stages = new ArrayList<>();
    public Stage finalStage;
    public Stage initialStage;
    private Random random;

    public void setSeed(Long seed) {
        this.random = new Random(seed);
    }

    public void addStage(Stage ...stages) {
        this.stages.addAll(Arrays.asList(stages));
    }

    public void addFinalStage(Stage finalStage) {
        this.finalStage = finalStage;
    }

    public void addInitialStage(Stage initialStage) {
        this.initialStage = initialStage;
    }

    private List<Stage> shuffle(List<Stage> list) {
        var newList = new ArrayList<>(list);
        for (int i = newList.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            var temp = newList.get(i);
            newList.set(i, newList.get(j));
            newList.set(j, temp);
        }
        return newList;
    }
    public void generateRandomProcess(RecipesEventJS event) {
        var stages = shuffle(this.stages);
        int stageCount = stages.size(), processedStageCount = 0;
        var waitForAddChildStages = new ArrayList<Stage>();
        var nextWaitForAddChildStages = new ArrayList<Stage>();

        ConsoleJS.SERVER.info("Start generating random process.");
        waitForAddChildStages.add(finalStage);
        while (!waitForAddChildStages.isEmpty()) {
            ConsoleJS.SERVER.info("Begin. Waiting: " + waitForAddChildStages.size());
            for (var stage : waitForAddChildStages) {
                ConsoleJS.SERVER.info("Begin parent: " + stage.ID);
                int maxChildren = Math.min(
                        stageCount - processedStageCount,
                        stage.defaultStartItems.size()
                );
                ConsoleJS.SERVER.info("Max children: " + maxChildren);
                int children = (maxChildren == 0) ? 0 : (random.nextInt(maxChildren) + 1);
                ConsoleJS.SERVER.info("Children count: " + children);

                var startItems = new ArrayList<ItemStack>();
                for (int i = 0; i < children; i++) {
                    var child = stages.get(processedStageCount++);
                    ConsoleJS.SERVER.info("Child: " + child.ID);
                    startItems.add(child.endItem);
                    nextWaitForAddChildStages.add(child);
                }
                var startItemArray = new ItemStack[stage.defaultStartItems.size()];
                for (int i = 0; i < stage.defaultStartItems.size(); i++) {
                    if (i < children)
                        startItemArray[i] = startItems.get(i);
                    else
                        startItemArray[i] = stage.defaultStartItems.get(i);
                }
                stage.startWith.accept(event, startItemArray);
                ConsoleJS.SERVER.info("Done parent: " + stage.ID);
            }
            waitForAddChildStages = nextWaitForAddChildStages;
            nextWaitForAddChildStages = new ArrayList<>();
        }
    }
}
