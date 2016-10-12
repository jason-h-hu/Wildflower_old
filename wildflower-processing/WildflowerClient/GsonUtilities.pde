class PVectorDeserializer implements JsonDeserializer<PVector> {
  @Override
  public PVector deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    JsonObject object = json.getAsJsonObject();
    return new PVector(object.get("x").getAsFloat(), object.get("y").getAsFloat());
  }
}

class PVectorSerializer implements JsonSerializer<PVector> {
    @Override
    public JsonElement serialize(PVector source, Type typeOfSource, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("x", new JsonPrimitive(source.x));
        result.add("y", new JsonPrimitive(source.y));
        return result;
    }
}

class UuidDeserializer implements JsonDeserializer<UUID> {
    @Override
    public UUID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException{
        return UUID.fromString(json.getAsString());
    }
}

class UuidSerializer implements JsonSerializer<UUID> {
    @Override
    public JsonElement serialize(UUID source, Type typeOfT, JsonSerializationContext context) {
        return new JsonPrimitive(source.toString());
    }
}