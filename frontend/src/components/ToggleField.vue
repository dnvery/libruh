<template>
  <div class="flex items-center gap-2">
    <input
      type="checkbox"
      :checked="modelValue"
      @change="$emit('update:modelValue', $event.target.checked)"
      class="w-4 h-4 rounded border-gray-300 text-primary-600 focus:ring-primary-500"
    />
    <label class="text-sm text-gray-700">{{ label }}</label>
  </div>
</template>

<script setup>
defineProps({ label: String, modelValue: Boolean })
defineEmits(['update:modelValue'])
</script>