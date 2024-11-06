<template>
    <div class="app-container" style="background-color: #fff;padding-top:10px;" v-loading="loading">
        <el-row>
            <el-col :span="10">
                <!-- 目录大纲编辑界面 -->
                <OutlineEditor ref="OutlineEditorRef" />
            </el-col>
            <el-col :span="14">
                <div class="chapter-edit">
                    <el-card class="box-card" shadow="never">
                        <template #header>
                            <div class="card-header">
                                <div style="display: flex;align-items: center;gap: 10px;;">
                                    章节编辑
                                    <span class="edit-header-avatar">
                                        <img src="http://data.linesno.com/switch_header.png" />
                                    </span>
                                    <span class="edit-header-avatar">
                                        <img src="https://foruda.gitee.com/avatar/1723434744214343939/41655_landonniao_1723434744.png" />
                                    </span>
                                    <el-tooltip class="box-item" effect="dark" content="添加内容编写人员" placement="top">
                                        <span class="edit-header-avatar">
                                            <i class="fa-solid fa-user-plus"></i>
                                        </span>
                                    </el-tooltip>
                                </div>
                                <div>
                                    <el-button type="primary" @click="onSubmit">保存</el-button>
                                    <el-button type="danger" @click="onSubmit">下载</el-button>
                                </div>
                            </div>
                        </template>
                        <el-form :model="form" label-width="100px" label-position="top">
                            <el-form-item :label="currentChaterTitle">
                                <el-input v-model="form.title" placeholder="请输出针对于本章节内容的一些自定义要求" ></el-input>
                            </el-form-item>
                            <el-form-item label="章节内容">
                                <el-scrollbar class="scroll-panel" ref="scrollbarRef" loading always wrap-style="padding:0px">
                                    <div class="chater-text-body" v-html="readerHtml(chaterText)"></div>
                                </el-scrollbar>
                            </el-form-item>
                        </el-form>
                    </el-card>
                </div>
            </el-col>
        </el-row>

        <!-- 输入用户菜单要求 -->

        <el-dialog
            v-model="dialogVisible"
            title="Tips"
            width="500"
            :before-close="handleClose">
            <span>This is a message</span>
            <template #footer>
            <div class="dialog-footer">
                <el-button @click="dialogVisible = false">Cancel</el-button>
                <el-button type="primary" @click="dialogVisible = false">
                Confirm
                </el-button>
            </div>
            </template>
        </el-dialog>

    </div>
</template>

<script setup>

import { ElLoading } from 'element-plus'
import MarkdownIt from 'markdown-it';
import mdKatex from '@traptitech/markdown-it-katex';
import hljs from 'highlight.js';
import ChapterEditor from './chapterEditor'

import { getParam } from '@/utils/ruoyi'
import { openSseConnect, handleCloseSse } from "@/api/base/im/chatsse";

import { ElMessage } from 'element-plus';

import OutlineEditor from './outlineEditor.vue'

const dialogVisible = ref(false)
const loading = ref(true)

const innerRef = ref(null); // 滚动条的处理_starter
const OutlineEditorRef = ref(null); // 滚动条的处理_starter
const scrollbarRef = ref(null);

const currentChaterTitle = ref("章节标题")

const mdi = new MarkdownIt({
  html: true,
  linkify: true,
  highlight(code, language) {
    const validLang = !!(language && hljs.getLanguage(language));
    if (validLang) {
      const lang = language || '';
      return highlightBlock(hljs.highlight(code, { language: lang }).value, lang);
    }
    return highlightBlock(hljs.highlightAuto(code).value, '');
  },
});

function highlightBlock(str, lang) {
  return `<pre class="code-block-wrapper"><code class="hljs code-block-body ${lang}">${str}</code></pre>`;
}

mdi.use(mdKatex, { blockClass: 'katexmath-block rounded-md p-[10px]', errorColor: ' #cc0000' });

const screenId = ref('')
const chaterText = ref("此处是章节内容")

const form = reactive({
    title: '',
    content: ''
});

const onSubmit = () => {
    // 这里处理表单提交逻辑
    ElMessage.success('保存成功！');
    console.log('提交的数据:', form);
};

const onCancel = () => {
    // 这里处理取消操作
    ElMessage.info('已取消');
    form.title = '';
    form.content = '';
}

/** 读取html文本 */
function readerHtml(chatText) {
  return mdi.render(chatText);
}

// 进入初始化
onMounted(() => {
   screenId.value = getParam('screenId')
   loading.value = false 
})


// 销毁信息
onBeforeUnmount(() => {
    //   handleCloseSse(channelId.value).then(res => {
    //     console.log('关闭sse连接成功:' + channelId)
    //   })
});

</script>

<style lang="scss" scoped>
.chapter-edit {
    margin: 0px;

    .box-card{
        border: 0px;
    }

    .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .el-form {
        .el-form-item {
            &.el-form-item--small {
                margin-bottom: 15px;
            }

            .el-input,
            .el-textarea {
                width: 100%;
            }
        }
    }

    .el-button+.el-button {
        margin-left: 10px;
    }

    .scroll-panel{
        height: calc(100vh - 300px);
        width: 100%;
    }

    .chater-text-body {
        background: #fafafa;
        padding:10px;
        height: 100% ;
        width: 100%;
        border-radius: 4px;
    }
}

</style>
