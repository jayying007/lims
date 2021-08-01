import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'


/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'/'el-icon-x' the icon show in the sidebar
    noCache: true                if set true, the page will no be cached(default is false)
    affix: true                  if set true, the tag will affix in the tags-view
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/login'),
    hidden: true
  },
  {
    path: '/register',
    component: () => import('@/views/login/register'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/error-page/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error-page/401'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/dashboard/index'),
        name: 'Dashboard',
        meta: { title: '首页', icon: 'dashboard', affix: true }
      }
    ]
  },
  {
    path: '/profile',
    component: Layout,
    redirect: '/profile/index',
    hidden: true,
    children: [
      {
        path: 'index',
        component: () => import('@/views/profile/index'),
        name: 'Profile',
        meta: { title: 'Profile', icon: 'user', noCache: true }
      }
    ]
  }
]

export const asyncRoutes = [
  {
    path: '/user',
    component: Layout,
    children: [
      {
        path: 'index',
        component: () => import('@/views/user/index'),
        name: 'User',
        meta: {
          title: '用户管理',
          icon: 'user',
          roles: ['SUPER_ADMIN'] // you can set roles in root nav
        },
      }
    ]
  },
  {
    path: '/laboratory',
    component: Layout,
    children: [
      {
        path: 'index',
        component: () => import('@/views/laboratory/index'),
        name: 'Laboratory',
        meta: {
          title: '实验室管理',
          icon: 'laboratory',
        },
      }
    ]
  },
  {
    path: '/device',
    redirect: '/device/list',
    component: Layout,
    alwaysShow: true, // will always show the root menu
    name: '设备管理',
    meta: {
      title: '设备管理',
      icon: 'device',
    },
    children: [
      {
        path: 'application-record',
        component: () => import('@/views/device/application-record'),
        name: '申请列表',
        meta: {
          title: '申请列表'
        }
      },
      {
        path: 'list',
        component: () => import('@/views/device/list'),
        name: '设备列表',
        meta: {
          title: '设备列表'
          // if do not set roles, means: this page does not require permission
        }
      },
      {
        path: 'detail/:id(\\d+)',
        component: () => import('@/views/device/detail'),
        name: '设备详情',
        meta: {
          title: '设备详情'
          // if do not set roles, means: this page does not require permission
        },
        hidden: true
      },
    ]
  },
  // 404 page must be placed at the end !!!
  { path: '*', redirect: '/404', hidden: true }
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
