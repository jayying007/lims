<template>
  <el-form>
    <el-upload
        action="http://127.0.0.1:8080/upload"
        :show-file-list="false"
        :on-success="handleAvatarSuccess">
      <el-button>更新头像</el-button>
    </el-upload>

    <el-form-item label="姓名">
      <el-input v-model.trim="user.name" />
    </el-form-item>

    <el-form-item label="密码">
      <el-input type="password" v-model.trim="user.password" placeholder="不填表示使用旧密码" />
    </el-form-item>

    <el-form-item>
      <el-button type="primary" @click="submit">确认修改</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { updateInfo } from "@/api/user"

export default {
  props: {
    user: {
      type: Object,
      default: () => {
        return {
          userId: undefined,
          name: '',
          email: '',
          avatar: '',
          role: '',
          password: ''
        }
      }
    }
  },
  data() {
    return {

    }
  },
  created() {
    this.user.password = null
  },
  methods: {
    submit() {
      updateInfo({ userId: this.user.userId, name: this.user.name, password: this.user.password }).then(response => {
        this.$store.dispatch('user/getInfo')
        this.$message({
          message: '修改个人信息成功',
          type: 'success',
          duration: 3 * 1000
        })
      })
    },
    handleAvatarSuccess(res) {
      this.user.avatar = res.data
      updateInfo({ userId: this.user.userId, avatar: res.data }).then(response => {
        this.$store.dispatch('user/getInfo')
      })
    },
  }
}
</script>
