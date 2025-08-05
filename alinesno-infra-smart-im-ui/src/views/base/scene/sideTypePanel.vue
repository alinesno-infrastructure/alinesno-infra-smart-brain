<template>
    <div class="side-type-container">
        <div class="title">场景类型</div>
        <div class="type-list" v-if="sceneList.length > 0">
            <!-- Add All Types option -->
            <div
                :class="{ 'active': activeItem === '' }"
                @mouseover="handleMouseOver('')"
                @mouseout="handleMouseOut('')"
                @click="handleClick('')">
                <i class="fa-solid fa-box"></i> 全部类型
            </div> 

            <div
                v-for="item in sceneList"
                :key="item.id"
                :class="{ 'active': activeItem === item.code }"
                @mouseover="handleMouseOver(item.id)"
                @mouseout="handleMouseOut(item.id)"
                @click="handleClick(item.code)">
                <i :class="item.icon"></i> {{ item.sceneName }}
            </div>
        </div>
        <div class="empty-list" v-else>
            <el-empty image-size="100" description="当前未配置组织场景类型，未找到场景类型" />
        </div>
    </div>
</template>

<script setup>
import { ref , defineEmits } from 'vue';

import { supportScene } from '@/api/base/im/scene'
import { getAgentStoreType } from '@/api/base/im/store';

const emit = defineEmits(['selectType']);

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
    console.log('type id = ' + id)
    emit('selectType', id);
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
    position: absolute;
    height: 100%;

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
            padding-left: 15px;

            i{
                color: #d33233;
                width:22px
            }

            &:hover,
            &.active {
                background-color: var(--el-color-primary-light-8);
                color: var(--el-color-primary);
            }
        }
    }

    .empty-list {
        display: flex;
        margin-top:30px;
        padding: 20px;
        justify-content: center;
        align-items: center;
    }
}
</style>    