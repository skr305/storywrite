<template>
    <div class="total-wrp">

        <div class="total">
            
            <div class="head">
                第一次续写
            </div>

            <div class="story-title">
                {{ story_detail[0].title }}
            </div>

            <div class="story-begin">
                {{ begin_content }}
            </div>

            <div class="story-write">
                <div class="story-write-ins">
                    现在请写下你的奇思妙想吧
                </div>

                <textarea class="story-write-area" v-model="content">
                </textarea>

                <div class="story-write-option">
                    <div class="submit finish" @click="finish()">我写完了</div>
                    <div class="submit give-up">我写不下去了</div>
                </div>
                
            </div>

        </div>
            
    </div>
</template>




<script>

import api from '../api/index'
import {backEndUrl} from '../api/index'



export default {

    beforeMount() {
        if(!this.store_util.get_session("user")) {
            this.$router.replace("/")
            return
        }
        api.storyDetail(this.id).then(
            (res) => {
                console.log(res)
                if(res.code == 0) {
                    console.log(res.data)
                    this.story_detail = res.data
                    
                    /** 视上次强退行为是刷新 不删除此故事 */
                    let delete_list = this.store_util.get_local("delete_list")
                    delete_list[this.id] = false
                } else if(res.code == 601) {
                    window.alert("登录状态已过期 请重新登录")
                    this.$router.replace("/")
                }
            }
        )

         window.addEventListener("beforeunload", e => this.before_unload_Fn(e))
    },
    
    data() {
        return {
           
           content: "",
           story_detail: "",
           /** 故事的开头内容 */
           begin_content: this.story_content[this.store_util.get_session("curStory").content],
           has_submit: false,
           type: this.store_util.get_session("curStory").type,
           id: this.store_util.get_session("curStory").id,
        }
    },

    methods: {

        tab(page) {
            this.curPage = page
        },

        finish() {
            if(!window.confirm("确认提交吗?(一旦确认后内容将无法修改, 请三思)")) {
                return
            }
            this.has_submit = true
            /** 解构赋值 */
            api.writeStory({content: this.content, id: this.id, type: this.type}).then(
                (res)=> {
                    if(res.code == 0) {
                        /** 跳出弹窗 表示可以分享了 */
                        window.alert(`您已经写完了 即刻发送给好友吧! 
                        url:${backEndUrl}NormalWrite/${this.id}`)

                        this.$router.replace("/MainPage")
                    } else if(res.code == 601) {
                        window.alert("登录状态已过期 请重新登录")
                        this.$router.replace("/")
                    }
                    
                }
            )
        },

        /** 页面关闭之前执行的函数
         * 
         */

        before_unload_Fn (e) {
            /** 如果没有完成提交就强制退出页面 则视之为放弃 自动删除 */
        //    if(!this.has_submit) {
        //        api.deleteStory_sync(this.id)
        //    }

            if(!this.has_submit) {
                let delete_list = this.store_util.get_local("delete_list")
                /** 把要删除的故事id先记录进去
                 *  之后如果是刷新行为 就会重新把此id对应的键值设置为false
                 *  键值被设置为true的id的对应故事会在返回主页面后被删除
                 */

                 delete_list[this.id] = true
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

        max-width: 20em;
    }

    
    .total>div {
        margin: 1em auto;
    }


    /** 故事标题部分 */
    .story-title {

        font-size: 25px;
        font-weight: 700;

        padding: .3em .5em;

        text-align: center;
        /* margin: .5em auto; */

        border-bottom: solid 2px rgb(9, 155, 123);

        color: rgb(9, 155, 123);

        min-width: 220px;

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


    /** 故事创作区域 */
    .story-write-ins {
        margin: 1em auto;
        font-weight: 700;
    }

    .story-write-area {
        min-height: 12em;
        min-width: 270px;

        max-width: 15em;

        border-radius: 1em;
        padding: 1em 1em;

        font-size: 20px;
        outline: 0;

        background: rgb(250, 246, 246);
    }

    .story-write-area:focus {
        border: 2px solid rgb(120, 182, 173);
    }


    .story-write-option {
        display: flex;
        align-items: center;
        
        margin: 1em auto;
        justify-content: space-around;
    }

    /** 提交区域 */

    .story-write-option {
        max-width: 90vw;
    }

    .submit {
         display: inline-block;

         /* border: solid 2px rgb(9, 155, 123); */
         border-radius: 30px;

         color: rgb(7, 131, 121);
         background: rgb(236, 232, 232);

         text-align: center;
         padding: .5em 1em;

         margin: 1em 1em;
         

         white-space: nowrap;
    }

</style>