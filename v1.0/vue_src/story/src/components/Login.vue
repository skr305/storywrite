<template>
    <div class="total-wrp">
        <div class="total" v-if="!loading">

         

            <div class="logo-wrp">
                <img src="../assets/logo.png" alt="" class="logo">
            </div>

            <div class="login-ins" v-if="admin">故事续写管理员登录</div>
            <div class="login-ins" v-if="!admin">山东大学统一认证登录</div>

            <div class="input-wrp">
                <input type="text" v-model="form.username" class="input" placeholder="学号">
                <input type="password" v-model="form.password" class="input" placeholder="密码">
            </div>
            
            <div class="bad-login" v-if="bad_login">
                用户名或密码错误啦
            </div>

            <div class="submit" @click="login()">登录</div>

            <div class="admin-login" @click="switch_admin()">{{this.admin ? "用户登录":"管理员登录"}}</div>

        </div>

        <div class="total loading" v-else>
            <div class="loading-ani">
                <div class="loading-ani-body">
                </div>
            </div>

            <span class="loading-word">加载中</span>
        </div>
    </div>
    
</template>




<script>
import load from '../api/load_data'
import api from '../api/index'


export default {
    data () {
        
        return {
            /** 登录表单 */
            form: {
                username: "",
                password: "",
            },

            loading: false,
            /** 是否是登录错误 */
            bad_login: false,
            admin: false
        }
    },  

    methods: {
        
        login() {

            // let _this = this
            // setTimeout(function() {
            //     _this.loading=false
            //     window.alert("登录超时")
            // }, 40000)

            this.loading = true
            api.login(this.form).then(
                (res) => {
                    
                    console.log(res)
                    if(res.code == 0) {
                        this.store_util.store_session("token", 
                        {token:res.data.token, refresh_token: res.data.refresh_token})


                        /** 先打个底 如果userInfo请求失败了 还可以正常显示数据 */
                        this.store_util.store_session("user", 
                        {username: this.form.username, nickname: ""})
                        

                        api.getUserInfo().then(
                            res => {
                                if(res.code == 0) {
                                    this.store_util.store_session("user", 
                                    {username: this.form.username, nickname: res.data.nickname})
                                }
                            }
                        ).then(
                            () => {

                                if(!this.admin) {
                                    api.nickname().then(
                                        (res) => {
                                            this.loading = false
                                            console.log(res)
                                            if(res.data.flag) {
                                                this.$router.replace("/MainPage")
                                            } else {
                                                this.$router.replace("/SetNickName")
                                            }
                                        }
                                    )
                                } else {
                                     let username = this.form.username
                                     if(username != "201900301072" && username != "202000300080" ) {
                                         window.alert("您不是管理员")
                                         return
                                     }
                                     this.$router.replace("/Admin")
                                }
                            }
                        )
                       
                    }  else if(res.code == 1) {
                        /** 显示错误信息 */
                        this.bad_login = true
                        this.loading = false
                    } else {
                        this.loading = false
                    }
                }
            ).catch((e) => {

            }) 
        },

        /** 切换是否是管理员状态 */
        switch_admin() {
            this.admin = !this.admin
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

    /** 登录提示语 */
    .login-ins {
        margin: 1em auto;

        font-size: 24px;
        font-weight: 800;
    }

    

    /* input的样式 */

    .input {
        outline: 0;
        border: solid 2px rgb(8, 68, 55);
        border-radius: .7em;
        
        text-align: center;
        padding: 1em 2em;
        min-width: 12em;

        font-size: 20px;

        margin: 1em 0;
    }

    /** 错误信息样式 */
    .bad-login {
        color: rgb(218, 117, 117);
        font-weight: 100;
    }

    /** 按钮样式 */
    .submit {
        /* border: solid .1em rgb(9, 155, 123); */
         border-radius: 3em;

         color: rgb(9, 104, 96);
         background: rgb(221, 221, 221);

         text-align: center;
         padding: .5em 3em;
         font-size: 25px;

         margin: 1em 0;
         margin-top: 2em;

         transition: all .7s ease;
    }

    .submit:hover {
        color: rgb(248, 242, 242);
        background: rgb(109, 182, 165);
    }


    /** 切换为管理员登录 */
    .admin-login {

         border-bottom: solid 1px rgb(255, 255, 255);

        /** 调的不那么明显 */
        font-size: 16px;
        opacity: .7;

         
         color: rgb(119, 221, 212);

         text-align: center;
         padding: .5em 1em;

         margin: 1em 1.5em;
         margin-top: 2em;

         white-space: nowrap;

         transition: all .7s ease;
         
    }

    .admin-login:hover {
        color: rgb(65, 235, 235);
        border-bottom: solid 1px rgb(130, 224, 217);
    }

    /** 加载动画 */
    .loading {
        position: fixed;
        top: 25vh;
        left: 50vw;
        transform: translate(-50%, 0);

        font-size: 25px;
        font-weight: 100;
    
    }

    @keyframes loading-ani {
        0% {
            content: "";
        }

        25% {
            content: ".";
        }

        50% {
            content: ".."
        }

        75% {
            content: "...";
        }

        100% {
            content: "...."
        }
    }

    .loading-word {
        margin: 1.8em auto;
    }

    .loading-word::after {
        content: ".";
        animation: loading-ani 6s linear infinite;
        /* animation-iteration-count: infinite; */

    }

    .loading-touch, .loading-quote {
        margin-top: 3em;
        font-size: 17px;
    }

    .loading-quote {
        text-align: right;
    }

    .loading-ani, .loading-ani-body {
        width: 7em;
        height: 7em;

        border-radius: 50%;

    }

    .loading-ani {
        padding: 10px;
        border: 2px solid rgb(184, 223, 238);

        position: relative;
        overflow: hidden;
    }

    .loading-ani-body {
        background: rgb(163, 235, 235);
        
    }

    .loading-ani-body::before {
        content: "";

        background: rgba(255, 255, 255, .9);
        position: absolute;
       
        height: 300px;
        width: 300px;
        
        top: -27%;
        left: -30%;

        border-radius: 40%;

        animation: wave 8s linear infinite;
    }


    @keyframes wave {
        0% {

            transform: translate(0, -50%) rotate(0);
        }
        100% {
             
            transform:  translate(0, -50%) rotate(360deg);
        }
    }

    


    
</style>