package wildflower.terrain;

public enum TerrainType {
    SAND('s'), GRASS('g'), DIRT('d'), WATER('w');

    public char sigil;

    TerrainType(char sigil) {
        this.sigil = sigil;
    }
}