package com.alinesno.infra.base.im.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {

//    @InjectMocks
//    private TaskServiceImpl taskService;
//
//    @BeforeEach
//    public void setUp() {
//        // No additional setup required as we are using @InjectMocks
//    }
//
//    @Test
//    public void testAddTask_QueueNotFull_TaskAdded() {
//        MessageTaskInfo info = new MessageTaskInfo();
//        info.setMessage("Test Message");
//        info.setTaskId("1");
//
//        taskService.addTask(info);
//
//        // Verify that the task was added to the queue
//        assertEquals(1, taskService.taskQueue.size());
//    }
//
//    @Test
//    public void testAddTask_QueueFull_TaskNotAdded() {
//        // Mock the behavior to simulate a full queue
//        doNothing().when(taskService.executor).execute(any(Runnable.class));
//        taskService.taskQueue = new LinkedList<>(); // Reset the queue
//        for (int i = 0; i < 100; i++) {
//            taskService.taskQueue.add(new MessageTaskInfo());
//        }
//
//        MessageTaskInfo info = new MessageTaskInfo();
//        info.setMessage("Test Message");
//        info.setTaskId("1");
//
//        taskService.addTask(info);
//
//        // Verify that the task was not added to the queue
//        assertEquals(100, taskService.taskQueue.size());
//    }
//
//    @Test
//    public void testProcessTask_TaskExists_TaskProcessed() {
//        MessageTaskInfo info = new MessageTaskInfo();
//        info.setMessage("Test Message");
//        info.setTaskId("1");
//        taskService.taskQueue.add(info);
//
//        taskService.processTask();
//
//        // Verify that the task was processed and removed from the queue
//        assertTrue(taskService.taskQueue.isEmpty());
//    }
//
//    @Test
//    public void testGetTaskMessage_TaskInfoProvided_ReturnsTaskList() {
//        MessageTaskInfo info = new MessageTaskInfo();
//        info.setMessage("Test Message");
//        info.setTaskId("1");
//
//        // Mock the behavior to simulate retrieving task list
//        when(taskService.getTaskMessage(any(MessageTaskInfo.class))).thenReturn(null);
//
//        List<MessageTaskInfo> result = taskService.getTaskMessage(info);
//
//        // Verify that the method returns null as it should be overridden to provide actual implementation
//        assertEquals(null, result);
//    }

}
