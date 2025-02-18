// 引入所需模块
import StartNodeVue from './index.vue'
import { AppNode, AppNodeModel } from '@/views/smart/assistant/workflow/common/appNode'

// 定义 StartNode 类，继承自 AppNode
class StartNode extends AppNode {
    constructor(props) {
        super(props, StartNodeVue);
    }
}

// 导出默认对象
export default {
    type: 'start',
    model: AppNodeModel,
    view: StartNode
};