package Utility;

import java.util.Stack;

public class Parentheses {

    private static final char L_PAREN = '(';
    private static final char R_PAREN = ')';
    private static final char L_BRACE = '{';
    private static final char R_BRACE = '}';
    private static final char L_BRACKET = '[';
    private static final char R_BRACKET = ']';

    public static boolean isBalanced(String s) {
        Stack<Character> stack = new Stack<Character>();
        for (int i = 0; i < s.length(); i++) {

            switch (s.charAt(i)) {
                case L_PAREN:
                    stack.push(L_PAREN);
                    break;
                // ignore all other characters
                case L_BRACE:
                    stack.push(L_BRACE);
                    break;
                case L_BRACKET:
                    stack.push(L_BRACKET);
                    break;
                case R_PAREN:
                    if (stack.isEmpty()) {
                        return false;
                    }
                    if (stack.pop() != L_PAREN) {
                        return false;
                    }
                    break;
                case R_BRACE:
                    if (stack.isEmpty()) {
                        return false;
                    }
                    if (stack.pop() != L_BRACE) {
                        return false;
                    }
                    break;
                case R_BRACKET:
                    if (stack.isEmpty()) {
                        return false;
                    }
                    if (stack.pop() != L_BRACKET) {
                        return false;
                    }
                    break;
                default:
                    break;
            }

        }
        return stack.isEmpty();
    }
}
