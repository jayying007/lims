<template>
  <div class="app-container">
    <div class="filter-container">
      <el-row :gutter="20">
        <el-col :span="4">
          <el-input v-model="listQuery.name" placeholder="实验室名" clearable/>
        </el-col>

        <el-col :span="2">
          <el-button type="primary" icon="el-icon-search" @click="getList">搜索</el-button>
        </el-col>

        <el-col v-if="role !== 'NORMAL'" :span="3">
          <el-button type="primary" icon="el-icon-edit" @click="showDialog('create')">添加实验室</el-button>
        </el-col>
      </el-row>
    </div>

    <el-table v-loading="listLoading" :data="laboratoryList" border>
      <el-table-column label="ID">
        <template slot-scope="scope">
          {{ scope.row.laboratoryId }}
        </template>
      </el-table-column>

      <el-table-column label="实验室名">
        <template slot-scope="scope">
          {{ scope.row.name }}
        </template>
      </el-table-column>

      <el-table-column label="实验室地点">
        <template slot-scope="scope">
          {{ scope.row.location }}
        </template>
      </el-table-column>

      <el-table-column label="管理员">
        <template slot-scope="scope">
          {{ scope.row.user.name }}
        </template>
      </el-table-column>

      <el-table-column label="联系方式">
        <template slot-scope="scope">
          {{ scope.row.contact }}
        </template>
      </el-table-column>

      <el-table-column label="操作" width="220px">
        <template slot-scope="scope">
          <el-button v-if="scope.row.user.userId === userId" type="primary" size="mini" @click="showDialog('update',scope.row)">修改</el-button>
          <el-button v-if="scope.row.user.userId === userId || role === 'SUPER_ADMIN'" type="danger" size="mini"  @click="deleteData(scope.row)">删除</el-button>
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

    <el-dialog :title="dialogTitleMap[dialogStatus]" :visible.sync="dialogFormVisible" width="30%">
      <el-form :model="template" label-position="left">
        <el-form-item label="实验室名">
          <el-input v-model="template.name" />
        </el-form-item>
        <el-form-item label="实验室地点">
          <el-input v-model="template.location" />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="template.contact" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="dialogStatus==='create'?createData():updateData()">确认</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {getLaboratoryList, countLaboratoryList, addLaboratory, updateLaboratory, deleteLaboratory} from "@/api/laboratory"
import {mapGetters} from "vuex";

const api = {
  getLaboratoryList,
  countLaboratoryList,
  addLaboratory,
  updateLaboratory,
  deleteLaboratory
}

export default {
  data() {
    return {
      listLoading: true,
      listQuery: {
        page: 1,
        pageSize: 10,
        name: undefined
      },
      totalSize: undefined,
      laboratoryList: [],

      //添加实验室窗口配置
      dialogFormVisible: false,
      dialogStatus: 'create',
      dialogTitleMap: {
        update: '修改实验室信息',
        create: '添加实验室'
      },

      template: {
        laboratoryId: undefined,
        name: '',
        location: '',
        user: {
          name: this.$store.getters.name,
        },
        contact: '',
      }
    }
  },
  created() {
    this.getList()
  },
  computed: {
    ...mapGetters([
      'role','userId'
    ])
  },
  methods: {
    //加载数据相关
    getList() {
      //获取总数
      api.countLaboratoryList(this.listQuery).then(response => {
        this.totalSize = response.data
      })
      //加载对应页数据,注意后端查询的page从0开始
      this.listLoading = true
      api.getLaboratoryList(this.listQuery).then(response => {
        this.laboratoryList = response.data
      })
      this.listLoading = false
    },
    handleSizeChange(val) {
      this.listQuery.pageSize = val
      this.getList()
    },
    handleCurrentChange(val) {
      this.listQuery.page = val
      this.getList()
    },

    showDialog(type, row) {
      if(type === "create") {
        this.template = {
          laboratoryId: undefined,
          name: '',
          location: '',
          user: {
            name: this.$store.getters.name,
          },
          contact: '',
        }
      } else {
        this.template = Object.assign({}, row) // copy obj
      }
      this.dialogStatus = type
      this.dialogFormVisible = true
    },
    createData() {
      api.addLaboratory(this.template).then(() => {
        this.getList()
        this.dialogFormVisible = false
        this.$notify({
          title: 'Success',
          message: '添加成功',
          type: 'success',
          duration: 2000
        })
      }).catch(error => {
        console.log(error)
      })
    },
    updateData() {
      api.updateLaboratory(this.template).then(() => {
        this.getList()
        this.dialogFormVisible = false
        this.$notify({
          title: 'Success',
          message: '修改成功',
          type: 'success',
          duration: 2000
        })
      }).catch(error => {
        console.log(error)
      })
    },
    deleteData(row) {
      api.deleteLaboratory(row.laboratoryId).then(() => {
        this.getList()
        this.$notify({
          title: 'Success',
          message: 'Delete Successfully',
          type: 'success',
          duration: 2000
        })
      }).catch(error => {
        console.log(error)
      })
    },
  }
}
</script>
