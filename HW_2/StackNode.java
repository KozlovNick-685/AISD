public class StackNode {
    private StackNode previous;
    private final int value;

    public StackNode(int value) {
        this.value = value;
    }

    public StackNode(int value, StackNode previous) {
        this.value = value;
        this.previous = previous;
    }

    public int getValue() {
        return this.value;
    }

    public StackNode getPrevious() {
        return this.previous;
    }

    public void setPrevious(StackNode previous) {
         this.previous = previous;
    }
}