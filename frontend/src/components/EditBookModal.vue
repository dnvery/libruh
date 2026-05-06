<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50" @click.self="$emit('close')">
    <div class="bg-white rounded-lg shadow-xl w-full max-w-lg mx-4 max-h-[90vh] overflow-y-auto">
      <div class="p-6">
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-xl font-bold text-gray-800">Edit Book</h2>
          <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600 text-xl">&times;</button>
        </div>

        <form @submit.prevent="handleSave" class="space-y-4">
          <div>
            <label for="title" class="block text-sm font-medium text-gray-700 mb-1">Title</label>
            <input
              id="title"
              v-model="form.title"
              type="text"
              required
              maxlength="500"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
          </div>

          <div>
            <label for="author" class="block text-sm font-medium text-gray-700 mb-1">Author</label>
            <input
              id="author"
              v-model="form.author"
              type="text"
              required
              maxlength="500"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
          </div>

          <div>
            <label for="genre" class="block text-sm font-medium text-gray-700 mb-1">Genre</label>
            <input
              id="genre"
              v-model="form.genre"
              type="text"
              maxlength="255"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
          </div>

          <div>
            <label for="language" class="block text-sm font-medium text-gray-700 mb-1">Language</label>
            <input
              id="language"
              v-model="form.language"
              type="text"
              maxlength="10"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
          </div>

          <div>
            <label for="publicationDate" class="block text-sm font-medium text-gray-700 mb-1">Publication Date</label>
            <input
              id="publicationDate"
              v-model="form.publicationDate"
              type="date"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
          </div>

          <div>
            <label for="description" class="block text-sm font-medium text-gray-700 mb-1">Description</label>
            <textarea
              id="description"
              v-model="form.description"
              rows="4"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
            ></textarea>
          </div>

          <div v-if="saveError" class="text-red-600 text-sm bg-red-50 p-3 rounded-md">
            {{ saveError }}
          </div>

          <div class="flex gap-3 justify-end">
            <button
              type="button"
              @click="$emit('close')"
              class="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium hover:bg-gray-50"
            >
              Cancel
            </button>
            <button
              type="submit"
              :disabled="saving"
              class="px-4 py-2 bg-primary-600 text-white rounded-md text-sm font-medium hover:bg-primary-700 disabled:opacity-50"
            >
              {{ saving ? 'Saving...' : 'Save' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { bookService } from '../services/bookService'

const props = defineProps({
  book: { type: Object, required: true },
})

const emit = defineEmits(['close', 'saved'])

const form = reactive({
  title: props.book.title || '',
  author: props.book.author || '',
  genre: props.book.genre || '',
  language: props.book.language || '',
  publicationDate: props.book.publicationDate || '',
  description: props.book.description || '',
})

const saving = ref(false)
const saveError = ref('')

async function handleSave() {
  saveError.value = ''
  saving.value = true
  try {
    const { data } = await bookService.update(props.book.id, form)
    emit('saved', data)
  } catch (err) {
    if (err.response?.data?.error) {
      saveError.value = err.response.data.error
    } else {
      saveError.value = 'Failed to update book.'
    }
  } finally {
    saving.value = false
  }
}
</script>