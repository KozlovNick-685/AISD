public class Stack {
   private StackNode root;
   private int size;

   public Stack() {
       this.size = 0;
   }
   
   public Stack(StackNode root) {
       this.root = root;
       this.size = 1;
   }

   public void add(StackNode node) {
       node.setPrevious(this.root);
       this.root = node;
       this.size++
   }

   public StackNode remove() {
       if (this.root == null) {
            return null;
       }
       StackNode result = this.root;
       this.root = root.getPrevious();
       this.size--;
       return result;
    }

    public StackNode get(int value) {
       StackNode current = this.root;
       while(current != null && current.getValue() != value) {
            current = current.getPrevious();
        }
     return current;
    }

    public int getSize() {
        return this.size;
    }
}