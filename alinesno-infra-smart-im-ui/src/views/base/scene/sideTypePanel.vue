<template>
    <div class="side-type-container">
        <div class="title">场景类型</div>
        <div class="type-list">
            <div
                v-for="item in sceneList"
                :key="item.id"
                :class="{ 'active': activeItem === item.id }"
                @mouseover="handleMouseOver(item.id)"
                @mouseout="handleMouseOut(item.id)"
                @click="handleClick(item.id)"
            >
                <i :class="item.icon"></i> {{ item.sceneName }}
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';

import { supportScene } from '@/api/base/im/scene'
import { getAgentStoreType } from '@/api/base/im/store';

const sceneList = ref([]);

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
};

const handleAgentStoreType = () => {
  supportScene().then(res => {
    console.log('res = ' + res);
    sceneList.value = res.data;
  })
};

handleAgentStoreType();

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

            i{
                color: #d33233;
                width:22px
            }

            &:hover,
            &.active {
                background: #f5f5f5;
            }
        }
    }
}
</style>    