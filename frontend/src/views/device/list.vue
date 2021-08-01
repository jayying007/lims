<template>
  <div class="app-container">
<!--    上方搜索框-->
    <div class="filter-container">
      <el-row :gutter="20">
        <el-col :span="4">
          <el-input v-model="listQuery.name" placeholder="设备名" clearable/>
        </el-col>

        <el-col :span="3">
          <el-select v-model="listQuery.status" placeholder="状态" clearable>
            <el-option v-for="item in statusOptions" :key="item.key" :label="item.value" :value="item.key" />
          </el-select>
        </el-col>

        <el-col :span="2">
          <el-button type="primary" icon="el-icon-search" @click="getDeviceList">搜索</el-button>
        </el-col>

        <el-col v-if="role !== 'NORMAL'" :span="3">
          <el-button type="primary" icon="el-icon-edit" @click="showFormDialog('create')">添加设备</el-button>
        </el-col>
      </el-row>
    </div>

<!--    展示列表-->
    <el-table v-loading="listLoading" :data="deviceList">
      <el-table-column label="ID">
        <template slot-scope="scope">
          {{ scope.row.deviceId }}
        </template>
      </el-table-column>

      <el-table-column label="设备名">
        <template slot-scope="scope">
          {{ scope.row.name }}
        </template>
      </el-table-column>

      <el-table-column label="设备图片">
        <template slot-scope="scope">
          <el-image :src="scope.row.imageUrl" style="width: 60%"></el-image>
        </template>
      </el-table-column>

      <el-table-column label="所属实验室">
        <template slot-scope="scope" v-if="scope.row.laboratory">
            {{ scope.row.laboratory.name }}
        </template>
      </el-table-column>

      <el-table-column label="当前状态">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status | statusFilter('css')">
            {{ scope.row.status | statusFilter('content') }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="360px">
        <template slot-scope="scope">
          <el-row :gutter="30">
            <el-col :span="6">
              <router-link :to="'/device/detail/'+scope.row.deviceId">
                <el-button type="primary" size="mini">设备详情</el-button>
              </router-link>
            </el-col>
            <el-col :span="6">
              <el-button size="mini" @click="showBorrowDialog(scope.row)">申请设备</el-button>
            </el-col>
            <el-col v-if="scope.row.laboratory.user.userId === userId" :span="4">
              <el-button type="warning" size="mini" @click="showFormDialog('update', scope.row)">修改</el-button>
            </el-col>
            <el-col v-if="role === 'SUPER_ADMIN' || scope.row.laboratory.user.userId === userId" :span="4">
              <el-button type="danger"  size="mini" @click="deleteData(scope.row)">删除</el-button>
            </el-col>
          </el-row>
        </template>
      </el-table-column>
    </el-table>
<!--    分页组件-->
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
<!--    添加/修改设备信息 Dialog-->
    <el-dialog :title="dialogTitleMap[dialogStatus]" :visible.sync="formDialogVisible" width="45%">
      <el-form :model="template" label-position="left">
        <el-form-item label="设备图片" label-width="90px">
          <el-upload
              class="avatar-uploader"
              action="http://127.0.0.1:8080/upload"
              :show-file-list="false"
              :on-success="handleAvatarSuccess">
            <img v-if="template.imageUrl" :src="template.imageUrl" class="avatar" alt="">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>

        <el-form-item label="设备名称" label-width="90px">
          <el-input v-model="template.name" />
        </el-form-item>

        <el-form-item label="所属实验室" label-width="90px">
          <el-select v-model="template.laboratory.laboratoryId">
            <el-option v-for="item in laboratoryList" :key="item.laboratoryId" :label="item.name" :value="item.laboratoryId"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="设备状态" label-width="90px">
          <el-select v-model="template.status">
            <el-option v-for="item in statusOptions" :key="item.key" :label="item.value" :value="item.key" />
          </el-select>
        </el-form-item>

        <el-form-item v-for="(property, index) in template.propertyList" :key="'属性' + index" :label="'属性' + index"  label-width="90px">
          <el-input v-model="property.name" placeholder="属性" style="width: 200px"></el-input>
          <el-input v-model="property.value" placeholder="属性值" style="width: 200px;margin: 0 20px"></el-input>
          <el-button @click.prevent="removeProperty(index)">删除</el-button>
        </el-form-item>

        <el-form-item v-for="(appendix, index) in template.appendixList" :key="'附件' + index" :label="'附件' + index"  label-width="90px">
          <el-input v-model="appendix.name" placeholder="附件名" style="width: 200px;"></el-input>
          <el-button @click.prevent="removeAppendix(index)" style="margin: 0 20px">删除</el-button>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="addProperty">添加属性</el-button>
        <el-button @click="addAppendix">添加附件</el-button>
        <el-button @click="formDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="dialogStatus==='create'?createData():updateData()">确认</el-button>
      </div>
    </el-dialog>
<!--    申请借用设备 Dialog-->
    <el-dialog title="填写申请单" :visible.sync="applyDialogVisible">
      <el-form :model="applyForm" label-position="left">
        <el-form-item label="归还时间">
          <el-date-picker v-model="applyForm.promiseTimestamp" type="datetime" value-format="timestamp" />
        </el-form-item>
        <el-form-item label="申请理由">
          <el-input v-model="applyForm.borrowReason" :autosize="{ minRows: 4, maxRows: 8}" type="textarea" />
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="applyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="createApply()">提交申请</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {getDeviceList, countDeviceList, addDevice, updateDevice, deleteDevice, applyDevice} from "@/api/device";
import {getLaboratoryListByUserId} from "@/api/laboratory"
import {mapGetters} from "vuex";

const api = {
  getDeviceList,
  countDeviceList,
  addDevice,
  updateDevice,
  deleteDevice,
  applyDevice,
  getLaboratoryListByUserId
}

const statusMap = {
  AVAILABLE: {
    css: "success",
    content: "可借用"
  },
  BORROWED: {
    css: "info",
    content: "已借出"
  },
  REPAIRING: {
    css: "warning",
    content: "维修中"
  },
  DAMAGED: {
    css: "danger",
    content: "已损坏"
  }
}

const statusOptions = [
  {key: "AVAILABLE", value: "可借用"},
  {key: "BORROWED", value: "已借出"},
  {key: "REPAIRING", value: "维修中"},
  {key: "DAMAGED", value: "已损坏"}
]

export default {
  data() {
    return {
      listLoading: true,
      listQuery: {
        page: 1,
        pageSize: 5,
        name: undefined,
        status: undefined
      },
      totalSize: undefined,
      statusOptions: statusOptions,
      deviceList: [],

      formDialogVisible: false,
      dialogStatus: '',
      dialogTitleMap: {
        update: '修改设备信息',
        create: '添加设备'
      },

      applyDialogVisible: false,
      applyForm: {
        device: {
          deviceId: undefined,
        },
        borrowReason: '',
        promiseTimestamp: ''
      },

      template: {
        appendixList: [],
        deviceId: undefined,
        imageUrl: '',
        laboratory: {
          laboratoryId: undefined
        },
        name: '',
        propertyList: [],
        status: undefined,
      },
      laboratoryList: []
    }
  },
  filters: {
    statusFilter(status, type) {
      return statusMap[status][type]
    }
  },
  created() {
    this.getDeviceList()
    this.getLaboratoryListByUserId()
  },
  computed: {
    ...mapGetters([
      'role','userId'
    ])
  },
  methods: {
    getDeviceList() {
      this.listLoading = true

      api.getDeviceList(this.listQuery).then(response => {
        this.deviceList = response.data
        api.countDeviceList(this.listQuery).then(response => {
          this.totalSize = response.data
          this.listLoading = false
        })
      })
    },
    handleSizeChange(val) {
      this.listQuery.pageSize = val
      this.getDeviceList()
    },
    handleCurrentChange(val) {
      this.listQuery.page = val
      this.getDeviceList()
    },
    getLaboratoryListByUserId() {
      api.getLaboratoryListByUserId().then(response => {
        this.laboratoryList = response.data
      })
    },
    showFormDialog(type, row) {
      if(type === "create") {
        this.template = {
          appendixList: [],
          deviceId: undefined,
          imageUrl: '',
          laboratory: {
            laboratoryId: undefined,
          },
          name: '',
          propertyList: [],
          status: undefined
        }
      } else {
        this.template = Object.assign({}, row) // copy obj
      }
      this.dialogStatus = type
      this.formDialogVisible = true
    },

    handleAvatarSuccess(res) {
      this.template.imageUrl = res.data
    },
    addProperty() {
      this.template.propertyList.push({ name: '', value: '' });
    },
    removeProperty(index) {
      this.template.propertyList.splice(index, 1)
    },
    addAppendix() {
      this.template.appendixList.push({ name: '' });
    },
    removeAppendix(index) {
      this.template.appendixList.splice(index, 1)
    },

    createData() {
      api.addDevice(this.template).then(() => {
        this.formDialogVisible = false
        this.$notify({
          title: 'Success',
          message: '添加成功',
          type: 'success',
          duration: 2000
        })
        this.getDeviceList()
      })
    },
    updateData() {
      api.updateDevice(this.template).then(() => {
        this.formDialogVisible = false
        this.$notify({
          title: 'Success',
          message: '修改成功',
          type: 'success',
          duration: 2000
        })
        this.getDeviceList()
      })
    },
    deleteData(row) {
      api.deleteDevice(row.deviceId).then(() => {
        this.$notify({
          title: 'Success',
          message: '删除成功',
          type: 'success',
          duration: 2000
        })
        this.getDeviceList()
      })
    },

    showBorrowDialog(row) {
      if(row.status !== "AVAILABLE") {
        this.$message.warning({
          message: "设备当前不可借用",
          duration: 2 * 1000
        })
        return
      }
      this.applyForm.device.deviceId = row.deviceId
      this.applyDialogVisible = true
    },
    createApply() {
      if(this.applyForm.promiseTimestamp <= new Date().getTime()) {
        this.$message.error({
          message: "不能选择之前的时间",
          duration: 2 * 1000
        })
        return
      }
      api.applyDevice(this.applyForm).then(() => {
        this.applyDialogVisible = false
        this.$notify({
          title: 'Success',
          message: '添加成功',
          type: 'success',
          duration: 2000
        })
        //清空申请表
        this.applyForm = {
          device: {
            deviceId: undefined,
          },
          borrowReason: '',
          promiseTimestamp: ''
        }
        //重新获取数据
        this.getDeviceList()
      })
    }
  }
}
</script>





<style lang="scss" scoped>
.app-container {
  .roles-table {
    margin-top: 30px;
  }
  .permission-tree {
    margin-bottom: 30px;
  }
}
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  border: 1px dashed #aaaaaa;
  border-radius: 8px;
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>
