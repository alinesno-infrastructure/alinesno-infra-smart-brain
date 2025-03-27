<template>
  <!-- 创建多个企业场景 -->
  <el-dialog v-model="centerDialogVisible" title="创建场景" width="1200" :append-to-body="true" align-center>
    <div>
      <div style="margin-bottom:10px">
        请选择合适的场景，并创建场景。
      </div>
      <div :size="large" style="flex-wrap: wrap;align-items: center;gap: 10px;width: 100%;display: flex;justify-content: flex-start;">
        <div  v-for="item in sceneList" :key="item.id" class="box-card" style="width: calc(20% - 10px);border-radius: 10px;" shadow="never">
          <div style="padding: 5px;background: rgb(33 36 36);border-radius: 5px;gap:7px; text-align: left;display: flex;flex-direction: column;flex-wrap: nowrap;justify-content: center;align-items: flex-start;cursor: pointer;">

            <div class="screen-banner">
              <img :src="'http://data.linesno.com/icons/screen/'+item.id+'.webp'" style="width:100%;border-radius: 5px;;" />
            </div>
            <div class="screen-title" style="font-size:15px;font-weight: bold;">
              <span>{{ item.sceneName }}</span>
            </div>
            <div class="screen-description" style="height:50px;">
              {{ item.sceneDescription }}
            </div>
            <!-- <div class="screen-tags">
              <el-button type="primary" text bg size="small">报表</el-button>
              <el-button type="primary" text bg size="small">办公</el-button>
            </div> -->
            <div class="screen-info" style="
    font-size: 12px;
    float: right;
    width: 100%;
    display: flex;
    justify-content: flex-end;
    padding-right:10px;
    margin-bottom: 10px;">
              <!-- <span>
                使用次数1200 
              </span> -->
            </div>
          </div>
        </div>
      </div>
      <div class="dialog-footer">
        <el-button type="primary" size="large" bg text @click="submitScreenForm" style="float:right;margin-top:20px" icon="Link">创建场景</el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>

const centerDialogVisible = ref(false);
const router = useRouter();
const large = 'large';

const sceneList = [
  {
    id: 1,
    sceneName: '长文本生成',
    englishName: 'LongText',
    sceneDescription: '主要用于合同、法律、招投标书编写等长文本场景',
    integrationProgress: '已集成',
    isMature: '是',
    bannerLink: ''
  },
  {
    id: 2,
    sceneName: '领导者场景',
    englishName: 'LeaderModel',
    sceneDescription: '主要用于管理者调动多个智能体去完成一个工作的场景，针对于这个场景进行结果的收集和进一步反馈',
    integrationProgress: '已集成',
    isMature: '是',
    bannerLink: ''
  },
  {
    id: 3,
    sceneName: '企业客服场景',
    englishName: 'CustomerService',
    sceneDescription: '一键导入企业官网或者上传知识库，自动生成企业客服角色【待定】',
    integrationProgress: '待定',
    isMature: '否',
    bannerLink: ''
  },
  {
    id: 4,
    sceneName: '文档审核场景',
    englishName: 'DocumentReview',
    sceneDescription: '上传一份文档进行文档审核和修改建议，可以配置多个审核的Agent角色',
    integrationProgress: '已集成',
    isMature: '是',
    bannerLink: ''
  },
  {
    id: 5,
    sceneName: '文档阅读场景',
    englishName: 'DocumentReading',
    sceneDescription: '针对于大文档类型的阅读，归纳总结等，查询内容，或者提供对应的建议等 ',
    integrationProgress: '已集成',
    isMature: '是',
    bannerLink: ''
  },
  {
    id: 6,
    sceneName: '生成试卷场景',
    englishName: 'ExamPaperGeneration',
    sceneDescription: '根据培训情况，自动生成试卷内容，并导出Word内容',
    integrationProgress: '已集成',
    isMature: '是',
    bannerLink: ''
  },
  {
    id: 7,
    sceneName: '文案生成场景',
    englishName: 'Copywriting',
    sceneDescription: '可以根据所提供的模板，自动生成文案信息，比较年度报告、合作协议等复杂文件初稿【待定】',
    integrationProgress: '待定',
    isMature: '否',
    bannerLink: ''
  },
  {
    id: 8,
    sceneName: '会议纪要场景',
    englishName: 'MeetingMinutes',
    sceneDescription: '自动生成会议纪要，生成专业报告并优化排版，根据上传的模板生成会议纪要',
    integrationProgress: '已集成',
    isMature: '是',
    bannerLink: ''
  },
  {
    id: 9,
    sceneName: '产品研究场景',
    englishName: 'ProductResearch',
    sceneDescription: '自动接入Git或者禅道等，获取到进度更新情况，并进行汇总归纳',
    integrationProgress: '已集成',
    isMature: '是',
    bannerLink: ''
  }
];

const handleOpenChannel = (val) => {
  centerDialogVisible.value = val;
};

const submitScreenForm = () => {
  // 这里可以添加创建场景的逻辑
  console.log('创建场景');
};

defineExpose({
  handleOpenChannel,
  submitScreenForm
});
</script>

<style lang="scss" scoped>

</style>