<template>
  <div>
    <router-link to="/books" class="text-primary-600 hover:text-primary-800 text-sm mb-4 inline-block">&larr; Back to books</router-link>

    <div class="bg-white rounded-lg shadow-md p-6 mt-2 max-w-xl mx-auto">
      <h1 class="text-2xl font-bold text-gray-800 mb-6">Upload Book</h1>

      <form @submit.prevent="handleUpload" class="space-y-4">
        <div>
          <label for="file" class="block text-sm font-medium text-gray-700 mb-1">FB2 File</label>
          <input
            id="file"
            ref="fileInput"
            type="file"
            accept=".fb2,.fb2.zip"
            required
            @change="onFileChange"
            class="w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-md file:border-0 file:text-sm file:font-medium file:bg-primary-50 file:text-primary-700 hover:file:bg-primary-100"
          />
          <p class="text-xs text-gray-400 mt-1">Accepted: .fb2, .fb2.zip</p>
        </div>

        <div v-if="error" class="text-red-600 text-sm bg-red-50 p-3 rounded-md">
          {{ error }}
        </div>

        <div v-if="uploadSuccess" class="text-green-700 text-sm bg-green-50 p-3 rounded-md">
          Book uploaded successfully! Metadata is being extracted and conversion is in progress.
          <router-link :to="`/books/${uploadedBookId}`" class="underline font-medium">View book</router-link>
        </div>

        <button
          type="submit"
          :disabled="loading || !selectedFile"
          class="w-full py-2 px-4 bg-primary-600 text-white font-medium rounded-md hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {{ loading ? 'Uploading...' : 'Upload' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { bookService } from '../services/bookService'

const selectedFile = ref(null)
const loading = ref(false)
const error = ref('')
const uploadSuccess = ref(false)
const uploadedBookId = ref(null)

function onFileChange(e) {
  selectedFile.value = e.target.files[0]
  error.value = ''
  uploadSuccess.value = false
}

async function handleUpload() {
  if (!selectedFile.value) return

  loading.value = true
  error.value = ''
  uploadSuccess.value = false

  const formData = new FormData()
  formData.append('file', selectedFile.value)

  try {
    const { data } = await bookService.upload(formData)
    uploadedBookId.value = data.id
    uploadSuccess.value = true
    selectedFile.value = null
  } catch (err) {
    if (err.response?.data?.error) {
      error.value = err.response.data.error
    } else {
      error.value = 'Upload failed. Please try again.'
    }
  } finally {
    loading.value = false
  }
}
</script>