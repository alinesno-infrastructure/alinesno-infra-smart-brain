<template>
    <div class="side-type-container">
        <div class="title">文章分类</div>
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
        sceneName: '培训课程',
        icon: 'fa-graduation-cap'
    },
    {
        id: 2,
        sceneName: '项目介绍',
        icon: 'fa-briefcase'
    },
    {
        id: 3,
        sceneName: '产品展示',
        icon: 'fa-cube'
    },
    {
        id: 4,
        sceneName: '数据分析',
        icon: 'fa-chart-bar'
    },
    {
        id: 5,
        sceneName: '活动策划',
        icon: 'fa-calendar-check'
    },
    {
        id: 6,
        sceneName: '企业宣传',
        icon: 'fa-building'
    },
    {
        id: 7,
        sceneName: '商业计划',
        icon: 'fa-line-chart'
    },
    {
        id: 8,
        sceneName: '教学课件',
        icon: 'fa-book'
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