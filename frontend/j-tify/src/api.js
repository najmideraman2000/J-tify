export const fetchJPopSongs = async () => {
    try {
        const response = await fetch("http://localhost:8080/top-jpop");
        if (!response.ok) throw new Error("Failed to fetch songs");
        return await response.json();
    } catch (error) {
        console.error("Error fetching songs:", error);
        return [];
    }
};
