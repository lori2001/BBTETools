package ScheduleGenerator.graphics;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Cell {
    public Rectangle rect;
    private Color color;

    private final DrawableText centerDrwText = new DrawableText();
    private final DrawableText bottomDrwText = new DrawableText();
    private final DrawableText topLeftDrwText = new DrawableText();

    public Cell(Rectangle rect, Color col, String centerText, String bottomText, String topLeftText, double lineGap)
    {
        this.rect = rect;
        this.color = col;

        centerDrwText.setText(centerText);
        bottomDrwText.setText(bottomText);
        topLeftDrwText.setText(topLeftText);
        centerDrwText.setLineGap(lineGap);
        bottomDrwText.setLineGap(lineGap);
        topLeftDrwText.setLineGap(lineGap);
    }

    private static float getMaxSizeForScale(Point2D.Double scale)  {
        return (float) (7.5f * scale.x); // 7.5
    }

    public void calcTextsPosAndScale(Rectangle2D.Double cellCoords, Point2D.Double textMargin, Point2D.Double scale, Graphics2D g2d) {
        Rectangle2D.Double container = new Rectangle2D.Double(
                cellCoords.x + textMargin.x,
                cellCoords.y + textMargin.y,
                cellCoords.width - textMargin.x * 2,
                cellCoords.height - textMargin.y * 2
        );

        if(bottomDrwText.getText() != null) {
            bottomDrwText.calcSize(g2d);

            if (bottomDrwText.getFont() == null){
                bottomDrwText.scaleFont(container.width, g2d, getMaxSizeForScale(scale));
            }

            if(bottomDrwText.getSize().x > container.width){
                bottomDrwText.scaleFont(container.width, g2d, getMaxSizeForScale(scale));
                bottomDrwText.calcSize(g2d);
            }

            bottomDrwText.setPos(
                    new Point2D.Double(
                            container.x + container.width / 2 - bottomDrwText.getSize().x / 2,
                            container.y + container.height - bottomDrwText.getSize().y
                    )
            );
        }

        if(topLeftDrwText.getText() != null) {
            topLeftDrwText.calcSize(g2d);

            if (topLeftDrwText.getFont() == null){
                topLeftDrwText.scaleFont(container.width, g2d, getMaxSizeForScale(scale));
            }

            if(topLeftDrwText.getSize().x > container.width){
                topLeftDrwText.scaleFont(container.width, g2d, getMaxSizeForScale(scale));
                topLeftDrwText.calcSize(g2d);
            }

            topLeftDrwText.setPos(
                    new Point2D.Double(container.x, container.y)
            );
        }

        if(centerDrwText.getText() != null) {
            Rectangle2D.Double adjustedContainer =
                    new Rectangle2D.Double(container.x, container.y, container.width, container.height);

            // move the center up if only bottom text
            if(bottomDrwText.getText() != null && topLeftDrwText.getText() == null) {
                adjustedContainer.height -= bottomDrwText.getSize().y;
            }
            // move the center down if only topLeft text
            else if(bottomDrwText.getText() == null && topLeftDrwText.getText() != null) {
                double offset = topLeftDrwText.getSize().y + textMargin.y * 2;

                adjustedContainer.y += offset;
                adjustedContainer.height -= offset;
            }

            if (!centerDrwText.isFontSizeSet()){
                centerDrwText.scaleFont(adjustedContainer.width, g2d, getMaxSizeForScale(scale));
            }

            centerDrwText.calcSize(g2d);
            centerDrwText.setPos(
                new Point2D.Double(
                        adjustedContainer.x + adjustedContainer.width / 2 - centerDrwText.getSize().x / 2,
                        adjustedContainer.y + adjustedContainer.height / 2 - centerDrwText.getSize().y / 2
                )
            );
        }
    }

    public void setFontStyle(Font font) {
        centerDrwText.setFont(font);
        topLeftDrwText.setFont(font);
        bottomDrwText.setFont(font);
    }

    public DrawableText getCenterDrwText() {
        return centerDrwText;
    }

    public DrawableText getBottomDrwText() {
        return bottomDrwText;
    }

    public DrawableText getTopLeftDrwText() {
        return topLeftDrwText;
    }

    public void setCenterFontSize(Font font, float fontSize) {
        centerDrwText.setFontSize(font, fontSize);
    }

    public void setBottomFontSize(Font font, float fontSize) {
        bottomDrwText.setFontSize(font, fontSize);
    }

    public void setTopLeftFontSize(Font font, float fontSize) {
        topLeftDrwText.setFontSize(font, fontSize);
    }

    public Color getColor() {
        return color;
    }
}
