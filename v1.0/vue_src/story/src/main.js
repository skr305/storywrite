// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import store_util from './util/store_obj'

import begin_content from './util/begin'

Vue.config.productionTip = false
Vue.prototype.store_util = store_util
Vue.prototype.story_content = begin_content

/** 初始化本地数据存储 */
/** 本地数据用来模拟数据库的存储 */
if(!localStorage.getItem("users")) {
  store_util.store_local("users", {})
}

if(!localStorage.getItem("nicknames")) {
  store_util.store_local("nicknames", {})
}
// /** 清空本地缓存的操作 */
// store_util.store_local("recomList", {})
// store_util.store_local("storyDetail", {})
// store_util.store_local("myList", {})

/** 初始化的操作 */
if(!localStorage.getItem("storyDetail")) {
  store_util.store_local("storyDetail", {})
}

if(!localStorage.getItem("myList")) {
  store_util.store_local("myList", {})
}

if(!localStorage.getItem("recomList")) {
  store_util.store_local("recomList", {})
}

if(!localStorage.getItem("storyStatus")) {
  store_util.store_local("storyStatus", {})
}

/** 收藏列表 */
if(!localStorage.getItem("starList")) {
  store_util.store_local("starList", {})
}

/** 用来模拟后台点赞收藏的功能 */
if(!localStorage.getItem("has_like")) {
  store_util.store_local("has_like", {})
}

if(!localStorage.getItem("has_star")) {
  store_util.store_local("has_star", {})
}

if(!localStorage.getItem("delete_list")) {
  store_util.store_local("delete_list", {})
}



/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>'
})

