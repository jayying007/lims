import request from '@/utils/request'

export function getTopDevice() {
    return request({
        url: "/device/top",
        method: "GET"
    })
}

export function getDevice(deviceId) {
    return request({
        url: "/device",
        method: "GET",
        params: {
            deviceId: deviceId
        }
    })
}

export function getDeviceList(listQuery) {
    return request({
        url: '/device/list',
        method: 'GET',
        params: {
            page: listQuery.page - 1,
            pageSize: listQuery.pageSize,
            name: listQuery.name,
            status: listQuery.status
        }
    })
}

export function countDeviceList(listQuery) {
    if(listQuery == null) {
        return request({
            url: '/device/list/count',
            method: 'GET'
        })
    }
    return request({
        url: '/device/list/count',
        method: 'GET',
        params: {
            name: listQuery.name,
            status: listQuery.status
        }
    })
}

export function addDevice(data) {
    return request({
        url: "/device",
        method: "POST",
        data
    })
}

export function updateDevice(data) {
    return request({
        url: "/device",
        method: "PUT",
        data
    })
}

export function deleteDevice(deviceId) {
    return request({
        url: "/device",
        method: "DELETE",
        params: {
            deviceId: deviceId
        }
    })
}

export function applyDevice(data) {
    return request({
        url: "/device/apply",
        method: "POST",
        data
    })
}

export function getApplyList(listQuery) {
    return request({
        url: "/device/apply",
        method: "GET",
        params: {
            page: listQuery.page - 1,
            pageSize: listQuery.pageSize,
            name: listQuery.name,
            status: listQuery.status
        }
    })
}

export function countApplyList(listQuery) {
    return request({
        url: "/device/apply/list",
        method: "GET",
        params: {
            page: listQuery.page - 1,
            pageSize: listQuery.pageSize,
            name: listQuery.name,
            status: listQuery.status
        }
    })
}

export function updateApply(data) {
    return request({
        url: "/device/apply",
        method: "PUT",
        params: {
            deviceApplyId: data.deviceApplyId,
            status: data.status
        }
    })
}

export function getRecordList(deviceId) {
    return request({
        url: "/device/record",
        method: "GET",
        params: {
            deviceId: deviceId
        }
    })
}

export function addRecord(data) {
    return request({
        url: "/device/record",
        method: "POST",
        data
    })
}

export function getCommentList(deviceId) {
    return request({
        url: "/device/comment",
        method: "GET",
        params: {
            deviceId: deviceId
        }
    })
}

export function addComment(data) {
    return request({
        url: "/device/comment",
        method: "POST",
        data
    })
}
