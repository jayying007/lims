import request from '@/utils/request'

export function sendVerifyCode(email) {
    return request({
        url: '/email/verifyCode',
        method: 'GET',
        params: {email}
    })
}
