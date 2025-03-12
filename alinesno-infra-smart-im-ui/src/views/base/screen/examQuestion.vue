<template>
  <div class="exam-container">
    <el-row :gutter="20">
      <!-- 左边：题目列表或题目详情 -->
      <el-col :span="24" class="question-section-col">
        <el-scrollbar height="calc(100vh - 170px)">

        <div v-if="isEditMode">
          <!-- 编辑模式：题目管理 -->
          <div v-for="(question, index) in questions" :key="index" class="question-item">
            <el-card class="box-card" shadow="never">
              <template #header>
                <div class="card-header">
                  <span>题目 {{ index + 1 }}</span>
                  <el-button style="float: right; padding: 3px 0" type="text" @click="editQuestion(index)">编辑</el-button>
                  <el-button style="float: right; padding: 3px 0; margin-right: 10px" type="text" @click="deleteQuestion(index)">删除</el-button>
                </div>
              </template>
              <p style="font-size:14px;">{{ index + 1 }} : {{ question.content }}</p>
              <div v-if="question.questionType === 'SINGLE_CHOICE' || question.questionType === 'MULTIPLE_CHOICE'">
                <ul>
                  <li v-for="(option, i) in question.options" :key="i">{{ option }}</li>
                </ul>
              </div>
            </el-card>
          </div>
          <el-button type="primary" size="large" @click="addQuestion">添加题目</el-button>
          <el-button type="success" size="large" @click="saveQuestions">保存题目</el-button>
        </div>

        <div v-else>
          <!-- 考试模式：题目详情 -->
          <div v-for="(question, index) in questions" :key="index" class="question-detail">
            <!-- <h4>题目 {{ index + 1 }}</h4> -->
            <p style="font-size:14px;">{{ index + 1 }} : {{ question.content }}</p>
            <div style="font-size:14px;" v-if="question.questionType === 'SINGLE_CHOICE' || question.questionType === 'MULTIPLE_CHOICE'">
              <el-radio-group v-model="answers[index]" v-if="question.questionType === 'SINGLE_CHOICE'">
                <el-radio v-for="(option, i) in question.options" :key="i" :label="option">{{ option }}</el-radio>
              </el-radio-group>
              <el-checkbox-group v-model="answers[index]" v-else>
                <el-checkbox v-for="(option, i) in question.options" :key="i" :label="option">{{ option }}</el-checkbox>
              </el-checkbox-group>
            </div>
            <el-input v-else-if="question.questionType === 'FILL_IN_BLANK'" v-model="answers[index]" placeholder="请输入答案"></el-input>
            <el-input v-else-if="question.questionType === 'SHORT_ANSWER'" type="textarea" v-model="answers[index]" placeholder="请输入答案"></el-input>
            <el-input v-else-if="question.questionType === 'CODING'" type="textarea" v-model="answers[index]" placeholder="请在此编写代码"></el-input>
            <p v-else-if="question.questionType === 'TRUE_FALSE'">
              <el-radio-group v-model="answers[index]">
                <el-radio :label="true">正确</el-radio>
                <el-radio :label="false">错误</el-radio>
              </el-radio-group>
            </p>
          </div>
          <div v-if="isSubmitted || timeLeft <= 0" class="result-section">
            <p>考试已结束</p>
          </div>
        </div>
        </el-scrollbar>

        <!-- 编辑题目对话框 -->
        <el-dialog :title="`编辑题目 ${editingIndex + 1}`" v-model="dialogVisible" width="50%">
          <el-form :model="editingQuestion" label-width="120px">
            <el-form-item label="题目内容">
              <el-input v-model="editingQuestion.content"></el-input>
            </el-form-item>
            <el-form-item label="题目类型">
              <el-select v-model="editingQuestion.questionType" placeholder="请选择题目类型">
                <el-option label="单选题" value="SINGLE_CHOICE"></el-option>
                <el-option label="多选题" value="MULTIPLE_CHOICE"></el-option>
                <el-option label="判断题" value="TRUE_FALSE"></el-option>
                <el-option label="填空题" value="FILL_IN_BLANK"></el-option>
                <el-option label="简答题" value="SHORT_ANSWER"></el-option>
                <el-option label="编程题" value="CODING"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item v-if="['SINGLE_CHOICE', 'MULTIPLE_CHOICE'].includes(editingQuestion.questionType)" label="选项">
              <el-input v-for="(option, idx) in editingQuestion.options" :key="idx" v-model="editingQuestion.options[idx]" placeholder="选项" style="margin-bottom: 10px;"></el-input>
              <el-button type="text" @click="addOption">添加选项</el-button>
            </el-form-item>
          </el-form>
          <template #footer>
            <span class="dialog-footer">
              <el-button @click="dialogVisible = false">取 消</el-button>
              <el-button type="primary" @click="saveEdit">保 存</el-button>
            </span>
          </template>
        </el-dialog>
      </el-col>

      <!-- 右边：考试信息及倒计时 -->
      <!-- 
      <el-col :span="6" class="exam-info-col">
        <div class="exam-info">
          <h3>Java面试考试</h3>
          <div style="font-size:14px">
            <p>考试描述：本次考试旨在评估您的Java编程技能。</p>
            <p>考试目标：通过解答一系列题目来展示您对Java的理解和应用能力。</p>
            <p>当前模式：{{ isEditMode ? '编辑模式' : '考试模式' }}</p>
          </div>
          <p>倒计时：{{ formatTime(timeLeft) }}</p>
          <el-button type="primary" size="large" @click="submitExam" :disabled="isSubmitted || timeLeft <= 0">提交试卷</el-button> <br/>
        </div>
      </el-col> 
      -->

    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';

