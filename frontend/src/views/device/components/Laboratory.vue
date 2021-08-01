<template>
  <el-card>
    <div slot="header" class="clearfix">
      <span>实验室信息</span>
    </div>
    <div v-if="laboratory" class="laboratory-section">
      <el-row>
        <el-col :span="6" class="laboratory-property">实验室名</el-col>
        <el-col :span="18">{{laboratory.name}}</el-col>
      </el-row>
      <el-row>
        <el-col :span="6" class="laboratory-property">实验室地点</el-col>
        <el-col :span="18">{{laboratory.location}}</el-col>
      </el-row>
      <el-row>
        <el-col :span="6" class="laboratory-property">管理员</el-col>
        <el-col :span="18">{{laboratory.user.name}}</el-col>
      </el-row>
      <el-row>
        <el-col :span="6" class="laboratory-property">联系方式</el-col>
        <el-col :span="18">{{laboratory.contact}}</el-col>
      </el-row>
    </div>
  </el-card>
</template>

<script>
import {getDevice} from "@/api/device";

const api = {
  getDevice
}

export default {
  name: "Laboratory",
  data() {
    return {
      laboratory: null
    }
  },
  created() {
    let deviceId = this.$route.path.substr(this.$route.path.lastIndexOf("/") + 1)

    api.getDevice(deviceId).then(response => {
      this.laboratory = response.data.laboratory
    })
  }
}
</script>

<style lang="scss" scoped>
.laboratory-section {
  color: #666;
  .el-row {
    margin: 8px 0;
  }
}
.laboratory-property {
  font-size: 14px;
  font-weight: bold;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
.box-center {
  margin: 0 auto;
  display: table;
}

.text-muted {
  color: #777;
}

.user-profile {
  .user-name {
    font-weight: bold;
  }

  .box-center {
    padding-top: 10px;
  }

  .user-role {
    padding-top: 10px;
    font-weight: 400;
    font-size: 14px;
  }

  .box-social {
    padding-top: 30px;

    .el-table {
      border-top: 1px solid #dfe6ec;
    }
  }

  .user-follow {
    padding-top: 20px;
  }
}

.user-bio {
  margin-top: 20px;
  color: #606266;

  span {
    padding-left: 4px;
  }

  .user-bio-section {
    font-size: 14px;
    padding: 15px 0;

    .user-bio-section-header {
      border-bottom: 1px solid #dfe6ec;
      padding-bottom: 10px;
      margin-bottom: 10px;
      font-weight: bold;
    }
  }
}
</style>
