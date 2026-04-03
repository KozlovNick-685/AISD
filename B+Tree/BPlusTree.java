import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


class BPlusTree {
    protected BPlusTreeNode root;
    private final int order;

    public BPlusTree(int order) {
        if (order < 3) {
            throw new IllegalArgumentException("Order must be at least 3");
        }
        this.root = new BPlusTreeNode(true);
        this.order = order;
    }


    private BPlusTreeNode findLeaf(int key) {
        BPlusTreeNode node = root;
        while (!node.isLeaf) {
            int i = 0;
            while (i < node.keys.size() && key >= node.keys.get(i)) {
                i++;
            }
            node = node.children.get(i);
        }
        return node;
    }


    public void insert(int key) {
        BPlusTreeNode leaf = findLeaf(key);
        insertIntoLeaf(leaf, key);

        if (leaf.keys.size() > order - 1) {
            splitLeaf(leaf);
        }
    }

    private void insertIntoLeaf(BPlusTreeNode leaf, int key) {
        int pos = Collections.binarySearch(leaf.keys, key);
        if (pos < 0) {
            pos = -(pos + 1);
        }
        leaf.keys.add(pos, key);
    }

    private void splitLeaf(BPlusTreeNode leaf) {
        int mid = order / 2;
        BPlusTreeNode newLeaf = new BPlusTreeNode(true);


        newLeaf.keys.addAll(leaf.keys.subList(mid, leaf.keys.size()));
        leaf.keys.subList(mid, leaf.keys.size()).clear();

        newLeaf.next = leaf.next;
        leaf.next = newLeaf;


        if (leaf == root) {
            BPlusTreeNode newRoot = new BPlusTreeNode(false);
            newRoot.keys.add(newLeaf.keys.get(0));
            newRoot.children.add(leaf);
            newRoot.children.add(newLeaf);
            root = newRoot;
        } else {
            insertIntoParent(leaf, newLeaf, newLeaf.keys.get(0));
        }
    }

    private void insertIntoParent(BPlusTreeNode left, BPlusTreeNode right, int key) {
        BPlusTreeNode parent = findParent(root, left);

        if (parent == null) {

            BPlusTreeNode newRoot = new BPlusTreeNode(false);
            newRoot.keys.add(key);
            newRoot.children.add(left);
            newRoot.children.add(right);
            root = newRoot;
            return;
        }

        int pos = Collections.binarySearch(parent.keys, key);
        if (pos < 0) {
            pos = -(pos + 1);
        }

        parent.keys.add(pos, key);
        parent.children.add(pos + 1, right);

        if (parent.keys.size() > order - 1) {
            splitInternal(parent);
        }
    }

    private void splitInternal(BPlusTreeNode internal) {
        int mid = order / 2;
        BPlusTreeNode newInternal = new BPlusTreeNode(false);


        int keyToPromote = internal.keys.get(mid);


        newInternal.keys.addAll(internal.keys.subList(mid + 1, internal.keys.size()));
        internal.keys.subList(mid, internal.keys.size()).clear();


        newInternal.children.addAll(internal.children.subList(mid + 1, internal.children.size()));
        internal.children.subList(mid + 1, internal.children.size()).clear();


        if (internal == root) {
            BPlusTreeNode newRoot = new BPlusTreeNode(false);
            newRoot.keys.add(keyToPromote);
            newRoot.children.add(internal);
            newRoot.children.add(newInternal);
            root = newRoot;
        } else {
            insertIntoParent(internal, newInternal, keyToPromote);
        }
    }


    private BPlusTreeNode findParent(BPlusTreeNode current, BPlusTreeNode target) {
        if (current.isLeaf || current.children.isEmpty()) {
            return null;
        }

        for (int i = 0; i < current.children.size(); i++) {
            if (current.children.get(i) == target) {
                return current;
            }
            BPlusTreeNode parent = findParent(current.children.get(i), target);
            if (parent != null) {
                return parent;
            }
        }
        return null;
    }


