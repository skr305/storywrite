<template>
<div class="total-wrp">

    <div class="total" v-if="story_exist">
            <!-- <div class="head">
                故事详情
            </div> -->
            <div class="head">
                故事详情
            </div>
            
            <div class="story-title">
                {{ story_detail[0].title }}
            </div>

            <div class="date-wrp">
                      创作时间: {{story_detail[0].date || "2007-07-06 12:31:21"}}
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


            <div class="editable bottom-tip" v-if="editable">
              ⭐故事还未完成
            </div>

            <div class="un-editable bottom-tip" v-else>
                故事完成啦
            </div>

            <div class="echo-bar">
                <span class="star recom-block-info echo-block" v-if="has_like" @click="un_like()">
                            <img src="../assets/has-like.png" alt="" class="echo-logo">
                            {{story_detail[0].like}}
                        </span>

                        <span class="like recom-block-info echo-block" v-else @click="like()">
                             <img src="../assets/zan.png" alt="" class="echo-logo">
                            {{story_detail[0].like}}
                            </span>
                        
                        <span class="like recom-block-info echo-block" v-if="has_star" @click="un_star()">
                             <img src="../assets/has-star.png" alt="" class="echo-logo">
                            {{story_detail[0].star}}
                        </span>
                        <span class="star recom-block-info echo-block" v-else @click="star()">
                             <img src="../assets/coll.png" alt="" class="echo-logo">
                            {{story_detail[0].star}}
                        </span>
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
     
    },

    data() {

        return {
           
            story_exist: true,
            story_detail: [{title:"加载中"}],
            begin_content: "",

          

            /** 此段故事的信息 */
            id: this.$route.params.id,

            /** 用来确认是否故事段数已满  */
            editable: false,

            has_like: this.store_util.get_local("has_like")[this.$route.params.id],
            has_star: this.store_util.get_local("has_star")[this.$route.params.id]
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
                            
                            this.has_like =  this.store_util.get_local("has_like")[this.store_util.get_session("user").username][this.$route.params.id] || false
                            this.has_star =  this.store_util.get_local("has_star")[this.store_util.get_session("user").username][this.$route.params.id] || false
                        
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

        delete_star() {
            if(window.confirm("你确定要取消收藏吗")) {
                api.un_star(this.id)
                this.$router.go(-1)
            } 
        },

        star() {
            api.star(this.id)
            this.has_star = true
            this.story_detail[0].star++
        },

        like() {
            api.like(this.id)
            this.has_like = true
            this.story_detail[0].like++
        },

        un_star(ele) {
            api.un_star(this.id)
            this.has_star = false
            this.story_detail[0].star--
        },

        un_like(ele) {
            api.un_like(this.id)
            this.has_like = false
            this.story_detail[0].like--
        },
        check_detail(id) {
            this.$router.push(`MainDetail/${id}`)
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

        

        border-bottom: solid 2px rgb(9, 155, 123);

        color: rgb(9, 155, 123);

    }

     /** 时间戳 */
    .date-wrp {
        font-size: 14px;
        font-weight: 100;
        float: right;

        position: relative;
        top: -2em;
        
    }


      /** 故事开头内容区块 */
    .story-begin {
        padding: .5em .5em .5em .5em;
        
        min-height: 7em;
        min-width: 300px;
        text-align: left;
        font-size: 18px;

        border: solid 1px rgb(14, 51, 43);
        border-radius: 10px;

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


    /** 删除故事按钮 */
    .delete-story {


         /** 调的不那么明显 */
        font-size: 16px;
        opacity: .7;

         border-bottom: solid 1px rgb(130, 224, 217);
         color: rgb(119, 221, 212);

         text-align: center;
         padding: .5em 1em;

         margin: 1em 1.5em;
         margin-top: 2em;

         white-space: nowrap;
    }



    /** 点赞收藏条 */
    .echo-bar {

        border-top: 1px solid black;

        position: fixed;
        left: 50%;
        transform: translate(-50%, 0);

        bottom: -1em;

        min-width: 360px;
        background: white;
        padding: 1em 1em;

        display: flex;
        justify-content: space-evenly;
    }

    /** 反馈区块 */

    .echo-logo {
        width: 27px;
        height: 27px;   
    }


    /** 底部提示语 */
     .bottom-tip {
         margin-bottom: 6em !important;
     }

</style>