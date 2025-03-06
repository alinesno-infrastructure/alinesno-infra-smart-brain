import { HtmlResize } from '@logicflow/extension';
import ElementPlus from 'element-plus'
import * as ElementPlusIcons from '@element-plus/icons-vue'
import { h as lh } from '@logicflow/core';
import { createApp, h } from 'vue'
import { WorkflowType } from '@/utils/workflow';
import { nodeDict } from './data'
import { isActive, connect, disconnect } from './teleport'
import { useFlowProcessStore } from '@/store/modules/flowProcess';

// 定义 AppNode 类，继承自 HtmlResize.view
class AppNode extends HtmlResize.view {

  constructor(props, VueNode) {
    super(props);
    this.component = VueNode;
    this.isMounted = false;

    if (props.model.properties.noRender) {
      delete props.model.properties.noRender;
    } else {
      const filterNodes = props.graphModel.nodes.filter((v) => v.type === props.model.type);
      if (filterNodes.length - 1 > 0) {
        getNodesName(filterNodes.length - 1);
      }
    }

    function getNodesName(num) {
      let number = num;
      const name = props.model.properties.stepName + number;
      if (!props.graphModel.nodes?.some((node) => node.properties.stepName === name.trim())) {
        props.model.properties.stepName = name;
      } else {
        number += 1;
        getNodesName(number);
      }
    }

    console.log('type = ' + props.model.type);
    console.log('type = ' + nodeDict[props.model.type]);

    props.model.properties.config = nodeDict[props.model.type].properties.config;

    console.log('nodeDict[props.model.type].properties.config = ' + nodeDict[props.model.type].properties.config)

    if (props.model.properties.height) {
      props.model.height = props.model.properties.height;
    }
  }

  // 获取节点 upstream 节点列表
  getUpNodeList(){

    const visited = new Set(); // 用于记录已经访问过的节点，避免循环引用
    const result = []; // 存储所有上一级节点

    const traverseUpNodes = (nodeId) => {
        const upNodeList = this.props.graphModel.getNodeIncomingNode(nodeId);
        for (const upNode of upNodeList) {
            if (!visited.has(upNode.id)) {
                visited.add(upNode.id);
                result.push(upNode);
                traverseUpNodes(upNode.id); // 递归调用，继续向上查找
            }
        }
    };

    traverseUpNodes(this.props.model.id);

    return result;

  }

