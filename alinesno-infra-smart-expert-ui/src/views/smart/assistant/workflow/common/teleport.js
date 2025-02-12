import { BaseEdgeModel, BaseNodeModel, GraphModel } from '@logicflow/core';
import { defineComponent, h, reactive, markRaw, Fragment, Teleport } from 'vue';

let active = false;
const items = reactive({});

export function connect(id, component, container, node, graph, get_props) {
    if (!get_props) {
        get_props = function (node, graph) {
            return { nodeModel: node, graph };
        };
    }
    if (active) {
        items[id] = markRaw(
            defineComponent({
                render() {
                    return h(Teleport, { to: container }, [h(component, get_props(node, graph))]);
                },
                provide() {
                    return {
                        getNode: function () {
                            return node;
                        },
                        getGraph: function () {
                            return graph;
                        }
                    };
                }
            })
        );
    }
}

export function disconnect(id) {
    if (active) {
        delete items[id];
    }
}

export function isActive() {
    return active;
}

export function getTeleport() { 
    active = true;

    return defineComponent({
        props: {
            flowId: {
                type: String,
                required: true
            }
        },
        setup(props) {
            return function () {
                const children = [];
                Object.keys(items).forEach(function (id) {
                    if (id.startsWith(props.flowId)) {
                        children.push(items[id]);
                    }
                });
                return h(
                    Fragment,
                    {},
                    children.map(function (item) {
                        return h(item);
                    })
                );
            };
        }
    });
}