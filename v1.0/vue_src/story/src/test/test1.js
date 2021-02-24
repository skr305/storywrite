import axios from 'axios'
import store_util from '../util/store_obj'
import axios_gen from '../api/axios_gen'
import {sync_req} from '../api/axios_gen'

let login_done = false

let login_handle = (res) => {
    console.log(res)
    if(res.data.code == 0) {
        if(!store_util.get_session("token")) {
            store_util.store_session("token", {})
        } 
    
        let token = store_util.get_session("token")
        token.refresh_token = res.data.data.refresh_token
        token.token = res.data.data.token

        store_util.store_session("token", token)

        console.log(store_util.get_session("token").token)

        login_done = true

        console.log("doneLogin")
    }
}

let login_params = {
    username: "201900301072",
    password: "zyl13705901107"
}


/**
 * 
 */
let check_nick_handle = (res) => {
    console.log(res)
    /** 还未设置昵称 */
    if(!res.data.data) {
        axios_gen({url: '/user/update/new', params: {nickname: "张三"}, method: "post"})
        .then(res => {console.log(res)})
    }
    // if(res.data.code == 0) {
    //     console.log(res.data.data)
    // }
}

let after_login = async () => {
    let create_done = false
    let id = ""

    
    await axios_gen({url: "/article/update/new", params: login_params, method: "get"}).then(
        res =>  {
            if(res.data.code == 0) {
                id = res.data.data.id
                create_done = true
            }
    })

    

    await axios_gen({url: "/article/update/writing", params: {id, flag: true}, method: "post"}).then(
        res =>  {
            if(res.data.code == 0) {
                console.log(res.data.data, "submit_status done")
            }
    })

    await axios_gen({url: "/article/update/ifwriting", params: {id}, method: "post"}).then(
        res =>  {
            if(res.data.code == 0) {
                console.log(res.data.data, "submit_status done")
            }
    })

    for(let i=0; i<3; i++) {
        
        await axios_gen({url: "/article/update/content", params: {id, type:1, content: i+".2333 content"}, method: "post"}).then(
            res =>  {
                console.log(res)
                if(res.data.code == 0) {
                    console.log(res.data.data, "submit_story done")
                }
        })
    }


    

    await axios_gen({url: "/homepage/storyhead", params: {id}, method: "post"}).then(
        res =>  {
            if(res.data.code == 0) {
                console.log("check_story done",res.data.data )
            }
    })


    await axios_gen({url: "/user/info/articles", params: {id}, method: "get"}).then(
        res =>  {
            if(res.data.code == 0) {
                console.log("check_my_story done", res.data.data )
            }
    })

    
    

    await axios_gen({url: "/article/star", params: {id}, method: "post"}).then(
        res =>  {
            if(res.data.code == 0) {
                console.log("like done", res.data.data )
            }
    })
    

    await axios_gen({url: "/user/info/favorites", params: {id}, method: "get"}).then(
        res =>  {
            if(res.data.code == 0) {
                console.log("check_star_story done", res.data.data )
            }
    })

    sync_req({url: "/article/delete", params: {id: 10014}, method: "Post"})

    
}


let check_like = async () => {

    await axios_gen({url: "/auth/login", params: {username: "201900301072", password: "zyl13705901107"}, method: "Post"})
        .then(res => login_handle(res))

    await axios_gen({url: "/article/like", params: {id: 10128}, method: "Post"}).then()
    await axios_gen({url: "/article/star", params: {id: 10128}, method: "Post"}).then()
    await axios_gen({url: "/user/select/like", params: {id: 10128}, method: "Post"})
    .then((res) => {
        console.log(res)
    })

    await axios_gen({url: "/user/select/favorite", params: {id: 10128}, method: "Post"})
    .then(res => console.log(res))

    await axios_gen({url: "/user/delete/favorites", params: {id: 10128}, method: "post"})
    .then(res => console.log(res))

    await axios_gen({url: "/user/select/favorite", params: {id: 10128}, method: "Post"})
    .then(res => console.log(res))

    await axios_gen({url: "/article/list", params: {}, method: "Post"}).then(
        res => {console.log(res)}
    )
    
}





export default () => {
    // axios_gen({url: "/auth/login", params: login_params, method: "post"})
    // .then((res) => {login_handle(res)})
    // .then(()=>{axios_gen({url: "/user/select/nickname", params: {}, method: "get"}).then((res)=>{check_nick_handle(res)})})
    // .then(() => after_login())
    check_like()

    
}

// axios({
//     url: '/auth/login',
//     method: 'post', // 默认是 get

//     baseURL: 'http://localhost:8080/SDUstory/',

  
//     // `headers` 是即将被发送的自定义请求头
//     headers: 
//     {
//         'X-Requested-With': 'XMLHttpRequest',
//         'Content-Type': "application/x-www-form-urlencoded",
//     },
  
//     // `params` 是即将与请求一起发送的 URL 参数
//     // 必须是一个无格式对象(plain object)或 URLSearchParams 对象
//     params: {
//       username: "201900301072",
//       password: "zyl13705901107"
//     },
// }).then((res) => {
   

    
// })


// if(login_done) {
//     axios({
//         url: '/user/select/nickname',
//         method: 'post', // 默认是 get
    
//         baseURL: 'http://localhost:8080/SDUstory/',
    
      
//         // `headers` 是即将被发送的自定义请求头
//         headers: 
//         {
//             'X-Requested-With': 'XMLHttpRequest',
//             'Content-Type': "application/x-www-form-urlencoded",
//             'token': store_util.get_session("user").token
//         },
      
//         // `params` 是即将与请求一起发送的 URL 参数
//         // 必须是一个无格式对象(plain object)或 URLSearchParams 对象
//         params: {
//           username: "201900301072",
//           password: "zyl13705901107"
//         },
//     }).then((res) => {
//         if(res.code == 0) {
//             /** 还未设置过昵称 */
//             if(!res.data.flag) {
                
//             }
//         }
//     })
// }