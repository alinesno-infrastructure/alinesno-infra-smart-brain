<template>
  <div class="layout-config">
    <div class="layout-manager">
      <div class="layout-list">
        <el-button type="primary" text bg size="large" icon="Plus" @click="addLayoutTemplate"
          class="mb-20">添加排版模板</el-button>

        <el-table :data="templates" style="width: 100%">
          <el-table-column type="index" label="序号" width="60" />
          <el-table-column prop="name" label="模板名称" width="120" :show-overflow-tooltip="true" />
          <el-table-column prop="description" label="描述" />
          <el-table-column label="操作" align="center" width="200">
            <template #default="{ row }">
              <el-button text bg @click="editTemplate(row)">编辑</el-button>
              <el-button type="danger" text bg @click="deleteTemplate(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="layout-editor" v-if="currentTemplate">
        <h3>模板编辑: {{ currentTemplate.name }}</h3>

        <el-tabs type="border-card">
          <el-tab-pane label="标题样式">
            <div class="style-editor">
              <el-form label-width="100px" size="large">
                <el-form-item v-for="level in 6" :key="level" :label="`标题 ${level}`">
                  <div class="style-controls">
                    <el-select v-model="currentTemplate.headings[level - 1].fontFamily" placeholder="字体"
                      style="width: 120px">
                      <el-option label="宋体" value="SimSun" />
                      <el-option label="黑体" value="SimHei" />
                      <el-option label="微软雅黑" value="Microsoft YaHei" />
                    </el-select>
                    <el-input-number v-model="currentTemplate.headings[level - 1].fontSize" :min="10" :max="36"
                      controls-position="right" />
                    <span>px</span>
                    <el-color-picker v-model="currentTemplate.headings[level - 1].color" />
                    <el-select v-model="currentTemplate.headings[level - 1].align" placeholder="对齐"
                      style="width: 100px">
                      <el-option label="左对齐" value="left" />
                      <el-option label="居中" value="center" />
                      <el-option label="右对齐" value="right" />
                    </el-select>
                  </div>
                </el-form-item>
                <el-form-item label="字体">
                  <el-select v-model="currentTemplate.body.fontFamily" placeholder="字体" style="width: 120px">
                    <el-option label="宋体" value="SimSun" />
                    <el-option label="黑体" value="SimHei" />
                    <el-option label="微软雅黑" value="Microsoft YaHei" />
                  </el-select>
                </el-form-item>
                <el-form-item label="字号">
                  <el-input-number v-model="currentTemplate.body.fontSize" :min="10" :max="24"
                    controls-position="right" />
                  <span>px</span>
                </el-form-item>
                <el-form-item label="行高">
                  <el-slider v-model="currentTemplate.body.lineHeight" :min="1" :max="2" :step="0.1" show-input />
                </el-form-item>
                <el-form-item label="颜色">
                  <el-color-picker v-model="currentTemplate.body.color" />
                </el-form-item>
              </el-form>
            </div>
          </el-tab-pane>

          <el-tab-pane label="页面设置">
            <el-form label-width="100px" size="large">

              <el-form-item label="预设边距">
                <el-select v-model="selectedMarginPreset" placeholder="选择预设边距" @change="applyMarginPreset">
                  <el-option v-for="preset in marginPresets" :key="preset.value" :label="preset.label"
                    :value="preset.value" />
                </el-select>
              </el-form-item>

              <!-- <el-form-item label="页边距(mm)">
                <div class="form-row">
                  <el-row>
                    <el-col :span="12">
                      <el-input-number v-model="currentTemplate.margin.top" :min="10" :max="50" /> 上
                      <el-input-number v-model="currentTemplate.margin.bottom" :min="10" :max="50" /> 下
                    </el-col>
                    <el-col :span="12">
                      <el-input-number v-model="currentTemplate.margin.left" :min="10" :max="50" /> 左
                      <el-input-number v-model="currentTemplate.margin.right" :min="10" :max="50" /> 右
                    </el-col>
                  </el-row>
                </div>
              </el-form-item> -->

              <el-form-item label="页边距(mm)">
                <div class="form-row">
                  <el-row>
                    <el-col :span="12">
                      <el-input-number v-model="currentTemplate.margin.top" :min="10" :max="50" /> 上
                      <el-input-number v-model="currentTemplate.margin.bottom" :min="10" :max="50" /> 下
                    </el-col>
                    <el-col :span="12">
                      <el-input-number v-model="currentTemplate.margin.left" :min="10" :max="50" /> 左
                      <el-input-number v-model="currentTemplate.margin.right" :min="10" :max="50" /> 右
                    </el-col>
                  </el-row>
                </div>
              </el-form-item>

              <el-form-item label="纸张大小">
                <el-select v-model="currentTemplate.pageSize" placeholder="请选择纸张大小">
                  <el-option label="A4" value="A4" />
                  <el-option label="A3" value="A3" />
                  <el-option label="Letter" value="Letter" />
                </el-select>
              </el-form-item>
              <el-row>
                <el-col :span="12">
                  <el-form-item label="页眉高度">
                    <el-input-number v-model="currentTemplate.headerHeight" :min="0" :max="50" />
                    <span>mm</span>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="页脚高度">
                    <el-input-number v-model="currentTemplate.footerHeight" :min="0" :max="50" />
                    <span>mm</span>
                  </el-form-item>
                </el-col>
              </el-row>

              <el-form-item label="页面预览">
                <div class="page-preview">
                  <div class="page" :style="pageStyle">
                    <div class="header" :style="headerStyle"></div>
                    <div class="content" :style="contentStyle">
                      <div class="margin-line top" :style="marginLineStyle('top')"></div>
                      <div class="margin-line bottom" :style="marginLineStyle('bottom')"></div>
                      <div class="margin-line left" :style="marginLineStyle('left')"></div>
                      <div class="margin-line right" :style="marginLineStyle('right')"></div>
                      <div class="content-text">文档内容区域</div>
                    </div>
                    <div class="footer" :style="footerStyle"></div>
                  </div>
                  <div class="page-size-info">
                    纸张大小: {{ currentTemplate.pageSize }} | 页边距: 上{{ currentTemplate.margin.top }} 下{{
                      currentTemplate.margin.bottom }} 左{{ currentTemplate.margin.left }} 右{{ currentTemplate.margin.right
                    }} mm
                  </div>
                </div>
              </el-form-item>

            </el-form>
          </el-tab-pane>

          <el-tab-pane label="封面设置">
            <el-form label-width="100px" size="large">
              <el-form-item label="封面样式">
                <el-input type="textarea" v-model="currentTemplate.coverHtml" :rows="2" placeholder="请输入封面HTML代码"
                  @input="updatePreview('cover')" />
              </el-form-item>
              <el-form-item label="预览" v-if="preview.cover">
                <div class="html-preview" v-html="preview.cover"></div>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <el-tab-pane label="页眉页脚">
            <el-form label-width="100px" size="large">
              <el-form-item label="页眉样式">
                <el-input type="textarea" v-model="currentTemplate.headerHtml" :rows="2" placeholder="请输入页眉HTML代码"
                  @input="updatePreview('header')" />
              </el-form-item>
              <el-form-item label="页脚样式">
                <el-input type="textarea" v-model="currentTemplate.footerHtml" :rows="2" placeholder="请输入页脚HTML代码"
                  @input="updatePreview('footer')" />
              </el-form-item>
              <el-form-item label="预览" v-if="preview.header">
                <div class="header-footer-preview">
                  <div class="html-preview" v-html="preview.header"></div>
                  <div class="page-content">文档内容区域...</div>
                  <div class="html-preview" v-html="preview.footer"></div>
                </div>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <el-tab-pane label="红头文件配置">
            <el-form label-width="120px" size="large">
              <el-form-item label="红头文件样式">
                <el-input type="textarea" v-model="currentTemplate.officialHeaderHtml" :rows="2"
                  placeholder="请输入红头文件HTML代码" @input="updatePreview('officialHeader')" />
                <div class="tips">提示：红头文件通常包含红色标题线、单位名称、文件编号、标题等内容</div>
              </el-form-item>

              <el-form-item label="红头文件高度">
                <el-input-number v-model="currentTemplate.officialHeaderHeight" :min="50" :max="300" :step="10" />
                <span>px</span>
              </el-form-item>

              <el-form-item label="红色标题线颜色">
                <el-color-picker v-model="currentTemplate.headerLineColor" show-alpha />
                <el-input v-model="currentTemplate.headerLineColor" style="width: 120px; margin-left: 10px;"
                  placeholder="输入颜色值" />
              </el-form-item>

              <el-form-item label="标题线粗细">
                <el-input-number v-model="currentTemplate.headerLineWidth" :min="1" :max="10" />
                <span>px</span>
              </el-form-item>

              <el-form-item label="预览" v-if="preview.officialHeader">
                <div class="official-header-preview">
                  <div class="html-preview" v-html="preview.officialHeader"></div>
                  <div class="page-content">正文内容区域...</div>
                </div>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>

        <div class="editor-actions">
          <el-button type="primary" size="large" text bg @click="saveTemplate">保存模板</el-button>
          <el-button size="large" text bg @click="previewTemplate">预览效果</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

