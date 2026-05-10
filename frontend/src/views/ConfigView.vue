<template>
  <div class="max-w-4xl mx-auto px-4 py-8">
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold text-gray-900">Conversion Config</h1>
      <div class="flex items-center gap-2">
        <button
          @click="resetConfig"
          :disabled="loading"
          class="px-3 py-1.5 text-sm bg-red-50 text-red-600 rounded-md hover:bg-red-100 disabled:opacity-50"
        >
          Reset to Defaults
        </button>
        <button
          @click="saveConfig"
          :disabled="loading"
          class="px-4 py-1.5 text-sm bg-primary-600 text-white rounded-md hover:bg-primary-700 disabled:opacity-50"
        >
          {{ loading ? 'Saving...' : 'Save Config' }}
        </button>
      </div>
    </div>

    <div v-if="loading && !config" class="text-center py-8 text-gray-500">Loading config...</div>

    <div v-if="error" class="mb-4 p-3 bg-red-50 text-red-700 rounded-md text-sm">{{ error }}</div>
    <div v-if="success" class="mb-4 p-3 bg-green-50 text-green-700 rounded-md text-sm">{{ success }}</div>

    <div v-if="config" class="space-y-4">
      <ConfigSection title="General">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <ToggleField label="Fix ZIP" v-model="config.document.fixZip" />
          <ToggleField label="Open from Cover" v-model="config.document.openFromCover" />
          <SelectField label="TOC Type" v-model="config.document.tocType" :options="['normal', 'flat', 'combined']" />
          <ToggleField label="Filename Transliterate" v-model="config.document.fileNameTransliterate" />
          <ToggleField label="Insert Soft Hyphen" v-model="config.document.insertSoftHyphen" />
          <div class="md:col-span-2">
            <TemplateField label="Output Name Template" v-model="config.document.outputNameTemplate" />
          </div>
        </div>
      </ConfigSection>

      <ConfigSection title="Images">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <ToggleField label="Use Broken Images" v-model="config.document.images.useBroken" />
          <ToggleField label="Remove Transparency" v-model="config.document.images.removeTransparency" />
          <NumberField label="Scale Factor" v-model="config.document.images.scaleFactor" :min="1" :max="4" />
          <ToggleField label="Optimize" v-model="config.document.images.optimize" />
          <NumberField label="JPEG Quality Level" v-model="config.document.images.jpegQualityLevel" :min="1" :max="100" />
        </div>

        <div class="mt-4 p-3 bg-gray-50 rounded-md">
          <h4 class="text-sm font-medium text-gray-700 mb-3">Screen</h4>
          <div class="grid grid-cols-2 gap-4">
            <NumberField label="Width" v-model="config.document.images.screen.width" />
            <NumberField label="Height" v-model="config.document.images.screen.height" />
          </div>
        </div>

        <div class="mt-4 p-3 bg-gray-50 rounded-md">
          <h4 class="text-sm font-medium text-gray-700 mb-3">Cover</h4>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <ToggleField label="Generate Cover" v-model="config.document.images.cover.generate" />
            <SelectField label="Cover Resize" v-model="config.document.images.cover.resize" :options="['stretch', 'fit', 'original']" />
          </div>
        </div>
      </ConfigSection>

      <ConfigSection title="Footnotes">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <SelectField label="Mode" v-model="config.document.footnotes.mode" :options="['default', 'chapter', 'end']" />
          <TextField label="Backlinks" v-model="config.document.footnotes.backlinks" />
          <TextField label="More Paragraphs" v-model="config.document.footnotes.moreParagraphs" />
          <div class="md:col-span-2">
            <ListField label="Bodies" v-model="config.document.footnotes.bodies" />
          </div>
          <div class="md:col-span-2">
            <TemplateField label="Label Template" v-model="config.document.footnotes.labelTemplate" />
          </div>
        </div>
      </ConfigSection>

      <ConfigSection title="Annotation">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <ToggleField label="Enable" v-model="config.document.annotation.enable" />
          <TextField label="Title" v-model="config.document.annotation.title" />
          <ToggleField label="Show in TOC" v-model="config.document.annotation.inToc" />
        </div>
      </ConfigSection>

      <ConfigSection title="TOC Page">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <SelectField label="Placement" v-model="config.document.tocPage.placement" :options="['none', 'before-content', 'after-content']" />
          <ToggleField label="Include Chapters Without Title" v-model="config.document.tocPage.includeChaptersWithoutTitle" />
          <div class="md:col-span-2">
            <TemplateField label="Authors Template" v-model="config.document.tocPage.authorsTemplate" />
          </div>
        </div>
      </ConfigSection>

      <ConfigSection title="Metainformation">
        <div class="grid grid-cols-1 gap-4">
          <ToggleField label="Transliterate" v-model="config.document.metainformation.transliterate" />
          <TemplateField label="Title Template" v-model="config.document.metainformation.titleTemplate" />
          <TemplateField label="Creator Name Template" v-model="config.document.metainformation.creatorNameTemplate" />
        </div>
      </ConfigSection>

      <ConfigSection title="Vignettes">
        <p class="text-xs text-gray-500 mb-3">Edit vignette definitions as JSON objects. Leave empty for no vignettes.</p>
        <div class="space-y-3">
          <JsonField label="Book" v-model="config.document.vignettes.book" />
          <JsonField label="Chapter" v-model="config.document.vignettes.chapter" />
          <JsonField label="Section" v-model="config.document.vignettes.section" />
        </div>
      </ConfigSection>

      <ConfigSection title="Dropcaps">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <ToggleField label="Enable" v-model="config.document.dropcaps.enable" />
          <TextField label="Ignore Symbols" v-model="config.document.dropcaps.ignoreSymbols" />
        </div>
      </ConfigSection>

      <ConfigSection title="Page Map">
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <ToggleField label="Enable" v-model="config.document.pageMap.enable" />
          <NumberField label="Size" v-model="config.document.pageMap.size" :min="1000" :max="10000" />
          <ToggleField label="Adobe DE" v-model="config.document.pageMap.adobeDe" />
        </div>
      </ConfigSection>

      <ConfigSection title="Text Transformations">
        <div class="space-y-4">
          <div class="p-3 bg-gray-50 rounded-md">
            <h4 class="text-sm font-medium text-gray-700 mb-3">Speech</h4>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
              <ToggleField label="Enable" v-model="config.document.textTransformations.speech.enable" />
              <TextField label="From" v-model="config.document.textTransformations.speech.from" />
              <TextField label="To" v-model="config.document.textTransformations.speech.to" />
            </div>
          </div>
          <div class="p-3 bg-gray-50 rounded-md">
            <h4 class="text-sm font-medium text-gray-700 mb-3">Dashes</h4>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
              <ToggleField label="Enable" v-model="config.document.textTransformations.dashes.enable" />
              <TextField label="From" v-model="config.document.textTransformations.dashes.from" />
              <TextField label="To" v-model="config.document.textTransformations.dashes.to" />
            </div>
          </div>
          <div class="p-3 bg-gray-50 rounded-md">
            <h4 class="text-sm font-medium text-gray-700 mb-3">Dialogue</h4>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
              <ToggleField label="Enable" v-model="config.document.textTransformations.dialogue.enable" />
              <TextField label="From" v-model="config.document.textTransformations.dialogue.from" />
              <TextField label="To" v-model="config.document.textTransformations.dialogue.to" />
            </div>
          </div>
        </div>
      </ConfigSection>

      <ConfigSection title="Logging">
        <div class="space-y-4">
          <div class="p-3 bg-gray-50 rounded-md">
            <h4 class="text-sm font-medium text-gray-700 mb-3">File</h4>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
              <SelectField label="Level" v-model="config.logging.file.level" :options="['debug', 'info', 'warn', 'error']" />
              <TextField label="Destination" v-model="config.logging.file.destination" />
              <SelectField label="Mode" v-model="config.logging.file.mode" :options="['overwrite', 'append']" />
            </div>
          </div>
          <div class="p-3 bg-gray-50 rounded-md">
            <h4 class="text-sm font-medium text-gray-700 mb-3">Console</h4>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <SelectField label="Level" v-model="config.logging.console.level" :options="['normal', 'debug', 'quiet']" />
            </div>
          </div>
        </div>
      </ConfigSection>

      <ConfigSection title="Reporting">
        <TextField label="Destination" v-model="config.reporting.destination" />
      </ConfigSection>
    </div>

    <div class="mt-8 border-t pt-6">
      <h2 class="text-lg font-semibold text-gray-900 mb-4">Presets</h2>
      <div class="flex items-end gap-3 mb-4">
        <div class="flex-1">
          <label class="block text-sm font-medium text-gray-700 mb-1">Save Current Config as Preset</label>
          <input
            v-model="presetName"
            type="text"
            placeholder="Preset name..."
            class="w-full px-3 py-2 border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
          />
        </div>
        <button
          @click="savePreset"
          :disabled="!presetName.trim() || savingPreset"
          class="px-4 py-2 text-sm bg-primary-600 text-white rounded-md hover:bg-primary-700 disabled:opacity-50"
        >
          {{ savingPreset ? 'Saving...' : 'Save Preset' }}
        </button>
      </div>

      <div v-if="presets.length === 0" class="text-sm text-gray-500">No presets saved yet.</div>
      <div v-else class="space-y-2">
        <div
          v-for="preset in presets"
          :key="preset.id"
          class="flex items-center justify-between p-3 bg-gray-50 rounded-md"
        >
          <div>
            <span class="font-medium text-gray-900">{{ preset.name }}</span>
            <span class="text-xs text-gray-500 ml-2">{{ formatDate(preset.createdAt) }}</span>
          </div>
          <div class="flex gap-2">
            <button
              @click="loadPreset(preset.id)"
              class="px-3 py-1 text-sm bg-primary-50 text-primary-700 rounded hover:bg-primary-100"
            >
              Load
            </button>
            <button
              @click="deletePreset(preset.id)"
              class="px-3 py-1 text-sm bg-red-50 text-red-700 rounded hover:bg-red-100"
            >
              Delete
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import configService from '../services/configService'
import ConfigSection from '../components/ConfigSection.vue'
import ToggleField from '../components/ToggleField.vue'
import SelectField from '../components/SelectField.vue'
import NumberField from '../components/NumberField.vue'
import TextField from '../components/TextField.vue'
import TemplateField from '../components/TemplateField.vue'
import ListField from '../components/ListField.vue'
import JsonField from '../components/JsonField.vue'

