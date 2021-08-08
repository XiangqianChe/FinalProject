package algonquin.cst2335.final_project;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class is to represent an object of item tag of RSS XML
 *
 * @author Zhiqian Qu
 * @version 1.0
 * @since 2021-8-1
 */
public class SoccerRssItem implements Parcelable {
    /**
     * INVALID_ID to initialize the id of an item
     */
    public static final int INVALID_ID = -1;
    /**
     * id is to be used in soccer table
     */
    private int id;
    /**
     * title holds the value of title tag in RSS XML
     */
    private String title;
    /**
     * link holds the value of link tag in RSS XML
     */
    private String link;
    /**
     * pubDate holds the value of pubDate tag in RSS XML
     */
    private String pubDate;
    /**
     * description holds the value of description tag in RSS XML
     */
    private String description;
    /**
     * thumbnail holds the value of thumbnail tag in RSS XML
     */
    private String thumbnail;

    /**
     * An instance of Parcelable class
     */
    public static final Creator CREATOR = new Creator() {
        public SoccerRssItem createFromParcel(Parcel in) {
            return new SoccerRssItem(in);
        }

        public SoccerRssItem[] newArray(int size) {
            return new SoccerRssItem[size];
        }
    };

    /**
     * Non-arg construction
     *
     */
    public SoccerRssItem() { }

    /**
     * Construction with a Parcel parameter
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
     * Construction with five parameters
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

    /**
     * Construction with five parameters
     *
     * @param dest Parcel
     * @param flags int
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
}
