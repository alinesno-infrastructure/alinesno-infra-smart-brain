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
                            <img :src="imagePathByPath(example.image)" @error="handleImageError"  />
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

const welcomeEnabled = ref(false)
const features = ref([])
const examples = ref([])
const tips = ref([])

const randomTip = computed(() => {
    return tips.value[Math.floor(Math.random() * tips.value.length)]
})

const handleImageError = (event) => {
  event.target.src = defaultDemoPng
}

onMounted(async() => {
    try {
        const welcomeConfig = JSON.parse(props.roleInfo.welcomeConfigData);
        welcomeEnabled.value = welcomeConfig.welcomeEnabled;
        features.value = welcomeConfig.features || [];
        examples.value = welcomeConfig.examples || [];
        tips.value = welcomeConfig.tips || [];
        
        // Debug logging to check the values
        console.log('Loaded tips:', tips.value);
        
        const hasSeenWelcome = localStorage.getItem('hasSeenWelcome_' + props.roleInfo.id)

        if(welcomeEnabled.value === 'close') {
            dialogVisible.value = false;
        }else if(welcomeEnabled.value === 'oneTime') {
            dialogVisible.value = !hasSeenWelcome;
        }else if(welcomeEnabled.value === 'always') {
            dialogVisible.value = true;
        }

        localStorage.setItem('hasSeenWelcome_' + props.roleInfo.id, 'true')
    } catch (e) {
        console.error('Error parsing welcome config:', e);
        // Set default values if parsing fails
        features.value = [];
        examples.value = [];
        tips.value = ['暂无提示'];
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
                    border-bottom: 0px solid #e2e8f0;
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