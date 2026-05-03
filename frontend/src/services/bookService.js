import api from './api'

export const bookService = {
  list(page = 0, size = 20) {
    return api.get('/books', { params: { page, size } })
  },

  get(id) {
    return api.get(`/books/${id}`)
  },

  upload(formData) {
    return api.post('/books', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  },

  update(id, data) {
    return api.put(`/books/${id}`, data)
  },

  delete(id) {
    return api.delete(`/books/${id}`)
  },

  search(query, field = 'title') {
    return api.get('/books/search', { params: { query, field } })
  },

  downloadEpub(id) {
    return api.get(`/books/${id}/download/epub`, { responseType: 'blob' })
  },

  downloadAzw8(id) {
    return api.get(`/books/${id}/download/azw8`, { responseType: 'blob' })
  },
}