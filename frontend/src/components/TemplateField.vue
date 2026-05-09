<template>
  <div>
    <label class="block text-sm font-medium text-gray-700 mb-1">{{ label }}</label>
    <textarea
      :value="modelValue"
      @input="$emit('update:modelValue', $event.target.value)"
      rows="5"
      class="w-full px-3 py-2 border rounded-md text-sm font-mono focus:outline-none focus:ring-2 focus:ring-primary-500 resize-y"
    ></textarea>
    <p class="text-xs text-gray-500 mt-1">Go template syntax</p>
  </div>
</template>

<script setup>
defineProps({ label: String, modelValue: String })
defineEmits(['update:modelValue'])
</script>