import TextToSpeechtVue from './index.vue'
import { AppNode, AppNodeModel } from '@/views/smart/assistant/workflow/common/appNode'

// 自定义节点的 view
class TextToSpeechtView extends AppNode {
    constructor(props) {
        super(props, TextToSpeechtVue);
    }
}


export default {
  type: 'text_to_speech',
  model: AppNodeModel,
  view: TextToSpeechtView
}