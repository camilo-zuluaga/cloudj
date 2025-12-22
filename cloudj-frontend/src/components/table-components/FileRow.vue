<script setup>
import { ref } from "vue"
import { toast } from "vue-sonner"
import { VProgressLinear } from "vuetify/components"

import { useActions } from "@/composables/useActions"
import { useAuthStore } from "@/stores/useAuthStore"

import ArrowDownIcon from "@/icons/ArrowDownIcon.vue"
import DeleteIcon from "@/icons/DeleteIcon.vue"
import LinkIcon from "@/icons/LinkIcon.vue"

const emit = defineEmits(["deleteResponse"])

const props = defineProps({
    id: [Number, String],
    s3Key: String,
    fileName: String,
    fileExtension: String,
    size: String,
    date: String,
    isUploading: Boolean,
    progress: Number,
})

const { deleteFile, downloadFile } = useActions()
const isDeleting = ref(false)

async function downloadFileByS3Key() {
    const response = await downloadFile(props.s3Key, props.fileName)
    window.open(response.url, "_self")
}

async function deleteFileById() {
    if (isDeleting.value) return

    const deletePromise = async () => {
        try {
            isDeleting.value = true
            await deleteFile(props.id)
            emit("deleteResponse", { status: "success", id: props.id })
        } catch (err) {
            emit("deleteResponse", { status: "error", id: props.id, err })
        } finally {
            isDeleting.value = false
        }
    }

    toast.promise(deletePromise(), {
        loading: "Deleting file...",
        success: (data) => {
            return `${props.fileName} has been deleted`
        },
        error: (data) => "Error",
    })
}
</script>

<template>
    <tr>
        <td>{{ fileName }}</td>
        <td style="color: #373737">{{ fileExtension }}</td>
        <td style="color: #373737; text-align: right">{{ size }}</td>
        <td style="color: #373737">{{ date }}</td>
        <td style="color: #373737" class="btn-td">
            <div v-if="isUploading" class="loader-container">
                <v-progress-linear :model-value="progress" style="width: 95px"></v-progress-linear>
                <div>{{ progress }}%</div>
            </div>
            <div v-else class="btn-container">
                <ArrowDownIcon class="download-btn" @click="downloadFileByS3Key" />
                <DeleteIcon class="remove-btn" @click="deleteFileById" />
                <LinkIcon class="link-btn" />
            </div>
        </td>
    </tr>
</template>

<style scoped>
td {
    border-bottom: 1px solid #373737;
    text-align: left;
    padding: 15px;
}

.btn-container button {
    cursor: pointer;
    display: flex;
    justify-content: center;
    align-content: center;
    align-items: center;
    border: none;
}

.download-btn {
    cursor: pointer;
    width: 30px;
    transition: 0.1s ease-in-out;
}

.download-btn:hover {
    color: #6bb073;
}

.remove-btn {
    cursor: pointer;
    width: 30px;
    transition: 0.1s ease-in-out;
}

.remove-btn:hover {
    color: #ab3e3e;
}

.link-btn {
    cursor: pointer;
    width: 30px;
    transition: 0.1s ease-in-out;
}

.link-btn:hover {
    color: #8b5d9e;
}

.btn-container {
    display: flex;
    justify-content: center;
    gap: 8px;
}

.loader-container {
    color: #606060;
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 15px;
}

.loader {
    width: 18px;
    height: 18px;
    border: 2px solid #ab3e3e;
    border-bottom-color: transparent;
    border-radius: 50%;
    display: inline-block;
    box-sizing: border-box;
    animation: rotation 1s linear infinite;
}

@keyframes rotation {
    0% {
        transform: rotate(0deg);
    }
    100% {
        transform: rotate(360deg);
    }
}
</style>
