<template>
<div class="private-message-container">
  <!-- 左侧导航栏 -->
  <div class="left-sidebar">
    <!-- 头部用户信息 -->
    <div class="sidebar-header">
      <div class="user-profile">
        <div class="user-avatar">
          <img :src="userAvatar" alt="avatar">
        </div>
        <div class="user-details">
          <div class="username">{{ userInfo?.nickname || userInfo?.username }} <span class="user-tag">#3905</span></div>
        </div>

      </div>
      <div class="header-actions">
        <button class="icon-btn">
          <svg viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z"/>
          </svg>
        </button>
      </div>
    </div>

    <!-- 好友与群聊分类 -->
    <div class="friends-section">
      <div class="section-header">
        <span>好友与群聊</span>
        <button class="add-friend-btn" @click="showAddFriendDialog = true">添加好友</button>
      </div>

      <div class="friend-category" :class="{ active: activeCategory === 'all' }" @click="activeCategory = 'all'">
        <svg class="category-icon" viewBox="0 0 24 24" fill="currentColor">
          <path d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z"/>
        </svg>
        <span>好友</span>
        <span class="count">{{ friendList.length }}</span>
      </div>

      <div class="friend-category" :class="{ active: activeCategory === 'groups' }" @click="activeCategory = 'groups'">
        <svg class="category-icon" viewBox="0 0 24 24" fill="currentColor">
          <path d="M12 12.75c1.63 0 3.07.39 4.24.9 1.08.48 1.76 1.56 1.76 2.73V18H6v-1.61c0-1.18.68-2.26 1.76-2.73 1.17-.52 2.61-.91 4.24-.91zM4 13c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm1.13 1.1c-.37-.06-.74-.1-1.13-.1-.99 0-1.93.21-2.78.58C.48 14.9 0 15.62 0 16.43V18h4.5v-1.61c0-.83.23-1.61.63-2.29zM20 13c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm4 3.43c0-.81-.48-1.53-1.22-1.85-.85-.37-1.79-.58-2.78-.58-.39 0-.76.04-1.13.1.4.68.63 1.46.63 2.29V18H24v-1.57zM12 6c1.66 0 3 1.34 3 3s-1.34 3-3 3-3-1.34-3-3 1.34-3 3-3z"/>
        </svg>
        <span>群聊</span>
        <span class="count">{{ groupList.length }}</span>
      </div>

      <div class="friend-category" :class="{ active: activeCategory === 'requests' }" @click="switchToRequests">
        <svg class="category-icon" viewBox="0 0 24 24" fill="currentColor">
          <path d="M19 3h-4.18C14.4 1.84 13.3 1 12 1c-1.3 0-2.4.84-2.82 2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-7 0c.55 0 1 .45 1 1s-.45 1-1 1-1-.45-1-1 .45-1 1-1zm2 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
        </svg>
        <span>好友请求</span>
        <span class="count">{{ receivedRequests.length }}</span>
      </div>
    </div>



    <!-- 好友请求列表 -->
    <div class="dm-section" v-if="activeCategory === 'requests'">
      <div class="section-title">好友请求</div>

      <div class="friend-list">
        <div v-if="receivedRequests.length === 0" class="empty-message">
          暂无好友请求
        </div>
        <div
          class="friend-request-item"
          v-for="request in receivedRequests"
          :key="request.id"
        >
          <div class="friend-avatar-wrapper">
            <img :src="getAvatarUrl(request.fromUser?.username, request.fromUser?.avatarUrl)" :alt="request.fromUser?.nickname" class="friend-avatar">
          </div>
          <div class="friend-info">
            <div class="friend-name">{{ request.fromUser?.nickname || request.fromUser?.username }}</div>
            <div class="friend-activity">{{ request.message || '想添加你为好友' }}</div>
          </div>
          <div class="request-actions">
            <button class="accept-btn" @click="handleRequest(request.id, true)">接受</button>
            <button class="reject-btn" @click="handleRequest(request.id, false)">拒绝</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 好友列表 -->
    <div class="dm-section" v-else-if="activeCategory !== 'groups'">
      <div class="section-title">好友</div>

      <div class="friend-list">
        <div v-if="displayedFriends.length === 0" class="empty-message">
          暂无好友，快去添加好友吧！
        </div>
        <div
          class="friend-item"
          v-for="friendship in displayedFriends"
          :key="friendship.friendId"
          :class="{ active: selectedFriend === friendship.friendId }"
          @click="selectFriend(friendship.friendId)"
        >
          <div class="friend-avatar-wrapper">
            <img :src="getAvatarUrl(friendship.friend?.username, friendship.friend?.avatarUrl)" :alt="friendship.friend?.nickname" class="friend-avatar">
            <span class="status-indicator" :class="friendship.friend?.isOnline ? 'online' : 'offline'"></span>
          </div>
          <div class="friend-info">
            <div class="friend-name">{{ friendship.friend?.nickname || friendship.friend?.username }}</div>
            <div class="friend-activity" v-if="friendship.friend?.isOnline">
              在线
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 群聊列表 -->
    <div class="dm-section" v-else>
      <div class="section-title">群聊</div>

      <div class="friend-list">
        <div class="empty-message" v-if="groupList.length === 0">
          暂无群聊，<span class="create-link" @click="showCreateGroupDialog = true">创建群聊</span>或加入群聊
        </div>
        <div
          class="friend-item"
          v-for="group in groupList"
          :key="group.id"
          :class="{ active: selectedFriend === group.id }"
          @click="selectGroup(group.id)"
        >
          <div class="friend-avatar-wrapper">
            <img :src="getGroupAvatar(group.id)" :alt="group.name" class="friend-avatar">
          </div>
          <div class="friend-info">
            <div class="friend-name">{{ group.name }}</div>
            <div class="friend-activity">
              {{ group.memberCount }} 人
            </div>
          </div>
        </div>
      </div>

      <!-- 创建/加入群聊按钮 -->
      <div class="group-actions">
        <button class="action-btn" @click="showCreateGroupDialog = true">
          <svg viewBox="0 0 24 24" fill="currentColor" width="16" height="16">
            <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
          </svg>
          创建群聊
        </button>
        <button class="action-btn" @click="showJoinGroupDialog">
          <svg viewBox="0 0 24 24" fill="currentColor" width="16" height="16">
            <path d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z"/>
          </svg>
          加入群聊
        </button>
      </div>
    </div>

    <!-- 底部控制栏 -->
    <div class="sidebar-footer">
      <div class="footer-user">
        <div class="footer-avatar" @click="goToSettings">
          <img :src="userAvatar" alt="avatar">
          <span class="status-indicator online"></span>
          <div class="edit-overlay">
            <svg viewBox="0 0 24 24" fill="currentColor" width="20" height="20">
              <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/>
            </svg>
          </div>
        </div>
        <div class="footer-info">
          <div class="footer-username">{{ userInfo?.nickname || userInfo?.username }}</div>
        </div>
      </div>
      <div class="footer-controls">
        <button class="control-btn" @click="logout" title="登出">
          <svg viewBox="0 0 24 24" fill="currentColor">
            <path d="M17 7l-1.41 1.41L18.17 11H8v2h10.17l-2.58 2.58L17 17l5-5zM4 5h8V3H4c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h8v-2H4V5z"/>
          </svg>
        </button>
        <button class="control-btn" @click="" title="设置">
          <svg viewBox="0 0 24 24" fill="currentColor">
            <path d="M19.43 12.98c.04-.32.07-.64.07-.98s-.03-.66-.07-.98l2.11-1.65c.19-.15.24-.42.12-.64l-2-3.46c-.12-.22-.39-.3-.61-.22l-2.49 1c-.52-.4-1.08-.73-1.69-.98l-.38-2.65C14.46 2.18 14.25 2 14 2h-4c-.25 0-.46.18-.49.42l-.38 2.65c-.61.25-1.17.59-1.69.98l-2.49-1c-.23-.09-.49 0-.61.22l-2 3.46c-.13.22-.07.49.12.64l2.11 1.65c-.04.32-.07.65-.07.98s.03.66.07.98l-2.11 1.65c-.19.15-.24.42-.12.64l2 3.46c.12.22.39.3.61.22l2.49-1c.52.4 1.08.73 1.69.98l.38 2.65c.03.24.24.42.49.42h4c.25 0 .46-.18.49-.42l.38-2.65c.61-.25 1.17-.59 1.69-.98l2.49 1c.23.09.49 0 .61-.22l2-3.46c.12-.22.07-.49-.12-.64l-2.11-1.65zM12 15.5c-1.93 0-3.5-1.57-3.5-3.5s1.57-3.5 3.5-3.5 3.5 1.57 3.5 3.5-1.57 3.5-3.5 3.5z"/>
          </svg>
        </button>

      </div>
    </div>
  </div>

  <!-- 右侧聊天区域 -->
  <div class="chat-area">
    <!-- 当没有选中好友时显示空状态 -->
    <div v-if="!route.params.friendId" class="empty-state">
      <div class="empty-state-content">
        <div class="empty-illustration">
          <svg viewBox="0 0 421 218" fill="none">
            <path d="M200 80C200 124.183 164.183 160 120 160C75.8172 160 40 124.183 40 80C40 35.8172 75.8172 0 120 0C164.183 0 200 35.8172 200 80Z" fill="#5865F2" fill-opacity="0.1"/>
            <path d="M380 80C380 124.183 344.183 160 300 160C255.817 160 220 124.183 220 80C220 35.8172 255.817 0 300 0C344.183 0 380 35.8172 380 80Z" fill="#5865F2" fill-opacity="0.1"/>
            <circle cx="120" cy="80" r="25" fill="#B9BBBE"/>
            <circle cx="300" cy="80" r="25" fill="#B9BBBE"/>
            <path d="M150 150 Q210 120, 270 150" stroke="#B9BBBE" stroke-width="8" stroke-linecap="round" fill="none"/>
          </svg>
        </div>
        <div class="empty-text">此时你没有好友在线，快去叫他们来玩！</div>
      </div>
    </div>
    <!-- 聊天窗口 -->
    <router-view v-else :friendInfo="selectedFriendInfo" :websocket="ws"></router-view>
  </div>

  <!-- 添加好友弹窗 -->
  <div class="dialog-overlay" v-if="showAddFriendDialog" @click="showAddFriendDialog = false">
    <div class="dialog" @click.stop>
      <div class="dialog-header">
        <h2>添加好友</h2>
        <button class="close-btn" @click="showAddFriendDialog = false">
          <svg viewBox="0 0 24 24" fill="currentColor">
            <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
          </svg>
        </button>
      </div>
      <div class="dialog-body">
        <div class="search-section">
          <input
            type="text"
            v-model="searchKeyword"
            placeholder="输入用户名或昵称搜索"
            @keyup.enter="searchUsersFn"
          >
          <button class="search-btn" @click="searchUsersFn">搜索</button>
        </div>

        <div class="search-results" v-if="searchResults.length > 0">
          <div class="result-item" v-for="user in searchResults" :key="user.id">
            <div class="user-avatar">
              <img :src="getAvatarUrl(user.username, user.avatarUrl)" :alt="user.nickname">
            </div>
            <div class="user-info">
              <div class="user-name">{{ user.nickname }}</div>
              <div class="user-username">@{{ user.username }}</div>
            </div>
            <button class="add-btn" @click="sendRequest(user.id, user.nickname)">添加</button>
          </div>
        </div>

        <div class="empty-message" v-else-if="hasSearched">
          未找到匹配的用户
        </div>
      </div>
    </div>
  </div>

  <!-- 创建群聊弹窗 -->
  <div class="dialog-overlay" v-if="showCreateGroupDialog" @click="showCreateGroupDialog = false">
    <div class="dialog" @click.stop>
      <div class="dialog-header">
        <h2>创建群聊</h2>
        <button class="close-btn" @click="showCreateGroupDialog = false">
          <svg viewBox="0 0 24 24" fill="currentColor">
            <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
          </svg>
        </button>
      </div>
      <div class="dialog-body">
        <div class="form-group">
          <label>群聊名称</label>
          <input
            type="text"
            v-model="newGroupName"
            placeholder="请输入群聊名称"
            maxlength="50"
          >
        </div>
        <div class="form-group">
          <label>群聊描述</label>
          <textarea
            v-model="newGroupDescription"
            placeholder="请输入群聊描述（可选）"
            rows="3"
            maxlength="200"
          ></textarea>
        </div>
        <div class="dialog-actions">
          <button class="btn-secondary" @click="showCreateGroupDialog = false">取消</button>
          <button class="btn-primary" @click="createGroup" :disabled="!newGroupName.trim()">创建</button>
        </div>
      </div>
    </div>
  </div>

  <!-- 加入群聊弹窗 -->
  <div class="dialog-overlay" v-if="showJoinGroupDialogFlag" @click="showJoinGroupDialogFlag = false">
    <div class="dialog" @click.stop>
      <div class="dialog-header">
          <h2>加入群聊</h2>
        <button class="close-btn" @click="showJoinGroupDialogFlag = false">
          <svg viewBox="0 0 24 24" fill="currentColor">
            <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
          </svg>
        </button>
      </div>
      <div class="dialog-body">
        <div class="form-group">
          <label>群聊号</label>
          <input
            type="text"
            v-model="joinGroupId"
            placeholder="请输入群聊号"
            maxlength="20"
            @keyup.enter="joinGroup"
          >
        </div>
        <div class="dialog-actions">
          <button class="btn-secondary" @click="showJoinGroupDialogFlag = false">取消</button>
          <button class="btn-primary" @click="joinGroup" :disabled="!joinGroupId.trim()">加入</button>
        </div>
      </div>
    </div>
  </div>
