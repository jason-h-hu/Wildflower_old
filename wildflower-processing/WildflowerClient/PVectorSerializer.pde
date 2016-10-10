import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import java.lang.reflect.Type;

class PVectorSerializer implements JsonSerializer<PVector> {
    @Override
    public JsonElement serialize(PVector source, Type typeOfSource, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("x", new JsonPrimitive(source.x));
        result.add("y", new JsonPrimitive(source.y));
        return result;
    }
}