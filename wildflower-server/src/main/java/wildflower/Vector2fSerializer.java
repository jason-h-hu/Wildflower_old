package wildflower;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import org.joml.Vector2f;

import java.lang.reflect.Type;

public class Vector2fSerializer implements JsonSerializer<Vector2f> {
    @Override
    public JsonElement serialize(Vector2f source, Type typeOfSource, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("x", new JsonPrimitive(source.x));
        result.add("y", new JsonPrimitive(source.y));
        return result;
    }
}
