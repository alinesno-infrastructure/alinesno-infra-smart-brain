<template>
    <div class="data-display-container" ref="container" @keydown.esc="closeContextMenu">
        <div>
            <div id="vditor" class="data-vditor-panel" @mouseup="handleSelection"></div>
        </div>

        <!-- 选中文本菜单 -->
        <div v-if="contextMenu.visible" class="context-menu" :style="menuPosition" @click.stop>
            <!-- 输入框区域 -->
            <div class="input-section">
                <el-input ref="promptInput" v-model="promptText" size="large" placeholder="输入您的指令..."
                    @keyup.enter="handleSendPrompt" />
                <el-button class="send-button" type="primary" size="large" text bg @click="handleSendPrompt">
                    <i class="fa-solid fa-paper-plane"></i>
                </el-button>
            </div>

            <!-- 快捷功能按钮 -->
            <div style="margin-top: 60px;">
                <div class="menu-item" @click="addContinuation">
                    <i class="fa-solid fa-plus"></i> 继写
                </div>
                <div class="menu-item" @click="improveWriting">
                    <i class="fa-solid fa-pen-fancy"></i> 改进写作
                </div>
                <div class="menu-item" @click="checkSpelling">
                    <i class="fa-solid fa-spell-check"></i> 检查拼写和语法
                </div>
                <div class="menu-item" @click="simplifyContent">
                    <i class="fa-solid fa-scissors"></i> 简化内容
                </div>
                <div class="menu-item" @click="translateContent">
                    <i class="fa-solid fa-language"></i> 翻译
                </div>
                <div class="menu-item" @click="summarizeContent">
                    <i class="fa-solid fa-file-contract"></i> 总结
                </div>
            </div>
        </div>

        <!-- AI 输出面板 -->
        <div v-if="showAIPanel" class="ai-output-panel" ref="aiOutputContent" :style="aiPanelPosition">
            <div class="ai-output-header">
                <span>AI 处理结果</span>
                <el-button class="close-button" type="text" @click="closeAIPanel">
                    <i class="fa-solid fa-xmark"></i>
                </el-button>
            </div>
            <div class="ai-output-content" ref="aiOutputContent">
                {{ aiOutput }}
            </div>
            <div class="ai-output-actions">
                <el-button type="primary" @click="replaceSelection">替换选中文本</el-button>
                <el-button @click="insertAfterSelection">插入到选中文本后</el-button>
                <el-button @click="copyToClipboard">复制到剪贴板</el-button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { onMounted, ref, computed, onUnmounted, nextTick, onBeforeUpdate, onUpdated } from 'vue';
import Vditor from 'vditor';
import 'vditor/dist/index.css';

const props = defineProps({
  // 文章数据
  articleData: {
    type: Object,
    required: true,
    default: () => ({
      title: '',
      content: ''
    })
  },
  // 是否自动聚焦编辑器
  autoFocus: {
    type: Boolean,
    default: false
  }
});

// 定义组件可能触发的事件
const emit = defineEmits([
  'update:articleData',  // 用于双向绑定
  'content-change',      // 内容变化事件
  'save'                 // 保存事件
]);

const contentEditor = ref(null);
const container = ref(null);
const promptInput = ref(null);
const aiOutputContent = ref(null);
const promptText = ref('');
const platItem = ref({
    title: '',
    description: ''
});

// 右键菜单状态
const contextMenu = ref({
    visible: false,
    clientX: 0,
    clientY: 0,
    selectedText: '',
    selectionRange: null // 存储选中文本的范围
});

// AI 输出面板状态
const showAIPanel = ref(false);
const aiOutput = ref('');
const aiPanelPosition = ref({});
let aiStreamInterval = null;

// 计算菜单位置
const menuPosition = computed(() => {
    if (!contextMenu.value.visible) return {};

    const menuWidth = 220;
    const menuHeight = 250;

    const viewportWidth = window.innerWidth;
    const viewportHeight = window.innerHeight;

    let left = contextMenu.value.clientX;
    let top = contextMenu.value.clientY;

    if (left + menuWidth > viewportWidth) {
        left = viewportWidth - menuWidth - 5;
    }

    if (top + menuHeight > viewportHeight) {
        top = viewportHeight - menuHeight - 5;
    }

    return {
        left: `${left}px`,
        top: `${top}px`
    };
});

const defaultMd = `
# 第一章 绪论

## 1.1 课题背景

[这是一段正文内容]

>引用他人的论点论据，用来佐证本文的背景及意义。
>
>——引文出处，适当增加尾注[^1]

[这是一段正文内容]

## 1.2 信息化管理

## 1.3 系统设计目的和内容

[^1]: 绪论尾注 1 的内容。
`;