const props = defineProps({
  examDuration: {
    type: Number,
    default: 60 * 60 // 默认一小时
  }
});

// 考试题目数据（示例）
const questions = ref([
  {
    content: '单选题：南宁市作为广西壮族自治区首府，其简称是什么？这不仅是对城市名称的缩写，也是理解当地文化和地理特征的重要标识。',
    questionType: 'SINGLE_CHOICE',
    options: ['桂', '邕', '南', '宁']
  },
  {
    content: '多选题：在悠久的历史长河中，南宁留下了众多具有深远意义的文化遗址，请问以下哪些是南宁历史上著名的文化遗址？这些遗址不仅承载着南宁的历史记忆，也反映了古代南宁人民的生活方式和社会风貌。',
    questionType: 'MULTIPLE_CHOICE',
    options: ['大明山', '青秀山', '三街两巷', '昆仑关']
  },
  {
    content: '判断题：自古以来，南宁就是广西的政治中心，这一点是否正确？了解南宁在不同历史时期的行政地位，有助于我们更好地认识这座城市的变迁和发展历程。',
    questionType: 'TRUE_FALSE'
  },
  {
    content: '填空题：南宁市因其独特的地理位置和丰富的自然景观而获得了许多别称，其中最著名的一个是______。这个别称体现了南宁的城市特色和自然环境，也成为了市民们引以为豪的文化符号。',
    questionType: 'FILL_IN_BLANK'
  },
  {
    content: '简答题：请简述南宁在抗日战争中的作用。作为一个重要的战略要地，南宁在中国抗战史上扮演了怎样的角色？它如何支持全国的抗日斗争，并且对抗战胜利做出了哪些贡献？',
    questionType: 'SHORT_ANSWER'
  },
  {
    content: '单选题：南宁市成为广西壮族自治区首府的具体年份是在哪一年？这一变化标志着南宁在自治区政治、经济和社会发展中的核心地位的确立，对南宁的发展产生了深远影响。',
    questionType: 'SINGLE_CHOICE',
    options: ['1949年', '1958年', '1978年', '1989年']
  },
  {
    content: '多选题：南宁拥有丰富多样的非物质文化遗产，下列哪些选项属于南宁的非物质文化遗产？这些遗产不仅展示了南宁独特的民俗风情，还传承了古老的传统技艺和文化价值。',
    questionType: 'MULTIPLE_CHOICE',
    options: ['邕剧', '壮族歌圩', '横县茉莉花茶制作技艺', '宾阳炮龙节']
  },
  {
    content: '判断题：南宁作为古代海上丝绸之路的重要节点城市，这一点是否正确？通过这条古老的贸易路线，南宁与外界进行了广泛的交流，促进了经济和文化的繁荣发展。',
    questionType: 'TRUE_FALSE'
  },
  {
    content: '填空题：南宁有一个传统节日，它不仅是当地人庆祝丰收、祈求平安的重要时刻，也是展示民族文化的重要平台，这个节日的名字是______。每年这个时候，南宁的大街小巷都充满了欢乐和喜庆的气氛。',
    questionType: 'FILL_IN_BLANK'
  },
  {
    content: '简答题：请解释一下南宁为什么被称为“绿城”。这个称号背后有着怎样的故事和背景？从绿化建设到环境保护，南宁是如何一步步成为一座绿色宜居之城的？',
    questionType: 'SHORT_ANSWER'
  },
  {
    content: '单选题：哪一年南宁市举办了首届中国-东盟博览会？这一盛会不仅提升了南宁的国际影响力，也为促进区域合作和经济发展提供了重要平台。',
    questionType: 'SINGLE_CHOICE',
    options: ['2003年', '2004年', '2005年', '2006年']
  },
  {
    content: '多选题：南宁的美食文化丰富多彩，下面列出了一些广受欢迎的小吃，请问哪些是南宁的著名小吃？这些小吃不仅满足了人们的味蕾，也成为了一种文化传承，深受游客和市民的喜爱。',
    questionType: 'MULTIPLE_CHOICE',
    options: ['老友粉', '酸嘢', '五色糯米饭', '柠檬鸭']
  },
  {
    content: '判断题：南宁地铁的建设始于20世纪末，这一说法是否正确？了解南宁地铁建设的时间表，可以让我们看到这座城市交通发展的历程以及现代化进程的步伐。',
    questionType: 'TRUE_FALSE'
  },
  {
    content: '填空题：南宁的市花是______。这种花象征着南宁人民的美好愿望和积极向上的精神面貌，同时也是南宁城市形象的一个重要标志。',
    questionType: 'FILL_IN_BLANK'
  },
  {
    content: '简答题：请描述南宁的城市发展历程。从一个边陲小镇逐渐成长为现代化大都市，南宁经历了怎样的转变？在这个过程中，有哪些关键事件和决策推动了城市的发展？',
    questionType: 'SHORT_ANSWER'
  },
  {
    content: '单选题：在南宁的历史上，哪一个景点被誉为“天下第一关”？这个景点不仅是南宁的重要地标，也是研究中国古代军事防御体系的重要实物资料。',
    questionType: 'SINGLE_CHOICE',
    options: ['镇南关', '友谊关', '昆仑关', '德天瀑布']
  },
  {
    content: '多选题：随着城市的发展，南宁涌现出了一批现代化地标建筑，下列哪些是南宁的现代化地标建筑？这些标志性建筑不仅展现了南宁的城市风貌，也反映了现代建筑艺术和技术的进步。',
    questionType: 'MULTIPLE_CHOICE',
    options: ['南宁国际会展中心', '广西文化艺术中心', '南宁大桥', '万象城']
  },
  {
    content: '判断题：广西壮族自治区成立的地方是南宁，这一点是否正确？了解自治区成立的历史背景和地点，可以帮助我们更好地理解南宁在自治区发展中的重要作用。',
    questionType: 'TRUE_FALSE'
  },
  {
    content: '填空题：南宁的母亲河是______江。这条河流不仅是南宁的生命之源，也见证了这座城市的历史变迁，孕育了丰富的水乡文化。',
    questionType: 'FILL_IN_BLANK'
  },
  {
    content: '简答题：请介绍南宁的气候特点及其对当地生活的影响。南宁地处亚热带季风气候区，其温暖湿润的气候条件对农业生产、日常生活和城市规划等方面都有哪些具体影响？',
    questionType: 'SHORT_ANSWER'
  },
  {
    content: '编程题：编写一个函数，该函数接收两个参数，一个是南宁的气温列表（以摄氏度为单位），另一个是天气类型（如晴天、雨天等）。函数应返回平均温度，并根据天气类型输出不同的提示信息。例如，如果天气是晴天且平均温度大于30℃，则输出“今天天气很好，但请注意防暑降温”。此题旨在考察学生的编程能力和逻辑思维，同时也希望学生能够结合南宁的气候特点进行思考。',
    questionType: 'CODING'
  }
]);

