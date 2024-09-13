<template>
    <div>
        <JsonEditorVue class="editor" 
          :options="options"
          v-model="data" />
    </div>
</template>

<script setup>

import JsonEditorVue from 'json-editor-vue3'

const options = ref({
    "mode": "text",
    "indentation": 2
})

const data = ref([
  {
    "title": "下列哪位作家被誉为现代爱情文学的开山鼻祖？",
    "desc": "这里考的是文学知识",
    "type": "single_choice",
    "score": 10,
    "answers": [
      "A: 莎士比亚",
      "B: 托尔斯泰",
      "C: 简·奥斯汀",
      "D: 雨果"
    ],
    "rightAnswer": "C"
  },
  {
    "title": "以下哪部电影被认为是爱情电影的经典之作？",
    "desc": "这里考的是电影知识",
    "type": "multiple_choice",
    "score": 10,
    "answers": [
      "A: 《泰坦尼克号》",
      "B: 《罗马假日》",
      "C: 《阿甘正传》",
      "D: 《肖申克的救赎》"
    ],
    "rightAnswer": "A|B"
  },
  {
    "title": "在中文中，哪两个字常常用来表达浪漫的爱情？",
    "desc": "这里考的是语言文化知识",
    "type": "fill_in_the_blank",
    "score": 10,
    "answers": [
      "海誓山盟"
    ],
    "rightAnswer": null
  },
  {
    "title": "情人节的起源与哪位古罗马神有关？",
    "desc": "这里考的是历史文化知识",
    "type": "single_choice",
    "score": 10,
    "answers": [
      "A: 维纳斯",
      "B: 罗密欧",
      "C: 朱丽叶",
      "D: 丘比特"
    ],
    "rightAnswer": "A"
  },
  {
    "title": "以下哪种花卉通常被视为爱情的象征？",
    "desc": "这里考的是植物知识",
    "type": "multiple_choice",
    "score": 10,
    "answers": [
      "A: 玫瑰",
      "B: 百合",
      "C: 菊花",
      "D: 牡丹"
    ],
    "rightAnswer": "A|B"
  },
  {
    "title": "哪位著名的诗人以一首《致橡树》表达了对爱情的独立思考？",
    "desc": "这里考的是诗歌知识",
    "type": "single_choice",
    "score": 10,
    "answers": [
      "A: 舒婷",
      "B: 海子",
      "C: 北岛",
      "D: 顾城"
    ],
    "rightAnswer": "A"
  },
  {
    "title": "下列哪一种巧克力被认为是最能激发浪漫情感的？",
    "desc": "这里考的是心理学知识",
    "type": "fill_in_the_blank",
    "score": 10,
    "answers": [
      "黑巧克力"
    ],
    "rightAnswer": null
  },
  {
    "title": "哪一部经典小说中的主人公，以其对爱情的执着追求而广为人知？",
    "desc": "这里考的是小说知识",
    "type": "single_choice",
    "score": 10,
    "answers": [
      "A: 《傲慢与偏见》的伊丽莎白",
      "B: 《红楼梦》的林黛玉",
      "C: 《呼啸山庄》的希斯克利夫",
      "D: 《简·爱》的简·爱"
    ],
    "rightAnswer": "D"
  },
  {
    "title": "哪一年被定为国际和平年的目的是为了促进全球对和平的重视与理解？",
    "desc": "这里考的是历史事件知识",
    "type": "single_choice",
    "score": 10,
    "answers": [
      "A: 1986年",
      "B: 1987年",
      "C: 1988年",
      "D: 1989年"
    ],
    "rightAnswer": "A"
  },
  {
    "title": "以下哪一种传统习俗常被视为表达深厚情感的一种方式？",
    "desc": "这里考的是民俗知识",
    "type": "multiple_choice",
    "score": 10,
    "answers": [
      "A: 订婚戒指",
      "B: 七夕节的乞巧",
      "C: 结婚时的交换信物",
      "D: 情人节送花"
    ],
    "rightAnswer": "A|C|D"
  }
])

</script>

<style scoped lang="scss">
</style>


