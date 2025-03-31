import React from "react";
import "../styles/songcard.css";

function SongCard({ song }) {
    return (
        <div className="song-card">
            <img src={song.albumImage} alt={song.name} />
            <h5>{song.name}</h5>
            <p>{song.artist}</p>
        </div>
    );
}

export default SongCard;
