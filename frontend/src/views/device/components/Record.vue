<template>
  <div>
    <el-timeline>
      <el-timeline-item v-for="record in recordList" v-bind:key="record.deviceRecordId"
                        :timestamp="record.recordTimestamp | parseTime('{y}-{m}-{d} {h}:{i}:{s}') " placement="top">
        <p>{{record.description}}</p>
        <div class="image-zoom-in">
          <div class="image-preview">
            <div v-show="record.recordImageUrl" class="image-preview-wrapper">
              <img v-if="record.recordImageUrl" :src="record.recordImageUrl" class="avatar" alt="">
              <div class="image-preview-action">
                <i class="el-icon-zoom-in" @click="showZoomInImage(record.recordImageUrl)"/>
              </div>
            </div>
          </div>
        </div>
      </el-timeline-item>
    </el-timeline>

    <el-dialog :visible.sync="imageVisible">
      <img width="100%" :src="imageUrl" alt="">
    </el-dialog>
  </div>
</template>

<script>
import { getRecordList } from "@/api/device";

const api = {
  getRecordList
}

export default {
  data() {
    return {
      //显示大图
      imageVisible: false,
      imageUrl: '',

      deviceId: null,
      recordList: []
    }
  },
  created() {
    this.deviceId = this.$route.path.substr(this.$route.path.lastIndexOf("/") + 1)

    api.getRecordList(this.deviceId).then(response => {
      this.recordList = response.data
    })
  },
  methods: {
    showZoomInImage(imageUrl) {
      this.imageUrl = imageUrl
      this.imageVisible = true
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
    width: 100px;
    height: 100px;
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
      line-height: 100px;
      .el-icon-zoom-in {
        font-size: 30px;
      }
    }
    &:hover {
      .image-preview-action {
        opacity: 1;
      }
    }
  }
}
</style>
