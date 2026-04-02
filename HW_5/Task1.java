import java.util.HashSet;

public class Task1 {
    public static void main(String[] args) {
        int[] values = {3, 7, 1, 9, 4};
        int target = 10;
        HashSet<Integer> cache = new HashSet<>();

        for (int x : values) {
            int y = target - x;
            if (cache.contains(y)) {
                System.out.println("Пара: " + x + " и " + y);
                return;
            }
            cache.add(x);
        }
        System.err.println("Ничего нет");
    }
}