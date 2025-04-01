import React, { useEffect, useState } from "react";
import ArtistItem from "./ArtistItem"; 
import "../styles/artistList.css"; 

const ArtistList = ({ timeRange }) => {
    const [artists, setArtists] = useState([]);
    const [loading, setLoading] = useState(true); 

    useEffect(() => {
        const fetchTopJPopArtists = async () => {
            setLoading(true); 
            const token = localStorage.getItem("spotifyAccessToken");
            const response = await fetch(`http://localhost:8080/top-jpop-artists?time_range=${timeRange}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (response.ok) {
                const data = await response.json();
                setArtists(data);
            }
            setLoading(false); 
        };

        fetchTopJPopArtists();
    }, [timeRange]);

    return (
        <div className="artist-list">
            <h2>Top J-Pop Artists</h2>
            {loading ? <p>Loading...</p> : (
                artists.length === 0 ? <p>No J-Pop artists found.</p> :
                artists.map((artist, index) => <ArtistItem key={index} artist={artist} />)
            )}
        </div>
    );
};

export default ArtistList;