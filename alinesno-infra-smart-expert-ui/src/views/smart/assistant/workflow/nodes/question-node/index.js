import QuestionNodeVue from './index.vue'
import { AppNode, AppNodeModel } from '@/views/smart/assistant/workflow/common/appNode'

// 自定义节点的 view
class QuestionNode extends AppNode {
    constructor(props) {
        super(props, QuestionNodeVue);
    }
}

export default {
  type: 'question',
  model: AppNodeModel,
  view: QuestionNode 
}