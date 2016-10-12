class ClientModel {
  public ViewportModel viewport;
  public UUID id;
}

class ViewportModel {
  public PVector upperLeft;
  public PVector lowerRight;
}

class RenderableEntityModel implements Comparable<RenderableEntityModel> {
  public PVector location; 
  
  @Override
  public int compareTo(RenderableEntityModel other) {
    Float lx1 = new Float(location.x);
    Float lx2 = new Float(other.location.x);
    int result = lx1.compareTo(lx2);
    if (result != 0) return result;
    Float ly1 = new Float(location.y);
    Float ly2 = new Float(other.location.y);
    return ly1.compareTo(ly2);
  }
}

class TerrainTileModel implements Comparable<TerrainTileModel> {
  PVector index;
  int xCount;
  int yCount;
  float gap;
  int[][] terrain;
  
  @Override
  public int compareTo(TerrainTileModel other) {
    Float ix1 = new Float(index.x);
    Float ix2 = new Float(other.index.x);
    int result = ix1.compareTo(ix2);
    if (result != 0) return result;
    Float iy1 = new Float(index.y);
    Float iy2 = new Float(other.index.y);
    return iy1.compareTo(iy2);
  }
}