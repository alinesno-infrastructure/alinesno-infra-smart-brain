import { createWebHistory, createRouter } from 'vue-router'
/* Layout */
import WorkflowLayout from '@/layout/WorkflowLayout'
import Layout from '@/layout/SaaSLayout'
// import Layout from '@/layout'

/**
 * Note: 路由配置项
 *
 * hidden: true                     // 当设置 true 的时候该路由不会再侧边栏出现 如401，login等页面，或者如一些编辑页面/edit/1
 * alwaysShow: true                 // 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
 *                                  // 只有一个时，会将那个子路由当做根路由显示在侧边栏--如引导页面
 *                                  // 若你想不管路由下面的 children 声明的个数都显示你的根路由
 *                                  // 你可以设置 alwaysShow: true，这样它就会忽略之前定义的规则，一直显示根路由
 * redirect: noRedirect             // 当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
 * name:'router-name'               // 设定路由的名字，一定要填写不然使用<keep-alive>时会出现各种问题
 * query: '{"id": 1, "name": "ry"}' // 访问路由的默认传递参数
 * roles: ['admin', 'common']       // 访问路由的角色权限
 * permissions: ['a:a:a', 'b:b:b']  // 访问路由的菜单权限
 * meta : {
    noCache: true                   // 如果设置为true，则不会被 <keep-alive> 缓存(默认 false)
    title: 'title'                  // 设置该路由在侧边栏和面包屑中展示的名字
    icon: 'svg-name'                // 设置该路由的图标，对应路径src/assets/icons/svg
    breadcrumb: false               // 如果设置为false，则不会在breadcrumb面包屑中显示
    activeMenu: '/system/user'      // 当路由设置了该属性，则会高亮相对应的侧边栏。
  }
 */

