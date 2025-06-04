<template>
    <div class="article-edit-container edit-display-container">
        <el-container style="height:calc(100vh - 40px );background-color: #fff;">

            <el-aside width="280px" class="article-edit-aside">
                <FunctionList />
            </el-aside>


            <el-main class="article-edit-main">

                <!-- 头部/保存 -->
                <div class="article-edit-header">
                    <el-button type="primary" text bg size="large" @click="handleSaveArticle" >
                        <i class="fa-solid fa-floppy-disk"></i> &nbsp; 保存
                    </el-button>
                </div>

                <div class="article-edit-content">
                    <!-- 标题内容 -->
                    <div class="article-edit-title">
                        {{ articleData.title }}
                    </div>
                    <!-- 编辑内容 -->
                    <div class="article-edit-content-display">
                        <AgentContentDisplay 
                            v-model:articleData="articleData"
                            @content-change="handleContentChange"
                            ref="contentEditor"
                        />
                    </div>
                </div>
            </el-main>

            <el-aside width="300px" class="article-edit-right-aside">
                <EditorFunctionPanel 
                    :resultConfig="articleConfig"
                    @config-change="handleConfigChange"
                    />
            </el-aside>

        </el-container>
    </div>
</template>

<script setup>

import { onMounted, ref } from 'vue';

import {
  getArticleById,
  updateArticle
} from '@/api/base/im/scene/articleWriting';

import FunctionList from './functionList'
import AgentContentDisplay from './agentContentDisplay'
import EditorFunctionPanel from './components/editorFunctionPanel'
import { ElMessage } from 'element-plus';

const route = useRoute();
const articleId = ref(route.query.articleId)
const contentEditor = ref(null);

const articleData = ref({
  title: '默认标题',
  content: '',
  description: ''
});

const articleConfig = ref({
    "styles": [
        {
            "key": "默认",
            "name": "默认"
        },
        {
            "key": "生动活泼",
            "name": "生动活泼"
        },
        {
            "key": "情感真挚",
            "name": "情感真挚"
        },
        {
            "key": "规范写作",
            "name": "规范写作"
        },
        {
            "key": "想象力丰富",
            "name": "想象力丰富"
        },
        {
            "key": "观察描写",
            "name": "观察描写"
        },
        {
            "key": "童趣表达",
            "name": "童趣表达"
        }
    ],
    "hooks": [
        {
            "key": "默认",
            "name": "默认"
        },
        {
            "key": "记叙文",
            "name": "记叙文"
        },
        {
            "key": "日记",
            "name": "日记"
        },
        {
            "key": "议论文",
            "name": "议论文"
        },
        {
            "key": "说明文",
            "name": "说明文"
        },
        {
            "key": "议论文",
            "name": "议论文"
        },
        {
            "key": "散文",
            "name": "散文"
        },
        {
            "key": "考试作文",
            "name": "考试作文"
        },
        {
            "key": "比赛投稿",
            "name": "比赛投稿"
        },
        {
            "key": "读后感",
            "name": "读后感"
        }
    ],
    "time": [
        {
            "key": "300字",
            "name": "300字"
        },
        {
            "key": "500字",
            "name": "500字"
        },
        {
            "key": "800字",
            "name": "800字"
        },
        {
            "key": "1000字",
            "name": "1000字"
        }
    ]
});


// 定义用于展示的响应式变量
const currentStyle = ref('默认');
const currentScene = ref('默认');
const currentNum = ref('300字');

// 接收子组件传递的配置变更事件
const handleConfigChange = (config) => {
  // 更新父组件的状态
  currentStyle.value = articleConfig.value.styles.find(
    style => style.key === config.style
  )?.name || '默认';
  currentScene.value = articleConfig.value.hooks.find(
    hook => hook.key === config.scene
  )?.name || '默认';
  currentNum.value = articleConfig.value.time.find(
    num => num.key === config.num
  )?.name || '300字';

  // 可在此处触发后续逻辑（如生成文章、保存配置等）
  console.log('最新配置：', config);
};

// 处理内容变化
const handleContentChange = (content) => {
  console.log('内容已更新:', content);
  // 可以在这里添加自动保存逻辑
  articleData.value.content = content ; 
};

// 保存文章
const handleSaveArticle = () => {
  // 发起保存请求
  updateArticle(articleData.value).then(res => {
    console.log('保存成功:', res);
    ElMessage.success('保存成功');
  })
}

onMounted(() => {
  // 初始化时，根据文章ID获取文章内容
  getArticleById(articleId.value).then(res => {
    console.log('res = ' + res);
    articleData.value = res.data ;
  }).catch(err => {
    ElMessage.error('获取文章失败:', err);
  })
});

</script>

<style lang="scss" scoped>
.article-edit-container {
    .article-edit-aside {
        padding: 0px;
        border-right: 1px solid #f2f3f7;
        background: #fff;
        margin-bottom: 0px;
    }

    .article-edit-header {
        margin: 10px;
        text-align: right;
        position: absolute;
        right: 310px;
    }

    .article-edit-right-aside {
        padding: 20px;
        margin-bottom: 0px;
        display: flex;
        flex-direction: column;
        height: calc(100vh - 40px);
        border-left: 1px solid #f2f3f7;
        background-color: #fafafa;
    }

    .article-edit-main {
        padding: 0px !important;
    }

    .article-edit-content {
        width: 980px;
        margin: auto;
        margin-top: 20px;
        height: calc(100vh - 70px);
        margin-bottom: 10px;

        .article-edit-title {
            margin: 10px 0px;
            font-size: 17px;
            font-weight: bold;
        }

    }

}
</style>