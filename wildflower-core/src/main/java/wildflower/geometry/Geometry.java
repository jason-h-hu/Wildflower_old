package wildflower.geometry;

import org.joml.Vector2f;
import org.joml.Vector2i;

class Geometry {
    static boolean rangesOverlap(float a0, float a1, float b0, float b1) {
        return a0 <= b1 && b0 <= a1;
    }

    static boolean within(float n, float min, float max) {
        return n >= min && n <= max;
    }

    static float clamp(float n, float min, float max) {
        return Math.min(max, Math.max(min, n));
    }

    static Vector2f clamp(Vector2f p, Vector2f upperLeft, Vector2f lowerRight) {
        return p.set(clamp(p.x, upperLeft.x, lowerRight.x), clamp(p.y, upperLeft.y, lowerRight.y));
    }
}