import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import websockets.WebsocketClient;

import java.util.UUID;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.lang.reflect.Type;

Gson gson = new GsonBuilder()
  .registerTypeAdapter(PVector.class, new PVectorSerializer())
  .registerTypeAdapter(PVector.class, new PVectorDeserializer())
  .registerTypeAdapter(UUID.class, new UuidSerializer())
  .registerTypeAdapter(UUID.class, new UuidDeserializer())
  .create();

WebsocketClient entityClient;
WebsocketClient viewportClient;
WebsocketClient terrainClient;

Set<RenderableEntityModel> renderableEntities = new ConcurrentSkipListSet<RenderableEntityModel>();
Type collectionType = new TypeToken<Collection<RenderableEntityModel>>(){}.getType();
boolean lock = false;

void setup() {
   size(600, 600);
   entityClient = new WebsocketClient(this, "ws://localhost:9090/entity");
   viewportClient = new WebsocketClient(this, "ws://localhost:9090/viewport");
   terrainClient = new WebsocketClient(this, "ws://localhost:9090/terrain");
   
   ViewportModel viewport = new ViewportModel();
   viewport.upperLeft = new PVector(0, 0);
   viewport.lowerRight = new PVector(600, 600);
   
   ClientModel client = new ClientModel();
   client.viewport = viewport;
   client.id = UUID.randomUUID();
   
   entityClient.sendMessage(gson.toJson(client));
   viewportClient.sendMessage(gson.toJson(client));
   terrainClient.sendMessage(gson.toJson(client));
   
   background(255);
}

void draw() {
  background(255);
  fill(0);
  noStroke();
  for (RenderableEntityModel entity : renderableEntities) {
    ellipse(entity.location.x, entity.location.y, 10, 10); 
  }
}

void webSocketEvent(String message) {
  println(message);
}