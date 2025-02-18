<template>
    <div class="app-container agent-inference-container">

        <div class="page-header-container">
            <el-page-header @back="goBack">
                <template #content>
                    <div style="display: flex;gap: 10px;align-items: center;">
                        <span style="color: #aaaaaa;font-size: 14px;">更新时间：2025-02-14 23:50:44</span>
                    </div>
                </template>
            </el-page-header>
            <el-button type="primary" bg @click="handleInference">保存配置</el-button>
        </div>

        <el-row :gutter="20">
            <!--类型数据-->
            <el-col :span="8" :xs="24">
                <el-scrollbar class="scrollbar-style">
                    <el-card class="box-card" shadow="never">
                        <div slot="header" class="clearfix">
                            <span class="box-card-title">角色信息</span>
                        </div>
                        <el-row style="margin: 10px 0px;">
                            <el-col :span="24">
                                <div style="display: flex;align-items: center;gap: 8px;margin-bottom: 10px;">
                                    <span>
                                        <img style="width:30px;height:30px;border-radius: 5px"
                                            src="http://staticok.oss-cn-hangzhou.aliyuncs.com/avatar-share/thumbnail-6046836d-7766-4bfd-a93d-85fd52d2b0e4.webp" />
                                    </span>
                                    <span style="font-weight: bold;">
                                        示例技术专家
                                    </span>
                                    <div class="text item">
                                        <el-button type="primary" text bg @click="handleInference">编辑</el-button>
                                    </div>
                                </div>
                            </el-col>
                            <el-col :span="24">
                                <div style="font-size:14px;color:#777;line-height: 20px;">
                                    这是一个智能助手，你可以通过智能助手来获取一些数据，这是一个智能助手，你可以通过智能助手来获取一些数据
                                </div>
                            </el-col>
                        </el-row>
                    </el-card>

                    <!-- AI配置界面 -->
                    <el-card class="box-card" shadow="never">
                        <div slot="header" class="clearfix">
                            <span class="box-card-title">AI配置</span>
                        </div>

                        <el-row class="nav-row">
                            <el-col :span="6">
                                <div class="ai-config-section-title">
                                    <i class="fa-solid fa-brain"></i> AI模型
                                </div>
                            </el-col>
                            <el-col :span="18">
                                <div class="select-wrapper">
                                    <el-select v-model="value" placeholder="Select" size="large" style="width:100%">
                                        <el-option v-for="item in options" :key="item.value" :label="item.label"
                                            :value="item.value" />
                                    </el-select>
                                    <i class="fa-solid fa-feather"></i>
                                </div>
                            </el-col>
                        </el-row>

                        <el-row class="nav-row">
                            <el-col :span="24">
                                <div class="ai-config-section-title">
                                    <i class="fa-solid fa-pen-to-square"></i> 提示词
                                </div>
                            </el-col>
                            <el-col :span="24">
                                <div class="input-wrapper">
                                    <el-input type="textarea" resize="none" :rows="4"
                                        placeholder="模型固定的引导词，通过调整该内容，可以引导模型聊天方向。该内容会被固定在上下文的开头。可通过输入 / 插入选择变量如果关联了知识库，你还可以通过适当的描述，来引导模型何时去调用知识库搜索。例如：你是电影《星际穿越》的助手，当用户询问与《星际穿越》相关的内容时，请搜索知识库并结合搜索结果进行回答。"></el-input>
                                </div>
                            </el-col>
                        </el-row>

                        <el-row class="nav-row">
                            <el-col :span="12">
                                <div class="ai-config-section-title">
                                    <i class="fa-solid fa-database"></i> <span>关联知识库</span>
                                </div>
                            </el-col>
                            <el-col :span="12">
                                <div class="button-group">
                                    <el-button type="primary" text bg>+ 选择</el-button>
                                    <el-button type="primary" text bg>参数</el-button>
                                </div>
                            </el-col>
                        </el-row>

                        <el-row class="nav-row">
                            <el-col :span="12">
                                <div class="ai-config-section-title">
                                    <i class="fa-solid fa-wrench"></i> <span>工具调用</span>
                                </div>
                            </el-col>
                            <el-col :span="12">
                                <div class="button-group">
                                    <el-button type="primary" text bg>+ 选择</el-button>
                                </div>
                            </el-col>
                        </el-row>

                        <el-row class="nav-row">
                            <el-col :span="12">
                                <div class="ai-config-section-title">
                                    <i class="fa-solid fa-upload"></i> <span>文件上传</span>
                                </div>
                            </el-col>
                            <el-col :span="12">
                                <div class="button-group">
                                    <el-button type="primary" text bg>开启</el-button>
                                </div>
                            </el-col>
                        </el-row>

                        <!-- 
                        <el-row class="nav-row">
                            <el-col :span="12">
                                <div class="ai-config-section-title">
                                    <i class="fa-solid fa-computer"></i> <span>全局变量</span>
                                </div>
                            </el-col>
                            <el-col :span="12">
                                <div class="button-group">
                                    <el-button type="primary" text bg>+ 新增</el-button>
                                </div>
                            </el-col>
                        </el-row> 
                        -->

                        <el-row class="nav-row">
                            <el-col :span="24">
                                <div class="ai-config-section-title">
                                    <i class="fa-solid fa-comment-slash"></i> 对话开场白
                                </div>
                            </el-col>
                            <el-col :span="24">
                                <div class="input-wrapper">
                                    <el-input type="textarea" resize="none" :rows="2"
                                        placeholder="每次对话开始前，发送一个初始内容。支持标准 Markdown 语法，可使用的额外标记：[快捷按键]：用户点击后可以直接发送该问题"></el-input>
                                </div>
                            </el-col>
                        </el-row>

                        <el-row class="nav-row">
                            <el-col :span="12">
                                <div class="ai-config-section-title">
                                    <i class="fa-solid fa-volume-up"></i> <span>语音播放</span>
                                </div>
                            </el-col>
                            <el-col :span="12">
                                <div class="button-group">
                                    <el-button type="primary" text bg>+ 浏览器自带（免费）</el-button>
                                </div>
                            </el-col>
                        </el-row>

                        <el-row class="nav-row">
                            <el-col :span="12">
                                <div class="ai-config-section-title">
                                    <i class="fa-solid fa-microphone"></i> <span>语音输入</span>
                                </div>
                            </el-col>
                            <el-col :span="12">
                                <div class="button-group">
                                    <el-button type="primary" text bg>关闭</el-button>
                                </div>
                            </el-col>
                        </el-row>

                        <el-row class="nav-row">
                            <el-col :span="12">
                                <div class="ai-config-section-title">
                                    <i class="fa-solid fa-lightbulb"></i> <span>猜你想问</span>
                                </div>
                            </el-col>
                            <el-col :span="12">
                                <div class="button-group">
                                    <el-button type="primary" text bg>关闭</el-button>
                                </div>
                            </el-col>
                        </el-row>

                        <!-- 
                        <el-row class="nav-row">
                            <el-col :span="12">
                                <div class="ai-config-section-title">
                                    <i class="fa-solid fa-hand-pointer"></i> <span>输入引导</span>
                                </div>
                            </el-col>
                            <el-col :span="12">
                                <div class="button-group">
                                    <el-button type="primary" text bg>关闭</el-button>
                                </div>
                            </el-col>
                        </el-row> 
                        -->

                    </el-card>
                </el-scrollbar>
            </el-col>

            <!--类型数据-->
            <el-col :span="16" :xs="24">
                <el-card shadow="never" class="agent-chat-box">

                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