</div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getFriendList, getReceivedRequests, searchUsers, sendFriendRequest, handleFriendRequest as handleFriendRequestAPI } from '../api/friend'
import { getGroupList, createGroup as createGroupAPI, joinGroup as joinGroupAPI } from '../api/group'
import { logout as logoutAPI } from '../api/auth.js'
import { getFullAvatarUrl, getDefaultAvatar } from '../utils/avatar'

const router = useRouter()
const route = useRoute()

// 用户信息
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
const activeCategory = ref('all')
const selectedFriend = ref(null)

// 好友相关数据
const friendList = ref([])
const receivedRequests = ref([])

// 群组相关数据
const groupList = ref([])
const showCreateGroupDialog = ref(false)
const showJoinGroupDialogFlag = ref(false)
const newGroupName = ref('')
const newGroupDescription = ref('')
const joinGroupId = ref('')

// 添加好友弹窗
const showAddFriendDialog = ref(false)
const searchKeyword = ref('')
const searchResults = ref([])
const hasSearched = ref(false)

// WebSocket连接
const ws = ref(null)

// 用户头像
const userAvatar = computed(() => {
  return getFullAvatarUrl(userInfo.value?.avatarUrl, userInfo.value?.username)
})

// 获取头像URL
const getAvatarUrl = (username, avatarUrl = null) => {
  return getFullAvatarUrl(avatarUrl, username)
}

