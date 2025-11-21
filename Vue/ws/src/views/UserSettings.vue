<template>
  <div class="settings-container">
    <div class="settings-content">
      <h1 class="settings-title">用户设置</h1>
      
      <!-- 头像设置 -->
      <div class="settings-section">
        <h2 class="section-title">头像</h2>
        <div class="avatar-settings">
          <div class="current-avatar">
            <img :src="avatarPreview || fullAvatarUrl" alt="头像">
          </div>
          <div class="avatar-actions">
            <input 
              type="file" 
              ref="fileInput" 
              @change="handleFileSelect" 
              accept="image/*"
              style="display: none"
            >
            <button class="btn-primary" @click="$refs.fileInput.click()">
              选择图片
            </button>
            <button 
              class="btn-success" 
              @click="uploadAvatar" 
              :disabled="!selectedFile || uploading"
            >
              {{ uploading ? '上传中...' : '上传头像' }}
            </button>
          </div>
        </div>
        <p class="hint">支持 JPG、PNG 格式，文件大小不超过 2MB</p>
      </div>

      <!-- 昵称设置 -->
      <div class="settings-section">
        <h2 class="section-title">昵称</h2>
        <div class="username-settings">
          <input 
            type="text" 
            v-model="newNickname" 
            :placeholder="userInfo.nickname || userInfo.username"
            class="input-field"
          >
          <button 
            class="btn-primary" 
            @click="updateNicknameFn"
            :disabled="!newNickname || updating"
          >
            {{ updating ? '更新中...' : '更新昵称' }}
          </button>
        </div>
        <p class="hint">昵称是其他用户看到的名字，修改后立即生效</p>
      </div>

      <!-- 返回按钮 -->
      <div class="settings-footer">
        <button class="btn-secondary" @click="goBack">返回</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { uploadAvatar as uploadAvatarAPI, updateNickname as updateNicknameAPI, getUserInfo } from '../api/user'
import { getFullAvatarUrl, getDefaultAvatar } from '../utils/avatar'

const router = useRouter()

const userInfo = ref({
  username: '',
  nickname: '',
  avatarUrl: ''
})

const newNickname = ref('')
const selectedFile = ref(null)
const avatarPreview = ref('')
const uploading = ref(false)
const updating = ref(false)
const fileInput = ref(null)

const defaultAvatar = computed(() => {
  return getDefaultAvatar(userInfo.value.username)
})

// 处理头像URL，确保包含完整的后端地址
const fullAvatarUrl = computed(() => {
  return getFullAvatarUrl(userInfo.value.avatarUrl, userInfo.value.username)
})

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const res = await getUserInfo()
    if (res.success) {
      userInfo.value = res.data
      // 更新localStorage
      localStorage.setItem('userInfo', JSON.stringify(res.data))
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
}

// 选择文件
const handleFileSelect = (event) => {
  const file = event.target.files[0]
  if (!file) return

  // 检查文件大小
  if (file.size > 2 * 1024 * 1024) {
    alert('文件大小不能超过 2MB')
    return
  }

  // 检查文件类型
  if (!file.type.startsWith('image/')) {
    alert('只支持图片格式')
    return
  }

  selectedFile.value = file

  // 预览图片
  const reader = new FileReader()
  reader.onload = (e) => {
    avatarPreview.value = e.target.result
  }
  reader.readAsDataURL(file)
}

// 上传头像
const uploadAvatar = async () => {
  if (!selectedFile.value) {
    alert('请先选择图片')
    return
  }

  console.log('[上传头像] 开始上传:', selectedFile.value.name)
  uploading.value = true
  try {
    const res = await uploadAvatarAPI(selectedFile.value)
    console.log('[上传头像] 响应:', res)
    
    if (res.success) {
      alert('头像上传成功')
      userInfo.value.avatarUrl = res.data.avatarUrl
      
      // 更新localStorage
      const savedUserInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      savedUserInfo.avatarUrl = res.data.avatarUrl
      localStorage.setItem('userInfo', JSON.stringify(savedUserInfo))
      console.log('[上传头像] 更新localStorage:', savedUserInfo)
      
      // 清空选择
      selectedFile.value = null
      avatarPreview.value = ''
      if (fileInput.value) {
        fileInput.value.value = ''
      }
    } else {
      console.error('[上传头像] 失败:', res.message)
      alert('上传失败: ' + res.message)
    }
  } catch (error) {
    console.error('[上传头像] 异常:', error)
    alert('上传失败，请重试')
  } finally {
    uploading.value = false
  }
}

// 更新昵称
const updateNicknameFn = async () => {
  if (!newNickname.value.trim()) {
    alert('请输入新昵称')
    return
  }

  if (newNickname.value === userInfo.value.nickname) {
    alert('新昵称与当前昵称相同')
    return
  }

  console.log('[更新昵称] 开始更新:', newNickname.value.trim())
  updating.value = true
  try {
    const res = await updateNicknameAPI(newNickname.value.trim())
    console.log('[更新昵称] 响应:', res)
    
    if (res.success) {
      alert('昵称更新成功')
      userInfo.value.nickname = newNickname.value.trim()
      
      // 更新localStorage
      const savedUserInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      savedUserInfo.nickname = newNickname.value.trim()
      localStorage.setItem('userInfo', JSON.stringify(savedUserInfo))
      console.log('[更新昵称] 更新localStorage:', savedUserInfo)
      
      // 清空输入
      newNickname.value = ''
    } else {
      console.error('[更新昵称] 失败:', res.message)
      alert('更新失败: ' + res.message)
    }
  } catch (error) {
    console.error('[更新昵称] 异常:', error)
    alert('更新失败，请重试')
  } finally {
    updating.value = false
  }
}

// 返回
const goBack = () => {
  router.back()
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.settings-container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  min-height: 100vh;
  background: #36393f;
  padding: 40px 20px;
}

.settings-content {
  width: 100%;
  max-width: 600px;
  background: #2f3136;
  border-radius: 8px;
  padding: 30px;
}

.settings-title {
  font-size: 28px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 30px;
}

.settings-section {
  margin-bottom: 40px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
  margin-bottom: 16px;
}

/* 头像设置 */
.avatar-settings {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 12px;
}

.current-avatar {
  flex-shrink: 0;
}

.current-avatar img {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #40444b;
}

.avatar-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 用户名设置 */
.username-settings {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.input-field {
  flex: 1;
  padding: 12px 16px;
  background: #40444b;
  border: 1px solid #202225;
  border-radius: 4px;
  color: #dcddde;
  font-size: 15px;
  outline: none;
  transition: all 0.2s;
}

.input-field:focus {
  border-color: #5865f2;
  background: #383a40;
}

.input-field::placeholder {
  color: #72767d;
}

/* 按钮 */
.btn-primary,
.btn-secondary,
.btn-success {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary {
  background: #5865f2;
  color: #fff;
}

.btn-primary:hover:not(:disabled) {
  background: #4752c4;
}

.btn-success {
  background: #43b581;
  color: #fff;
}

.btn-success:hover:not(:disabled) {
  background: #3ca374;
}

.btn-secondary {
  background: #4f545c;
  color: #fff;
}

.btn-secondary:hover {
  background: #5d6269;
}

.btn-primary:disabled,
.btn-success:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 提示文字 */
.hint {
  font-size: 13px;
  color: #72767d;
  margin: 0;
}

/* 底部 */
.settings-footer {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #40444b;
}
</style>
