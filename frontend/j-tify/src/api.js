export const fetchTopJPopSongs = async (timeRange = "medium_term") => {
    try {
        const response = await fetch(`http://localhost:8080/top-jpop-tracks?time_range=${timeRange}`);
        if (!response.ok) throw new Error("Failed to fetch songs");
        return await response.json();
    } catch (error) {
        console.error("Error fetching songs:", error);
        return [];
    }
};

export const fetchTopJPopArtists = async (timeRange = "medium_term") => {
    try {
        const response = await fetch(`http://localhost:8080/top-jpop-artists?time_range=${timeRange}`);
        if (!response.ok) throw new Error("Failed to fetch artists");
        return await response.json();
    } catch (error) {
        console.error("Error fetching artists:", error);
        return [];
    }
};

export const saveTrack = async (trackId) => {
    const accessToken = localStorage.getItem("spotifyAccessToken");

    if (!accessToken) {
        alert("You need to log in to save tracks!");
        return false;
    }

    if (!trackId) {
        console.error("Track ID is undefined:", artistId);
        return false;
    }

    try {
        const response = await fetch(`http://localhost:8080/save-track?trackId=${trackId}`, {
            method: "POST",
            headers: {
                "Authorization": `Bearer ${accessToken}`
            }
        });

        if (response.ok) {
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


export const saveArtist = async (artistId) => {
    const accessToken = localStorage.getItem("spotifyAccessToken");

    if (!accessToken) {
        alert("You need to log in to save artists!");
        return false;
    }

    if (!artistId) {
        console.error("Artist ID is undefined:", artistId);
        return false;
    }

    try {
        const response = await fetch(`http://localhost:8080/follow-artist?artistId=${artistId}`, {
            method: "POST",
            headers: {
                "Authorization": `Bearer ${accessToken}`
            }
        });

        if (response.status === 204) {
            return true;
        } else {
            alert("Failed to save artist. Try again!");
            return false;
        }
    } catch (error) {
        console.error("Error saving artist:", error);
        alert("An error occurred while saving the artist.");
        return false;
    }
};

export const startPlayback = async (trackId) => {
    const accessToken = localStorage.getItem("spotifyAccessToken");
    if (!accessToken) {
        alert("You need to log in to play music!");
        return;
    }

    if (!trackId) {
        console.error("Track ID is undefined:", artistId);
        return false;
    }

    try {
        const response = await fetch(`http://localhost:8080/start-resume-playback?trackId=${trackId}`, {
            method: "PUT",
            headers: {
                "Authorization": `Bearer ${accessToken}`
            }
        });

        if (response.ok) {
            alert("Playback started or resumed!");
        } else {
            alert("Failed to start or resume playback.");
        }
    } catch (error) {
        console.error("Error starting playback:", error);
        alert("An error occurred while trying to play music.");
    }
};