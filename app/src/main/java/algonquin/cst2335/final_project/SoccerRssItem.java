package algonquin.cst2335.final_project;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class is used to make an object of xml file(for the item of url)
 *
 * @author Zhiqian Qu
 * @version 1.0
 * @since 2021-8-1
 */
public class SoccerRssItem implements Parcelable {
    /**
     * these variables will be used for url
     */
    public static final int INVALID_ID = -1;
    private int id;
    private String title;
    private String link;
    private String pubDate;
    private String description;
    private String thumbnail;
    public static final Creator CREATOR = new Creator() {
        public SoccerRssItem createFromParcel(Parcel in) {
            return new SoccerRssItem(in);
        }
    public SoccerRssItem[] newArray(int size) {
            return new SoccerRssItem[size];
        }
    };

    /**
     * No-argument constructer
     */
    public SoccerRssItem() { }

    /**
     * Constructer whose parameter is Parcel
     *
     * @param in  Parcel
     */
    public SoccerRssItem(Parcel in){
        this.id = in.readInt();
        this.title = in.readString();
        this.link = in.readString();
        this.pubDate = in.readString();
        this.description = in.readString();
        this.thumbnail = in.readString();
    }

    /**
     * Construtcer with five parameters
     *
     * @param title String
     * @param link  String
     * @param pubDate String
     * @param description String
     * @param thumbnail thumbnail
     */
    public SoccerRssItem(String title, String link, String pubDate, String description, String thumbnail) {
        this.id = INVALID_ID;
        this.title = title;
        this.link = link;
        this.pubDate = pubDate;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    /**
     * Constructer
     *
     * @param dest is a kind of  Parcel
     * @param flags is aa kind of int
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.link);
        dest.writeString(this.pubDate);
        dest.writeString(this.description);
        dest.writeString(this.thumbnail);
    }

    /**
     * getter and setter
     * @return
     */
    public int getId() {
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public int describeContents() {
        return 0;
    }


}
