<script setup>
import { ref } from "vue"
import { useFileUpload } from "@/composables/useFileUpload"

const emit = defineEmits(["file"])

const fileInput = ref(null)
const { uploadFile } = useFileUpload()

const openFileDialog = () => {
    fileInput.value.click()
}

const handleFileSelection = async (event) => {
    const file = event.target.files[0]
    if (!file) return
    emit("file", file)
}
</script>

<template>
    <input type="file" ref="fileInput" style="display: none" @change="handleFileSelection" />
    <button class="upload-btn" @click="openFileDialog">Upload File</button>
</template>

<style scoped>
.upload-btn {
    font-size: 16px;
    color: black;
    position: absolute;
    bottom: 20px;
    right: 20px;
    z-index: 1000;
    padding: 15px 25px;
    background-color: white;
    border: none;
    cursor: pointer;
    transition: ease 0.3s;
}

.upload-btn:hover {
    text-decoration: underline;
}
</style>
