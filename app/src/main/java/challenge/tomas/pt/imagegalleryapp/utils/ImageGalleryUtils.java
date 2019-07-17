package challenge.tomas.pt.imagegalleryapp.utils;

/**
 * Modified by tomas on 21/02/2018.
 */
public class ImageGalleryUtils {

    //API PATHS
    public static final String API_KEY = "9a95c68a9c6ec61104cd3967dcbb8bd3";
    public static final String BASE_URL = "https://api.flickr.com/services/rest/?method=flickr.photos.";
    public static final String ENDPOINT_ENDING = "&format=json&nojsoncallback=1";

    public static final String GET_PHOTOS_SEARCH = "search&api_key=";
    public static final String GET_PHOTO_SIZE = "getSizes&api_key=";

    public static final String GET_TAG = "&tags=";
    public static final String GET_TEXT = "&text=";
    public static final String GET_PER_PAGE = "&per_page=";
    public static final Integer ITEMS_PER_PAGE = 20;
    public static final String GET_PHOTO_ID = "&photo_id=";

    //API keys
    public static final String PHOTOS = "photos";
    public static final String PHOTO  = "photo";
    public static final String ID     = "id";
    public static final String TITLE  = "title";
    public static final String SIZES  = "sizes";
    public static final String SIZE   = "size";
    public static final String LABEL  = "label";
    public static final String WIDTH  = "width";
    public static final String HEIGHT = "height";
    public static final String SOURCE = "source";
    public static final String TAGS   = "tags";

    //API def values
    public static final String LARGE_SQUARE = "Large Square";
    public static final String LARGE = "Large";

    //Search options
    public static final String TEXT_TO_SEARCH = "text_to_search";
    public static final String SEARCH_OPTION = "search_option";
    public static final int SEARCH_OPTION_TAG = 1;
    public static final int SEARCH_OPTION_TEXT = 2;
}
