<template>
    <div class="total-wrp">

        <div class="total">
            <div class="head">
                开始创作
            </div>

            <div class="ins story-block">
                <div class="ins-title" >规则：</div>
                <div class="ins-content">1.点击按钮随机算则一个开头和故事类型，点击“我要续写”开始续<br />写故事，建议续写100-200字。<br />2.完成后，点击“我写完啦”完成续写，然后把它分享给你的好友让<br />ta接着续写，好友写完后ta也可以分享给ya的好友接着写，以此类<br />推，直到第四位好友，ta的任务就是帮你们的故事写一个合乎逻辑<br />的结尾啦！<br />3.好友之间要通力合作写出精彩的故事，这样才能获得更多的赞和<br />收藏哦。</div>
            </div>

            <div class="story-begin story-block">
                <div class="story-begin-title">
                    故事开头:
                    <div class="story-begin-change" @click="change_story()">
                        <img src="../assets/fresh.png" alt="" class="change-logo">
                        更换开头和类型
                    </div>
                </div>

                <div class="story-begin-content">
                    <span> 
                        {{ content }}
                    </span>
                </div>
            </div>


            <div class="story-type story-block">
                <div class="story-type-title">
                    故事类型:
                </div>
                <div class="story-type-list">
                    <div class="story-type-list-ele" :class="{selected : type == 0}">悬疑</div>
                    <div class="story-type-list-ele" :class="{selected : type == 1}">搞笑</div>
                    <div class="story-type-list-ele" :class="{selected : type == 2}">科幻</div>
                    <div class="story-type-list-ele" :class="{selected : type == 3}">悬疑</div>
                    <div class="story-type-list-ele" :class="{selected : type == 4}">言情</div>
                </div>
            </div>

            <div class="story-title story-block">
                <div class="story-title-title">
                    故事标题:
                </div>
                <div class="story-title-input-wrp">
                    <input type="text" class="story-title-input"
                    placeholder="快给故事起一个响亮的标题吧"
                    v-model="title">
                </div>
            </div>


            <div class="submit-wrp">
                <div class="submit" @click="submit()">
                    我要续写
                </div>
            </div>
        </div>
            
    </div>
</template>




<script>
import api from '../api/index'
import store_util from '../util/store_obj'
import change_cur_story from '../api/change_story'
import {time} from '../util/date'




export default {


    /** 进入页面前开始加载数据 */
    beforeMount() {
        if(!this.store_util.get_session("user")) {
            this.$router.replace("/")
            return
        }
        api.createStory().then(
            (res) => {
                console.log(res)
                if(res.code == 0) {
                    /** 统一type的编码口径 防止越界 */
                    if(res.data.type > 4) {
                        res.data.type = 2
                    }

                    this.store_util.store_session("curStory", res.data)
                    this.content = this.story_content[this.store_util.get_session("curStory").content]
                    this.type = this.store_util.get_session("curStory").type
                    this.id = this.store_util.get_session("curStory").id
                } else if(res.code == 601) {
                    window.alert("登录状态已过期 请重新登录")
                    this.$router.replace("/")
                }    
            }
        )

         window.addEventListener("beforeunload", e => this.before_unload_Fn(e))
    },

    mounted() {
       
    },

    // beforeRouteLeave (to, from, next) {
    // // 导航离开该组件的对应路由时调用
    // // 可以访问组件实例 `this`
    //     console.log("in")

    // },
    data() {
        return {
            title: "",
            /** 当前生成的故事对象 */
            content: "",
            type: "",
            id: "",

            /** 是否完成了提交 */
            has_submit: false
        }
    },

    methods: {
        tab(page) {
            this.curPage = page
        },

        submit() {
            if(this.title.length < 1 || this.title.length > 35) {
        
                window.alert("标题不能太长也不能太短")
                return
            }
            
            this.has_submit = true
            api.setTitle({id: this.id, title: this.title}).then(
                (res) => {
                    if(res.code == 0) {
                        this.$router.replace("/FirstWrite")
                    }
                }
            )
            
        },
        
        first_write() {
            api.setStoryTitle({id: this.id ,title: this.title}).then(
                (res) => {
                     this.$router.push(`/FirstWrite/${this.id}`)
                }
            )
        },

        /** 切换当前故事开头和type */
        change_story() {
            change_cur_story(this.id).then((res) => {
                if(res) {
                    this.content = this.story_content[this.store_util.get_session("curStory").content]
                    this.type = this.store_util.get_session("curStory").type
                    this.id = this.store_util.get_session("curStory").id
                }
            })
        },

        /** 页面关闭之前执行的函数
         * 
         */

        before_unload_Fn (e) {
            /** 如果没有完成提交就强制退出页面 则视之为放弃 自动删除 */
           if(!this.has_submit) {
               api.deleteStory_sync(this.id)
               console.log("delete")
               /** 使用同步版本的方法对远台进行操作
                * 这里是做一个模拟
                */


            //    let has_del = false
            //    api.deleteStory(this.id).then(
            //        (res) => {
            //            if(res.code == 0) {
            //                has_del = true
            //            }
            //        }
            //    )

             

            //    change_cur_story(this.id)
            //    .then(console.log("hhh"))
            //    let changed = typeof store_util.get_session("inChange") == Number || 0
            //    store_util.store_session("inChange", {changed: time()})
               // 加一段同步代码阻塞一下，不然刷新会发不出去异步请求
            // let now = new Date()
            // while (new Date() - now < 5000) {}
           }
        }
        
    }
}
</script>


