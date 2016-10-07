public class DiffusionEngine {

  private int[][] world;
  private int W;
  private int H;
  private int SIZE;

  private float FLOW_RATE;
  private int CUTTOFF_THRESHOLD;

  public DiffusionEngine(int W, int H, int SIZE, float FLOW_RATE, int CUTTOFF_THRESHOLD) {
    this.world = GridHelper.makeIntArray(W, H);
    this.W = W;
    this.H = H;
    this.SIZE = SIZE;
    this.FLOW_RATE = FLOW_RATE;
    this.CUTTOFF_THRESHOLD = CUTTOFF_THRESHOLD;
  }
  
  public void update() {
    int[][] newWorld = GridHelper.makeIntArray(this.W, this.H);
    for (int i = 0; i < W; i++) {
      for (int j = 0; j < H; j++) {
        int currentConcentration = this.world[i][j];
        int newConcentration = (int)((1.0 - this.FLOW_RATE)*currentConcentration);
        newConcentration = newConcentration > this.CUTTOFF_THRESHOLD ? newConcentration : 0;
        newWorld[i][j] += newConcentration;

        if (newConcentration > 0) {
          ArrayList<PVector> neighbors = GridHelper.getNeighbors(this.world, i, j);
          int flowPerNeighbor = (int)((this.FLOW_RATE*currentConcentration)/neighbors.size());
          for (PVector neighbor: neighbors) { 
            int x = (int) neighbor.x;
            int y = (int) neighbor.y;
            newWorld[x][y] += flowPerNeighbor;
          }
        }
      }
    }
    this.world = newWorld;  
  }
  
  public void render() {
    background(0);
    for (int i = 0; i < this.W; i++) {
      for (int j = 0; j < this.H; j++) {
        int value = this.world[i][j];
        if (value > this.CUTTOFF_THRESHOLD) {
          int fill = 255;//min(255, value);
          int opacity = min(200, value);
          fill(fill, fill, fill, opacity);
          stroke(fill, fill, fill, 0);
          strokeWeight(0);
          rect(i*this.SIZE, j*this.SIZE, this.SIZE, this.SIZE);
        }
      }
    }
  }
  
  public void setConcentration(int i, int j, int val) {
    this.world[i][j] = val;
  }

  public int getConcentration(int i, int j) {
    return this.world[i][j];
  }
}

public static class GridHelper {

  public static int[][] makeIntArray(int W, int H) {
    int[][] world = new int[W][H];
    for (int i = 0; i < W; i++) {
      for (int j = 0; j < H; j++) {
        world[i][j] = 0;
      }
    }
    return world;  
  }
  
  public static boolean isValid(int[][] mat, int i, int j) {
    int W = mat.length;
    int H = mat[0].length;
    return (i >= 0 && i < W && j >= 0 && j < H);
  }
  public static boolean isValid(int[][] mat, PVector location) {
    int W = mat.length;
    int H = mat[0].length;
    return (location.x >= 0 && location.x < W && location.y >= 0 && location.y < H);
  }
  
  public static ArrayList<PVector> getNeighbors(int[][] mat, int i, int j) {
    ArrayList<PVector> neighbors = new ArrayList<PVector>();
    PVector location = new PVector(i, j);
    ArrayList<PVector> directions = new ArrayList<PVector>();
    directions.add(new PVector(-1, 0));
    directions.add(new PVector(1, 0));
    directions.add(new PVector(0, 1));
    directions.add(new PVector(0, -1));
    
    for (PVector direction : directions) {
      PVector neighbor = PVector.add(direction, location);
      if (isValid(mat, neighbor)) {
        neighbors.add(neighbor);
      }
    }
    return neighbors;
  }  
}