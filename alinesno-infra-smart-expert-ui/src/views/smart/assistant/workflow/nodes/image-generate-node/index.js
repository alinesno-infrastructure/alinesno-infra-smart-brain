import ImageGenerateNodeVue from './index.vue'
import { AppNode, AppNodeModel } from '@/views/smart/assistant/workflow/common/appNode'

// 自定义节点的 view
class ImageGenerateNode extends AppNode {
    constructor(props) {
        super(props, ImageGenerateNodeVue);
    }
}


export default {
  type: 'image_generate',
  model: AppNodeModel,
  view: ImageGenerateNode 
}