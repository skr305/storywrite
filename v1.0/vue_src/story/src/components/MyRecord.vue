<template>
    <div class="total-wrp">
        <div class="total" v-if="load_done">
            <div class="head">
                我的创作
            </div>
            <div class="recom">
                <div class="recom-block" v-for="ele in my_list" :key="ele.id">
                    <div class="recom-block-name">{{ele.main_writer ? (ele.main_writer)[0]: "佚"}}{{ele.main_writer ? (ele.main_writer) : "佚名"}}</div>
                    <div class="recom-block-title" @click="check_detail(ele.id)">{{ ele.title }}</div>

                    <!-- 因为区块内部的数据不会及时刷新 所以这里搞个sign 使得这个区块会被强制刷新 -->
                    <div class="recom-block-echo">
          
                        <span class="like recom-block-info">
                             <img src="../assets/zan.png" alt="" class="echo-logo">
                            {{ele.like}}
                        </span>
                        
                        <span class="star recom-block-info">
                             <img src="../assets/coll.png" alt="" class="echo-logo">
                            {{ele.star}}
                        </span>
                    </div>
                </div>
            </div>

            <div class="none-tip" v-if="!my_list">
                暂时还空空如也哦~
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



export default {
   

    beforeMount() {
        if(!this.store_util.get_session("user")) {
            this.$router.replace("/")
            return
        }
        api.getMyWrite().then(
            (res) => {
                if(res.code == 0) {
                
                    this.my_list = res.data
                    this.load_done = true
                } else if(res.code == 601) {
                    window.alert("登录状态已过期 请重新登录")
                    this.$router.replace("/")
                }
            }
        )
    },

    data: () => {
        return {
           my_list: [],
           /** 代表数据加载中 */
           load_done: false,
        }
    },

    methods: {
        check_detail(id) {
            this.$router.push(`/MyDetail/${id}`)
        }
    }
}
</script>


<style scoped>
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
        align-items: center;

        /** 防止底部与tabber重合 */
        padding-bottom: 6em;

        font-size: 20px;
    }

    /** 导航条部分 */

    .nav-bar {
        /** 定位到最底下 */
        position: fixed;
        bottom: 0em;
        /* left: 50%;
        transform: translate(-50%, 0); */

        display: flex;
        align-items: center;

        border: solid .1em rgb(5, 92, 87);
        border-radius: 1em 1em 0 0;
        font-size: 25px;
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

    .nav-ele {
        padding: 1em 3em;
        text-align: center;

        white-space: nowrap;
    }

    .nav-ele:nth-child(1) {
        border-right: solid .1em rgb(5, 92, 87);
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


    /** 个人信息部分 */

    .person {
        font-size: 30px;
    }


    .person-avatar {
        width: 7em;
        height: 7em;

        border-radius: 50%;
        border: solid 2px black;

        margin: 1em auto;
    }


    .person-info {
        font-weight: 600;
        padding: .5em 0;
    }

    .person-info-ele {
        margin: .6em 0;
        text-align: left;
    }

    .person-option-ele {

        padding-right: 7em;
        padding-bottom: .2em;
        padding-left: .2em;

        margin: 1.4em 0;

        border-bottom: 2px solid rgb(5, 92, 87);  

        position: relative;
        text-align: left;

        white-space: nowrap;
        
    }

    .person-option-ele::after {
        content: ">";
        vertical-align: baseline;
        
        position: absolute;
        right: 1em;
    }

    .write-record::before {
        content: "🖊";
    }

    .star-collection::before {
        content: "💗";
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