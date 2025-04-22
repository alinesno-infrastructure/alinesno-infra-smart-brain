<template>
  <div class="template-card-panel">
      <div class="template-img" v-if="props.templateItem.templateImage" :style="{ backgroundImage: `url(${imagePath(props.templateItem.templateImage)})` }"></div>
      <div class="template-img" v-else :style="'background-image:url(\'http://data.linesno.com/aip-template/images/template_0'+(index % 6 +1)+'.jpeg\')'"></div>
    <div class="template-text">
        <div class="template-name">
            {{ props.templateItem.templateName }}

        </div>
        <span>
            {{ props.templateItem.templateDesc}}
        </span>
    </div>
    <div class="template-btn">
        <el-text>
            <i :class="getWorkplaceNameByType(props.templateItem.templateDataScope)?.icon" ></i>
            {{ getWorkplaceNameByType(props.templateItem.templateDataScope)?.name }}

            <el-text v-if="props.templateItem.templateEngine == 'simple'" type="text">
                简单
            </el-text>
            <el-text v-if="props.templateItem.templateEngine == 'complex'" type="text">
                复杂 
            </el-text>
        </el-text>

        <el-button v-if="props.templateItem.paramFormat" type="primary" text bg @click="configParamFormat()">
            已配置
        </el-button>
        <el-button v-else type="warning" text bg @click="configParamFormat()">
            未配置
        </el-button>
    </div>
  </div>
</template>

<script setup name="Index">

const emit = defineEmits(['configParamFormat'])

const props = defineProps({
    templateItem: {
        type: Object,
        default: () => {}
    } , 
    index: {
        type: Number,
        default: 0
    }
})

const templateDataScopesArr = [
    { "id": "public", "name": "公开", "icon": "fa-solid fa-globe" , "desc":"模板对外所有人可用" },
    { "id": "org", "name": "组织", "icon": "fa-solid fa-truck-plane" , "desc":"只对组织内的成员可用" }
];

const configParamFormat = () => {
    emit('configParamFormat' , props.templateItem)
};

const getWorkplaceNameByType = (type) => {
  for (let i = 0; i < templateDataScopesArr.length; i++) {
    let item = templateDataScopesArr[i] ; 
    if (item.id == type) {
      return item ; 
    }
  }
  return null ;
};

</script>

<style lang="scss" scoped>
// 自定义新式 
.template-card-panel {
    border: 1px #d3d3d3 solid;
    padding: 15px;
    margin: 5px;
    border-radius: 5px;
    display: flex;
    flex-direction: column;
    gap: 10px;

    &:hover {
        box-shadow: 0 0 12px rgba(0, 0, 0, .2);
    }

    .template-img {
        img {
            width: 100%;
            height: auto;
        }
    }

    .template-img {
        background-image: url(http://data.linesno.com/aip-template/images/template_02.jpeg);
        height: 250px;
        background-size: contain;
        border-radius: 5px;
    }

    .template-text {
        display: flex;
        flex-direction: column;
        gap: 8px;
        font-size: 14px;
        color: #333;

        .template-name {
            font-size: 16px;
            font-weight: bold;
            white-space: nowrap;
            overflow: hidden;
            width:100%;
            text-overflow: ellipsis;
        }

        span {
            display: flex;
            flex-direction: row;
            gap: 10px;
        }
    }

    .template-btn {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }
}
</style>