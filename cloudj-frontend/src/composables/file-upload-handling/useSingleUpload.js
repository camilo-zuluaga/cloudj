import axios from "axios"
import { useAuthStore } from "@/stores/useAuthStore"

export function useSingleUpload() {
    const auth = useAuthStore()
    let currentController = null

    async function getPresignedUrl(fileName, contentType) {
        try {
            const response = await axios.post("/api/files/presigned-url", null, {
                params: {
                    fileName,
                    contentType,
                },
                headers: {
                    Authorization: `Bearer ${auth.accessToken}`,
                    "Content-Type": "application/json",
                },
            })

            return response.data
        } catch (err) {
            throw new Error("Failed to get presigned URL")
        }
    }

    async function uploadFileToS3(presignedUrl, file, onProgress) {
        currentController = new AbortController()

        try {
            await axios.put(presignedUrl, file, {
                signal: currentController.signal,
                headers: {
                    "Content-Type": file.type,
                    "x-amz-meta-file-name": file.name,
                },
                onUploadProgress: (progressEvent) => {
                    if (!progressEvent.total) return
                    const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total)
                    onProgress?.(percent)
                },
            })

            currentController = null
        } catch (err) {
            currentController = null
            if (axios.isCancel(err)) {
                throw new Error("Upload cancelled")
            }
            throw new Error("Failed to upload file to S3")
        }
    }

    async function completeSinglePartUpload(keyName, fileName, contentType, fileSize) {
        try {
            const response = await axios.post(
                "/api/files/complete-upload",
                {
                    keyName,
                    fileName,
                    contentType,
                    fileSize,
                },
                {
                    headers: {
                        Authorization: `Bearer ${auth.accessToken}`,
                        "Content-Type": "application/json",
                    },
                },
            )

            return response.data
        } catch (err) {
            throw new Error("Could not save metadata")
        }
    }

    async function upload(file, onProgress) {
        try {
            const { key, url } = await getPresignedUrl(file.name, file.type)

            await uploadFileToS3(url, file, onProgress)
            const response = await completeSinglePartUpload(key, file.name, file.type, file.size)

            return {
                success: true,
                key,
                message: "File uploaded successfully",
                fileData: {
                    id: response.message.id,
                    s3Key: response.message.s3Key,
                    fileName: file.name,
                    fileSize: file.size,
                    uploadedAt: response.timestamp,
                },
            }
        } catch (err) {
            return { success: false, error: err.message }
        }
    }

    function cancelUpload() {
        if (currentController) {
            currentController.abort()
            currentController = null
        }
    }

    return {
        upload,
        cancelUpload,
    }
}