// 保存当前选择的范围
const saveSelection = () => {
    const selection = window.getSelection();
    if (selection.rangeCount > 0) {
        const range = selection.getRangeAt(0);
        return range.cloneRange();
    }
    return null;
};

// 恢复之前保存的选择范围
const restoreSelection = (savedRange) => {
    if (!savedRange) return;

    const selection = window.getSelection();
    selection.removeAllRanges();
    selection.addRange(savedRange);
};

// 处理文本选中事件
const handleSelection = (e) => {
    const selection = window.getSelection();
    const selectedText = selection.toString().trim();

    if (selectedText) {
        // 保存选中文本的范围
        const savedRange = saveSelection();

        // 获取选中文本的位置
        const range = selection.getRangeAt(0);
        const rect = range.getBoundingClientRect();

        contextMenu.value = {
            visible: true,
            clientX: rect.left,
            clientY: rect.bottom + 5,
            selectedText: selectedText,
            selectionRange: savedRange // 存储选择范围
        };
        promptText.value = '';

        // 恢复选择状态
        nextTick(() => {
            restoreSelection(savedRange);
            promptInput.value?.focus();
        });
    } else {
        closeContextMenu();
    }
};

// 关闭菜单
const closeContextMenu = () => {
    if (contextMenu.value.visible) {
        contextMenu.value.visible = false;
    }
};

// 处理发送prompt
const handleSendPrompt = async () => {
    if (!promptText.value.trim()) return;

    closeContextMenu();
    showAIPanel.value = true;
    aiOutput.value = '';

    // 设置AI面板位置
    aiPanelPosition.value = {
        left: `${contextMenu.value.clientX}px`,
        top: `${contextMenu.value.clientY + 5}px`
    };

    // 模拟SSE流式输出
    simulateSSEStream();
};

// 模拟SSE流式输出
const simulateSSEStream = () => {
    const responses = [
        "我正在分析您选中的文本...\n",
        "根据您的要求，我将改进以下内容：\n",
        `${contextMenu.value.selectedText}\n`,
        "改进后的版本如下：\n",
        "这是一个经过优化的文本版本，更加简洁明了。",
        " 我调整了句子结构，使其更易读。",
        " 同时保持了原文的核心意思。",
        " 您可以根据需要进一步修改。"
    ];

    let index = 0;
    aiStreamInterval = setInterval(() => {
        if (index < responses.length) {
            aiOutput.value += responses[index];
            index++;

            // 自动滚动到底部
            nextTick(() => {
                if (aiOutputContent.value) {
                    aiOutputContent.value.scrollTop = aiOutputContent.value.scrollHeight;
                }
            });

            if (index >= responses.length) {
                clearInterval(aiStreamInterval);
                nextTick(() => {
                    // 获取面板实际高度
                    const panelHeight = aiOutputContent.value.offsetHeight + 100; // 加上头部和底部高度
                    const viewportHeight = window.innerHeight;
                    const menuTop = contextMenu.value.clientY + 5;

                    if (menuTop + panelHeight > viewportHeight) {
                        aiPanelPosition.value.top = `${viewportHeight - panelHeight - 5}px`;
                    }
                });
            }

        } else {
            clearInterval(aiStreamInterval);
        }
    }, 300);
};

// 监听 articleData 变化
watch(() => props.articleData, (newVal) => {
  if (contentEditor.value && newVal.content !== contentEditor.value.getValue()) {
    contentEditor.value.setValue(newVal.content || '');
  }
}, { deep: true });

// 关闭AI面板
const closeAIPanel = () => {
    showAIPanel.value = false;
    clearInterval(aiStreamInterval);
};

// 替换选中文本
const replaceSelection = () => {
    if (contentEditor.value) {
        // 获取当前光标位置或选中范围
        const selection = window.getSelection();
        if (!selection.rangeCount) return;

        const range = selection.getRangeAt(0);
        const selectedText = range.toString();

        console.log('range = ' + range + ' , selectedText = ' + selectedText);

        if (selectedText) {
            // 使用Vditor的API替换选中文本
            const currentValue = contentEditor.value.getValue();
            const newValue = currentValue.replace(selectedText, aiOutput.value);

            contentEditor.value.setValue(newValue);
            emit('content-change', newValue);

            // 尝试恢复选中状态（可能需要更复杂的逻辑）
            // setTimeout(() => {
            //     const newPos = currentValue.indexOf(selectedText);
            //     if (newPos !== -1) {
            //         contentEditor.value.setSelection({
            //             start: newPos,
            //             end: newPos + aiOutput.value.length
            //         });
            //     }
            // }, 100);

        } else {
            // 如果没有选中文本，在光标位置插入
            const cursorPos = contentEditor.value.getCursorPosition();
            const currentValue = contentEditor.value.getValue();
            const newValue = currentValue.slice(0, cursorPos) + aiOutput.value + currentValue.slice(cursorPos);

            contentEditor.value.setValue(newValue);
            emit('content-change', newValue);

            // TODO 移动光标到插入内容后
            // contentEditor.value.setCursorPosition(cursorPos + aiOutput.value.length);
        }

        closeAIPanel();
    }
};

