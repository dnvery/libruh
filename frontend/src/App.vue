<template>
  <div id="app" class="min-h-screen flex flex-col">
    <Navbar />
    <main class="flex-1 container mx-auto px-4 py-8">
      <router-view />
    </main>
    <footer class="bg-gray-800 text-gray-400 text-center py-4 text-sm">
      Libruh Book Library &copy; 2026
    </footer>
  </div>
</template>

<script setup>
import Navbar from './components/Navbar.vue'
</script>