// 用户答案
const answers = ref([]);

// 是否提交试卷
const isSubmitted = ref(false);

// 剩余时间
const timeLeft = ref(props.examDuration);

// 计时器
let timerId;

onMounted(() => {
  startTimer();
});

const startTimer = () => {
  timerId = setInterval(() => {
    if (timeLeft.value > 0 && !isEditMode.value) {
      timeLeft.value--;
    } else {
      clearInterval(timerId);
      submitExam();
    }
  }, 1000);
};

const submitExam = () => {
  clearInterval(timerId);
  isSubmitted.value = true;
  ElMessage.success('试卷已成功提交！');
  // 提交试卷逻辑（例如发送到服务器）
};

const formatTime = computed(() => {
  return (seconds) => {
    const h = Math.floor(seconds / 3600);
    const m = Math.floor((seconds % 3600) / 60);
    const s = seconds % 60;
    return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`;
  };
});

// 模式切换相关逻辑
const isEditMode = ref(false);

const toggleEditMode = () => {
  isEditMode.value = !isEditMode.value;
  if (!isEditMode.value) {
    startTimer();
  } else {
    clearInterval(timerId);
  }
};

// 题目管理相关逻辑
const addQuestion = () => {
  questions.value.push({
    content: '',
    questionType: 'SINGLE_CHOICE',
    options: []
  });
};

const deleteQuestion = (index) => {
  questions.value.splice(index, 1);
};

const editQuestion = (index) => {
  editingIndex.value = index;
  editingQuestion.value = { ...questions.value[index] };
  dialogVisible.value = true;
};

const saveEdit = () => {
  questions.value[editingIndex.value] = { ...editingQuestion.value };
  dialogVisible.value = false;
  ElMessage.success('题目已成功更新！');
};

const addOption = () => {
  editingQuestion.value.options.push('');
};

const dialogVisible = ref(false);
const editingIndex = ref(-1);
const editingQuestion = ref({});

// 保存题目到服务端
const saveQuestions = async () => {
  try {
    // 模拟API请求
    await new Promise(resolve => setTimeout(resolve, 1000));
    ElMessage.success('题目已成功保存到服务端！');
  } catch (error) {
    ElMessage.error('保存失败，请稍后再试。');
  }
};
</script>

<style lang="scss" scoped>
.exam-container {
  // max-width: 1200px;
  margin: auto;
  padding: 0px;
}

.question-section-col {
  .question-item {
    margin-bottom: 20px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .el-button--text {
    padding: 0;
  }

  .question-detail {
    background-color: #fff;
    padding: 20px;
    padding-left: 0px;
    border-radius: 4px;
    margin-bottom: 0px;
    text-align: left;

    h4 {
      margin-top: 0;
    }
  }

  .result-section {
    text-align: center;
    margin-top: 20px;
  }
}

.exam-info-col {
  .exam-info {
    background-color: #f9f9f9;
    padding: 10px;
    border-radius: 2px;
    text-align: left;
    height: 100%;
  }

  .timer {
    font-size: 1.5em;
    font-weight: bold;
    margin-top: 20px;
    text-align: right;
  }
}
</style>