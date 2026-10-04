package ds.stack;

import org.junit.jupiter.api.Test;

import java.util.EmptyStackException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MyStackTest {

    @Test
    void 마지막에_넣은_것이_먼저_나온다() {
        MyStack<String> stack = new MyStack<>();
        stack.push("a");
        stack.push("b");
        stack.push("c");

        assertEquals("c", stack.peek());   // 보기만
        assertEquals(3, stack.size());

        assertEquals("c", stack.pop());
        assertEquals("b", stack.pop());
        assertEquals("a", stack.pop());
        assertTrue(stack.isEmpty());
    }

    @Test
    void 빈_스택에서_pop하면_예외() {
        MyStack<Integer> stack = new MyStack<>();
        assertThrows(EmptyStackException.class, stack::pop);
        assertThrows(EmptyStackException.class, stack::peek);
    }

    @Test
    void 많이_넣어도_자동_확장() {
        MyStack<Integer> stack = new MyStack<>();
        for (int i = 0; i < 1000; i++) stack.push(i);

        assertEquals(1000, stack.size());
        assertEquals(999, stack.pop());
    }

    @Test
    void 괄호_짝_검사() {
        assertTrue(MyStack.isBalanced("()"));
        assertTrue(MyStack.isBalanced("([]{})"));
        assertTrue(MyStack.isBalanced("{ a[b(c)d]e }"));
        assertTrue(MyStack.isBalanced(""));

        assertFalse(MyStack.isBalanced("("));        // 안 닫힘
        assertFalse(MyStack.isBalanced(")"));        // 열지도 않고 닫음
        assertFalse(MyStack.isBalanced("([)]"));     // 교차
        assertFalse(MyStack.isBalanced("(()"));      // 하나 덜 닫힘
    }
}
