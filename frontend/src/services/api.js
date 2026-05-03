import axios from 'axios'
import { useAuthStore } from '../stores/auth'
import router from '../router'

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
    if (error.response?.status === 401 && !originalRequest._retry) {
      const auth = useAuthStore()
      try {
        const response = await axios.post('/api/auth/refresh', {
          refreshToken: auth.refreshToken,
        })
        const { accessToken, refreshToken: newRefresh } = response.data
        auth.setTokens(accessToken, newRefresh || auth.refreshToken)
        originalRequest.headers.Authorization = `Bearer ${accessToken}`
        originalRequest._retry = true
        return api(originalRequest)
      } catch {
        auth.logout()
        router.push('/login')
      }
    }
    return Promise.reject(error)
  }
)

export default api