// 公共路由
export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login'),
    hidden: true
  },
  {
     path: '/sso/login',
     component: () => import('@/views/loginSso'),
     hidden: true
  },
  {
    path: '/chat/publish',
    component: () => import('@/views/smart/publish/index'),
    name: '/chat/publish',
    meta: { title: '分享聊天', icon: 'dashboard', affix: true }
  },
  {
    path: "/:pathMatch(.*)*",
    component: () => import('@/views/error/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error/401'),
    hidden: true
  },
  {
    path: '',
    component: Layout,
    redirect: '/index',
    hidden: true,
    children: [
      {
        path: '/index',
        component: () => import('@/views/index'),
        name: '/dashboard',
        meta: { title: '首页', icon: 'dashboard', affix: true }
      },
      {
        path: '/global/config',
        component: () => import('@/views/smart/assistant/config/index'),
        name: '/global/config',
        meta: { title: '全局配置', icon: 'dashboard', affix: true }
      },
      {
        path: '/smart/assistant/config/mcpConfig',
        component: () => import('@/views/smart/assistant/config/mcpConfig'),
        name: '/smart/assistant/config/mcpConfig',
        meta: { title: 'MCP配置界面', icon: 'dashboard', affix: true }
      },
      {
        path: '/smart/assistant/config/globalConfig',
        component: () => import('@/views/smart/assistant/config/globalConfig'),
        name: '/smart/assistant/config/globalConfig',
        meta: { title: '团队配置', icon: 'dashboard', affix: true }
      },
      {
        path: '/dashboard/suportTechnique',
        component: () => import('@/views/suportTechnique'),
        name: '/dashboard/suportTechnique',
        meta: { title: '支持管理', icon: 'dashboard', affix: true }
      },
      {
        path: '/dashboard/learnPanel',
        component: () => import('@/views/learnPanel'),
        name: '/dashboard/learnPanel',
        meta: { title: '学习手册', icon: 'dashboard', affix: true }
      },
      {
        path: '/expert/smart/assistant/role/script',
        component: () => import('@/views/smart/assistant/role/executeScriptPanel'),
        name: '/expert/smart/assistant/role/script',
        meta: { title: '脚本配置', icon: 'dashboard', affix: true }
      },
      {
        path: '/expert/smart/assistant/tool/script',
        component: () => import('@/views/smart/assistant/plugin/script'),
        name: '/expert/smart/assistant/tool/script',
        meta: { title: '工具脚本', icon: 'dashboard', affix: true }
      },
      {
        path: '/expert/smart/assistant/role/react',
        component: () => import('@/views/smart/assistant/role/reactPanel'),
        name: '/expert/smart/assistant/role/react',
        meta: { title: '推理配置', icon: 'dashboard', affix: true }
      },
      {
        path: '/knowledge/knowledge/index',
        component: () => import('@/views/smart/assistant/role/knowledge/parseDataset'),
        name: '/knowledge/knowledge/index',
        meta: { title: '知识库配置', icon: 'dashboard', affix: true }
      },
      {
        path: '/smart/assistant/llmModel/index',
        hidden: false,
        component: () => import('@/views/smart/assistant/llmModel/index'),
        name: '/smart/assistant/llmModel/index',
        meta: { title: '模型管理', icon: 'fa-solid fa-code-pull-request', affix: false }
      },
      {
        path: '/smart/assistant/roleTemplate/add',
        hidden: false,
        component: () => import('@/views/smart/assistant/roleTemplate/add'),
        name: '/smart/assistant/roleTemplate/add',
        meta: { title: '添加角色模板', icon: 'fa-solid fa-code-pull-request', affix: false }
      },
      {
        path: '/base/search/vectorData/index',
        component: () => import('@/views/base/search/vectorData/index'),
        name: '/base/search/vectorData/index',
        meta: { title: '数据集管理', icon: 'dashboard', affix: true }
      },
      {
        path: '/base/search/vectorData/knowledgeDetail',
        component: () => import('@/views/base/search/vectorData/knowledgeDetail'),
        name: '/base/search/vectorData/knowledgeDetail',
        meta: { title: '数据集管理', icon: 'dashboard', affix: true }
      },
      {
        path: '/base/search/vectorData/parseDataset',
        component: () => import('@/views/base/search/vectorData/parseDataset'),
        name: '/base/search/vectorData/parseDataset',
        meta: { title: '解析向量库管理', icon: 'dashboard', affix: true }
      },
      {
        path: '/base/search/vectorData/websiteImport' , 
        component: () => import('@/views/base/search/vectorData/websiteImport'),
        name: '/base/search/vectorData/websiteImport' , 
        meta: { title: 'Web站点同步', icon: 'dashboard', affix: true }
      },
      {
        path: '/base/search/vectorData/dataUpload' , 
        component: () => import('@/views/base/search/vectorData/dataUpload'),
        name: '/base/search/vectorData/dataUpload' , 
        meta: { title: '数据集上传', icon: 'dashboard', affix: true }
      },
      {
        path: '/base/search/memoryData/index',
        component: () => import('@/views/base/search/memory/displayMemory'),
        name: '/base/search/memoryData/index',
        meta: { title: '记忆库管理', icon: 'dashboard', affix: true }
      },
      {
        path: '/base/search/catalog/index',
        component: () => import('@/views/base/search/catalog/index'),
        name: '/base/search/catalog/index',
        meta: { title: '数据集分类', icon: 'dashboard', affix: true }
      },
      {
        path: '/expert/smart/assistant/role/workflowAgent',
        component: () => import('@/views/smart/assistant/role/workflowAgent'),
        name: '/expert/smart/assistant/role/workflowAgent',
        meta: { title: '工作流角色', icon: 'dashboard', affix: true }
      },
      {
        path: '/expert/smart/assistant/role/constomScript',
        component: () => import('@/views/smart/assistant/role/constomScript'),
        name: '/expert/smart/assistant/role/constomScript',
        meta: { title: '自定义脚本', icon: 'dashboard', affix: true }
      },
      {
        path: '/expert/smart/assistant/role/deepSearch',
        component: () => import('@/views/smart/assistant/role/deepSearch'),
        name: '/expert/smart/assistant/role/deepSearch',
        meta: { title: '深度搜索', icon: 'dashboard', affix: true }
      },
      {
        path: '/template/smart/assistant/plugin/type',
        component: () => import('@/views/smart/assistant/plugin/type'),
        name: '/template/smart/assistant/plugin/type',
        meta: { title: '类型管理', icon: 'dashboard', affix: true }
      },
      {
        path: '/expert/smart/assistant/role/agentInference',
        component: () => import('@/views/smart/assistant/role/agentInference'),
        name: '/expert/smart/assistant/role/agentInference',
        meta: { title: 'Agent推理角色', icon: 'dashboard', affix: true }
      },
      {
        path: '/expert/smart/assistant/role/publishChannel' , 
        component: () => import('@/views/smart/assistant/publishChannel/index'),
        name: '/expert/smart/assistant/role/publishChannel' , 
        meta: { title: '推送渠道', icon: 'dashboard', affix: true }
      },
      {
        path: '/expert/smart/assistant/role/welcomePage' , 
        component: () => import('@/views/smart/assistant/welcome/index'),
        name: '/expert/smart/assistant/role/welcomePage' , 
        meta: { title: '欢迎页配置', icon: 'dashboard', affix: true }
      },
      {
        path: '/expert/smart/assistant/reviews/index' , 
        component: () => import('@/views/smart/assistant/reviews/index'),
        name: '/expert/smart/assistant/reviews/index' , 
        meta: { title: '审核清单配置', icon: 'dashboard', affix: true }
      },
      {
        path: '/expert/smart/assistant/reviews/addAudit' , 
        component: () => import('@/views/smart/assistant/reviews/addAudit'),
        name: '/expert/smart/assistant/reviews/addAudit' , 
        meta: { title: '添加审核清单', icon: 'dashboard', affix: true }
      },
      {
        path: '/expert/smart/assistant/reviews/ruleList' , 
        component: () => import('@/views/smart/assistant/reviews/ruleList'),
        name: '/expert/smart/assistant/reviews/ruleList' , 
        meta: { title: '添加审核规则清单', icon: 'dashboard', affix: true }
      },
      {
        path: '/template/smart/assistant/template/addTemplate' , 
        component: () => import('@/views/smart/assistant/template/addTemplate'),
        name: '/template/smart/assistant/template/addTemplate' , 
        meta: { title: '添加模板', icon: 'dashboard', affix: true }
      },
      {
        path: "/smart/assistant/template/articleTemplate",
        component: () => import('@/views/smart/assistant/template/articleTemplate'),
        name: "/smart/assistant/template/articleTemplate",
        meta: { title: '文章模板', icon: 'dashboard', affix: true }
      },
      {
        path: "/smart/assistant/template/longTextTemplate",
        component: () => import('@/views/smart/assistant/template/longTextTemplate'),
        name: "/smart/assistant/template/longTextTemplate",
        meta: { title: '长文本模板', icon: 'dashboard', affix: true }
      },
      {
        path: "/smart/assistant/config/generalAgent",
        component: () => import('@/views/smart/assistant/config/generalAgent'),
        name: "/smart/assistant/template/generalAgent",
        meta: { title: '通用智能体配置', icon: 'dashboard', affix: true }
      },
      {
        path: "/smart/assistant/config/officialDocument",
        component: () => import('@/views/smart/assistant/config/officialDocument'),
        name: "/smart/assistant/config/officialDocument",
        meta: { title: '智能文档配置', icon: 'dashboard', affix: true }
      },
    ]
  },
  {
    path: '/flow',
    component: WorkflowLayout,
    hidden: true,
    redirect: 'noredirect',
    children: [
      {
        path: '/smart/assistant/role/createDefinition',
        component: () => import('@/views/smart/assistant/workflow/index'),
        name: '/smart/assistant/role/createDefinition',
        meta: { title: '添加流程任务', icon: 'fa-solid fa-code-pull-request', affix: false }
      }
    ]
  },
  {
    path: '/user',
    component: Layout,
    hidden: true,
    redirect: 'noredirect',
    children: [
      {
        path: 'profile',
        component: () => import('@/views/system/user/profile/index'),
        name: 'Profile',
        meta: { title: '个人中心', icon: 'user' }
      }
    ]
  }
]

// 动态路由，基于用户权限动态去加载
export const dynamicRoutes = [
]

const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  },
});

export default router;
