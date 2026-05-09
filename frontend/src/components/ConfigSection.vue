<template>
  <div class="bg-white border rounded-lg p-4">
    <h3 class="text-base font-semibold text-gray-900 mb-3">{{ title }}</h3>
    <slot />
  </div>
</template>

<script setup>
defineProps({ title: String })
</script>