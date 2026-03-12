public class TreeNode<T> {
    private T value;
    private TreeNode<T> left;
    private TreeNode<T> right;
    private TreeNode<T> parent;

    public TreeNode(T value) {
        this.value = value;
    }

    public TreeNode(T value, TreeNode<T> parent) {
        this.value = value;
        this.parent = parent;
    }

    public TreeNode(T value, TreeNode<T> left, TreeNode<T> right, TreeNode<T> parent) {
        this.value = value;
        this.left = left;
        this.right = right;
        this.parent = parent;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public TreeNode<T> getLeft() {
        return left;
    }

    public void setLeft(TreeNode<T> left) {
        this.left = left;
    }

    public TreeNode<T> getRight() {
        return right;
    }

    public void setRight(TreeNode<T> right) {
        this.right = right;
    }

    public TreeNode<T> getParent() {
        return parent;
    }

    public void setParent(TreeNode<T> parent) {
        this.parent = parent;
    }

    public boolean isLeaf() {
        return this.left == null && this.right == null;
    }

    public boolean isRoot() {
        return this.parent == null;
    }
}