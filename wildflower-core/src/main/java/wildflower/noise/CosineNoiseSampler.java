package wildflower.noise;

import java.util.LinkedList;
import java.util.List;

public class CosineNoiseSampler {
    private List<Operation> operations = new LinkedList<>();
    private float maxValue = 0;
    private float minValue = 0;

    private enum Operation {
        ADD, MULTIPLY, SCALE_VERTICAL, SCALE_HORIZONTAL,
        TRUNCATE, SHIFT, LIMIT_ABOVE, LIMIT_BELOW, NORMALIZE;

        float scale;
        RandomGrid grid;

        Operation() {
            if (this.name().equals("ADD") || this.name().equals("MULTIPLY")) {
                grid = new RandomGrid();
            }
        }

        Operation by(float scale) {
            this.scale = scale;
            return this;
        }
    }

    public float sample(float x, float y) {
        return sample(x, y, operations.size() - 1);
    }

    public void sample(float[][] result, float xMin, float xStep, int xCount, float yMin, float yStep, float yCount) {
        for (float i = 0; i < xCount; i++) {
            for (float j = 0; j < yCount; j++) {
                float x = xMin + (i * xStep);
                float y = yMin + (j * yStep);
                result[(int) i][(int) j] = sample(x, y);
            }
        }
    }

    private float sample(float x, float y, int operationIndex) {
        if (operationIndex == -1) {
            return 0;
        }

        Operation operation = operations.get(operationIndex);
        switch (operation) {
            case ADD:
                maxValue++;
                return operation.grid.interpolatedValueAt(x, y) + sample(x, y, --operationIndex);
            case MULTIPLY:
                return operation.grid.interpolatedValueAt(x, y) * sample(x, y, --operationIndex);
            case SCALE_VERTICAL:
                minValue *= operation.scale;
                maxValue *= operation.scale;
                return operation.scale * sample(x, y, --operationIndex);
            case SCALE_HORIZONTAL:
                return sample(x / operation.scale, y / operation.scale, --operationIndex);
            case TRUNCATE:
                minValue = (int) minValue;
                maxValue = (int) maxValue;
                return (int) sample(x, y, --operationIndex);
            case SHIFT:
                minValue += operation.scale;
                maxValue += operation.scale;
                return operation.scale + sample(x, y, --operationIndex);
            case LIMIT_BELOW:
                minValue = Math.max(minValue, operation.scale);
                return Math.max(operation.scale, sample(x, y, --operationIndex));
            case LIMIT_ABOVE:
                maxValue = Math.min(maxValue, operation.scale);
                return Math.min(operation.scale, sample(x, y, --operationIndex));
            case NORMALIZE:
                minValue = 0;
                maxValue = 1;
                return map(sample(x, y, --operationIndex), minValue, maxValue, 0, 1);
        }

        throw new IllegalStateException("Should not be possible to get here!");
    }

    private float map(float x, float inMin, float inMax, float outMin, float outMax) {
        return (x - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }

    public CosineNoiseSampler add() {
        operations.add(Operation.ADD);
        return this;
    }

    public CosineNoiseSampler multiply() {
        operations.add(Operation.MULTIPLY);
        return this;
    }

    public CosineNoiseSampler scaleHorizontal(float amount) {
        operations.add(Operation.SCALE_HORIZONTAL.by(amount));
        return this;
    }

    public CosineNoiseSampler scaleVertical(float amount) {
        operations.add(Operation.SCALE_VERTICAL.by(amount));
        return this;
    }

    public CosineNoiseSampler shift(float amount) {
        operations.add(Operation.SHIFT.by(amount));
        return this;
    }

    public CosineNoiseSampler limitAbove(float amount) {
        operations.add(Operation.LIMIT_ABOVE.by(amount));
        return this;
    }

    public CosineNoiseSampler limitBelow(float amount) {
        operations.add(Operation.LIMIT_BELOW.by(amount));
        return this;
    }

    public CosineNoiseSampler truncate() {
        operations.add(Operation.TRUNCATE);
        return this;
    }

    public CosineNoiseSampler normalize() {
        operations.add(Operation.NORMALIZE);
        return this;
    }
}