// 显示的好友列表（在线好友置顶）
const displayedFriends = computed(() => {
  return [...friendList.value].sort((a, b) => {
    // 在线状态排序：在线(true)在前，离线(false)在后
    const aOnline = a.friend?.isOnline ? 1 : 0
    const bOnline = b.friend?.isOnline ? 1 : 0
    return bOnline - aOnline
  })
})

// 加载好友列表
const loadFriendList = async () => {
  try {
    console.log('开始加载好友列表...')
    const res = await getFriendList()
    console.log('好友列表响应:', res)
    if (res.success) {
      friendList.value = res.data || []
      console.log('好友列表数量:', friendList.value.length)
    } else {
      console.error('加载好友列表失败:', res.message)
    }
  } catch (error) {
    console.error('加载好友列表失败:', error)
    console.error('错误详情:', error.response)
    if (error.response?.status === 401) {
      console.error('未登录或登录已过期')
    }
  }
}

// 加载好友请求
const loadFriendRequests = async () => {
  try {
    console.log('开始加载好友请求...')
    const res = await getReceivedRequests()
    console.log('好友请求响应:', res)
    if (res.success) {
      receivedRequests.value = res.data || []
      console.log('收到的好友请求数量:', receivedRequests.value.length)
    } else {
      console.error('加载好友请求失败:', res.message)
      alert('加载好友请求失败: ' + (res.message || '未知错误'))
    }
  } catch (error) {
    console.error('加载好友请求失败:', error)
    if (error.response?.status === 401) {
      alert('请先登录')
    } else {
      alert('加载好友请求失败，请检查网络连接')
    }
  }
}