const config = ref(null)
const loading = ref(false)
const error = ref('')
const success = ref('')
const presets = ref([])
const presetName = ref('')
const savingPreset = ref(false)

function clearMessages() {
  error.value = ''
  success.value = ''
}

async function loadConfig() {
  loading.value = true
  clearMessages()
  try {
    const { data } = await configService.getConfig()
    config.value = data
  } catch (e) {
    error.value = 'Failed to load config'
  } finally {
    loading.value = false
  }
}

async function loadPresets() {
  try {
    const { data } = await configService.getPresets()
    presets.value = data
  } catch {
    // ignore
  }
}

async function saveConfig() {
  loading.value = true
  clearMessages()
  try {
    const { data } = await configService.updateConfig(config.value)
    config.value = data
    success.value = 'Config saved successfully'
  } catch (e) {
    error.value = 'Failed to save config'
  } finally {
    loading.value = false
  }
}

async function resetConfig() {
  if (!confirm('Reset all settings to defaults? This cannot be undone.')) return
  loading.value = true
  clearMessages()
  try {
    const { data } = await configService.resetConfig()
    config.value = data
    success.value = 'Config reset to defaults'
  } catch (e) {
    error.value = 'Failed to reset config'
  } finally {
    loading.value = false
  }
}

async function savePreset() {
  if (!presetName.value.trim()) return
  savingPreset.value = true
  clearMessages()
  try {
    await configService.savePreset(presetName.value.trim())
    presetName.value = ''
    success.value = 'Preset saved'
    await loadPresets()
  } catch (e) {
    error.value = 'Failed to save preset'
  } finally {
    savingPreset.value = false
  }
}

async function loadPreset(id) {
  if (!confirm('Load this preset? Current config will be replaced.')) return
  loading.value = true
  clearMessages()
  try {
    const { data } = await configService.loadPreset(id)
    config.value = data
    success.value = 'Preset loaded'
  } catch (e) {
    error.value = 'Failed to load preset'
  } finally {
    loading.value = false
  }
}

async function deletePreset(id) {
  if (!confirm('Delete this preset?')) return
  try {
    await configService.deletePreset(id)
    success.value = 'Preset deleted'
    await loadPresets()
  } catch (e) {
    error.value = 'Failed to delete preset'
  }
}

function formatDate(dateStr) {
  return new Date(dateStr).toLocaleDateString()
}

onMounted(() => {
  loadConfig()
  loadPresets()
})
</script>