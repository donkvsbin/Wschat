<template>
  <div class="chat-container">

    <div>
      <router-view/>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { logout } from '../api/auth'

const router = useRouter()
const route = useRoute()
const userInfo = ref(null)
const currentServer = ref('star')
const activeCategory = ref('online')
const selectedFriend = ref(null)

// 检查当前路由是否为私信页面
const isMessageActive = computed(() => {
  return route.path === '/home/privatemessage'
})

// 导航到私信页面
const navigateToMessage = () => {
  router.push('/home/privatemessage')
}

// 用户头像
const userAvatar = computed(() => {
  return `https://api.dicebear.com/7.x/avataaars/svg?seed=${userInfo.value?.username || 'default'}`
})

// 模拟在线好友数据
const onlineFriends = ref([
  {
    id: 1,
    name: '余悦通知',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=kook1',
    status: 'online',
    activity: 'KOOK',
    badge: true
  },
  {
    id: 2,
    name: '活动小助手',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=kook2',
    status: 'online',
    activity: 'KOOK',
    badge: true
  },
  {
    id: 3,
    name: '领头',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=leader',
    status: 'idle'
  },
  {
    id: 4,
    name: 'Sunset.',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=sunset',
    status: 'idle'
  },
  {
    id: 5,
    name: 'SeuLe.',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=seule',
    status: 'idle'
  },
  {
    id: 6,
    name: 'godhong',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=godhong',
    status: 'online'
  },
  {
    id: 7,
    name: 'LXB',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=lxb',
    status: 'idle'
  },
  {
    id: 8,
    name: 'quin33',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=quin33',
    status: 'idle'
  }
])

onMounted(() => {
  const savedUserInfo = localStorage.getItem('userInfo')
  if (savedUserInfo) {
    userInfo.value = JSON.parse(savedUserInfo)
  } else {
    router.push('/login')
  }
})

const selectFriend = (friendId) => {
  selectedFriend.value = friendId
}

const handleLogout = async () => {
  try {
    await logout()
    localStorage.removeItem('userInfo')
    router.push('/login')
  } catch (error) {
    console.error('退出失败:', error)
    localStorage.removeItem('userInfo')
    router.push('/login')
  }
}
</script>

<style scoped>
.chat-container {
  display: flex;
  height: 100vh;
  background: #36393f;
  color: #dcddde;
  overflow: hidden;
}

/* 最左侧图标导航栏 */
.icon-sidebar {
  width: 90px;
  background: #202225;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px 0;
  gap: 12px;
  overflow-y: auto;
  scrollbar-width: none;
}

.icon-sidebar::-webkit-scrollbar {
  display: none;
}

.icon-item {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: #36393f;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
  color: #96989d;
}

.icon-item:hover {
  border-radius: 20px;
  background: #5865f2;
  color: #fff;
}

.icon-item.active {
  border-radius: 20px;
  background: #5865f2;
  color: #fff;
}

.icon-item svg {
  width: 28px;
  height: 28px;
}

.message-icon {
  background: #36393f;
  color: #96989d;
  margin-bottom: 8px;
  border-radius: 50%;
}

.message-icon:hover {
  background: #5865f2;
  color: #fff;
}

.message-icon.active {
  background: #5865f2;
  color: #fff;
}

.icon-divider {
  width: 32px;
  height: 2px;
  background: #36393f;
  margin: 4px 0;
}

.server-avatars {
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
  align-items: center;
}

.server-avatar img {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  transition: border-radius 0.2s ease;
}

.server-avatar:hover img {
  border-radius: 20px;
}

.add-server {
  background: #36393f;
  color: #43b581;
  font-size: 32px;
  font-weight: 300;
}

.add-server:hover {
  background: #43b581;
  color: #fff;
}

/* 左侧导航栏 */
.left-sidebar {
  width: 320px;
  background: #2f3136;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
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
}

.footer-avatar img {
  width: 38px;
  height: 38px;
  border-radius: 50%;
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

/* 退出登录圆形红色按钮 */
.logout-btn-circle {
  width: 36px;
  height: 36px;
  border: none;
  background: #ed4245;
  color: #fff;
  cursor: pointer;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  box-shadow: 0 2px 6px rgba(237, 66, 69, 0.3);
}

.logout-btn-circle:hover {
  background: #c03537;
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(237, 66, 69, 0.5);
}

.logout-btn-circle:active {
  transform: scale(0.95);
}

.logout-btn-circle svg {
  width: 20px;
  height: 20px;
}

/* 主聊天区域 */
.main-content {
  flex: 1;
  background: #36393f;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-state {
  text-align: center;
  color: #72767d;
}

.empty-state svg {
  width: 240px;
  height: 240px;
  margin-bottom: 24px;
  opacity: 0.5;
}

.empty-state p {
  font-size: 18px;
  color: #72767d;
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
</style>
