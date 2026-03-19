public class Task2 {
    public static void main(String[] args) {
        int[] digits = {0, 1, 2, 3, 5, 6, 4, 8, 9};
        int sum = -1;
        for (int i: digits) {
            if (i == 0) {
                sum = 0;
            }
            if (sum != -1) {
                sum += i;
            }
        }
        if (sum == -1) {
            System.out.println(0);
        } else {
            System.out.println((digits.length + 1) * digits.length / 2 - sum);
        }
    }
}