const router = useRouter();
const { proxy } = getCurrentInstance();
</script>

<style lang="scss" scoped>
.ai-config-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px;

    .left-part {
        display: flex;
        align-items: center;

        span {
            margin-left: 10px;
        }
    }

    .right-part {
        width: 200px;
    }
}

.ai-config-section {
    padding: 10px;

    .section-title {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 5px;
    }
}

.agent-inference-container {
    padding: 5px;
    padding-top: 10px;
    background: #f4f4f7;

    .box-card {
        margin-bottom: 10px;
        background: #fff;
    }

    .ai-config-section-title {
        display: flex;
        height: 100%;
        align-items: center;
        padding: 10px 5px;
        font-size: 14px;
        gap: 8px;
    }

    .scrollbar-style {
        height: calc(100vh - 122px);
        // background-color: #fff;
        padding-right: 10px;
    }

    .nav-row {
        padding-top: 5px;
        padding-bottom: 5px;
        border-bottom: 1px solid #e8ebf0;

        :last-child {
            border: 0px;
        }
    }

    .select-wrapper {
        display: flex;
        align-items: center;
        gap: 10px;
        width: 100%;
    }

    .input-wrapper {
        display: flex;
        align-items: center;
        gap: 10px;

        input {
            background-color: #f5f5f5;
        }
    }

    .button-group {
        display: flex;
        align-items: center;
        gap: 10px;
        justify-content: flex-end;
    }

    .box-card-title {
        font-weight: bold;
        font-size: 14px;
    }

    .agent-chat-box {
        width: 100%;
        background: #ffffff;
        border-radius: 5px;
        height: calc(100vh - 120px);
    }

    .page-header-container {
        margin-bottom: 10px;
        width: 100%;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }
}
</style>