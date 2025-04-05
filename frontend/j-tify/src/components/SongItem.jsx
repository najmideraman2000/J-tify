import { saveTrack, startPlayback } from "../api";
import "../styles/songItem.css";

const SongItem = ({ song }) => {
    const saveSong = async () => {
        const success = await saveTrack(song.id);
        if (success) {
            alert(`Saved "${song.name}" to your library!`);
        }
    };

    const handlePlaySong = async () => {
        let trackID = song.id;
        const success = await startPlayback(trackID);  
        if (!success) {
            alert("Could not start playback.");
        }
    };

    return (
        <div className="song-item" onClick={handlePlaySong}>
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