<template>
    <el-card shadow="always" class="flow-control-panel">
        <el-button link @click="zoomOut">
            <el-tooltip class="box-item" effect="dark" content="缩小" placement="top">
                <el-icon :size="16" title="缩小">
                    <ZoomOut />
                </el-icon>
            </el-tooltip>
        </el-button>
        <el-button link @click="zoomIn">
            <el-tooltip class="box-item" effect="dark" content="放大" placement="top">
                <el-icon :size="16" title="放大">
                    <ZoomIn />
                </el-icon>
            </el-tooltip>
        </el-button>
        <el-button link @click="fitView">
            <el-tooltip class="box-item" effect="dark" content="适应" placement="top">
                <el-icon :size="16" title="适应">
                    <Position />
                </el-icon>
            </el-tooltip>
        </el-button>
        <el-divider direction="vertical" />
        <el-button link @click="retract">
            <el-tooltip class="box-item" effect="dark" content="收起全部节点" placement="top">
                <el-icon :size="16" title="收起全部节点">
                    <Download />
                </el-icon>
            </el-tooltip>
        </el-button>
        <el-button link @click="extend">
            <el-tooltip class="box-item" effect="dark" content="展开全部节点" placement="top">
                <el-icon :size="16" title="展开全部节点">
                    <Upload />
                </el-icon>
            </el-tooltip>
        </el-button>
        <el-button link @click="layout">
            <el-tooltip class="box-item" effect="dark" content="一键美化" placement="top">
                <el-icon :size="16" title="一键美化">
                    <Star />
                </el-icon>
            </el-tooltip>
        </el-button>
        <el-divider direction="vertical" />
        <el-button link @click="layout">
            <el-tooltip class="box-item" effect="dark" content="试运行" placement="top">
                <el-icon :size="16" title="试运行">
                    <CaretRight /> 
                </el-icon>
            </el-tooltip>
        </el-button>
    </el-card>
</template>

<script setup>
const props = defineProps({
    lf: {
        type: [Object, String, null],
        default: null
    }
})

function zoomIn() {
    if (props.lf) {
        props.lf.zoom(true, [0, 0]);
    }
}

function zoomOut() {
    if (props.lf) {
        props.lf.zoom(false, [0, 0]);
    }
}

function fitView() {
    if (props.lf) {
        props.lf.resetZoom();
        props.lf.resetTranslate();
        props.lf.fitView();
    }
}

const layout = () => {
    if (props.lf) {
        props.lf.extension.dagre.layout();
    }
}

const retract = () => {
    if (props.lf) {
        props.lf.graphModel.nodes.forEach((element) => {
            element.properties.showNode = false;
        });
    }
}

const extend = () => {
    if (props.lf) {
        props.lf.graphModel.nodes.forEach((element) => {
            element.properties.showNode = true;
        });
    }
}
</script>

<style lang="scss">
.flow-control-panel {
    .el-card__body {
        padding: 10px !important
    }
}
</style>