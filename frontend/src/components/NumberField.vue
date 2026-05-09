<template>
  <div>
    <label class="block text-sm font-medium text-gray-700 mb-1">{{ label }}</label>
    <input
      type="number"
      :value="modelValue"
      @input="$emit('update:modelValue', Number($event.target.value))"
      :min="min"
      :max="max"
      class="w-full px-3 py-2 border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
    />
  </div>
</template>

<script setup>
defineProps({ label: String, modelValue: Number, min: Number, max: Number })
defineEmits(['update:modelValue'])
</script>