// 插入到选中文本后
const insertAfterSelection = () => {
    if (contentEditor.value) {
        // 获取当前光标位置或选中范围
        const selection = window.getSelection();
        if (!selection.rangeCount) return;

        const range = selection.getRangeAt(0);
        const selectedText = range.toString();

        if (selectedText) {
            // 在选中文本后插入
            const currentValue = contentEditor.value.getValue();
            const insertPos = currentValue.indexOf(selectedText) + selectedText.length;
            const newValue = currentValue.slice(0, insertPos) + "\n\n" + aiOutput.value + currentValue.slice(insertPos);

            contentEditor.value.setValue(newValue);
            emit('content-change', newValue);

            // TODO 移动光标到插入内容后
            // contentEditor.value.setCursorPosition(insertPos + aiOutput.value.length + 2);
        } else {
            // 如果没有选中文本，在光标位置插入
            const cursorPos = contentEditor.value.getCursorPosition();
            const currentValue = contentEditor.value.getValue();
            const newValue = currentValue.slice(0, cursorPos) + "\n\n" + aiOutput.value + currentValue.slice(cursorPos);

            contentEditor.value.setValue(newValue);
            emit('content-change', newValue);

            // TODO 移动光标到插入内容后
            // contentEditor.value.setCursorPosition(cursorPos + aiOutput.value.length + 2);
        }

        closeAIPanel();
    }
};

// 复制到剪贴板
const copyToClipboard = async () => {
    try {
        await navigator.clipboard.writeText(aiOutput.value);
        ElMessage.success('已复制到剪贴板');
    } catch (err) {
        ElMessage.error('复制失败');
    }
};

// 菜单项功能
const improveWriting = () => {
    promptText.value = "请改进以下文本的写作质量:";
    nextTick(() => {
        promptInput.value?.focus();
    });
};
// 继写
const addContinuation = () => {
    promptText.value = "请续写以下文本:";
    nextTick(() => {
        promptInput.value?.focus();
    });
};

const checkSpelling = () => {
    promptText.value = "请检查以下文本的拼写和语法:";
    nextTick(() => {
        promptInput.value?.focus();
    });
};

const simplifyContent = () => {
    promptText.value = "请简化以下内容:";
    nextTick(() => {
        promptInput.value?.focus();
    });
};

const translateContent = () => {
    promptText.value = "请将以下内容翻译为中文:";
    nextTick(() => {
        promptInput.value?.focus();
    });
};

const summarizeContent = () => {
    promptText.value = "请总结以下内容:";
    nextTick(() => {
        promptInput.value?.focus();
    });
};

// 点击页面其他地方关闭菜单
const handleClickOutside = (e) => {
    if (!container.value?.contains(e.target)) {
        closeContextMenu();
    }
};

const handleKeyDown = (e) => {
    if (e.key === 'Escape') {
        if (contextMenu.value.visible) {
            closeContextMenu();
        } else if (showAIPanel.value) {
            closeAIPanel();
        }
    } else if (e.ctrlKey && e.key === '/') {
        // 处理 Ctrl+/
        e.preventDefault();
        
        const selection = window.getSelection();
        const selectedText = selection.toString().trim();
        
        if (!selectedText) {
            // 获取当前光标位置
            const range = selection.getRangeAt(0);
            const rect = range.getBoundingClientRect();
            
            // 如果光标在行首，rect可能没有宽度/高度，需要特殊处理
            const cursorX = rect.left || (range.startContainer && range.startContainer.nodeType === Node.TEXT_NODE ? 
                getCursorPixelPosition(range.startContainer, range.startOffset) : 100);
            const cursorY = rect.top || 100;

            contextMenu.value = {
                visible: true,
                clientX: cursorX,
                clientY: cursorY + 20, // 稍微下移，避免遮挡光标
                selectedText: '',
                selectionRange: range.cloneRange() // 保存当前光标位置
            };
            
            promptText.value = '';
            
            nextTick(() => {
                promptInput.value?.focus();
            });
        }
    }
};

// 辅助函数：获取文本节点中光标的像素位置
function getCursorPixelPosition(textNode, offset) {
    const range = document.createRange();
    range.setStart(textNode, offset);
    range.setEnd(textNode, offset);
    return range.getBoundingClientRect().left;
}

// 在更新前保存选择
let savedRangeBeforeUpdate = null;

onBeforeUpdate(() => {
    if (contextMenu.value.visible && contextMenu.value.selectionRange) {
        savedRangeBeforeUpdate = contextMenu.value.selectionRange;
    }
});