<style scoped>

    /* 惯用色 */
    /* rgb(9, 155, 123) */

    .total-wrp {


        height: 100%;
        display: flex;
        flex-direction: column;
        align-items: center;
        /** 防止底部与tabber重合 */
    }

    .total {
        position: relative;

        height: 100%;
        display: flex;
        flex-direction: column;
        align-items: flex-start;
        /** 防止底部与tabber重合 */

        font-size: 20px;
    }

    
    .total>div {
        margin: .3em 0;
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

    /** 开头部分防重叠 */
    .total>div:nth-child(2) {
        margin-top: 3em;
    }

    /** 区块统一规则 */

    .story-block>div:nth-child(1) {
        font-size: 25px;
        color: rgb(13, 104, 84);
        text-align: left;
    }

    .story-block {
        max-width: 16em;
        
    }

    /** 规则说明块部分 */
    .ins {
        text-align: left;
        color: rgb(19, 117, 96);

    }

    .ins-title {
        font-size: 20px;
    }

    .ins-content {
        font-size: 12px;
        font-weight: 800;
    }



    /** 故事开头部分 */
    .story-begin-change {
        font-size: 12px;
        float: right;

        position: relative;
        right: -3em;
    }

    .change-logo {
        width: 2em;
        height: 2em;
        display: block;
        margin: 0 auto;
    }

    .story-begin-title {
        margin: .5em 0;
    }

    .story-begin-content {
        border: solid 1px rgb(14, 82, 67);
        border-radius: 19px;

        background: rgb(246, 244, 244);

        
        min-width: 340px;

        min-height: 140px;
        padding: .5em;

        text-align: left;

    }


    /** 故事类型选择部分 */
    .story-type-list {
        display: flex;
        align-items: center;
        justify-content: start;

        min-width: 130%;
        /* max-width: 90vw; */

        border-bottom: solid 1px rgb(41, 88, 88);

    }
        /** 给不同的类型不同的颜色 */
        
    .story-type-list-ele:nth-child(1) {
        color: rgb(53, 129, 196);
    }

    .story-type-list-ele:nth-child(2) {
        color: rgb(226, 133, 133);
    }
    

    .story-type-list-ele:nth-child(3) {
        color: rgb(92, 177, 165);
    }

    .story-type-list-ele:nth-child(4) {
        color: rgb(34, 120, 233);
    }

    .story-type-list-ele:nth-child(5) {
        color: rgb(209, 135, 49);
    }

        /** 设置合适的间隔 */
    .story-type-list-ele {
        margin: .4em .7em;
        font-size: 18px;
        white-space: nowrap;
        letter-spacing: .2em;

        /* display: inline-block; */
    }

    .selected {

        position: relative;
        color: rgba(255,255,255,0);
    }

    .selected::before {
        content: "";
        background: rgba(0,0,0,1);
        border-radius: 2px;
        filter: blur(1.5px);

        position: absolute;
        left: -1px;
        right: -1px;
        top: -1px;
        bottom: -1px;

        z-index: -1;

        
    }


    /** 设置标题的输入框 */
    .story-title-input {
        font-size: 20px;

        outline: 0;
        border: solid 1px rgb(8, 63, 51);
        border-radius: 10px;
        
        text-align: center;
        padding: .8em 2em;

        min-width: 270px;

        

        /* font-size: 25px; */

        margin: 1em 0;
    }


      /** 按钮样式 */

    .submit-wrp {
        text-align: center;
        font-size: 20px;
    }

    .submit {
        display: inline-block;

         /* border: solid 2px rgb(16, 99, 81); */
         border-radius: 3em;

         color: rgb(9, 104, 96);
         background: rgb(221, 221, 221);

         text-align: center;
         font-weight: 800;
         padding: .7em 3em;

         margin: 1em auto;
         margin-left: 3em;
         margin-top: 2em;
    }

    
</style>