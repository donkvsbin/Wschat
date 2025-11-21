<template>
  <div class="login-container">
    <!-- 背景动画层 -->
    <div class="bg-animation">
      <div class="gradient-orb orb-1"></div>
      <div class="gradient-orb orb-2"></div>
      <div class="gradient-orb orb-3"></div>
    </div>

    <!-- 左侧品牌区域 -->
    <div class="brand-section">
      <div class="brand-content">
        <div class="logo">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 2L2 7L12 12L22 7L12 2Z" fill="url(#gradient1)"/>
            <path d="M2 17L12 22L22 17V12L12 17L2 12V17Z" fill="url(#gradient2)"/>
            <defs>
              <linearGradient id="gradient1" x1="2" y1="2" x2="22" y2="12">
                <stop offset="0%" stop-color="#6366F1"/>
                <stop offset="100%" stop-color="#8B5CF6"/>
              </linearGradient>
              <linearGradient id="gradient2" x1="2" y1="12" x2="22" y2="22">
                <stop offset="0%" stop-color="#8B5CF6"/>
                <stop offset="100%" stop-color="#EC4899"/>
              </linearGradient>
            </defs>
          </svg>
        </div>
        <h1 class="brand-title">WS Chat</h1>
        <p class="brand-subtitle">即刻开始你的实时聊天之旅</p>
        <div class="feature-list">
          <div class="feature-item">
            <span class="feature-icon">💬</span>
            <span>实时消息</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">🔒</span>
            <span>安全加密</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">⚡</span>
            <span>极速体验</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧登录区域 -->
    <div class="auth-section">
      <div class="auth-card">
        <!-- Tab切换 -->
        <div class="auth-tabs">
          <button
            :class="['tab-item', { active: isLogin }]"
            @click="switchTab(true)"
          >
            登录
          </button>
          <button
            :class="['tab-item', { active: !isLogin }]"
            @click="switchTab(false)"
          >
            注册
          </button>
          <div class="tab-indicator" :style="{ transform: isLogin ? 'translateX(0)' : 'translateX(100%)' }"></div>
        </div>

        <!-- 登录表单 -->
        <transition name="slide-fade" mode="out-in">
          <form v-if="isLogin" @submit.prevent="handleLogin" class="auth-form" key="login">
            <div class="form-group">
              <div class="input-wrapper">
                <span class="input-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                    <circle cx="12" cy="7" r="4"/>
                  </svg>
                </span>
                <input
                  v-model="loginForm.username"
                  type="text"
                  placeholder="用户名"
                  required
                  class="form-input"
                />
              </div>
            </div>

            <div class="form-group">
              <div class="input-wrapper">
                <span class="input-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                    <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                  </svg>
                </span>
                <input
                  v-model="loginForm.password"
                  :type="showPassword ? 'text' : 'password'"
                  placeholder="密码"
                  required
                  class="form-input"
                />
                <button
                  type="button"
                  class="password-toggle"
                  @click="showPassword = !showPassword"
                >
                  <svg v-if="showPassword" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                    <circle cx="12" cy="12" r="3"/>
                  </svg>
                  <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/>
                    <line x1="1" y1="1" x2="23" y2="23"/>
                  </svg>
                </button>
              </div>
            </div>

            <div v-if="errorMessage" class="error-banner">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <circle cx="12" cy="12" r="10"/>
                <line x1="12" y1="8" x2="12" y2="12"/>
                <line x1="12" y1="16" x2="12.01" y2="16"/>
              </svg>
              <span>{{ errorMessage }}</span>
            </div>

            <button type="submit" class="submit-btn" :disabled="loading">
              <span v-if="loading" class="loading-spinner"></span>
              <span>{{ loading ? '登录中...' : '登录' }}</span>
            </button>

            <div class="form-footer">
              <a href="#" class="link">忘记密码?</a>
            </div>
          </form>

          <!-- 注册表单 -->
          <form v-else @submit.prevent="handleRegister" class="auth-form" key="register">
            <div class="form-group">
              <div class="input-wrapper">
                <span class="input-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                    <circle cx="12" cy="7" r="4"/>
                  </svg>
                </span>
                <input
                  v-model="registerForm.username"
                  type="text"
                  placeholder="用户名"
                  required
                  class="form-input"
                />
              </div>
            </div>

            <div class="form-group">
              <div class="input-wrapper">
                <span class="input-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                    <polyline points="22,6 12,13 2,6"/>
                  </svg>
                </span>
                <input
                  v-model="registerForm.email"
                  type="email"
                  placeholder="邮箱（可选）"
                  class="form-input"
                />
              </div>
            </div>

            <div class="form-group">
              <div class="input-wrapper">
                <span class="input-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <path d="M5.52 19c.64-2.2 1.84-3 3.22-3h6.52c1.38 0 2.58.8 3.22 3"/>
                    <circle cx="12" cy="10" r="3"/>
                    <circle cx="12" cy="12" r="10"/>
                  </svg>
                </span>
                <input
                  v-model="registerForm.nickname"
                  type="text"
                  placeholder="昵称"
                  required
                  class="form-input"
                />
              </div>
            </div>

            <div class="form-group">
              <div class="input-wrapper">
                <span class="input-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
                    <path d="M7 11V7a5 5 0 0 1 10 0v4"/>
                  </svg>
                </span>
                <input
                  v-model="registerForm.password"
                  :type="showPassword ? 'text' : 'password'"
                  placeholder="密码"
                  required
                  class="form-input"
                />
                <button
                  type="button"
                  class="password-toggle"
                  @click="showPassword = !showPassword"
                >
                  <svg v-if="showPassword" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                    <circle cx="12" cy="12" r="3"/>
                  </svg>
                  <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor">
                    <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/>
                    <line x1="1" y1="1" x2="23" y2="23"/>
                  </svg>
                </button>
              </div>
            </div>

            <div v-if="errorMessage" class="error-banner">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <circle cx="12" cy="12" r="10"/>
                <line x1="12" y1="8" x2="12" y2="12"/>
                <line x1="12" y1="16" x2="12.01" y2="16"/>
              </svg>
              <span>{{ errorMessage }}</span>
            </div>

            <button type="submit" class="submit-btn" :disabled="loading">
              <span v-if="loading" class="loading-spinner"></span>
              <span>{{ loading ? '注册中...' : '创建账户' }}</span>
            </button>

            <div class="form-footer">
              <span class="text-muted">注册即表示同意</span>
              <a href="#" class="link">服务条款</a>
            </div>
          </form>
        </transition>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login, register } from '../api/auth'

