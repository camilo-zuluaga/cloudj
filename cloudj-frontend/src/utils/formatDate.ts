export function formatDate(dateToFormat: string | Date): string {
    const date = new Date(dateToFormat)

    const options: Intl.DateTimeFormatOptions = {
        year: "numeric",
        month: "short",
        day: "numeric",
        hour: "2-digit",
        minute: "2-digit",
    }

    return date.toLocaleDateString("en-us", options)
}
