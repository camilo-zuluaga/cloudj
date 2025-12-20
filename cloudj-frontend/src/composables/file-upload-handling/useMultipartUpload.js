import { useAuthStore } from "@/stores/useAuthStore"
import axios from "axios"

export function useMultipartUpload() {
    let uploadState = {
        file: null,
        key: null,
        uploadId: null,
        parts: [],
        currentPart: 0,
        uploadedParts: [],
        uploadedBytes: 0,
    }
    const auth = useAuthStore()

    async function upload(file, onProgress) {
        uploadState.file = file
        uploadState.currentPart = 0
        uploadState.uploadedParts = []
        uploadState.uploadedBytes = 0
        const { id, key } = await startMultipartUpload(file.name, file.type)
        uploadState.uploadId = id
        uploadState.key = key

        const partSize = 100 * 1024 * 1024 // 100 MB
        uploadState.parts = prepareParts(file, partSize)

        try {
            await uploadParts(onProgress)
            await saveMetadata(key, file.name, file.type, file.size)
            return {
                success: true,
                key,
                message: "File uploaded successfully",
                fileData: {
                    fileName: file.name,
                    fileSize: file.size,
                    uploadedAt: new Date().toISOString().slice(0, 10),
                },
            }
        } catch (err) {
            throw new Error(err)
        }
    }

    function prepareParts(file, partSize) {
        const parts = []
        for (let i = 0; i < file.size; i += partSize) {
            parts.push({
                start: i,
                end: Math.min(i + partSize, file.size),
            })
        }

        return parts
    }

    async function startMultipartUpload(fileName, contentType) {
        try {
            const response = await axios.post("/api/files/start-multipart", null, {
                params: {
                    fileName,
                    contentType,
                },
                headers: {
                    Authorization: `Bearer ${auth.accessToken}`,
                },
            })

            return response.data
        } catch (err) {
            throw new Error(err)
        }
    }

    async function uploadParts(onProgress) {
        while (uploadState.currentPart < uploadState.parts.length) {
            const part = uploadState.parts[uploadState.currentPart]
            const partFile = uploadState.file.slice(part.start, part.end)

            const { url } = await getPresignedUrlForPart(
                uploadState.key,
                uploadState.uploadId,
                uploadState.currentPart + 1,
            )

            const uploadPartResponse = await uploadPart(url, partFile, onProgress)

            if (uploadPartResponse.status === 200) {
                const eTag =
                    uploadPartResponse.headers.etag || uploadPartResponse.headers.get?.("ETag")
                uploadState.uploadedParts.push({
                    partNumber: uploadState.currentPart + 1,
                    eTag: eTag,
                })

                uploadState.uploadedBytes += part.end - part.start
            }

            uploadState.currentPart++

            if (uploadState.currentPart === uploadState.parts.length) {
                await completeMultiPartUpload(
                    uploadState.key,
                    uploadState.uploadId,
                    uploadState.uploadedParts,
                )
            }
        }
    }

    async function uploadPart(url, part, onProgress) {
        return axios.put(url, part, {
            onUploadProgress: (progressEvent) => {
                const currentPartBytes = progressEvent.loaded
                const totalUploadedBytes = uploadState.uploadedBytes + currentPartBytes
                const percentage = Math.round((totalUploadedBytes / uploadState.file.size) * 100)

                onProgress?.(percentage)
            },
        })
    }

    async function getPresignedUrlForPart(key, uploadId, currentPart) {
        try {
            const response = await axios.post(
                `/api/files/pre-signed-part`,
                {
                    key,
                    uploadId,
                    currentPart,
                },
                {
                    headers: {
                        Authorization: `Bearer ${auth.accessToken}`,
                    },
                },
            )
            return response.data
        } catch (err) {
            throw new Error(err)
        }
    }

    async function saveMetadata(keyName, fileName, contentType, fileSize) {
        try {
            await axios.post(
                "/api/files/complete-single-upload",
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
        } catch (err) {
            throw new Error("Could not save metadata")
        }
    }

    async function completeMultiPartUpload(key, uploadId, uploadedParts) {
        try {
            const response = await axios.post(
                `/api/files/complete-multipart-upload`,
                {
                    key,
                    uploadId,
                    parts: uploadedParts,
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
            throw new Error(err)
        }
    }

    return {
        upload,
    }
}
