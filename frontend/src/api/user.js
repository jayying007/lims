import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/token',
    method: 'POST',
    data
  })
}

export function logout(token) {
  return request({
    url: "/user/logout",
    method: "POST",
    params: {
      token: token
    }
  })
}

export function registerAccount(data) {
  return request({
    url: "/user/register",
    method: "post",
    data
  })
}

export function getInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

export function updateInfo(data) {
  return request({
    url: "/user/info",
    method: "PUT",
    data
  })
}

export function getUserList(listQuery) {
  return request({
    url: "/user/list",
    method: "GET",
    params: {
      page: listQuery.page - 1,
      pageSize: listQuery.pageSize,
      name: listQuery.name,
      role: listQuery.role,
      status: listQuery.status
    }
  })
}

export function countList(listQuery) {
  return request({
    url: "/user/list/count",
    method: "GET",
    params: {
      name: listQuery.name,
      role: listQuery.role,
      status: listQuery.status
    }
  })
}
