import axios from 'axios'
import store_util from '../util/store_obj'
import $ from 'jquery'
const baseURL = window.location.origin + "/SDUstory/"
// const baseURL = "/api/"
/** 生成一个固定模板的axios请求 */

export default ({url, params, method, isFile=false}) => {

    if(!store_util.get_session("token")) {
        store_util.store_session("token", {})
    }


    return axios({
        url: url,
        method: method, // 默认是 get
    
        baseURL: baseURL,
    
      
        // `headers` 是即将被发送的自定义请求头
        headers: 
        {
            'X-Requested-With': 'XMLHttpRequest',
            'Content-Type': isFile ? "multipart/form-data" : "application/x-www-form-urlencoded",
            'token': store_util.get_session("token").token || "",
            'refresh_token': store_util.get_session("token").refresh_token || ""
        },
      
        // `params` 是即将与请求一起发送的 URL 参数
        // 必须是一个无格式对象(plain object)或 URLSearchParams 对象
        params: params
    })
}


 export let sync_req = ({url, params, method}) => {
        $.ajax({
        headers: {
            "testheader": "test",
            'X-Requested-With': 'XMLHttpRequest',
            'Content-Type':  "application/x-www-form-urlencoded",
            'token': store_util.get_session("token").token || "",
            'refresh_token': store_util.get_session("token").refresh_token || ""
        },
        data: params,
        type: method,
        url: "/api" + url,
        contentType: "application/x-www-form-urlencoded",
        success: function (data) {
            //your code
            console.log(data)
        },

        /** 开启同步模式 */
        async: false
    })
 }






