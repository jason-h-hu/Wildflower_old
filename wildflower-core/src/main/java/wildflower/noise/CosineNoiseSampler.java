package wildflower.noise;

import java.util.LinkedList;
import java.util.List;

public class CosineNoiseSampler {
    private List<OperationItem> operations = new LinkedList<>();
    private float maxValue = 0;
    private float minValue = 0;

    private enum Operation {
        ADD, MULTIPLY, SCALE_VERTICAL, SCALE_HORIZONTAL,
        TRUNCATE, SHIFT, LIMIT_ABOVE, LIMIT_BELOW, NORMALIZE
    }

    private class OperationItem {
        float scale;
        RandomGrid grid;
        Operation operation;

        OperationItem(Operation operation, float scale) {
            this.operation = operation;
            this.scale = scale;
            if (operation == Operation.ADD || operation == Operation.MULTIPLY) {
                this.grid = new RandomGrid();
            }
        }

        OperationItem(Operation operation) {
            this(operation, 0);
        }
    }

    public float sample(float x, float y) {
        return sample(x, y, operations.size() - 1, 0, 0);
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

    private float sample(float x, float y, int oIndex, int aIndex, int mIndex) {
        if (oIndex == -1) {
            return 0;
        }

        OperationItem operationItem = operations.get(oIndex);
        switch (operationItem.operation) {
            case ADD:
                maxValue++;
                return operationItem.grid.interpolatedValueAt(x, y) + sample(x, y, --oIndex, ++aIndex, mIndex);
            case MULTIPLY:
                return operationItem.grid.interpolatedValueAt(x, y) * sample(x, y, --oIndex, aIndex, ++mIndex);
            case SCALE_VERTICAL:
                minValue *= operationItem.scale;
                maxValue *= operationItem.scale;
                return operationItem.scale * sample(x, y, --oIndex, aIndex, mIndex);
            case SCALE_HORIZONTAL:
                return sample(x / operationItem.scale, y / operationItem.scale, --oIndex, aIndex, mIndex);
            case TRUNCATE:
                minValue = (int) minValue;
                maxValue = (int) maxValue;
                return (int) sample(x, y, --oIndex, aIndex, mIndex);
            case SHIFT:
                minValue += operationItem.scale;
                maxValue += operationItem.scale;
                return operationItem.scale + sample(x, y, --oIndex, aIndex, mIndex);
            case LIMIT_BELOW:
                minValue = Math.max(minValue, operationItem.scale);
                return Math.max(operationItem.scale, sample(x, y, --oIndex, aIndex, mIndex));
            case LIMIT_ABOVE:
                maxValue = Math.min(maxValue, operationItem.scale);
                return Math.min(operationItem.scale, sample(x, y, --oIndex, aIndex, mIndex));
            case NORMALIZE:
                float prevMinValue = minValue;
                float prevMaxValue = maxValue;
                minValue = 0;
                maxValue = 1;
                return map(sample(x, y, --oIndex, aIndex, mIndex), prevMinValue, prevMaxValue, 0, 1);
        }

        throw new IllegalStateException("Should not be possible to get here!");
    }

    private float map(float x, float inMin, float inMax, float outMin, float outMax) {
        return (x - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;
    }

    public CosineNoiseSampler add() {
        operations.add(new OperationItem(Operation.ADD));
        return this;
    }

    public CosineNoiseSampler multiply() {
        operations.add(new OperationItem(Operation.MULTIPLY));
        return this;
    }

    public CosineNoiseSampler scaleHorizontal(float amount) {
        operations.add(new OperationItem(Operation.SCALE_HORIZONTAL, amount));
        return this;
    }

    public CosineNoiseSampler scaleVertical(float amount) {
        operations.add(new OperationItem(Operation.SCALE_VERTICAL, amount));
        return this;
    }

    public CosineNoiseSampler shift(float amount) {
        operations.add(new OperationItem(Operation.SHIFT, amount));
        return this;
    }

    public CosineNoiseSampler limitAbove(float amount) {
        operations.add(new OperationItem(Operation.LIMIT_ABOVE, amount));
        return this;
    }

    public CosineNoiseSampler limitBelow(float amount) {
        operations.add(new OperationItem(Operation.LIMIT_BELOW, amount));
        return this;
    }

    public CosineNoiseSampler truncate() {
        operations.add(new OperationItem(Operation.TRUNCATE));
        return this;
    }

    public CosineNoiseSampler normalize() {
        operations.add(new OperationItem(Operation.NORMALIZE));
        return this;
    }
}
