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