package chess.model;

public class Chessman {

    private String name;
    private String color;
    private String code;

    public Chessman() {}

    public Chessman(String name, String color, String code) {
        this.name = name;
        if (color.equals("W")) this.color = "white";
        else if (color.equals("B")) this.color = "black";
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
