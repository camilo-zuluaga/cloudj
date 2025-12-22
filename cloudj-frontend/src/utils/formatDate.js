export function formatDate(dateToFormat) {
    const date = new Date(dateToFormat)

    const options = {
        year: "numeric",
        month: "short",
        day: "numeric",
        hour: "2-digit",
        minute: "2-digit",
    }

    return date.toLocaleDateString("en-us", options)
}
