import { useAuthStore } from "@/stores/useAuthStore"
import axios from "axios"

export function useLoadUserFiles() {
    const auth = useAuthStore()

    async function getFiles() {
        try {
            const response = await axios.get("/api/files", {
                headers: {
                    Authorization: `Bearer ${auth.accessToken}`,
                },
            })

            if (response.status === 401) {
                await auth.refreshToken()
                return await getFiles()
            }

            return response.data
        } catch (err) {
            throw new Error(err)
        }
    }

    return { getFiles }
}
