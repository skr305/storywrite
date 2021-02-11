import api from './index'

let ll_request =  async () => {
    let done = false
    
    api.getUserInfo().then(
        (res) => {
            if(res.code == 0) {
                done = true
            }
        }
    )


    return {done: done}
}


export default ll_request