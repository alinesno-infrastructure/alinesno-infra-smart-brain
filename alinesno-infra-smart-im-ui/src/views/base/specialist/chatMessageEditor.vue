<template>
  <div>
    <el-card v-for="(item, index) in items" :key="index" shadow="hover">
      <el-input v-model="item.title" placeholder="输入标题"></el-input>
      <el-button type="primary" @click="addItem(index)">添加子标题</el-button>
      <el-collapse v-if="item.subtitles && item.subtitles.length > 0" accordion>
        <el-collapse-item v-for="(subItem, subIndex) in item.subtitles" :key="subIndex">
          <template #title>
            {{ subItem.title }}
          </template>
          <div>
            <el-input v-model="subItem.title" placeholder="输入子标题"></el-input>
            <el-button type="danger" @click="removeItem(index, subIndex)">删除</el-button>
            <el-button type="primary" @click="addItem(index, subIndex)">添加子标题</el-button>
            <JsonEditor :items="subItem.subtitles" />
          </div>
        </el-collapse-item>
      </el-collapse>
      <el-button type="danger" @click="removeItem(index)">删除</el-button>
    </el-card>
    <el-button type="primary" @click="addItem()">添加新标题</el-button>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { ElCard, ElInput, ElButton, ElCollapse, ElCollapseItem } from 'element-plus';

// 初始JSON数据
const initialData = [
  {
    title: "人生规划界面",
    subtitles: [
      {
        title: "自我认知与目标设定",
        subtitles: [
          { title: "理解自我：兴趣、价值观与潜能" },
          { title: "设定目标：短期与长期目标的制定" }
        ]
      },
      {
        title: "行动计划与资源利用",
        subtitles: [
          { title: "制定行动计划：SMART原则的应用" },
          { title: "资源获取与优化：人脉、信息与工具" }
        ]
      },
      {
        title: "持续评估与调整",
        subtitles: [
          { title: "定期回顾与反思：成果与经验总结" },
          { title: "灵活调整策略：应对变化与挑战" }
        ]
      }
    ]
  }
];

const items = reactive(initialData);

function addItem(parentIndex, subParentIndex) {
  const newItem = { title: '', subtitles: [] };
  if (parentIndex === undefined) {
    items.push(newItem);
  } else if (subParentIndex === undefined) {
    items[parentIndex].subtitles.push(newItem);
  } else {
    items[parentIndex].subtitles[subParentIndex].subtitles.push(newItem);
  }
}

function removeItem(parentIndex, subIndex) {
  if (subIndex !== undefined) {
    items[parentIndex].subtitles.splice(subIndex, 1);
  } else {
    items.splice(parentIndex, 1);
  }
}

</script>

<style scoped>
.el-card {
  margin-bottom: 10px;
}

.el-collapse {
  margin-top: 10px;
}

.el-collapse-item__header {
  font-weight: bold;
}
</style>