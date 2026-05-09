<template>
  <div>
    <label class="block text-sm font-medium text-gray-700 mb-1">{{ label }}</label>
    <div class="space-y-1">
      <div
        v-for="(item, index) in modelValue"
        :key="index"
        class="flex gap-2"
      >
        <input
          :value="item"
          @input="updateItem(index, $event.target.value)"
          class="flex-1 px-3 py-1.5 border rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
        />
        <button
          @click="removeItem(index)"
          class="px-2 py-1.5 text-red-600 hover:bg-red-50 rounded"
        >×</button>
      </div>
      <button
        @click="addItem"
        class="text-sm text-primary-600 hover:text-primary-800"
      >+ Add</button>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({ label: String, modelValue: Array })
const emit = defineEmits(['update:modelValue'])

function updateItem(index, value) {
  const arr = [...props.modelValue]
  arr[index] = value
  emit('update:modelValue', arr)
}

function addItem() {
  emit('update:modelValue', [...props.modelValue, ''])
}

function removeItem(index) {
  const arr = [...props.modelValue]
  arr.splice(index, 1)
  emit('update:modelValue', arr)
}
</script>