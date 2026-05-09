import api from './api'

const configService = {
  getConfig() {
    return api.get('/config')
  },

  updateConfig(config) {
    return api.put('/config', config)
  },

  resetConfig() {
    return api.post('/config/reset')
  },

  getPresets() {
    return api.get('/config/presets')
  },

  savePreset(name) {
    return api.post('/config/presets', { name })
  },

  loadPreset(presetId) {
    return api.post(`/config/presets/${presetId}/load`)
  },

  deletePreset(presetId) {
    return api.delete(`/config/presets/${presetId}`)
  },
}

export default configService