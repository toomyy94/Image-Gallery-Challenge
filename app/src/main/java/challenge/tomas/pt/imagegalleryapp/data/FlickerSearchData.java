package challenge.tomas.pt.imagegalleryapp.data;

import android.graphics.Bitmap;
import android.util.Pair;

/**
 * Modified by Tomás Rodrigues on 04/19/2018.
 */

public class FlickerSearchData {

    private int id;
    private static int nextId = 0;
    private String photoID;
    private String title;
    private Pair<String, Bitmap> squaredImage;
    private Pair<String, Bitmap> fullscreenImage;

    public FlickerSearchData() {
        this.id = nextId;
        nextId++;
    }

    public FlickerSearchData(String photoID, String title, Pair<String, Bitmap> squaredImage) {
        this.id = nextId;
        nextId++;
        this.photoID = photoID;
        this.title = title;
        this.squaredImage = squaredImage;
    }

    public FlickerSearchData(String photoID, String title, Pair<String, Bitmap> squaredImage, Pair<String, Bitmap> fullscreenImage) {
        this.id = nextId;
        nextId++;
        this.photoID = photoID;
        this.title = title;
        this.squaredImage = squaredImage;
        this.fullscreenImage = fullscreenImage;
    }

    public String getPhotoID() {
        return photoID;
    }

    public void setPhotoID(String photoID) {
        this.photoID = photoID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Pair<String, Bitmap> getSquaredImage() {
        return squaredImage;
    }

    public void setSquaredImage(Pair<String, Bitmap> squaredImage) {
        this.squaredImage = squaredImage;
    }

    public Pair<String, Bitmap> getFullscreenImage() {
        return fullscreenImage;
    }

    public void setFullscreenImage(Pair<String, Bitmap> fullscreenImage) {
        this.fullscreenImage = fullscreenImage;
    }
}
