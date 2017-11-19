package kamrulhasan3288.com.audiostraming.model;

/**
 * Created by kamrulhasan on 11/18/17.
 */

public class SongList {
    private String songTitle;
    private String songUrl;

    public SongList(String songTitle, String songUrl) {
        this.songTitle = songTitle;
        this.songUrl = songUrl;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public String getSongUrl() {
        return songUrl;
    }
}
