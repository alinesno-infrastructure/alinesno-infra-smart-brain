import { ElMessageBox, ElMessage } from 'element-plus';

export const MsgSuccess = (message) => {
    ElMessage.success({
        message: message,
        type: 'success',
        showClose: true,
        duration: 3000
    });
};

export const MsgInfo = (message) => {
    ElMessage.info({
        message: message,
        type: 'info',
        showClose: true,
        duration: 3000
    });
};

export const MsgWarning = (message) => {
    ElMessage.warning({
        message: message,
        type: 'warning',
        showClose: true,
        duration: 3000
    });
};

export const MsgError = (message) => {
    ElMessage.error({
        message: message,
        type: 'error',
        showClose: true,
        duration: 3000
    });
};

export const MsgAlert = (title, description, options = {}) => {
    const defaultOptions = {
        confirmButtonText: '确认',
        ...options
    };
    return ElMessageBox.alert(description, title, defaultOptions);
};

/**
 * 删除知识库
 * @param 参数 message: {title, description,type}
 */
export const MsgConfirm = (title, description, options = {}) => {
    const defaultOptions = {
        showCancelButton: true,
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        ...options
    };
    return ElMessageBox.confirm(description, title, defaultOptions);
};