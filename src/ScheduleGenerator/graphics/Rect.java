package ScheduleGenerator.graphics;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Rect {
    public static void draw(Graphics2D g2d, Rectangle2D.Double rect, Color color) {
        g2d.setColor(color);
        g2d.fill(rect);
    }
}
