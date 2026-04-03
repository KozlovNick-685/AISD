import java.util.ArrayList;
import java.util.List;

public class BPlusTreeNode {

    protected boolean isLeaf;
    protected List<Integer> keys;
    protected List<BPlusTreeNode> children;
    protected BPlusTreeNode next;

    public BPlusTreeNode(boolean isLeaf) {
        this.isLeaf = isLeaf;
        this.keys = new ArrayList<>();
        this.children = new ArrayList<>();
        this.next = null;
    }
}
