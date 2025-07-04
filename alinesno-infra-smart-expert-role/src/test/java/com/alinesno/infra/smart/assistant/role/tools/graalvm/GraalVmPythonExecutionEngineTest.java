//package com.alinesno.infra.smart.assistant.role.tools.graalvm;
//
//import dev.langchain4j.code.CodeExecutionEngine;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class GraalVmPythonExecutionEngineTest {
//
//    private final CodeExecutionEngine engine = new GraalVmPythonExecutionEngine();
//
//    /**
//     *
//     */
//    @Test
//    void should_execute_code() {
//
//        String code = """
//                def fibonacci(n):
//                    if n <= 1:
//                        return n
//                    else:
//                        return fibonacci(n-1) + fibonacci(n-2)
//
//                fibonacci(10)
//                """;
//
//        String result = engine.execute(code);
//
//        assertEquals(result , "55");
//    }
//
//    // 测试简单的加法运算
//    @Test
//    public void testSimpleAddition() {
//        String code = """
//                2 + 3
//                """;
//        String result = engine.execute(code);
//        assertEquals("5", result);
//    }
//
//    // 测试变量赋值和使用
//    @Test
//    public void testVariableAssignment() {
//        String code = """
//                a = 4
//                b = 5
//                a + b
//                """;
//        String result = engine.execute(code);
//        assertEquals("9", result);
//    }
//
//    // 测试函数调用
//    @Test
//    public void testFunctionCall() {
//        String code = """
//                def multiply(a, b):
//                    return a * b
//
//                multiply(3, 4)
//                """;
//        String result = engine.execute(code);
//        assertEquals("12", result);
//    }
//
//    // 测试条件语句
//    @Test
//    public void testConditionalStatement() {
//        String code = """
//                def check_num():
//                    num = 10
//                    if num > 5:
//                        return 'Greater than 5'
//                    else:
//                        return 'Less than or equal to 5'
//                check_num()
//                """;
//        String result = engine.execute(code);
//        assertEquals("Greater than 5", result);
//    }
//
//    // 测试列表操作
//    @Test
//    public void testListManipulation() {
//        String code = """
//                my_list = [1, 2, 3]
//                sum(my_list)
//                """;
//        String result = engine.execute(code);
//        assertEquals("6", result);
//    }
//
//    // 测试异常处理
//    @Test
//    public void testExceptionHandling() {
//        String code = """
//                def handle_exception():
//                    try:
//                        raise ValueError('Test error')
//                    except ValueError as e:
//                        return str(e)
//                handle_exception()
//                """;
//        String result = engine.execute(code);
//        assertEquals("Test error", result);
//    }
//
//    // 测试字典操作
//    @Test
//    public void testDictionaryManipulation() {
//        String code = """
//                my_dict = {'name': 'John', 'age': 30}
//                my_dict['name']
//                """;
//        String result = engine.execute(code);
//        assertEquals("John", result);
//    }
//}