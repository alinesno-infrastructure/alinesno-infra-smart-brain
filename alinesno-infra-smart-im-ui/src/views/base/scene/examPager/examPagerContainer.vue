<!-- 试卷阅卷 -->
 <template> 
  <div class="exam-pagercontainer">
    <el-container style="height:calc(100vh - 40px );background-color: #fff;">
      <el-aside width="80px" class="exam-pager-aside">
        <FunctionList />
      </el-aside>

      <el-main class="exam-pager-manager" style="padding: 0px !important">
 
         <!-- 阅读界面 -->
          <slot></slot>

      </el-main>
      </el-container>
    </div>
 </template>

<!-- 逻辑 -->
<script setup>  

import FunctionList from './functionList'

const router = useRouter()
const route = useRoute()

const sceneId = ref(route.query.sceneId)

const props = defineProps({
  currentExam: {
    type: Object,
    required: false 
  }
})

// 返回
const goBack = () => {
  router.push({
    path: '/scene/examPager/examManager',
    query: { sceneId: sceneId.value }
  })
}

</script>

<!-- 样式 -->
<style lang="scss" scoped>
</style>