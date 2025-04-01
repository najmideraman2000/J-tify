import React from "react";
import "../styles/songItem.css";

const SongItem = ({ song }) => {
    const accessToken = localStorage.getItem("spotifyAccessToken");

    const saveTrack = async () => {
        if (!accessToken) {
            alert("You need to log in to save tracks!");
            return;
        }

        if (!song.id) {
            console.error("Track ID is undefined:", song);
            alert("Error: Track ID is missing.");
            return;
        }

        try {
            console.log(accessToken);
            const response = await fetch(`http://localhost:8080/save-track?trackId=${song.id}`, {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${accessToken}`
                }
            });

            if (response.ok) {
                alert(`Saved "${song.name}" to your library!`);
            } else {
                alert("Failed to save track. Try again!");
            }
        } catch (error) {
            console.error("Error saving track:", error);
            alert("An error occurred while saving the track.");
        }
    };

    return (
        <div className="song-item">
            <img src={song.albumImageUrl} alt={song.name} />
            <div className="song-info">
                <h3>{song.name}</h3>
                <p>{song.artist}</p>
            </div>
            <button className="save-button" onClick={saveTrack}>+</button>
        </div>
    );
};

export default SongItem;