  getAnchorShape(anchorData) {
    let { x, y, type } = anchorData;

    // console.log('getAnchorShape upNodeList = ' + this.getUpNodeList());
    // console.log('anchorData = ' + JSON.stringify(anchorData));

    let isConnect = false;

    if (type == 'left') {
      isConnect = this.props.graphModel.edges.some((edge) => edge.targetAnchorId == anchorData.id);
    } else {
      isConnect = this.props.graphModel.edges.some((edge) => edge.sourceAnchorId == anchorData.id);
    }

    return lh(
      'foreignObject',
      {
        ...anchorData,
        x: x - 10,
        y: y - 12,
        width: 30,
        height: 30
      },
      [
        lh('div', {
          style: { zindex: 0 },
          onClick: () => {
            if (type == 'right') {
              this.props.model.openNodeMenu(anchorData);
            }
          },
          dangerouslySetInnerHTML: {
            __html: isConnect
              ? `<svg width="100%" height="100%" viewBox="0 0 42 42" fill="none" xmlns="http://www.w3.org/2000/svg">
                <g filter="url(#filter0_d_5119_232585)">
                <path d="M20.9998 29.8333C28.0875 29.8333 33.8332 24.0876 33.8332 17C33.8332 9.91231 28.0875 4.16663 20.9998 4.16663C13.9122 4.16663 8.1665 9.91231 8.1665 17C8.1665 24.0876 13.9122 29.8333 20.9998 29.8333Z" fill="white"/>
                <path fill-rule="evenodd" clip-rule="evenodd" d="M20.9998 27.5C26.7988 27.5 31.4998 22.799 31.4998 17C31.4998 11.201 26.7988 6.49996 20.9998 6.49996C15.2008 6.49996 10.4998 11.201 10.4998 17C10.4998 22.799 15.2008 27.5 20.9998 27.5ZM33.8332 17C33.8332 24.0876 28.0875 29.8333 20.9998 29.8333C13.9122 29.8333 8.1665 24.0876 8.1665 17C8.1665 9.91231 13.9122 4.16663 20.9998 4.16663C28.0875 4.16663 33.8332 9.91231 33.8332 17Z" fill="#3370FF"/>
                </g>
                <defs>
                <filter id="filter0_d_5119_232585" x="-1" y="-1" width="44" height="44" filterUnits="userSpaceOnUse" color-interpolation-filters="sRGB">
                <feFlood flood-opacity="0" result="BackgroundImageFix"/>
                <feColorMatrix in="SourceAlpha" type="matrix" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0" result="hardAlpha"/>
                <feOffset dy="4"/>
                <feGaussianBlur stdDeviation="4"/>
                <feComposite in2="hardAlpha" operator="out"/>
                <feColorMatrix type="matrix" values="0 0 0 0 0.2 0 0 0 0 0.439216 0 0 0 0 1 0 0 0 0.1 0"/>
                <feBlend mode="normal" in2="BackgroundImageFix" result="effect1_dropShadow_5119_232585"/>
                <feBlend mode="normal" in="SourceGraphic" in2="effect1_dropShadow_5119_232585" result="shape"/>
                </filter>
                </defs>
                </svg>
                `
                  : `<svg width="100%" height="100%" viewBox="0 0 42 42" fill="none" xmlns="http://www.w3.org/2000/svg">
                <g filter="url(#filter0_d_5199_166905)">
                <path d="M20.9998 29.8333C28.0875 29.8333 33.8332 24.0876 33.8332 17C33.8332 9.91231 28.0875 4.16663 20.9998 4.16663C13.9122 4.16663 8.1665 9.91231 8.1665 17C8.1665 24.0876 13.9122 29.8333 20.9998 29.8333Z" fill="#3370FF"/>
                <path d="M19.8332 11.75C19.8332 11.4278 20.0943 11.1666 20.4165 11.1666H21.5832C21.9053 11.1666 22.1665 11.4278 22.1665 11.75V15.8333H26.2498C26.572 15.8333 26.8332 16.0945 26.8332 16.4166V17.5833C26.8332 17.9055 26.572 18.1666 26.2498 18.1666H22.1665V22.25C22.1665 22.5721 21.9053 22.8333 21.5832 22.8333H20.4165C20.0943 22.8333 19.8332 22.5721 19.8332 22.25V18.1666H15.7498C15.4277 18.1666 15.1665 17.9055 15.1665 17.5833V16.4166C15.1665 16.0945 15.4277 15.8333 15.7498 15.8333H19.8332V11.75Z" fill="white"/>
                </g>
                <defs>
                <filter id="filter0_d_5199_166905" x="-1" y="-1" width="44" height="44" filterUnits="userSpaceOnUse" color-interpolation-filters="sRGB">
                <feFlood flood-opacity="0" result="BackgroundImageFix"/>
                <feColorMatrix in="SourceAlpha" type="matrix" values="0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 127 0" result="hardAlpha"/>
                <feOffset dy="4"/>
                <feGaussianBlur stdDeviation="4"/>
                <feComposite in2="hardAlpha" operator="out"/>
                <feColorMatrix type="matrix" values="0 0 0 0 0.2 0 0 0 0 0.439216 0 0 0 0 1 0 0 0 0.1 0"/>
                <feBlend mode="normal" in2="BackgroundImageFix" result="effect1_dropShadow_5199_166905"/>
                <feBlend mode="normal" in="SourceGraphic" in2="effect1_dropShadow_5199_166905" result="shape"/>
                </filter>
                </defs>
              </svg>`
          }
        })
      ]
    );
  }

  setHtml(rootEl) {

    // console.log('setHtml getUpNodeList = ' + this.getUpNodeList())

    if (!this.isMounted) {
      this.isMounted = true;

      console.log('this.model = ' +  this.props.model.isSelected + ' , hello world = ' + this.props.model.helloworld)

      this.r = h(this.component, {
        properties: this.props.model.getProperties(),
        text: this.props.model.inputData,
        isSelected: this.props.model.isSelected,
        nodeModel: this.props.model,
        upNodeList: this.getUpNodeList(),
        onBtnClick: (i) => {
          props.graphModel.eventCenter.emit("custom:onBtnClick", i);
        },
      });

      const node = document.createElement('div');
      rootEl.appendChild(node);

      this.app = createApp({
        render: () => this.r
      })

      this.app.use(ElementPlus);
      this.app.use(ElementPlusIcons);
      this.app.mount(node)

    } else {
      if (this.r && this.r.component) {
        this.r.component.props.properties = this.props.model.getProperties();
      }
    }
  }

  componentWillUnmount() {
    super.componentWillUnmount();
    this.unmount();
  }

  getComponentContainer() {
    return this.root;
  }

  targetId() {
    return `${this.props.graphModel.flowId}:${this.props.model.id}`;
  }

  unmountVueComponent() {
    if (this.app) {
      this.app.unmount();
      this.app = null;
    }
    if (this.root) {
      this.root.innerHTML = '';
    }
    return this.root;
  }

  unmount() {
    if (isActive()) {
      disconnect(this.targetId());
    }
    this.unmountVueComponent();
  }

}

