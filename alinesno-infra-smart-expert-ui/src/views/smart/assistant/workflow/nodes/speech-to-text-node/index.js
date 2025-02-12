import SpeechToTextVue from './index.vue'
import { AppNode, AppNodeModel } from '@/views/smart/assistant/workflow/common/appNode'

class SpeechToTextNode extends AppNode {
    constructor(props) {
        super(props, SpeechToTextVue);
    }
}
export default {
  type: 'speech_to_text',
  model: AppNodeModel,
  view: SpeechToTextNode
}
