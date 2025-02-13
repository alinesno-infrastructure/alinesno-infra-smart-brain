import ImageUnderstandNodeVue from './index.vue'
import { AppNode, AppNodeModel } from '@/views/smart/assistant/workflow/common/appNode'

// 自定义节点的 view
class ImageUnderstandNode extends AppNode {
    constructor(props) {
        super(props, ImageUnderstandNodeVue);
    }
}


export default {
  type: 'image_understand',
  model: AppNodeModel,
  view: ImageUnderstandNode 
}