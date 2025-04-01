import React from "react";
import "../styles/artistItem.css";

const ArtistItem = ({ artist }) => {
    console.log(artist);
    const accessToken = localStorage.getItem("spotifyAccessToken");

    const saveArtist = async () => {
        if (!accessToken) {
            alert("You need to log in to save artists!");
            return;
        }
    
        if (!artist.id) {
            console.error("Artist ID is undefined:", artist);
            alert("Error: Artist ID is missing.");
            return;
        }
    
        try {
            console.log(accessToken);
            const response = await fetch(`http://localhost:8080/save-artist?artistId=${artist.id}`, {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${accessToken}`
                }
            });
    
            if (response.status === 204) {
                alert(`Saved "${artist.name}" to your library!`);
            } else {
                alert("Failed to save artist. Try again!");
            }
        } catch (error) {
            console.error("Error saving artist:", error);
            alert("An error occurred while saving the artist.");
        }
    };
    
    return (
        <div className="artist-item">
            <img src={artist.imageUrl} alt={artist.name} />
            <div className="artist-info">
                <h3>{artist.name}</h3>
            </div>
            <button className="save-button" onClick={saveArtist}>+</button>
        </div>
    );
};

export default ArtistItem;