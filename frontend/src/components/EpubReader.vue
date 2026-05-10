<template>
  <div class="epub-reader-container fixed inset-0 flex flex-col bg-gray-50">
    <div class="bg-white shadow-sm border-b border-gray-200 px-4 py-3 flex items-center justify-between flex-shrink-0">
      <div class="flex items-center gap-4">
        <button
          @click="goBack"
          class="text-gray-600 hover:text-gray-900 font-medium text-sm"
        >
          ← Back
        </button>
        <div class="text-sm text-gray-600">
          <span v-if="bookTitle" class="font-medium">{{ bookTitle }}</span>
        </div>
      </div>

      <div class="flex items-center gap-2">
        <button
          @click="goBackInBook"
          v-if="canGoBack"
          class="px-3 py-1 bg-primary-600 text-white hover:bg-primary-700 rounded text-sm"
          title="Go back to previous location"
        >
          ↩ Back
        </button>
        <button
          @click="prevPage"
          :disabled="!canGoPrev"
          class="px-3 py-1 bg-gray-100 hover:bg-gray-200 disabled:opacity-50 disabled:cursor-not-allowed rounded text-sm"
        >
          Previous
        </button>
        <span class="text-sm text-gray-600 min-w-[100px] text-center">
          {{ currentLocation }}
        </span>
        <button
          @click="nextPage"
          :disabled="!canGoNext"
          class="px-3 py-1 bg-gray-100 hover:bg-gray-200 disabled:opacity-50 disabled:cursor-not-allowed rounded text-sm"
        >
          Next
        </button>
      </div>

      <div class="flex items-center gap-2">
        <button
          @click="decreaseFontSize"
          class="px-2 py-1 bg-gray-100 hover:bg-gray-200 rounded text-sm"
          title="Decrease font size"
        >
          A-
        </button>
        <button
          @click="increaseFontSize"
          class="px-2 py-1 bg-gray-100 hover:bg-gray-200 rounded text-sm"
          title="Increase font size"
        >
          A+
        </button>
        <button
          @click="toggleToc"
          class="px-3 py-1 bg-gray-100 hover:bg-gray-200 rounded text-sm ml-2"
        >
          Contents
        </button>
      </div>
    </div>

    <div class="bg-white border-b border-gray-200 px-4 py-2 flex-shrink-0">
      <div class="flex items-center gap-3">
        <span class="text-xs text-gray-500 min-w-[40px]">{{ currentLocation }}</span>
        <div class="flex-1 relative">
          <input
            type="range"
            min="0"
            max="100"
            :value="progressPercentage"
            @input="seekToPercentage"
            :disabled="!locationsReady"
            :class="locationsReady ? 'cursor-pointer' : 'cursor-not-allowed opacity-50'"
            class="w-full h-2 bg-gray-200 rounded-lg appearance-none accent-primary-600"
          />
        </div>
        <span class="text-xs text-gray-500 min-w-[40px] text-right">100%</span>
      </div>
    </div>

    <div class="flex-1 flex min-h-0">
      <div
        v-if="showToc"
        class="w-64 bg-white border-r border-gray-200 overflow-y-auto p-4 flex-shrink-0"
      >
        <h3 class="font-bold text-gray-800 mb-3">Table of Contents</h3>
        <div v-if="toc.length === 0" class="text-sm text-gray-500">
          No table of contents available
        </div>
        <ul v-else class="space-y-1">
          <li
            v-for="(item, index) in toc"
            :key="index"
            class="text-sm"
          >
            <button
              @click="goToChapter(item.href)"
              class="text-left w-full hover:text-primary-600 text-gray-700 py-1"
            >
              {{ item.label }}
            </button>
          </li>
        </ul>
      </div>

      <div class="flex-1 flex flex-col items-center min-h-0">
        <div
          ref="viewerContainer"
          class="epub-viewer w-full h-full max-w-4xl bg-white shadow-lg"
          v-show="!loading && !error"
        ></div>
        <div v-if="loading" class="flex items-center justify-center h-full">
          <div class="text-gray-500">Loading book...</div>
        </div>
        <div v-else-if="error" class="flex items-center justify-center h-full">
          <div class="text-red-600 bg-red-50 rounded-lg p-4">{{ error }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import ePub from 'epubjs'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const viewerContainer = ref(null)
const loading = ref(true)
const error = ref('')
const bookTitle = ref('')
const currentLocation = ref('')
const progressPercentage = ref(0)
const locationsReady = ref(false)
const canGoPrev = ref(false)
const canGoNext = ref(true)
const showToc = ref(false)
const toc = ref([])
const fontSize = ref(100)
const navigationHistory = ref([])
const canGoBack = ref(false)

let book = null
let rendition = null

const props = defineProps({
  bookId: {
    type: [String, Number],
    required: true
  }
})

function goBack() {
  router.push(`/books/${props.bookId}`)
}

async function loadBook() {
  // Wait for the DOM to be ready first
  await nextTick()

  if (!viewerContainer.value) {
    error.value = 'Viewer container not ready'
    loading.value = false
    return
  }

  // Set loading to false early so the container becomes visible and gets dimensions
  loading.value = false
  await nextTick()

  // Wait for container to have dimensions
  let attempts = 0
  while (attempts < 10 && (viewerContainer.value.offsetWidth === 0 || viewerContainer.value.offsetHeight === 0)) {
    await new Promise(resolve => setTimeout(resolve, 50))
    attempts++
  }

  try {
    // Fetch the EPUB file as a blob with authentication
    const response = await fetch(`/api/books/${props.bookId}/epub`, {
      headers: {
        'Authorization': `Bearer ${authStore.accessToken}`
      }
    })

    if (!response.ok) {
      throw new Error(`Failed to fetch EPUB: ${response.status}`)
    }

    const blob = await response.blob()
    const arrayBuffer = await blob.arrayBuffer()

    book = ePub(arrayBuffer)

    await book.ready

    const metadata = await book.loaded.metadata
    bookTitle.value = metadata.title || 'Unknown Title'

    rendition = book.renderTo(viewerContainer.value, {
      width: '100%',
      height: '100%',
      spread: 'none',
      flow: 'paginated',
      ignoreClass: 'annotator-hl'
    })

    rendition.themes.fontSize(`${fontSize.value}%`)

    // Display the first page immediately
    await rendition.display()

    // Force a resize to trigger proper rendering
    setTimeout(() => {
      if (rendition) {
        rendition.resize()
      }
    }, 100)

    const navigation = await book.loaded.navigation
    toc.value = navigation.toc.map(item => ({
      label: item.label,
      href: item.href
    }))

    rendition.on('relocated', (location) => {
      if (location && location.start && location.start.cfi && book.locations.length() > 0) {
        const percentage = book.locations.percentageFromCfi(location.start.cfi)
        progressPercentage.value = Math.round(percentage * 100)
        currentLocation.value = `${progressPercentage.value}%`

        canGoPrev.value = !location.atStart
        canGoNext.value = !location.atEnd
      } else {
        // Fallback when locations aren't ready yet
        currentLocation.value = 'Loading...'
        canGoPrev.value = !location.atStart
        canGoNext.value = !location.atEnd
      }
    })

    // Listen to all events to see what's available
    rendition.hooks.content.register((contents) => {
      contents.document.addEventListener('click', (event) => {
        const target = event.target
        if (target.tagName.toLowerCase() === 'a') {
          const currentLoc = rendition.currentLocation()
          if (currentLoc && currentLoc.start && currentLoc.start.cfi) {
            navigationHistory.value.push(currentLoc.start.cfi)
            canGoBack.value = true
          }
        }
      })
    })

    // Generate locations in the background (larger chunk size = faster but less accurate)
    book.locations.generate(1600).then(() => {
      locationsReady.value = true
      // Trigger a location update now that we have accurate progress
      const currentLoc = rendition.currentLocation()
      if (currentLoc && currentLoc.start && currentLoc.start.cfi) {
        const percentage = book.locations.percentageFromCfi(currentLoc.start.cfi)
        progressPercentage.value = Math.round(percentage * 100)
        currentLocation.value = `${progressPercentage.value}%`
      }
    })
  } catch (err) {
    console.error('Failed to load EPUB:', err)
    error.value = `Failed to load book: ${err.message}`
  }
}

function nextPage() {
  if (rendition) {
    rendition.next()
  }
}

function prevPage() {
  if (rendition) {
    rendition.prev()
  }
}

function increaseFontSize() {
  fontSize.value = Math.min(fontSize.value + 10, 200)
  if (rendition) {
    rendition.themes.fontSize(`${fontSize.value}%`)
  }
}

function decreaseFontSize() {
  fontSize.value = Math.max(fontSize.value - 10, 50)
  if (rendition) {
    rendition.themes.fontSize(`${fontSize.value}%`)
  }
}

function toggleToc() {
  showToc.value = !showToc.value
}

function goToChapter(href) {
  if (rendition) {
    const currentLoc = rendition.currentLocation()
    if (currentLoc && currentLoc.start && currentLoc.start.cfi) {
      navigationHistory.value.push(currentLoc.start.cfi)
      canGoBack.value = true
    }
    rendition.display(href)
  }
}

function goBackInBook() {
  if (navigationHistory.value.length > 0 && rendition) {
    const previousCfi = navigationHistory.value.pop()
    canGoBack.value = navigationHistory.value.length > 0
    rendition.display(previousCfi)
  }
}

function seekToPercentage(event) {
  const percentage = parseInt(event.target.value) / 100
  if (book && book.locations && book.locations.length() > 0) {
    const cfi = book.locations.cfiFromPercentage(percentage)
    if (rendition && cfi) {
      rendition.display(cfi)
    }
  }
}

function handleKeyPress(e) {
  if (e.key === 'ArrowLeft') {
    prevPage()
  } else if (e.key === 'ArrowRight') {
    nextPage()
  }
}

onMounted(() => {
  loadBook()
  window.addEventListener('keydown', handleKeyPress)
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeyPress)
  if (rendition) {
    rendition.destroy()
  }
  if (book) {
    book.destroy()
  }
})
</script>

<style scoped>
.epub-viewer {
  position: relative;
}

.epub-reader-container {
  background: #f9fafb;
  overflow: hidden;
}
</style>
