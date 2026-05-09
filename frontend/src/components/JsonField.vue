<template>
  <div>
    <label class="block text-sm font-medium text-gray-700 mb-1">{{ label }}</label>
    <textarea
      :value="jsonValue"
      @input="handleInput"
      rows="3"
      class="w-full px-3 py-2 border rounded-md text-sm font-mono focus:outline-none focus:ring-2 focus:ring-primary-500 resize-y"
    ></textarea>
    <p v-if="parseError" class="text-xs text-red-600 mt-1">{{ parseError }}</p>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({ label: String, modelValue: Object })
const emit = defineEmits(['update:modelValue'])

const parseError = ref('')
const jsonValue = computed(() => JSON.stringify(props.modelValue || {}, null, 2))

function handleInput(e) {
  try {
    const parsed = JSON.parse(e.target.value)
    parseError.value = ''
    emit('update:modelValue', parsed)
  } catch {
    parseError.value = 'Invalid JSON'
  }
}
</script>