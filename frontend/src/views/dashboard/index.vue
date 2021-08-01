<template>
  <div class="dashboard-editor-container">
    <div class=" clearfix">
      <pan-thumb :image="avatar" style="float: left">
        Your roles:
        <span class="pan-info-roles">{{ role }}</span>
      </pan-thumb>

      <div class="info-container">
        <span class="display_name">欢迎你，{{ name }}</span>
      </div>
    </div>
    <panel-group/>
    <el-tag type="info" effect="dark">热门借用设备</el-tag>
    <top-device/>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import PanThumb from '@/components/PanThumb'
import PanelGroup from "./components/PanelGroup";
import TopDevice from "./components/TopDevice";

export default {
  name: 'DashboardEditor',
  components: {TopDevice, PanelGroup, PanThumb },
  data() {
    return {

    }
  },
  computed: {
    ...mapGetters([
      'name',
      'avatar',
      'role'
    ])
  }
}
</script>

<style lang="scss" scoped>
.dashboard-editor-container {
  background-color: rgb(240, 242, 245);
  min-height: 100vh;
  padding: 50px 60px 0;
  .pan-info-roles {
    font-size: 12px;
    font-weight: 700;
    color: #333;
    display: block;
  }
  .info-container {
    position: relative;
    margin-left: 190px;
    height: 150px;
    line-height: 200px;
    .display_name {
      font-size: 40px;
      line-height: 48px;
      color: #212121;
      position: absolute;
      top: 25px;
    }
  }
}
</style>
