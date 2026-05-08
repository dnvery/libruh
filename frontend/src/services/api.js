import axios from 'axios'
import { useAuthStore } from '../stores/auth'
import router from '../router'

let isRefreshing = false
let failedQueue = []

function processQueue(error, token = null) {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error)
    } else {
      prom.resolve(token)
    }
  })
  failedQueue = []
}

const api = axios.create({
  baseURL: '/api',
  timeout: 30000,
})

api.interceptors.request.use((config) => {
  const auth = useAuthStore()
  if (auth.accessToken) {
    config.headers.Authorization = `Bearer ${auth.accessToken}`
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config

    const status = error.response?.status
    const isAuthError = status === 401 || (status === 403 && !originalRequest._retry)

    if (!isAuthError || originalRequest._retry) {
      return Promise.reject(error)
    }

    if (isRefreshing) {
      return new Promise(function (resolve, reject) {
        failedQueue.push({ resolve, reject })
      }).then((token) => {
        originalRequest.headers.Authorization = `Bearer ${token}`
        return api(originalRequest)
      }).catch((err) => {
        return Promise.reject(err)
      })
    }

    originalRequest._retry = true
    isRefreshing = true

    const auth = useAuthStore()

    try {
      const response = await axios.post('/api/auth/refresh', {
        refreshToken: auth.refreshToken,
      })
      const { accessToken, refreshToken: newRefresh } = response.data
      auth.setTokens(accessToken, newRefresh || auth.refreshToken)
      originalRequest.headers.Authorization = `Bearer ${accessToken}`
      processQueue(null, accessToken)
      return api(originalRequest)
    } catch (refreshError) {
      processQueue(refreshError, null)
      auth.logout()
      router.push('/login')
      return Promise.reject(refreshError)
    } finally {
      isRefreshing = false
    }
  }
)

export default api