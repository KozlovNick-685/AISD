public class Postfix {

    public static int evaluate(String expression, int a, int b) {
        Stack stack = new Stack();
        String[] tokens = expression.split(" ");

        for (String token : tokens) {
            if (token.equals("+") || token.equals("-") || token.equals("*")) {
                StackNode operand2 = stack.remove();
                StackNode operand1 = stack.remove();

                if (operand1 == null || operand2 == null) {
                    throw new IllegalArgumentException("Недостаточно операндов для операции " + token);
                }

                int result;
                switch (token) {
                    case "+":
                        result = operand1.getValue() + operand2.getValue();
                        break;
                    case "-":
                        result = operand1.getValue() - operand2.getValue();
                        break;
                    case "*":
                        result = operand1.getValue() * operand2.getValue();
                        break;
                    default:
                        throw new IllegalArgumentException("Неизвестная операция: " + token);
                }


                stack.add(new StackNode(result));

            } else {
                int value;
                switch (token) {
                    case "A":
                        value = a;
                        break;
                    case "B":
                        value = b;
                        break;
                    default:
                        throw new IllegalArgumentException("Недопустимый операнд: " + token);
                }


                stack.add(new StackNode(value));
            }
        }


        if (stack.getSize() != 1) {
            throw new IllegalArgumentException("Некорректное выражение: в стеке осталось " +
                    stack.getSize() + " элементов");
        }

        return stack.remove().getValue();
    }

}