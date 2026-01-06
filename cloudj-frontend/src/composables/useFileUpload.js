import { useSingleUpload } from "./file-upload-handling/useSingleUpload"
import { useMultipartUpload } from "./file-upload-handling/useMultipartUpload"

const SINGLE_UPLOAD_LIMIT = 100 * 1024 * 1024 // 100MB

export function useFileUpload() {
    const single = useSingleUpload()
    const multi = useMultipartUpload()
    let currentController = null

    async function uploadFile(file, onProgress) {
        if (file.size <= SINGLE_UPLOAD_LIMIT) {
            currentController = "single"
            return single.upload(file, onProgress)
        }

        currentController = "multi"
        return multi.upload(file, onProgress)
    }

    function cancelUpload() {
        if (currentController === "single") single.cancelUpload()
        else if (currentController === "multi") multi.cancelUpload()
    }

    return {
        uploadFile,
        cancelUpload,
    }
}
