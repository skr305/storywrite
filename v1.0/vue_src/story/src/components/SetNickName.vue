<template>
    <div class="total">

        <div class="logo-wrp">
            <img src="../assets/logo.png" alt="" class="logo">
        </div>

        <div class="input-wrp">
            <input type="text" class="input" v-model="nickName" placeholder="请输入昵称">
        </div>

        <div class="tip">
            昵称只能设置一次，请三思哦
        </div>

        <div class="submit" @click="setNickName()">确认</div>

    </div>
</template>




<script>
import api from '../api/index'
import load from '../api/load_data'


export default {

    beforeMount() {
        if(!this.store_util.get_session("user")) {
            this.$router.replace("/")
        }
    },
    data: () => {
        
        return {
           nickName: "",
            
        }
    },  

    methods: {
        setNickName() {

            if(this.nickName.length < 2 || this.nickName.length > 18) {
                alert("昵称不能太长也不能太短")
                return
            }

            let nickName = this.nickName
            api.setNickName(nickName).then(
                (res) => {
                    if(res.code == 0) {
                        
                        /** 存入用户信息 */
                        let user = this.store_util.get_session("user")
                        user["nickname"] = nickName
                        this.store_util.set_session("user", user)
                        this.$router.replace("/MainPage")
                         
                    } else if(res.code == 601) {
                        window.alert("登录状态已过期 请重新登录")
                        this.$router.replace("/")
                    }
                   
                                
                   
                }
            ).catch((e) => {
                
            }) 
        }
    }
}
</script>


<style scoped>
    .total {
        height: 100%;
        display: flex;
        flex-direction: column;
        align-items: center;

        font-size: 20px;
    }

    .img {
        width: 8em;
        height: 8em;
    }

    .input-wrp {
        display: flex;
        flex-direction: column;
        align-items: center;
    }

    /* input的样式 */

    .input {
        outline: 0;
        border: solid .1em rgb(9, 155, 123);
        border-radius: 3em;
        
        text-align: center;
        padding: 1em 2em;
        min-width: 12em;

        font-size: 20px;

        margin: 1em 0;
    }

    /** 提示标语的样式 */
    .tip {
        padding: 1em 1em;
        font-weight: 100;
    }


    /** 按钮样式 */
    .submit {
        border: solid .1em rgb(9, 155, 123);
         border-radius: 3em;

         color: rgb(9, 104, 96);

         text-align: center;
         padding: .5em 3em;
         font-size: 25px;

         margin: 1em 0;
         margin-top: 2em;
    }

 


    
</style>