package wildflower.api;

import wildflower.Entity;

import org.joml.Vector2f;

public class RenderableEntityModel {
    public Vector2f location;

    public RenderableEntityModel() {
        // No arg constructor for Gson
    }

    public RenderableEntityModel(Entity entity) {
        this.location = entity.getLocation();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof RenderableEntityModel && ((RenderableEntityModel)other).location.equals(location);
    }

    @Override
    public int hashCode() {
        return location.hashCode();
    }
}
