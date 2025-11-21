<template>
  <div class="message-container">
    <!-- 聊天头部 -->
    <div class="chat-header">
      <div class="chat-friend-info">
        <div class="chat-friend-avatar" :class="{ 'editable': isGroupChat && groupInfo }" @click="openGroupSettings">
          <img :src="getAvatarUrl(selectedFriendInfo?.username, selectedFriendInfo?.avatarUrl)" :alt="selectedFriendInfo?.nickname">
          <span class="status-indicator" :class="selectedFriendInfo?.isOnline ? 'online' : 'offline'" v-if="!isGroupChat"></span>
          <!-- 群聊编辑图标 -->
          <div class="edit-overlay" v-if="isGroupChat && groupInfo">
            <svg viewBox="0 0 24 24" fill="currentColor" width="20" height="20">
              <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/>
            </svg>
          </div>
        </div>
        <div class="chat-friend-details">
          <div class="chat-friend-name">{{ selectedFriendInfo?.nickname || selectedFriendInfo?.username }}</div>
          <div class="chat-friend-tag">@{{ selectedFriendInfo?.username }}</div>
        </div>
      </div>
      <div class="chat-actions">
        <button class="chat-action-btn" title="语音通话">
          <svg viewBox="0 0 24 24" fill="currentColor">
            <path d="M20.01 15.38c-1.23 0-2.42-.2-3.53-.56-.35-.12-.74-.03-1.01.24l-1.57 1.97c-2.83-1.35-5.48-3.9-6.89-6.83l1.95-1.66c.27-.28.35-.67.24-1.02-.37-1.11-.56-2.3-.56-3.53 0-.54-.45-.99-.99-.99H4.19C3.65 3 3 3.24 3 3.99 3 13.28 10.73 21 20.01 21c.71 0 .99-.63.99-1.18v-3.45c0-.54-.45-.99-.99-.99z"/>
          </svg>
        </button>
        <button class="chat-action-btn" title="视频通话">
          <svg viewBox="0 0 24 24" fill="currentColor">
            <path d="M17 10.5V7c0-.55-.45-1-1-1H4c-.55 0-1 .45-1 1v10c0 .55.45 1 1 1h12c.55 0 1-.45 1-1v-3.5l4 4v-11l-4 4z"/>
          </svg>
        </button>
        <button class="chat-action-btn" title="更多选项">
          <svg viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 8c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm0 2c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm0 6c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z"/>
          </svg>
        </button>
      </div>
    </div>

    <!-- 消息列表 -->
    <div class="messages-container" ref="messagesContainer">
      <div class="messages-wrapper">
        <!-- 加载中 -->
        <div v-if="isLoading" class="loading-message">
          加载中...
        </div>

        <!-- 消息列表 -->
        <div v-else-if="messages.length > 0">
          <div
            v-for="(message, index) in messages"
            :key="message.id"
            class="message-item"
            :class="{ 'message-group-continue': shouldHideAvatar(index) }"
          >
            <!-- 只在新消息组的第一条显示头像 -->
            <div class="message-avatar" v-if="!shouldHideAvatar(index)">
              <img 
                :src="getMessageAvatar(message)" 
                :alt="getMessageAuthorName(message)"
              >
            </div>
            <!-- 占位符，保持对齐 -->
            <div class="message-avatar-placeholder" v-else></div>
            
            <div class="message-content-wrapper">
              <!-- 只在新消息组的第一条显示用户名和时间 -->
              <div class="message-header" v-if="!shouldHideAvatar(index)">
                <span class="message-author">
                  {{ getMessageAuthorName(message) }}
                </span>
                <span class="message-time">{{ formatTime(message.createdAt) }}</span>
              </div>
              <!-- 消息内容 -->
              <div class="message-content" :class="{ 'message-content-compact': shouldHideAvatar(index) }">
                <span class="message-timestamp" v-if="shouldHideAvatar(index)">{{ formatTimeCompact(message.createdAt) }}</span>
                {{ message.content }}
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else class="empty-message">
          <p>暂无消息，快来聊天吧！</p>
        </div>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="message-input-area">
      <div class="input-toolbar">
        <button class="toolbar-btn" title="添加附件">
          <svg viewBox="0 0 24 24" fill="currentColor">
            <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
          </svg>
        </button>
        <button class="toolbar-btn" title="表情">
          <svg viewBox="0 0 24 24" fill="currentColor">
            <path d="M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm3.5-9c.83 0 1.5-.67 1.5-1.5S16.33 8 15.5 8 14 8.67 14 9.5s.67 1.5 1.5 1.5zm-7 0c.83 0 1.5-.67 1.5-1.5S9.33 8 8.5 8 7 8.67 7 9.5 7.67 11 8.5 11zm3.5 6.5c2.33 0 4.31-1.46 5.11-3.5H6.89c.8 2.04 2.78 3.5 5.11 3.5z"/>
          </svg>
        </button>
      </div>
      <div class="input-wrapper">
        <input 
          type="text" 
          v-model="messageInput" 
          :placeholder="isGroupChat ? `在 ${selectedFriendInfo?.nickname || 'group'} 中发消息` : `给 @${selectedFriendInfo?.username || 'friend'} 发消息`"
          @keyup.enter="sendMessage"
          class="message-input"
        />
      </div>
    </div>
  </div>

  <!-- 群聊设置弹窗 -->
  <div class="dialog-overlay" v-if="showGroupSettingsDialog" @click="showGroupSettingsDialog = false">
    <div class="dialog" @click.stop>
      <div class="dialog-header">
        <h2>群聊设置</h2>
        <button class="close-btn" @click="showGroupSettingsDialog = false">
          <svg viewBox="0 0 24 24" fill="currentColor">
            <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
          </svg>
        </button>
      </div>
      <div class="dialog-body">
        <div class="group-info-section">
          <div class="group-avatar-large">
            <img :src="getAvatarUrl(groupInfo?.id, null)" :alt="groupInfo?.name">
          </div>
          <div class="group-id">群号：{{ groupInfo?.id }}</div>
        </div>
        
        <div class="form-group">
          <label>群聊名称</label>
          <input
            type="text"
            v-model="editGroupName"
            placeholder="请输入群聊名称"
            maxlength="50"
          >
        </div>
        <div class="form-group">
          <label>群聊描述</label>
          <textarea
            v-model="editGroupDescription"
            placeholder="请输入群聊描述（可选）"
            rows="3"
            maxlength="200"
          ></textarea>
        </div>
        <div class="dialog-actions">
          <button class="btn-secondary" @click="showGroupSettingsDialog = false">取消</button>
          <button class="btn-primary" @click="saveGroupSettings" :disabled="!editGroupName.trim()">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getChatHistory, markAsRead } from '../api/message'
