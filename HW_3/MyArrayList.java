public class MyArrayList<T>{

    private final int CAPACITY = 10;
    private Object[] array = new Object[CAPACITY];
    private int count;

    public int getCurrentSize(){return array.length;}

    public MyArrayList(){
        this.count=0;
    }

    public void add(T item) {
        if (size()==getCurrentSize()) {
            resize();
        }
        array[size()]=item;
        count++;
    }

    public void add(int index, T item) {
        if (index < 0 || index > size()) {
            throw new IndexOutOfBoundsException("Нет такого индекса");
        }
        if (size() == getCurrentSize()) {
            resize();
        }
        for (int i = size(); i > index; i--) {
            array[i] = array[i - 1];
        }
        array[index]=item;
        count++;
    }

    public T get(int index) {
        if (index<0 || index>=size()){
            throw new IndexOutOfBoundsException("Отсутствует данный индекс");
        }
        return (T) array[index];
    }

    public T remove(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException("Индекс вне диапазона");
        }
        T removedItem = (T) array[index];
        for (int i = index; i < size() - 1; i++) {
            array[i] = array[i + 1];
        }
        array[size() - 1] = null;
        count--;
        return removedItem;
    }

    public boolean remove(T item) {
        for (int i = 0; i<size(); i++){
            if (array[i].equals(item)){
                for (int j = i; j<size()-1; j++){
                    array[j]=array[j+1];
                }
                array[size()-1]=null;
                count--;
                return true;
            }
        }
        return false;
    }

    public int size() {
        return count;
    }

    public boolean isEmpty() {
        return size()==0;
    }

    public void clear() {
        for (int i = 0; i<size(); i++){
            array[i]=null;
        }
        count = 0;
    }

    public boolean contains(T item) {
        return indexOf(item) != -1;
    }

    public int indexOf(T item) {
        for (int i = 0; i<size(); i++){
            if (array[i].equals(item)){
                return i;
            }
        }
        return -1;
    }

    public void resize(){
        Object[] newArray = new Object[(int) (getCurrentSize() * 1.5)];
        for (int i = 0; i < size(); i++) {
            newArray[i] = array[i];
        }
        array = newArray;
    }
}