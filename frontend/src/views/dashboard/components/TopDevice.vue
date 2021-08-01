<template>
  <el-table :data="list" style="width: 820px;">
    <el-table-column label="Top" type="index" width="100"/>
    <el-table-column label="设备名称" width="220">
      <template slot-scope="scope">
        {{ scope.row.deviceName }}
      </template>
    </el-table-column>

    <el-table-column label="设备编号" width="150">
      <template slot-scope="scope">
        {{ scope.row.deviceId }}
      </template>
    </el-table-column>

    <el-table-column label="所属实验室" width="200">
      <template slot-scope="scope">
        {{ scope.row.laboratoryName }}
      </template>
    </el-table-column>

    <el-table-column label="预约次数" width="150">
      <template slot-scope="scope">
        {{ scope.row.applyCount }}
      </template>
    </el-table-column>
  </el-table>
</template>

<script>
import { getTopDevice } from '@/api/device'

const api = {
  getTopDevice
}

export default {
  name: "TopDevice",
  data() {
    return {
      list: []
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    fetchData() {
      api.getTopDevice().then(response => {
        this.list = response.data
        console.log(this.list)
      })
    }
  }
}
</script>
