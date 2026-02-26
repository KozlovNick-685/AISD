import java.util.Scanner;

public class task1 {
    public static void main(String[] args) {
        Scanner myScanner = new Scanner(System.in);
        double a = myScanner.nextDouble();
        double b = myScanner.nextDouble();
        double c = result(a, b);
        System.out.println(c);

    }

    public static double result(double a, double b) {
        if (a == 0.0 || b == 0.0) {
            return 0;
        }
        if (b != 0) {
            if (a < b) {
                return nod(b, a);
            } else {
                return nod(a, b);
            }
        } else {
            return nod(b, a % b);
        }
    }

    public static double nod(double a, double b) {
        for (int i = (int) (a + b); i > 0; i--) {
            if (a % i == 0 && b % i == 0) {
                return i;
            }
        }
        return 1;
    }


}