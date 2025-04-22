<template>
  <div>
    <el-input
      v-model="jsonData"
      type="textarea"
      :rows="20"
      placeholder="请输入 JSON 数据"
      @input="onJsonChange"
    ></el-input>
    <el-row>
      <el-col :span="24" style="margin-top: 20px;display: flex;justify-content: flex-end;">
        <el-button type="primary" size="large" @click="getFinalJson">获取最终 JSON 结果</el-button>
        <el-button type="primary" size="large" @click="loadJson">加载 JSON</el-button>
        <el-button type="primary" size="large" @click="saveJson">保存 JSON</el-button>
      </el-col>
    </el-row>
    <pre v-if="finalJson">{{ finalJson }}</pre>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import { updateTemplateParamFormat } from "@/api/smart/assistant/template";

const emit = defineEmits(['getList']);

// 初始 JSON 数据
const initialJson = ref({});
const templateId = ref(null);

const jsonData = ref(JSON.stringify(initialJson.value, null, 2));
const finalJson = ref('');

const onJsonChange = (newValue) => {
  jsonData.value = newValue;
};

const getFinalJson = () => {
  try {
    const parsedJson = JSON.parse(jsonData.value);
    finalJson.value = JSON.stringify(parsedJson, null, 2);
  } catch (error) {
    ElMessage.error('JSON 解析错误');
    console.error('JSON 解析错误:', error);
  }
};

const initialTemplateJson = (row) => {
  console.log('row = ' + row.paramFormat) ;
  if(!row.paramFormat){
    row.paramFormat = {};
  }

  initialJson.value = JSON.parse(row.paramFormat);
  templateId.value = row.id;
  // 更新 jsonData 的值
  jsonData.value = JSON.stringify(initialJson.value, null, 2);
}

const loadJson = () => {
  const input = document.createElement('input');
  input.type = 'file';
  input.accept = '.json';
  input.addEventListener('change', (event) => {
    const target = event.target ; //  as HTMLInputElement;
    const file = target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        const result = e.target?.result ; // as string;
        try {
          const parsedJson = JSON.parse(result);
          jsonData.value = JSON.stringify(parsedJson, null, 2);
          ElMessage.success('JSON 加载成功');
        } catch (error) {
          ElMessage.error('JSON 解析错误');
          console.error('JSON 解析错误:', error);
        }
      };
      reader.readAsText(file);
    }
  });
  input.click();
};

const saveJson = () => {
  try {
    const parsedJson = JSON.parse(jsonData.value);
    const jsonStr = JSON.stringify(parsedJson, null, 2);

    const data = {
      templateId: templateId.value,
      jsonStr: jsonStr

    }
    updateTemplateParamFormat(data).then(res => {
      console.log(res);
      ElMessage.success('JSON 保存成功');
      emit('getList');
    })
  } catch (error) {
    ElMessage.error('JSON 解析错误');
    console.error('JSON 解析错误:', error);
  }
};

defineExpose({
  initialTemplateJson,
});

</script>
