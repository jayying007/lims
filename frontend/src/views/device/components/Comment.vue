<template>
  <div>
    <div>
      <el-input v-model="commentTemplate.comment" :autosize="{ minRows: 5, maxRows: 10}" type="textarea" placeholder="请留言" />
      <el-button style="margin: 10px" type="primary" @click="addComment()">留言</el-button>
      <el-divider><i class="el-icon-chat-dot-square"></i></el-divider>
    </div>

    <div class="user-activity" v-for="comment in commentList" v-bind:key="comment.deviceCommentId">
      <div class="post">
        <div class="user-block">
          <img class="img-circle" :src="comment.user.avatar">
          <span class="username text-muted">{{comment.user.name}}</span>
          <span class="description">{{comment.commentTimestamp | parseTime('{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </div>
        <p>{{comment.comment}}</p>
      </div>
    </div>
  </div>
</template>

<script>
import { getCommentList, addComment} from "@/api/device";

const api = {
  getCommentList,
  addComment
}

export default {
  data() {
    return {
      comment: '',
      commentList: [],
      deviceId: null,
      //发布留言模板
      commentTemplate: {
        device: {
          deviceId: undefined
        },
        comment: null
      }
    }
  },
  created() {
    this.deviceId = this.$route.path.substr(this.$route.path.lastIndexOf("/") + 1)
    this.getCommentList()
  },
  methods: {
    getCommentList() {
      api.getCommentList(this.deviceId).then(response => {
        this.commentList = response.data
      })
    },
    addComment() {
      this.commentTemplate.device.deviceId = this.deviceId
      api.addComment(this.commentTemplate).then(() => {
        this.commentTemplate.comment = null
        this.$message.success({
          message: "留言成功",
          duration: 1000 * 2
        })
        //refresh
        this.getCommentList()
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.user-activity {
  .user-block {

    .username,
    .description {
      display: block;
      margin-left: 50px;
      padding: 2px 0;
    }

    .username{
      font-size: 16px;
      color: #000;
    }

    :after {
      clear: both;
    }

    .img-circle {
      border-radius: 50%;
      width: 40px;
      height: 40px;
      float: left;
    }

    span {
      font-weight: 500;
      font-size: 12px;
    }
  }

  .post {
    font-size: 14px;
    border-bottom: 1px solid #d2d6de;
    margin-bottom: 15px;
    padding-bottom: 15px;
    color: #666;

    .image {
      width: 100%;
      height: 100%;

    }

    .user-images {
      padding-top: 20px;
    }
  }

  .list-inline {
    padding-left: 0;
    margin-left: -5px;
    list-style: none;

    li {
      display: inline-block;
      padding-right: 5px;
      padding-left: 5px;
      font-size: 13px;
    }

    .link-black {

      &:hover,
      &:focus {
        color: #999;
      }
    }
  }

}

.box-center {
  margin: 0 auto;
  display: table;
}

.text-muted {
  color: #777;
}
</style>
