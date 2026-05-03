<template>
  <nav class="bg-white shadow-md">
    <div class="container mx-auto px-4 flex items-center justify-between h-16">
      <router-link to="/" class="text-2xl font-bold text-primary-600">
        Libruh
      </router-link>
      <div class="flex items-center gap-4">
        <template v-if="authStore.isAuthenticated">
          <span class="text-gray-600 text-sm">Hello, {{ authStore.username }}</span>
          <button
            @click="authStore.logout()"
            class="px-4 py-2 text-sm text-red-600 hover:text-red-800"
          >
            Logout
          </button>
        </template>
        <template v-else>
          <router-link to="/login" class="px-4 py-2 text-sm text-primary-600 hover:text-primary-800">
            Login
          </router-link>
          <router-link to="/register" class="px-4 py-2 text-sm bg-primary-600 text-white rounded hover:bg-primary-700">
            Register
          </router-link>
        </template>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { useAuthStore } from '../stores/auth'
const authStore = useAuthStore()
</script>