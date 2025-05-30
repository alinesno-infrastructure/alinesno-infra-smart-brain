<!-- components/FunctionPanel.vue -->
<template>
  <div class="function-panel" :class="panelClass">
    <div class="title" v-if="showTitle">
      <i :class="titleIcon"></i> 
      {{ title }}
    </div>
    
    <div class="add-button" v-if="showAddButton">
      <router-link :to="addButtonLink">
        <el-button 
          :type="addButtonType" 
          :text="addButtonText" 
          :bg="addButtonBg" 
          :size="addButtonSize" 
          :style="{ width: '100%' }"
        >
          <i :class="addButtonIcon"></i> 
          {{ addButtonLabel }}
        </el-button>
      </router-link>
    </div>

    <div class="function-list">
      <div 
        v-for="(item, index) in list" 
        :key="index" 
        class="function-item" 
        @click="handleItemClick(item)" 
        :class="{ active: item.isSelected }"
      >
        <div class="item-icon">
          <i :class="item.icon || defaultIcon"></i>
        </div>  

        <div class="item-content">
          <div class="item-header">
            <span class="label">{{ item.label }}</span>
            <span v-if="item.status" class="status-dot" :style="{ backgroundColor: statusColors[item.status] }"></span>
          </div>
          
          <div class="item-desc" v-if="item.desc">{{ item.desc }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';

const props = defineProps({
  // 基础配置
  title: { type: String, default: '功能面板' },
  titleIcon: { type: String, default: 'fa-solid fa-bars' },
  panelClass: { type: String, default: '' },
  
  // 按钮配置
  showAddButton: { type: Boolean, default: true },
  addButtonLabel: { type: String, default: '新建任务' },
  addButtonIcon: { type: String, default: 'fa-solid fa-plus' },
  addButtonType: { type: String, default: 'primary' },
  addButtonBg: { type: String },
  addButtonSize: { type: String, default: 'large' },
  addButtonLink: { type: String, default: '/scene/newTask' },

  // 数据配置
  list: {
    type: Array,
    default: () => [{
      label: '默认功能',
      icon: 'fa-solid fa-circle-info',
      desc: '请通过 props 传入功能列表',
      status: 'waiting', // waiting/done/error
      isSelected: false,
      link: '#',
      onClick: () => {}
    }]
  },

  // 样式配置
  activeColor: { type: String, default: '#409EFF' },
  defaultIcon: { type: String, default: 'fa-solid fa-circle' },
  showStatusDot: { type: Boolean, default: true }
});

const emits = defineEmits([
  'item-click',
  'add-click',
  'router-navigate'
]);

const router = useRouter();

const statusColors = computed(() => ({
  waiting: '#f0f2f5',
  done: props.activeColor,
  error: '#ff4d4f'
}));

const handleItemClick = (item) => {
  // 执行点击事件回调
  item.onClick?.();
  
  // 触发外部事件
  emits('item-click', item);

  // 路由跳转处理
  if (item.link) {
    router.push({
      path: item.link,
      query: { sceneId: props.sceneId } // 支持传递场景ID
    }).then(() => emits('router-navigate', item.link));
  }
};
</script>

<style lang="scss" scoped>
.function-panel {
  padding: 15px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
  height: calc(100vh - 120px);
  overflow-y: auto;

  .title {
    display: flex;
    align-items: center;
    font-size: 16px;
    font-weight: 500;
    color: #333;
    margin-bottom: 20px;
    gap: 8px;
    
    i {
      font-size: 1.2em;
    }
  }

  .add-button {
    margin-bottom: 20px;

    .el-button {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      transition: transform 0.2s;
      
      &:hover {
        transform: translateX(4px);
      }
    }
  }

  .function-list {
    display: flex;
    flex-direction: column;
    gap: 12px;

    .function-item {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 12px 16px;
      border-radius: 6px;
      cursor: pointer;
      transition: all 0.3s ease;
      font-size: 14px;
      color: #666;

      &:hover {
        background: #f6f9ff;
        transform: translateX(4px);
      }

      &.active {
        background: var(--active-color, #409EFF);
        color: #fff;
        box-shadow: 0 0 8px rgba(var(--el-color-primary-light-3), 0.3);
        
        .item-icon i {
          color: #fff;
        }
      }

      .item-icon {
        min-width: 36px;
        display: flex;
        align-items: center;
        justify-content: center;
        
        i {
          font-size: 1.1em;
          color: #999;
        }
      }

      .item-content {
        flex: 1;
        display: flex;
        flex-direction: column;
        gap: 4px;

        .item-header {
          display: flex;
          align-items: center;
          gap: 8px;
          font-weight: 500;

          .status-dot {
            width: 8px;
            height: 8px;
            border-radius: 50%;
            margin-top: 2px;
          }
        }

        .item-desc {
          font-size: 12px;
          color: #888;
          line-height: 1.4;
        }
      }
    }
  }
}
</style>