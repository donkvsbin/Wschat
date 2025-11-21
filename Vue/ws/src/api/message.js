import request from './request'

/**
 * 发送消息
 */
export const sendMessage = (data) => {
  return request({
    url: '/api/messages/send',
    method: 'post',
    data
  })
}

/**
 * 获取聊天记录
 */
export const getChatHistory = (friendId, page = 1, pageSize = 50) => {
  return request({
    url: `/api/messages/history/${friendId}`,
    method: 'get',
    params: { page, pageSize }
  })
}

/**
 * 标记消息为已读
 */
export const markAsRead = (friendId) => {
  return request({
    url: `/api/messages/read/${friendId}`,
    method: 'post'
  })
}

/**
 * 获取未读消息数
 */
export const getUnreadCount = (friendId) => {
  return request({
    url: `/api/messages/unread/${friendId}`,
    method: 'get'
  })
}
