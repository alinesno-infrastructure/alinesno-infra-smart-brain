export const useFlowProcessStore = defineStore('flowProcess', {
    state: () => ({
        // 用于存储流程节点的数组
        processNodes: []
    }),
    actions: {
        /**
         * 保存流程节点的方法
         * @param {string} nodeId - 节点的唯一标识
         * @param {Array} upNodeList - 该节点的上级节点列表
         */
        saveProcessNode(nodeId, upNodeList) {
            const newNode = {
                nodeId,
                upNodeList
            };
            // 查找是否已存在该节点
            const existingNodeIndex = this.processNodes.findIndex(node => node.nodeId === nodeId);
            if (existingNodeIndex!== -1) {
                // 如果存在，则更新该节点
                this.processNodes[existingNodeIndex] = newNode;
            } else {
                // 如果不存在，则添加新节点
                this.processNodes.push(newNode);
            }
        },
        /**
         * 根据节点 ID 获取上一个节点的方法
         * @param {string} nodeId - 要查找的节点的唯一标识
         * @returns {Object|null} - 返回上一个节点对象，如果没有则返回 null
         */
        getPreviousNodeById(nodeId) {
            const currentNodeIndex = this.processNodes.findIndex(node => node.nodeId === nodeId);
            if (currentNodeIndex > 0) {
                return this.processNodes[currentNodeIndex - 1];
            }
            return null;
        }
    }
});