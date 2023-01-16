package Holders;

public class Properties {
    public int width;
    public int height;
    public int bottom_margin_image;
    public int bottom_margin_name;
    public int name_font_size;

    public Properties(int width, int length, int bottom_margin_image, int bottom_margin_name, int name_font_size){
        this.width = width;
        this.height = length;
        this.bottom_margin_image = bottom_margin_image;
        this.bottom_margin_name = bottom_margin_name;
        this.name_font_size = name_font_size;
    }
}
