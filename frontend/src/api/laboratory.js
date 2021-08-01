import request from '@/utils/request'

export function getLaboratoryList(listQuery) {
    return request({
        url: '/laboratory/list',
        method: 'GET',
        params: {
            page: listQuery.page - 1,
            pageSize: listQuery.pageSize,
            name: listQuery.name
        }
    })
}

export function getLaboratoryListByUserId() {
    return request({
        url: '/laboratory/list/user',
        method: 'GET',
    })
}

export function countLaboratoryList(listQuery) {
    if(listQuery == null) {
        return request({
            url: "/laboratory/list/count",
            method: "GET"
        })
    }
    return request({
        url: "/laboratory/list/count",
        method: "GET",
        params: {
            name: listQuery.name
        }
    })
}

export function addLaboratory(data) {
    return request({
        url: "/laboratory",
        method: "POST",
        data
    })
}

export function updateLaboratory(data) {
    return request({
        url: "/laboratory",
        method: "PUT",
        data
    })
}

export function deleteLaboratory(laboratoryId) {
    return request({
        url: "/laboratory",
        method: "DELETE",
        params: {
            laboratoryId: laboratoryId
        }
    })
}
