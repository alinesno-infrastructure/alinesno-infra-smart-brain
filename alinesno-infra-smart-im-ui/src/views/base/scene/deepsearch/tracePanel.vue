<template>
    <div class="task-trace-panel">
        <div class="title">
            动态流程 
            <span>
                <el-button  type="primary" size="small" text @click="handleClose()">
                    <el-icon><Close /></el-icon>
                </el-button>
            </span>
        </div>
        <div class="follow-step-panel">
            <div class="step-title">
                正在使用
                <span class="trace-tag">EchartsTool</span>
                显示
                <span class="trace-tag">团队人员提交代码比例</span>
            </div>
            <div class="step-body">
                <div v-html="toolContentPanel" />
            </div>
        </div>
        <div class="follow-btn-panel">
            
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { Close } from '@element-plus/icons-vue';

const toolContentPanel = ref("");

// 假设这里是包含网站链接和新闻名称的对象
const newsLinks = {
    'https://www.xinhuanet.com/': '新华网-权威新闻资讯，全方位报道国内外大事',
    'https://www.people.com.cn/': '人民网-依托人民日报，深度解读时政热点与民生百态',
    'https://www.cctv.com/': '央视网-汇聚央视优质资源，呈现丰富新闻及各类节目内容',
    'https://www.chinanews.com/': '中国新闻网-提供海量实时新闻，聚焦全球华人资讯与社会万象',
    'https://www.gmw.cn/': '光明网-以思想文化为特色，关注科技、教育、文化等领域动态',
    'https://www.eastday.com/': '东方网-立足上海，辐射全国，报道各类综合新闻资讯',
    'https://www.dzwww.com/': '大众网-山东重要新闻门户，涵盖地方及全国多元新闻报道',
    'https://www.southcn.com/': '南方网-经广东官方批准，深度聚焦南方地区发展与热点',
    'https://www.enorth.com.cn/': '北方网-天津权威媒体平台，展现天津及周边地区新闻',
    'https://www.zynews.cn/': '中原网-国家一类新闻网站，聚焦中原地区各类资讯'
};

onMounted(() => {
    // 动态生成包含链接和新闻名称的HTML内容
    const linkHtml = Object.entries(newsLinks).map(([link, name]) => {
        return `<p class="link-item"><a href="${link}" target="_blank">${name}</a></p>`;
    }).join('');
    toolContentPanel.value = linkHtml;
});

const handleClose = () => {
    // 关闭操作的逻辑，这里暂时为空
};
</script>

<style lang="scss" scoped>

.task-trace-panel {

    height: 85%;
    background: #fff;
    border-radius: 8px;
    padding: 5px;
    margin-right: 20px;
    margin-left: 20px;
    box-shadow: 0px 2px 6px 0px rgba(0, 0, 0, 0.05);
    border: 1px solid var(--line-color-border-2, #eaedf1);
    margin-top: 20%;

    .title {
        font-size: 14px;
        padding: 10px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        color: #555;
        border-bottom: 1px solid var(--line-color-border-2, #eaedf1);
        margin-bottom: 10px;
    }

    .follow-step-panel {
        padding-left: 10px;
        padding-right: 10px;

        .step-title {
            font-size: 14px;
            display: flex;
            color: #555;
            align-items: center;
        }

        .trace-tag {
            margin: 0 4px;
            display: flex;
            padding: 3px 4px;
            align-items: center;
            gap: 4px;
            border-radius: 4px;
            background: #f1f3f5;
            color: #42464e;
            font-size: 11px;
            font-style: normal;
            font-weight: 600;
        }

    }

}


</style>

<style lang="css">
.link-item{
    margin: 7px 0px;
    font-size: 14px;
    background: #f5f5f5;
    border-radius: 5px;
    padding: 7px;
    padding-left: 10px;
    color: #666;
}
</style>