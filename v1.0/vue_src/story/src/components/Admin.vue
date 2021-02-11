<template>
    <div class="total-wrp">
        <div class="total" v-if="!loading">
            <div class="search-wrp">
                <img src="../assets/search.png" class="search-logo" alt="" srcset="">
                <input type="text" class="search input" v-model="filter_str" placeholder="(请输入故事标题进行搜索)">
            </div>
            
            <div class="head">
                故事续写(管理员)
            </div>

            <div class="recom">
                <div class="recom-block" v-for="ele in filtered_list" :key="ele.id">
                    <div class="recom-block-name">{{(ele.main_writer) ? ele.main_writer[0] : "佚"}}{{ele.main_writer ? (ele.main_writer) : "佚名"}}</div>
                    <div class="recom-block-title" @click="check_detail(ele.id)">{{ ele.title }}</div>

                    <!-- 因为区块内部的数据不会及时刷新 所以这里搞个sign 使得这个区块会被强制刷新 -->
                    <div class="recom-block-echo" v-if="sign">
                        <span class="star recom-block-info" v-if="has_like[ele.id]" @click="un_like(ele)">
                            <img src="../assets/has-like.png" alt="" class="echo-logo">
                            {{ele.like}}
                        </span>
                        <span class="like recom-block-info" v-else @click="like(ele)">
                             <img src="../assets/zan.png" alt="" class="echo-logo">
                            {{ele.like}}
                            </span>
                        
                        <span class="like recom-block-info" v-if="has_star[ele.id]" @click="un_star(ele)">
                             <img src="../assets/has-star.png" alt="" class="echo-logo">
                            {{ele.star}}
                        </span>
                        <span class="star recom-block-info" v-else @click="star(ele)">
                             <img src="../assets/coll.png" alt="" class="echo-logo">
                            {{ele.star}}
                        </span>
                    </div>
                    <div class="recom-block-echo" v-else>
                        <span class="star recom-block-info" v-if="has_like[ele.id]" @click="un_like(ele)">
                            <img src="../assets/has-like.png" alt="" class="echo-logo">
                            {{ele.like}}
                        </span>
                        <span class="like recom-block-info" v-else @click="like(ele)">
                             <img src="../assets/zan.png" alt="" class="echo-logo">
                            {{ele.like}}
                            </span>
                        
                        <span class="like recom-block-info" v-if="has_star[ele.id]" @click="un_star(ele)">
                             <img src="../assets/has-star.png" alt="" class="echo-logo">
                            {{ele.star}}
                        </span>
                        <span class="star recom-block-info" v-else @click="star(ele)">
                             <img src="../assets/coll.png" alt="" class="echo-logo">
                            {{ele.star}}
                        </span>
                    </div>

                    <div class="date-wrp">
                      创作时间: {{ele.date || "2007-07-06 12:31:21"}}
                    </div>

                </div>

                <div class="submit-wrp">
                    <div class="search submit" @click="search()">搜索</div>
                    <div class="show-all submit" @click="show_all()">显示全部</div>
                </div>

                
                
            </div>
  
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
import api from '../api/index'
import store_util from '../util/store_obj'
import {time} from '../util/date'


export default {

    beforeDestroy() {
    //   this.before_unload_Fn()
    },


    mounted() {
        window.addEventListener("beforeunload", e => this.before_unload_Fn(e))
    },

    beforeMount() {
        if(!this.store_util.get_session("user")) {
            this.$router.replace("/")
            return
        }
        
        /** 删除没有进行完整创建流程的坏故事 */
        let delete_list = this.store_util.get_local("delete_list")
        for (let key of Object.keys(delete_list)) {
            if(delete_list[key]) {
                api.deleteStory_sync(key)
            }
        }
    

        api.getAllList().then(
            (res) => {
                if(res.code == 0) {
                    console.log(res)
                    this.all_list = res.data
                    this.filtered_list = this.all_list
                    console.log("inMount")
                    this.loading = false
                } else if(res.code == 601) {
                    window.alert("登录状态已过期 请重新登录")
                    this.$router.replace("/")
                }
            }
        )
        

        console.log("destory_count in memory:", !localStorage.getItem("destroy_count"))
        console.log("destory_count is:", store_util.get_local("destroy_count") || 0)

        console.log(`user:`, this.store_util.get_session("user"))

        console.log(`nicknames:`, this.store_util.get_local("nicknames"))

        console.log(`storyDetail:`, this.store_util.get_local("storyDetail"))
    },

    data () {
        let has_like = this.store_util.get_local("has_like")[this.store_util.get_session("user").username]
        let has_star = this.store_util.get_local("has_star")[this.store_util.get_session("user").username]
        if(!has_like) {
            has_like = {}
        }

        if(!has_star) {
            has_star = {}
        }

        return {
            /** 0是推荐页面 1是个人页 */
            curPage: 0,
            all_list: [],
            

            /** 用来进行过滤的函数 */
            filter: (ele) => {return true},
            /** 过滤语料 */
            filter_str: "",

            filtered_list: [],

            username: this.store_util.get_session("user").username,
            nickname: this.store_util.get_session("user").nickname,

            /** 某个人是否喜欢和收藏的信息 */
            has_like: has_like,
            has_star: has_star,

            sign: true,

            loading: true,
        }
    },

    methods: {
        tab(page) {
            this.curPage = page
        },
        
        begin_write() {
            this.$router.push(`/BeginWrite`)
        },

        /** 页面关闭之前执行的函数 */
        before_unload_Fn (e) {
            if(!localStorage.getItem("destroy_count")) {
                store_util.store_local("destory_count", Number(time()))
            }

            let destroy_count = store_util.get_local("destroy_count")
            destroy_count = Number(destroy_count) + 1
            
            store_util.store_local("destroy_count", destroy_count)
        },

       
        check_detail(id) {
            this.$router.push(`AdminDetail/${id}`)
        },

        search() {
            let str = this.filter_str

            /** 采用的是一种比较简单的
             * 模糊匹配 根据搜索字符串解析
             * 成的所有字符是否存在于标题中为标准
             */
            let filter = (ele) => {
                let result = false
                for(let char of str) {
                    console.log(ele)
                    if(!ele.title || ele.title.indexOf(char) == -1) {
                        return false
                    }
                }

                return true
            }   

            this.filtered_list = this.all_list.filter(filter)
            this.filter = filter

        },

        show_all() {
            this.filtered_list = this.all_list
        },
        star(ele) {
            api.star(ele.id)
            this.has_star[ele.id] = true
            this.sign = !this.sign
            ele.star++
        },

        like(ele) {
            api.like(ele.id)
            this.has_like[ele.id] = true
            console.log(this.has_like[ele.id])
            this.sign = !this.sign
            ele.like++
        },

        un_star(ele) {
            api.un_star(ele.id)
            this.has_star[ele.id] = false
            this.sign = !this.sign
            ele.star--
        },

        un_like(ele) {
            api.un_like(ele.id)
            this.has_like[ele.id] = false

            this.sign = !this.sign
            ele.like--
        },
    }
}
</script>


