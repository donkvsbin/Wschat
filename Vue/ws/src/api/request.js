import axios from 'axios'

// 创建axios实例
const service = axios.create({
  baseURL: 'http://localhost:8081', // 后端API地址
  timeout: 10000, // 请求超时时间
  withCredentials: true // 允许携带cookie（用于Session认证）
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 可以在这里添加token等请求头
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果返回的是标准ApiResponse格式（有code字段）
    if (res.hasOwnProperty('code')) {
      // 将code转换为success字段，方便前端统一判断
      return {
        success: res.code === 200,
        code: res.code,
        message: res.message,
        data: res.data
      }
    }
    
    // 如果已经有success字段，直接返回
    if (res.hasOwnProperty('success')) {
      return res
    }
    
    // 否则直接返回数据
    return res
  },
  error => {
    console.error('响应错误:', error)
    
    // 处理HTTP错误状态
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // 未登录，跳转到登录页
          localStorage.removeItem('userInfo')
          window.location.href = '/login'
          break
        case 403:
          console.error('没有权限')
          break
        case 404:
          console.error('请求的资源不存在')
          break
        case 500:
          console.error('服务器错误')
          break
        default:
          console.error('请求失败:', error.response.status)
      }
    } else if (error.request) {
      console.error('网络错误，请检查网络连接')
    }
    
    return Promise.reject(error)
  }
)

export default service
