import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

interface SessionState {
  token: string
  userId: number
  username: string
  nickname: string
  role: number
}

const STORAGE_KEY = 'campus-lost-found-session'

function loadSession(): SessionState | null {
  const raw = localStorage.getItem(STORAGE_KEY)
  if (!raw) {
    return null
  }

  try {
    return JSON.parse(raw) as SessionState
  } catch {
    return null
  }
}

export const useAuthStore = defineStore('auth', () => {
  const session = ref<SessionState | null>(loadSession())

  const isLoggedIn = computed(() => Boolean(session.value?.token))
  const isAdmin = computed(() => session.value?.role === 1)

  function setSession(nextSession: SessionState) {
    session.value = nextSession
    localStorage.setItem(STORAGE_KEY, JSON.stringify(nextSession))
    localStorage.setItem('campus-lost-found-token', nextSession.token)
  }

  function clearSession() {
    session.value = null
    localStorage.removeItem(STORAGE_KEY)
    localStorage.removeItem('campus-lost-found-token')
  }

  return {
    session,
    isLoggedIn,
    isAdmin,
    setSession,
    clearSession,
  }
})