    public boolean delete(int key) {
        BPlusTreeNode leaf = findLeaf(key);
        int pos = leaf.keys.indexOf(key);

        if (pos < 0) {
            return false;
        }

        leaf.keys.remove(pos);


        if (leaf == root && leaf.keys.isEmpty()) {
            return true;
        }

        int minKeys = (order + 1) / 2 - 1;
        if (leaf.keys.size() >= minKeys || leaf == root) {
            return true;
        }

        handleUnderflow(leaf);
        return true;
    }

    private void handleUnderflow(BPlusTreeNode node) {
        BPlusTreeNode parent = findParent(root, node);
        if (parent == null) return;

        int childIndex = -1;
        for (int i = 0; i < parent.children.size(); i++) {
            if (parent.children.get(i) == node) {
                childIndex = i;
                break;
            }
        }


        if (childIndex > 0) {
            BPlusTreeNode leftSibling = parent.children.get(childIndex - 1);
            int minKeys = (order + 1) / 2 - 1;

            if (leftSibling.keys.size() > minKeys) {

                if (node.isLeaf) {
                    node.keys.add(0, leftSibling.keys.remove(leftSibling.keys.size() - 1));
                    parent.keys.set(childIndex - 1, node.keys.get(0));
                } else {
                    node.keys.add(0, parent.keys.get(childIndex - 1));
                    parent.keys.set(childIndex - 1, leftSibling.keys.remove(leftSibling.keys.size() - 1));
                    node.children.add(0, leftSibling.children.remove(leftSibling.children.size() - 1));
                }
                return;
            }
        }


        if (childIndex < parent.children.size() - 1) {
            BPlusTreeNode rightSibling = parent.children.get(childIndex + 1);
            int minKeys = (order + 1) / 2 - 1;

            if (rightSibling.keys.size() > minKeys) {

                if (node.isLeaf) {
                    node.keys.add(rightSibling.keys.remove(0));
                    parent.keys.set(childIndex, rightSibling.keys.get(0));
                } else {
                    node.keys.add(parent.keys.get(childIndex));
                    parent.keys.set(childIndex, rightSibling.keys.remove(0));
                    node.children.add(rightSibling.children.remove(0));
                }
                return;
            }
        }


        if (childIndex > 0) {

            BPlusTreeNode leftSibling = parent.children.get(childIndex - 1);
            if (node.isLeaf) {
                leftSibling.keys.addAll(node.keys);
                leftSibling.next = node.next;
            } else {
                leftSibling.keys.add(parent.keys.get(childIndex - 1));
                leftSibling.keys.addAll(node.keys);
                leftSibling.children.addAll(node.children);
            }
            parent.children.remove(childIndex);
            parent.keys.remove(childIndex - 1);

            if (parent == root && parent.keys.isEmpty()) {
                root = leftSibling;
            } else if (parent.keys.size() < (order + 1) / 2 - 1 && parent != root) {
                handleUnderflow(parent);
            }
        } else {

            BPlusTreeNode rightSibling = parent.children.get(childIndex + 1);
            if (node.isLeaf) {
                node.keys.addAll(rightSibling.keys);
                node.next = rightSibling.next;
            } else {
                node.keys.add(parent.keys.get(childIndex));
                node.keys.addAll(rightSibling.keys);
                node.children.addAll(rightSibling.children);
            }
            parent.children.remove(childIndex + 1);
            parent.keys.remove(childIndex);

            if (parent == root && parent.keys.isEmpty()) {
                root = node;
            } else if (parent.keys.size() < (order + 1) / 2 - 1 && parent != root) {
                handleUnderflow(parent);
            }
        }
    }


    public boolean search(int key) {
        BPlusTreeNode leaf = findLeaf(key);
        return leaf.keys.contains(key);
    }


    public void printTree() {
        printNode(root, 0);
    }

    private void printNode(BPlusTreeNode node, int level) {
        System.out.println("Level " + level + ": " + node.keys + (node.isLeaf ? " (leaf)" : ""));
        if (!node.isLeaf) {
            for (BPlusTreeNode child : node.children) {
                printNode(child, level + 1);
            }
        }
    }
}
