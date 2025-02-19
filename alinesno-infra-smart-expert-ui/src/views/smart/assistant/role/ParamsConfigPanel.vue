<template>
    <div>
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
                        <el-button type="primary" text bg>
                            <i class="fa-solid fa-screwdriver-wrench"></i>
                        </el-button>
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
                        <i class="fa-solid fa-upload"></i> <span>长期记忆</span>
                    </div>
                </el-col>
                <el-col :span="12">
                    <div class="button-group">
                        <el-button type="primary" text bg>开启</el-button>
                    </div>
                </el-col>
            </el-row>

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

        </el-card>
    </div>
</template>
