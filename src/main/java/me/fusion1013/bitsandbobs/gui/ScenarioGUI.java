package me.fusion1013.bitsandbobs.gui;

import me.fusion1013.bitsandbobs.scenarios.*;
import me.fusion1013.bitsandbobs.util.ItemStackUtil;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class ScenarioGUI extends AbstractGUI {

    List<IScenario> scenarios;
    List<ITimedScenario> timedScenarios;

    public ScenarioGUI(int invSize, String invName, Plugin plugin) {
        super(invSize, invName);

        backButton(invSize - 1);

        // Instantiate all scenarios
        initScenarios(plugin);
    }

    private void initScenarios(Plugin plugin){
        // Add all scenarios to a list
        scenarios = new ArrayList<>();
        scenarios.add(new NoFall(plugin));
        scenarios.add(new Switcheroo(plugin));
        scenarios.add(new SpeedMode(plugin));
        scenarios.add(new AutoSmelt(plugin));

        timedScenarios = new ArrayList<>();
        timedScenarios.add(new PlayerSwitch(plugin));
        timedScenarios.add(new HelpingHand(plugin));
        timedScenarios.add(new Snitch(plugin));

        updateScenarios();
    }

    private void updateScenarios(){
        // Create all scenario buttons
        for (int i = 0; i < scenarios.size(); i++){
            createScenarioButton(scenarios.get(i), i);
        }

        for (int i = 0; i < timedScenarios.size(); i++){
            createScenarioButton(timedScenarios.get(i), scenarios.size() + i);
        }
    }

    private void createScenarioButton(IScenario scenario, int slot){

        List<String> lore = Arrays.asList("Enabled: " + scenario.getEnabled(), scenario.getDescription());

        ItemStack stack = new ItemStackUtil(scenario.getSlotMaterial())
                .setName(scenario.getName())
                .setEnchantmentGlint(scenario.getEnabled())
                .setLore(lore)
                .getItemStack();
        setItem(slot, stack, player -> {
            scenario.toggle();
            updateScenarios();
        });
    }

    private void backButton(int slot){

        ItemStack stack = new ItemStackUtil(Material.BARRIER)
                .setName("Back")
                .getItemStack();

        setItem(slot, stack, player -> {

            GUIHolder.uhcgui.reload();
            GUIHolder.uhcgui.open(player);

        });
    }

    public void queueTimedScenarios(int delay, int randomRange){
        for (ITimedScenario scenario : timedScenarios){
            if (scenario.getEnabled()){
                scenario.QueueScenario(delay, randomRange);
            }
        }
    }

    public List<String> getActiveScenarios(){
        List<String> activeScenarios = new ArrayList<>();
        activeScenarios.add("Active Scenarios:");

        for (IScenario scenario : scenarios){
            if (scenario.getEnabled()) activeScenarios.add(scenario.getName());
        }
        for (ITimedScenario scenario : timedScenarios){
            if (scenario.getEnabled()) activeScenarios.add(scenario.getName());
        }

        return activeScenarios;
    }
}