// 定义 AppNodeModel 类，继承自 HtmlResize.model
class AppNodeModel extends HtmlResize.model {
  refreshDeges() {
    // 更新节点连接边的 path
    this.incoming.edges.forEach(function (edge) {
      // 调用自定义的更新方案
      edge.updatePathByAnchor();
    });
    this.outgoing.edges.forEach(function (edge) {
      edge.updatePathByAnchor();
    });
  }

  set_position(position) {
    const { x, y } = position;

    console.log('set_position = ' + x + ',' + y)

    if (x) {
      this.x = x;
    }
    if (y) {
      this.y = y;
    }
    this.refreshDeges();
  }

  getResizeOutlineStyle() {
    const style = HtmlResize.model.prototype.getResizeOutlineStyle.call(this);
    style.stroke = 'none';
    return style;
  }

  getUpNodeList() {
    return this.graphModel.getNodeIncomingNode(this.id);
  }

  getControlPointStyle() {
    const style = HtmlResize.model.prototype.getControlPointStyle.call(this);
    style.stroke = 'none';
    style.fill = 'none';
    return style;
  }

  getNodeStyle() {
    return {
      overflow: 'visible'
    };
  }

  getOutlineStyle() {
    const style = HtmlResize.model.prototype.getOutlineStyle.call(this);
    style.stroke = 'none';
    if (style.hover) {
      style.hover.stroke = 'none';
    }
    return style;
  }

  // 如果不用修改锚地形状，可以重写颜色相关样式
  getAnchorStyle(anchorInfo) {
    const style = HtmlResize.model.prototype.getAnchorStyle.call(this, anchorInfo);
    if (anchorInfo.type === 'left') {
      style.fill = 'red';
      style.hover.fill = 'transparent';
      style.hover.stroke = 'transparent';
      style.className = 'lf-hide-default';
    } else {
      style.fill = 'green';
    }
    return style;
  }

  setHeight(height) {
    const sourceHeight = this.height;
    const targetHeight = height + 100;

    console.log('设置高度:' + height + ',this.height = ' + this.height + ' , sourceHeight = ' + sourceHeight + ' , targetHeight = ' + targetHeight)

    this.height = targetHeight;
    this.properties['height'] = targetHeight;
    this.move(0, (targetHeight - sourceHeight) / 2);
    this.outgoing.edges.forEach(function (edge) {
      // 调用自定义的更新方案
      edge.updatePathByAnchor();
    });
    this.incoming.edges.forEach(function (edge) {
      // 调用自定义的更新方案
      edge.updatePathByAnchor();
    });
  }

  get_width() {
    return this.properties && this.properties.width ? this.properties.width : 340;
  }

  setAttributes() {
    this.width = this.get_width();

    // this.x = this.properties.x ;
    // this.y = this.properties.y ;

    // console.log('this.properties = ' + JSON.stringify(this.properties))

    const isLoop = function (node_id, target_node_id) {
      const up_node_list = this.graphModel.getNodeIncomingNode(node_id);
      for (const index in up_node_list) {
        const item = up_node_list[index];
        if (item.id === target_node_id) {
          return true;
        } else {
          const result = isLoop.call(this, item.id, target_node_id);
          if (result) {
            return true;
          }
        }
      }
      return false;
    }.bind(this);

    const circleOnlyAsTarget = {
      message: '仅允许从右侧连接',
      validate: function (sourceNode, targetNode, sourceAnchor) {
        return sourceAnchor.type === 'right';
      }
    };

    this.sourceRules.push({
      message: '不允许循环连接',
      validate: function (sourceNode, targetNode, sourceAnchor, targetAnchor) {
        return !isLoop(sourceNode.id, targetNode.id);
      }
    });

    this.sourceRules.push(circleOnlyAsTarget);
    this.targetRules.push({
      message: '仅允许连接到左侧',
      validate: function (sourceNode, targetNode, sourceAnchor, targetAnchor) {
        return targetAnchor.type === 'left';
      }
    });
  }

  getDefaultAnchor() {
    const { id, x, y, width } = this;

    // console.log('getDefaultAnchor this = ' + this + ' id = ' + id + ' x = ' + x + ' y = ' + y + ' width = ' + width)

    const showNode = typeof this.properties.showNode === 'undefined' ? true : this.properties.showNode;
    const anchors = [];

    if (this.type !== WorkflowType.Base) {
      if (this.type !== WorkflowType.Start) {
        anchors.push({
          x: x - width / 2 - 5,
          y: showNode ? y : y - 15,
          id: `${id}_left`,
          edgeAddable: false,
          type: 'left'
        });
      }
      anchors.push({
        x: x + width / 2 - 5,
        y: showNode ? y : y - 15,
        id: `${id}_right`,
        type: 'right'
      });
    }

    return anchors;
  }
}

export { AppNodeModel, AppNode }