<style scoped>
@import '../iconfont/iconfont.css';

    .total-wrp {


        height: 100%;
        display: flex;
        flex-direction: column;
        align-items: center;
        /** 防止底部与tabber重合 */

        
    }


    .total {
        /* position: relative; */
        min-width: 340px;
        margin: 0 auto;

        height: 100%;
        display: flex;
        flex-direction: column;
        align-items: center;

        /** 防止底部与tabber重合 */
        padding-bottom: 6em;
        padding-top: 3em;

        font-size: 20px;
    }

    /** 头部部分 */
    .head {
        position: fixed;

        background: rgba(49, 168, 168, 0.7);
        color: rgb(252, 249, 249);
        letter-spacing: .6em;

        max-width: 100vw;
        
        min-width: 350px;


        padding: 1.4em 0;
        border-radius: 0 0 10px 10px;
        margin-bottom: 1em;

        top: 0;
    }

    /* 搜索框的样式 */

    .search-wrp {
        position: relative;
    }

    .input {
        outline: 0;
        border: solid 2px rgb(8, 68, 55);
        border-radius: .7em;
        
        text-align: center;
        padding: .7em 2em;
        min-width: 12em;

        font-size: 20px;

        margin: 1em 0;
    }

    .search-logo {
        width: 1.4em;
        height: 1.5em;

        position:absolute;
        left: .5em;
        top: 1.6em;
    }



    /** 导航条部分 */

    .nav-bar {
        /** 定位到最底下 */
        position: fixed;
        bottom: 0em;
        left: 50%;
        transform: translate(-50%, 0);
        /* left: 50%;
        transform: translate(-50%, 0); */

        display: flex;
        align-items: center;

        border: solid 2px rgb(24, 143, 137);
        border-radius: 8px 8px 0 0;
        font-size: 26px;

        background: white;
    }

    .nav-ele {
        padding: 5px 70px;
        text-align: center;

        white-space: nowrap;

        font-size: 16px;
        
    }

    .nav-ele:nth-child(1) {
        /* border-right: solid .1em rgb(5, 92, 87); */
    }

    .nav-logo {
        display: block;
        height: 2em;
        width: 2em;

        margin: .1em auto;
    }

    /** 推荐信息部分 */
    .recom-block {
        font-size: 25px;
        padding: .5em 0;
    }

    .recom-block-name {
        padding-right: 9em;
    }

    .recom-block-name::first-letter {
        
        padding: .5em .5em;
        font-size: 25px;
        font-weight: 100;

        color: white;
        background: rgb(42, 138, 202);

        display: inline-block;
        margin-right: .5em;
        
        border-radius: 50%;
    }

    .recom-block-title {
        text-align: left;
        font-size: 17px;
        padding: .5em 2em 2em .5em;

        min-width: 200px;
        min-height: 60px;

        border: 2px solid rgb(2, 46, 44);
        border-radius: 14px;

        background: rgb(250, 246, 246);

        margin: 1.6em 0;
    }

    /** 反馈区块 */

    .echo-logo {
        width: 27px;
        height: 27px;
        
    }

    .recom-block-echo {
        font-size: 22px;
        font-weight: 300;

        text-align: right;
        padding: 0 1em 0em 0;
        border-bottom: 2px solid rgb(5, 92, 87);

        margin-bottom: 2em;
    }

    .recom-block-info {
        user-select: none;
    }

    /** 时间戳 */
    .date-wrp {
        font-size: 14px;
        font-weight: 100;
        float: right;

        position: relative;
        top: -2em;
    }

    


    .submit-wrp {
        font-size: 17px;
        position: fixed;
        bottom: 0em;


        transform: (-50%, 0);

        min-width: 20em;
    

        display: flex;
        flex-direction: row;
        justify-content: space-between;
        align-items: center;

        background: white
    }

    .submit {
        display: inline-block;

         /* border: solid 2px rgb(16, 99, 81); */
         border-radius: 3em;
          user-select: none;

         color: rgb(9, 104, 96);
         background: rgb(221, 221, 221);

         text-align: center;
         font-weight: 800;
         padding: .7em 3em;

         margin: 1em auto;
         /* margin-left: 3em;
         margin-top: 2em; */
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