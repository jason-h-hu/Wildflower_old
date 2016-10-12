package wildflower.noise;

import org.joml.Vector2i;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class RandomGrid {
    private Map<Vector2i, Float> values = new ConcurrentHashMap<>();

    public float interpolatedValueAt(float x, float y) {
        int X = (int) x;
        int Y = (int) y;

        if (x < 0) X--;
        if (y < 0) Y--;

        float bottomLeft = valueAt(X, Y);
        float bottomRight = valueAt(X + 1, Y);
        float upperLeft = valueAt(X, Y + 1);
        float upperRight = valueAt(X + 1, Y + 1);

        float f = (float) (1 - Math.cos((x - X) * Math.PI)) * 0.5f;
        float bottom = bottomLeft * (1 - f) + (bottomRight * f);
        float top = upperLeft * (1 - f) + (upperRight * f);

        f = (float) (1 - Math.cos((y - Y) * Math.PI)) * 0.5f;
        return bottom * (1 - f) + (top * f);
    }

    private float valueAt(int x, int y) {
        Vector2i index = new Vector2i(x, y);
        if (!values.containsKey(index)) {
            values.put(index, ThreadLocalRandom.current().nextFloat());
        }
        return values.get(index);
    }
}
