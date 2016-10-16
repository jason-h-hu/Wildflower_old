package wildflower.api;

import com.google.gson.annotations.Expose;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ClientModel {
    public class State {
        private State() {}
        public boolean needsNewTerrain = true;
        public Set<TerrainTileModel> terrainTileModels = new HashSet<>();
        public Set<RenderableEntityModel> renderableEntityModels = new HashSet<>();
    }

    public ViewportModel viewport;
    public UUID id;

    @Expose(serialize = false, deserialize = false)
    public State state = new State();

    public ClientModel() {
        // No arg constructor for Gson
    }
}
