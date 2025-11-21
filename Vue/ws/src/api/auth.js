import request from './request'

/**
 * 用户登录
 */
export const login = (data) => {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data
  })
}

/**
 * 用户注册
 */
export const register = (data) => {
  return request({
    url: '/api/auth/register',
    method: 'post',
    data
  })
}

/**
 * 用户登出
 */
export const logout = () => {
  return request({
    url: '/api/auth/logout',
    method: 'post'
  })
}

/**
 * 获取当前用户信息
 */
export const getCurrentUser = () => {
  return request({
    url: '/api/auth/current',
    method: 'get'
  })
}
