package wildflower.api;

import wildflower.Entity;

import org.joml.Vector2f;

public class EntityRenderingModel {
    public Vector2f location;

    public EntityRenderingModel() {
        // No arg constructor for Gson
    }

    public EntityRenderingModel(Entity entity) {
        this.location = entity.getLocation();
    }
}