// 搜索用户
const searchUsersFn = async () => {
  if (!searchKeyword.value.trim()) {
    alert('请输入搜索关键词')
    return
  }

  try {
    const res = await searchUsers(searchKeyword.value.trim())
    if (res.success) {
      searchResults.value = res.data || []
      hasSearched.value = true
    }
  } catch (error) {
    console.error('搜索用户失败:', error)
    alert('搜索失败，请重试')
  }
}

// 发送好友请求
const sendRequest = async (friendId, nickname) => {
  try {
    const res = await sendFriendRequest({
      friendId: friendId,
      message: `你好，我是${userInfo.value.nickname || userInfo.value.username}`
    })

    if (res.success) {
      alert(`好友请求已发送给 ${nickname}`)
      searchResults.value = []
      searchKeyword.value = ''
      hasSearched.value = false
      showAddFriendDialog.value = false
    } else {
      alert(res.message || '发送失败')
    }
  } catch (error) {
    console.error('发送好友请求失败:', error)
    alert('发送失败，请重试')
  }
}

// 处理好友请求
const handleRequest = async (requestId, accept) => {
  try {
    const res = await handleFriendRequestAPI({
      requestId: requestId,
      accept: accept
    })

    if (res.success) {
      alert(accept ? '已接受好友请求' : '已拒绝好友请求')
      await loadFriendRequests()
      if (accept) {
        await loadFriendList()
      }
    } else {
      alert(res.message || '操作失败')
    }
  } catch (error) {
    console.error('处理好友请求失败:', error)
    alert('操作失败，请重试')
  }
}

