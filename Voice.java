import java.awt.*;
import java.io.Serializable;

class Voice implements Serializable {
    protected String name;
    protected int id;
    protected boolean enable = true;
    Color color;

    Voice(String name, int id, Color color) {
        this.name = name;
        this.id = id;
        this.color = color;
    }

    boolean isEnable() {
        return enable;
    }

    void setEnable(boolean enable) {
        this.enable = enable;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }
}
