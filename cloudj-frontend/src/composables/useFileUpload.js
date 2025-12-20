import { useSingleUpload } from "./file-upload-handling/useSingleUpload"
import { useMultipartUpload } from "./file-upload-handling/useMultipartUpload"

const SINGLE_UPLOAD_LIMIT = 100 * 1024 * 1024 // 100MB

export function useFileUpload() {
    const single = useSingleUpload()
    const multi = useMultipartUpload()

    async function uploadFile(file, onProgress) {
        if (file.size <= SINGLE_UPLOAD_LIMIT) {
            return single.upload(file, onProgress)
        }

        return multi.upload(file, onProgress)
    }

    return {
        uploadFile,
    }
}
