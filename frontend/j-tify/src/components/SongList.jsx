import React, { useEffect, useState } from "react";
import { fetchTopJPopSongs } from "../api";
import SongItem from "./SongItem";
import "../styles/songList.css";

const SongList = ({ timeRange }) => {
    const [songs, setSongs] = useState([]);
    const [loading, setLoading] = useState(true); 

    useEffect(() => {
        const getSongs = async () => {
            setLoading(true); 
            const data = await fetchTopJPopSongs(timeRange);
            setSongs(data);
            setLoading(false); 
        };

        getSongs();
    }, [timeRange]);

    return (
        <div className="song-list">
            <h2>Top 10 J-Pop Songs</h2>
            {loading ? <p>Loading...</p> : (
                songs.length === 0 ? <p>No J-Pop songs found.</p> :
                songs.map((song, index) => <SongItem key={index} song={song} />)
            )}
        </div>
    );
};

export default SongList;