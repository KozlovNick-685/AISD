import java.util.HashSet;
import java.util.Set;

public class Task6 {
    public static void main(String[] args) {
        int[] arr1 = {1, 2, 3, 4, 5};
        int[] arr2 = {4, 5, 6, 7, 8};
        int[] arr3 = {9, 10, 4, 11, 12};

        Integer result = together(arr1, arr2, arr3);

        if (result != null) {
            System.out.println("Первый общий элемент: " + result);
        } else {
            System.out.println("Общих элементов не найдено");
        }
    }
    public static Integer together(int[] a, int[] b, int[] c) {
        Set<Integer> firstSet = new HashSet<>();
        Set<Integer> secondSet = new HashSet<>();

        for (int x : a) firstSet.add(x);
        for (int x : b) secondSet.add(x);

        for (int x : c) {
            if (firstSet.contains(x) && secondSet.contains(x))
                return x;
        }
        return null;
    }
}
