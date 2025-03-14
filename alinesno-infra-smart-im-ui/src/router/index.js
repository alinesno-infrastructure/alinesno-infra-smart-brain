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
    component: () => import('@/views/base/specialist/channelChatPanel'),
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
        component: () => import('@/views/base/specialist/channelHome'),
        name: '/index',
        meta: { title: '首页', icon: 'dashboard', affix: true }
      },
      {
        path: '/screen',
        component: () => import('@/views/base/screen/index'),
        name: '/screen',
        meta: { title: '场景列表', icon: 'dashboard', affix: true }
      },
      {
        path: '/screen/leaderModel',
        component: () => import('@/views/base/screen/leaderModel'),
        name: '/screen/leaderModel',
        meta: { title: '管理者模式', icon: 'dashboard', affix: true }
      },
      {
        path: '/screen/leaderPlan',
        component: () => import('@/views/base/screen/leaderPlan'),
        name: '/screen/leaderPlan',
        meta: { title: '管理者计划', icon: 'dashboard', affix: true }
      },
      {
        path: '/screen/longText',
        component: () => import('@/views/base/screen/longText'),
        name: '/screen/longText',
        meta: { title: '长文本场景', icon: 'dashboard', affix: true }
      },
      {
        path: '/screen/mediaClip',
        component: () => import('@/views/base/screen/media'),
        name: '/screen/mediaClip',
        meta: { title: '视频剪辑', icon: 'dashboard', affix: true }
      },
      {
        path: '/screen/exam',
        component: () => import('@/views/base/screen/exam'),
        name: '/screen/exam',
        meta: { title: '考试场景', icon: 'dashboard', affix: true }
      },
      {
        path: '/single/agentChat',
        component: () => import('@/views/base/agent/agentSingleChatPanel'),
        name: '/single/agentChat',
        meta: { title: '首页', icon: 'dashboard', affix: true }
      },
      {
        path: '/chat',
        component: () => import('@/views/base/specialist/smartService'),
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
        component: () => import('@/views/base/specialist/agentList'),
        name: '/agentList',
        meta: { title: '服务列表', icon: 'dashboard', affix: true }
      },
      {
        path: '/agentMarket',
        component: () => import('@/views/base/market/agents'),
        name: '/agentMarket',
        meta: { title: '智能体市场', icon: 'dashboard', affix: true }
      },
      // {
      //   path: '/serviceProduct',
      //   component: () => import('@/views/base/specialist/serviceProduct'),
      //   name: '/serviceProduct',
      //   meta: { title: '服务列表', icon: 'dashboard', affix: true }
      // },
      // {
      //   path: '/executeRecord',
      //   component: () => import('@/views/base/specialist/executeRecord'),
      //   name: '/executeRecord',
      //   meta: { title: '服务列表', icon: 'dashboard', affix: true }
      // },
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
