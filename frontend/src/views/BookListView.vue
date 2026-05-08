<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold text-gray-800">My Books</h1>
      <router-link
        to="/books/upload"
        class="px-4 py-2 bg-primary-600 text-white rounded-md hover:bg-primary-700 text-sm font-medium"
      >
        Upload Book
      </router-link>
    </div>

    <SearchBar @search="handleSearch" @clear="handleClear" class="mb-4" />

    <div v-if="loading" class="text-center py-12 text-gray-500">Loading books...</div>

    <div v-else-if="error" class="text-center py-12 text-red-600 bg-red-50 rounded-lg">
      {{ error }}
    </div>

    <div v-else-if="books.length === 0" class="text-center py-12 text-gray-500">
      {{ searchActive ? 'No books found matching your search.' : 'No books yet. Upload your first FB2 file!' }}
    </div>

    <div v-else>
      <p v-if="searchActive" class="text-sm text-gray-500 mb-3">
        Showing results for "{{ searchQuery }}" in {{ searchField }}
      </p>

      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
        <router-link
          v-for="book in books"
          :key="book.id"
          :to="`/books/${book.id}`"
          class="bg-white rounded-lg shadow-sm border border-gray-200 hover:shadow-md transition-shadow overflow-hidden block"
        >
          <div class="aspect-[2/3] bg-gray-100 flex items-center justify-center overflow-hidden">
            <img
              v-if="book.hasCover"
              :src="`/api/books/${book.id}/cover`"
              :alt="book.title"
              class="w-full h-full object-contain"
              loading="lazy"
            />
            <svg v-else class="w-16 h-16 text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
            </svg>
          </div>
          <div class="p-3">
            <h3 class="font-semibold text-gray-800 text-sm truncate">{{ book.title }}</h3>
            <p class="text-xs text-gray-500 truncate mt-0.5">{{ book.author }}</p>
            <div class="flex items-center gap-2 mt-2">
              <span
                :class="statusClass(book.conversionStatus)"
                class="px-2 py-0.5 rounded-full text-xs font-medium"
              >
                {{ book.conversionStatus }}
              </span>
              <span v-if="book.genre" class="text-xs text-gray-400 truncate">{{ book.genre }}</span>
            </div>
          </div>
        </router-link>
      </div>

      <div v-if="pagination.totalPages > 1" class="flex items-center justify-center gap-2 mt-8">
        <button
          :disabled="pagination.page === 0"
          @click="loadPage(pagination.page - 1)"
          class="px-3 py-1 text-sm border rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          Previous
        </button>
        <span class="text-sm text-gray-600">
          Page {{ pagination.page + 1 }} of {{ pagination.totalPages }}
        </span>
        <button
          :disabled="pagination.page >= pagination.totalPages - 1"
          @click="loadPage(pagination.page + 1)"
          class="px-3 py-1 text-sm border rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          Next
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useBooksStore } from '../stores/books'
import { bookService } from '../services/bookService'
import SearchBar from '../components/SearchBar.vue'

const booksStore = useBooksStore()
const loading = ref(true)
const error = ref('')

const books = ref([])
const pagination = ref({ page: 0, size: 20, totalElements: 0, totalPages: 0 })
const searchActive = ref(false)
const searchQuery = ref('')
const searchField = ref('title')

function statusClass(status) {
  switch (status) {
    case 'COMPLETED':
      return 'bg-green-100 text-green-700'
    case 'PROCESSING':
      return 'bg-yellow-100 text-yellow-700'
    case 'FAILED':
      return 'bg-red-100 text-red-700'
    default:
      return 'bg-gray-100 text-gray-700'
  }
}

async function loadPage(page) {
  loading.value = true
  error.value = ''
  try {
    let data
    if (searchActive.value) {
      ;({ data } = await bookService.search(searchQuery.value, searchField.value, page, 20))
    } else {
      ;({ data } = await bookService.list(page, 20))
    }
    books.value = data.books
    pagination.value = {
      page: data.page,
      size: data.size,
      totalElements: data.totalElements,
      totalPages: data.totalPages,
    }
    booksStore.books = data.books
    booksStore.pagination = pagination.value
  } catch {
    error.value = 'Failed to load books'
  } finally {
    loading.value = false
  }
}

function handleSearch(query, field) {
  searchActive.value = true
  searchQuery.value = query
  searchField.value = field
  loadPage(0)
}

function handleClear() {
  searchActive.value = false
  searchQuery.value = ''
  searchField.value = 'title'
  loadPage(0)
}

onMounted(() => loadPage(0))
</script>