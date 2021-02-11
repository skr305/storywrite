 import api from './index'
 import store_util from '../util/store_obj'
 import {time} from '../util/date'
 

 /** 切换故事开头和type的一套操作 */
 async function change_cur_story(id) {
    let del_done = false
    let done  = false



    await api.deleteStory(id).then(
        (res) => {
            /** 操作成功 */
            if(res.code == 0) {
                /** 再创建一个新的 */
                del_done = true
            }
        }
    )
    if(del_done) {
        await api.createStory().then(
            (res) => {
                if(res.code == 0) {
                    store_util.store_session("curStory", res.data)
                    store_util.store_session("nowTime", time())
                    /** 重新设置一遍数据 */
                    done = true
                }   
            }
        )
    }
    
    // console.log("done:", done)
    // return done
    return done
}


export default change_cur_story