
import { data } from 'jquery'
import {time} from '../util/date'
import store_util from '../util/store_obj'

export const backEndUrl = window.location.origin + "/"
import axios_gen from './axios_gen'
import {sync_req} from './axios_gen'


let api = {}
/** 用来记录刷新次数的 */
let refresh_times = 0

/**
 * 数组去重
 */
function unique (arr) {
    return Array.from(new Set(arr))
}


/**
 * 1. 登录的接口
 */

api.login = ({username, password}) => {

    return axios_gen({url: "/auth/login", params: {username, password}, method: "Post"})
    .then((res) => res.data)
}


/**
 * 2.是否起过昵称
 */

api.nickname = () => {


    return axios_gen({url: "/user/select/nickname", params: {}, method: "get"})
    /** 对得到的结果规范化修正 */
    .then(res => {
        console.log(res)
        //  let fRes = res.data 
        //  fRes.data = {}
        //  fRes.data.flag = res.data.data
         
         return res.data
    })
}


/**
 * 3. 获取个人基本信息
 */


api.getUserInfo = () => {


    
    return axios_gen({url: "/user/info/base", params: {}, method: "get"})
    .then(res => {
        return res.data
    })
}

/**
 * 4. 创建一个故事
 */

api.createStory = () => {
    return axios_gen({url: "/article/update/new", params: {}, method: "get"})
    .then(res => {
        return res.data
    })
}



/**
 * 5. 给故事提交标题
 */


api.setTitle = ({id, title}) => {
    return axios_gen({url: "/article/update/title", params: {id, title}, method: "post"})
    .then(res => {
        return res.data
    })
}





/**
 * 6. 写故事
 */


api.writeStory = ({id, type, content}) => {
    return axios_gen({url: "/article/update/content", params: {id, type, content}, method: "post"}).then(
        res =>  {
            return res.data
    })
}



/**
 * 7. 删除一个故事
 */

api.deleteStory = (id) => {

    
    return axios_gen({url: "/article/delete", params: {id}, method: "post"}).then(
        res =>  {
            return res.data
    })
}

/**
 * 7.1删除故事的同步版本
 */

 
api.deleteStory_sync = (id) => {
    return sync_req({url: "/article/delete", params: {id}, method: "Post"})
}



/**
 * 8. 获取故事详情
 */


api.storyDetail = (id) => {
    let entire_res = [{}]

    return axios_gen({url: "/homepage/storyhead", params: {id}, method: "post"})
    .then(
        res =>  {
            /** 把获取的故事头部装到一个数组里头去
             * 然后和后台来的数据给拼到一块去
             */
            entire_res[0] = res.data.data
            return res
    })
    .then(() => { return axios_gen({url: "/homepage/story", params: {id}, method: "post"})
        .then(
            res =>  {
                /** 
                 * 把两个数组给拼在一块
                 */
                entire_res = entire_res.concat(res.data.data)
                res.data.data = entire_res
                console.log(res.data)
                return res.data
        })})
}



/**
 * 9. 获取故事写作状态
 */


api.storyStatus = (id) => {
    return axios_gen({url: "/article/update/ifwriting", params: {id}, method: "post"})
    .then(res => {
        let fRes = res.data 
        fRes.data = {}
        fRes.data.flag = res.data.data
        return fRes
   })
}



/**
 * 10. 改变故事写作状态
 */


api.setStatus = ({id, status}) => {

    return axios_gen({url: "/article/update/writing", params: {id, flag: status}, method: "post"})
    .then(res => {
        return res.data
    })
}


/**
 * 10.1 改变故事写作状态的同步版本
 */


api.setStatus_sync = ({id, status}) => {

  return sync_req({url: "/article/update/writing", params: {id, flag: status}, method:"Post"})

}



/**
 * 11. 获得我的创作记录
 */


 
api.getMyWrite =  () => {
    return axios_gen({url: "/user/info/articles", params: {}, method: "Get"})
    .then((res) => {
        // /**
        //  * 检测是否601 并refresh之
        //  */
        // if(res.data.code == 601) {
        //     await axios_gen({url: "/auth/refresh", params: {}, method: "Get"})
        //     .then(res => {
        //         let token = store_util.get_session("token")
        //         token["token"] = res.data.data.token
        //         store_util.store_session("token", token)
        //     })
        // }

        /** 去除坏数据 */
        
        let data_list = res.data.data

        if(data_list) {
            console.log(res.data)
            data_list = data_list.filter(Boolean)
            res.data.data = unique(data_list)
        }
        

        return res.data
    })
}

