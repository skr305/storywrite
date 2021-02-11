import Vue from 'vue'
import Router from 'vue-router'
import Login from '@/components/Login'
import BeginWrite from '@/components/BeginWrite'
import MainPage from '@/components/MainPage'
import FirstWrite from '@/components/FirstWrite'
import SetNickName from '@/components/SetNickName'
import NormalWrite from '@/components/NormalWrite'
import MyRecord from '@/components/MyRecord'
import MyDetail from '@/components/MyDetail'
import StarRecord from '@/components/StarRecord'
import StarDetail from '@/components/StarDetail'
import RecomDetail from '@/components/RecomDetail'
import MainDetail from '@/components/MainDetail'
import Admin from '@/components/Admin'
import AdminDetail from '@/components/AdminDetail'


Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Login',
      component: Login
    },
    {
      path: '/MainPage',
      name: 'MainPage',
      component: MainPage
    },
    {
      path: '/BeginWrite',
      name: 'BeginWrite',
      component: BeginWrite
    },
    {
      path: '/FirstWrite',
      name: 'FirstWrite',
      component: FirstWrite
    },
    {
      path: '/SetNickName',
      name: 'SetNickName',
      component: SetNickName
    },
    {
      path: '/NormalWrite/:id',
      name: '/NormalWrite',
      component: NormalWrite
    },
    {
      path: '/MyRecord',
      name: 'MyRecord',
      component: MyRecord
    },
    {
      path: '/MyDetail/:id',
      name: 'MyDetail',
      component: MyDetail
    },
    {
      path: '/StarRecord',
      name: 'StarRecord',
      component: StarRecord
    },
    {
      path: '/StarDetail/:id',
      name: 'StarDetail',
      component: StarDetail
    },
    {
      path: '/RecomDetail/:id',
      name: 'RecomDetail',
      component: RecomDetail
    },
    {
      path: '/MainDetail/:id',
      name: 'MainDetail',
      component: MainDetail
    },
    {
      path: '/Admin',
      name: 'Admin',
      component: Admin
    },
    {
      path: '/AdminDetail/:id',
      name: 'AdminDetail',
      component: AdminDetail
    }

  ]
})