const selectFriend = (friendId) => {
  selectedFriend.value = friendId
  // 导航到消息页面
  router.push(`/home/privatemessage/${friendId}`)
}

// 加载群组列表
const loadGroupList = async () => {
  try {
    const res = await getGroupList()
    if (res.success) {
      groupList.value = res.data || []
      console.log('群组列表数量:', groupList.value.length)
    }
  } catch (error) {
    console.error('加载群组列表失败:', error)
  }
}

// 创建群组
const createGroup = async () => {
  if (!newGroupName.value.trim()) {
    alert('请输入群聊名称')
    return
  }

  try {
    const res = await createGroupAPI({
      name: newGroupName.value.trim(),
      description: newGroupDescription.value.trim()
    })

    if (res.success) {
      alert(`群聊创建成功！群号：${res.data.id}`)
      newGroupName.value = ''
      newGroupDescription.value = ''
      showCreateGroupDialog.value = false
      await loadGroupList()
      // 自动切换到群聊分类
      activeCategory.value = 'groups'
    } else {
      alert('创建失败: ' + res.message)
    }
  } catch (error) {
    console.error('创建群组失败:', error)
    alert('创建失败，请重试')
  }
}

// 显示加入群聊对话框
const showJoinGroupDialog = () => {
  joinGroupId.value = ''
  showJoinGroupDialogFlag.value = true
}

// 加入群组
const joinGroup = async () => {
  if (!joinGroupId.value.trim()) {
    alert('请输入群号')
    return
  }

  try {
    const res = await joinGroupAPI(joinGroupId.value.trim())
    if (res.success) {
      alert('加入成功！')
      joinGroupId.value = ''
      showJoinGroupDialogFlag.value = false
      await loadGroupList()
      activeCategory.value = 'groups'
    } else {
      alert('加入失败: ' + res.message)
    }
  } catch (error) {
    console.error('加入群组失败:', error)
    alert('加入失败，请重试')
  }
}

// 选择群组
const selectGroup = (groupId) => {
  selectedFriend.value = groupId
  // 导航到消息页面（复用私聊消息组件）
  router.push(`/home/privatemessage/group_${groupId}`)
}

// 获取群组头像
const getGroupAvatar = (groupId) => {
  // 使用群ID生成默认头像
  return `https://api.dicebear.com/7.x/initials/svg?seed=${groupId}&backgroundColor=5865f2`
}

// 获取当前选中的好友信息
const selectedFriendInfo = computed(() => {
  console.log('[PrivateMessage] selectedFriend.value:', selectedFriend.value)
  console.log('[PrivateMessage] friendList.value:', friendList.value)

  if (!selectedFriend.value) {
    console.log('[PrivateMessage] selectedFriend 为空')
    return null
  }

  const friendship = friendList.value.find(f => f.friendId === selectedFriend.value)
  console.log('[PrivateMessage] 找到的friendship:', friendship)

  const friendInfo = friendship?.friend || null
  console.log('[PrivateMessage] 返回的friendInfo:', friendInfo)

  return friendInfo
})

// 切换到好友请求
const switchToRequests = async () => {
  activeCategory.value = 'requests'
  await loadFriendRequests()
}