import { getGroupMessages, getGroupDetail, updateGroup } from '../api/group'
import { getFullAvatarUrl } from '../utils/avatar'

// 接收props
const props = defineProps({
  friendInfo: {
    type: Object,
    default: null
  },
  websocket: {
    type: Object,
    default: null
  }
})

const route = useRoute()
const router = useRouter()
const userInfo = ref(null)
const messageInput = ref('')
const messagesContainer = ref(null)
const messages = ref([])
const isLoading = ref(false)
const isGroupChat = ref(false)
const groupInfo = ref(null)
const showGroupSettingsDialog = ref(false)
const editGroupName = ref('')
const editGroupDescription = ref('')

// 使用父组件传递的WebSocket
const ws = computed(() => props.websocket)

// 初始化用户信息
const initUserInfo = () => {
  try {
    const savedUserInfo = localStorage.getItem('userInfo')
    if (savedUserInfo) {
      userInfo.value = JSON.parse(savedUserInfo)
      if (!userInfo.value || !userInfo.value.id) {
        console.error('用户信息不完整，跳转到登录页')
        router.push('/login')
        return false
      }
      return true
    } else {
      console.error('未找到用户信息，跳转到登录页')
      router.push('/login')
      return false
    }
  } catch (error) {
    console.error('解析用户信息失败:', error)
    router.push('/login')
    return false
  }
}

// 获取路由参数中的好友ID或群组ID
const friendId = computed(() => {
  const id = route.params.friendId
  // 检测是否是群聊
  if (id && id.startsWith('group_')) {
    isGroupChat.value = true
    return id.substring(6) // 移除 'group_' 前缀，获取真实群组ID
  } else {
    isGroupChat.value = false
    return id
  }
})

