/**
 * 用于完成登录到加载个人信息的一系列异步操作的
 */

import api from './index'

let ll_request =  async () => {
     /**
     * 分别获取收藏列表 创作列表 推荐列表
           */
          await api.getLikeList()
          .then((res) => {
            if(res.code == 0) {
                /** 先存储token信息 */
                sessionStorage.setItem("likeList", res.data)
              } else {
                  /** 错误处理 */
              }
          })

          await api.getMyList()
          .then((res) => {
            if(res.code == 0) {
                /** 先存储token信息 */
                sessionStorage.setItem("myList", res.data)
              } else {
                  /** 错误处理 */
              }
          })

          await api.getRecom()
          .then((res) => {
            if(res.code == 0) {
                /** 先存储token信息 */
                sessionStorage.setItem("recom", JSON.stringify(res.data))
              } else {
                  /** 错误处理 */
              }
          })

          console.log(JSON.parse(sessionStorage.getItem("recom")))

          return {done: true}
}


export default ll_request