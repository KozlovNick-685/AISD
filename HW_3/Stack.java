public class Stack<T> {
    private final int CAPACITY = 10;
    private Object[] values = new Object[CAPACITY];
    private int index=0;

    public void push(T value){
        if (index==CAPACITY){
            throw new StackOverflowError("Stack is full");
        }
        values[index]=value;
        index++;
    }

    public T pop(){
        if (index==0){
            throw new IllegalStateException("Stack is empty");
        }
        index--;
        return (T) values[index];
    }

    public T peek(){
        if (index==0){
            throw new IllegalStateException("Stack is empty");
        }
        return (T) values[index-1];
    }

    public int getIndex(){return index;}
    public boolean isEmpty(){return index==0;}
}