const router = useRouter()
const isLogin = ref(true)
const loading = ref(false)
const errorMessage = ref('')
const showPassword = ref(false)

const loginForm = ref({
  username: '',
  password: ''
})

const registerForm = ref({
  username: '',
  password: '',
  nickname: '',
  email: ''
})

const switchTab = (toLogin) => {
  isLogin.value = toLogin
  errorMessage.value = ''
  showPassword.value = false
}

const handleLogin = async () => {
  try {
    loading.value = true
    errorMessage.value = ''

    const response = await login(loginForm.value)

    // 后端返回格式: {code: 200, message: "登录成功", data: {...}}
    if (response.code === 200) {
      // 保存用户信息到 localStorage
      localStorage.setItem('userInfo', JSON.stringify(response.data))

      // 跳转到好友列表页面（可以选择好友后再进入聊天）
      router.push('/home/privatemessage')
    } else {
      errorMessage.value = response.message || '登录失败'
    }
  } catch (error) {
    errorMessage.value = error.message || '登录失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  try {
    loading.value = true
    errorMessage.value = ''

    const response = await register(registerForm.value)

    // 后端返回格式: {code: 200, message: "注册成功", data: {...}}
    if (response.code === 200) {
      // 注册成功后自动切换到登录
      isLogin.value = true
      loginForm.value.username = registerForm.value.username
      loginForm.value.password = registerForm.value.password

      // 清空注册表单
      registerForm.value = {
        username: '',
        password: '',
        nickname: '',
        email: ''
      }

      // 显示成功消息
      alert('注册成功！请登录')
    } else {
      errorMessage.value = response.message || '注册失败'
    }
  } catch (error) {
    errorMessage.value = error.message || '注册失败，请稍后重试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 主容器 */
.login-container {
  min-height: 100vh;
  display: flex;
  position: relative;
  overflow: hidden;
  background: #0a0e27;
}

/* 背景动画 */
.bg-animation {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
}

.gradient-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.5;
  animation: float 20s infinite ease-in-out;
}

.orb-1 {
  width: 500px;
  height: 500px;
  background: linear-gradient(135deg, #6366F1, #8B5CF6);
  top: -10%;
  left: -10%;
  animation-delay: 0s;
}

.orb-2 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, #EC4899, #8B5CF6);
  bottom: -10%;
  right: -10%;
  animation-delay: -7s;
}

.orb-3 {
  width: 350px;
  height: 350px;
  background: linear-gradient(135deg, #06B6D4, #6366F1);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: -14s;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(30px, -50px) scale(1.1);
  }
  66% {
    transform: translate(-20px, 20px) scale(0.9);
  }
}

/* 左侧品牌区域 */
.brand-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  position: relative;
  z-index: 1;
}

