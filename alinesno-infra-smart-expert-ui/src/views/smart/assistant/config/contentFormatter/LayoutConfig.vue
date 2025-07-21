<template>
  <div class="layout-config">
    <div class="layout-manager">
      <div class="layout-list">
        <el-button type="primary" @click="addLayoutTemplate" class="mb-20">添加排版模板</el-button>

        <el-table :data="templates" border style="width: 100%">
          <el-table-column prop="name" label="模板名称" width="180" />
          <el-table-column prop="description" label="描述" />
          <el-table-column label="操作" width="150">
            <template #default="{row}">
              <el-button size="small" @click="editTemplate(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="deleteTemplate(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="layout-editor" v-if="currentTemplate">
        <h3>模板编辑: {{ currentTemplate.name }}</h3>

        <el-tabs type="border-card">
          <el-tab-pane label="标题样式">
            <div class="style-editor">
              <el-form label-width="100px">
                <el-form-item v-for="level in 6" :key="level" :label="`标题 ${level}`">
                  <div class="style-controls">
                    <el-select v-model="currentTemplate.headings[level-1].fontFamily" placeholder="字体" style="width: 120px">
                      <el-option label="宋体" value="SimSun" />
                      <el-option label="黑体" value="SimHei" />
                      <el-option label="微软雅黑" value="Microsoft YaHei" />
                    </el-select>
                    <el-input-number v-model="currentTemplate.headings[level-1].fontSize" :min="10" :max="36" controls-position="right" />
                    <span>px</span>
                    <el-color-picker v-model="currentTemplate.headings[level-1].color" />
                    <el-select v-model="currentTemplate.headings[level-1].align" placeholder="对齐" style="width: 100px">
                      <el-option label="左对齐" value="left" />
                      <el-option label="居中" value="center" />
                      <el-option label="右对齐" value="right" />
                    </el-select>
                  </div>
                </el-form-item>
              </el-form>
            </div>
          </el-tab-pane>

          <el-tab-pane label="正文样式">
            <el-form label-width="100px">
              <el-form-item label="字体">
                <el-select v-model="currentTemplate.body.fontFamily" placeholder="字体" style="width: 120px">
                  <el-option label="宋体" value="SimSun" />
                  <el-option label="黑体" value="SimHei" />
                  <el-option label="微软雅黑" value="Microsoft YaHei" />
                </el-select>
              </el-form-item>
              <el-form-item label="字号">
                <el-input-number v-model="currentTemplate.body.fontSize" :min="10" :max="24" controls-position="right" />
                <span>px</span>
              </el-form-item>
              <el-form-item label="行高">
                <el-slider v-model="currentTemplate.body.lineHeight" :min="1" :max="2" :step="0.1" show-input />
              </el-form-item>
              <el-form-item label="颜色">
                <el-color-picker v-model="currentTemplate.body.color" />
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <el-tab-pane label="页面设置">
            <el-form label-width="100px">
              <el-form-item label="页边距(mm)">
                <div class="form-row">
                  <el-input-number v-model="currentTemplate.margin.top" :min="10" :max="50" /> 上
                  <el-input-number v-model="currentTemplate.margin.bottom" :min="10" :max="50" /> 下
                  <el-input-number v-model="currentTemplate.margin.left" :min="10" :max="50" /> 左
                  <el-input-number v-model="currentTemplate.margin.right" :min="10" :max="50" /> 右
                </div>
              </el-form-item>
              <el-form-item label="纸张大小">
                <el-select v-model="currentTemplate.pageSize" placeholder="请选择纸张大小">
                  <el-option label="A4" value="A4" />
                  <el-option label="A3" value="A3" />
                  <el-option label="Letter" value="Letter" />
                </el-select>
              </el-form-item>
              <el-form-item label="页眉高度">
                <el-input-number v-model="currentTemplate.headerHeight" :min="0" :max="50" />
                <span>mm</span>
              </el-form-item>
              <el-form-item label="页脚高度">
                <el-input-number v-model="currentTemplate.footerHeight" :min="0" :max="50" />
                <span>mm</span>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <el-tab-pane label="封面设置">
            <el-form label-width="100px">
              <el-form-item label="封面HTML">
                <el-input
                  type="textarea"
                  v-model="currentTemplate.coverHtml"
                  :rows="6"
                  placeholder="请输入封面HTML代码"
                  @input="updatePreview('cover')"
                />
              </el-form-item>
              <el-form-item label="预览">
                <div class="html-preview" v-html="preview.cover"></div>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <el-tab-pane label="页眉页脚">
            <el-form label-width="100px">
              <el-form-item label="页眉HTML">
                <el-input
                  type="textarea"
                  v-model="currentTemplate.headerHtml"
                  :rows="6"
                  placeholder="请输入页眉HTML代码"
                  @input="updatePreview('header')"
                />
              </el-form-item>
              <el-form-item label="页脚HTML">
                <el-input
                  type="textarea"
                  v-model="currentTemplate.footerHtml"
                  :rows="6"
                  placeholder="请输入页脚HTML代码"
                  @input="updatePreview('footer')"
                />
              </el-form-item>
              <el-form-item label="预览">
                <div class="header-footer-preview">
                  <div class="html-preview" v-html="preview.header"></div>
                  <div class="page-content">文档内容区域...</div>
                  <div class="html-preview" v-html="preview.footer"></div>
                </div>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <el-tab-pane label="红头文件配置">
            <el-form label-width="120px">
              <el-form-item label="红头文件HTML">
                <el-input
                  type="textarea"
                  v-model="currentTemplate.officialHeaderHtml"
                  :rows="8"
                  placeholder="请输入红头文件HTML代码"
                  @input="updatePreview('officialHeader')"
                />
                <div class="tips">提示：红头文件通常包含红色标题线、单位名称、文件编号、标题等内容</div>
              </el-form-item>

              <el-form-item label="红头文件高度">
                <el-input-number
                  v-model="currentTemplate.officialHeaderHeight"
                  :min="50"
                  :max="300"
                  :step="10"
                />
                <span>px</span>
              </el-form-item>

              <el-form-item label="红色标题线颜色">
                <el-color-picker v-model="currentTemplate.headerLineColor" show-alpha />
                <el-input
                  v-model="currentTemplate.headerLineColor"
                  style="width: 120px; margin-left: 10px;"
                  placeholder="输入颜色值"
                />
              </el-form-item>

              <el-form-item label="标题线粗细">
                <el-input-number
                  v-model="currentTemplate.headerLineWidth"
                  :min="1"
                  :max="10"
                />
                <span>px</span>
              </el-form-item>

              <el-form-item label="预览">
                <div class="official-header-preview">
                  <div class="html-preview" v-html="preview.officialHeader"></div>
                  <div class="page-content">正文内容区域...</div>
                </div>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>

        <div class="editor-actions">
          <el-button type="primary" @click="saveTemplate">保存模板</el-button>
          <el-button @click="previewTemplate">预览效果</el-button>
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
      level: i+1,
      fontFamily: 'SimSun',
      fontSize: 24 - i*2,
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
    coverHtml: '',
    headerHtml: '',
    footerHtml: '',
    officialHeaderHtml: `
      <div style="text-align: center; margin-bottom: 20px;">
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

const addLayoutTemplate = () => {
  currentTemplate.value = {
    id: Date.now(),
    name: '新建模板',
    description: '',
    headings: Array(6).fill().map((_, i) => ({
      level: i+1,
      fontFamily: 'SimSun',
      fontSize: 24 - i*2,
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
    coverHtml: '',
    headerHtml: '',
    footerHtml: '',
    officialHeaderHtml: `
      <div style="text-align: center; margin-bottom: 20px;">
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
</script>

<style lang="scss" scoped>
.layout-config {
  .layout-manager {
    display: flex;
    gap: 20px;

    .layout-list {
      flex: 0 0 300px;
    }

    .layout-editor {
      flex: 1;

      h3 {
        margin-bottom: 20px;
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
        border: 1px solid #eee;
        padding: 20px;
        background: white;

        .html-preview {
          margin-bottom: 10px;
        }

        .page-content {
          padding: 20px;
          border-top: 1px dashed #eee;
          border-bottom: 1px dashed #eee;
          color: #666;
          min-height: 100px;
        }
      }

      .official-header-preview {
        border: 1px solid #eee;
        padding: 20px;
        background: white;

        .html-preview {
          margin-bottom: 20px;
        }

        .page-content {
          padding: 20px;
          border-top: 1px dashed #eee;
          color: #666;
        }
      }

      .editor-actions {
        margin-top: 20px;
        text-align: right;
      }
    }
  }

  .mb-20 {
    margin-bottom: 20px;
  }
}
</style>