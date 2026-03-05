public class Main {
    public static void main(String[] args) {

        String x = "A B *";
        System.out.println("A B * = " + Postfix.evaluate(x, 5, 3)); // 13


        String y = "A B * A -";
        System.out.println("A B * A - = " + Postfix.evaluate(y, 5, 3)); // 12
    }
}