import api from './api'

export const authService = {
  login(username, password) {
    return api.post('/auth/login', { username, password })
  },

  register(username, email, password) {
    return api.post('/auth/register', { username, email, password })
  },

  refresh(refreshToken) {
    return api.post('/auth/refresh', { refreshToken })
  },

  logout() {
    return api.post('/auth/logout')
  },
}