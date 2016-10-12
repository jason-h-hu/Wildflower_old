package wildflower.api;

import org.joml.Vector2f;
import wildflower.terrain.TerrainTile;

import static wildflower.terrain.TerrainTile.DIMENSION_X;
import static wildflower.terrain.TerrainTile.DIMENSION_Y;
import static wildflower.terrain.TerrainTile.GRID_GAP;

public class TerrainTileModel {
    public Vector2f index;
    public int xCount;
    public int yCount;
    public float gap;
    public int[][] terrain;

    public TerrainTileModel() {
        // No arg constructor Gson
    }

    public TerrainTileModel(TerrainTile tile) {
        this.index = new Vector2f(tile.getIndex().x, tile.getIndex().y);
        this.xCount = DIMENSION_X;
        this.yCount = DIMENSION_Y;
        this.gap = GRID_GAP;

        this.terrain = new int[xCount][yCount];
        for (int x = 0; x < xCount; x++) {
            for (int y = 0; y < yCount; y++) {
                terrain[x][y] = (int) tile.getTerrain()[x][y].sigil;
            }
        }
    }
}
