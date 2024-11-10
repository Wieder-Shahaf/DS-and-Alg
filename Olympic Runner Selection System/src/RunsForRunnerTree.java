public class RunsForRunnerTree {
    public FloatNode runRoot;

    public RunsForRunnerTree(){
        this.runRoot = null;
    }
    public void addRun(RunnerID id, float time) {
        FloatNode newNode = new FloatNode(id);
        newNode.time = time;
        runRoot = insertRun(runRoot, newNode);
    }


    public FloatNode insertRun(FloatNode node, FloatNode newNode) {
        if (node == null) {
            return newNode; // If the current node is null, insert the new node here
        }

        if (newNode.time < node.time) {
            // If the average run time of the new node is smaller, insert it in the left subtree
            node.left = insertRun(node.left, newNode);
        } else if (newNode.time > node.time) {
            // If the average run time of the new node is larger, insert it in the right subtree
            node.right = insertRun(node.right, newNode);
        } else {
            node.middle = insertRun(node.middle, newNode);
        }
        return node;
    }

    public FloatNode findRun(FloatNode node, float time) {
        if (node == null) {
            return null;
        }

        if (time < node.time) {
            // If the time is smaller than the current node's minRunTime, search in the left subtree
            return findRun(node.left, time);
        } else if (time > node.time) {
            // If the time is larger than the current node's minRunTime, search in the right subtree
            return findRun(node.right, time);
        } else {
            // If the time matches the current node's minRunTime, search in the middle subtree
            // or return the current node if the time is found
            return node;
        }
    }
}
