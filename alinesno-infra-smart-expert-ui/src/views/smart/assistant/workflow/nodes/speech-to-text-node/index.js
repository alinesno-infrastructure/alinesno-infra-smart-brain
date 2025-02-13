import SpeechToTextVue from './index.vue'
import { AppNode, AppNodeModel } from '@/views/smart/assistant/workflow/common/appNode'

// 自定义节点的 view
class SpeechToTextView extends AppNode {
    constructor(props) {
        super(props, SpeechToTextVue);
    }
}


export default {
  type: 'speech_to_text',
  model: AppNodeModel,
  view: SpeechToTextView
}