// 更新后恢复选择
onUpdated(() => {
    if (savedRangeBeforeUpdate) {
        nextTick(() => {
            restoreSelection(savedRangeBeforeUpdate);
            savedRangeBeforeUpdate = null;
        });
    }
});

onMounted(() => {

    contentEditor.value = new Vditor('vditor', {
        height: 'calc(100vh - 120px)',
        width: '100%',
        mode: "wysiwyg",
        cdn: '/vditor',
        toolbarConfig: {
            pin: true,
        },
        cache: {
            enable: false,
        },
        after: () => {
            contentEditor.value.setValue(defaultMd);
        },
        input: (content) => {
            // 编辑器内容变化时触发
            emit('content-change', content);
            emit('update:articleData', {
                ...props.articleData,
                content: content
            });
        },
        after: () => {
            contentEditor.value.setValue(props.articleData.content || '');
            if (props.autoFocus) {
                nextTick(() => {
                contentEditor.value.focus();
                });
            }
        }
    });

    document.addEventListener('click', handleClickOutside);
    document.addEventListener('scroll', closeContextMenu, true);
    document.addEventListener('keydown', handleKeyDown);
});


onUnmounted(() => {
    document.removeEventListener('click', handleClickOutside);
    document.removeEventListener('scroll', closeContextMenu, true);
    document.removeEventListener('keydown', handleKeyDown);
    clearInterval(aiStreamInterval);
});

const setPlanItemContentData = (content) => {
    contentEditor.value.setValue(content);
}

// 修改后的 setData 方法
const setData = (data) => {
  if (contentEditor.value) {
    contentEditor.value.setValue(data == null ? '' : data);
    // 通知父组件内容变化
    emit('content-change', data);
    emit('update:articleData', {
      ...props.articleData,
      content: data
    });
  }
}

// 修改后的 setPlanItem 方法
const setPlanItem = (planItemVal) => {
  console.log('setPlanItem = ' + JSON.stringify(planItemVal));
  emit('update:articleData', {
    ...props.articleData,
    title: planItemVal.label,
    description: planItemVal.description
  });
}

// 暴露方法给父组件
defineExpose({
  setData,
  setPlanItem,
  setPlanItemContentData,
  focusEditor: () => contentEditor.value?.focus()
});

</script>

<style lang="scss" scoped>
.data-display-container {
    height: 100%;
    position: relative;

    .time-data-range-container {
        display: flex;
        flex-direction: column;
        gap: 8px;
        margin-bottom: 20px;

        .title {
            font-size: 21px;
            font-weight: bold;
        }

        .description {
            font-size: 13px;
            color: #a5a5a5;
        }
    }
}

.context-menu {
    position: fixed;
    background: white;
    border: 1px solid #ddd;
    border-radius: 6px;
    box-shadow: 0 2px 15px rgba(0, 0, 0, 0.15);
    z-index: 1000;
    width: 405px;
    padding: 8px 0;

    .input-section {
        display: flex;
        position: absolute;
        border-radius: 7px;
        padding: 10px;
        width: 400px;
        background: #fff;

        .send-button {
            margin-left: 10px;
        }
    }

    .menu-item {
        padding: 8px 15px;
        cursor: pointer;
        display: flex;
        align-items: center;
        gap: 10px;
        font-size: 13px;

        &:hover {
            background-color: #f5f5f5;
        }

        i {
            width: 18px;
            text-align: center;
            color: #555;
            font-size: 14px;
        }
    }
}

.ai-output-panel {
    position: fixed;
    width: 500px;
    max-height: 60vh;
    background: white;
    border: 1px solid #ddd;
    border-radius: 8px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
    z-index: 1001;
    display: flex;
    flex-direction: column;

    .ai-output-header {
        padding: 12px 16px;
        border-bottom: 1px solid #eee;
        display: flex;
        justify-content: space-between;
        align-items: center;
        font-weight: bold;
        background: #f9f9f9;
        border-radius: 8px 8px 0 0;

        .close-button {
            padding: 0;
            min-height: auto;
        }
    }

    .ai-output-content {
        padding: 10px;
        flex: 1;
        overflow-y: auto;
        white-space: pre-wrap;
        font-size: 14px;
        line-height: 1.6;
    }

    .ai-output-actions {
        padding: 12px 16px;
        border-top: 1px solid #eee;
        display: flex;
        gap: 10px;
        justify-content: flex-end;
        background: #f9f9f9;
        border-radius: 0 0 8px 8px;
    }
}
</style>

<style>
.data-vditor-panel .vditor-toolbar, .data-vditor-panel .vditor-reset {
    padding-left: 10px !important;
    padding-right: 10px !important;
}
</style>