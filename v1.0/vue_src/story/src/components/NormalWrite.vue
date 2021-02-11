<template>
<div class="total-wrp">

    <div class="total" v-if="story_exist">
            <div class="head">
              续写故事
            </div>
            <div class="story-title">
                {{ story_detail[0].title }}
            </div>

            <div class="story-begin">
                {{ begin_content }}
            </div>

            <div class="story-content">
                <div class="story-content" v-for="i in story_detail.length-1" :key="i">
                <div class="story-content-name">⚪{{ story_detail[i].writer }}</div>
                <div class="story-content-title">{{ story_detail[i].content }}</div>
            </div>
            </div>


            <div class="editable" v-if="editable">
                 <!-- 还未点击"我要续写"前的面板 -->
                <div class="un-ready" v-if="!ready">
                    
                    <div class="ins">
                        <div class="ins-title">规则:</div>

                        <!-- 非最后一位续写者时的规则 -->
                        <div class="ins-content"  v-if="!lastWriter">
                            1.点击按钮随机生成一个开头和故事类型，点击"我要续写" 开始续写
                            <br> 故事，建议续写100~200字 <br>

                            2.完成后，可以将此故事分享给你的好友让ta接着续写<br>
                            好友写完后ta也可以分享给ta的好友接着写，以此类推，知道第四个好友<br>
                            ta的任务就是帮你们的故事写一个合乎逻辑的结尾啦!
                            <br>
                            3.好友之间要通力合作写出精彩的故事，这样才能获得更多的赞和收藏哦

                        </div>
                        <!-- 最后一位续写者时的规则 -->
                        <div class="ins-content" v-else> 
                            1.你是这个故事的最后一个续写人啦!
                            <br>2.你的任务就是给上面的故事写一个合乎逻辑的结尾，看看你能不能<br>
                            把上面天马行空的情节给圆回来
                            <br>3.写完之后点击"我写完啦"完成故事的续写，然后可以分享给你的好友们<br>
                            看看你们的大作吧
                        </div>
                    
                    </div>
                    
                    <!-- 正式开始续写 -->
                    <div class="submit" @click="ready_write()">我要续写</div>
                    
                </div>


                <!-- 选择续写后进入的面板 -->
                <div class="ready" v-else>
                <div class="story-type" v-if="!beginEdit">
                    <div class="story-type-title" v-if="!lastWriter">
                        系统给你选定的故事内容是:
                    </div>

                    <div class="story-type-list" v-if="!lastWriter">
                        <div class="story-type-list-ele" :class="{selected : type == 0}">珂幻</div>
                        <div class="story-type-list-ele" :class="{selected : type == 1}">悬疑</div>
                        <div class="story-type-list-ele" :class="{selected : type == 2}">惊悚</div>
                        <div class="story-type-list-ele" :class="{selected : type == 3}">恐怖</div>
                        <div class="story-type-list-ele" :class="{selected : type == 4}">吓人</div>
                    </div>

                    <div class="story-type-tip">
                        准备好了吗? 开始吧?
                    </div>

                    <div class="submit" @click="begin()">
                        开始续写
                    </div>

                </div>

                <div class="story-write" v-else>
                    <div class="story-write-ins">
                        现在请写下你的奇思妙想吧
                    </div>

                    <textarea class="story-write-area" v-model="content">
                    </textarea>

                    <div class="story-write-option">
                        <div class="submit finish" @click="finish()">我写完了</div>
                        <div class="submit give-up" @click="give_up()">我写不下去了</div>
                    </div>
                    
                </div>

            </div> 
        </div>

        <div class="un-editable" v-else>
            故事完成啦
        </div>
    </div>

           

    <div v-else class="not-found">
            404
    </div>
            
</div>
</template>




<script>
import api from '../api/index'

