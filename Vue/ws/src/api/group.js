import request from './request'

/**
 * 创建群组
 */
export const createGroup = (data) => {
  return request({
    url: '/api/group/create',
    method: 'post',
    data
  })
}

/**
 * 加入群组
 */
export const joinGroup = (groupId) => {
  return request({
    url: `/api/group/join/${groupId}`,
    method: 'post'
  })
}

/**
 * 退出群组
 */
export const leaveGroup = (groupId) => {
  return request({
    url: `/api/group/leave/${groupId}`,
    method: 'post'
  })
}

/**
 * 获取群组列表
 */
export const getGroupList = () => {
  return request({
    url: '/api/group/list',
    method: 'get'
  })
}

/**
 * 获取群组详情
 */
export const getGroupDetail = (groupId) => {
  return request({
    url: `/api/group/${groupId}`,
    method: 'get'
  })
}

/**
 * 获取群组成员
 */
export const getGroupMembers = (groupId) => {
  return request({
    url: `/api/group/${groupId}/members`,
    method: 'get'
  })
}

/**
 * 获取群组消息历史
 */
export const getGroupMessages = (groupId, page = 1, size = 50) => {
  return request({
    url: `/api/group/${groupId}/messages`,
    method: 'get',
    params: { page, size }
  })
}

/**
 * 更新群组信息
 */
export const updateGroup = (groupId, data) => {
  return request({
    url: `/api/group/${groupId}/update`,
    method: 'put',
    data
  })
}