// 登出功能
const logout = async () => {
  try {
    console.log('开始登出...')

    // 调用后端API登出
    await logoutAPI()

    // 关闭 WebSocket 连接
    if (ws.value) {
      ws.value.close()
    }

    // 清除本地存储
    localStorage.removeItem('userInfo')

    // 跳转到登录页
    router.push('/login')

    console.log('登出成功')
  } catch (error) {
    console.error('登出失败:', error)
    // 即使后端API失败，也清除本地数据并跳转
    localStorage.removeItem('userInfo')
    if (ws.value) {
      ws.value.close()
    }
    router.push('/login')
  }
}

// 跳转到设置页面
const goToSettings = () => {
  router.push('/settings')
}

// 初始化WebSocket连接
const initWebSocket = () => {
  const userId = userInfo.value?.id
  if (!userId) {
    console.error('[好友列表] 用户未登录')
    return
  }

  ws.value = new WebSocket(`ws://localhost:8081/ws/chat?userId=${userId}`)

  ws.value.onopen = () => {
    console.log('[好友列表] WebSocket连接已建立')
  }

  ws.value.onmessage = (event) => {
    try {
      const data = JSON.parse(event.data)
      console.log('[好友列表] 收到WebSocket消息:', data)

      // 处理用户状态变化消息
      if (data.type === 'USER_STATUS') {
        console.log('[好友列表] 用户状态变化:', data.userId, data.isOnline)

        // 更新好友列表中的在线状态
        friendList.value.forEach(friendship => {
          if (friendship.friend && friendship.friend.id === data.userId) {
            console.log('[好友列表] 更新好友', friendship.friend.nickname, '的在线状态为:', data.isOnline)
            friendship.friend.isOnline = data.isOnline
          }
        })
      }
      // 其他类型的消息不在这里处理，由Message组件处理
    } catch (error) {
      console.error('[好友列表] 解析WebSocket消息失败:', error)
    }
  }

  ws.value.onerror = (error) => {
    console.error('[好友列表] WebSocket错误:', error)
  }

  ws.value.onclose = () => {
    console.log('[好友列表] WebSocket连接已关闭')
    // 尝试重连
    setTimeout(() => {
      if (ws.value && ws.value.readyState === WebSocket.CLOSED) {
        initWebSocket()
      }
    }, 3000)
  }
}

// 页面加载时获取数据
onMounted(() => {
  loadFriendList()
  loadFriendRequests()
  loadGroupList()

  // 初始化WebSocket
  initWebSocket()

  // 如果路由中已经有friendId，设置为选中状态
  if (route.params.friendId) {
    selectedFriend.value = parseInt(route.params.friendId)
  }
})

// 组件卸载时关闭 WebSocket
onUnmounted(() => {
  if (ws.value) {
    ws.value.close()
  }
})

// 监听路由变化，更新选中的好友
watch(() => route.params.friendId, (newFriendId) => {
  if (newFriendId) {
    selectedFriend.value = parseInt(newFriendId)
  }
})
</script>

<style scoped>
/* 主容器 */
.private-message-container {
  display: flex;
  width: 100vw;
  height: 100vh;
  background: #36393f;
  overflow: hidden;
}

/* 左侧导航栏 */
.left-sidebar {
  width: 320px;
  height: 100vh;
  background: #2f3136;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: #202225 transparent;
}

/* 左侧导航栏滚动条样式（Webkit浏览器） */
.left-sidebar::-webkit-scrollbar {
  width: 8px;
}

.left-sidebar::-webkit-scrollbar-track {
  background: transparent;
}

.left-sidebar::-webkit-scrollbar-thumb {
  background: #202225;
  border-radius: 4px;
}

.left-sidebar::-webkit-scrollbar-thumb:hover {
  background: #1a1c1f;
}

/* 头部用户信息 */
.sidebar-header {
  padding: 20px;
  background: #2f3136;
  border-bottom: 1px solid #202225;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 14px;
}

.user-avatar {
  position: relative;
}

.user-avatar img {
  width: 48px;
  height: 48px;
  border-radius: 50%;
}

.user-details {
  display: flex;
  flex-direction: column;
}

.username {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
}