/**
 * 12. 点赞
 */

api.like = (id) => {
    let username = store_util.get_session("user").username
    let has_like = store_util.get_local("has_like")

    if(!has_like[username]) {
        has_like[username] = {}
    }


        has_like[username][id] = true
        store_util.store_local("has_like", has_like)
        return axios_gen({url: "/article/like", params: {id}, method: "post"})
        .then((res) => {
            return res.data
        })

        
    
}


/**
 * 12.1 取消点赞
 */

api.un_like = (id) => {
    /** 由于没有后端相应接口 
     * 在本地修改即可
     */
    let username = store_util.get_session("user").username
    let has_like = store_util.get_local("has_like")

    if(!has_like[username]) {
        has_like[username] = {}
    }

        axios_gen({url: "/article/delete/like", params: {id: id}, method: "post"})
        .then(res => console.log(res))
        has_like[username][id] = false
        store_util.store_local("has_like", has_like)

}


/**
 * 13. 收藏
 */

api.star = (id) => {
    let username = store_util.get_session("user").username
    let has_star = store_util.get_local("has_star")

    if(!has_star[username]) {
        has_star[username] = {}
    }


        has_star[username][id] = true
        store_util.store_local("has_star", has_star)
        return axios_gen({url: "/article/star", params: {id}, method: "post"})
        .then((res) => {
            return res.data
        })

}


/**
 * 13.1 取消收藏
 */

api.un_star = (id) => {

    let username = store_util.get_session("user").username
    let has_star = store_util.get_local("has_star")

    if(!has_star[username]) {
        has_star[username] = {}
    }

        has_star[username][id] = false
        store_util.store_local("has_star", has_star)
        axios_gen({url: "/user/delete/favorites", params: {id: id}, method: "post"})
        .then(res => console.log(res))

}


/**
 * 14. 获取收藏列表
 */

api.getStarList = () => {
    return axios_gen({url: "/user/info/favorites", params: {}, method: "Get" })
    .then((res) => {
        let username = store_util.get_session("user").username
        let has_star = store_util.get_local("has_star")

        if(!has_star[username]) {
            has_star[username] = {}
        }

        /** 去除坏数据 */


        let data_list = res.data.data
        if(data_list) {
            console.log(res.data)
            data_list = data_list.filter(Boolean)
            res.data.data = unique(data_list)
        }

        for(let ele of data_list) {
            /** 在本地进行标注 */
            has_star[username][ele.id] = true
        }

        return res.data
    })
}


/**
 * 15. 获取推荐列表
 */

api.getRecomList = () => {

    return axios_gen({url: "/homepage/list", params: {}, method: "Get"})
    .then((res) => {
        
        
        let data_list = res.data.data

        if(data_list) {
            console.log(res.data)
            data_list = data_list.filter(Boolean)
            res.data.data = unique(data_list)
        }
        

        return res.data
    })
}


/**
 * 16.获取所有故事信息
 */

api.getAllList = () => {
    return axios_gen({url: "/article/list", params: {}, method: "Get" })
    .then((res) => {
        /** 去除坏数据 */
        let data_list = res.data.data
        if(data_list) {
            console.log(res.data)
            data_list = data_list.filter(Boolean)
            res.data.data = unique(data_list)
        }

        return res.data
    })
}








/**
 * 16.设置昵称
 */

api.setNickName = (nickName) => {

    // let nicknames = store_util.get_local("nicknames")
    // let username = store_util.get_session("user").username 

    // nicknames[username] = nickName

    // store_util.store_local("nicknames", nicknames)

    // let res = {
    // //    data: storyDetailList,
    //    code: 0,
    //    message: "success"
    // }

    // // res.nickName = nickName

    // return new Promise(
    //     /** 模拟后台的延迟 */
    //     (resolve, reject) => {
    //         setInterval(
    //             () => {resolve(res)},
    //             100
    //         )
    //     }
    //  )
    return axios_gen({url: "/user/update/new", params: {nickname: nickName}, method: "post"})
    .then(
        res => {
            return res.data
    })
}















export default api
