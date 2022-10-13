package ScheduleGenerator.graphics;

import ScheduleGenerator.Course;
import ScheduleGenerator.data.SGData;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Cell {
    private Point2D.Double padding; // makes sure text leaves this padding to the cell's border
    private Point2D.Double margin;
    private Point2D.Double scale;

    public Rectangle indexRect; // index-based rectangle

    private Rectangle2D.Double actualRect; // actual pixel-based rectangle
    public void setActualRect(Rectangle2D.Double actualRect) {
        this.actualRect = actualRect;
    }

    private final Color color;

    private Course course = null;
    public Course getCourse() {
        return course;
    }

    private final DrawableText centerDrwText = new DrawableText();
    private final DrawableText bottomDrwText = new DrawableText();
    private final DrawableText topLeftDrwText = new DrawableText();

    public Cell(Course course, Rectangle indexRect, Color color, double lineGap, Point2D.Double margin, Point2D.Double padding, Point2D.Double scale)
    {
        this.course = course;
        this.indexRect = indexRect;
        this.color = color;
        this.margin = margin;
        this.padding = padding;
        this.scale = scale;

        evaluateStringsBasedOnSpace();

        centerDrwText.setLineGap(lineGap);
        bottomDrwText.setLineGap(lineGap);
        topLeftDrwText.setLineGap(lineGap);
    }
    public Cell(Rectangle indexRect, Color col, String centerText, String bottomText, String topLeftText, double lineGap, Point2D.Double margin, Point2D.Double padding, Point2D.Double scale)
    {
        this.indexRect = indexRect;
        this.color = col;
        this.margin = margin;
        this.padding = padding;
        this.scale = scale;

        centerDrwText.setText(centerText);
        bottomDrwText.setText(bottomText);
        topLeftDrwText.setText(topLeftText);

        centerDrwText.setLineGap(lineGap);
        bottomDrwText.setLineGap(lineGap);
        topLeftDrwText.setLineGap(lineGap);
    }

    public void evaluateStringsBasedOnSpace() {
        centerDrwText.setText(course.getSubjectAlias());

        if(course.getFreqInHu() != null) {
            topLeftDrwText.setText(course.getContent(Course.HEADER_CONTENT.HALL) + "  (" + course.getFreqInHu() + ")");
        } else {
            topLeftDrwText.setText(course.getContent(Course.HEADER_CONTENT.HALL));
        }

        // large form
        if(indexRect.height > 1) {
            bottomDrwText.setText(course.getTypeInHu().getAbbreviation());
        } else { // small form
            bottomDrwText.setText(null);
            topLeftDrwText.setText(topLeftDrwText.getText() + "  " + course.getTypeInHu().getFirstLetter());
        }
    }

    private float getMaxFontSizeForScale()  {
        return (float) (7.5f * scale.x); // 7.5
    }

    private void calcTextsPosAndScale(Graphics2D g2d) {
        Rectangle2D.Double container = new Rectangle2D.Double(
                actualRect.x + padding.x,
                actualRect.y + padding.y,
                actualRect.width - padding.x * 2,
                actualRect.height - padding.y * 2
        );

        if(bottomDrwText.getText() != null) {
            bottomDrwText.calcSize(g2d);

            if (bottomDrwText.getFont() == null){
                bottomDrwText.scaleFont(container.width, g2d, getMaxFontSizeForScale());
            }

            if(bottomDrwText.getSize().x > container.width){
                bottomDrwText.scaleFont(container.width, g2d, getMaxFontSizeForScale());
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
                topLeftDrwText.scaleFont(container.width, g2d, getMaxFontSizeForScale());
            }

            if(topLeftDrwText.getSize().x > container.width){
                topLeftDrwText.scaleFont(container.width, g2d, getMaxFontSizeForScale());
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
                double offset = topLeftDrwText.getSize().y + padding.y * 2;

                adjustedContainer.y += offset;
                adjustedContainer.height -= offset;
            }

            if (!centerDrwText.isFontSizeSet()){
                centerDrwText.scaleFont(adjustedContainer.width, g2d, getMaxFontSizeForScale());
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

    public void setCenterFontSize(Font font, float fontSize) {
        centerDrwText.setFontSize(font, fontSize);
    }

    public void setBottomFontSize(Font font, float fontSize) {
        bottomDrwText.setFontSize(font, fontSize);
    }

    public void setTopLeftFontSize(Font font, float fontSize) {
        topLeftDrwText.setFontSize(font, fontSize);
    }

    public void drawCell(Graphics2D g2d) {
        calcTextsPosAndScale(g2d);

        // draw underlying rect
        Rect.draw(g2d, actualRect, color);

        // Define rendering hint, font name, font style and font size
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if(centerDrwText.shouldBeDrawn()) {
            centerDrwText.draw(g2d);
        }

        if(bottomDrwText.shouldBeDrawn()) {
            double southBgHeight = bottomDrwText.getSize().y + padding.x * 2;
            Rectangle2D.Double southBg = new Rectangle2D.Double(
                    actualRect.x, actualRect.y + actualRect.height - southBgHeight,
                    actualRect.width, southBgHeight
            );
            Rect.draw(g2d, southBg, SGData.Colors.TEXT_BG_COLOR);

            bottomDrwText.draw(g2d);
        }

        if(topLeftDrwText.shouldBeDrawn()) {
            Rectangle2D.Double topLeftBg = new Rectangle2D.Double(
                    actualRect.x, actualRect.y,
                    topLeftDrwText.getSize().x + padding.x * 2,
                    topLeftDrwText.getSize().y + padding.y * 2
            );
            Rect.draw(g2d, topLeftBg, SGData.Colors.TEXT_BG_COLOR);

            topLeftDrwText.draw(g2d);
        }
    }
}
