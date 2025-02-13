import KnowledgeSearchNodeVue from './index.vue'
import { AppNode, AppNodeModel } from '@/views/smart/assistant/workflow/common/appNode'

// 自定义节点的 view
class KnowledgeSearchNode extends AppNode {
    constructor(props) {
        super(props, KnowledgeSearchNodeVue);
    }
}


export default {
  type: 'knowledge_search',
  model: AppNodeModel,
  view: KnowledgeSearchNode 
}