import React, { useEffect, useState } from "react";
import { fetchTopJPopArtists } from "../api"; 
import ArtistItem from "./ArtistItem";
import "../styles/artistList.css";

const ArtistList = ({ timeRange }) => {
    const [artists, setArtists] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const getTopArtists = async () => {
            setLoading(true);
            const data = await fetchTopJPopArtists(timeRange); 
            setArtists(data);
            setLoading(false);
        };

        getTopArtists();
    }, [timeRange]);

    return (
        <div className="artist-list">
            {loading ? (
                <p>Loading...</p>
            ) : (
                artists.length === 0 ? (
                    <p>No J-Pop artists found.</p>
                ) : (
                    artists.map((artist, index) => (
                        <ArtistItem key={index} artist={artist} />
                    ))
                )
            )}
        </div>
    );
};

export default ArtistList;