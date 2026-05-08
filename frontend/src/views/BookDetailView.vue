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
        <div class="flex gap-6 mb-4">
          <div v-if="book.hasCover" class="shrink-0">
            <img
              :src="`/api/books/${book.id}/cover`"
              :alt="book.title"
              class="w-40 h-60 object-contain rounded border border-gray-200"
            />
          </div>
          <div class="flex-1 min-w-0">
            <div class="flex items-start justify-between gap-4">
              <div class="min-w-0">
                <h1 class="text-2xl font-bold text-gray-800 truncate">{{ book.title }}</h1>
                <p class="text-gray-600 mt-1">by {{ book.author }}</p>
              </div>
              <span
                :class="statusClass(book.conversionStatus)"
                class="px-3 py-1 rounded-full text-sm font-medium shrink-0"
              >
                {{ book.conversionStatus }}
              </span>
            </div>

            <div class="grid grid-cols-2 gap-y-2 gap-x-8 text-sm mt-4">
              <div v-if="book.genre">
                <span class="text-gray-500">Genre:</span>
                <span class="ml-2 text-gray-800">{{ book.genre }}</span>
              </div>
              <div v-if="book.language">
                <span class="text-gray-500">Language:</span>
                <span class="ml-2 text-gray-800">{{ book.language }}</span>
              </div>
              <div v-if="book.sequenceName">
                <span class="text-gray-500">Series:</span>
                <span class="ml-2 text-gray-800">{{ book.sequenceName }}<span v-if="book.sequenceNumber"> #{{ book.sequenceNumber }}</span></span>
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
          </div>
        </div>

        <div v-if="book.description" class="mb-6">
          <h3 class="text-sm font-medium text-gray-500 mb-1">Description</h3>
          <p class="text-gray-700 text-sm whitespace-pre-line">{{ book.description }}</p>
        </div>

        <div v-if="conversion" class="mb-6">
          <h3 class="text-sm font-medium text-gray-500 mb-2">Conversion Result</h3>
          <div class="bg-gray-50 rounded-lg p-4">
            <div class="grid grid-cols-2 sm:grid-cols-3 gap-y-2 gap-x-6 text-sm">
              <div v-if="conversion.epubFileSize">
                <span class="text-gray-500">EPUB size:</span>
                <span class="ml-2 text-gray-800">{{ formatSize(conversion.epubFileSize) }}</span>
              </div>
              <div v-if="conversion.azw8FileSize">
                <span class="text-gray-500">AZW8 size:</span>
                <span class="ml-2 text-gray-800">{{ formatSize(conversion.azw8FileSize) }}</span>
              </div>
            </div>

            <div v-if="hasEpubMeta" class="mt-3 pt-3 border-t border-gray-200">
              <p class="text-xs font-medium text-gray-400 mb-2 uppercase tracking-wide">EPUB Metadata</p>
              <div class="grid grid-cols-2 sm:grid-cols-3 gap-y-2 gap-x-6 text-sm">
                <div v-if="conversion.epubTitle">
                  <span class="text-gray-500">Title:</span>
                  <span class="ml-2 text-gray-800">{{ conversion.epubTitle }}</span>
                </div>
                <div v-if="conversion.epubAuthor">
                  <span class="text-gray-500">Author:</span>
                  <span class="ml-2 text-gray-800">{{ conversion.epubAuthor }}</span>
                </div>
                <div v-if="conversion.epubPublisher">
                  <span class="text-gray-500">Publisher:</span>
                  <span class="ml-2 text-gray-800">{{ conversion.epubPublisher }}</span>
                </div>
                <div v-if="conversion.epubLanguage">
                  <span class="text-gray-500">Language:</span>
                  <span class="ml-2 text-gray-800">{{ conversion.epubLanguage }}</span>
                </div>
                <div v-if="conversion.epubIdentifier">
                  <span class="text-gray-500">Identifier:</span>
                  <span class="ml-2 text-gray-800 break-all">{{ conversion.epubIdentifier }}</span>
                </div>
                <div v-if="conversion.epubPublicationDate">
                  <span class="text-gray-500">Date:</span>
                  <span class="ml-2 text-gray-800">{{ conversion.epubPublicationDate }}</span>
                </div>
                <div v-if="conversion.epubSubjects && conversion.epubSubjects.length" class="col-span-2">
                  <span class="text-gray-500">Subjects:</span>
                  <span class="ml-2 text-gray-800">{{ conversion.epubSubjects.join(', ') }}</span>
                </div>
              </div>
            </div>
          </div>
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
const conversion = ref(null)
const loading = ref(true)
const error = ref('')
const downloading = ref(false)
const showEdit = ref(false)

const isOwner = computed(() => book.value && book.value.username === authStore.username)
const hasEpubMeta = computed(() => {
  if (!conversion.value) return false
  return !!(conversion.value.epubTitle || conversion.value.epubAuthor ||
    conversion.value.epubPublisher || conversion.value.epubLanguage ||
    conversion.value.epubIdentifier || conversion.value.epubPublicationDate ||
    (conversion.value.epubSubjects && conversion.value.epubSubjects.length))
})

function statusClass(status) {
  switch (status) {
    case 'COMPLETED': return 'bg-green-100 text-green-700'
    case 'PROCESSING': return 'bg-yellow-100 text-yellow-700'
    case 'FAILED': return 'bg-red-100 text-red-700'
    default: return 'bg-gray-100 text-gray-700'
  }
}

function formatSize(bytes) {
  if (bytes === null || bytes === undefined) return ''
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

async function loadBook() {
  loading.value = true
  error.value = ''
  try {
    const { data } = await bookService.get(route.params.id)
    book.value = data
    if (data.conversionStatus === 'COMPLETED') {
      try {
        const { data: convData } = await bookService.conversionDetail(data.id)
        conversion.value = convData
      } catch {
        // conversion detail not available, skip
      }
    }
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