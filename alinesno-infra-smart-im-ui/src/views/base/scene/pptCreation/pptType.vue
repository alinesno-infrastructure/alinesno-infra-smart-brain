<template>
    <div class="side-type-container">
        <div class="title">试卷场景</div>
        <div class="type-list">
            <div
                v-for="item in sceneList"
                :key="item.id"
                :class="{ 'active': activeItem === item.id }"
                @mouseover="handleMouseOver(item.id)"
                @mouseout="handleMouseOut(item.id)"
                @click="handleClick(item.id)"
            >
                <i :class="['fa-solid', item.icon]"></i> {{ item.sceneName }}
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';

const sceneList = ref([
    {
        id: 1,
        sceneName: '驾校培训',
        icon: 'fa-car'
    },
    {
        id: 2,
        sceneName: '资格考试',
        icon: 'fa-building'
    },
    {
        id: 3,
        sceneName: '职称考试',
        icon: 'fa-id-card'
    },
    {
        id: 4,
        sceneName: '学校考试',
        icon: 'fa-school'
    },
    {
        id: 5,
        sceneName: '企业培训',
        icon: 'fa-building'
    },
    {
        id: 6,
        sceneName: '技能认证',
        icon: 'fa-certificate'
    },
    {
        id: 7,
        sceneName: '在线学习',
        icon: 'fa-laptop'
    },
    {
        id: 8,
        sceneName: '安全培训',
        icon: 'fa-shield-halved'
    }
]);

const activeItem = ref(null);
const hoverItem = ref(null);

const handleMouseOver = (id) => {
    hoverItem.value = id;
};

const handleMouseOut = (id) => {
    if (hoverItem.value === id) {
        hoverItem.value = null;
    }
};

const handleClick = (id) => {
    activeItem.value = id;
    // 这里可以触发场景切换事件
    console.log('切换到场景:', sceneList.value.find(item => item.id === id).sceneName);
};

</script>

<style lang="scss" scoped>
.side-type-container {
    padding: 15px;
    width: 220px;

    .title {
        font-size: 18px;
        margin-bottom: 10px;
        font-weight: bold;
    }

    .type-list {
        font-size: 14px;
        display: flex;
        flex-direction: column;
        gap: 5px;

        div {
            padding: 8px 5px;
            cursor: pointer;
            border-radius: 5px;
            display: flex;
            align-items: center;
            gap: 8px;

            i {
                color: #d33233;
                width: 20px;
                text-align: center;
                font-size: 16px;
            }

            &:hover,
            &.active {
                background: #f5f5f5;
            }
        }
    }
}
</style>