import request from './request'

/**
 * 上传头像
 */
export const uploadAvatar = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  
  return request({
    url: '/api/user/upload-avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 更新昵称
 */
export const updateNickname = (nickname) => {
  return request({
    url: '/api/user/update-nickname',
    method: 'post',
    data: { nickname }
  })
}

/**
 * 获取用户信息
 */
export const getUserInfo = () => {
  return request({
    url: '/api/user/info',
    method: 'get'
  })
}
