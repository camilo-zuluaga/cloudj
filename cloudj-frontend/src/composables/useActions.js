import { useAuthStore } from "@/stores/useAuthStore"
import axios from "axios"

export function useActions() {
    const auth = useAuthStore()

    async function deleteFile(id) {
        try {
            await axios.delete(`/api/files/${id}`, {
                headers: {
                    Authorization: `Bearer ${auth.accessToken}`,
                },
            })
        } catch (err) {
            throw new Error(err)
        }
    }

    async function downloadFile(key, filename) {
        const getKey = key.slice(key.lastIndexOf("/") + 1)
        try {
            const response = await axios.get(`/api/files/view/${getKey}`, {
                params: {
                    filename,
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

    return {
        deleteFile,
        downloadFile,
    }
}
