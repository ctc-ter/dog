import { defineStore } from 'pinia'
import { ref } from 'vue'
import Cookies from 'js-cookie'

export const useUserStore = defineStore('user', () => {
  const token = ref(Cookies.get('token') || '')
  const userInfo = ref(null)

  const setToken = (t) => {
    token.value = t
    Cookies.set('token', t, { expires: 1 })
  }

  const clearToken = () => {
    token.value = ''
    userInfo.value = null
    Cookies.remove('token')
  }

  return { token, userInfo, setToken, clearToken }
})
