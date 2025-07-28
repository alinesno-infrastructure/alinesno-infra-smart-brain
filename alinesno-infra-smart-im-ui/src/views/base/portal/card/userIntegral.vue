<template>
    <div class="user-integral">
        <div class="integral-title">
            权益中心
        </div>
        <div class="integral-level" :class="userType">
            <i :class="userIcon"></i>
            <span>{{ userTypeText }}</span>
        </div>
    </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
    // 用户类型: trial(试用), standard(标准), enterprise(企业)
    type: {
        type: String,
        default: 'trial',
        validator: (value) => ['trial', 'standard', 'enterprise'].includes(value)
    }
});

// 用户类型对应文本
const userTypeText = computed(() => {
    const map = {
        trial: '试用用户',
        standard: '标准用户',
        enterprise: '企业用户'
    };
    return map[props.type] || '试用用户';
});

// 用户类型对应图标
const userIcon = computed(() => {
    const map = {
        trial: 'fas fa-user-clock',
        standard: 'fas fa-user-check',
        enterprise: 'fas fa-building-user'
    };
    return map[props.type] || 'fas fa-user-clock';
});
</script>

<style lang="scss">
.user-integral {
    font-size: 14px;
    position: absolute;
    bottom: 0px;
    width: 220px;
    padding: 12px;
    border-radius: 8px;
    background-color: #f8f9fa;

    .integral-title {
        font-size: 15px;
        font-weight: bold;
        margin-bottom: 10px;
        color: #666;
    }
    
    .integral-level {
        display: flex;
        align-items: center;
        gap: 10px;
        border-radius: 6px;
        transition: all 0.3s ease;
        
        i {
            font-size: 16px;
            width: 20px;
            text-align: center;
        }
        
        span {
            font-weight: 500;
        }
        
        &.trial {
            color: #FF9500;
            background-color: rgba(255, 149, 0, 0.1);
            
            i {
                color: #FF9500;
            }
        }
        
        &.standard {
            color: #007AFF;
            background-color: rgba(0, 122, 255, 0.1);
            
            i {
                color: #007AFF;
            }
        }
        
        &.enterprise {
            color: #34C759;
            background-color: rgba(52, 199, 89, 0.1);
            
            i {
                color: #34C759;
            }
        }
    }
}
</style>