// 获取好友/群组信息（优先使用props，否则尝试从好友列表获取，最后使用默认值）
const selectedFriendInfo = computed(() => {
  // 如果是群聊，返回群组信息
  if (isGroupChat.value && groupInfo.value) {
    return {
      username: groupInfo.value.id,
      nickname: groupInfo.value.name,
      isOnline: true, // 群组没有在线状态
      avatarUrl: null
    }
  }
  
  // 优先使用父组件传递的好友信息
  if (props.friendInfo) {
    console.log('使用props传递的好友信息:', props.friendInfo)
    return props.friendInfo
  }
  
  // 如果没有props，使用默认值
  console.log('使用默认好友信息')
  return {
    username: 'friend',
    nickname: '好友',
    isOnline: false
  }
})

const userAvatar = computed(() => {
  return getFullAvatarUrl(userInfo.value?.avatarUrl, userInfo.value?.username)
})

const getAvatarUrl = (username, avatarUrl = null) => {
  // 如果是群聊，使用群组头像
  if (isGroupChat.value && groupInfo.value) {
    return `https://api.dicebear.com/7.x/initials/svg?seed=${groupInfo.value.id}&backgroundColor=5865f2`
  }
  return getFullAvatarUrl(avatarUrl, username)
}

// 初始化WebSocket消息监听
const messageHandler = ref(null)

const initWebSocketListener = () => {
  console.log('[聊天窗口] 初始化WebSocket监听器')
  console.log('[聊天窗口] ws.value:', ws.value)
  console.log('[聊天窗口] props.websocket:', props.websocket)
  
  if (!ws.value) {
    console.error('[聊天窗口] WebSocket未初始化')
    return
  }

  // 定义消息处理函数
  messageHandler.value = async (event) => {
    try {
      const data = JSON.parse(event.data)
      console.log('[聊天窗口] 收到WebSocket消息:', data)
      console.log('当前聊天的ID:', friendId.value, '类型:', typeof friendId.value)
      console.log('当前用户ID:', userInfo.value.id, '类型:', typeof userInfo.value.id)
      console.log('是否群聊:', isGroupChat.value)

      if (data.type === 'GROUP' && isGroupChat.value) {
        // 群聊消息
        console.log('收到群聊消息，群组ID:', data.groupId, '当前群组ID:', friendId.value)
        if (data.groupId === friendId.value) {
          console.log('[聊天窗口] 添加群聊消息到列表')
          messages.value.push({
            id: data.messageId || Date.now(),
            fromUserId: data.senderId,
            groupId: data.groupId,
            content: data.content,
            createdAt: new Date(data.timestamp).toISOString(),
            isRead: 0,
            // 保存发送者信息
            senderUsername: data.senderUsername,
            senderNickname: data.senderNickname,
            senderAvatarUrl: data.senderAvatarUrl
          })
          await nextTick()
          setTimeout(() => {
            scrollToBottom()
          }, 50)
        }
      } else if (data.type === 'PRIVATE' && !isGroupChat.value) {
        // 私聊消息 - 只要是与当前好友的对话，就显示
        // 将friendId转换为Number进行比较
        const currentFriendId = Number(friendId.value)
        const currentUserId = Number(userInfo.value.id)
        
        const isFromCurrentFriend = data.fromUserId === currentFriendId
        const isToCurrentFriend = data.toUserId === currentFriendId
        const isMyMessage = data.fromUserId === currentUserId
        const isToMe = data.toUserId === currentUserId
        
        console.log('isFromCurrentFriend:', isFromCurrentFriend, '(', data.fromUserId, '===', currentFriendId, ')')
        console.log('isToCurrentFriend:', isToCurrentFriend, '(', data.toUserId, '===', currentFriendId, ')')
        console.log('isMyMessage:', isMyMessage, '(', data.fromUserId, '===', currentUserId, ')')
        console.log('isToMe:', isToMe, '(', data.toUserId, '===', currentUserId, ')')
        
        // 如果是我发给当前好友的，或者是当前好友发给我的
        if ((isMyMessage && isToCurrentFriend) || (isFromCurrentFriend && isToMe)) {
          console.log('[聊天窗口] 添加消息到列表')
          messages.value.push({
            id: Date.now(),
            fromUserId: data.fromUserId,
            toUserId: data.toUserId,
            content: data.content,
            createdAt: new Date(data.timestamp).toISOString(),
            isRead: 0
          })
          // 等待DOM更新后滚动到底部
          await nextTick()
          setTimeout(() => {
            scrollToBottom()
          }, 50)
        } else {
          console.log('[聊天窗口] 消息不属于当前聊天，忽略')
        }
      } else if (data.type === 'USER_STATUS') {
        // 用户状态变化
        console.log('[聊天窗口] 用户状态变化:', data.userId, data.isOnline)
        if (data.userId == friendId.value) {
          if (selectedFriendInfo.value) {
            selectedFriendInfo.value.isOnline = data.isOnline
          }
        }
      } else if (data.type === 'CONFIRM') {
        console.log('[聊天窗口] 消息确认:', data.message)
      }
    } catch (error) {
      console.error('[聊天窗口] 解析WebSocket消息失败:', error)
    }
  }

  // 添加监听器
  console.log('[聊天窗口] 添加message事件监听器')
  ws.value.addEventListener('message', messageHandler.value)
}