const templates = ref([
  {
    id: 1,
    name: '政府公文模板',
    description: '适用于政府机关公文排版',
    headings: Array(6).fill().map((_, i) => ({
      level: i + 1,
      fontFamily: 'SimSun',
      fontSize: 24 - i * 2,
      color: '#333333',
      align: 'center'
    })),
    body: {
      fontFamily: 'SimSun',
      fontSize: 16,
      lineHeight: 1.5,
      color: '#333333'
    },
    margin: {
      top: 30,
      bottom: 30,
      left: 28,
      right: 26
    },
    pageSize: 'A4',
    headerHeight: 15,
    footerHeight: 15,
    coverHtml: `<div style="text-align: center; padding: 50px 0; border: 1px solid #eee; background: #f9f9f9;">
        <h1 style="font-size: 36px; color: #333; margin-bottom: 30px;">文档标题</h1>
        <div style="font-size: 18px; color: #666; margin-bottom: 20px;">某某单位</div>
        <div style="font-size: 16px; color: #999;">日期：2023年XX月XX日</div>
      </div>
    `,

    headerHtml: `<div style="display: flex; justify-content: space-between; align-items: center; padding: 5px 10px; border-bottom: 1px solid #eee;">
        <div style="font-size: 12px; color: #333;">某某单位文件</div>
        <div style="font-size: 12px; color: #666;">第<span style="margin: 0 5px;">1</span>页</div>
      </div>
    `,

    footerHtml: `<div style="display: flex; justify-content: center; align-items: center; padding: 5px 10px; border-top: 1px solid #eee; font-size: 12px; color: #666;">
        <div>机密等级：普通</div>
        <div style="margin: 0 20px;">文件编号：XXXX-XX-XX</div>
        <div>打印日期：2023-XX-XX</div>
      </div>
    `,
    officialHeaderHtml: `<div style="text-align: center; margin-bottom: 20px;">
        <div style="font-size: 22px; font-weight: bold; color: #c00; margin-bottom: 5px;">
          某某市人民政府文件
        </div>
        <div style="font-size: 16px; color: #333; margin-bottom: 15px;">
          某政发〔2023〕12号
        </div>
        <div style="height: 2px; background: #c00; margin: 0 auto 15px; width: 100%;"></div>
        <div style="font-size: 24px; font-weight: bold; color: #333;">
          关于某某事项的通知
        </div>
      </div>
    `,
    officialHeaderHeight: 150,
    headerLineColor: '#c00',
    headerLineWidth: 2
  }
])

