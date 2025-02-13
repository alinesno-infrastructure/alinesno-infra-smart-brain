import ReplyNodeVue from './index.vue'
import { AppNode, AppNodeModel } from '@/views/smart/assistant/workflow/common/appNode'

// 自定义节点的 view
class ReplyNode extends AppNode {
    constructor(props) {
        super(props, ReplyNodeVue);
    }
}

export default {
  type: 'reply',
  model: AppNodeModel,
  view: ReplyNode 
}