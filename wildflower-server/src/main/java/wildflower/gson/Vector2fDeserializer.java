package wildflower.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonParseException;

import org.joml.Vector2f;

import java.lang.reflect.Type;

public class Vector2fDeserializer implements JsonDeserializer<Vector2f> {
    @Override
    public Vector2f deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        return new Vector2f(object.get("x").getAsFloat(), object.get("y").getAsFloat());
    }
}
