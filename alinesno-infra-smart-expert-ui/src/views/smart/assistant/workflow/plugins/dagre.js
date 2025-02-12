import { DagreLayout } from '@antv/layout';

export default class Dagre {
    static pluginName = 'dagre';

    constructor() {
        this.lf = null;
        this.option = {};
    }

    render(lf) {
        this.lf = lf;
    }

    layout(option = {}) {
        const { nodes, edges, gridSize } = this.lf.graphModel;
        let nodesep = 40;
        let ranksep = 40;
        if (gridSize > 20) {
            nodesep = gridSize * 2;
            ranksep = gridSize * 2;
        }
        this.option = {
            type: 'dagre',
            rankdir: 'LR',
            align: 'DR',
            nodesep,
            ranksep,
            begin: [120, 120],
            ...option
        };
        const layoutInstance = new DagreLayout(this.option);
        const layoutData = layoutInstance.layout({
            nodes: nodes.map((node) => ({
                id: node.id,
                size: {
                    width: node.width,
                    height: node.height
                },
                model: node
            })),
            edges: edges.map((edge) => ({
                source: edge.sourceNodeId,
                target: edge.targetNodeId,
                model: edge
            }))
        });

        if (layoutData.nodes) {
            layoutData.nodes.forEach((node) => {
                const { model } = node;
                model.set_position({ x: node.x, y: node.y });
            });
        }
        this.lf.fitView();
    }
}