.brand-content {
  max-width: 500px;
  color: white;
}

.logo {
  width: 80px;
  height: 80px;
  margin-bottom: 32px;
  animation: pulse 3s infinite ease-in-out;
}

.logo svg {
  width: 100%;
  height: 100%;
  filter: drop-shadow(0 0 20px rgba(99, 102, 241, 0.5));
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.brand-title {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 16px;
  background: linear-gradient(135deg, #fff 0%, #a8b3ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.brand-subtitle {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 48px;
  line-height: 1.6;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.08);
  transform: translateX(8px);
}

.feature-icon {
  font-size: 24px;
}

/* 右侧登录区域 */
.auth-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  position: relative;
  z-index: 1;
}

.auth-card {
  width: 100%;
  max-width: 440px;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 48px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

/* Tab切换 */
.auth-tabs {
  display: flex;
  position: relative;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  padding: 4px;
  margin-bottom: 32px;
}

.tab-item {
  flex: 1;
  padding: 12px 24px;
  border: none;
  background: transparent;
  color: rgba(255, 255, 255, 0.6);
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: color 0.3s;
  position: relative;
  z-index: 2;
}

.tab-item.active {
  color: white;
}

.tab-indicator {
  position: absolute;
  top: 4px;
  left: 4px;
  width: calc(50% - 4px);
  height: calc(100% - 8px);
  background: linear-gradient(135deg, #6366F1, #8B5CF6);
  border-radius: 8px;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 1;
}

/* 表单样式 */
.auth-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 16px;
  width: 20px;
  height: 20px;
  color: rgba(255, 255, 255, 0.4);
  pointer-events: none;
  z-index: 2;
}

.input-icon svg {
  width: 100%;
  height: 100%;
  stroke-width: 2;
}

.form-input {
  width: 100%;
  padding: 14px 16px 14px 48px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  color: white;
  font-size: 15px;
  transition: all 0.3s;
}

.form-input::placeholder {
  color: rgba(255, 255, 255, 0.3);
}

.form-input:focus {
  outline: none;
  background: rgba(255, 255, 255, 0.08);
  border-color: #6366F1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}

.password-toggle {
  position: absolute;
  right: 16px;
  width: 20px;
  height: 20px;
  background: none;
  border: none;
  color: rgba(255, 255, 255, 0.4);
  cursor: pointer;
  transition: color 0.3s;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.password-toggle:hover {
  color: rgba(255, 255, 255, 0.7);
}

.password-toggle svg {
  width: 100%;
  height: 100%;
  stroke-width: 2;
}

/* 错误提示 */
.error-banner {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid rgba(239, 68, 68, 0.3);
  border-radius: 12px;
  color: #FCA5A5;
  font-size: 14px;
}

.error-banner svg {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
  stroke-width: 2;
}

/* 提交按钮 */
.submit-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #6366F1, #8B5CF6);
  border: none;
  border-radius: 12px;
  color: white;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  box-shadow: 0 4px 16px rgba(99, 102, 241, 0.3);
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(99, 102, 241, 0.4);
}

.submit-btn:active:not(:disabled) {
  transform: translateY(0);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 表单底部 */
.form-footer {
  text-align: center;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
  margin-top: 8px;
}

.link {
  color: #8B5CF6;
  text-decoration: none;
  transition: color 0.3s;
  margin-left: 4px;
}

.link:hover {
  color: #A78BFA;
  text-decoration: underline;
}

.text-muted {
  margin-right: 4px;
}

/* 过渡动画 */
.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.slide-fade-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.slide-fade-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .brand-section {
    display: none;
  }

  .auth-section {
    flex: 1;
  }
}

@media (max-width: 640px) {
  .auth-section {
    padding: 24px;
  }

  .auth-card {
    padding: 32px 24px;
  }

  .brand-title {
    font-size: 36px;
  }
}
</style>
