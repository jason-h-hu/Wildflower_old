package wildflower.terrain;

import org.joml.Vector2i;

public class TerrainTile {
    public static int DIMENSION_X = 15;
    public static int DIMENSION_Y = 15;
    public static int GRID_GAP = 10;

    private Vector2i index;
    private TerrainSurface[][] terrain;

    public TerrainTile(Vector2i index, TerrainSurface[][] terrain) {
        this.index = index;
        this.terrain = terrain;
    }

    public Vector2i getIndex() {
        return index;
    }

    public TerrainSurface[][] getTerrain() {
        return terrain;
    }
}
