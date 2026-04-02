import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Task4 {
    public static void main(String[] args) {
        int[] arr = new int[]{1, 2, 1, 5, 3, 2};
        func(arr);

    }
    public static void func(int[] arr) {
        List<String> list = new ArrayList<>();
        for (int n : arr) {
            list.add("" + n);
        }
        Collections.sort(list, new Comparator<String>() {
            public int compare(String x, String y) {
                return (y + x).compareTo(x + y);
            }
        });
        if (list.get(0).equals("0")) {
            System.out.println("0");
            return;
        }
        for (String s : list) {
            System.out.print(s);
        }
    }
}