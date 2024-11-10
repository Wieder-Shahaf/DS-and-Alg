public class MinTree {
    public FloatNode minRoot;

    public MinTree(){
        this.minRoot = null;
    }

    public void addRunnerMin(RunnerID id, float minRunTime) {
        FloatNode newNode = new FloatNode(id);
        newNode.minRunTime = minRunTime;
        minRoot = insertMin(minRoot, newNode);
    }


    public FloatNode insertMin(FloatNode node, FloatNode newNode) {
        if (node == null) {
            return newNode; // If the current node is null, insert the new node here
        }

        if (newNode.minRunTime < node.minRunTime) {
            // If the average run time of the new node is smaller, insert it in the left subtree
            node.left = insertMin(node.left, newNode);
        } else if (newNode.minRunTime > node.minRunTime) {
            // If the average run time of the new node is larger, insert it in the right subtree
            node.right = insertMin(node.right, newNode);
        } else {
            // If the average run time is the same, compare IDs using isSmaller
            if (newNode.id.isSmaller(node.id)) {
                // If the new node's ID is smaller, insert it in the left subtree
                node.left = insertMin(node.left, newNode);
            } else if (node.id.isSmaller(newNode.id)) {
                // If the new node's ID is not smaller, insert it in the right subtree
                node.right = insertMin(node.right, newNode);
            } else {
                // If the IDs are equal, insert it in the middle subtree
                node.middle = insertMin(node.middle, newNode);
            }
        }

        return node;
    }

}
