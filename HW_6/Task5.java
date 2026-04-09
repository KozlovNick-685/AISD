import java.util.Arrays;

public class Task5 {
    public static void main(String[] args) {
        int[] nums = {2, 5, 8, 11, 14, 17, 20, 23};
        int goal = 100;

        int answer = findClosestSum(nums, goal);

        System.out.println("Массив " + Arrays.toString(nums));
        System.out.println("Цель " + goal);
        System.out.println("Ближайшая сумма трёх чисел " + answer);
    }

    public static int findClosestSum(int[] arr, int goal) {
        int length = arr.length;
        int bestSum = arr[0] + arr[1] + arr[2];

        for (int first = 0; first < length - 2; first++) {
            int second = first + 1;
            int third = length - 1;

            while (second < third) {
                int currentSum = arr[first] + arr[second] + arr[third];

                if (currentSum == goal) {
                    return currentSum;
                }

                if (Math.abs(currentSum - goal) < Math.abs(bestSum - goal)) {
                    bestSum = currentSum;
                }

                if (currentSum < goal) {
                    second++;
                } else {
                    third--;
                }
            }
        }

        return bestSum;
    }
}
