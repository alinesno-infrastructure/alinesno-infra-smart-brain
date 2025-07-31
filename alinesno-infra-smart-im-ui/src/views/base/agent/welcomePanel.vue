<template>
    <div class="welcome-container">
        <el-dialog v-model="dialogVisible" width="800px" :close-on-click-modal="false" :show-close="true">
            <div class="welcome-card gradient-border">
                <!-- Header Section -->
                <div class="card-header">
                    <div class="icon-circle">
                        <img :src="imagePathByPath(roleInfo.roleAvatar)" class="agent-avatar" />
                    </div>
                    <div class="header-text">
                        <h1>你好，我是 {{ roleInfo.roleName }}</h1>
                        <p class="description">{{ roleInfo.responsibilities }}</p>
                    </div>
                </div>

                <!-- Feature Grid -->
                <div class="welcome-example-section">
                    <h3>核心能力</h3>
                    <div class="feature-grid">
                        <div v-for="(feature, index) in features" :key="index" class="feature-item">
                            <div class="feature-icon">
                                <i :class="feature.icon"></i>
                            </div>
                            <div class="feature-text">
                                <h3>{{ feature.title }}</h3>
                                <p>{{ feature.description }}</p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Example Section -->
                <div class="welcome-example-section">
                    <h3>使用示例</h3>
                    <ul class="welcome-example-list">
                        <li v-for="(example, index) in examples" :key="index" class="welcome-example-item">
                            <img :src="example.image" @error="handleImageError"  />
                            <div class="text-label">{{ example.label }}</div>
                        </li>
                    </ul>
                </div>

                <!-- Tip Section -->
                <div class="tip-section">
                    <div class="tip-icon">
                        <i class="fas fa-lightbulb"></i>
                    </div>
                    <div class="tip-content">
                        <p>{{ randomTip }}</p>
                    </div>
                </div>
            </div>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

import defaultDemoPng from "@/assets/banner/3.png"

const props = defineProps({
    roleInfo: {
        type: Object,
        required: true
    }
})

const dialogVisible = ref(true)

// Feature data
const features = [
    {
        icon: 'fas fa-database',
        title: '数据智能处理',
        description: '支持Excel/CSV等多种格式，自动清洗与预处理'
    },
    {
        icon: 'fas fa-chart-bar',
        title: '可视化分析',
        description: '自动生成最佳图表，直观呈现数据洞察'
    },
    {
        icon: 'fas fa-project-diagram',
        title: '趋势预测',
        description: '基于机器学习算法预测未来趋势'
    },
    {
        icon: 'fas fa-file-export',
        title: '报告生成',
        description: '一键生成专业分析报告，支持PDF/PPT导出'
    }
]

// Example data
const examples = [
    {
        image: 'https://p3-flow-imagex-sign.byteimg.com/ocean-cloud-tos/image_generation/7b4a0f7eb22c1e0fd84545b424539c41_1753357641410101866.png~tplv-a9rns2rl98-image.png',
        label: '分析过去6个月的销售额趋势并预测未来3个月'
    },
    {
        image: 'https://p9-flow-imagex-sign.byteimg.com/ocean-cloud-tos/image_generation/8529aa9b1e8a9f4a368c9e79b103430f_1752460962402108797.png~tplv-a9rns2rl98-image.png',
        label: '客户群体细分与特征分析'
    },
    {
        image: 'https://p3-flow-imagex-sign.byteimg.com/ocean-cloud-tos/image_generation/28a35129026ff5c54507831d76424e14_1752461855457689800.png~tplv-a9rns2rl98-image.png',
        label: '产品销量关联性分析'
    }
]

// Tips data
const tips = [
    "提示：使用自然语言描述你的分析需求，我会自动选择最合适的分析方法",
    "技巧：上传数据后，可以问我'这些数据有什么值得关注的趋势吗？'",
    "建议：对于复杂分析，可以分步骤提出需求，我会逐步引导完成",
    "小贴士：点击图表可以查看详细数据，支持下载高清图片"
]

const randomTip = computed(() => {
    return tips[Math.floor(Math.random() * tips.length)]
})

const handleImageError = (event) => {
  event.target.src = defaultDemoPng
}