const currentTemplate = ref(null)
const preview = reactive({
  cover: '',
  header: '',
  footer: '',
  officialHeader: ''
})

// 新增边距预设选项
const marginPresets = ref([
  { label: '普通', value: 'normal', margins: { top: 25, bottom: 25, left: 25, right: 25 } },
  { label: '窄', value: 'narrow', margins: { top: 15, bottom: 15, left: 15, right: 15 } },
  { label: '适中', value: 'medium', margins: { top: 30, bottom: 30, left: 30, right: 30 } },
  { label: '宽', value: 'wide', margins: { top: 40, bottom: 40, left: 40, right: 40 } },
  { label: '公文标准', value: 'official', margins: { top: 37, bottom: 35, left: 28, right: 26 } } // GB/T 9704-2012 公文格式标准
])

const selectedMarginPreset = ref('')

// 应用预设边距
const applyMarginPreset = (presetValue) => {
  const preset = marginPresets.value.find(p => p.value === presetValue)
  if (preset) {
    currentTemplate.value.margin = { ...preset.margins }
  }
}

// // 页面预览样式计算
// const pageStyle = computed(() => {
//   return {
//     width: currentTemplate.value.pageSize === 'A4' ? '210mm' : '297mm',
//     height: currentTemplate.value.pageSize === 'A4' ? '297mm' : '420mm',
//     margin: '0 auto',
//     position: 'relative'
//   }
// })

