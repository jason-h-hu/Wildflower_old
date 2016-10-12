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
Set<TerrainTileModel> terrainTiles = new ConcurrentSkipListSet<TerrainTileModel>();
Type entityCollectionType = new TypeToken<Collection<RenderableEntityModel>>(){}.getType();
Type terrainTileCollectionType = new TypeToken<Collection<TerrainTileModel>>(){}.getType();
boolean lock = false;

PVector upperLeft = new PVector(0, 0);
PVector lowerRight = new PVector(600, 600);

void setup() {
   size(600, 600);
   //entityClient = new WebsocketClient(this, "ws://localhost:9090/entity");
   viewportClient = new WebsocketClient(this, "ws://localhost:9090/viewport");
   terrainClient = new WebsocketClient(this, "ws://localhost:9090/terrain");
   
   ViewportModel viewport = new ViewportModel();
   viewport.upperLeft = upperLeft;
   viewport.lowerRight = lowerRight;
   
   ClientModel client = new ClientModel();
   client.viewport = viewport;
   client.id = UUID.randomUUID();
   
  // entityClient.sendMessage(gson.toJson(client));
   viewportClient.sendMessage(gson.toJson(client));
   terrainClient.sendMessage(gson.toJson(client));
   
   background(255);
}

void draw() {
  background(255);
  fill(0);
  noStroke();
  for(TerrainTileModel tile : terrainTiles) {
    float tileWidth = (tile.xCount - 1) * tile.gap;
    float tileHeight = (tile.xCount - 1) * tile.gap;
    stroke(0);
    strokeWeight(3);
    noFill();
    //rect(tile.index.x * tileWidth - upperLeft.x, tile.index.y * tileHeight - upperLeft.y, tileWidth, tileHeight);
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
       rect((tileWidth * tile.index.x) + (x * tile.gap) - upperLeft.x, (tileHeight * tile.index.y) + (y * tile.gap) - upperLeft.y, tile.gap, tile.gap);
     }
    }
  }
  
  for (RenderableEntityModel entity : renderableEntities) {
    ellipse(entity.location.x, entity.location.y, 10, 10); 
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
  Collection<TerrainTileModel> tiles = gson.fromJson(message, terrainTileCollectionType);
  terrainTiles.clear();
  terrainTiles.addAll(tiles);
}