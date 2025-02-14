import { AppNode, AppNodeModel } from '@/views/smart/assistant/workflow/common/appNode';
import ConditionNodeVue from './index.vue';

// 自定义节点的 view
class ConditionNode extends AppNode {
    constructor(props) {
        super(props, ConditionNodeVue);
    }
}

const get_up_index_height = (condition_list, index) => {
  return condition_list
    .filter((item, i) => i < index)
    .map((item) => item.height + 8)
    .reduce((x, y) => x + y, 0);
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
    this.graphModel.refreshEdgeAnchor(this);
  }

  getDefaultAnchor() {
    const {
      id,
      x,
      y,
      width,
      height,
      properties: { branch_condition_list }
    } = this;

    if (this.height === undefined) {
      this.height = 200;
    }

    const showNode = this.properties.showNode === undefined ? true : this.properties.showNode;
    const anchors = [];
    anchors.push({
      x: x - width / 2 - 5,
      y: showNode ? y : y - 15,
      id: `${id}_left`,
      edgeAddable: false,
      type: 'left'
    });

    if (branch_condition_list) {
      for (let index = 0; index < branch_condition_list.length; index++) {
        const element = branch_condition_list[index];
        const h = get_up_index_height(branch_condition_list, index);
        anchors.push({
          x: x + width / 2 - 10,
          y: showNode ? y - height / 2 + 75 + h + element.height / 2 : y - 15,
          id: `${id}_${element.id}_right`,
          type: 'right'
        });
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