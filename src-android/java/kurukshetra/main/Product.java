package kurukshetra.main;
/**
 * Created by Belal on 10/18/2017.
 */


public class Product {

    private String title,shortdesc;
    private  int image;
    public Product(String title,String size,int image) {

        this.title = title;
        this.shortdesc=size;
        this.image=image;
    }

    public String getTitle() {
        return title;
    }

    public int getImage() {
        return image;
    }
    public String getShortdesc() {
        return shortdesc;
    }
}