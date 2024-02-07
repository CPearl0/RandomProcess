package com.cpearl.randomprocess;

import dev.latvian.mods.kubejs.recipe.RecipesEventJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RandomProcessKubeJSBindings {
    public List<Stage> stages = new ArrayList<>();
    public Stage finalStage;
    public Stage initialStage;
    private final Random random = new Random();
    private static long savedSeed;

    public void saveSeed(Long seed) {
        savedSeed = seed;
    }
    public long getSavedSeed() {
        return savedSeed;
    }
    public void setSeed(Long seed) {
        this.random.setSeed(seed);
    }
    public void setSeedToSaved() {
        setSeed(savedSeed);
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
    private void shuffleInPlace(List<Stage> list) {
        for (int i = list.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            var temp = list.get(i);
            list.set(i, list.get(j));
            list.set(j, temp);
        }
    }
    public void generateRandomProcess(RecipesEventJS event) {
        ConsoleJS.SERVER.info("Begin generating random process.");
        var stageList = new ArrayList<>(stages);
        generateRandomProcessRecursively(event, stageList, finalStage);
        ConsoleJS.SERVER.info("Finish generating random process.");
    }
    private void generateRandomProcessRecursively(RecipesEventJS event, List<Stage> stages, Stage finalStage) {
        ConsoleJS.SERVER.info("Begin. Parent: " + finalStage.ID);

        // shuffle the list
        shuffleInPlace(stages);

        // Randomly find the child count
        int maxChildCount = Math.min(
                stages.size(),
                finalStage.defaultStartItems.size()
        );
        if (maxChildCount == 0) {
            ConsoleJS.SERVER.info("End without child. Parent: " + finalStage.ID);
            return;
        }
        int childCount = random.nextInt(1, maxChildCount + 1);
        ConsoleJS.SERVER.info("Child count: " + childCount);

        // Add to the stages' pointer
        finalStage.child.addAll(stages.subList(stages.size() - childCount, stages.size()));
        finalStage.child.forEach(child -> child.parent.add(finalStage));
        finalStage.child.forEach(child -> ConsoleJS.SERVER.info("Child: " + child.ID));

        // Recursively generate the process tree
        int splitSize = (stages.size() - childCount) / childCount;
        int index = 0;
        for (int i = 0; i < childCount - 1; i++, index += splitSize) {
            var child = stages.get(stages.size() - childCount + i);
            generateRandomProcessRecursively(event,
                    Stream.concat(stages.subList(index, index + splitSize).stream(),
                            child.dependencies.stream()).collect(Collectors.toList()),
                    child);
        }
        var lastChild = stages.get(stages.size() - 1);
        generateRandomProcessRecursively(event,
                Stream.concat(stages.subList(index, stages.size() - childCount).stream(),
                        lastChild.dependencies.stream()).collect(Collectors.toList()),
                lastChild);

        // call the startWith function
        finalStage.doStartWith(event);

        ConsoleJS.SERVER.info("End. Parent: " + finalStage.ID);
    }
}
