import { createWebHistory, createRouter } from 'vue-router'
/* Layout */
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

const screenRoutes = [
  // -->>>>>>>>>>>> 长文本场景_start >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
  {
    path: '/scene/longText/index',
    component: () => import('@/views/base/scene/longText/index'),
    name: '/scene/longText/index',
    meta: { title: '长文本场景', icon: 'dashboard', affix: true }
  },
  {
    path: '/scene/longText/textParser',
    component: () => import('@/views/base/scene/longText/textParser'),
    name: '/scene/longText/textParser',
    meta: { title: '长文本场景', icon: 'dashboard', affix: true }
  },
  // -->>>>>>>>>>>> 长文本场景_end >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

  // -->>>>>>>>>>>> 文档审核场景_start >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
  {
    path: '/scene/documentReview/index',
    component: () => import('@/views/base/scene/documentReview/index'),
    name: '/scene/documentReview/index',
    meta: { title: '文档审核场景', icon: 'fa-solid fa-file-pen', affix: true }
  },
  {
    path: '/scene/documentReview/documentParser',
    component: () => import('@/views/base/scene/documentReview/documentParser'),
    name: '/scene/documentReview/documentParser',
    meta: { title: '文档审核解析', icon: 'fa-solid fa-file-pen', affix: true }
  },
  {
    path: '/scene/documentReview/documentDataset',
    component: () => import('@/views/base/scene/documentReview/datasetList'),
    name: '/scene/documentReview/documentDataset',
    meta: { title: '文档审核知识库', icon: 'fa-solid fa-file-pen', affix: true }
  },
  {
    path: '/scene/documentReview/addDataset',
    component: () => import('@/views/base/scene/documentReview/addDataset'),
    name: '/scene/documentReview/addDataset',
    meta: { title: '添加审核清单', icon: 'fa-solid fa-file-pen', affix: true }
  },
  // -->>>>>>>>>>>> 文档审核场景_end >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
  
  // -->>>>>>>>>>>> 文档阅读场景_start >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
  {
    path: '/scene/documentReader/index',
    component: () => import('@/views/base/scene/documentReader/index'),
    name: '/scene/documentReader/index',
    meta: { title: '文档阅读场景', icon: 'fa-solid fa-book-open', affix: true }
  },
  {
    path: '/scene/documentReader/documentParser',
    component: () => import('@/views/base/scene/documentReader/documentParser'),
    name: '/scene/documentReader/documentParser',
    meta: { title: '文档阅读解析', icon: 'fa-solid fa-file-pen', affix: true }
  },
  // -->>>>>>>>>>>> 文档阅读场景_end >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

  // -->>>>>>>>>>>> 生成试卷场景_start >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
  {
    path: '/scene/examPager/index',
    component: () => import('@/views/base/scene/examPager/index'),
    name: '/scene/examPager/index',
    meta: { title: '生成试卷场景', icon: 'fa-solid fa-file-invoice', affix: true }
  },
  {
    path: '/scene/examPager/agentParser',
    component: () => import('@/views/base/scene/examPager/agentParser'),
    name: '/scene/examPager/agentParser',
    meta: { title: '生成试卷', icon: 'fa-solid fa-file-invoice', affix: true }
  },
  {
    path: '/scene/examPager/quickPaper',
    component: () => import('@/views/base/scene/examPager/quickPaper'),
    name: '/scene/examPager/quickPaper',
    meta: { title: '快速组卷', icon: 'fa-solid fa-file-invoice', affix: true }
  },
  {
    path: '/scene/examPager/pagerManager',
    component: () => import('@/views/base/scene/examPager/pagerManager'),
    name: '/scene/examPager/pagerManager',
    meta: { title: '试卷管理', icon: 'fa-solid fa-file-invoice', affix: true }
  },
  {
    path: '/scene/examPager/questionBankManager',
    component: () => import('@/views/base/scene/examPager/questionBankManager'),
    name: '/scene/examPager/questionBankManager',
    meta: { title: '题库管理', icon: 'fa-solid fa-file-invoice', affix: true }
  },
  {
    path: '/scene/examPager/examPagerView',
    component: () => import('@/views/base/scene/examPager/examPagerView'),
    name: '/scene/examPager/examPagerView',
    meta: { title: '试卷编辑', icon: 'fa-solid fa-file-invoice', affix: true }
  },
  {
    path: '/scene/examPager/examManager',
    component: () => import('@/views/base/scene/examPager/examManager'),
    name: '/scene/examPager/examManager',
    meta: { title: '考试管理', icon: 'fa-solid fa-file-invoice', affix: true }
  },
  // -->>>>>>>>>>>> 生成试卷场景_end >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

  // -->>>>>>>>>>>> 文章编辑_start >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
  {
    path: '/scene/articleWriting/index',
    component: () => import('@/views/base/scene/articleWriting/index'),
    name: '/scene/articleWriting/index',
    meta: { title: '文章编辑', icon: 'fa-solid fa-file-word', affix: true }
  },
  {
    path: '/scene/articleWriting/articleManager',
    component: () => import('@/views/base/scene/articleWriting/articleManager'),
    name: '/scene/articleWriting/articleManager',
    meta: { title: '文章管理', icon: 'fa-solid fa-file-word', affix: true }
  },
  {
    path: '/scene/articleWriting/articleEditPager',
    component: () => import('@/views/base/scene/articleWriting/articleEditPager'),
    name: '/scene/articleWriting/articleEditPager',
    meta: { title: '文章编辑', icon: 'fa-solid fa-file-word', affix: true }
  },
  // -->>>>>>>>>>>> 文章编辑_end >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

  // -->>>>>>>>>>>> 原型设计_start >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
  {
    path: '/scene/prototypeDesign/index',
    component: () => import('@/views/base/scene/prototypeDesign/index'),
    name: '/scene/prototypeDesign/index',
    meta: { title: '原型设计', icon: 'fa-solid fa-file-word', affix: true }
  },
  {
    path: '/scene/prototypeDesign/prototypeManager',
    component: () => import('@/views/base/scene/prototypeDesign/prototypeManager'),
    name: '/scene/prototypeDesign/prototypeManager',
    meta: { title: '原型管理', icon: 'fa-solid fa-file-word', affix: true }
  },
  {
    path: '/scene/prototypeDesign/prototypeParser',
    component: () => import('@/views/base/scene/prototypeDesign/components/prototypeParser'),
    name: '/scene/prototypeDesign/prototypeParser',
    meta: { title: '原型编辑', icon: 'fa-solid fa-file-word', affix: true }
  },
  // -->>>>>>>>>>>> 原型设计_end >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

  // -->>>>>>>>>>>> PPT编辑_start >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
  {
    path: '/scene/pptCreation/index',
    component: () => import('@/views/base/scene/pptCreation/index'),
    name: '/scene/pptCreation/index',
    meta: { title: 'PPT编辑', icon: 'fa-solid fa-file-word', affix: true }
  },
  {
    path: '/scene/pptCreation/pptManager',
    component: () => import('@/views/base/scene/pptCreation/pptManager'),
    name: '/scene/pptCreation/pptManager',
    meta: { title: 'PPT管理', icon: 'fa-solid fa-file-word', affix: true }
  },
  // -->>>>>>>>>>>> PPT编辑_end >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

  // -->>>>>>>>>>>> 内容排版_start >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
  {
    path: '/scene/contentFormatter/index',
    component: () => import('@/views/base/scene/contentFormatter/index'),
    name: '/scene/contentFormatter/index',
    meta: { title: '内容排版场景', icon: 'fa-solid fa-file-word', affix: true }
  },
  {
    path: '/scene/contentFormatter/contentParser',
    component: () => import('@/views/base/scene/contentFormatter/contentParser'),
    name: '/scene/contentFormatter/contentParser',
    meta: { title: '内容排版场景', icon: 'fa-solid fa-file-word', affix: true }
  },
  // -->>>>>>>>>>> 内容排版_end>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

  // -->>>>>>>>>>>> 短视频生成场景_start >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
  {
    path: '/scene/videoGeneration/index',
    component: () => import('@/views/base/scene/videoGeneration/index'),
    name: '/scene/videoGeneration/index',
    meta: { title: '短视频生成场景', icon: 'fa-solid fa-file-word', affix: true }
  },
  // -->>>>>>>>>>>> 短视频生成场景_end >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

  // -->>>>>>>>>>>> 会议纪要场景_start >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
  {
    path: '/scene/meetingMinutes/index',
    component: () => import('@/views/base/scene/meetingMinutes/index'),
    name: '/scene/meetingMinutes/index',
    meta: { title: '会议纪要场景', icon: 'fa-solid fa-calendar-check', affix: true }
  },
  // -->>>>>>>>>>>> 会议纪要场景_end >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

  // -->>>>>>>>>>>> 产品进度汇总场景_start >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
  {
    path: '/scene/productResearch/index',
    component: () => import('@/views/base/scene/productResearch/index'),
    name: '/scene/productResearch/index',
    meta: { title: '产品进度汇总', icon: 'fa-solid fa-chart-line', affix: true }
  },
  {
    path: '/scene/productResearch/agentChatPanel',
    component: () => import('@/views/base/scene/productResearch/agentChatPanel'),
    name: '/scene/productResearch/agentChatPanel',
    meta: { title: 'AI数据分析', icon: 'fa-solid fa-chart-line', affix: true }
  },
  {
    path: '/scene/productResearch/projectManagement',
    component: () => import('@/views/base/scene/productResearch/projectManagement'),
    name: '/scene/productResearch/projectManagement',
    meta: { title: '项目管理', icon: 'fa-solid fa-chart-line', affix: true }
  },
  {
    path: '/scene/productResearch/taskMonitoring',
    component: () => import('@/views/base/scene/productResearch/taskMonitoring'),
    name: '/scene/productResearch/taskMonitoring',
    meta: { title: '任务监控', icon: 'fa-solid fa-chart-line', affix: true }
  },
  {
    path: '/scene/productResearch/teamConfiguration',
    component: () => import('@/views/base/scene/productResearch/teamConfiguration'),
    name: '/scene/productResearch/teamConfiguration',
    meta: { title: '团队配置', icon: 'fa-solid fa-chart-line', affix: true }
  },
  // -->>>>>>>>>>>> 产品进度汇总场景_end >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

  // -->>>>>>>>>>>> 通用智能体_start >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
  {
    path: '/scene/generalAgent/index',
    component: () => import('@/views/base/scene/generalAgent/index'),
    name: '/scene/generalAgent/index',
    meta: { title: '通用智能体', icon: 'fa-solid fa-chart-line', affix: true }
  },
  {
    path: '/scene/generalAgent/agentParser',
    component: () => import('@/views/base/scene/generalAgent/agentParser'),
    name: '/scene/generalAgent/agentParser',
    meta: { title: '通用智能体', icon: 'fa-solid fa-chart-line', affix: true }
  },
  // -->>>>>>>>>>>> 通用智能体_end >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
];

