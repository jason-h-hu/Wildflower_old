package wildflower;

class Ticker {
    private static final long ONE_MILLISECOND = 1_000_000;
    private static final long ONE_SECOND = 1_000_000_000;

    private TickFunction tickFunction;
    private ConditionalFunction conditionalFunction;

    private final int targetFramesPerSecond;
    private int framesPerSecond;
    private String name;

    @FunctionalInterface
    interface TickFunction {
        void tick(float delta);
    }

    @FunctionalInterface
    interface ConditionalFunction {
        boolean shouldContine();
    }

    Ticker(String name,
           int targetFramesPerSecond,
           TickFunction tickFunction,
           ConditionalFunction conditionalFunction) {
        this.name = name;
        this.targetFramesPerSecond = targetFramesPerSecond;
        this.tickFunction = tickFunction;
        this.conditionalFunction = conditionalFunction;
        this.framesPerSecond = 0;
    }

    void run() {
        System.out.printf("Starting up %s in %s with target framerate %d%n",
                name, Thread.currentThread().getName(), targetFramesPerSecond);

        double delta;
        long optimalTime = ONE_SECOND / targetFramesPerSecond;
        long lastLoopTime = System.nanoTime();
        long lastFpsCountTime = 0;
        long now;

        while (conditionalFunction.shouldContine()) {
            now = System.nanoTime();
            delta = now - lastLoopTime;
            lastLoopTime = now;

            lastFpsCountTime += delta;
            framesPerSecond++;

            if (lastFpsCountTime >= ONE_SECOND) {
                lastFpsCountTime = 0;
                framesPerSecond = 0;
            }

            tickFunction.tick((float) delta / (float) ONE_SECOND);

            try {
                long time = (lastLoopTime - System.nanoTime() + optimalTime) / ONE_MILLISECOND;
                Thread.sleep(Math.max(time, 0));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Somehow while stopped");
    }

    public int getFramesPerSecond() {
        return framesPerSecond;
    }
}
