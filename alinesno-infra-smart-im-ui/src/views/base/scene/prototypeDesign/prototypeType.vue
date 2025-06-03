<template>
    <div class="side-type-container">
        <div class="title">原型分类</div>
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
        sceneName: '移动网站',
        icon: 'fa-mobile'
    },
    {
        id: 2,
        sceneName: '门户官网',
        icon: 'fa-globe'
    },
    {
        id: 3,
        sceneName: '管理后台',
        icon: 'fa-cog'
    },
    {
        id: 4,
        sceneName: '小程序',
        icon: 'fa-th'
    },
    {
        id: 5,
        sceneName: '行业应用',
        icon: 'fa-building'
    },
    {
        id: 6,
        sceneName: '政务系统',
        icon: 'fa-gavel'
    },
    {
        id: 7,
        sceneName: '社交应用',
        icon: 'fa-users'
    },
    {
        id: 8,
        sceneName: '教育平台',
        icon: 'fa-book'
    },
    {
        id: 9,
        sceneName: '电商平台',
        icon: 'fa-shopping-cart'
    },
    {
        id: 10,
        sceneName: '数据展示',
        icon: 'fa-money-bill'
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
    