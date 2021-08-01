<template>
  <div class="app-container">
    <div class="filter-container">
      <el-row :gutter="20">
        <el-col :span="3">
          <el-input v-model="listQuery.name" placeholder="用户名" clearable/>
        </el-col>

        <el-col :span="3">
          <el-select v-model="listQuery.role" placeholder="身份" clearable>
            <el-option v-for="item in roleOptions" :key="item.key" :label="item.value" :value="item.key" />
          </el-select>
        </el-col>

        <el-col :span="3">
          <el-select v-model="listQuery.status" placeholder="状态" clearable>
            <el-option v-for="item in statusOptions" :key="item.key" :label="item.value" :value="item.key" />
          </el-select>
        </el-col>

        <el-col :span="3">
          <el-button type="primary" icon="el-icon-search" @click="getUserList">搜索</el-button>
        </el-col>
      </el-row>
    </div>

    <el-table v-loading="listLoading" :data="userList" border>
      <el-table-column label="ID">
        <template slot-scope="scope">
          {{ scope.row.userId }}
        </template>
      </el-table-column>

      <el-table-column label="用户名">
        <template slot-scope="scope">
          {{ scope.row.name }}
        </template>
      </el-table-column>

      <el-table-column label="邮箱">
        <template slot-scope="scope">
          {{scope.row.email}}
        </template>
      </el-table-column>

      <el-table-column label="身份">
        <template slot-scope="scope">
          <el-select v-if="scope.row.edit" v-model="scope.row.role">
            <el-option v-for="item in roles" :key="item.key" :label="item.value" :value="item.key"></el-option>
          </el-select>
          <span v-else>{{ scope.row.role | roleFilter }}</span>
        </template>
      </el-table-column>

      <el-table-column label="状态">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status | statusFilter('css')">
            {{ scope.row.status | statusFilter('content') }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="220px">
<!--        禁止自己删自己-->
        <template v-if="scope.row.email !== email" slot-scope="scope">
          <el-button size="mini" @click="changeStatus(scope.row)">{{scope.row.status | statusFilter('btn')}}</el-button>
          <template v-if="scope.row.edit">
            <el-button type="warning" size="mini" icon="el-icon-close" circle @click="cancelEdit(scope.row)" />
            <el-button type="success" size="mini" icon="el-icon-check" circle @click="confirmEdit(scope.row)" />
          </template>
          <el-button v-else type="primary" icon="el-icon-edit" size="mini" @click="scope.row.edit=!scope.row.edit">修改身份</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :page-sizes="[5, 10, 15, 20]"
        :page-size="listQuery.pageSize"
        :total="totalSize"
        style="margin: 40px 0"
        background
        layout="sizes, prev, pager, next">
    </el-pagination>
  </div>
</template>

<script>
import { updateInfo, getUserList, countList } from "@/api/user"

const api = {
  updateInfo,
  getUserList,
  countList
}

const roleMap = {
  SUPER_ADMIN: "超级管理员",
  ADMIN: "管理员",
  NORMAL: "普通用户"
}
const roleOptions = [
  {key: "SUPER_ADMIN", value: "超级管理员"},
  {key: "ADMIN", value: "管理员"},
  {key: "NORMAL", value: "普通用户"},
]
const statusMap = {
  ENABLE: {
    css: "success",
    btn: "禁用",
    content: "正常"
  },
  DISABLE: {
    css: "info",
    btn: "恢复",
    content: "禁用中"
  }
}
const statusOptions = [
  {key: "ENABLE", value: "正常"},
  {key: "DISABLE", value: "禁用中"},
]

export default {
  data() {
    return {
      email: this.$store.getters.email,
      listLoading: true,
      roleOptions: roleOptions,
      statusOptions: statusOptions,
      listQuery: {
        page: 1,
        pageSize: 10,
        name: undefined,
        role: undefined,
        status: undefined,
      },
      totalSize: undefined,
      userList: [],
      //在修改身份时需要用到
      roles: roleOptions
    }
  },
  filters: {
    roleFilter(role) {
      return roleMap[role]
    },
    statusFilter(status, type) {
      return statusMap[status][type]
    }
  },
  created() {
    this.getUserList()
  },
  methods: {
    getUserList() {
      this.listLoading = true
      //query处理
      if(this.listQuery.role === "") this.listQuery.role = null
      if(this.listQuery.status === "") this.listQuery.status = null
      api.getUserList(this.listQuery).then(response => {
        this.userList = response.data
        const data = this.userList
        data.map(v => {
          this.$set(v, 'edit', false)
          v.originalRole = v.role //  will be used when user click the cancel button
          return v
        })
      })
      api.countList(this.listQuery).then(response => {
        this.totalSize = response.data
      })
      this.listLoading = false
    },
    changeStatus(row) {
      let newStatus = row.status === "ENABLE" ? "DISABLE" : "ENABLE"
      api.updateInfo({ userId: row.userId, status: newStatus }).then(() => {
        this.$message({
          type: 'success',
          message: '设置成功'
        })
        this.getUserList()
      })
    },
    cancelEdit(row) {
      row.role = row.originalRole
      row.edit = false
      this.$message({
        message: '取消修改',
        type: 'warning'
      })
    },
    confirmEdit(row) {
      api.updateInfo({ userId: row.userId, role: row.role }).then(() => {
        row.edit = false
        row.originalRole = row.role
        this.$message({
          message: '修改成功',
          type: 'success'
        })
      })
    },
    handleSizeChange(val) {
      this.listQuery.pageSize = val
      this.getUserList()
    },
    handleCurrentChange(val) {
      this.listQuery.page = val
      this.getUserList()
    },
  }
}
</script>
