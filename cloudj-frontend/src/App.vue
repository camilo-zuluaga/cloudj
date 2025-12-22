<script setup>
import { Toaster } from "vue-sonner"
import "vue-sonner/style.css"
</script>

<template>
    <Toaster
        position="bottom-center"
        theme="dark"
        :toastOptions="{ style: { background: '#0d0d0d' } }"
    />
    <router-view></router-view>
</template>