// Optional: Show dialog only on first visit
onMounted(() => {
    const hasSeenWelcome = localStorage.getItem('hasSeenWelcome_' + props.roleInfo.id)
    if (!hasSeenWelcome) {
        dialogVisible.value = true
        localStorage.setItem('hasSeenWelcome_' + props.roleInfo.id, 'true')
    } else {
        dialogVisible.value = true
    }
})
</script>

<style lang="scss" scoped>
.welcome-container {
    height: 100%;
    background-color: #f5f5f5;
}

.welcome-card {
    max-width: 100%;
    background-color: white;
    border-radius: 12px;
    position: relative;
    overflow: hidden;
    padding: 10px;

    .card-header {
        display: flex;
        align-items: center;
        gap: 16px;
        margin-bottom: 24px;

        .icon-circle {
            width: 58px;
            height: 58px;
            border-radius: 50%;
            background: linear-gradient(135deg, #3a7bd5, #00d2ff);
            display: flex;
            align-items: center;
            justify-content: center;
            flex-shrink: 0;

            i {
                color: white;
                font-size: 20px;
            }
        }

        .header-text {
            h1 {
                font-size: 24px;
                font-weight: 600;
                margin: 0;
                color: #2c3e50;
            }

            .description {
                color: #7f8c8d;
                margin: 8px 0 0;
                font-size: 14px;
                line-height: 1.4;
            }
        }
    }

    .agent-avatar {
        width: 100%;
        height: 100%;
        border-radius: 50%;
        object-fit: cover;
        object-position: center;
    }

    .feature-grid {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 16px;
        margin-bottom: 24px;

        @media (max-width: 600px) {
            grid-template-columns: 1fr;
        }

        .feature-item {
            display: flex;
            align-items: flex-start;
            gap: 12px;
            padding: 16px;
            border-radius: 8px;
            background-color: #f8fafc;
            transition: all 0.3s ease;
            border: 0px solid #e2e8f0;

            &:hover {
                background-color: #f1f5f9;
            }

            .feature-icon {
                width: 36px;
                height: 36px;
                border-radius: 8px;
                display: flex;
                align-items: center;
                justify-content: center;
                flex-shrink: 0;
                background-color: #e3f2fd;
                color: #1976d2;

                i {
                    font-size: 16px;
                }
            }

            .feature-text {
                h3 {
                    font-weight: 500;
                    margin: 0 0 6px 0;
                    color: #2c3e50;
                    font-size: 15px;
                }

                p {
                    font-size: 13px;
                    color: #7f8c8d;
                    margin: 0;
                    line-height: 1.4;
                }
            }
        }
    }

    .welcome-example-section {
        margin-bottom: 20px;

        h3 {
            font-weight: 500;
            color: #2c3e50;
            margin-bottom: 16px;
            font-size: 14px;
            font-weight: bold;
            position: relative;
            padding-left: 0px;
        }

        .welcome-example-list {
            list-style: none;
            padding: 0;
            margin: 0;
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 12px;

            @media (max-width: 768px) {
                grid-template-columns: repeat(2, 1fr);
            }

            @media (max-width: 480px) {
                grid-template-columns: 1fr;
            }

            .welcome-example-item {
                background: #f8fafc;
                border-radius: 8px;
                transition: all 0.3s ease;
                overflow: hidden;
                border: 0px solid #e2e8f0;
                cursor: pointer;

                &:hover {
                    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
                }

                .text-label {
                    font-size: 13px;
                    padding: 12px;
                    color: #2c3e50;
                    line-height: 1.4;
                }

                img {
                    width: 100%;
                    height: 100px;
                    object-fit: cover;
                    border-bottom: 1px solid #e2e8f0;
                }
            }
        }
    }

    .tip-section {
        display: flex;
        align-items: flex-start;
        gap: 12px;
        padding: 8px 16px;
        background-color: #f0f7ff;
        border-radius: 8px;
        margin-top: 20px;
        border-left: 4px solid #3a7bd5;

        .tip-icon {
            color: #3a7bd5;
            font-size: 18px;
            margin-top: 2px;
        }

        .tip-content {
            p {
                margin: 0;
                font-size: 14px;
                color: #2c3e50;
                line-height: 1.5;
            }
        }
    }
}
</style>