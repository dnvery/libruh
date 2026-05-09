<template>
  <nav class="bg-white shadow-md">
    <div class="container mx-auto px-4 flex items-center justify-between h-16">
      <router-link to="/" class="text-2xl font-bold text-primary-600">
        Libruh
      </router-link>
      <div class="flex items-center gap-4">
        <template v-if="authStore.isAuthenticated">
          <router-link to="/books" class="text-sm text-gray-600 hover:text-primary-600">
            My Books
          </router-link>
          <router-link to="/config" class="text-sm text-gray-600 hover:text-primary-600">
            Config
          </router-link>
          <span class="text-gray-600 text-sm">{{ authStore.username }}</span>
          <button
            @click="handleLogout"
            :disabled="loggingOut"
            class="px-4 py-2 text-sm text-red-600 hover:text-red-800 hover:bg-red-50 rounded-md transition-colors disabled:opacity-50"
          >
            {{ loggingOut ? 'Logging out...' : 'Logout' }}
          </button>
        </template>
        <template v-else>
          <router-link to="/login" class="px-4 py-2 text-sm text-primary-600 hover:text-primary-800">
            Login
          </router-link>
          <router-link to="/register" class="px-4 py-2 text-sm bg-primary-600 text-white rounded-md hover:bg-primary-700">
            Register
          </router-link>
        </template>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { authService } from '../services/authService'

const authStore = useAuthStore()
const router = useRouter()
const loggingOut = ref(false)

async function handleLogout() {
  loggingOut.value = true
  try {
    await authService.logout(authStore.refreshToken)
  } catch {
    // ignore errors on logout
  } finally {
    authStore.logout()
    router.push('/login')
    loggingOut.value = false
  }
}
</script>