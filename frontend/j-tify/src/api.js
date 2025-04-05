export const fetchTopJPopSongs = async (timeRange = "medium_term") => {
    try {
        const response = await fetch(`http://localhost:8080/top-jpop-tracks?time_range=${timeRange}`, {
            credentials: "include", 
        });

        if (response.status === 401) {
            window.location.href = `http://localhost:8080/login?redirectAfter=http://localhost:5173/top-jpop-tracks`;
            return;
        }

        if (!response.ok) throw new Error("Failed to fetch tracks");

        return await response.json();
    } catch (error) {
        console.error("Error fetching tracks:", error);
        return [];
    }
};

export const fetchTopJPopArtists = async (timeRange = "medium_term") => {
    try {
        const response = await fetch(`http://localhost:8080/top-jpop-artists?time_range=${timeRange}`, {
            credentials: "include", 
        });

        if (response.status === 401) {
            window.location.href = `http://localhost:8080/login?redirectAfter=http://localhost:5173/top-jpop-tracks`;
            return;
        }

        if (!response.ok) throw new Error("Failed to fetch artists");

        return await response.json();
    } catch (error) {
        console.error("Error fetching artists:", error);
        return [];
    }
};

export const saveTrack = async (trackId) => {
    try {
        const response = await fetch(`http://localhost:8080/save-track?trackId=${trackId}`, {
            method: "POST",
            credentials: "include", 
        });

        if (response.status === 204) {
            return true; 
        }

        if (response.status === 401) {
            window.location.href = `http://localhost:8080/login?redirectAfter=http://localhost:5173/top-jpop-tracks`;
            return;
        }

        if (!response.ok) throw new Error("Failed to save track");

        return await response.json();
    } catch (error) {
        console.error("Error saving track:", error);
        return [];
    }
};

export const followArtist = async (artistId) => {
    try {
        const response = await fetch(`http://localhost:8080/follow-artist?artistId=${artistId}`, {
            method: "POST",
            credentials: 'include',
        });

        if (response.status === 204) {
            return true; 
        }

        if (response.status === 401) {
            window.location.href = `http://localhost:8080/login?redirectAfter=http://localhost:5173/top-jpop-tracks`;
            return;
        }

        if (response.status !== 204) throw new Error("Failed to follow artist");

        return await response.json();
    } catch (error) {
        console.error("Error following artist:", error);
        return [];
    }
};

export const startPlayback = async (trackId) => {
    try {
        const response = await fetch(`http://localhost:8080/start-resume-playback?trackId=${trackId}`, {
            method: "PUT",
            credentials: 'include',
        });

        if (response.status === 204) {
            return true; 
        }

        if (response.status === 401) {
            window.location.href = `http://localhost:8080/login?redirectAfter=http://localhost:5173/top-jpop-tracks`;
            return;
        }

        if (response.status !== 204) throw new Error("Failed to play track");

        return await response.json();
    } catch (error) {
        console.error("Error playing track:", error);
        return [];
    }
};