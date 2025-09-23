// 引入所需模块
import EndNodeVue from './index.vue'
import { AppNode, AppNodeModel } from '@/views/smart/assistant/workflow/common/appNode'

// 定义 StartNode 类，继承自 AppNode
class EndNode extends AppNode {
    constructor(props) {
        super(props, EndNodeVue);
    }
}

// 导出默认对象
export default {
    type: 'end',
    model: AppNodeModel,
    view: EndNode
};