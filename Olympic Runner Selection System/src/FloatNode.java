public class FloatNode {
    RunnerID id;
    FloatNode left, middle, right;
    float avgRunTime;
    float minRunTime;
    float time;

    FloatNode(RunnerID id) {
        this.id = id;
        this.left = null;
        this.middle = null;
        this.right = null;
        this.avgRunTime = Float.MAX_VALUE;
        this.minRunTime = Float.MAX_VALUE;
        this.time = 0;

    }
}