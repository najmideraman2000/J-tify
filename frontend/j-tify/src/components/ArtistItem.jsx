import React from "react";
import { followArtist } from "../api"; 
import "../styles/artistItem.css";

const ArtistItem = ({ artist }) => {
    const followArtistData = async () => {
        const success = await followArtist(artist.id);
        if (success) {
            alert(`Saved "${artist.name}" to your library!`);
        }
    };

    return (
        <div className="artist-item">
            <img src={artist.imageUrl} alt={artist.name} />
            <div className="artist-info">
                <h3>{artist.name}</h3>
            </div>
            <button className="save-button" onClick={followArtistData}>+</button>
        </div>
    );
};

export default ArtistItem;