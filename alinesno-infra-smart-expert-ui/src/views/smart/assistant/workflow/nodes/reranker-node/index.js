import RerankerNodeVue from './index.vue'
import { AppNode, AppNodeModel } from '@/views/smart/assistant/workflow/common/appNode'

// 自定义节点的 view
class RerankerNode extends AppNode {
    constructor(props) {
        super(props, RerankerNodeVue);
    }
}

export default {
  type: 'reranker',
  model: AppNodeModel,
  view: RerankerNode 
}