import { AppNode, AppNodeModel } from '@/views/smart/assistant/workflow/common/appNode';
import ConditionNodeVue from './index.vue';

// 自定义节点的 view
class ConditionNode extends AppNode {
    constructor(props) {
        super(props, ConditionNodeVue);
    }
}
const get_up_index_height = (condition_list, index) => {
  if (!condition_list || index <= 0) return 0;
  
  return condition_list
    .slice(0, index)
    .reduce((sum, item) => sum + (item.height || 100) + 8, 0);
};

// 自定义节点的 model
class ConditionModel extends AppNodeModel {

  refreshBranch() {
    // 更新节点连接边的path
    this.incoming.edges.forEach((edge) => {
      // 调用自定义的更新方案
      edge.updatePathByAnchor();
    });
    this.outgoing.edges.forEach((edge) => {
      edge.updatePathByAnchor();
    });
    // 重新计算锚点
    this.anchors = this.getDefaultAnchor();
    console.log('this.anchors = ' + JSON.stringify(this.anchors));
    // this.graphModel.refreshEdgeAnchor(this);
  }


  getDefaultAnchor() {
  const {
    id,
    x,
    y,
    width,
    height,
    properties: { branch_condition_list = [] }
  } = this;

  const showNode = this.properties.showNode === undefined ? true : this.properties.showNode;
  const anchors = [];
  
  // 左侧锚点（固定位置）
  anchors.push({
    x: x - width / 2 - 5,
    y: showNode ? y : y - 15,
    id: `${id}_left`,
    edgeAddable: false,
    type: 'left'
  });

  if (branch_condition_list.length > 0) {
    // 计算总内容高度（所有分支高度 + 间距）
    const totalContentHeight = branch_condition_list.reduce((sum, item) => sum + (item.height || 100) + 8, 0);

    // 起始Y坐标（从节点顶部开始计算）
    const startY = y - height / 2 + 55; // 55是第一个分支的顶部偏移量
    
    let currentY = startY;

        console.log('计算锚点 - 分支数量:', branch_condition_list.length);
    console.log('节点中心Y:', y);
    console.log('节点高度:', height);
    console.log('总内容高度:', totalContentHeight);
    
    for (let index = 0; index < branch_condition_list.length; index++) {
      const element = branch_condition_list[index];
      const branchHeight = element.height || 100;
      
      // 当前分支的中心Y坐标
      const branchCenterY = currentY + branchHeight / 2;

      console.log(`分支 ${index}: 高度=${branchHeight}, 中心Y=${branchCenterY}, 当前Y=${currentY}`);
      
      const item = {
        x: x + width / 2 + 10,
        y: showNode ? branchCenterY : y - 15,
        id: `${id}_${element.id}_right`,
        type: 'right'
      };
      
      anchors.push(item);
      
      // 移动到下一个分支的位置（当前分支高度 + 间距）
      currentY += branchHeight + 8;
    }
  }



  return anchors;
}

}

export default {
  type: 'condition',
  model: ConditionModel,
  view: ConditionNode 
}