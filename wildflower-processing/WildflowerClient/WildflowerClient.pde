import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import websockets.WebsocketClient;

import java.util.UUID;
import java.util.Collection;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.Type;

Gson gson = new GsonBuilder()
  .registerTypeAdapter(PVector.class, new PVectorSerializer())
  .registerTypeAdapter(PVector.class, new PVectorDeserializer())
  .registerTypeAdapter(UUID.class, new UuidSerializer())
  .registerTypeAdapter(UUID.class, new UuidDeserializer())
  .create();

WebsocketClient viewportClient;
WebsocketClient terrainClient;

Map<PVector, TerrainTileModel> terrainTiles = new ConcurrentHashMap<PVector, TerrainTileModel>();
boolean terrainComplete = false;

PVector upperLeft = new PVector(0, 0);
PVector lowerRight = new PVector(600, 600);

int START_STREAM = 0;
int STOP_STREAM = 1;

void setup() {
   size(600, 600);
   viewportClient = new WebsocketClient(this, "ws://localhost:9090/viewport");
   terrainClient = new WebsocketClient(this, "ws://localhost:9090/terrain");
   
   ViewportModel viewport = new ViewportModel();
   viewport.upperLeft = upperLeft;
   viewport.lowerRight = lowerRight;
   
   ClientModel client = new ClientModel();
   client.viewport = viewport;
   client.id = UUID.randomUUID();
   
   viewportClient.sendMessage(gson.toJson(client));
   terrainClient.sendMessage(gson.toJson(client));
   
   background(255);
}

void draw() {
  background(0);
  fill(0);
  noStroke();
  for(TerrainTileModel tile : terrainTiles.values()) {
    float tileWidth = (tile.xCount - 1) * tile.gap;
    float tileHeight = (tile.xCount - 1) * tile.gap;
    
    for (int x = 0; x < tile.xCount; x++) {
     for (int y = 0; y < tile.yCount; y++) {
       char terrainSurface = (char) tile.terrain[x][y];
       switch (terrainSurface) {
         case 's':
           fill(200, 100, 0); break;
         case 'g':
           fill(10, 100, 10); break;
         case 'd':
           fill(100, 30, 0); break;
         case 'w':
           fill(10, 10, 100); break;
       }
       noStroke();
       ellipse((tileWidth * tile.index.x) + (x * tile.gap) - upperLeft.x, (tileHeight * tile.index.y) + (y * tile.gap) - upperLeft.y, tile.gap, tile.gap);
     }
    }
    
    stroke(0);
    strokeWeight(3);
    noFill();
    rect(tile.index.x * tileWidth - upperLeft.x, tile.index.y * tileHeight - upperLeft.y, tileWidth, tileHeight);
  }
}

void keyPressed() {
  ViewportModel viewport = new ViewportModel();
  viewport.upperLeft = upperLeft;
  viewport.lowerRight = lowerRight;
  
  if (key == CODED) {
    if (keyCode == UP)  {
      viewport.upperLeft.y -= 10;
      viewport.lowerRight.y -= 10;
    } else if (keyCode == DOWN) {
      viewport.upperLeft.y += 10;
      viewport.lowerRight.y += 10;
    } else if (keyCode == LEFT) {
      viewport.upperLeft.x -= 10;
      viewport.lowerRight.x -= 10;
    } else if (keyCode == RIGHT) {
      viewport.upperLeft.x += 10;
      viewport.lowerRight.x += 10;
    }
    viewportClient.sendMessage(gson.toJson(viewport));
  }
}

void webSocketEvent(String message) {
  println(message);
  TerrainTileUpdateModel terrainTileUpdate = gson.fromJson(message, TerrainTileUpdateModel.class);
  switch (terrainTileUpdate.change) {
    case "ADD":
      terrainTiles.put(terrainTileUpdate.item.index, terrainTileUpdate.item);
      break;
    case "REMOVE":
      terrainTiles.remove(terrainTileUpdate.item.index);
      break;
  }
}