// const headerStyle = computed(() => {
//   return {
//     height: `${currentTemplate.value.headerHeight}mm`,
//     backgroundColor: 'rgba(200, 200, 255, 0.3)',
//     borderBottom: '1px dashed #ccc'
//   }
// })

// const footerStyle = computed(() => {
//   return {
//     height: `${currentTemplate.value.footerHeight}mm`,
//     backgroundColor: 'rgba(200, 200, 255, 0.3)',
//     borderTop: '1px dashed #ccc'
//   }
// })

// const contentStyle = computed(() => {
//   return {
//     height: `calc(100% - ${currentTemplate.value.headerHeight + currentTemplate.value.footerHeight}mm)`,
//     position: 'relative',
//     backgroundColor: 'rgba(255, 255, 255, 0.8)'
//   }
// })

// const marginLineStyle = (side) => {
//   const marginValue = currentTemplate.value.margin[side]
//   const positionStyle = {
//     top: {
//       top: '0',
//       left: '0',
//       right: '0',
//       height: `${marginValue}mm`,
//       borderBottom: '1px dashed red'
//     },
//     bottom: {
//       bottom: '0',
//       left: '0',
//       right: '0',
//       height: `${marginValue}mm`,
//       borderTop: '1px dashed red'
//     },
//     left: {
//       top: '0',
//       bottom: '0',
//       left: '0',
//       width: `${marginValue}mm`,
//       borderRight: '1px dashed red'
//     },
//     right: {
//       top: '0',
//       bottom: '0',
//       right: '0',
//       width: `${marginValue}mm`,
//       borderLeft: '1px dashed red'
//     }
//   }
//   return positionStyle[side]
// }

// 页面预览样式计算（按比例缩小，宽度固定180px）
const pageStyle = computed(() => {
  // A4纸原始尺寸：210mm×297mm，比例约为1:1.414
  // 我们固定宽度为180px，计算高度为180 * 1.414 ≈ 254.52px
  const previewWidth = 180 // 固定预览宽度
  const previewHeight = currentTemplate.value.pageSize === 'A4'
    ? Math.round(previewWidth * 297 / 210)
    : Math.round(previewWidth * 420 / 297) // A3尺寸比例

  return {
    width: `${previewWidth}px`,
    height: `${previewHeight}px`,
    margin: '0 auto',
    position: 'relative',
    backgroundColor: 'white',
    // boxShadow: '0 0 5px rgba(0, 0, 0, 0.1)'
  }
})

const headerStyle = computed(() => {
  // 计算缩小比例：180px / 210mm ≈ 0.857px/mm
  const scale = 180 / 210
  const height = Math.round(currentTemplate.value.headerHeight * scale)

  return {
    height: `${height}px`,
    backgroundColor: 'rgba(200, 200, 255, 0.3)',
    borderBottom: '1px dashed #ccc',
    fontSize: '10px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center'
  }
})

