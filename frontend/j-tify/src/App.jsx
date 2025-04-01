import React, { useState } from "react";
import "./styles/index.css";
import SongList from "./components/SongList";

const App = () => {
    const [timeRange, setTimeRange] = useState("medium_term");

    return (
        <div className="app-container">
            <h2>Top J-Pop Songs</h2>
            
            <select onChange={(e) => setTimeRange(e.target.value)} value={timeRange}>
                <option value="long_term">Past 1 Year</option>
                <option value="medium_term">Past 6 Months</option>
                <option value="short_term">Past 1 Month</option>
            </select>

            <SongList timeRange={timeRange} />
        </div>
    );
};

export default App;
