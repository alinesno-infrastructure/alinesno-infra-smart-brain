import ConditionNodeVue from './index.vue'
import { AppNode, AppNodeModel } from '@/views/smart/assistant/workflow/common/appNode'

// 自定义节点的 view
class ConditionNode extends AppNode {
    constructor(props) {
        super(props, ConditionNodeVue);
    }
}

export default {
  type: 'condition',
  model: AppNodeModel,
  view: ConditionNode 
}