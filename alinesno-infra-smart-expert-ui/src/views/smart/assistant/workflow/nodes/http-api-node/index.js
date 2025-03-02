import HttpApiNodeVue from './index.vue'
import { AppNode, AppNodeModel } from '@/views/smart/assistant/workflow/common/appNode'

// 自定义节点的 view
class HttpApiNode extends AppNode {
    constructor(props) {
        super(props, HttpApiNodeVue);
    }
}

export default {
  type: 'http_api',
  model: AppNodeModel,
  view: HttpApiNode 
}