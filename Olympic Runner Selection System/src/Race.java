public class Race {
    private boolean initialized = false;
    private Node root; // Root of the tree sorted by runner ID
    private AvgTree avgTree; // Root of the tree sorted by average run time
    private MinTree minTree;
    private Node fastestRunnerByAvg;
    private Node fastestRunnerByMin;
    public Race(){
        this.root = null;
        this.avgTree = null;
        this.minTree = null;
    }

    public void init() {
        // Initialize the race
        this.root = null;
        this.avgTree= new AvgTree();
        this.minTree = new MinTree();
        this.fastestRunnerByAvg = null;
        this.fastestRunnerByMin = null;
        initialized = true;
    }

    public void addRunner(RunnerID id) {
        // Check if the race has been initialized
        if (!initialized) {
            throw new IllegalArgumentException("The race has not been initialized. Call init() before adding runners.");
        }
        // Check if the runner already exists
        if (findNode(id, root) != null) {
            throw new IllegalArgumentException("Runner with ID " + id.toString() + " already exists.");
        }

        Node newNode = new Node(id);

        root = insert(root, newNode, 0);

        FloatNode newAvgNode = new FloatNode(id);
        newAvgNode.avgRunTime = Float.MAX_VALUE;
        avgTree.avgRoot = avgTree.insertAvg(avgTree.avgRoot, newAvgNode);

        FloatNode newMinNode = new FloatNode(id);
        newMinNode.minRunTime = Float.MAX_VALUE;
        minTree.minRoot = minTree.insertMin(minTree.minRoot, newMinNode);

        if (fastestRunnerByMin == null || (fastestRunnerByMin.minRunTime == Float.MAX_VALUE && id.isSmaller(fastestRunnerByMin.id))) {
            fastestRunnerByMin = newNode;
        }
        if (fastestRunnerByAvg == null || (fastestRunnerByAvg.avgRunTime == Float.MAX_VALUE && id.isSmaller(fastestRunnerByAvg.id))) {
            fastestRunnerByAvg = newNode;
        }

    }

    public void removeRunner(RunnerID id) {
        // Check if the race has been initialized
        if (!initialized) {
            throw new IllegalArgumentException("The race has not been initialized. Call init() before adding runners.");
        }
        Node nodeToRemove = findNode(id, root);

        if (nodeToRemove == null) {
            throw new IllegalArgumentException("Runner with ID " + id.toString() + " does not exist.");
        }

        avgTree.avgRoot = removeAvg(avgTree.avgRoot, id);
        minTree.minRoot = removeMin(minTree.minRoot, id);

        if (nodeToRemove.runsTree != null) {
            nodeToRemove.runsTree = null;
        }
        // Decrement numRuns for the removed runner
        nodeToRemove.numRuns--;

        // Remove the node from the TST
        root = remove(root, id, 0);

        // Update fastestRunnerByAvg if necessary
        if (nodeToRemove == fastestRunnerByAvg) {
            updateFastestRunnerByAvg(root);
        }

        // Update fastestRunnerByMin if necessary
        if (nodeToRemove == fastestRunnerByMin) {
            updateFastestRunnerByMin(root);
        }
    }

    public void addRunToRunner(RunnerID id, float time) {
        // Check if the race has been initialized
        if (!initialized) {
            throw new IllegalArgumentException("The race has not been initialized. Call init() before adding runners.");
        }
        // Find the node corresponding to the given ID
        Node runnerNode = findNode(id, root);

        // Check if the time is negative
        if (time < 0) {
            throw new IllegalArgumentException("Negative running time is not allowed.");
        }

        // If the node doesn't exist, throw an exception
        if (runnerNode == null) {
            throw new IllegalArgumentException("Runner with ID " + id.toString() + " does not exist.");
        }
        avgTree.avgRoot = removeAvg(avgTree.avgRoot, id);
        minTree.minRoot = removeMin(minTree.minRoot, id);

        // Increment numRuns for the runner
        runnerNode.numRuns++;
        runnerNode.sumOfRunTimes += time;

        runnerNode.runsTree.addRun(id,time);

        // Update minRunTime if the newly added time is smaller
        if (time < runnerNode.minRunTime) {
            runnerNode.minRunTime = time;
        }

        // Update avgRunTime
        runnerNode.avgRunTime = calculateAverage(runnerNode);

        // Update fastestRunnerByAvg if necessary
        if (runnerNode.avgRunTime < fastestRunnerByAvg.avgRunTime) {
            fastestRunnerByAvg = runnerNode;
        }

        // Update fastestRunnerByMin if necessary
        if (runnerNode.minRunTime < fastestRunnerByMin.minRunTime) {
            fastestRunnerByMin = runnerNode;
        }

        avgTree.addRunnerAvg(runnerNode.id, runnerNode.avgRunTime);
        minTree.addRunnerMin(runnerNode.id, runnerNode.minRunTime);
    }

    public void removeRunFromRunner(RunnerID id, float time) {
        // Check if the race has been initialized
        if (!initialized) {
            throw new IllegalArgumentException("The race has not been initialized. Call init() before adding runners.");
        }
        Node runnerNode = findNode(id, root);

        if (runnerNode == null) {
            throw new IllegalArgumentException("Runner with ID " + id.toString() + " does not exist.");
        }

        avgTree.avgRoot = removeAvg(avgTree.avgRoot, id);
        minTree.minRoot = removeMin(minTree.minRoot, id);

        FloatNode run = runnerNode.runsTree.findRun(runnerNode.runsTree.runRoot, time);

        if (run == null) {
            throw new IllegalArgumentException("Run time " + time + " not found for runner with ID " + id.toString());
        }

        runnerNode.runsTree.runRoot = removeRun(runnerNode.runsTree.runRoot, time);

        // Check if the removed time was the minRunTime and update it if necessary
        if (time == runnerNode.minRunTime) {
            FloatNode newMinRun = findMinRun(runnerNode.runsTree.runRoot);
            if (newMinRun == null){
                runnerNode.minRunTime = Float.MAX_VALUE;
            } else {
                runnerNode.minRunTime = findMinRun(runnerNode.runsTree.runRoot).time;
            }
        }

        runnerNode.numRuns--;
        runnerNode.sumOfRunTimes -= time;
        runnerNode.avgRunTime = calculateAverage(runnerNode);

        // Update fastestRunnerByAvg if necessary
        if (runnerNode == fastestRunnerByAvg || runnerNode.avgRunTime < fastestRunnerByAvg.avgRunTime) {
            updateFastestRunnerByAvg(root);
        }

        // Update fastestRunnerByMin if necessary
        if (runnerNode == fastestRunnerByMin || runnerNode.minRunTime < fastestRunnerByMin.minRunTime) {
            updateFastestRunnerByMin(root);
        }

        avgTree.addRunnerAvg(id, runnerNode.avgRunTime);
        minTree.addRunnerMin(id, runnerNode.minRunTime);
    }

    public RunnerID getFastestRunnerAvg() {
        // Check if the race has been initialized
        if (!initialized) {
            throw new IllegalArgumentException("The race has not been initialized. Call init() before adding runners.");
        }
        if (fastestRunnerByAvg == null) {
            throw new IllegalArgumentException("No runners in the race.");
        }
        return fastestRunnerByAvg.id;
    }

    public RunnerID getFastestRunnerMin() {
        // Check if the race has been initialized
        if (!initialized) {
            throw new IllegalArgumentException("The race has not been initialized. Call init() before adding runners.");
        }
        if (fastestRunnerByMin == null) {
            throw new IllegalArgumentException("No runners in the race.");
        }
        return fastestRunnerByMin.id;
    }

    public float getMinRun(RunnerID id) {
        // Check if the race has been initialized
        if (!initialized) {
            throw new IllegalArgumentException("The race has not been initialized. Call init() before adding runners.");
        }
        // Find the node corresponding to the given ID
        Node runnerNode = findNode(id, root);

        // If the node doesn't exist, throw an exception
        if (runnerNode == null) {
            throw new IllegalArgumentException("Runner with ID " + id.toString() + " does not exist.");
        }

        // If the runner has no runs, return Float.MAX_VALUE
        if (runnerNode.numRuns == 0) {
            return Float.MAX_VALUE;
        }

        // Return the minRunTime attribute of the runner's node
        return runnerNode.minRunTime;
    }

    public float getAvgRun(RunnerID id) {
        // Check if the race has been initialized
        if (!initialized) {
            throw new IllegalArgumentException("The race has not been initialized. Call init() before adding runners.");
        }
        // Find the node corresponding to the given ID
        Node runnerNode = findNode(id, root);

        // If the node doesn't exist, throw an exception
        if (runnerNode == null) {
            throw new IllegalArgumentException("Runner with ID " + id.toString() + " does not exist.");
        }

        // If the runner has no runs, return Float.MAX_VALUE
        if (runnerNode.numRuns == 0) {
            return Float.MAX_VALUE;
        }

        // Return the avgRunTime attribute of the runner's node
        return runnerNode.avgRunTime;
    }

    private Node findNode(RunnerID id, Node node) {
        if (node == null) {
            return null; // Runner not found
        }

        if (id.isSmaller(node.id)) {
            return findNode(id, node.left);
        } else if (node.id.isSmaller(id)) {
            return findNode(id, node.right);
        } else {
            return node; // Runner found
        }
    }

    private Node insert(Node node, Node newNode, int charIndex) {
        if (node == null) {
            node = newNode;
        } else {
            boolean isSmaller = newNode.id.isSmaller(node.id);

            if (isSmaller) {
                node.left = insert(node.left, newNode, charIndex);
            } else {
                boolean isLarger = node.id.isSmaller(newNode.id);
                if (isLarger) {
                    node.right = insert(node.right, newNode, charIndex);
                } else {
                    if (charIndex < newNode.id.toString().length() - 1) {
                        node.middle = insert(node.middle, newNode, charIndex + 1);
                    } else {
                        // Check if the middle subtree has reached its limit
                        if (node.middle != null && node.middle.left != null && node.middle.right != null) {
                            // Split the middle subtree into two subtrees
                            Node leftNode = new Node(node.middle.id);
                            Node rightNode = new Node(node.middle.right.id);

                            leftNode.left = node.middle.left;
                            rightNode.left = node.middle.middle;
                            rightNode.right = node.middle.right.right;

                            node.middle = leftNode;
                            node.middle.right = rightNode;
                        }

                        // Insert the new node into the middle subtree
                        node.middle = insert(node.middle, newNode, charIndex + 1);
                    }
                }
            }
        }
        return node;
    }

    private Node remove(Node node, RunnerID id, int charIndex) {
        if (node == null) {
            return null;
        }

        // Compare using isSmaller instead of directly comparing characters
        boolean smaller = id.isSmaller(node.id);
        boolean greater = node.id.isSmaller(id);

        if (smaller) {
            node.left = remove(node.left, id, charIndex);
        } else if (greater) {
            node.right = remove(node.right, id, charIndex);
        } else {
            if (charIndex < id.toString().length() - 1) {
                node.middle = remove(node.middle, id, charIndex + 1);
            } else {
                // Node to be removed is found
                if (node.left == null && node.right == null && node.middle == null) {
                    // Case 1: Node is a leaf node
                    return null;
                } else if (node.left == null && node.right == null) {
                    // Case 2: Node has only one child (middle)
                    return node.middle;
                } else if (node.middle == null && node.right == null) {
                    // Case 2: Node has only one child (left)
                    return node.left;
                } else if (node.middle == null && node.left == null) {
                    // Case 2: Node has only one child (right)
                    return node.right;
                } else {
                    // Case 3: Node has two children
                    Node successor = findSuccessor(node.right);
                    node.id = successor.id;
                    node.right = remove(node.right, successor.id, 0);
                }
            }
        }

        return node;
    }

    private Node findSuccessor(Node node) {
        if (node.left == null) {
            return node;
        }
        return findSuccessor(node.left);
    }

    private float calculateAverage(Node node) {
        float avg = node.sumOfRunTimes / node.numRuns;
        return avg;
    }

    private void updateFastestRunnerByAvg(Node node) {
        if (node == null) {
            return;
        }

        // Compare the current node with the current fastest runner
        if (node.avgRunTime < fastestRunnerByAvg.avgRunTime) {
            fastestRunnerByAvg = node;
        }

        // Recursively visit the left subtree if necessary
        if (node.left != null && node.left.avgRunTime < fastestRunnerByAvg.avgRunTime) {
            updateFastestRunnerByAvg(node.left);
        }

        // Recursively visit the right subtree if necessary
        if (node.right != null && node.right.avgRunTime < fastestRunnerByAvg.avgRunTime) {
            updateFastestRunnerByAvg(node.right);
        }
    }

    private void updateFastestRunnerByMin(Node node) {
        if (node == null) {
            return;
        }

        if (node.left != null && node.left.minRunTime < fastestRunnerByMin.minRunTime) {
            fastestRunnerByMin = node.left;
        }

        if (node.right != null && node.right.minRunTime < fastestRunnerByMin.minRunTime) {
            fastestRunnerByMin = node.right;
        }

        updateFastestRunnerByMin(node.left);
        updateFastestRunnerByMin(node.right);
    }

    public int getRankAvg(RunnerID id) {
        // Check if the race has been initialized
        if (!initialized) {
            throw new IllegalArgumentException("The race has not been initialized. Call init() before adding runners.");
        }
        Node runnerNode = findNode(id, root);

        // If the node doesn't exist, throw an exception
        if (runnerNode == null) {
            throw new IllegalArgumentException("Runner with ID " + id.toString() + " does not exist.");
        }

        return getRankAvgHelper(runnerNode, avgTree.avgRoot) + 1;
    }

    private int getRankAvgHelper(Node runnerNode, FloatNode node) {
        if (node == null) {
            return 0; // If the node is null, return 0
        }

        // Compare the runnerNode's average run time with the current node's average run time
        int avgComparison = compareFloat(runnerNode.avgRunTime, node.avgRunTime);
        if (avgComparison < 0) {
            // If the runnerNode's average run time is smaller, recursively search in the left subtree
            return getRankAvgHelper(runnerNode, node.left);
        } else if (avgComparison > 0) {
            // If the runnerNode's average run time is larger, recursively search in the right subtree
            // Add the rank of the left subtree along with 1 (for the current node) and the rank of the right subtree
            return 1 + sizeAvg(node.left) + getRankAvgHelper(runnerNode, node.right);
        } else {
            // If the average run times are equal, use secondary sorting by ID
            boolean idComparison = runnerNode.id.isSmaller(node.id);
            boolean idComparison2 = node.id.isSmaller(runnerNode.id);
            if (idComparison) {
                // If the runnerNode's ID is smaller, recursively search in the left subtree
                return getRankAvgHelper(runnerNode, node.left);
            } else if ((!idComparison) && (!idComparison2)) {
                // If the IDs are equal, return the rank of the left subtree
                return sizeAvg(node.left);
            } else {
                // If the runnerNode's ID is larger, recursively search in the right subtree
                // Add the rank of the left subtree along with 1 (for the current node) and the rank of the right subtree
                return 1 + sizeAvg(node.left) + getRankAvgHelper(runnerNode, node.right);
            }
        }
    }

    public int getRankMin(RunnerID id) {
        // Check if the race has been initialized
        if (!initialized) {
            throw new IllegalArgumentException("The race has not been initialized. Call init() before adding runners.");
        }
        Node runnerNode = findNode(id, root);

        if (runnerNode == null) {
            throw new IllegalArgumentException("Runner with ID " + id.toString() + " does not exist.");
        }

        return getRankMinHelper(runnerNode, minTree.minRoot) + 1;
    }

    private int getRankMinHelper(Node runnerNode, FloatNode node) {
        if (node == null) {
            return 0; // If the node is null, return 0
        }

        // Compare the runnerNode's average run time with the current node's average run time
        int minComparison = compareFloat(runnerNode.minRunTime, node.minRunTime);
        if (minComparison < 0) {
            return getRankMinHelper(runnerNode, node.left);
        } else if (minComparison > 0) {
            return 1 + sizeAvg(node.left) + getRankMinHelper(runnerNode, node.right);
        } else {
            // If the average run times are equal, use secondary sorting by ID
            boolean idComparison = runnerNode.id.isSmaller(node.id);
            boolean idComparison2 = node.id.isSmaller(runnerNode.id);
            if (idComparison) {
                // If the runnerNode's ID is smaller, recursively search in the left subtree
                return getRankMinHelper(runnerNode, node.left);
            } else if ((!idComparison) && (!idComparison2)) {
                // If the IDs are equal, return the rank of the left subtree
                return sizeAvg(node.left);
            } else {
                // If the runnerNode's ID is larger, recursively search in the right subtree
                // Add the rank of the left subtree along with 1 (for the current node) and the rank of the right subtree
                return 1 + sizeAvg(node.left) + getRankMinHelper(runnerNode, node.right);
            }
        }
    }

    private int sizeAvg(FloatNode node) {
        if (node == null) {
            return 0; // If the node is null, return 0
        }
        // Recursively calculate the size of the left and right subtrees and add 1 for the current node
        return 1 + sizeAvg(node.left) + sizeAvg(node.middle) + sizeAvg(node.right);
    }

    private int compareFloat(float a, float b) {
        final float epsilon = 0.000001f; // Define a small epsilon value for float comparison

        // Calculate the difference between a and b
        float diff = a - b;

        // Check if the difference is within the epsilon range
        if (diff > -epsilon && diff < epsilon) {
            return 0; // a and b are considered equal within epsilon
        } else if (diff < 0) {
            return -1; // a is less than b
        } else {
            return 1; // a is greater than b
        }
    }

    private FloatNode removeAvg(FloatNode node, RunnerID id) {
        if (node == null) {
            return null;
        }
        // Compare the average run time of the node with the target ID
        float nodeAvg = node.avgRunTime;
        float targetAvg = findNode(id, root).avgRunTime;

        if (targetAvg < nodeAvg) {
            node.left = removeAvg(node.left, id);
        } else if (targetAvg > nodeAvg) {
            node.right = removeAvg(node.right, id);
        } else {
            // If the average run time matches, check if the node matches the ID
            if ((!node.id.isSmaller(id) && (!id.isSmaller(node.id)))) {
                // Node to be removed is found
                if (node.left == null && node.right == null && node.middle == null) {
                    // Case 1: Node is a leaf node
                    return null;
                } else if (node.left == null && node.right == null) {
                    // Case 2: Node has only one child (middle)
                    return node.middle;
                } else if (node.middle == null && node.right == null) {
                    // Case 2: Node has only one child (left)
                    return node.left;
                } else if (node.middle == null && node.left == null) {
                    // Case 2: Node has only one child (right)
                    return node.right;
                } else {
                    // Case 3: Node has two children
                    FloatNode successor = findSuccessorAvg(node.right);
                    node.id = successor.id;
                    node.avgRunTime = successor.avgRunTime;
                    node.right = removeAvg(node.right, successor.id);
                }
            } else {
                // If the ID doesn't match, continue searching in the appropriate subtree
                if (targetAvg < nodeAvg) {
                    node.left = removeAvg(node.left, id);
                } else {
                    node.right = removeAvg(node.right, id);
                }
            }
        }

        return node;
    }

    private FloatNode removeMin(FloatNode node, RunnerID id) {
        if (node == null) {
            return null;
        }
        // Compare the min run time of the node with the target ID
        float nodeMin = node.minRunTime;
        float targetMin = findNode(id, root).minRunTime;

        if (targetMin < nodeMin) {
            node.left = removeMin(node.left, id);
        } else if (targetMin > nodeMin) {
            node.right = removeMin(node.right, id);
        } else {
            if ((!node.id.isSmaller(id) && (!id.isSmaller(node.id)))) {
                // Node to be removed is found
                if (node.left == null && node.right == null && node.middle == null) {
                    // Case 1: Node is a leaf node
                    return null;
                } else if (node.left == null && node.right == null) {
                    // Case 2: Node has only one child (middle)
                    return node.middle;
                } else if (node.middle == null && node.right == null) {
                    // Case 2: Node has only one child (left)
                    return node.left;
                } else if (node.middle == null && node.left == null) {
                    // Case 2: Node has only one child (right)
                    return node.right;
                } else {
                    // Case 3: Node has two children
                    FloatNode successor = findSuccessorAvg(node.right);
                    node.id = successor.id;
                    node.minRunTime = successor.minRunTime;
                    node.right = removeMin(node.right, successor.id);
                }
            } else {
                // If the ID doesn't match, continue searching in the appropriate subtree
                if (targetMin < nodeMin) {
                    node.left = removeMin(node.left, id);
                } else {
                    node.right = removeMin(node.right, id);
                }
            }
        }

        return node;
    }

    private FloatNode findSuccessorAvg(FloatNode node) {
        if (node.left == null) {
            return node;
        }
        return findSuccessorAvg(node.left);
    }

    private FloatNode removeRun(FloatNode node, float time) {
        if (node == null) {
            return null; // If the current node is null, return null
        }

        if (time < node.time) {
            // If the target run time is smaller than the current node's time, search in the left subtree
            node.left = removeRun(node.left, time);
        } else if (time > node.time) {
            // If the target run time is larger than the current node's time, search in the right subtree
            node.right = removeRun(node.right, time);
        } else {
            // If the target run time matches the current node's time
            if (node.middle != null && node.middle.time == time) {
                // If the target run time is found in the middle subtree, remove it
                node.middle = null;
            } else {
                // If the target run time is not found in the middle subtree, remove the node itself
                // Case 1: Node is a leaf node
                if (node.left == null && node.right == null) {
                    return null;
                }
                // Case 2: Node has only one child (middle)
                else if (node.left == null && node.right == null) {
                    return node.middle;
                }
                // Case 2: Node has only one child (left)
                else if (node.middle == null && node.right == null) {
                    return node.left;
                }
                // Case 2: Node has only one child (right)
                else if (node.middle == null && node.left == null) {
                    return node.right;
                }
                // Case 3: Node has two children
                else {
                    // Find the successor in the right subtree
                    FloatNode successor = findSuccessorAvg(node.right);
                    // Replace the current node's ID and time with the successor's ID and time
                    node.id = successor.id;
                    node.time = successor.time;
                    // Remove the successor from the right subtree
                    node.right = removeRun(node.right, successor.time);
                }
            }
        }

        return node;
    }

    private FloatNode findMinRun(FloatNode node) {
        if (node == null) {
            return null;
        }
        // Traverse to the leftmost node to find the minimum run time
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }


    private class Node {
        RunnerID id;
        Node left, middle, right;
        RunsForRunnerTree runsTree;
        float sumOfRunTimes; // Sum of run times
        float minRunTime; // Minimum run time
        float avgRunTime; // Average run time
        int numRuns; // Number of runs


        private Node(RunnerID id) {
            this.id = id;
            this.left = null;
            this.middle = null;
            this.right = null;
            this.runsTree = new RunsForRunnerTree();
            this.sumOfRunTimes = 0.0f;
            this.numRuns = 0; // Initialize numRuns to 0
            this.minRunTime = Float.MAX_VALUE; // Initialize minRunTime to max value
            this.avgRunTime = Float.MAX_VALUE; // Initialize avgRunTime to max value
        }
    }

}

