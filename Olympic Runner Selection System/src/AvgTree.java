public class AvgTree {

    public FloatNode avgRoot;

    public AvgTree(){
        this.avgRoot = null;
    }


    public void addRunnerAvg(RunnerID id, float avgRunTime) {
        FloatNode newNode = new FloatNode(id);
        newNode.avgRunTime = avgRunTime;
        avgRoot = insertAvg(avgRoot, newNode);
    }


    public FloatNode insertAvg(FloatNode node, FloatNode newNode) {
        if (node == null) {
            return newNode; // If the current node is null, insert the new node here
        }

        if (newNode.avgRunTime < node.avgRunTime) {
            // If the average run time of the new node is smaller, insert it in the left subtree
            node.left = insertAvg(node.left, newNode);
        } else if (newNode.avgRunTime > node.avgRunTime) {
            // If the average run time of the new node is larger, insert it in the right subtree
            node.right = insertAvg(node.right, newNode);
        } else {
            // If the average run time is the same, compare IDs using isSmaller
            if (newNode.id.isSmaller(node.id)) {
                // If the new node's ID is smaller, insert it in the left subtree
                node.left = insertAvg(node.left, newNode);
            } else if (node.id.isSmaller(newNode.id)) {
                // If the new node's ID is not smaller, insert it in the right subtree
                node.right = insertAvg(node.right, newNode);
            } else {
                // If the IDs are equal, insert it in the middle subtree
                node.middle = insertAvg(node.middle, newNode);
            }
        }

        return node;
    }

}
