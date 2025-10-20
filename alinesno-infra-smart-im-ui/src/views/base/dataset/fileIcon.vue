<template>
    <span>
        <i :class="getFileTypeIcon(item?.fileType)" :style="{ color: getFileTypeColor(item?.fileType) }" class="mr-2"></i>
    </span>
</template>

<script setup>

const props = defineProps({
    item: {
        type: Object,
        required: true,
        default: () => ({})
    }
});

// 监控整个 fileItem 对象的变化（深度监听，因为是对象类型）
watch(
    () => props.item,
    (newVal, oldVal) => {
        console.log('文件对象发生变化：');
        console.log('新值：', newVal);
        console.log('旧值：', oldVal);
        // 可以在这里添加对象变化后的逻辑（如重新处理数据）
    },
    { deep: true } // 关键：对象需要开启深度监听
);

/**
 * 根据文件类型获取对应的Font Awesome 6.x图标
 * @param {string} fileType - 文件类型
 * @returns {string} 图标类名
 */
const getFileTypeIcon = (fileType) => {
    if (!fileType) return 'fa-solid fa-file';
    
    switch (fileType.toLowerCase()) {
        case 'xlsx':
        case 'xls':
            return 'fa-solid fa-file-excel';
        case 'csv':
            return 'fa-solid fa-file-csv';
        case 'txt':
            return 'fa-solid fa-file-lines';
        case 'pdf':
            return 'fa-solid fa-file-pdf';
        case 'doc':
        case 'docx':
            return 'fa-solid fa-file-word';
        case 'ppt':
        case 'pptx':
            return 'fa-solid fa-file-powerpoint';
        case 'md':
            return 'fa-solid fa-file-markdown';
        case 'jpg':
        case 'jpeg':
        case 'png':
        case 'gif':
        case 'bmp':
            return 'fa-solid fa-file-image';
        case 'mp3':
        case 'wav':
        case 'ogg':
            return 'fa-solid fa-file-audio';
        case 'mp4':
        case 'avi':
        case 'mov':
            return 'fa-solid fa-file-video';
        case 'zip':
        case 'rar':
        case '7z':
            return 'fa-solid fa-file-archive';
        case 'js':
        case 'css':
        case 'html':
        case 'json':
        case 'xml':
        case 'java':
        case 'py':
        case 'php':
        case 'cpp':
        case 'c':
            return 'fa-solid fa-file-code';
        default:
            return 'fa-solid fa-file';
    }
};

/**
 * 2. 根据文件类型获取对应颜色（使用Element Plus主题色系）
 * @param {string} fileType - 文件后缀
 * @returns {string} 颜色值（十六进制/RGB）
 */
const getFileTypeColor = (fileType) => {
    if (!fileType) return '#606266'; // 默认灰色
    const type = fileType.toLowerCase();
    const colorMap = {
        // 办公文档：主题色
        'xlsx': '#409EFF', // 蓝色（Excel）
        'xls': '#409EFF',
        'doc': '#409EFF', // 绿色（Word）
        'docx': '#409EFF',
        'ppt': '#E6A23C', // 橙色（PPT）
        'pptx': '#E6A23C',
        // 其他类型：功能色
        'pdf': '#F56C6C', // 红色（PDF）
        'csv': '#909399', // 灰色（CSV）
        'txt': '#909399', // 灰色（TXT）
        'md': '#1E40AF', // 深蓝色（Markdown）
        'jpg': '#F7BA1E', // 黄色（图片）
        'jpeg': '#F7BA1E',
        'png': '#F7BA1E',
        'gif': '#F7BA1E',
        'mp3': '#C062D4', // 紫色（音频）
        'mp4': '#4CAF50', // 深绿色（视频）
        'zip': '#7B68EE', // 靛蓝色（压缩包）
        'js': '#F0DB4F', // 黄色（代码）
        'css': '#264DE4', // 蓝色（代码）
        'html': '#E34F26' // 红色（代码）
    };
    return colorMap[type] || '#606266'; // 默认灰色
};

</script>

<style scoped lang="scss">

.mr-2 {
    font-size: 1.2rem;
    margin-right: 5px;
}

</style>