.user-tag {
  font-size: 16px;
  color: #b9bbbe;
  font-weight: 400;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.icon-btn {
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

.icon-btn:hover {
  background: #36393f;
  color: #dcddde;
}

.icon-btn svg {
  width: 22px;
  height: 22px;
}

/* 好友分类 */
.friends-section {
  padding: 18px 10px;
  border-bottom: 1px solid #202225;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 10px;
  margin-bottom: 14px;
}

.section-header span {
  font-size: 13px;
  font-weight: 600;
  color: #96989d;
  text-transform: uppercase;
}

.add-friend-btn {
  background: #43b581;
  color: #fff;
  border: none;
  padding: 6px 14px;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
  transition: background 0.2s;
}

.add-friend-btn:hover {
  background: #3ca374;
}

.friend-category {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  border-radius: 4px;
  cursor: pointer;
  color: #96989d;
  transition: all 0.2s;
  margin-bottom: 6px;
}

.friend-category:hover {
  background: #36393f;
  color: #dcddde;
}

.friend-category.active {
  background: #36393f;
  color: #fff;
}

.category-icon {
  width: 22px;
  height: 22px;
}

.friend-category span {
  font-size: 15px;
}

.count {
  margin-left: auto;
  font-size: 13px;
  color: #96989d;
}

/* 服务器分类 */
.server-section {
  padding: 18px 10px;
  border-bottom: 1px solid #202225;
}

.section-title {
  font-size: 13px;
  font-weight: 600;
  color: #96989d;
  text-transform: uppercase;
  padding: 0 10px;
  margin-bottom: 10px;
}

.server-category {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  border-radius: 4px;
  cursor: pointer;
  color: #96989d;
  transition: all 0.2s;
  margin-bottom: 6px;
}

.server-category:hover {
  background: #36393f;
  color: #dcddde;
}

/* 私信列表 */
.dm-section {
  flex: 1;
  overflow-y: auto;
  padding: 18px 10px;
}

.friend-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-top: 10px;
}

.friend-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 10px;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s;
}

.friend-item:hover {
  background: #36393f;
}

.friend-item.active {
  background: #36393f;
}

.friend-avatar-wrapper {
  position: relative;
  flex-shrink: 0;
}

.friend-avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
}

.status-indicator {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 3px solid #2f3136;
}

.status-indicator.online {
  background: #43b581;
}

.status-indicator.idle {
  background: #faa61a;
}

.status-indicator.offline {
  background: #747f8d;
}

.friend-info {
  flex: 1;
  min-width: 0;
}

.friend-name {
  font-size: 15px;
  font-weight: 500;
  color: #dcddde;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.friend-activity {
  font-size: 13px;
  color: #43b581;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 底部控制栏 */
.sidebar-footer {
  background: #292b2f;
  padding: 14px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.footer-user {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  min-width: 0;
}

.footer-avatar {
  position: relative;
  flex-shrink: 0;
  cursor: pointer;
  width: 38px;
  height: 38px;
  border-radius: 50%;
  overflow: hidden;
  transition: all 0.2s;
}

.footer-avatar:hover {
  transform: scale(1.05);
}

.footer-avatar img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  transition: filter 0.2s;
}

.footer-avatar:hover img {
  filter: brightness(0.6);
}

.footer-avatar .edit-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.5);
  opacity: 0;
  transition: opacity 0.2s;
  pointer-events: none;
}

.footer-avatar:hover .edit-overlay {
  opacity: 1;
}

.footer-avatar .edit-overlay svg {
  color: #fff;
}

.footer-info {
  flex: 1;
  min-width: 0;
}

