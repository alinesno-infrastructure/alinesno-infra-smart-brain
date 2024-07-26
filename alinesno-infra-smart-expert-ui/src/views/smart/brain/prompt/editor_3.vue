<template>
      <Codemirror
        v-model:value="code"
        :options="cmOptions"
        :border="true"
        ref="cmRef"
        height="500"
        @change="onChange"
        @input="onInput"
        @ready="onReady"
      >
      </Codemirror>
</template>
<script setup>
  import { ref, onMounted, onUnmounted } from "vue"
  import "codemirror/mode/markdown/markdown.js"
  import Codemirror from "codemirror-editor-vue3"

  // import type { CmComponentRef } from "codemirror-editor-vue3"
  // import type { Editor, EditorConfiguration } from "codemirror"

  const labelPosition = ref("top")
  const systemContent = ref("")
  const userContent = ref("")

  const code = ref(
    `你现在是一名Java计算机面试官，请生成微服务架构相关的，现在需要生成题目做于Java面试题，主要题目类型如下：

- 单选题： single_choice  
- 多选题： multiple_choice  
- 填空题： fill_in_the_blank  

现在请使用yaml生成单选题5题，多选题2题，填写题3题，参考下面的示例生成yaml文件，并返回yaml格式
题目的评分标准按题目的难度来设定分数，总分是100分，请生成10道题目。
`
  )
  const cmRef = ref(null)
  const cmOptions = {
    mode: "text/markdown"
  }

  const onChange = (val, cm) => {
    console.log(val)
    console.log(cm.getValue())

    userContent.value = cm.getValue()
  }

  const onInput = (val) => {
    console.log(val)
  }

  const onReady = (cm) => {
    console.log(cm.focus())
  }

  /** 提交按钮 */
  function onSubmit() {
    updatePrompt(form.value).then(response => {
      proxy.$modal.msgSuccess("修改成功");
      open.value = false;
    });
  };

  onMounted(() => {
    setTimeout(() => {
      cmRef.value?.refresh()
    }, 1000)

   //  setTimeout(() => {
   //    cmRef.value?.resize(300, 200)
   //  }, 2000)

   //  setTimeout(() => {
   //    cmRef.value?.cminstance.isClean()
   //  }, 3000)

  })

  onUnmounted(() => {
    cmRef.value?.destroy()
  })
</script>
