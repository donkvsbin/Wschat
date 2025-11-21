/**
 * 头像URL处理工具
 */

const BACKEND_URL = 'http://localhost:8081'

/**
 * 获取完整的头像URL
 * @param {string} avatarUrl - 头像URL（可能是相对路径或完整URL）
 * @param {string} username - 用户名（用于生成默认头像）
 * @returns {string} 完整的头像URL
 */
export const getFullAvatarUrl = (avatarUrl, username = 'default') => {
  // 如果没有头像URL，返回默认头像
  if (!avatarUrl) {
    return `https://api.dicebear.com/7.x/avataaars/svg?seed=${username}`
  }
  
  // 如果是完整URL（http://或https://），直接返回
  if (avatarUrl.startsWith('http://') || avatarUrl.startsWith('https://')) {
    return avatarUrl
  }
  
  // 否则拼接后端地址
  return `${BACKEND_URL}${avatarUrl}`
}

/**
 * 获取默认头像URL
 * @param {string} username - 用户名
 * @returns {string} 默认头像URL
 */
export const getDefaultAvatar = (username = 'default') => {
  return `https://api.dicebear.com/7.x/avataaars/svg?seed=${username}`
}
