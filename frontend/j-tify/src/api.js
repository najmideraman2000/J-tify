export const fetchJPopSongs = async (timeRange = "medium_term") => {
    try {
        const response = await fetch(`http://localhost:8080/top-jpop?time_range=${timeRange}`);
        if (!response.ok) throw new Error("Failed to fetch songs");
        return await response.json();
    } catch (error) {
        console.error("Error fetching songs:", error);
        return [];
    }
};

export const saveTrack = async (trackId) => {
    const accessToken = localStorage.getItem("spotifyAccessToken");

    if (!accessToken) {
        alert("You need to log in to save tracks!");
        return false;
    }

    try {
        const response = await fetch(`http://localhost:8080/api/spotify/save-track?trackId=${trackId}`, {
            method: "POST",
            headers: {
                "Authorization": `Bearer ${accessToken}`
            }
        });

        if (response.ok) {
            alert("Track saved successfully!");
            return true;
        } else {
            alert("Failed to save track. Try again!");
            return false;
        }
    } catch (error) {
        console.error("Error saving track:", error);
        alert("An error occurred while saving the track.");
        return false;
    }
};