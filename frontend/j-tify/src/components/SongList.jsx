import React, { useEffect, useState } from "react";
import { fetchJPopSongs } from "../api";
import SongItem from "./SongItem";

const SongList = () => {
    const [songs, setSongs] = useState([]);

    useEffect(() => {
        const getSongs = async () => {
            const data = await fetchJPopSongs();
            setSongs(data);
        };
        getSongs();
    }, []);

    return (
        <div className="song-list">
            <h2>Top 10 J-Pop Songs</h2>
            {songs.length === 0 ? <p>Loading...</p> : songs.map((song, index) => (
                <SongItem key={index} song={song} />
            ))}
        </div>
    );
};

export default SongList;
