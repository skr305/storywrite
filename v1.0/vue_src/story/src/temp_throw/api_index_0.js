
import { rsort } from 'semver'
import {time} from '../util/date'
import store_util from '../util/store_obj'

export const backEndUrl = "http://localhost:8080/"
import axios_gen from './axios_gen'


let api = {}


/**
 * 1. 登录的接口
 */

api.login = ({name, password}) => {


    return axios_gen({url: "/auth/login", params: {name, password}, method: "post"})
    .then((res) => res.data)
}


/**
 * 2.是否起过昵称
 */

api.nickname = (username) => {


    return axios_gen({url: "/user/select/nickname", params: {}, method: "get"})
    /** 对得到的结果规范化修正 */
    .then(res => {
         let fRes = res.data 
         fRes.data = {}
         fRes.data.flag = res.data.data
         
         return fRes
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
    axios_gen({url: "/article/update/new", params: {}, method: "get"})
}


/**
 * 4.1创建故事的同步版本
 */

 
api.createStory_sync = () => {

    /** 产生的id */
    let id = time()

    /** 存入myList和recomList的对象 */
    let list_obj = {
        star: 0,
        like: 0,
        date: "2333-3-3",
        id: id,
        
        /** 标题一开始还没有 */
        title: "",
        main_wirter: store_util.get_session("user").username,
       
    }

    /** 生成对应开头的内容编号 */
    let content =  Math.floor(Math.random() * 2)

    let detail_obj = [{
        
        level: 0,

        star: 0,
        like: 0,
        date: "2333-3-3",

        id: id,
        title: "",
        
         /** 获得一个随机的开头 */
        content_id: content
    }]

    /** 添加对应内容至相应列表 */

    let myList = store_util.get_local("myList")
    let recomList = store_util.get_local("recomList")

    let storyDetail = store_util.get_local("storyDetail")
 /** myList在内存区块中需要确认是谁写的 以username为辨识 */
    let username = store_util.get_session("user").username
    
    /** 如果是这人写的第一篇故事 就初始化 */
    if(!myList[username]) {
        myList[username] = {}
    }

    myList[username][id] = list_obj

    recomList[id] = list_obj

    storyDetail[id] = detail_obj

    store_util.store_local("myList", myList)
    store_util.store_local("recomList", recomList)
    store_util.store_local("storyDetail", storyDetail)


     /** 获得一个随机的类型 此乃第一段续写的类型*/
     let type =  Math.floor(Math.random() * 5)

    /** 同步加载到session内存块中 */
     store_util.store_session("curStory", {type, content, id})
}

/**
 * 5. 给故事提交标题
 */


api.setTitle = ({id, title}) => {

    /** 添加标题至相应列表 */

    let myList = store_util.get_local("myList")
    let recomList = store_util.get_local("recomList")

    let storyDetail = store_util.get_local("storyDetail")
    
     /** myList在内存区块中需要确认是谁写的 以username为辨识 */
     let username = store_util.get_session("user").username
    
    
     /** 把标题写入 */

     /** 防止myList中不存在这条记录的情况下发出的坏请求 */
    if(myList[username][id]) {
        myList[username][id].title = title
    }
     

   
    recomList[id].title = title

    storyDetail[id][0].title = title

    

    store_util.store_local("myList", myList)
    store_util.store_local("recomList", recomList)
    store_util.store_local("storyDetail", storyDetail)

    

    let res = {
        "code": 0,
        "message": "success",
        "data": null
    }


    return new Promise(
        /** 模拟后台的登录行为 */
        (resolve, reject) => {
            setInterval(
                /** resolve with token */
                () => {resolve(res)},
                100
            )
        }
     )
}





/**
 * 6. 写故事
 */


api.writeStory = ({id, type, content}) => {

    let storyDetail = store_util.get_local("storyDetail")

    console.log(storyDetail[id])

    /** 确认当前是第几段续写 */
    let level = storyDetail[id].length

    if(level > 4) {
        /** 超过level4不予续写 */
        return
    }

    /** 如果是第四段续写就无type(-1表示) */
    let cur_type = level == 4 ? -1 : type

    /** 故事片段对象 */
    let story_sec = {
        /** 当前登陆者为作者 */
        writer: store_util.get_session("user").username,
        date: "2333-3-3",
        content: content,
        type: cur_type,
        level: level
    }
    

    /** 存入本地数据中 */
    storyDetail[id].push(story_sec)
    store_util.store_local("storyDetail", storyDetail)

    

    let res = {
        "code": 0,
        "message": "success",
        "data": null
    }



    return new Promise(
        /** 模拟后台的登录行为 */
        (resolve, reject) => {
            setInterval(
                /** resolve with token */
                () => {resolve(res)},
                100
            )
        }
     )
}



/**
 * 7. 删除一个故事
 */

api.deleteStory = (id) => {


    

    let myList = store_util.get_local("myList")
    let recomList = store_util.get_local("recomList")

    let storyDetail = store_util.get_local("storyDetail")

    /** 删除相关的内容 */

     /** myList在内存区块中需要确认是谁写的 以username为辨识 */
    let username = store_util.get_session("user").username
    /** 管理员也有自己的username 但是他的对应创作区域记录里什么都没有 所以这段删除无效 */
     if(myList[username]) {
         delete myList[username][id]
     }
    
    
    delete recomList[id]

    delete storyDetail[id]

    store_util.store_local("myList", myList)
    store_util.store_local("recomList", recomList)
    store_util.store_local("storyDetail", storyDetail)






    let res = {
        "code": 0,
        "message": "success",
        "data": null
    }


    return new Promise(
        /** 模拟后台的登录行为 */
        (resolve, reject) => {
            setInterval(
                /** resolve with token */
                () => {resolve(res)},
                100
            )
        }
     )
}

/**
 * 7.1删除故事的同步版本
 */

 
api.deleteStory_sync = (id) => {


    

    let myList = store_util.get_local("myList")
    let recomList = store_util.get_local("recomList")

    let storyDetail = store_util.get_local("storyDetail")

    /** 删除相关的内容 */
    /** myList在内存区块中需要确认是谁写的 以username为辨识 */
    let username = store_util.get_session("user").username
        
    if(myList[username]) {
        delete myList[username][id]
    }  

    delete recomList[id]

    delete storyDetail[id]

    store_util.store_local("myList", myList)
    store_util.store_local("recomList", recomList)
    store_util.store_local("storyDetail", storyDetail)

}



/**
 * 8. 获取故事详情
 */


api.storyDetail = (id) => {

    let storyDetail = store_util.get_local("storyDetail")

    /** 确认当前是第几段续写 */
    let detail = storyDetail[id]

    console.log(storyDetail[id].title)


    let res = {
        "code": 0,
        "message": "success",
        "data": detail
    }


    return new Promise(
        /** 模拟后台的登录行为 */
        (resolve, reject) => {
            setInterval(
                /** resolve with token */
                () => {resolve(res)},
                100
            )
        }
     )
}



/**
 * 9. 获取故事写作状态
 */


api.storyStatus = (id) => {

    let storyStatus = store_util.get_local("storyStatus")

    let in_writing = false

    if(storyStatus[id]) {
        in_writing = true
    }

   


    let res = {
        "code": 0,
        "message": "success",
        "data": {
            flag: in_writing
        }
    }


    return new Promise(
        /** 模拟后台的登录行为 */
        (resolve, reject) => {
            setInterval(
                /** resolve with token */
                () => {resolve(res)},
                100
            )
        }
     )
}



/**
 * 10. 改变故事写作状态
 */


api.setStatus = ({id, status}) => {

    let storyStatus = store_util.get_local("storyStatus")

    storyStatus[id] = status

    store_util.store_local("storyStatus", storyStatus)



    let res = {
        "code": 0,
        "message": "success",
        "data": null
    }


    return new Promise(
        /** 模拟后台的登录行为 */
        (resolve, reject) => {
            setInterval(
                /** resolve with token */
                () => {resolve(res)},
                100
            )
        }
     )
}


/**
 * 10.1 改变故事写作状态的同步版本
 */


api.setStatus_sync = ({id, status}) => {

    let storyStatus = store_util.get_local("storyStatus")

    storyStatus[id] = status

    store_util.store_local("storyStatus", storyStatus)

}



/**
 * 11. 获得我的创作记录
 */


 
api.getMyWrite = () => {

    let write_record = []
    let myList = store_util.get_local("myList")

    /** 删除相关的内容 */

     /** myList在内存区块中需要确认是谁写的 以username为辨识 */
    let username = store_util.get_session("user").username
    /** 管理员也有自己的username 但是他的对应创作区域记录里什么都没有 所以这段删除无效 */
     if(myList[username]) {
        for(let key of Object.keys(myList[username])) {
            write_record.push(myList[username][key])
        } 
     }

    let res = {
        "code": 0,
        "message": "success",
        "data": write_record
    }


    return new Promise(
        /** 模拟后台的登录行为 */
        (resolve, reject) => {
            setInterval(
                /** resolve with token */
                () => {resolve(res)},
                100
            )
        }
     )
}

/**
 * 12. 点赞
 */

api.like = (id) => {


    /** 1.在相应列表修改点赞数 */

    let myList = store_util.get_local("myList")
    let starList = store_util.get_local("starList")
    
    let recomList = store_util.get_local("recomList")

    let storyDetail = store_util.get_local("storyDetail")
    
     /** myList在内存区块中需要确认是谁写的 以username为辨识 */
     let username = store_util.get_session("user").username
    
    
     /**把点赞数修改 */

     /** 防止myList中不存在这条记录的情况下发出的坏请求 */
     if(myList[username]) {
        if(myList[username][id]) {
            myList[username][id].like++
        }
     }

     if(starList[username]) {
        if(starList[username][id]) {
            starList[username][id].like++
        }
     }
    
    recomList[id].like++
    storyDetail[id][0].like++

    
    store_util.store_local("starList", starList)
    store_util.store_local("myList", myList)
    store_util.store_local("recomList", recomList)
    store_util.store_local("storyDetail", storyDetail)


    // 2.开始在本地记录收藏和喜欢的状态
    let has_like = store_util.get_local("has_like")
    /**初始化 */
    if(!has_like[username]) {
        has_like[username] = {}
    }
    if(!has_star[username]) {
        has_star[username] = {}
    }
    has_like[username][id] = true
    store_util.store_local("has_like", has_like)

    let res = {
        "code": 0,
        "message": "success",
        "data": null
    }


    return new Promise(
        /** 模拟后台的登录行为 */
        (resolve, reject) => {
            setInterval(
                /** resolve with token */
                () => {resolve(res)},
                100
            )
        }
     )
}


/**
 * 12.1 取消点赞
 */

api.un_like = (id) => {


    /** 1.在相应列表修改点赞数 */

    let myList = store_util.get_local("myList")
    let starList = store_util.get_local("starList")
    
    let recomList = store_util.get_local("recomList")

    let storyDetail = store_util.get_local("storyDetail")
    
     /** myList在内存区块中需要确认是谁写的 以username为辨识 */
     let username = store_util.get_session("user").username
    
    
     /**把点赞数修改 */

     /** 防止myList中不存在这条记录的情况下发出的坏请求 */
     if(myList[username]) {
        if(myList[username][id]) {
            myList[username][id].like--
        }
     }

     if(starList[username]) {
        if(starList[username][id]) {
            starList[username][id].like--
        }
     }
    
    recomList[id].like--
    storyDetail[id][0].like--

    
    store_util.store_local("starList", starList)
    store_util.store_local("myList", myList)
    store_util.store_local("recomList", recomList)
    store_util.store_local("storyDetail", storyDetail)


    // 2.开始在本地记录收藏和喜欢的状态
    

    let has_like = store_util.get_local("has_like")

    /**初始化 */
    if(!has_like[username]) {
        has_like[username] = {}
    }
    if(!has_star[username]) {
        has_star[username] = {}
    }

    has_like[username][id] = false
    store_util.store_local("has_like", has_like)

    let res = {
        "code": 0,
        "message": "success",
        "data": null
    }


    return new Promise(
        /** 模拟后台的登录行为 */
        (resolve, reject) => {
            setInterval(
                /** resolve with token */
                () => {resolve(res)},
                100
            )
        }
     )
}


/**
 * 13. 收藏
 */

api.star = (id) => {


    /** 1.在相应列表修改收藏数 */

    let myList = store_util.get_local("myList")
    let starList = store_util.get_local("starList")
    
    let recomList = store_util.get_local("recomList")

    let storyDetail = store_util.get_local("storyDetail")
    
     /** myList在内存区块中需要确认是谁写的 以username为辨识 */
     let username = store_util.get_session("user").username
    
    
     /**把点赞数修改 */

     /** 防止myList中不存在这条记录的情况下发出的坏请求 */
     if(myList[username]) {
        if(myList[username][id]) {
            myList[username][id].star++
        }
     }
  
    
    recomList[id].star++
    storyDetail[id][0].star++

    
    
    store_util.store_local("myList", myList)
    store_util.store_local("recomList", recomList)
    store_util.store_local("storyDetail", storyDetail)


    /** 如果之前没有收藏过任何故事 */
     if(!starList[username]) {
        starList[username] = {}
    }
       

    /**
     * 2.开始处理收藏列表
     */

    /** 将对应区块添加到此用户的收藏列表中 */
     //避免是屑故事(指连作者都没有续写的故事)
    
    let main_writer = storyDetail[id][1] ? storyDetail[id][1].writer : "佚名"
    starList[username][id] = {
        star: storyDetail[id][0].star,
        like: storyDetail[id][0].like,
        main_writer: main_writer,
        title: storyDetail[id][0].title,
        date: storyDetail[id][0].date
    }
        
    
    store_util.store_local("starList", starList)


    // 3.开始在本地记录收藏和喜欢的状态
    let has_star = store_util.get_local("has_star")

    /**初始化 */
    if(!has_like[username]) {
        has_like[username] = {}
    }
    if(!has_star[username]) {
        has_star[username] = {}
    }

    has_star[username][id] = true
    store_util.store_local("has_star", has_star)

    let res = {
        "code": 0,
        "message": "success",
        "data": null
    }


    return new Promise(
        /** 模拟后台的登录行为 */
        (resolve, reject) => {
            setInterval(
                /** resolve with token */
                () => {resolve(res)},
                100
            )
        }
     )
}


/**
 * 13.1 取消收藏
 */

api.un_star = (id) => {


    /** 1.在相应列表修改收藏数 */

    let myList = store_util.get_local("myList")
    let starList = store_util.get_local("starList")
    
    let recomList = store_util.get_local("recomList")

    let storyDetail = store_util.get_local("storyDetail")
    
     /** myList在内存区块中需要确认是谁写的 以username为辨识 */
     let username = store_util.get_session("user").username
    
    
     /**把点赞数修改 */

     /** 防止myList中不存在这条记录的情况下发出的坏请求 */
     if(myList[username]) {
        if(myList[username][id]) {
            myList[username][id].star--
        }
     }
  
    
    recomList[id].star--
    storyDetail[id][0].star--

    
    
    store_util.store_local("myList", myList)
    store_util.store_local("recomList", recomList)
    store_util.store_local("storyDetail", storyDetail)


    /** 如果之前没有收藏过任何故事 */
     if(!starList[username]) {
        starList[username] = {}
    }
       

    /**
     * 2.开始处理收藏列表
     * 直接删除
     */

    delete starList[username][id]
    
    store_util.store_local("starList", starList)


    // 3.开始在本地记录收藏和喜欢的状态
    let has_star = store_util.get_local("has_star")

    /**初始化 */
    if(!has_like[username]) {
        has_like[username] = {}
    }
    if(!has_star[username]) {
        has_star[username] = {}
    }

    has_star[username][id] = false
    store_util.store_local("has_star", has_star)

    let res = {
        "code": 0,
        "message": "success",
        "data": null
    }


    return new Promise(
        /** 模拟后台的登录行为 */
        (resolve, reject) => {
            setInterval(
                /** resolve with token */
                () => {resolve(res)},
                100
            )
        }
     )
}


/**
 * 14. 获取收藏列表
 */

api.getStarList = () => {


    let result = []

    let starList = store_util.get_local("starList")

    
     /** myList在内存区块中需要确认是谁写的 以username为辨识 */
     let username = store_util.get_session("user").username
    


    /** 如果之前没有收藏过任何故事 */
     if(!starList[username]) {
        starList[username] = {}
    }

    for(let key in Object.keys(starList[username])) {
        result.push(starList[username][key])
    }
       

    let res = {
        "code": 0,
        "message": "success",
        "data": result
    }


    return new Promise(
        /** 模拟后台的登录行为 */
        (resolve, reject) => {
            setInterval(
                /** resolve with token */
                () => {resolve(res)},
                100
            )
        }
     )
}


/**
 * 15. 获取推荐列表
 */

api.getRecomList = () => {


    let result = []

    let recomList = store_util.get_local("recomList")

    
     /** myList在内存区块中需要确认是谁写的 以username为辨识 */
     let username = store_util.get_session("user").username
    


    for(let key in Object.keys(recomList)) {
        result.push(recomList[key])
    }
       

    let res = {
        "code": 0,
        "message": "success",
        "data": result
    }


    return new Promise(
        /** 模拟后台的登录行为 */
        (resolve, reject) => {
            setInterval(
                /** resolve with token */
                () => {resolve(res)},
                100
            )
        }
     )
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
}















export default api
