<template>
    <div class="agent-card" :class="{ 'selected': isSelected }" @click="toggleSelection">
        <div>
            <span class="agent-avatar">
                <img :src="imagePathByPath(agent.roleAvatar)">
            </span>
            <span class="agent-name">
                {{ agent.roleName }}
            </span>
        </div>
        <div v-if="isSelected" class="checkmark">&#10004;</div>
    </div>
</template>

<script setup>
import { ref, defineProps, defineEmits } from 'vue';

const props = defineProps({
    agent: {
        type: Object,
        default: {}
    },
    modelValue: {
        type: [String, Array],
        default: ''
    }
})

const emits = defineEmits(['update:modelValue']);
const isSelected = ref(false);

const toggleSelection = () => {
    isSelected.value = !isSelected.value;
    let selectedIds = Array.isArray(props.modelValue) ? [...props.modelValue] : [];
    if (isSelected.value) {
        if (!selectedIds.includes(props.agent.id)) {
            selectedIds.push(props.agent.id);
        }
    } else {
        selectedIds = selectedIds.filter(id => id !== props.agent.id);
    }
    emits('update:modelValue', selectedIds);
}

// 初始化选中状态
if (Array.isArray(props.modelValue)) {
    isSelected.value = props.modelValue.includes(props.agent.id);
}
</script>

<style lang="scss" scoped>
.agent-card {
    padding: 10px;
    margin: 5px;
    cursor: pointer;
    position: relative;
    line-height: 2rem;
    background: #fafafa;
    border-radius: 8px;
    border: 0px;

    .agent-avatar {
        width: 30px;
        height: 30px;
        border-radius: 50%;
        overflow: hidden;
        margin-right: 10px;
        display: inline-block;
        vertical-align: middle;

        img {
            width: 100%;
            height: 100%;
        }
    }

    .agent-name {
        font-size: 14px;
        color: #606266;
        display: inline-block;
        font-weight: 500;
    }
}

.agent-card.selected {
    border-color: #145799;
    background-color: #e7f3ff;
}

.checkmark {
    position: absolute;
    top: 5px;
    right: 5px;
    color: #145799;
}
</style>