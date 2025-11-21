import request from './request'

/**
 * 好友相关API
 */

// 搜索用户
export function searchUsers(keyword) {
  return request({
    url: '/api/friend/search',
    method: 'get',
    params: { keyword }
  })
}

// 发送好友请求
export function sendFriendRequest(data) {
  return request({
    url: '/api/friend/request',
    method: 'post',
    data
  })
}

// 处理好友请求（接受/拒绝）
export function handleFriendRequest(data) {
  return request({
    url: '/api/friend/request/handle',
    method: 'post',
    data
  })
}

// 获取收到的好友请求
export function getReceivedRequests() {
  return request({
    url: '/api/friend/request/received',
    method: 'get'
  })
}

// 获取发送的好友请求
export function getSentRequests() {
  return request({
    url: '/api/friend/request/sent',
    method: 'get'
  })
}

// 获取好友列表
export function getFriendList() {
  return request({
    url: '/api/friend/list',
    method: 'get'
  })
}

// 删除好友
export function deleteFriend(friendId) {
  return request({
    url: `/api/friend/${friendId}`,
    method: 'delete'
  })
}

// 检查是否是好友
export function checkFriend(friendId) {
  return request({
    url: `/api/friend/check/${friendId}`,
    method: 'get'
  })
}
