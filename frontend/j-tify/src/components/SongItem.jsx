import React from "react";
import { saveTrack } from "../api"; 
import "../styles/songItem.css";

const SongItem = ({ song }) => {
    const saveSong = async () => {
        const success = await saveTrack(song.id);
        if (success) {
            alert(`Saved "${song.name}" to your library!`);
        }
    };

    return (
        <div className="song-item">
            <img src={song.albumImageUrl} alt={song.name} />
            <div className="song-info">
                <h3>{song.name}</h3>
                <p>{song.artist}</p>
            </div>
            <button className="save-button" onClick={saveSong}>+</button>
        </div>
    );
};

export default SongItem;