const footerStyle = computed(() => {
  const scale = 180 / 210
  const height = Math.round(currentTemplate.value.footerHeight * scale)

  return {
    height: `${height}px`,
    backgroundColor: 'rgba(200, 200, 255, 0.3)',
    borderTop: '1px dashed #ccc',
    fontSize: '10px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center'
  }
})

const contentStyle = computed(() => {
  const scale = 180 / 210
  const headerHeight = Math.round(currentTemplate.value.headerHeight * scale)
  const footerHeight = Math.round(currentTemplate.value.footerHeight * scale)
  const totalHeight = parseInt(pageStyle.value.height) // 获取总高度px值

  return {
    height: `${totalHeight - headerHeight - footerHeight}px`,
    position: 'relative',
    backgroundColor: 'rgba(255, 255, 255, 0.8)'
  }
})

const marginLineStyle = (side) => {
  const scale = 180 / 210
  const marginValue = Math.round(currentTemplate.value.margin[side] * scale)

  const positionStyle = {
    top: {
      top: '0',
      left: '0',
      right: '0',
      height: `${marginValue}px`,
      borderBottom: '1px dashed red'
    },
    bottom: {
      bottom: '0',
      left: '0',
      right: '0',
      height: `${marginValue}px`,
      borderTop: '1px dashed red'
    },
    left: {
      top: '0',
      bottom: '0',
      left: '0',
      width: `${marginValue}px`,
      borderRight: '1px dashed red'
    },
    right: {
      top: '0',
      bottom: '0',
      right: '0',
      width: `${marginValue}px`,
      borderLeft: '1px dashed red'
    }
  }
  return positionStyle[side]
}

const addLayoutTemplate = () => {
  currentTemplate.value = {
    id: Date.now(),
    name: '新建模板',
    description: '',
    headings: Array(6).fill().map((_, i) => ({
      level: i + 1,
      fontFamily: 'SimSun',
      fontSize: 24 - i * 2,
      color: '#333333',
      align: 'left'
    })),
    body: {
      fontFamily: 'SimSun',
      fontSize: 12,
      lineHeight: 1.5,
      color: '#333333'
    },
    margin: {
      top: 30,
      bottom: 30,
      left: 25,
      right: 25
    },
    pageSize: 'A4',
    headerHeight: 15,
    footerHeight: 15,
    coverHtml: `<div style="text-align: center; padding: 50px 0; border: 1px solid #eee; background: #f9f9f9;">
        <h1 style="font-size: 36px; color: #333; margin-bottom: 30px;">文档标题</h1>
        <div style="font-size: 18px; color: #666; margin-bottom: 20px;">某某单位</div>
        <div style="font-size: 16px; color: #999;">日期：2023年XX月XX日</div>
      </div>
    `,

    headerHtml: `<div style="display: flex; justify-content: space-between; align-items: center; padding: 5px 10px; border-bottom: 1px solid #eee;">
        <div style="font-size: 12px; color: #333;">某某单位文件</div>
        <div style="font-size: 12px; color: #666;">第<span style="margin: 0 5px;">1</span>页</div>
      </div>
    `,

    footerHtml: `<div style="display: flex; justify-content: center; align-items: center; padding: 5px 10px; border-top: 1px solid #eee; font-size: 12px; color: #666;">
        <div>机密等级：普通</div>
        <div style="margin: 0 20px;">文件编号：XXXX-XX-XX</div>
        <div>打印日期：2023-XX-XX</div>
      </div>
    `,
    officialHeaderHtml: `<div style="text-align: center; margin-bottom: 20px;">
        <div style="font-size: 22px; font-weight: bold; color: #c00; margin-bottom: 5px;">
          单位名称文件
        </div>
        <div style="font-size: 16px; color: #333; margin-bottom: 15px;">
          文件编号
        </div>
        <div style="height: 2px; background: #c00; margin: 0 auto 15px; width: 100%;"></div>
        <div style="font-size: 24px; font-weight: bold; color: #333;">
          文件标题
        </div>
      </div>
    `,
    officialHeaderHeight: 150,
    headerLineColor: '#c00',
    headerLineWidth: 2
  }
  templates.value.push(currentTemplate.value)
  updateAllPreviews()
}