// 深度搜索deepsearch
const deepsearchRoutes = [
  {
    path: '/scene/deepsearch/index',
    component: () => import('@/views/base/scene/deepsearch/index'),
    name: '/scene/deepsearch/index',
    meta: { title: '深度搜索', icon: 'fa-solid fa-magnifying-glass', affix: true }
  },
]

// 公共路由
export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login'),
    hidden: true
  },
  {
    path: '/createOrg',
    component: () => import('@/views/createOrg'),
    hidden: true
  },
  {
    path: '/demo',
    component: () => import('@/views/demo'),
    hidden: true
  },
  {
    path: '/agentChat',
    component: () => import('@/views/base/agent/agentChatPanel'),
    hidden: true
  },
  {
    path: '/channelChat',
    component: () => import('@/views/base/chat/channelChatPanel'),
    hidden: true
  },
  {
    path: '/sso/login',
    component: () => import('@/views/loginSso'),
    hidden: true
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
        component: () => import('@/views/base/portal/index'),
        name: '/index',
        meta: { title: '组织工作台', icon: 'dashboard', affix: true }
      },
      {
        path: '/channel',
        component: () => import('@/views/base/chat/channelHome'),
        name: '/channel',
        meta: { title: '频道列表', icon: 'dashboard', affix: true }
      },
      {
        path: '/scene',
        component: () => import('@/views/base/scene/index'),
        name: '/scene',
        meta: { title: '场景列表', icon: 'dashboard', affix: true }
      },
      {
        path: '/store',
        component: () => import('@/views/base/store/index'),
        name: '/store',
        meta: { title: '智能体商店', icon: 'dashboard', affix: true }
      },

      ...screenRoutes ,

      ...deepsearchRoutes ,

      {
        path: '/single/agentChat',
        component: () => import('@/views/base/agent/agentSingleChatPanel'),
        name: '/single/agentChat',
        meta: { title: '首页', icon: 'dashboard', affix: true }
      },
      {
        path: '/chat',
        component: () => import('@/views/base/chat/smartService'),
        name: '/chat',
        meta: { title: '聊天', icon: 'dashboard', affix: true }
      },
      {
        path: '/dashboard/smartService',
        component: () => import('@/views/smartService'),
        name: '/dashboard/smartService',
        meta: { title: '智能客服', icon: 'dashboard', affix: true }
      },
      {
        path: '/agentList',
        component: () => import('@/views/base/chat/agentList'),
        name: '/agentList',
        meta: { title: '服务列表', icon: 'dashboard', affix: true }
      },
      {
        path: '/agentMarket',
        component: () => import('@/views/base/market/agents'),
        name: '/agentMarket',
        meta: { title: '智能体市场', icon: 'dashboard', affix: true }
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

      // ->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
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
