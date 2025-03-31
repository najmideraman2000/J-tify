import React from "react";

const SongItem = ({ song }) => {
    return (
        <div className="song-item">
            <img src={song.albumImageUrl} alt={song.name} />
            <div className="song-info">
                <h3>{song.name}</h3>
                <p>{song.artist}</p>
            </div>
        </div>
    );
};

export default SongItem;
