import Clipboard from 'vue-clipboard3';
import { ElMessage } from 'element-plus';

/*
  复制粘贴
*/
export async function copyClick(info) {
    const { toClipboard } = await Clipboard();
    try {
        await toClipboard(info);
        ElMessage.success('复制成功');
    } catch (e) {
        console.error(e);
        ElMessage.error('复制失败');
    }
}