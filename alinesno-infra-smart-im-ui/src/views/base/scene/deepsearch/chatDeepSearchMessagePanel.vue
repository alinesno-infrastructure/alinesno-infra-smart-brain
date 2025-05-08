<template>
  <div class="deepsearch-app-container">

    <div class="deepsearch-message-item">
      <!-- 任务规划_start -->
      <div class="step-title">
        <i class="fa-solid fa-compass-drafting"></i> 问题拆分
      </div>
      <div class="task-plan-panel">

        <div class="think-panel">
          <div class="think-title">
            执行规划思考
          </div>
          <div class="think-content">
            首先，用户需要将复杂问题拆解成任务并分配成员，目标是调用save_tasks工具保存任务。需要明确任务描述和对应的成员。
            分析复杂问题，包含三个主要部分：解读论文重点、检索领域内重要学者及相关文献、整理为清晰易读的表格。
            对于“解读论文重点”，因为论文有链接https://arxiv.org/abs/1706.03762，成员searcher有读取网页或链接内容的能力，所以可以分配searcher来读取该链接内容并解读重点。
            对于“检索领域内重要学者及相关文献”，需要联网搜索公域资料，searcher具备联网搜索能力，所以这部分也分配searcher。
            对于“整理为清晰易读的表格”，需要将前面获取的信息进行整理，searcher可以完成整理工作，所以也分配searcher。
            考虑到限制任务数量最多5条，这里可以拆分为三个任务：读取并解读论文重点、检索领域内重要学者及相关文献、整理信息为表格，每个任务都分配给searcher。
            然后按照save_tasks工具的要求，将任务描述列表和对应的成员列表准备好，调用工具保存任务。
          </div>
          <div class="output-content">
            提出了一种全新的序列转换模型架构——Transformer，其完全基于注意力机制，摒弃了传统模型中使用的循环神经网络（RNN）和卷积神经网络（CNN）结构。
          </div>
        </div>

            <div class="summary-panel">
              <div class="think-title">
                总结
              </div>
              <div class="think-content">
                用户需要读取并解读指定论文的重点内容，已知论文的URL，根据工具列表，“link_reader”工具可以获取网页、pdf等链接下的标题和内容，正好满足需求，所以选择该工具，参数url_list为包含该论文URL的列表。
              </div>
              <div class="output-content">
                提出了一种全新的序列转换模型架构——Transformer，其完全基于注意力机制，摒弃了传统模型中使用的循环神经网络（RNN）和卷积神经网络（CNN）结构。
              </div>
            </div>

      </div>
      <!-- 任务规划_end -->

      <!-- 任务执行_start -->
      <div class="step-title">
        <i class="fa-solid fa-signature"></i> 执行步骤
      </div>
      <div v-for="(item, index) in 5" :key="item" class="task-item">
        <div class="task-step-title">
          <i class="fa-solid fa-arrow-right"></i> 当前步骤: <span class="task-step-name"> Step{{ index + 1 }} </span> 任务名称:
          读取并解读论文重点
        </div>
        <div>
          <div class="think-loop">
            <div class="think-panel">
              <div class="think-title">
                思考1
              </div>
              <div class="think-content">
                用户需要读取并解读指定论文的重点内容，已知论文的URL，根据工具列表，“link_reader”工具可以获取网页、pdf等链接下的标题和内容，正好满足需求，所以选择该工具，参数url_list为包含该论文URL的列表。
              </div>
              <div class="output-content">
                提出了一种全新的序列转换模型架构——Transformer，其完全基于注意力机制，摒弃了传统模型中使用的循环神经网络（RNN）和卷积神经网络（CNN）结构。
              </div>
            </div>

            <div class="tool-panel">
              <div class="tool-title">
                使用工具 | EchatTool
              </div>
              <div class="tool-content">
                <i class="fa-solid fa-screwdriver-wrench"></i>
                <span>
                  解析「URL」中，正在解析的URL为：https://arxiv.org/abs/1706.03762  <el-button type="success" text size="small" icon="Check">成功</el-button>
                </span>
              </div>
              <div class="tool-content">
                <i class="fa-solid fa-screwdriver-wrench"></i>
                <span>
                  解析「URL」中，正在解析的URL为：https://arxiv.org/abs/1706.03762  <el-button type="success" text size="small" icon="Check">成功</el-button>
                </span>
              </div>
              <div class="tool-content">
                <i class="fa-solid fa-screwdriver-wrench"></i>
                <span>
                  解析「URL」中，正在解析的URL为：https://arxiv.org/abs/1706.03762  <el-button type="success" text size="small" icon="Check">成功</el-button>
                </span>
              </div>
            </div>

            <div class="summary-panel">
              <div class="think-title">
                总结
              </div>
              <div class="think-content">
                用户需要读取并解读指定论文的重点内容，已知论文的URL，根据工具列表，“link_reader”工具可以获取网页、pdf等链接下的标题和内容，正好满足需求，所以选择该工具，参数url_list为包含该论文URL的列表。
              </div>
              <div class="output-content">
                提出了一种全新的序列转换模型架构——Transformer，其完全基于注意力机制，摒弃了传统模型中使用的循环神经网络（RNN）和卷积神经网络（CNN）结构。
              </div>
            </div>

          </div>

          <div class="think-loop">
            <div>
              <div class="think-title">
                思考2
              </div>
              <div class="think-content">
                用户需要读取并解读指定论文的重点内容，已知论文的URL，根据工具列表，“link_reader”工具可以获取网页、pdf等链接下的标题和内容，正好满足需求，所以选择该工具，参数url_list为包含该论文URL的列表。
              </div>
              <div class="output-content">
                用户需要读取并解读指定论文的重点内容，已知论文的URL，根据工具列表，“link_reader”工具可以获取网页、pdf等链接下的标题和内容，正好满足需求，所以选择该工具，参数url_list为包含该论文URL的列表。
              </div>
            </div>
            <div class="tool-panel">
              <div class="tool-title">
                使用工具 | EchatTool
              </div>
              <div class="tool-content">
                <i class="fa-solid fa-screwdriver-wrench"></i> 
                <span>
                  解析「URL」中，正在解析的URL为：https://arxiv.org/abs/1706.03762  <el-button type="info" text size="small" loading>执行中</el-button>
                </span>
              </div>
              <div class="tool-content">
                <i class="fa-solid fa-screwdriver-wrench"></i> 
                <span>
                  解析「URL」中，正在解析的URL为：https://arxiv.org/abs/1706.03762  <el-button type="success" text size="small" icon="Check">成功</el-button>
                </span>
              </div>
              <div class="tool-content">
                <i class="fa-solid fa-screwdriver-wrench"></i> 
                <span>
                  解析「URL」中，正在解析的URL为：https://arxiv.org/abs/1706.03762  <el-button type="danger" text size="small" icon="Close">失败 (点击查看异常)</el-button>
                </span>
              </div>
            </div>

            <div class="summary-panel">
              <div class="think-title">
                总结
              </div>
              <div class="think-content">
                用户需要读取并解读指定论文的重点内容，已知论文的URL，根据工具列表，“link_reader”工具可以获取网页、pdf等链接下的标题和内容，正好满足需求，所以选择该工具，参数url_list为包含该论文URL的列表。
              </div>
              <div class="output-content">
                提出了一种全新的序列转换模型架构——Transformer，其完全基于注意力机制，摒弃了传统模型中使用的循环神经网络（RNN）和卷积神经网络（CNN）结构。
              </div>
            </div>

          </div>

        </div>
      </div>
      <!-- 任务执行_end -->

      <!-- 任务结果输出_start -->
      <div class="step-title">
        <i class="fa-solid fa-paper-plane"></i> 任务结果
      </div>
      <div class="task-result">
        <!-- 内容结果输出_start -->
        <div class="task-content-panel">
          <div class="think-title">
            思考
          </div>
          <div class="think-content">
            用户需要读取并解读指定论文的重点内容，已知论文的URL，根据工具列表，“link_reader”工具可以获取网页、pdf等链接下的标题和内容，正好满足需求，所以选择该工具，参数url_list为包含该论文URL的列表。
          </div>
          <div class="output-content">
            机器学习领域
            重要学者：周志华（国内机器学习领域权威学者）。
            相关文献：
            《机器学习及其应用2007》（周志华著，2007年，清华大学出版社）：结合理论与实际案例，探讨机器学习技术的落地应用[3]。
            《机器学习》（机械工业出版社，2013年，豆瓣评分7.4）：计算机科学与人工智能领域经典教材，覆盖主流算法与理论[6]。
            《Generative Adversarial Networks》（生成式对抗网络）：提出GAN架构及训练理论，证明生成器数据分布的全局最优性与算法收敛性[1]。
            总结
            《Attention Is All You
            Need》通过Transformer模型推动了NLP领域从传统RNN/CNN向注意力机制的范式转移，其成果与计算语言学、机器学习领域的基础理论（如翁富良等学者的著作）及技术方法如周志华的机器学习教材、GAN模型）密切相关，共同构成了现代深度学习与自然语言处理的研究基石。
          </div>
        </div>
        <!-- 内容结果输出_end -->

        <!-- 文件结果输出_start -->
        <div class="task-file-panel">
          <div class="think-title">
            文件列表
          </div>
          <div class="file-list-content">
            <div v-for="(item, index) in fileList" class="file-item" :key="index">
              <div class="file-item-content">
                <div class="icon">
                  <i :class="item.icon"></i>
                </div>
                <div class="file-content">
                  <div class="file-name">
                    {{ item.name }}
                  </div>
                  <div class="file-link">
                    {{ item.downloadLink }}
                  </div>
                </div>
              </div>
              <div class="download-link">
                <el-button type="info" size="small" text @click="handleDownload(item)">
                  <i class="fa-solid fa-arrow-down"></i>
                </el-button>
              </div>
            </div>
          </div>
        </div>
        <!-- 文件结果输出_end -->

      </div>
      <!-- 任务结果输出_start -->

    </div>


  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';

