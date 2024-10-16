import { defineStore } from 'pinia';
import { getStartNode } from '@/utils/nodeUtil';

const flowNodeStore = defineStore(
  'flowNode',
  {
    state: () => ({
      // 节点数据数组
      nodes: [getStartNode()],
      // 缩略图
      mapImg: ''
    }),
    getters: {
      // 可以在这里定义任何需要的 getter
      currentNode: (state) => state.nodes,
      currentMapImage: (state) => state.mapImg
    },
    actions: {
      /**
       * 通过nodeId获取到node信息
       */
      getNodeById(nodeId) {
        return this.nodes.find(node => node.id === nodeId);
      },
      /**
       * 设置或替换节点
       * @param {Object} node - 新的或要替换的节点对象
       */
      setNode(node) {
        // 查找具有相同 ID 的现有节点
        const index = this.nodes.findIndex(existingNode => existingNode.id === node.id);

        if (index !== -1) {
          // 如果找到具有相同 ID 的节点，则替换它
          this.nodes.splice(index, 1, node);
        } else {
          // 如果没有找到具有相同 ID 的节点，则添加新的节点
          this.nodes.push(node);
        }

        console.info('Updated nodes: ', JSON.stringify(this.nodes));
      },
      /**
       * 添加节点
       * @param {Object} data - 包含新节点信息的对象
       */
      addNode(data) {
        const { addNode, currNode } = data;

        if (currNode && currNode.id) {
          const currentIndex = this.nodes.findIndex(node => node.id === currNode.id);
          if (currentIndex !== -1) {
            this.nodes.splice(currentIndex + 1, 0, addNode);
          } else {
            this.nodes.push(addNode);
          }
        } else {
          this.nodes.push(addNode);
        }

        console.info('Updated nodes: ', JSON.stringify(this.nodes));
      },
      /**
       * 删除节点
       * @param {string|number} id - 要删除的节点ID
       */
      removeNode(id) {
        const index = this.nodes.findIndex(node => node.id === id);

        if (index !== -1) {
          this.nodes.splice(index, 1);
          console.info(`Node with ID ${id} removed. Updated nodes: `, JSON.stringify(this.nodes));
        } else {
          console.warn(`Node with ID ${id} not found.`);
        }
      },
      /**
       * 修改节点名称
       * @param {string|number} id - 要更新的节点ID
       * @param {string} newName - 新的节点名称
       */
      updateNodeName(id, newName) {
        const index = this.nodes.findIndex(node => node.id === id);

        if (index !== -1) {
          // 创建一个新的节点对象，或者直接在原对象上修改属性
          const updatedNode = { ...this.nodes[index], name: newName };
          this.nodes.splice(index, 1, updatedNode);
          console.info(`Node with ID ${id} updated. New name: ${newName}. Updated nodes: `, JSON.stringify(this.nodes));
        } else {
          console.warn(`Node with ID ${id} not found.`);
        }
      } ,
      /**
       * 获取所有的节点
       */
      getAllNodes() {
        return this.nodes;
      },
    }
  }
);

export default flowNodeStore;