const editTemplate = (template) => {
  currentTemplate.value = template
  updateAllPreviews()
}

const deleteTemplate = (template) => {
  templates.value = templates.value.filter(item => item !== template)
  if (currentTemplate.value === template) {
    currentTemplate.value = null
  }
}

const saveTemplate = () => {
  console.log('保存模板:', currentTemplate.value)
}

const previewTemplate = () => {
  console.log('预览模板:', currentTemplate.value)
}

const updatePreview = (type) => {
  if (currentTemplate.value) {
    preview[type] = currentTemplate.value[`${type}Html`] || ''

    // 特殊处理红头文件的预览
    if (type === 'officialHeader') {
      preview.officialHeader = preview.officialHeader.replace(
        /height:\s*\d+px;\s*background:\s*[^;]+;/,
        `height: ${currentTemplate.value.headerLineWidth}px; background: ${currentTemplate.value.headerLineColor};`
      )
    }
  }
}

const updateAllPreviews = () => {
  if (currentTemplate.value) {
    preview.cover = currentTemplate.value.coverHtml || ''
    preview.header = currentTemplate.value.headerHtml || ''
    preview.footer = currentTemplate.value.footerHtml || ''
    preview.officialHeader = currentTemplate.value.officialHeaderHtml || ''

    // 更新红头文件预览中的标题线样式
    preview.officialHeader = preview.officialHeader.replace(
      /height:\s*\d+px;\s*background:\s*[^;]+;/,
      `height: ${currentTemplate.value.headerLineWidth}px; background: ${currentTemplate.value.headerLineColor};`
    )
  }
}

// 在 setup 脚本中添加：
onMounted(() => {
  if (templates.value.length > 0) {
    currentTemplate.value = templates.value[0]
    updateAllPreviews()
  }
})

</script>

<style lang="scss" scoped>
.layout-config {
  .layout-manager {
    display: flex;
    gap: 20px;

    .layout-list {
      flex: 0 0 50%;
    }

    .layout-editor {
      flex: 1;
      margin-top: 15px;

      h3 {
        margin-bottom: 20px;
        font-size: 16px;
        color: #333;
      }

      .style-controls {
        display: flex;
        align-items: center;
        gap: 10px;
      }

      .form-row {
        display: flex;
        align-items: center;
        gap: 10px;
      }

      .tips {
        font-size: 12px;
        color: #999;
        margin-top: 5px;
      }

      .html-preview {
        border: 1px solid #eee;
        padding: 10px;
        min-height: 100px;
        background: white;
      }

      .header-footer-preview {
        // border: 1px solid #eee;
        // padding: 20px;
        background: white;

        .html-preview {
          margin-bottom: 10px;
        }

        .page-content {
          padding: 0px;
          border-top: 1px dashed #eee;
          border-bottom: 1px dashed #eee;
          color: #666;
          min-height: 30px;
        }
      }

      .official-header-preview {
        // border: 1px solid #eee;
        // padding: 20px;
        background: white;

        .html-preview {
          margin-bottom: 20px;
        }

        .page-content {
          padding: 0px;
          border-top: 1px dashed #eee;
          color: #666;
        }
      }

      .editor-actions {
        margin-top: 20px;
        text-align: right;
      }


      // 页面预览
      .page-preview {
        width: 180px;
        padding: 0px;
        background: #f9f9f9;
        border: 1px solid #eee;

        .page {
          position: relative;

          .header,
          .footer {
            font-size: 10px;
            color: #666;
          }

          .content {
            .content-text {
              display: flex;
              align-items: center;
              justify-content: center;
              height: 100%;
              color: #999;
              font-size: 12px;
            }

            .margin-line {
              position: absolute;
              opacity: 0.6;
            }
          }
        }

        .page-size-info {
          margin-top: 8px;
          font-size: 12px;
          color: #666;
          line-height: 1.4rem;
          text-align: center;
        }
      }

    }
  }

  .mb-20 {
    margin-bottom: 20px;
  }
}
</style>