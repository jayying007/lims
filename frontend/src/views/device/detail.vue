<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="10">
        <device/>
        <div style="margin: 10px"></div>
        <laboratory/>
      </el-col>
      <el-col :span="14">
        <el-card>
          <el-tabs v-model="activeTab">
            <el-tab-pane label="留言板" name="comment">
              <comment/>
            </el-tab-pane>
            <el-tab-pane label="使用记录" name="record">
              <record/>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import Device from "@/views/device/components/Device";
import Laboratory from "@/views/device/components/Laboratory";
import Comment from "@/views/device/components/Comment";
import Record from "@/views/device/components/Record";

export default {
  name: 'detail',
  components: { Device, Laboratory, Comment, Record },
  data() {
    return {
      activeTab: "comment"
    }
  },
  computed: {

  },
  created() {

  },
  methods: {

  }
}
</script>
