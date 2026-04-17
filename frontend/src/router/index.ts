import { createRouter, createWebHistory } from 'vue-router'

import { useAuthStore } from '@/stores/auth'
import AppShell from '@/components/AppShell.vue'
import AdminView from '@/views/AdminView.vue'
import HomeView from '@/views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import MessagesView from '@/views/MessagesView.vue'
import NotificationsView from '@/views/NotificationsView.vue'
import ProfileView from '@/views/ProfileView.vue'
import RegisterView from '@/views/RegisterView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { guestOnly: true },
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView,
      meta: { guestOnly: true },
    },
    {
      path: '/',
      component: AppShell,
      children: [
        {
          path: '',
          name: 'home',
          component: HomeView,
        },
        {
          path: 'profile',
          name: 'profile',
          component: ProfileView,
          meta: { requiresAuth: true },
        },
        {
          path: 'messages',
          name: 'messages',
          component: MessagesView,
          meta: { requiresAuth: true },
        },
        {
          path: 'notifications',
          name: 'notifications',
          component: NotificationsView,
          meta: { requiresAuth: true },
        },
        {
          path: 'admin',
          name: 'admin',
          component: AdminView,
          meta: { requiresAuth: true, requiresAdmin: true },
        },
      ],
    },
  ],
})

router.beforeEach((to) => {
  const authStore = useAuthStore()

  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }

  if (to.meta.requiresAdmin && !authStore.isAdmin) {
    return { name: 'home' }
  }

  if (to.meta.guestOnly && authStore.isLoggedIn) {
    return { name: 'home' }
  }

  return true
})

export default router
