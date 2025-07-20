<template>
    <div class="sidebar-section">
        <h2>
            <i class="fa-solid fa-list-ul"></i>
            <span>文档目录</span>
        </h2>
        <div class="outline-tree">
            <div class="outline-item">
                <div class="outline-header" @click="toggleOutline('chapter1')">
                    <i class="fa-solid fa-chevron-right" :class="{ 'rotate-90': activeOutline === 'chapter1' }"></i>
                    <span>第一章 项目概述</span>
                </div>
                <div class="outline-children" v-show="activeOutline === 'chapter1'">
                    <div class="outline-child">1.1 项目背景</div>
                    <div class="outline-child">1.2 研究目的</div>
                    <div class="outline-child">1.3 研究意义</div>
                </div>
            </div>
            <div class="outline-item">
                <div class="outline-header" @click="toggleOutline('chapter2')">
                    <i class="fa-solid fa-chevron-right" :class="{ 'rotate-90': activeOutline === 'chapter2' }"></i>
                    <span>第二章 相关理论</span>
                </div>
            </div>
            <div class="outline-item">
                <div class="outline-header" @click="toggleOutline('chapter3')">
                    <i class="fa-solid fa-chevron-right" :class="{ 'rotate-90': activeOutline === 'chapter3' }"></i>
                    <span>第三章 研究方法</span>
                </div>
            </div>
            <div class="outline-item">
                <div class="outline-header" @click="toggleOutline('chapter4')">
                    <i class="fa-solid fa-chevron-right" :class="{ 'rotate-90': activeOutline === 'chapter4' }"></i>
                    <span>第四章 实验结果与分析</span>
                </div>
            </div>
            <div class="outline-item">
                <div class="outline-header" @click="toggleOutline('chapter5')">
                    <i class="fa-solid fa-chevron-right" :class="{ 'rotate-90': activeOutline === 'chapter5' }"></i>
                    <span>第五章 结论与展望</span>
                </div>
            </div>
        </div>
    </div>

    <div class="sidebar-section">
        <h2>
            <i class="fa-solid fa-clock-rotate-left"></i>
            <span>历史版本</span>
        </h2>
        <div class="version-history">
            <div class="version-item" v-for="version in versions" :key="version.id">
                <div class="version-header">
                    <span class="version-name">{{ version.name }}</span>
                    <span class="version-time">{{ version.time }}</span>
                </div>
                <div class="version-desc">{{ version.desc }}</div>
            </div>
        </div>
    </div>
</template>

<script setup>
// Outline management
const activeOutline = ref('chapter1');
const toggleOutline = (chapter) => {
    activeOutline.value = activeOutline.value === chapter ? null : chapter;
};

// Version history
const versions = ref([
    { id: 1, name: '版本 1.3', time: '今天 14:30', desc: 'AI润色修改' },
    { id: 2, name: '版本 1.2', time: '今天 11:15', desc: '章节内容更新' },
]);
</script>

<style lang="scss" scoped>
@import '@/assets/styles/document-formatter.scss';
</style>