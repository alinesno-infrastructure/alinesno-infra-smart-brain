//package com.alinesno.infra.smart.assistant.role.tools.graalvm;
//
//import dev.langchain4j.code.CodeExecutionEngine;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class GraalVmJavaScriptExecutionEngineTest {
//
//    private final CodeExecutionEngine engine = new GraalVmJavaScriptExecutionEngine();
//
//    /**
//     * 测试执行JavaScript代码
//     */
//    @Test
//    void should_execute_code() {
//
//        String code = """
//                function fibonacci(n) {
//                    if (n <= 1) return n;
//                    return fibonacci(n - 1) + fibonacci(n - 2);
//                }
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
//                let a = 4;
//                let b = 5;
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
//                function multiply(a, b) {
//                    return a * b;
//                }
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
//                let num = 10;
//                if (num > 5) {
//                    'Greater than 5'
//                } else {
//                    'Less than or equal to 5'
//                }
//                """;
//        String result = engine.execute(code);
//        assertEquals("Greater than 5", result);
//    }
//
//    // 测试数组操作
//    @Test
//    public void testArrayManipulation() {
//        String code = """
//                let arr = [1, 2, 3];
//                arr.reduce((sum, num) -> sum + num, 0)
//                """;
//        String result = engine.execute(code);
//        assertEquals("6", result);
//    }
//
//    // 测试异常处理
//    @Test
//    public void testExceptionHandling() {
//        String code = """
//                try {
//                    throw new Error('Test error');
//                } catch (e) {
//                    e.message
//                }
//                """;
//        String result = engine.execute(code);
//        assertEquals("Test error", result);
//    }
//
//    // 测试返回对象
//    @Test
//    public void testReturnObject() {
//        String code = """
//                {name: 'John', age: 30}
//                """;
//        String result = engine.execute(code);
//        assertNotNull(result);
//        assertTrue(result.contains("name") && result.contains("John") && result.contains("age") && result.contains("30"));
//    }
//
//    // 测试 JSON 解析
//    @Test
//    public void testJsonParsing() {
//        String code = """
//                let jsonStr = '{"city": "New York", "population": 8500000}';
//                let jsonObj = JSON.parse(jsonStr);
//                jsonObj.city
//                """;
//        String result = engine.execute(code);
//        assertEquals("New York", result);
//    }
//
//    // 测试获取 JSON 对象字段
//    @Test
//    public void testGettingJsonField() {
//        String code = """
//                let person = {
//                    "name": "Alice",
//                    "job": {
//                        "title": "Engineer",
//                        "company": "TechCorp"
//                    }
//                };
//                person.job.company
//                """;
//        String result = engine.execute(code);
//        assertEquals("TechCorp", result);
//    }
//}