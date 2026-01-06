<script setup>
import { ref, onMounted } from "vue"
import { useRouter } from "vue-router"

import { Table, FileRow } from "@/components/table-components"
import UploadButton from "@/components/UploadButton.vue"
import FileIcon from "@/icons/FileIcon.vue"

import { toast } from "vue-sonner"
import { useAuthStore } from "@/stores/useAuthStore"
import { useLoadUserFiles } from "@/composables/useLoadUserFiles"
import { formatFileSize } from "@/utils/formatFileSize"
import { formatDate } from "@/utils/formatDate"
import { useFileUpload } from "@/composables/useFileUpload"

const router = useRouter()
const auth = useAuthStore()
const { getFiles } = useLoadUserFiles()
const { uploadFile, cancelUpload } = useFileUpload()

const fileRows = ref([])
const username = ref(null)

function fileExtension(file) {
    return file.fileName.slice(file.fileName.lastIndexOf(".") + 1, file.fileName.length)
}

onMounted(async () => {
    username.value = auth.username

    const data = await getFiles()
    for (const file of data) {
        handleFileUploaded({
            id: file.id,
            fileName: file.originalFilename,
            s3Key: file.s3Key,
            fileExtension: file.content_type,
            fileSize: file.fileSize,
            uploadedAt: formatDate(file.uploadedAt),
        })
    }
})

function handleFileUploaded(fileData) {
    const formattedFileSize = formatFileSize(fileData.fileSize)

    fileRows.value.push({
        id: fileData.id,
        fileName: fileData.fileName,
        s3Key: fileData.s3Key,
        fileExtension: fileExtension(fileData),
        size: formattedFileSize,
        uploadedAt: formatDate(fileData.uploadedAt),
    })
}

async function handleFileUpload(file) {
    const tempId = insertTemporalRow(file)
    const fileRow = fileRows.value.find((f) => f.id === tempId)

    const response = await uploadFile(file, (progress) => {
        if (fileRow) {
            fileRow.progress = progress
        }
    })

    if (response.success) {
        const fileData = response.fileData
        if (fileRow) {
            fileRow.id = fileData.id
            fileRow.s3Key = fileData.s3Key
            fileRow.uploadedAt = formatDate(fileData.uploadedAt)
            fileRow.uploading = false
            fileRow.progress = 100
        }
        toast.success("File uploaded")
    }
}

function handleDelete({ status, id, error }) {
    if (status === "success") {
        fileRows.value = fileRows.value.filter((file) => file.id != id)
        return
    }
}

function insertTemporalRow(fileTemporalData) {
    const formattedFileSize = formatFileSize(fileTemporalData.size)
    const fileExtension = fileTemporalData.name.slice(
        fileTemporalData.name.lastIndexOf(".") + 1,
        fileTemporalData.name.length,
    )
    const tempId = crypto.randomUUID()

    fileRows.value.push({
        id: tempId,
        fileName: fileTemporalData.name,
        s3Key: null,
        fileExtension: fileExtension,
        size: formattedFileSize,
        uploadedAt: "-",
        uploading: true,
        progress: 0,
    })

    return tempId
}

function handleCancel(id) {
    fileRows.value = fileRows.value.filter((file) => file.id != id)
    cancelUpload()
    toast.success("File upload cancelled")
}

async function handleLogout() {
    await auth.logout()
    await router.push("/login")
}
</script>

<template>
    <div class="dashboard-wrapper">
        <header>
            <span>cloudj</span>
            <span style="color: #373737">{{ username }}</span>
            <span @click="handleLogout" class="logout">logout</span>
        </header>

        <div class="yourfiles-title">
            <FileIcon />
            <h1>Your Files</h1>
        </div>

        <Table>
            <FileRow
                v-for="file in fileRows"
                :key="file.id"
                :id="file.id"
                :fileName="file.fileName"
                :s3Key="file.s3Key"
                :fileExtension="file.fileExtension"
                :size="file.size"
                :date="file.uploadedAt"
                :isUploading="file.uploading"
                :progress="file.progress"
                @deleteResponse="handleDelete"
                @cancelUpload="handleCancel"
            />
        </Table>
        <div class="empty-files" v-if="fileRows.length === 0">No files uploaded yet</div>
        <UploadButton @file="handleFileUpload" />
    </div>
</template>

<style scoped>
.dashboard-wrapper {
    position: relative;
    min-height: 100vh;
    border-right: 1px solid #373737;
    border-left: 1px solid #373737;
    display: flex;
    flex-direction: column;
}

header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #373737;
    padding: 15px;
    font-size: 16px;
    color: white;
}

h1 {
    color: white;
    font-size: 16px;
}

.empty-files {
    font-size: 16px;
    display: flex;
    justify-content: center;
    align-items: center;
    flex: 1;
    color: #666;
}

.yourfiles-title {
    margin: 15px;
    gap: 15px;
    display: flex;
    align-items: center;
}

.logout {
    color: #373737;
}

.logout:hover {
    cursor: pointer;
    color: white;
    text-decoration: underline;
}
</style>
