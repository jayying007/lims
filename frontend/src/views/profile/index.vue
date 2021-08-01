<template>
  <div class="app-container">
    <div v-if="user">
      <el-row :gutter="20">

        <el-col :span="6" :xs="24">
          <user-card :user="user" />
        </el-col>

        <el-col :span="18" :xs="24">
          <el-card>
            <el-tabs v-model="activeTab">
              <el-tab-pane label="账号设置" name="account">
                <account :user="user" />
              </el-tab-pane>
            </el-tabs>
          </el-card>
        </el-col>

      </el-row>
    </div>
  </div>
</template>

<script>
import UserCard from './components/UserCard'
import Account from './components/Account'
import { getInfo } from '@/api/user'

export default {
  name: 'Profile',
  components: { UserCard, Account },
  data() {
    return {
      user: null,
      activeTab: 'account'
    }
  },
  computed: {

  },
  created() {
    getInfo().then(response => {
      this.user = response.data
    }).catch(error => {
      console.log(error)
    })
  },
  methods: {

  }
}
</script>
