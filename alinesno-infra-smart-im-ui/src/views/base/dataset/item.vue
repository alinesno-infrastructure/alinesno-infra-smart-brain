<template>
    <div class="dataset-item">
        <div class="knowledge-card">
            <!-- 状态标签 -->
            <div class="status-badge">
                <el-tag :type="statusTagType[kb.status || 'normal']"
                    :effect="kb.status === 'normal' || !kb.status ? 'dark' : 'plain'">
                    {{ statusTextMap[kb.status || 'normal'] }}
                </el-tag>
            </div>

            <div class="card-body">
                <h3 class="kb-title" @click="handleView(kb.id)">{{ kb.groupName }}</h3>
                <div class="description-container">
                    <p class="kb-description">{{ kb.description || '无描述信息' }}</p>
                </div>

                <div class="kb-meta">
                    <div class="meta-item">
                        <el-icon size="16">
                            <Document />
                        </el-icon>
                        <span>{{ kb.documentCount || 0 }} 篇文档</span>
                    </div>
                    <div class="meta-item">
                        <el-icon size="16">
                            <Clock />
                        </el-icon>
                        <span>{{ formatDate(kb.addTime) }}</span>
                    </div>
                </div>
            </div>

            <div class="card-actions">
                <LlmDisplay 
                    :modelId="kb.embeddingModelId" 
                    :modelType="'vector_model'"
                />
                <el-button size="small" text bg @click="handleEdit(kb)" icon="Edit">
                    编辑
                </el-button>

                <el-popover :visible="visible" placement="top" :width="180">
                    <p>确认删除当前知识库么?</p>
                    <div style="text-align: right; margin: 0">
                        <el-button size="small" text @click="visible = false">取消</el-button>
                        <el-button size="small" type="primary" @click="emit('delete', kb.id)">
                             确认 
                        </el-button>
                    </div>
                    <template #reference>
                        <el-button size="small" text danger bg @click="handleDelete(kb.id)" icon="Delete">
                            删除
                        </el-button>
                    </template>
                </el-popover>

            </div>
        </div>
    </div>
</template>

<script setup>
import { ElMessage, ElMessageBox, ElTag, ElIcon } from 'element-plus';
import { Document, Clock, Edit, Delete } from '@element-plus/icons-vue';

import LlmDisplay from './LlmDisplay'

// 不再使用路由
// import { useRouter } from 'vue-router';

// 定义 emits
const emit = defineEmits(['delete', 'edit', 'view']);

const visible = ref(false);
const props = defineProps({
    kb: {
        type: Object,
        required: true,
        default: () => ({})
    }
});

// 状态文本映射
const statusTextMap = {
    'normal': '正常',
    'archived': '已归档',
    'pending': '待审核'
};

// 状态标签样式映射
const statusTagType = {
    'normal': 'primary',
    'archived': 'info',
    'pending': 'warning'
};

// 格式化日期
const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
};

// 查看知识库（不跳转，发出事件）
const handleView = (id) => {
    // 发出 view 事件，父组件可选择监听并作相应处理
    emit('view', id);
};

// 编辑知识库（不跳转，发出事件）
const handleEdit = (kb) => {
    emit('edit', kb);
};

// 删除知识库（在子组件确认后发出删除事件）
const handleDelete = (id) => {
    visible.value = true;
};
</script>

<style lang="scss" scoped>
.dataset-item {
    position: relative;
    display: flex;
    flex-direction: column;
    line-height: 1.5;
    height: 100%;
    padding: 1.25rem;
    cursor: pointer;
    border: 1px solid #DFE2EA;
    user-select: none;
    background: #FFFFFF;
    border-radius: 0.75rem;
    transition: all 0.3s ease;

    &:hover {
        box-shadow: 0 4px 12px rgba(19, 51, 107, 0.1);
    }

    .status-badge {
        position: absolute;
        top: 1rem;
        right: 1rem;
    }

    .card-body {
        padding: 0;
        flex: 1;
        display: flex;
        flex-direction: column;
    }

    .kb-title {
        font-size: 16px;
        font-weight: 600;
        color: #1d2129;
        margin: 0 0 12px 0;
        cursor: pointer;
        transition: color 0.2s;
        padding-right: 60px;
        /* 为右侧状态标签预留空间 */

        &:hover {
            color: #4096ff;
        }
    }

    .description-container {
        flex: 1;
        /* 让描述区域占据剩余空间 */
        min-height: 60px;
        /* 固定最小高度，确保内容少时也有足够空间 */
        display: flex;
        align-items: flex-start;
    }

    .kb-description {
        color: #4e5969;
        font-size: 14px;
        line-height: 1.6;
        margin: 0 0 16px 0;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        /* 最多显示行数 */
        -webkit-box-orient: vertical;
        overflow: hidden;
        width: 100%;
    }

    .kb-meta {
        display: flex;
        flex-wrap: wrap;
        gap: 16px;
        color: #86909c;
        font-size: 12px;
        margin-top: auto;
        /* 推到容器底部，确保对齐 */
        padding-bottom: 8px;
        border-bottom: 1px solid #f2f3f5;
    }

    .meta-item {
        display: flex;
        align-items: center;
        gap: 4px;
    }

    .card-actions {
        display: flex;
        gap: 8px;
        padding: 12px 0px 0px;
        justify-content: flex-end;
        /* 靠右对齐操作按钮 */
    }
}

// 响应式调整
@media (max-width: 768px) {
    .dataset-item {
        min-height: auto;
        padding: 1rem;
    }

    .kb-title {
        font-size: 15px;
        padding-right: 50px;
    }

    .description-container {
        min-height: 50px;
    }
}
</style>
