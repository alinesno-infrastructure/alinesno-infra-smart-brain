import { AppNode, AppNodeModel } from '@/views/smart/assistant/workflow/common/appNode';
import ConditionNodeVue from './index.vue';

// 自定义节点的 view
class ConditionNode extends AppNode {
    constructor(props) {
        super(props, ConditionNodeVue);
    }
}

// 计算指定索引前所有分支的总高度（含间距）- 工具函数
const getUpIndexTotalHeight = (conditionList, index) => {
    if (!conditionList || index <= 0) return 0;
    // 累加前 index 个分支的高度 + 每个分支间的 8px 间距
    return conditionList
        .slice(0, index)
        .reduce((sum, item) => sum + (item.height || 100) + 8, 0);
};

// 自定义节点的 model
class ConditionModel extends AppNodeModel {

    // // 刷新分支锚点和边路径
    // refreshBranch() {
    //     // 先更新所有边的路径（基于最新锚点）
    //     [...this.incoming.edges, ...this.outgoing.edges].forEach(edge => {
    //         edge.updatePathByAnchor();
    //     });
    //     // 重新计算锚点并更新
    //     this.anchors = this.getDefaultAnchor();
    //     // 通知图模型刷新当前节点的锚点关联
    //     if (this.graphModel) {
    //         this.graphModel.refreshEdgeAnchor(this);
    //     }
    // }

    // 自定义清空锚点方法（核心：保持数组引用不变）
    clearAnchors() {
        if (Array.isArray(this.anchors)) {
            // 清空数组但保留原引用（关键：避免触发Proxy的set陷阱）
            this.anchors.splice(0, this.anchors.length);
            // 触发更新事件（根据框架特性选择，可选）
            if (this.emit) {
                this.emit('anchors:clear', this.anchors);
            }
        } else {
            // 若anchors未初始化，创建空数组
            this.anchors = [];
        }
    }

    // 添加新锚点（批量）
    addAnchors(newAnchors) {
        if (Array.isArray(this.anchors) && Array.isArray(newAnchors)) {
            // 批量添加新锚点（修改原数组内容）
            this.anchors.push(...newAnchors);
            // 触发更新事件
            if (this.emit) {
                this.emit('anchors:add', newAnchors);
            }
        }
    }

    // 刷新分支锚点和边路径
    refreshBranch() {
        try {
            // 1. 更新所有边的路径（基于最新锚点）
            [...this.incoming.edges, ...this.outgoing.edges].forEach(edge => {
                if (edge.updatePathByAnchor) edge.updatePathByAnchor();
            });
            
            // 2. 计算最新锚点
            const newAnchors = this.getDefaultAnchor();
            
            // 3. 先清空旧锚点，再添加新锚点（使用自定义方法）
            this.clearAnchors();
            this.addAnchors(newAnchors);
            
            // 4. 通知图模型刷新锚点关联
            if (this.graphModel && this.graphModel.refreshEdgeAnchor) {
                this.graphModel.refreshEdgeAnchor(this);
            }
        } catch (err) {
            console.error("刷新锚点失败：", err);
        }
    }

    // 计算默认锚点（核心修复：高度与位置匹配）
    getDefaultAnchor() {
        const {
            id,
            x, // 节点中心X坐标
            y, // 节点中心Y坐标
            width, // 节点宽度
            height: nodeTotalHeight, // 节点总高度（从props获取）
            properties: { 
                branch_condition_list = [], // 分支配置（含高度）
                showNode = true // 是否显示节点（控制锚点显隐）
            }
        } = this;

        const anchors = [];
        // 1. 左侧入参锚点（固定在节点左侧中心，与分支无关）
        anchors.push({
            x: x - width / 2 - 5, // 节点左侧外移5px（避免与节点重叠）
            y: y, // 左侧锚点固定在节点垂直中心
            id: `${id}_left`,
            edgeAddable: false, // 禁止从左侧添加边
            type: 'left'
        });

        // 2. 右侧出参锚点（每个分支对应一个锚点）
        if (branch_condition_list.length === 0) {
            return anchors; // 无分支时仅返回左侧锚点
        }

        // -------------------------- 核心修复1：计算正确的起始Y坐标 --------------------------
        // 节点内部结构偏移：标题栏高度（假设50px）+ 上下内边距（各10px）= 70px（可根据实际UI调整）
        const nodeInnerOffset = 70; 
        // 分支区域的起始Y坐标 = 节点顶部Y坐标 + 内部偏移
        // 节点顶部Y = 节点中心Y - 节点总高度/2
        const branchStartY = y - nodeTotalHeight / 2 + nodeInnerOffset;

        // -------------------------- 核心修复2：校验分支总高度与节点高度匹配 --------------------------
        // 所有分支的总高度（含分支间8px间距）
        const allBranchesTotalHeight = branch_condition_list
            .reduce((sum, item) => sum + (item.height || 100) + 8, 0) - 8; // 最后一个分支无间距，减8px
        // 若分支总高度超过节点剩余空间，强制调整节点高度（避免溢出）
        const requiredNodeHeight = nodeInnerOffset + allBranchesTotalHeight + 20; // 20px为底部冗余
        if (requiredNodeHeight > nodeTotalHeight && this.setHeight) {
            this.setHeight(requiredNodeHeight); // 同步更新节点高度
        }

        // -------------------------- 核心修复3：遍历分支计算锚点 --------------------------
        branch_condition_list.forEach((branch, index) => {
            const branchHeight = branch.height || 100; // 分支实际高度（默认100px）
            // 当前分支的顶部Y坐标 = 起始Y + 前序分支总高度（含间距）
            const currentBranchTopY = branchStartY + getUpIndexTotalHeight(branch_condition_list, index);
            // 当前分支的中心Y坐标（锚点Y坐标）= 分支顶部Y + 分支高度/2
            const branchCenterY = currentBranchTopY + branchHeight / 2;

            // 右侧锚点配置（每个分支对应一个锚点）
            anchors.push({
                x: x + width / 2 + 10, // 节点右侧外移10px（方便连接）
                y: showNode ? branchCenterY : y, // 隐藏节点时锚点归位到中心
                id: `${id}_${branch.id || index}_right`, // 用分支ID或索引确保唯一
                type: 'right',
                edgeAddable: true // 允许从右侧添加边
            });

            // 调试日志（可删除）
            console.log(`分支${index}（${branch.type || 'ELSE'}）：` + `顶部Y=${currentBranchTopY.toFixed(1)}, 中心Y=${branchCenterY.toFixed(1)}, 高度=${branchHeight}`);
        });

        return anchors;
    }
}

export default {
    type: 'condition',
    model: ConditionModel,
    view: ConditionNode
};