.footer-username {
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.footer-status {
  font-size: 12px;
  color: #b9bbbe;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.footer-controls {
  display: flex;
  gap: 10px;
}

.control-btn {
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

.control-btn:hover {
  background: #36393f;
  color: #dcddde;
}

.control-btn svg {
  width: 20px;
  height: 20px;
}

/* 登出按钮特殊样式 */
.control-btn:first-child:hover {
  background: #f04747;
  color: #fff;
}

/* 滚动条样式 */
.dm-section::-webkit-scrollbar {
  width: 8px;
}

.dm-section::-webkit-scrollbar-track {
  background: transparent;
}

.dm-section::-webkit-scrollbar-thumb {
  background: #202225;
  border-radius: 4px;
}

.dm-section::-webkit-scrollbar-thumb:hover {
  background: #1a1c1f;
}

/* 空消息提示 */
.empty-message {
  text-align: center;
  color: #72767d;
  padding: 30px 20px;
  font-size: 14px;
}

/* 空状态提示 */
.empty-state {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #36393f;
}

.empty-state-content {
  text-align: center;
  padding: 40px;
}

.empty-illustration {
  margin-bottom: 30px;
  opacity: 0.6;
}

.empty-illustration svg {
  width: 280px;
  height: auto;
  filter: grayscale(30%);
}

.empty-text {
  font-size: 18px;
  font-weight: 500;
  color: #b9bbbe;
  letter-spacing: 0.5px;
  text-align: center;
}

/* 好友请求项 */
.friend-request-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px;
  border-radius: 6px;
  background: #36393f;
  margin-bottom: 10px;
}

.request-actions {
  display: flex;
  gap: 8px;
}

.accept-btn, .reject-btn {
  padding: 6px 16px;
  border: none;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.accept-btn {
  background: #43b581;
  color: #fff;
}

.accept-btn:hover {
  background: #3ca374;
}

.reject-btn {
  background: #f04747;
  color: #fff;
}

.reject-btn:hover {
  background: #d84040;
}

/* 添加好友弹窗 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog {
  background: #36393f;
  border-radius: 8px;
  width: 500px;
  max-height: 600px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #2f3136;
}

.dialog-header h2 {
  color: #fff;
  font-size: 20px;
  margin: 0;
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
  background: #f04747;
  color: #fff;
}

.close-btn svg {
  width: 20px;
  height: 20px;
}

.dialog-body {
  padding: 20px;
  overflow-y: auto;
}

/* 表单组 */
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

/* 对话框按钮 */
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

/* 群组操作按钮 */
.group-actions {
  padding: 12px;
  border-top: 1px solid #2f3136;
  display: flex;
  gap: 8px;
}

.action-btn {
  flex: 1;
  padding: 8px 12px;
  background: #5865f2;
  border: none;
  border-radius: 4px;
  color: #fff;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  transition: all 0.2s;
}

.action-btn:hover {
  background: #4752c4;
}

.create-link {
  color: #5865f2;
  cursor: pointer;
  text-decoration: underline;
}

.create-link:hover {
  color: #4752c4;
}

.search-section {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.search-section input {
  flex: 1;
  padding: 10px 14px;
  background: #2f3136;
  border: 1px solid #202225;
  border-radius: 4px;
  color: #fff;
  font-size: 14px;
}

.search-section input:focus {
  outline: none;
  border-color: #43b581;
}

.search-btn {
  padding: 10px 20px;
  background: #43b581;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s;
}

.search-btn:hover {
  background: #3ca374;
}

.search-results {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.result-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px;
  background: #2f3136;
  border-radius: 6px;
}

.result-item .user-avatar {
  flex-shrink: 0;
}

.result-item .user-avatar img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.result-item .user-info {
  flex: 1;
  min-width: 0;
}

.result-item .user-name {
  font-size: 15px;
  font-weight: 500;
  color: #fff;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.result-item .user-username {
  font-size: 13px;
  color: #72767d;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.add-btn {
  padding: 8px 20px;
  background: #43b581;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
  transition: background 0.2s;
}

.add-btn:hover {
  background: #3ca374;
}

.status-indicator.active {
  background: #43b581;
}

.status-indicator.inactive {
  background: #747f8d;
}

.status-indicator.banned {
  background: #f04747;
}

/* ==================== 右侧聊天区域 ==================== */
.chat-area {
  flex: 1;
  width: 100%;
  display: flex;
  flex-direction: column;
  background: #36393f;
  overflow: hidden;
}

/* 未选择好友提示 */
.no-chat-selected {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #36393f;
}

.welcome-content {
  text-align: center;
  color: #b9bbbe;
}

.welcome-content h2 {
  margin: 20px 0 10px;
  font-size: 24px;
  font-weight: 600;
  color: #fff;
}

.welcome-content p {
  font-size: 14px;
  color: #b9bbbe;
}

/* 聊天窗口 */
.chat-window {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
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

.chat-friend-avatar img {
  width: 38px;
  height: 38px;
  border-radius: 50%;
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
  max-width: 900px;
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
  margin-bottom: 18px;
  padding: 4px 0;
  transition: background 0.2s;
}

.message-item:hover {
  background: rgba(4, 4, 5, 0.07);
}

.message-avatar {
  flex-shrink: 0;
}

.message-avatar img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.message-content-wrapper {
  flex: 1;
  min-width: 0;
}

.message-header {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 4px;
}

.message-author {
  font-size: 15px;
  font-weight: 600;
  color: #fff;
}

.message-time {
  font-size: 12px;
  color: #72767d;
}

.message-content {
  font-size: 15px;
  line-height: 1.5;
  color: #dcddde;
  word-wrap: break-word;
  margin-bottom: 2px;
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
</style>
