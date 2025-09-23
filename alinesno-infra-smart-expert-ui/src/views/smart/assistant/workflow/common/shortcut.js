// import { getCurrentInstance } from 'vue'
// const { proxy } = getCurrentInstance();

import { MsgSuccess, MsgError, MsgConfirm } from '@/utils/message'
import { WorkflowType } from '@/utils/workflow' ;

let selected = null;

function translationNodeData(nodeData, distance) {
    nodeData.x += distance;
    nodeData.y += distance;
    if (nodeData.text) {
        nodeData.text.x += distance;
        nodeData.text.y += distance;
    }
    return nodeData;
}

function translationEdgeData(edgeData, distance) {
    if (edgeData.startPoint) {
        edgeData.startPoint.x += distance;
        edgeData.startPoint.y += distance;
    }
    if (edgeData.endPoint) {
        edgeData.endPoint.x += distance;
        edgeData.endPoint.y += distance;
    }
    if (edgeData.pointsList && edgeData.pointsList.length > 0) {
        edgeData.pointsList.forEach((point) => {
            point.x += distance;
            point.y += distance;
        });
    }
    if (edgeData.text) {
        edgeData.text.x += distance;
        edgeData.text.y += distance;
    }
    return edgeData;
}

const TRANSLATION_DISTANCE = 40;
let CHILDREN_TRANSLATION_DISTANCE = 40;

/**
 * 辅助：判断当前是否有可输入元素处于焦点
 * - 支持原生 input/textarea/select
 * - 支持 contenteditable 元素
 * - 支持 Element Plus 输入类组件（通过 class 名检测）
 */
function isInputFocused() {
    if (typeof document === 'undefined') return false;
    const activeEl = document.activeElement;
    if (!activeEl) return false;

    const tag = activeEl.tagName;
    if (tag === 'INPUT' || tag === 'TEXTAREA' || tag === 'SELECT') return true;

    if (activeEl.isContentEditable) return true;

    // 常见第三方组件输入内部 class（Element Plus）
    // el-input 内部输入元素 class 为 'el-input__inner'
    // el-textarea 也会存在 'el-textarea__inner'
    // el-select 的输入框可能在 .el-input__inner 内
    if (activeEl.classList && (
        activeEl.classList.contains('el-input__inner') ||
        activeEl.classList.contains('el-textarea__inner') ||
        activeEl.closest?.('.el-input__inner, .el-textarea__inner, .el-select .el-input') 
    )) {
        return true;
    }

    return false;
}

export function initDefaultShortcut(lf, graph) {
    const { keyboard } = lf;
    const {
        options: { keyboard: keyboardOptions }
    } = keyboard;

    // const vm = getCurrentInstance();
    // const { proxy } = vm;

    const copy_node = () => {
        CHILDREN_TRANSLATION_DISTANCE = TRANSLATION_DISTANCE;
        if (!keyboardOptions?.enabled) return true;
        if (graph.textEditElement) return true;
        if (isInputFocused()) return true;

        const { guards } = lf.options;
        const elements = graph.getSelectElements(false);
        const enabledClone = guards && guards.beforeClone ? guards.beforeClone(elements) : true;
        if (!enabledClone || (elements.nodes.length === 0 && elements.edges.length === 0)) {
            selected = null;
            return true;
        }
        const base_nodes = elements.nodes.filter(
            (node) => node.type === WorkflowType.Start || node.type === WorkflowType.Base
        );
        if (base_nodes.length > 0) {
            MsgError(base_nodes[0]?.properties?.stepName + '不能复制');
            return;
        }
        selected = elements;
        selected.nodes.forEach((node) => translationNodeData(node, TRANSLATION_DISTANCE));
        selected.edges.forEach((edge) => translationEdgeData(edge, TRANSLATION_DISTANCE));
        MsgSuccess('复制成功');
        return false;
    };

    const paste_node = () => {
        if (!keyboardOptions?.enabled) return true;
        if (graph.textEditElement) return true;
        if (isInputFocused()) return true; // 输入框聚焦时放行（让粘贴作用于输入框）

        if (selected && (selected.nodes || selected.edges)) {
            lf.clearSelectElements();
            const addElements = lf.addElements(selected, CHILDREN_TRANSLATION_DISTANCE);
            if (!addElements) return true;
            addElements.nodes.forEach((node) => lf.selectElementById(node.id, true));
            addElements.edges.forEach((edge) => lf.selectElementById(edge.id, true));
            selected.nodes.forEach((node) => translationNodeData(node, TRANSLATION_DISTANCE));
            selected.edges.forEach((edge) => translationEdgeData(edge, TRANSLATION_DISTANCE));
            CHILDREN_TRANSLATION_DISTANCE = CHILDREN_TRANSLATION_DISTANCE + TRANSLATION_DISTANCE;
        }
        return false;
    };

    const delete_node = () => {

        // 如果输入/富文本聚焦，放行（不进行节点删除）
        if (isInputFocused()) return true;

        const elements = graph.getSelectElements(true);
        lf.clearSelectElements();
        if (elements.nodes.length === 0 && elements.edges.length === 0) {
            return;
        }
        if (elements.edges.length > 0 && elements.nodes.length === 0) {
            elements.edges.forEach((edge) => lf.deleteEdge(edge.id));
            return;
        }
        const nodes = elements.nodes.filter((node) => ['start', 'base'].includes(node.type));
        if (nodes.length > 0) {
            MsgError(`${nodes[0].properties?.stepName}不能删除`);
            return;
        }
        
        MsgConfirm('提示', '确认要删除这些元素吗？', {
            confirmButtonText: '删除',
            confirmButtonClass: 'danger'
        }).then(() => {
            if (!keyboardOptions?.enabled) return true;
            if (graph.textEditElement) return true;
            if (isInputFocused()) return true;

            elements.edges.forEach((edge) => lf.deleteEdge(edge.id));
            elements.nodes.forEach((node) => lf.deleteNode(node.id));
        });

        return false;
    };

    graph.eventCenter.on('copy_node', copy_node);
    // 复制
    keyboard.on(['cmd + c', 'ctrl + c'], copy_node);
    // 粘贴
    keyboard.on(['cmd + v', 'ctrl + v'], paste_node);
    // undo
    keyboard.on(['cmd + z', 'ctrl + z'], () => {
        if (!keyboardOptions?.enabled) return true
        if (graph.textEditElement) return true
        if (isInputFocused()) return true
        lf.undo()
        return false
    });
    // redo
    keyboard.on(['cmd + y', 'ctrl + y'], () => {
        if (!keyboardOptions?.enabled) return true;
        if (graph.textEditElement) return true;
        if (isInputFocused()) return true;
        lf.redo();
        return false;
    });
    // delete
    keyboard.on(['backspace'], delete_node);

    // 关键修改：返回方法，供组件调用
    return {
        copy_node,
        paste_node,
        delete_node
    };

}