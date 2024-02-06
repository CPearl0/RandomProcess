package com.cpearl.randomprocess;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;

public class RandomProcessPlugin extends KubeJSPlugin {
    @Override
    public void registerBindings(BindingsEvent event) {
        event.add(RandomProcess.MODID, new RandomProcessKubeJSBindings());
        event.add("Stage", Stage.class);
        event.add("StageBuilder", Stage.Builder.class);
    }
}