// 20210201120121
export default {


    beforeMount() {
        if(!this.store_util.get_session("user")) {
            this.$router.replace("/")
            return
        }

        this.init()
        /** 添加页面关闭事件 */
        window.addEventListener("beforeunload", e => this.before_unload_Fn(e))
    },

    data() {

        return {
           
            story_exist: true,
            story_detail: [{title:"加载中"}],

            begin_content: "",

            /** 此段故事的信息 */
            id: this.$route.params.id,
            type: "",
            content: "",

            /** 用来确定在写作流中的位置的 */
            beginEdit: false,
            ready: false,

            /** 是否是最后一位续写者 */
            lastWriter: false,
            /** 用来确认是否故事段数已满  */
            editable: false
        }
    },

    methods: {
        /** 加载初始数据 */
        init() {
            api.storyDetail(this.$route.params.id).then(
                (res) => {
                    if(res.code == 0) {
                        console.log(res.data)

                            /** 判断这次编辑的情况 */
                            this.lastWriter = res.data.length == 4
                            this.editable = res.data.length < 5
                            /** 关于这个故事全部 */
                            this.story_detail = res.data
                            /** 加载故事的开头 */
                            this.begin_content = this.story_content[res.data[0].content_id] 
                        
                        } else if(res.code == 601) {
                            window.alert("登录状态已过期 请重新登录")
                            this.$router.replace("/")
                        } else {
                            this.story_exist = false
                            console.log(404)
                        }
                    }
                )
             },


        tab(page) {
            this.curPage = page
        },

        ready_write() {
            /** 刷新一遍数据 */
            this.init()

            api.storyStatus(this.id).then(
                (res) => {
                    if(res.code == 0) {
                        /** 看看是否有人在写 */
                        if(res.data.flag) {
                            window.alert("现在有人在写")
                        } else {
                            if(!this.lastWriter) {
                                this.type = Math.floor(Math.random() * 5)
                            } 
                            /** 最后一个写作者没有类型 */
                            else {
                                this.type = -1
                            }
                            this.ready = true

                            console.log(this.type)
                            /** 给这个故事记上 有人在写惹 */
                            api.setStatus({id: this.id, status: true})
                        }
                    } else if(res.code == 601) {
                        window.alert("登录状态已过期 请重新登录")
                        this.$router.replace("/")
                    }
                }
            )
            
        },

         finish() {
             if(window.confirm("确认提交吗")) {
                /** 解构赋值 */
                api.writeStory({content: this.content, id: this.id, type: this.type}).then(
                    (res)=> {
                        if(res.code == 0) {
                            /** 跳出弹窗 表示可以分享了 */
                            window.alert(`您已经写完了 即刻发送给好友吧! 
                            url:NormalWrite/${this.id}`)
                            this.before_unload_Fn({})

                            this.$router.replace("/MainPage")
                        } else if(res.code == 601) {
                            window.alert("登录状态已过期 请重新登录")
                            this.$router.replace("/")
                        }
                        
                    }
                )
            }
        },

        begin() {
            this.beginEdit = true
        },

        give_up() {
            if(window.confirm("你确定要删除吗")){
                /** 重新进入初始页面 */
                this.before_unload_Fn({})
            }   else {
                return
            }
        },

         /** 页面关闭之前执行的函数
         * 
         */

        before_unload_Fn (e) {
            this.ready = false
            this.beginEdit = false
            api.setStatus_sync({id: this.id, status: false}) 
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

        max-width: 20em;
    }

    
    .total>div {
        margin: 1em auto;
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


    /** 故事标题部分 */
    .story-title {
        padding: .3em 3em;
        text-align: center;
        /* margin: .5em auto; */

        border-bottom: solid 2px rgb(9, 155, 123);

        color: rgb(9, 155, 123);

    }

    /** 故事开头内容区块 */
    .story-begin {

        padding: .5em .5em .5em .5em;
        
        min-height: 6em;
        min-width: 8em;

        max-width: 90vw;

        border: solid 2px rgb(9, 155, 123);
        border-radius: 1em;

        font-weight: 100;
    }

    /** 故事续写内容区块 */

     .story-content {
        font-size: 20px;
        padding: .5em 0;
     }

    .story-content-name {
        text-align: left;
        /* padding-right: 10em; */
    }

    .story-content-title {
        text-align: left;

        min-width: 16em;
        min-height: 7em;
        padding: .5em .5em .5em .5em;

        border: 2px solid rgb(5, 92, 87);
        border-radius: 1em;

        margin: .5em 0;
    }


     /** 故事类型选择部分 */
    .story-type-list {
        display: flex;
        align-items: center;
        justify-content: space-around;
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
        font-size: 22px;
        white-space: nowrap;
    }
    /** 被选择后的样式 */
    .selected {
        background: black;
        color: white;
    }



    /** 故事创作区域 */
    .story-write-ins {
        margin: 1em auto;
        font-weight: 100;
    }

    .story-write-area {
        min-height: 6em;
        min-width: 8em;

        max-width: 15em;

        border-radius: 1em;
        padding: 1em 1em;

        font-size: 20px;
        outline: 0;

    }

    .story-write-area:focus {
        border: 1.2px solid rgb(10, 230, 193);
    }


    .story-write-option {
        display: flex;
        align-items: center;
        
        margin: 1em auto;
        justify-content: space-around;
    }

    /** 按钮 */
    .submit {
         display: inline-block;

         border: solid 2px rgb(9, 155, 123);
         border-radius: 30px;

         color: rgb(9, 104, 96);

         text-align: center;
         padding: .5em 1em;

         margin: 1em 1.5em;
         margin-top: 2em;

         white-space: nowrap;
    }

</style>