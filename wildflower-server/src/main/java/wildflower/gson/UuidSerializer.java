package wildflower.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.UUID;

public class UuidSerializer implements JsonSerializer<UUID> {
    @Override
    public JsonElement serialize(UUID source, Type typeOfT, JsonSerializationContext context) {
        return new JsonPrimitive(source.toString());
    }
}
