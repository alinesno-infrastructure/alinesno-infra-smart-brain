<template>
	<div class="statistic-card"> 
                    <el-statistic :value="2">
                      <template #title>
                        <div style="display: inline-flex; align-items: center">
                          审核规则数
                          <el-tooltip
                            effect="dark"
                            content="本次审核使用的规则数据"
                            placement="top"
                          >
                            <el-icon style="margin-left: 4px" :size="12">
                              <Warning />
                            </el-icon>
                          </el-tooltip>
                        </div>
                      </template>
                    </el-statistic>
                    <div class="statistic-footer">
                      <div class="footer-item">
                        <span>较多</span> 
                      </div>
                    </div>
                  </div>
</template>

<script setup>

const props = defineProps({
  currentSceneInfo: {
    type: Object, 
    required: false 
  },
  currentTaskInfo: {
    type: Object, 
    required: false 
  }
}) 

</script>