// 加载聊天记录
const loadChatHistory = async () => {
  if (!friendId.value) return
  
  isLoading.value = true
  try {
    let res
    if (isGroupChat.value) {
      // 加载群聊消息
      console.log('加载群聊消息，群组ID:', friendId.value)
      res = await getGroupMessages(friendId.value, 1, 50)
      // 同时加载群组信息
      const groupRes = await getGroupDetail(friendId.value)
      if (groupRes.success) {
        groupInfo.value = groupRes.data
      }
    } else {
      // 加载私聊消息
      res = await getChatHistory(friendId.value, 1, 50)
    }
    
    if (res.success) {
      // 群聊消息需要将 senderId 映射为 fromUserId，以便与私聊消息保持一致
      if (isGroupChat.value) {
        messages.value = (res.data || []).map(msg => ({
          ...msg,
          fromUserId: msg.senderId // 将 senderId 映射为 fromUserId
        }))
      } else {
        messages.value = res.data || []
      }
      
      console.log('加载的消息:', messages.value)
      
      // 等待DOM更新完成后滚动到底部
      await nextTick()
      setTimeout(() => {
        scrollToBottom()
      }, 100)
      
      // 私聊标记为已读
      if (!isGroupChat.value) {
        await markAsRead(friendId.value)
      }
    }
  } catch (error) {
    console.error('加载聊天记录失败:', error)
  } finally {
    isLoading.value = false
  }
}

// 发送消息
const sendMessage = async () => {
  if (!messageInput.value.trim()) return
  
  const content = messageInput.value.trim()
  
  console.log('[聊天窗口] 准备发送消息')
  console.log('[聊天窗口] ws.value:', ws.value)
  console.log('[聊天窗口] readyState:', ws.value?.readyState)
  console.log('[聊天窗口] 是否群聊:', isGroupChat.value)
  
  // 通过WebSocket发送
  if (ws.value && ws.value.readyState === WebSocket.OPEN) {
    let message
    if (isGroupChat.value) {
      // 群聊消息
      message = {
        type: 'GROUP',
        groupId: friendId.value,
        content: content
      }
      console.log('[聊天窗口] 发送群聊消息:', message)
    } else {
      // 私聊消息
      message = {
        type: 'PRIVATE',
        toUserId: Number(friendId.value),
        content: content
      }
      console.log('[聊天窗口] 发送私聊消息:', message)
    }
    
    ws.value.send(JSON.stringify(message))
    
    // 添加到本地消息列表（群聊消息会通过WebSocket广播回来，这里不重复添加）
    if (!isGroupChat.value) {
      messages.value.push({
        id: Date.now(),
        fromUserId: userInfo.value.id,
        toUserId: Number(friendId.value),
        content: content,
        createdAt: new Date().toISOString(),
        isRead: 0
      })
    }
    
    messageInput.value = ''
    // 等待DOM更新后滚动到底部
    await nextTick()
    setTimeout(() => {
      scrollToBottom()
    }, 50)
  } else {
    console.error('[聊天窗口] WebSocket连接未就绪')
    alert('WebSocket连接未建立')
  }
}

