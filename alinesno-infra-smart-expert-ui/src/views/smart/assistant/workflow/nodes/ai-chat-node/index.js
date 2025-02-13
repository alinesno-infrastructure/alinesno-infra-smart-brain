import ChatNodeVue from './index.vue'
import { AppNode, AppNodeModel } from '@/views/smart/assistant/workflow/common/appNode'

// 自定义节点的 view
class ChatNode extends AppNode {
    constructor(props) {
        super(props, ChatNodeVue);
    }
}


export default {
  type: 'ai_chat',
  model: AppNodeModel,
  view: ChatNode 
}