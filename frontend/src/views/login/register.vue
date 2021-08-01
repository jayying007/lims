<template>
  <div class="register-container">
    <el-form class="register-form" label-position="left">
      <div class="title-container">
        <h3 class="title">账号注册</h3>
      </div>

      <el-form-item>
        <span class="svg-container">
          <svg-icon icon-class="user" />
        </span>
        <el-input v-model="registerForm.name" type="text" tabindex="1" placeholder="姓名" />
      </el-form-item>

      <el-form-item>
        <span class="svg-container">
          <svg-icon icon-class="lock" />
        </span>
        <el-input v-model="registerForm.password" :type="passwordType" tabindex="2" placeholder="密码" />
        <span class="show-pwd" @click="showPassword">
          <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />
        </span>
      </el-form-item>

      <el-form-item>
        <span class="svg-container">
          <svg-icon icon-class="email" />
        </span>
        <el-input v-model="registerForm.email" type="text" tabindex="3" placeholder="邮箱" />
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="16">
          <el-form-item>
            <span class="svg-container">
              <svg-icon icon-class="key" />
            </span>
            <el-input v-model="registerForm.verifyCode" type="text" tabindex="4" placeholder="验证码" />
          </el-form-item>
        </el-col>

        <el-col :span="8" style="margin: 6px 0">
          <el-button style="width: 100%" @click="sendVerifyCode" :class="{disabled: !this.canClick}">{{content}}</el-button>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="16">
          <el-button style="width: 100%" type="primary" @click="submitForm">注册</el-button>
        </el-col>

        <el-col :span="8">
          <router-link to="/login">
            <el-button style="width: 100%" type="info">前往登录</el-button>
          </router-link>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script>
import {registerAccount} from "@/api/user"
import {sendVerifyCode} from "@/api/email"

const api = {
  registerAccount,
  sendVerifyCode
}

export default {
  name: 'register',
  data() {
    return {
      registerForm: {
        name: '',
        password: '',
        email: '',
        verifyCode: ''
      },
      passwordType: 'password',
      /* 验证码相关配置 */
      content: '发送验证码',
      totalTime: 30,
      canClick: true
    };
  },
  methods: {
    showPassword() {
      if (this.passwordType === 'password') {
        this.passwordType = ''
      } else {
        this.passwordType = 'password'
      }
    },
    sendVerifyCode() {
      if(this.canClick) {
        this.canClick = false
        this.content = this.totalTime + 's后重新发送'
        let clock = window.setInterval(() => {
          this.totalTime--
          this.content = this.totalTime + 's后重新发送'
          if (this.totalTime < 0) {     //当倒计时小于0时清除定时器
            window.clearInterval(clock)
            this.content = '发送验证码'
            this.totalTime = 30
            this.canClick = true
          }
        },1000)
        //发送验证码
        api.sendVerifyCode(this.registerForm.email)
      }
    },
    submitForm() {
      api.registerAccount(this.registerForm).then(() => {
        //只有请求成功才会到这里
        //请求失败时统一在request.js中拦截，输出错误信息
        this.$message({
          message: '注册成功',
          type: 'success'
        })
        this.$router.push({ path: '/login' })
      }).catch(() => {

      })
    },
  }
}
</script>


<style lang="scss" scoped>
.disabled{
  background-color: #ddd;
  border-color: #ddd;
  color:#57a3f3;
  cursor: not-allowed; // 鼠标变化
}
</style>

<style lang="scss">
$bg:#283443;
$light_gray:#fff;
$cursor: #fff;
$dark_gray:#889aa4;

/* reset element-ui css */
.register-container {
  min-height: 100%;
  width: 100%;
  background-color: $bg;
  overflow: hidden;

  .el-input {
    display: inline-block;
    height: 47px;
    width: 85%;

    input {
      background: transparent;
      border: 0;
      -webkit-appearance: none;
      border-radius: 0;
      padding: 12px 5px 12px 15px;
      color: $light_gray;
      height: 47px;
      caret-color: $cursor;

      &:-webkit-autofill {
        box-shadow: 0 0 0 1000px $bg inset !important;
        -webkit-text-fill-color: $cursor !important;
      }
    }
  }

  .el-form-item {
    border: 1px solid rgba(255, 255, 255, 0.1);
    background: rgba(0, 0, 0, 0.1);
    border-radius: 5px;
    color: #454545;
  }

  .register-form {
    position: relative;
    width: 520px;
    max-width: 100%;
    padding: 160px 35px 0;
    margin: 0 auto;
    overflow: hidden;
  }

  .svg-container {
    padding: 6px 5px 6px 15px;
    color: $dark_gray;
    vertical-align: middle;
    width: 30px;
    display: inline-block;
  }

  .title-container {
    position: relative;

    .title {
      font-size: 26px;
      color: $light_gray;
      margin: 0 auto 40px auto;
      text-align: center;
    }
  }

  .show-pwd {
    position: absolute;
    right: 10px;
    top: 7px;
    font-size: 16px;
    color: $dark_gray;
    cursor: pointer;
    user-select: none;
  }
}
</style>
