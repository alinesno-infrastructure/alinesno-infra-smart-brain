<template>
  <div class="exam-settings-panel">
    <el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleClick">
      <el-tab-pane label="整卷设置" name="first">

        <div class="pager-settings">
          <div class="pager-settings-title" v-for="(item, index) in pagerSettings" :key="index">

            <div class="pager-settings-title-label">
              <i :class="item.icon"></i> {{ item.label }}
            </div>

            <div class="pager-settings-title-item" v-for="(item, index) in item.settings" :key="index">
              <div>
                <i :class="item.icon"></i> {{ item.label }}
              </div>
              <div class="pager-settings-title-item-value">
                <el-switch v-model="item.value" size="small" />
              </div>
            </div>
          </div>

        </div>

      </el-tab-pane>

      <!-- 
      <el-tab-pane label="题目设置" name="second">
      </el-tab-pane> 
      -->

      <!-- 
      <el-tab-pane label="分页设置" name="third">
        分页设置 
      </el-tab-pane> 
      -->

    </el-tabs>
  </div>
</template>

<script setup>

import { ref } from 'vue'

const activeName = ref('first')

const pagerSettings = ref([{
  "id": "display-settings",
  "label": "显示设置",
  "icon": "fa-solid fa-display",
  "settings": [
    {
      "id": "show-question-number",
      "code": "SHOW_QUESTION_NUMBER",
      "label": "显示题目编号",
      "icon": "fa-solid fa-list-ol",
      "value": true
    },
    {
      "id": "show-prev-button",
      "code": "SHOW_PREV_BUTTON",
      "label": "显示「上一页」按钮",
      "icon": "fa-solid fa-arrow-left",
      "value": false
    },
    {
      "id": "randomize-questions",
      "code": "RANDOMIZE_QUESTIONS",
      "label": "随机抽题",
      "icon": "fa-solid fa-random",
      "value": false
    },
    {
      "id": "hide-mobile-cover",
      "code": "HIDE_MOBILE_COVER",
      "label": "隐藏移动端封面",
      "icon": "fa-solid fa-mobile-screen-button",
      "value": false
    },
    {
      "id": "hide-title-instructions",
      "code": "HIDE_TITLE_INSTRUCTIONS",
      "label": "隐藏标题和引导语",
      "icon": "fa-solid fa-heading",
      "value": false
    },
    {
      "id": "hide-copyright",
      "code": "HIDE_COPYRIGHT",
      "label": "隐藏版权信息",
      "icon": "fa-solid fa-copyright",
      "value": false
    }
  ]
},
{
  "id": "after-submit",
  "label": "提交问卷后",
  "icon": "fa-solid fa-paper-plane",
  "settings": [
    {
      "id": "redirect-after-submit",
      "code": "REDIRECT_AFTER_SUBMIT",
      "label": "交卷后跳转",
      "icon": "fa-solid fa-arrow-right-from-bracket",
      "value": false
    }
  ]
},
{
  "id": "access-control",
  "label": "谁可以填答",
  "icon": "fa-solid fa-lock",
  "settings": [
    {
      "id": "response-permission",
      "code": "RESPONSE_PERMISSION",
      "label": "谁可以填答",
      "icon": "fa-solid fa-users",
      "value": "true"
    },
    {
      "id": "contact-permission",
      "code": "CONTACT_PERMISSION",
      "label": "联系人",
      "icon": "fa-solid fa-user-friends",
      "value": "true"
    },
    {
      "id": "list-permission",
      "code": "LIST_PERMISSION",
      "label": "录入名单（QQ/手机号/自定义）",
      "icon": "fa-solid fa-address-book",
      "value": "true"
    },
    {
      "id": "no-response-permission",
      "code": "NO_RESPONSE_PERMISSION",
      "label": "不可填答",
      "icon": "fa-solid fa-ban",
      "value": "true"
    }
  ]
}]
)

const handleClick = (tab, event) => {
  console.log(tab, event)
}

</script>

<style lang="scss" scoped>
.exam-settings-panel {
  background: #fff;
  border-radius: 5px;
  margin-top: 10px;
  padding: 10px;
  margin-right: 10px;
  text-align: left;
  height: calc(100vh - 110px);

  .pager-settings-title {
    background: #fafafa;
    margin-bottom: 20px;
    padding: 5px;
    border-radius: 5px;
}

  .pager-settings {
    width: 100%;
    text-align: left;
    padding: 0px;
    font-size: 14px;
  }

  .pager-settings-title-item {
    display: flex;
    align-items: center;
    cursor: pointer;
    gap: 5px;
    padding: 6px;
    margin-bottom: 5px;
    border-radius: 5px;
    justify-content: space-between;
  }

  .pager-settings-title-label {
    text-align: left;
    padding: 10px 5px;
    font-weight: bold;
    margin-bottom: 5px;
  }

  .pager-settings-title-item-value {
    margin-right: 10px;
  }

}
</style>

<style lang="css">
.el-tabs__item {
  color: #333 !important
}

.el-tabs__item.is-active,
.el-tabs__item:hover {
  font-weight: bold;
}
</style>
