package wildflower.terrain;

import org.joml.Vector2i;
import wildflower.geometry.AxisAlignedBox;
import wildflower.noise.CosineNoiseSampler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static wildflower.terrain.TerrainTile.DIMENSION_X;
import static wildflower.terrain.TerrainTile.DIMENSION_Y;
import static wildflower.terrain.TerrainTile.GRID_GAP;

public class TerrainEngine {
    private CosineNoiseSampler temperatureFunction;
    private CosineNoiseSampler groundWaterFunction;
    private Map<Vector2i, TerrainTile> cache = new ConcurrentHashMap<>();
    private float terrainTileSideLengthX = (DIMENSION_X - 1) * GRID_GAP;
    private float terrainTileSideLengthY = (DIMENSION_Y - 1) * GRID_GAP;

    public TerrainEngine() {
        temperatureFunction = new CosineNoiseSampler()
                .add().scaleHorizontal(700);

        groundWaterFunction = new CosineNoiseSampler()
                .add().scaleHorizontal(100);
    }

    public Set<TerrainTile> getTerrainFor(AxisAlignedBox region) {
        int minXIndex = (int) (region.getUpperLeft().x / terrainTileSideLengthX);
        int minYIndex = (int) (region.getUpperLeft().y / terrainTileSideLengthY);
        if (region.getUpperLeft().x < 0) minXIndex--;
        if (region.getUpperLeft().y < 0) minYIndex--;

        int maxXIndex = (int) (region.getLowerRight().x / terrainTileSideLengthX);
        int maxYIndex = (int) (region.getLowerRight().y / terrainTileSideLengthY);
        if (region.getLowerRight().x < 0) maxXIndex--;
        if (region.getLowerRight().y < 0) maxYIndex--;

        Set<TerrainTile> tiles = new HashSet<>();
        Vector2i index = new Vector2i(minXIndex, minYIndex);
        while(index.x <= maxXIndex) {
            index.y = minYIndex;
            while(index.y <= maxYIndex) {
                if (!cache.containsKey(index)) {
                    Vector2i indexCopy = new Vector2i(index);
                    TerrainSurface[][] terrain = new TerrainSurface[DIMENSION_X][DIMENSION_Y];
                    fillTile(terrain, indexCopy);
                    TerrainTile tile = new TerrainTile(indexCopy, terrain);
                    tiles.add(tile);
                    cache.put(indexCopy, tile);
                } else {
                    tiles.add(cache.get(index));
                }
                index.y++;
            }
            index.x++;
        }

        return tiles;
    }

    private void fillTile(TerrainSurface[][] terrain, Vector2i index) {
        float[][] temperatureValues = new float[DIMENSION_X][DIMENSION_Y];
        float[][] groundWaterValues = new float[DIMENSION_X][DIMENSION_Y];

        temperatureFunction.sample(temperatureValues,
                index.x * terrainTileSideLengthX, GRID_GAP, DIMENSION_X,
                index.y * terrainTileSideLengthY, GRID_GAP, DIMENSION_Y);

        groundWaterFunction.sample(groundWaterValues,
                index.x * terrainTileSideLengthX, GRID_GAP, DIMENSION_X,
                index.y * terrainTileSideLengthY, GRID_GAP, DIMENSION_Y);

        float temperature, groundWater;
        for (int x = 0; x < DIMENSION_X; x++) {
            for (int y = 0; y < DIMENSION_Y; y++) {
                temperature = temperatureValues[x][y];
                groundWater = groundWaterValues[x][y];
                terrain[x][y] = mapFieldsToTerrainType(temperature, groundWater);
            }
        }
    }

    private TerrainSurface mapFieldsToTerrainType(float temperature, float groundWater) {
        if (temperature > 0.5f && groundWater > 0.5f) {
            return TerrainSurface.GRASS;
        } else if (temperature > 0.5f && groundWater < 0.5f) {
            return TerrainSurface.SAND;
        } else if (temperature < 0.5f && groundWater > 0.5f) {
            return TerrainSurface.WATER;
        } else if (temperature < 0.5f && groundWater < 0.5f) {
            return TerrainSurface.DIRT;
        }
        return TerrainSurface.DIRT;
    }
}
