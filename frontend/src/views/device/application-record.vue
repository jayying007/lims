<template>
  <div class="app-container">
    <div class="filter-container">
      <el-row :gutter="20">
        <el-col :span="4">
          <el-input v-model="listQuery.name" placeholder="实验室名" clearable/>
        </el-col>

        <el-col :span="3">
          <el-select v-model="listQuery.status" placeholder="状态" clearable>
            <el-option v-for="item in statusOptions" :key="item.key" :label="item.key" :value="item.value" />
          </el-select>
        </el-col>

        <el-col :span="2">
          <el-button type="primary" icon="el-icon-search" @click="getDeviceApplyList">搜索</el-button>
        </el-col>
      </el-row>
    </div>

    <el-table v-loading="listLoading" :data="deviceApplyList">
      <el-table-column label="ID">
        <template slot-scope="scope">
          {{ scope.row.deviceApplyId }}
        </template>
      </el-table-column>

      <el-table-column label="设备名">
        <template slot-scope="scope">
          {{ scope.row.device.name }}
        </template>
      </el-table-column>

      <el-table-column label="设备图片" width="200px">
        <template slot-scope="scope">
          <el-image :src="scope.row.device.imageUrl" style="width: 60%"></el-image>
        </template>
      </el-table-column>

      <el-table-column label="申请时间" width="160px">
        <template slot-scope="scope">
          {{ scope.row.applyTimestamp | parseTime('{y}-{m}-{d} {h}:{i}:{s}') }}
        </template>
      </el-table-column>

      <el-table-column label="预计归还时间" width="160px">
        <template slot-scope="scope">
          {{ scope.row.promiseTimestamp | parseTime('{y}-{m}-{d} {h}:{i}:{s}') }}
        </template>
      </el-table-column>

      <el-table-column label="申请人">
        <template slot-scope="scope">
          {{ scope.row.user.name }}
        </template>
      </el-table-column>

      <el-table-column label="申请理由">
        <template slot-scope="scope">
          {{ scope.row.borrowReason }}
        </template>
      </el-table-column>

      <el-table-column label="状态">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status | statusFilter('css')">
            {{ scope.row.status | statusFilter("content") }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="260px">
        <template slot-scope="scope">
          <el-row :gutter="20">
            <el-col :span="8"><el-button type="primary" size="mini" @click="showProgressDialog(scope.row)">查询流程</el-button></el-col>
            <el-col :span="8">
              <el-button v-if="scope.row.status === 'GRANTED' && scope.row.user.userId === userId" type="warning" size="mini" @click="showUseRecordDialog(scope.row)">上报使用情况</el-button>
            </el-col>
          </el-row>
          <div style="height: 10px"></div>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-button v-if="(scope.row.status === 'APPLIED' || scope.row.status === 'GRANTED') && scope.row.user.userId === userId" type="primary" size="mini" @click="userOperate(scope.row)">{{scope.row.status | statusFilter("user")}}</el-button>
            </el-col>
            <el-col :span="16" v-if="role === 'ADMIN' || role === 'SUPER_ADMIN'">
              <el-button v-if="scope.row.status === 'APPLIED' || scope.row.status === 'RETURNED'" size="mini" @click="adminOperate(scope.row)">{{scope.row.status | statusFilter("admin")}}</el-button>
              <el-button v-if="scope.row.status === 'APPLIED'" type="success" size="mini" @click="adminOperate(scope.row, 'GRANTED')">同意</el-button>
            </el-col>
          </el-row>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :visible.sync="progressDialogVisible" title="申请进度" width="55%">
      <el-steps :active="processInfo.step" align-center>
        <el-step title="提交申请" :description="processInfo.applyTimestamp | parseTime('{y}-{m}-{d} {h}:{i}:{s}')"></el-step>
        <el-step title="允许借用" :description="processInfo.grantTimestamp | parseTime('{y}-{m}-{d} {h}:{i}:{s}')"></el-step>
        <el-step title="提交归还" :description="processInfo.returnTimestamp | parseTime('{y}-{m}-{d} {h}:{i}:{s}')"></el-step>
        <el-step title="归还确认" :description="processInfo.finishTimestamp | parseTime('{y}-{m}-{d} {h}:{i}:{s}')"></el-step>
      </el-steps>
    </el-dialog>

    <el-dialog title="设备使用情况登记" :visible.sync="useRecordDialogVisible">
      <h4 style="margin: 0 0 5px 0">图片上传</h4>
      <el-upload
          class="avatar-uploader"
          action="http://127.0.0.1:8080/upload"
          :show-file-list="false"
          :on-success="handleAvatarSuccess">
        <img v-if="deviceRecord.recordImageUrl" :src="deviceRecord.recordImageUrl" class="avatar" alt="">
        <i v-else class="el-icon-plus avatar-uploader-icon"></i>
      </el-upload>
      <h4 style="margin: 20px 0 5px 0">备注</h4>
      <el-input v-model="deviceRecord.description" :autosize="{ minRows: 5, maxRows: 10}" type="textarea" />

      <div slot="footer" class="dialog-footer">
        <el-button @click="useRecordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="createRecord()">提交</el-button>
      </div>
    </el-dialog>

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
import { getApplyList, countApplyList, updateApply, addRecord } from "@/api/device";
import {mapGetters} from "vuex";

const api = {
  getApplyList,
  countApplyList,
  updateApply,
  addRecord
}

const statusMap = {
  APPLIED: {
    css: "primary",
    content: "已申请",
    user: "取消申请",
    admin: "拒绝"
  },
  GRANTED: {
    css: "success",
    content: "使用中",
    user: "归还设备"
  },
  RETURNED: {
    css: "warning",
    content: "已归还",
    admin: "归还确认"
  },
  FINISHED: {
    css: "success",
    content: "已完成"
  },
  DENIED: {
    css: "danger",
    content: "已拒绝"
  },
  CANCELED: {
    css: "info",
    content: "已取消"
  }
}

const statusOptions = [
  {key: "已申请", value: "APPLIED"},
  {key: "使用中", value: "GRANTED"},
  {key: "已归还", value: "RETURNED"},
  {key: "已完成", value: "FINISHED"},
  {key: "已拒绝", value: "DENIED"},
  {key: "已取消", value: "CANCELED"}
]

export default {
  data() {
    return {
      //获取申请表相关
      listLoading: true,
      listQuery: {
        page: 1,
        pageSize: 10,
        name: undefined,
        status: undefined
      },
      totalSize: undefined,
      deviceApplyList: [],
      statusOptions: statusOptions,
      //显示流程Dialog
      progressDialogVisible: false,
      processInfo: {
        step: 0,
        applyTimestamp: undefined,
        grantTimestamp: undefined,
        returnTimestamp: undefined,
        finishTimestamp: undefined
      },
      //上报设备使用记录相关
      useRecordDialogVisible: false,
      deviceRecord: {
        device: {
          deviceId: undefined
        },
        description: null,
        recordImageUrl: null
      },

    }
  },
  filters: {
    statusFilter(status, type) {
      return statusMap[status][type]
    }
  },
  computed: {
    ...mapGetters([
      'role',
      'userId'
    ])
  },
  created() {
    this.getDeviceApplyList()
  },
  methods: {
    getDeviceApplyList() {
      this.listLoading = true
      if(this.listQuery.status === "") this.listQuery.status = null
      api.getApplyList(this.listQuery).then(response => {
        this.deviceApplyList = response.data
        api.countApplyList(this.listQuery).then(response => {
          this.totalSize = response.data
        })
      })
      this.listLoading = false
    },
    handleSizeChange(val) {
      this.listQuery.pageSize = val
      this.getDeviceApplyList()
    },
    handleCurrentChange(val) {
      this.listQuery.page = val
      this.getDeviceApplyList()
    },
    showProgressDialog(row) {
      //计算流程图
      this.processInfo = {
        step: 0,
        applyTimestamp: undefined,
        grantTimestamp: undefined,
        returnTimestamp: undefined,
        finishTimestamp: undefined
      }
      if(row.applyTimestamp != null) {
        this.processInfo.step++;
        this.processInfo.applyTimestamp = row.applyTimestamp;
      }
      if(row.grantTimestamp != null) {
        this.processInfo.step++;
        this.processInfo.grantTimestamp = row.grantTimestamp;
      }
      if(row.returnTimestamp != null) {
        this.processInfo.step++;
        this.processInfo.returnTimestamp = row.returnTimestamp;
      }
      if(row.finishTimestamp != null) {
        this.processInfo.step++;
        this.processInfo.finishTimestamp = row.finishTimestamp;
      }

      this.progressDialogVisible = true
    },
    userOperate(row) {
      let deviceApply = row
      let status = undefined
      if(deviceApply.status === "APPLIED") status = "CANCELED"
      if(deviceApply.status === "GRANTED") status = "RETURNED"

      api.updateApply({deviceApplyId: deviceApply.deviceApplyId, status: status}).then(() => {
        this.getDeviceApplyList()
        this.$message.success({
          message: "操作成功",
          duration: 1000 * 2
        })
      })
    },
    adminOperate(row, status) {
      let deviceApply = row
      if(status === undefined) {
        if(deviceApply.status === "APPLIED") status = "DENIED"
        if(deviceApply.status === "RETURNED") status = "FINISHED"
      }

      api.updateApply({deviceApplyId: deviceApply.deviceApplyId, status: status}).then(() => {
        this.getDeviceApplyList()
        this.$message.success({
          message: "操作成功",
          duration: 1000 * 2
        })
      })
    },
    showUseRecordDialog(row) {
      this.deviceRecord = {
        device: {
          deviceId: row.device.deviceId
        },
        description: null,
        recordImageUrl: null
      }
      this.useRecordDialogVisible = true
    },
    handleAvatarSuccess(res) {
      this.deviceRecord.recordImageUrl = res.data
    },
    createRecord() {
      api.addRecord(this.deviceRecord).then(() => {
        this.useRecordDialogVisible = false
        this.$message.success({
          message: "上报记录成功",
          duration: 1000 * 2
        })
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
