<template>
  <el-card>
    <div slot="header" class="clearfix">
      <span>设备详情</span>
    </div>

    <div class="user-profile" v-if="device">
      <div class="box-center">
        <div class="image-zoom-in">
          <div class="image-preview">
            <div v-show="device.imageUrl" class="image-preview-wrapper">
              <img v-if="device.imageUrl" :src="device.imageUrl" class="avatar" alt="">
              <div class="image-preview-action">
                <i class="el-icon-zoom-in" @click="imageVisible = true"/>
              </div>
            </div>
          </div>
        </div>
      </div>
      <el-dialog :visible.sync="imageVisible">
        <img width="100%" :src="device.imageUrl" alt="">
      </el-dialog>
      <div class="box-center">
        <div class="user-name text-center">{{device.name}}</div>
        <div class="user-role text-center text-muted">
          <el-tag>
            {{device.status | statusFilter("content") }}
          </el-tag>
        </div>
      </div>
    </div>

    <div class="user-bio" v-if="device">
      <div class="user-education user-bio-section">
        <div class="user-bio-section-header"><svg-icon icon-class="education" /><span>属性</span></div>
        <div class="user-bio-section-body">
          <el-table :data="device.propertyList" style="width: 100%;margin-top:30px;" border>
            <el-table-column label="属性名" width="160px">
              <template slot-scope="scope">
                {{ scope.row.name }}
              </template>
            </el-table-column>
            <el-table-column label="属性值">
              <template slot-scope="scope">
                {{ scope.row.value }}
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <div class="user-skills user-bio-section">
        <div class="user-bio-section-header"><svg-icon icon-class="skill" /><span>附件</span></div>
        <div class="user-bio-section-body">
          <el-table :data="device.appendixList" style="width: 100%;margin-top:30px;" border>
            <el-table-column label="附件名">
              <template slot-scope="scope">
                {{ scope.row.name }}
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script>
import {getDevice} from "@/api/device";

const api = {
  getDevice
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

export default {
  name: "Device",
  data() {
    return {
      //图片放大
      imageVisible: false,
      device: null
    }
  },
  filters: {
    statusFilter(status, type) {
      return statusMap[status][type]
    }
  },
  created() {
    let deviceId = this.$route.path.substr(this.$route.path.lastIndexOf("/") + 1)

    api.getDevice(deviceId).then(response => {
      this.device = response.data
    })
  },
}
</script>

<style lang="scss" scoped>
@import "~@/styles/mixin.scss";
.image-zoom-in {
  width: 100%;
  position: relative;
  @include clearfix;
  .image-uploader {
    width: 35%;
    float: left;
  }
  .image-preview {
    width: 200px;
    height: 200px;
    position: relative;
    border: 1px dashed #d9d9d9;
    float: left;
    .image-preview-wrapper {
      position: relative;
      width: 100%;
      height: 100%;
      img {
        width: 100%;
        height: 100%;
      }
    }
    .image-preview-action {
      position: absolute;
      width: 100%;
      height: 100%;
      left: 0;
      top: 0;
      text-align: center;
      color: #fff;
      opacity: 0;
      font-size: 20px;
      background-color: rgba(0, 0, 0, .5);
      transition: opacity .3s;
      cursor: pointer;
      line-height: 200px;
      .el-icon-zoom-in {
        font-size: 40px;
      }
    }
    &:hover {
      .image-preview-action {
        opacity: 1;
      }
    }
  }
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
