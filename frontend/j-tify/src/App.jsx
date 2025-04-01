import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./styles/index.css";
import SongList from "./components/SongList";
import ArtistList from "./components/ArtistList";

const App = () => {
    const [timeRange, setTimeRange] = useState("medium_term");
    const [view, setView] = useState("songs"); // To toggle between songs and artists
    const navigate = useNavigate();

    const redirectToLogin = () => {
        window.location.href = "http://localhost:8080/login?redirect_uri=http://localhost:5173/top-jpop";
    };

    useEffect(() => {
        const urlParams = new URLSearchParams(window.location.search);
        const token = urlParams.get("access_token");

        if (token) {
            localStorage.setItem("spotifyAccessToken", token);
            navigate("/"); 
        } else {
            const storedToken = localStorage.getItem("spotifyAccessToken");
            if (!storedToken) {
                redirectToLogin();
            }
        }
    }, [navigate]);

    return (
        <div className="app-container">
            <h2>Top J-Pop {view === "songs" ? "Songs" : "Artists"}</h2>
            <button onClick={redirectToLogin}>Login to Spotify</button>
            
            <select onChange={(e) => setTimeRange(e.target.value)} value={timeRange}>
                <option value="long_term">Past 1 Year</option>
                <option value="medium_term">Past 6 Months</option>
                <option value="short_term">Past 1 Month</option>
            </select>

            <div className="button-container">
                <button onClick={() => setView("songs")}>View Songs</button>
                <button onClick={() => setView("artists")}>View Artists</button>
            </div>

            {view === "songs" ? <SongList timeRange={timeRange} /> : <ArtistList timeRange={timeRange} />}
        </div>
    );
};

export default App;