public class Tree<T extends Comparable<T>> {

    private TreeNode<T> root;
    private int height;

    public Tree(TreeNode<T> root) {
        this.root = root;
        this.height = 1;
    }

    public Tree(){
        root=null;
        height=0;
    }

    public void add(T value) {
        if (root == null) {
            root = new TreeNode<>(value);
        } else {
            add(root, value);
        }
        height=height();
    }

    private void add(TreeNode<T> current, T value) {
        // если текущая нода больше вставляемого значения
        // проверяем, есть ли левый потомок
        // если нет, то добавляем нового потомка
        // если есть, вызываем рекурсивный метод снова
        // если текущая нода меньше вставляемого значения
        // проверяем, есть ли правый потомок и делаем все то же самое
        //(коммент с пары)
        if (current.getValue().compareTo(value) > 0) {
            if (current.getLeft() == null) {
                current.setLeft(new TreeNode<>(value,current));
            } else {
                add(current.getLeft(), value);
            }
        } else  {
            if (current.getRight() == null) {
                current.setRight(new TreeNode<>(value,current));
            } else {
                add(current.getRight(), value);
            }
        }
    }

    public int height(){
        return height(root);
    }
    public int height(TreeNode<T> node){
        if (node==null){
            return 0;
        }
        int leftHeight = height(node.getLeft()); // высота левого поддерева
        int rightHeight = height(node.getRight()); // высота правого дерева
        return Math.max(leftHeight,rightHeight)+1; //+1 короче чтобы учитывался корень тоже
    }

    public TreeNode<T> get(T value){
        return get(root,value);
    }

    public TreeNode<T> get(TreeNode<T> current, T value){
        if (current == null) {return null;}
        int compare = current.getValue().compareTo(value);
        if (compare == 0){
            return current;
        } else if (compare>0){
            return get(current.getLeft(),value); //типо если больше, то ищем в левом поддереве
        } else {
            return get(current.getRight(),value); // иначе в правом дереве
        }
    }

    public boolean remove(T value) { // а вот хардово было... пришлось чуть чуть вайбкодить (ну или не чуть чуть)
        TreeNode<T> nodeToRemove = get(value);
        if (nodeToRemove == null) return false;

        remove(nodeToRemove);
        return true;
    }

    public void remove(TreeNode<T> node) {
        if (node.getLeft() == null && node.getRight() == null) {
            if (node.getParent() == null) {
                root = null;
            } else {
                TreeNode<T> parent = node.getParent();
                if (parent.getLeft() == node) {
                    parent.setLeft(null);
                } else {
                    parent.setRight(null);
                }
            }
        }
        else if (node.getLeft() != null && node.getRight() == null) {
            if (node.getParent() == null) {
                root = node.getLeft();
                root.setParent(null);
            } else {
                TreeNode<T> parent = node.getParent();
                if (parent.getLeft() == node) {
                    parent.setLeft(node.getLeft());
                } else {
                    parent.setRight(node.getLeft());
                }
                node.getLeft().setParent(parent);
            }
        }
        else if (node.getLeft() == null && node.getRight() != null) {
            if (node.getParent() == null) {
                root = node.getRight();
                root.setParent(null);
            } else {
                TreeNode<T> parent = node.getParent();
                if (parent.getLeft() == node) {
                    parent.setLeft(node.getRight());
                } else {
                    parent.setRight(node.getRight());
                }
                node.getRight().setParent(parent);
            }
        }
        else {
            TreeNode<T> successor = findMin(node.getRight());
            node.setValue(successor.getValue());
            remove(successor);
        }
    }
    // это короче для нахождения мин элемента
    public TreeNode<T> findMin(TreeNode<T> node) {
        if (node.getLeft() == null) {
            return node;
        }
        return findMin(node.getLeft());
    }

    public void preOrder(TreeNode<T> node) {
        if (node == null) return;
        System.out.print(node.getValue());
        preOrder(node.getLeft());
        preOrder(node.getRight());
    }

    public void inOrder(TreeNode<T> node) {
        if (node == null) return;
        inOrder(node.getLeft());
        System.out.print(node.getValue());
        inOrder(node.getRight());
    }

    public void postOrder(TreeNode<T> node) {
        if (node == null) return;
        postOrder(node.getLeft());
        postOrder(node.getRight());
        System.out.print(node.getValue());
    }

}