const props = defineProps({
  message: {
    type: Object,
    required: true
  }
});

const emits = defineEmits(['update:message']);

const fileList = ref([
  {
    name: '员工工资表.xlsx',
    icon: 'fa-solid fa-file-excel',
    downloadLink: 'employee_salary.xlsx',
    type: 'excel'
  },
  {
    name: '项目计划书.docx',
    icon: 'fa-solid fa-file-word',
    downloadLink: 'project_plan.docx',
    type: 'word'
  },
  {
    name: '会议记录.txt',
    icon: 'fa-solid fa-file',
    downloadLink: 'meeting_notes.txt',
    type: 'txt'
  },
  {
    name: '产品宣传视频.mp4',
    icon: 'fa-solid fa-file-video',
    downloadLink: 'product_promotion.mp4',
    type: 'video'
  },
  {
    name: '培训课程音频.mp3',
    icon: 'fa-solid fa-file-audio',
    downloadLink: 'training_course.mp3',
    type: 'audio'
  }
]);

const updateMessage = () => {
  const newMessage = { ...props.message, text: '更新后的消息' };
  emits('update:message', newMessage);
};
</script>

<style lang="scss" scoped>
.deepsearch-app-container {

  .step-title {
    font-size: 15px;
    background-color: #fafafa;
    font-weight: bold;
    margin-bottom: 10px;
    padding: 7px;
    padding-left: 10px;
    border-radius: 5px;
    margin-top: 10px;
    color: #444;
  }

  .task-item {
    font-size: 14px;
    line-height: 1.5rem;
    padding-left: 10px;

    .task-step-title {
      display: flex;
      align-items: center;
      gap: 5px;
      margin-top: 10px;
      margin-bottom: 10px;

      .task-step-name {
        color: #444;
        font-weight: bold;
        background-color: #f5f5f5;
        padding: 5px;
        border-radius: 3px;
        line-height: 1rem;
      }
    }
  }

  .task-plan-panel {
    padding-left: 10px;
    display: flex;
    flex-direction: column;
    gap: 5px;
  }

  .think-content {
    color: #aaa;
    border-left: 2px solid #ddd;
    padding-left: 10px;
    font-size: 14px;
    line-height: 1.3rem;
    border-radius: 5px;
    margin-top: 5px;
    margin-bottom: 5px;
    margin-left:10px;
  }

  .output-content {
    color: #444;
    margin-top: 5px;
    font-size: 14px;
    line-height: 1.3rem;
    margin-bottom: 5px;
    margin-left:10px;
  }

  .think-title {
    font-size: 14px;
    background-color: #fafafa;
    color: #444;
    padding: 5px;
    border-radius: 5px;
  }

  .task-result {
    padding-left: 10px;
  }

  .tool-panel {
    .tool-title {
      background-color: #fafafa;
      color: #444;
      padding: 5px;
      border-radius: 5px;
    }

    .tool-content {
      margin: 10px;
      padding: 5px;
      background: #fafafa;
      border-radius: 7px;
      font-size: 14px;
      color: #444;
    }
  }

  .summary-panel {
    display: flex;
    gap: 8px;
    flex-direction: column;
  }

  .think-panel {
    display: flex;
    gap: 8px;
    flex-direction: column;
  }

  .task-result{

    .task-content-panel {
        display: flex;
        gap: 10px;
        flex-direction: column;
        margin-bottom: 10px;
    }

    .task-file-panel {
        margin-top: 10px;
        margin-bottom: 20px;
    }

    .file-list-content{

      display: flex;
      margin-top: 10px;
      flex-wrap: wrap;
      gap: 10px;

      .icon {
        font-size: 20px;
        width: 30px;
        display: flex;
        align-items: center;
        justify-content: center;
      }

      .file-content {
        font-size: 14px;
      }

      .file-item-content {
        display: flex;
        flex-direction: row;
        align-content: center;
        align-items: center;
      }

      .file-item {
        width: calc(33% - 10px);
        float: left;
        color: #555;
        background: #fafafa;
        padding: 10px;
        border-radius: 8px;
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        align-items: center;

        &:hover {
          .download-link {
            display: block;
          }
        }

        .file-link {
          font-size: 13px;
          color:#777;
        }

        .download-link {
          font-size: 16px;
          color: #777;
          // margin-right: 10px;
          display: none;
          cursor: pointer;
        }
      }

    }
  }

}
</style>