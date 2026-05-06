<template>
  <div class="flex items-center justify-center min-h-[60vh]">
    <div class="text-center">
      <h1 class="text-4xl font-bold text-primary-600 mb-4">Libruh</h1>
      <p class="text-gray-600 text-lg mb-8">Your personal book library</p>
      <div v-if="authStore.isAuthenticated">
        <router-link
          to="/books"
          class="px-6 py-3 bg-primary-600 text-white rounded-md hover:bg-primary-700 font-medium"
        >
          My Books
        </router-link>
      </div>
      <div v-else class="flex gap-4 justify-center">
        <router-link
          to="/login"
          class="px-6 py-3 border border-primary-600 text-primary-600 rounded-md hover:bg-primary-50 font-medium"
        >
          Login
        </router-link>
        <router-link
          to="/register"
          class="px-6 py-3 bg-primary-600 text-white rounded-md hover:bg-primary-700 font-medium"
        >
          Register
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from '../stores/auth'
const authStore = useAuthStore()
</script>