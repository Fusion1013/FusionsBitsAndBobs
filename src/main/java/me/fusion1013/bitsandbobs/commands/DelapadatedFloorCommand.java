package me.fusion1013.bitsandbobs.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DelapadatedFloorCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Generates the pattern
        int[][] pattern = generatePattern(Integer.parseInt(args[0]), args[5], args[6]);

        // Gets corners and y pos
        int cornerx = Integer.parseInt(args[1]);
        int ypos = Integer.parseInt(args[2]);
        int cornerz = Integer.parseInt(args[3]);

        // Sets the blocks in the world
        World world = ((Player) sender).getWorld();
        for (int x = 0; x < pattern.length; x++) {
            for (int z = 0; z < pattern.length; z++) {
                Location currentPos = new Location(world, cornerx + x, ypos, cornerz + z);

                String[] materialsWithChances = parseMaterials(args[4] + "," + args[5]);
                Material currentMat = getMaterial(materialsWithChances[pattern[x][z]]);

                world.getBlockAt(currentPos).setType(currentMat);
            }
        }

        return true;
    }

    private int[][] generatePattern(int size, String materials, String modifiers) {
        int[][] pattern = new int[size][size];
        String[] materialsWithChances = parseMaterials(materials);
        boolean allowBackPlacement = Boolean.parseBoolean(modifiers);

        Random r = new Random();

        // Sets the first layer of randomized blocks
        pattern = replaceBlockWithAtRandom(pattern, 0, 1, getChance(materialsWithChances[0]), allowPlaceNextTo(materialsWithChances[0]));

        // Runs the iterator for all materials in materialsWithChances
        // TODO: Random block scatter using replaceBlockWithAtRandom
        for (int i = 1; i < materialsWithChances.length; i++){
            pattern = runIteration(pattern, i, i+1, getChance(materialsWithChances[i]), allowPlaceNextTo(materialsWithChances[i]), allowBackPlacement);
        }

        return pattern;
    }

    /**
     * Iterates over all tiles in input and changes blockToReplace at random with chance to setTo. If allowNextTo is false, no tiles next to each other will be set to the same tile type
     * @param input input pattern to edit
     * @param blockToReplace the tile type to replace
     * @param setTo the tile type to be set
     * @param chance the chance of the tile being changed
     * @param allowNextTo if tiles should be allowed to be set next to each other
     * @return
     */
    private int[][] replaceBlockWithAtRandom(int[][] input, int blockToReplace, int setTo, float chance, boolean allowNextTo){
        Random r = new Random();

        for (int x = 0; x < input.length; x++) {
            for (int z = 0; z < input.length; z++) {
                if (r.nextInt(101) <= chance * 100 && input[x][z] == blockToReplace){
                    if (allowNextTo || !nearbyTileOfType(input, x, z, setTo)) input[x][z] = setTo;
                }
            }
        }

        return input;
    }

    /**
     * Runs one iteration of the pattern generator
     * @param input the input pattern to edit
     * @param setAt the tile type to set at
     * @param setTo the tile type to set to
     * @param chance the chance of changing a tile
     * @param allowNextTo if tiles should be allowed to be placed next to each other
     * @return
     */
    private int[][] runIteration(int[][] input, int setAt, int setTo, float chance, boolean allowNextTo, boolean allowBackPlacement){
        List<int[]> coordinatesOfType = getCoordinatesForTilesOfType(input, setAt, allowBackPlacement);

        for (int[] i : coordinatesOfType){
            input = setAdjacentIfEmpty(input, i[0], i[1], setTo, chance, allowNextTo);
        }

        return input;
    }

    /**
     * Sets the adjacent tile to setTo if that tile is empty (0)
     * @param input the input pattern to edit
     * @param x coordinate on the x-axis
     * @param z coordinate on the z-axis
     * @param setTo tile to set to
     * @param chance chance of tile being changed
     * @return
     */
    private int[][] setAdjacentIfEmpty(int[][] input, int x, int z, int setTo, float chance, boolean allowNextTo) {
        setTileWithChance(input, Math.max(x - 1, 0), z, setTo, chance, allowNextTo);
        setTileWithChance(input, x, Math.max(z - 1, 0), setTo, chance, allowNextTo);
        setTileWithChance(input, Math.min(x + 1, input.length - 1), z, setTo, chance, allowNextTo);
        setTileWithChance(input, x, Math.min(z + 1, input.length - 1), setTo, chance, allowNextTo);

        return input;
    }

    /**
     * Sets a tile at position x z to setTo with chance
     * @param input input pattern to edit
     * @param x coordinate on x-axis
     * @param z coordinate on z-axis
     * @param setTo tile to set to
     * @param chance chance of tile being placed
     * @return
     */
    private int[][] setTileWithChance(int[][] input, int x, int z, int setTo, float chance, boolean allowNextTo) {
        int tile = input[x][z];
        Random r = new Random();
        if (tile == 0 && r.nextInt(1000001) <= chance * 1000000 && (allowNextTo || !nearbyTileOfType(input, x, z, setTo))) {
            input[x][z] = setTo;
        }

        return input;
    }

    /**
     * Returns the coordinates for all tiles of type tile
     * @param input the input pattern to analyze
     * @param tile the tile to look for
     * @return
     */
    private List<int[]> getCoordinatesForTilesOfType(int[][] input, int tile, boolean allowBackPlacement) {
        List<int[]> output = new ArrayList<>();

        for (int x = 0; x < input.length; x++) {
            for (int z = 0; z < input.length; z++) {
                if (allowBackPlacement && input[x][z] <= tile && input[x][z] > 0) output.add(new int[]{x, z});
                else if (input[x][z] == tile) output.add(new int[]{x, z});
            }
        }
        return output;
    }

    /**
     * Checks if there is a nearby tile of type tile
     * @param input the input pattern to analyze
     * @param x position on the x-axis
     * @param z position on the z-axis
     * @param tile the tile type to look for
     * @return
     */
    private boolean nearbyTileOfType(int[][] input, int x, int z, int tile){
        if(input[Math.max(x - 1, 0)][z] == tile) return true;
        if(input[x][Math.max(z - 1, 0)] == tile) return true;
        if(input[Math.min(x + 1, input.length - 1)][z] == tile) return true;
        if(input[x][Math.min(z + 1, input.length - 1)] == tile) return true;

        return false;
    }

    private boolean allowPlaceNextTo(String materialWithChance){
        String[] split = materialWithChance.split(":");
        if (split.length < 3) return true;
        else return Boolean.parseBoolean(split[2]);
    }

    private Material getMaterial(String materialWithChance){
        return Material.getMaterial(materialWithChance.split(":")[0]);
    }

    private float getChance(String materialWithChance){
        return Float.parseFloat(materialWithChance.split(":")[1]);
    }

    // Parses a string input into materials and chances
    private String[] parseMaterials(String input){
        input = input.toUpperCase();
        String[] split = input.split(",");
        return split;
    }
}
