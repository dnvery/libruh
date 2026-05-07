<template>
  <div class="flex gap-2">
    <div class="relative flex-1">
      <input
        v-model="query"
        type="text"
        placeholder="Search books..."
        class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500 text-sm"
        @keyup.enter="handleSearch"
      />
      <svg
        class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400"
        fill="none"
        stroke="currentColor"
        viewBox="0 0 24 24"
      >
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
      </svg>
    </div>
    <select
      v-model="field"
      class="px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500 bg-white"
    >
      <option value="title">Title</option>
      <option value="author">Author</option>
      <option value="genre">Genre</option>
    </select>
    <button
      @click="handleSearch"
      class="px-4 py-2 bg-primary-600 text-white rounded-md hover:bg-primary-700 text-sm font-medium"
    >
      Search
    </button>
    <button
      v-if="active"
      @click="handleClear"
      class="px-4 py-2 border border-gray-300 rounded-md hover:bg-gray-50 text-sm"
    >
      Clear
    </button>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const emit = defineEmits(['search', 'clear'])

const query = ref('')
const field = ref('title')
const active = ref(false)

function handleSearch() {
  const q = query.value.trim()
  if (!q) return
  active.value = true
  emit('search', q, field.value)
}

function handleClear() {
  query.value = ''
  field.value = 'title'
  active.value = false
  emit('clear')
}
</script>