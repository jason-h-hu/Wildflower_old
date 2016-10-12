package wildflower.terrain;

public enum TerrainSurface {
    SAND('s'), GRASS('g'), DIRT('d'), WATER('w');

    public char sigil;

    TerrainSurface(char sigil) {
        this.sigil = sigil;
    }
}