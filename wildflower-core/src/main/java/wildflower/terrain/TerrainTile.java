package wildflower.terrain;

import org.joml.Vector2i;

public class TerrainTile {
    public static int DIMENSION_X = 30;
    public static int DIMENSION_Y = 30;
    public static int GRID_GAP = 10;

    private Vector2i index;
    private TerrainType[][] terrain;

    public TerrainTile(Vector2i index, TerrainType[][] terrain) {
        this.index = index;
        this.terrain = terrain;
    }

    public Vector2i getIndex() {
        return index;
    }

    public TerrainType[][] getTerrain() {
        return terrain;
    }
}