// 滚动到底部
const scrollToBottom = () => {
  if (messagesContainer.value) {
    // 使用scrollTop确保滚动到最底部
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    
    // 备用方案：如果上面不起作用，使用scrollIntoView
    const lastMessage = messagesContainer.value.querySelector('.message-item:last-child')
    if (lastMessage) {
      lastMessage.scrollIntoView({ behavior: 'auto', block: 'end' })
    }
  }
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

// 格式化紧凑时间（只显示时:分）
const formatTimeCompact = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${hours}:${minutes}`
}

// 获取消息发送者的头像
const getMessageAvatar = (message) => {
  if (isGroupChat.value) {
    // 群聊中，显示每个发送者的头像
    if (message.fromUserId == userInfo.value.id) {
      // 自己的消息
      return userAvatar.value
    } else {
      // 其他人的消息，使用消息中的发送者信息
      return getFullAvatarUrl(message.senderAvatarUrl, message.senderUsername)
    }
  } else {
    // 私聊中，区分自己和好友
    if (message.fromUserId == userInfo.value.id) {
      return userAvatar.value
    } else {
      return getAvatarUrl(selectedFriendInfo.value?.username, selectedFriendInfo.value?.avatarUrl)
    }
  }
}

// 获取消息发送者的昵称
const getMessageAuthorName = (message) => {
  if (isGroupChat.value) {
    // 群聊中，显示每个发送者的昵称
    if (message.fromUserId == userInfo.value.id) {
      return userInfo.value?.nickname || userInfo.value?.username
    } else {
      // 使用消息中的发送者信息
      return message.senderNickname || message.senderUsername || '用户'
    }
  } else {
    // 私聊中
    if (message.fromUserId == userInfo.value.id) {
      return userInfo.value?.nickname || userInfo.value?.username
    } else {
      return selectedFriendInfo.value?.nickname || selectedFriendInfo.value?.username
    }
  }
}

// 打开群聊设置
const openGroupSettings = () => {
  if (!isGroupChat.value || !groupInfo.value) return
  
  editGroupName.value = groupInfo.value.name
  editGroupDescription.value = groupInfo.value.description || ''
  showGroupSettingsDialog.value = true
}

// 保存群聊设置
const saveGroupSettings = async () => {
  if (!editGroupName.value.trim()) {
    alert('请输入群聊名称')
    return
  }
  
  try {
    const res = await updateGroup(friendId.value, {
      name: editGroupName.value.trim(),
      description: editGroupDescription.value.trim()
    })
    
    if (res.success) {
      alert('群聊设置保存成功！')
      showGroupSettingsDialog.value = false
      
      // 重新加载群组信息
      const groupRes = await getGroupDetail(friendId.value)
      if (groupRes.success) {
        groupInfo.value = groupRes.data
      }
    } else {
      alert('保存失败: ' + res.message)
    }
  } catch (error) {
    console.error('保存群聊设置失败:', error)
    alert('保存失败，请重试')
  }
}

// 判断是否应该隐藏头像（Discord风格消息分组）
const shouldHideAvatar = (index) => {
  if (index === 0) return false // 第一条消息总是显示头像
  
  const currentMsg = messages.value[index]
  const prevMsg = messages.value[index - 1]
  
  // 如果发送者不同，显示头像
  if (currentMsg.fromUserId !== prevMsg.fromUserId) {
    return false
  }
  
  // 如果发送者相同，检查时间间隔
  const currentTime = new Date(currentMsg.createdAt).getTime()
  const prevTime = new Date(prevMsg.createdAt).getTime()
  const timeDiff = currentTime - prevTime
  
  // 如果时间间隔超过5分钟，显示头像（开始新的消息组）
  if (timeDiff > 5 * 60 * 1000) {
    return false
  }
  
  // 否则隐藏头像（继续同一消息组）
  return true
}

// 监听好友ID变化
watch(friendId, (newId, oldId) => {
  console.log('好友ID变化:', oldId, '->', newId)
  if (newId && newId !== oldId) {
    // 清空当前消息列表
    messages.value = []
    // 加载新好友的聊天记录
    loadChatHistory()
  }
})

// 监听WebSocket变化
watch(() => props.websocket, (newWs, oldWs) => {
  console.log('[聊天窗口] WebSocket props变化')
  console.log('[聊天窗口] 旧WebSocket:', oldWs)
  console.log('[聊天窗口] 新WebSocket:', newWs)
  console.log('[聊天窗口] readyState:', newWs?.readyState)
  
  if (newWs) {
    if (newWs.readyState === WebSocket.OPEN) {
      console.log('[聊天窗口] WebSocket已就绪，立即初始化监听器')
      // 移除旧的监听器
      if (oldWs && messageHandler.value) {
        oldWs.removeEventListener('message', messageHandler.value)
      }
      // 初始化监听器
      initWebSocketListener()
    } else {
      console.log('[聊天窗口] WebSocket还未就绪，等待onopen事件')
      // 监听WebSocket的onopen事件
      const openHandler = () => {
        console.log('[聊天窗口] WebSocket onopen事件触发')
        initWebSocketListener()
        newWs.removeEventListener('open', openHandler)
      }
      newWs.addEventListener('open', openHandler)
    }
  }
}, { immediate: true })

onMounted(() => {
  // 先初始化用户信息
  if (!initUserInfo()) {
    return
  }
  
  // 不在这里初始化监听器，改为通过watch监听WebSocket变化
  console.log('[聊天窗口] onMounted - 等待WebSocket就绪')
  
  // 加载聊天记录
  if (friendId.value) {
    loadChatHistory()
  }
})

// onUnmounted - 移除监听器
onUnmounted(() => {
  console.log('[聊天窗口] 组件卸载，移除监听器')
  if (ws.value && messageHandler.value) {
    ws.value.removeEventListener('message', messageHandler.value)
  }
})
</script>

<style scoped>
.message-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
  background: #36393f;
  overflow: hidden;
}

/* 聊天头部 */
.chat-header {
  height: 60px;
  background: #36393f;
  border-bottom: 1px solid #202225;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  flex-shrink: 0;
}

.chat-friend-info {
  display: flex;
  align-items: center;
  gap: 14px;
}

.chat-friend-avatar {
  position: relative;
}

.chat-friend-avatar.editable {
  cursor: pointer;
}

.chat-friend-avatar img {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  transition: opacity 0.2s;
}

.chat-friend-avatar.editable:hover img {
  opacity: 0.6;
}

/* 编辑覆盖层 */
.edit-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s;
  pointer-events: none;
}

.chat-friend-avatar.editable:hover .edit-overlay {
  opacity: 1;
}

.edit-overlay svg {
  color: #fff;
}

.status-indicator {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 3px solid #36393f;
}

.status-indicator.online {
  background: #43b581;
}

.status-indicator.offline {
  background: #747f8d;
}

.chat-friend-details {
  display: flex;
  flex-direction: column;
}

.chat-friend-name {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
}

.chat-friend-tag {
  font-size: 13px;
  color: #b9bbbe;
}

.chat-actions {
  display: flex;
  gap: 12px;
}

.chat-action-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: transparent;
  color: #b9bbbe;
  cursor: pointer;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.chat-action-btn:hover {
  background: #3c3f45;
  color: #dcddde;
}

.chat-action-btn svg {
  width: 20px;
  height: 20px;
}

/* 消息列表 */
.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #36393f;
  scrollbar-width: thin;
  scrollbar-color: #202225 transparent;
  min-height: 0;
}

.messages-container::-webkit-scrollbar {
  width: 8px;
}

.messages-container::-webkit-scrollbar-track {
  background: transparent;
}

.messages-container::-webkit-scrollbar-thumb {
  background: #202225;
  border-radius: 4px;
}

.messages-wrapper {
  width: 100%;
}

/* 日期分隔符 */
.date-divider {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 24px 0 12px;
}

.date-divider span {
  padding: 4px 12px;
  background: #2f3136;
  border-radius: 12px;
  font-size: 12px;
  color: #72767d;
  font-weight: 600;
}

/* 系统消息 */
.system-message {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 12px 0;
  padding: 8px 12px;
  background: rgba(114, 137, 218, 0.1);
  border-radius: 4px;
  font-size: 13px;
  color: #b9bbbe;
}

.system-message svg {
  flex-shrink: 0;
}

/* 消息项 */
.message-item {
  display: flex;
  gap: 14px;
  padding: 2px 16px;
  transition: background 0.2s;
  margin-top: 0;
}

/* 第一条消息或新消息组 */
.message-item:not(.message-group-continue) {
  margin-top: 17px;
  padding-top: 4px;
}

/* 继续同一组的消息 */
.message-item.message-group-continue {
  margin-top: 0;
  padding-top: 0;
  padding-bottom: 0;
}

.message-item:hover {
  background: rgba(4, 4, 5, 0.07);
}

/* 在hover时显示紧凑时间戳 */
.message-item.message-group-continue:hover .message-timestamp {
  opacity: 1;
}

.message-avatar {
  flex-shrink: 0;
  width: 40px;
  margin-top: 2px;
}

.message-avatar img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

/* 头像占位符 */
.message-avatar-placeholder {
  flex-shrink: 0;
  width: 40px;
}

.message-content-wrapper {
  flex: 1;
  min-width: 0;
}

.message-header {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 2px;
  line-height: 1.375rem;
}

.message-author {
  font-size: 15px;
  font-weight: 600;
  color: #fff;
}

.message-time {
  font-size: 12px;
  color: #72767d;
  font-weight: 400;
}

.message-content {
  font-size: 15px;
  line-height: 1.375rem;
  color: #dcddde;
  word-wrap: break-word;
  position: relative;
  padding-left: 0;
}

/* 紧凑消息内容 */
.message-content-compact {
  padding-left: 0;
}

/* 时间戳（hover时显示） */
.message-timestamp {
  position: absolute;
  left: -56px;
  font-size: 11px;
  color: #72767d;
  opacity: 0;
  transition: opacity 0.2s;
  line-height: 1.375rem;
  text-align: right;
  width: 48px;
}

/* 输入区域 */
.message-input-area {
  padding: 20px;
  background: #36393f;
  border-top: 1px solid #202225;
  flex-shrink: 0;
}

.input-toolbar {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.toolbar-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  color: #b9bbbe;
  cursor: pointer;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.toolbar-btn:hover {
  background: #3c3f45;
  color: #dcddde;
}

.toolbar-btn svg {
  width: 20px;
  height: 20px;
}

.input-wrapper {
  width: 100%;
}

.message-input {
  width: 100%;
  padding: 12px 16px;
  background: #40444b;
  border: none;
  border-radius: 8px;
  color: #dcddde;
  font-size: 15px;
  outline: none;
  transition: background 0.2s;
}

.message-input::placeholder {
  color: #72767d;
}

.message-input:focus {
  background: #383a40;
}

/* 加载和空状态 */
.loading-message,
.empty-message {
  text-align: center;
  color: #72767d;
  padding: 40px 20px;
  font-size: 14px;
}

.empty-message p {
  margin: 0;
}

/* 弹窗样式 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.85);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.15s;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.dialog {
  background: #36393f;
  border-radius: 8px;
  width: 90%;
  max-width: 480px;
  max-height: 85vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.24);
  animation: slideUp 0.2s;
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.dialog-header {
  padding: 20px;
  border-bottom: 1px solid #2f3136;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-shrink: 0;
}

.dialog-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #fff;
}

.close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  color: #b9bbbe;
  cursor: pointer;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.close-btn:hover {
  background: #3c3f45;
  color: #dcddde;
}

.close-btn svg {
  width: 20px;
  height: 20px;
}

.dialog-body {
  padding: 20px;
  overflow-y: auto;
  flex: 1;
}

.group-info-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #2f3136;
}

.group-avatar-large {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  margin-bottom: 12px;
}

.group-avatar-large img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.group-id {
  font-size: 14px;
  color: #b9bbbe;
  background: #2f3136;
  padding: 6px 12px;
  border-radius: 4px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  color: #b9bbbe;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 8px;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 10px;
  background: #202225;
  border: 1px solid #2f3136;
  border-radius: 4px;
  color: #dcddde;
  font-size: 14px;
  font-family: inherit;
  outline: none;
  transition: all 0.2s;
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: #5865f2;
}

.form-group textarea {
  resize: vertical;
  min-height: 60px;
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.btn-primary,
.btn-secondary {
  padding: 10px 24px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  min-width: 80px;
}

.btn-primary {
  background: #5865f2;
  color: #fff;
}

.btn-primary:hover:not(:disabled) {
  background: #4752c4;
}

.btn-primary:disabled {
  background: #4752c4;
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-secondary {
  background: transparent;
  color: #fff;
  border: 1px solid transparent;
}

.btn-secondary:hover {
  background: #4e5058;
  border-color: #4e5058;
}
</style>
