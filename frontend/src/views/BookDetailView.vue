<template>
  <div>
    <div v-if="loading" class="text-center py-12 text-gray-500">Loading book details...</div>

    <div v-else-if="error" class="text-center py-12">
      <p class="text-red-600 bg-red-50 rounded-lg p-4">{{ error }}</p>
      <router-link to="/books" class="text-primary-600 hover:text-primary-800 mt-4 inline-block">Back to books</router-link>
    </div>

    <div v-else-if="book">
      <router-link to="/books" class="text-primary-600 hover:text-primary-800 text-sm mb-4 inline-block">&larr; Back to books</router-link>

      <div class="bg-white rounded-lg shadow-md p-6 mt-2">
        <div class="flex items-start justify-between mb-4">
          <div>
            <h1 class="text-2xl font-bold text-gray-800">{{ book.title }}</h1>
            <p class="text-gray-600 mt-1">by {{ book.author }}</p>
          </div>
          <span
            :class="statusClass(book.conversionStatus)"
            class="px-3 py-1 rounded-full text-sm font-medium"
          >
            {{ book.conversionStatus }}
          </span>
        </div>

        <div class="grid grid-cols-2 gap-y-3 gap-x-8 text-sm mb-6">
          <div v-if="book.genre">
            <span class="text-gray-500">Genre:</span>
            <span class="ml-2 text-gray-800">{{ book.genre }}</span>
          </div>
          <div v-if="book.language">
            <span class="text-gray-500">Language:</span>
            <span class="ml-2 text-gray-800">{{ book.language }}</span>
          </div>
          <div v-if="book.publicationDate">
            <span class="text-gray-500">Published:</span>
            <span class="ml-2 text-gray-800">{{ book.publicationDate }}</span>
          </div>
          <div v-if="book.uploadDate">
            <span class="text-gray-500">Uploaded:</span>
            <span class="ml-2 text-gray-800">{{ new Date(book.uploadDate).toLocaleDateString() }}</span>
          </div>
          <div>
            <span class="text-gray-500">Uploaded by:</span>
            <span class="ml-2 text-gray-800">{{ book.username }}</span>
          </div>
        </div>

        <div v-if="book.description" class="mb-6">
          <h3 class="text-sm font-medium text-gray-500 mb-1">Description</h3>
          <p class="text-gray-700 text-sm whitespace-pre-line">{{ book.description }}</p>
        </div>

        <div class="flex flex-wrap gap-3 border-t pt-4">
          <button
            v-if="book.conversionStatus === 'COMPLETED'"
            @click="download('epub')"
            :disabled="downloading"
            class="px-4 py-2 bg-primary-600 text-white rounded-md hover:bg-primary-700 text-sm font-medium disabled:opacity-50"
          >
            Download EPUB
          </button>
          <button
            v-if="book.conversionStatus === 'COMPLETED'"
            @click="download('azw8')"
            :disabled="downloading"
            class="px-4 py-2 bg-primary-600 text-white rounded-md hover:bg-primary-700 text-sm font-medium disabled:opacity-50"
          >
            Download AZW8
          </button>
          <button
            v-if="isOwner"
            @click="showEdit = true"
            class="px-4 py-2 border border-gray-300 rounded-md hover:bg-gray-50 text-sm font-medium"
          >
            Edit
          </button>
          <button
            v-if="isOwner"
            @click="handleDelete"
            class="px-4 py-2 text-red-600 border border-red-300 rounded-md hover:bg-red-50 text-sm font-medium"
          >
            Delete
          </button>
          <span v-if="book.conversionStatus === 'PENDING' || book.conversionStatus === 'PROCESSING'" class="text-sm text-yellow-600 self-center">
            Conversion in progress... refresh to check status.
          </span>
        </div>
      </div>

      <EditBookModal
        v-if="showEdit"
        :book="book"
        @close="showEdit = false"
        @saved="onBookUpdated"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { bookService } from '../services/bookService'
import EditBookModal from '../components/EditBookModal.vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const book = ref(null)
const loading = ref(true)
const error = ref('')
const downloading = ref(false)
const showEdit = ref(false)

const isOwner = computed(() => book.value && book.value.username === authStore.username)

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

async function loadBook() {
  loading.value = true
  error.value = ''
  try {
    const { data } = await bookService.get(route.params.id)
    book.value = data
  } catch {
    error.value = 'Failed to load book'
  } finally {
    loading.value = false
  }
}

async function download(format) {
  downloading.value = true
  try {
    const res = await (format === 'epub'
      ? bookService.downloadEpub(book.value.id)
      : bookService.downloadAzw8(book.value.id))
    const url = window.URL.createObjectURL(new Blob([res.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `${book.value.title}.${format}`)
    document.body.appendChild(link)
    link.click()
    link.remove()
    window.URL.revokeObjectURL(url)
  } catch {
    alert('Download failed. The file may not be ready yet.')
  } finally {
    downloading.value = false
  }
}

async function handleDelete() {
  if (!confirm('Are you sure you want to delete this book?')) return
  try {
    await bookService.delete(book.value.id)
    router.push('/books')
  } catch {
    alert('Failed to delete book')
  }
}

function onBookUpdated(updatedBook) {
  book.value = updatedBook
  showEdit.value = false
}

onMounted(loadBook)
</script>