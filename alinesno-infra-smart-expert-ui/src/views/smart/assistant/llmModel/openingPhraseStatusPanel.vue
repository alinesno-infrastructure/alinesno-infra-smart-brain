<template>
    <div>
        <div>
            <el-input size="large" v-model="newQuestion" placeholder="输入新的开场白问题" @keyup.enter="addQuestion">
                <template #append>
                    <el-button type="primary" size="large" @click="addQuestion">添加问题</el-button>
                </template>
            </el-input>
            <div v-for="(question, index) in questions" :key="question.id" draggable="true" class="draggable-item"
                @dragstart="dragStart(index)" @dragover.prevent @drop="drop(index)">
                <div class="question-item">
                    <el-input v-model="question.text" size="large" class="editable-input" />
                    <el-button type="danger" size="default" text @click="deleteQuestion(index)">删除</el-button>
                </div>
            </div>
            <div v-if="questions.length === 0">
                <el-empty description="请添加开场白问题" image-size="80" />
            </div>
        </div>
        <div style="margin: 20px 0px;text-align: right;">
            <el-button type="primary" size="large" @click="onConfirm">确认</el-button>
        </div>
    </div>
</template>

<script setup name="Index">
import { ref } from 'vue';
import { ElMessage, ElInput, ElButton } from 'element-plus';

const emit = defineEmits(['handleOpeningPhraseStatusPanelClose'])

// 存储开场白问题的数组，每个元素为包含 id 和 text 的对象
const questions = ref([]);
// 新输入的问题
const newQuestion = ref('');
// 记录拖动的索引
const draggedIndex = ref(null);

// 生成唯一 ID 的函数
const generateId = () => Date.now() + Math.random().toString(36).substr(2, 5);

// 添加问题的方法
const addQuestion = () => {
    if (newQuestion.value.trim()) {
        questions.value.push({
            id: generateId(),
            text: newQuestion.value.trim()
        });
        newQuestion.value = '';
    }else {
        ElMessage.warning('请输入开场白问题');
    }
};

// 删除问题的方法
const deleteQuestion = (index) => {
    questions.value.splice(index, 1);
};

// 拖动开始时记录索引
const dragStart = (index) => {
    draggedIndex.value = index;
};

// 拖动结束时处理顺序变化
const drop = (index) => {
    if (draggedIndex.value !== null) {
        const draggedItem = questions.value[draggedIndex.value];
        questions.value.splice(draggedIndex.value, 1);
        questions.value.splice(index, 0, draggedItem);
        draggedIndex.value = null;
    }
};

// 获取开场白数组的方法
const getOpeningQuestions = () => {
    return questions.value.map(question => question.text);
};

// 设置开场白的方法
const setOpeningQuestions = (newQuestions) => {
    if (Array.isArray(newQuestions)) {
        questions.value = newQuestions.map((text, index) => ({
            id: generateId(),
            text
        }));
    }
};

// 确认事件
const onConfirm = () => {
    const finalQuestions = getOpeningQuestions();
    console.log('最终确认的开场白问题:', finalQuestions);
    emit('handleOpeningPhraseStatusPanelClose', finalQuestions);
    ElMessage.success('提交成功');
};

// 暴露方法给父组件
defineExpose({
    getOpeningQuestions,
    setOpeningQuestions
});
</script>

<style lang="scss" scoped>
.el-input {
    margin-bottom: 10px;
}

[draggable] {
    cursor: move;
    margin-bottom: 8px;
}

.question-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
    padding: 10px;
    background: #fafafa;
    border-radius: 5px;
}

.editable-input {
    flex: 1;
    margin-right: 10px;
    margin-bottom: 0px !important;
}
</style>