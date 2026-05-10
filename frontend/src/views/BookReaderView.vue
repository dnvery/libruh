<template>
  <EpubReader :book-id="route.params.id" />
</template>

<script setup>
import { useRoute } from 'vue-router'
import EpubReader from '../components/EpubReader.vue'

const route = useRoute()
</script>
