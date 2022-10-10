package ScheduleGenerator.graphics;

import ScheduleGenerator.Course;
import ScheduleGenerator.TimeFormatter;
import ScheduleGenerator.data.SGData;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import static ScheduleGenerator.data.SGData.DAYS_OF_WEEK_HU;

public class ScheduleDrawer extends JComponent {
    private final Point2D.Double textMargin;
    private final Point2D.Double cellMargin;

    private final Point2D.Double A4InMM = new Point2D.Double(297, 210);
    private final Point2D.Double border = new Point2D.Double(2, 2); // mm
    private final Point2D.Double pos;
    private final Point2D.Double size;
    private final Point2D.Double scale;

    private Graphics2D g2d = null;
    private Font font = null;

    private final int cols; // 15 = 1 + 14
    private final int rows; // 11 = 1 + (5 * 2)
    private ArrayList<LocalTime[]> intervals;

    // specific elements
    private ArrayList<Course> courses;
    private String topLeftCText;

    public ScheduleDrawer(Point2D.Double pos, Point2D.Double size, ArrayList<LocalTime[]> intervals) {
        this.pos = pos;
        this.size = size;
        this.scale = new Point2D.Double(size.x / A4InMM.x, size.y / A4InMM.y);
        setSize(new Dimension((int) size.x, (int) size.y));

        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File("assets/fonts/Eras-Bold-ITC.ttf")).deriveFont(10F);
        } catch (IOException|FontFormatException e) {
            e.printStackTrace();
        }

        if(intervals != null)
            this.intervals = intervals;

        cols = 1 + this.intervals.size(); // header + num of hours
        rows = 1 + DAYS_OF_WEEK_HU.length * 2; // side + dow * 2

        textMargin = new Point2D.Double(2.5 * scale.x, 2.5 * scale.y);
        cellMargin = new Point2D.Double(0.5 * scale.x,0.5  * scale.y); // milimeters
    }

    public  void setSpecificProps(String topLeftCText, ArrayList<Course> courses) {
        this.topLeftCText = topLeftCText;
        this.courses = courses;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g2d = (Graphics2D) g;
        g2d.setFont(font);

        // g2d.setBackground(Colors.BACKGROUND_COLOR);
        drawRect(new Rectangle2D.Double(pos.x, pos.y, size.x, size.y), SGData.Colors.BACKGROUND_COLOR);

        ArrayList<Cell> cells = generateCells(courses);

        drawTable(new Point2D.Double(0,0), new Point2D.Double(A4InMM.x, A4InMM.y), cells);
    }

    private void drawTable(Point2D.Double tPos, Point2D.Double tSize, ArrayList<Cell> cells) {
        if(cells == null) return;

        Point2D.Double absPos = new Point2D.Double(pos.x, pos.y); // , tSize = new Vec(size.x, size.y);

        // convert MM to px
        tPos.x *= scale.x;
        tPos.y *= scale.y;
        tSize.x *= scale.x;
        tSize.y *= scale.y;

        tSize.x -= border.x * scale.x * 2; // left, right
        tSize.y -= border.y * scale.y * 2; // top, bottom
        absPos.x += border.x * scale.x  + tPos.x;
        absPos.y += border.y * scale.y + tPos.y;

        int xI = 0, yI, i = 0;
        double xOffs = tSize.x / cols;
        double yOffs = tSize.y / rows;

        for(double x = 0; x <= tSize.x - xOffs + 1; x += xOffs) {
            yI = 0;
            for(double y = 0; y <= tSize.y - yOffs + 1; y += yOffs) {

                Rectangle2D.Double cellCoords = new Rectangle2D.Double(
                        absPos.x + x + cellMargin.x, absPos.y + y + cellMargin.y,
                        xOffs - (cellMargin.y * 2), yOffs - (cellMargin.y * 2));

                boolean inAnyCell = false;
                for(Cell cell : cells) {
                    if(cell.rect.x == xI && cell.rect.y == yI) {

                        cellCoords.width = cell.rect.width * xOffs - (cellMargin.x * 2);
                        cellCoords.height = cell.rect.height * yOffs - (cellMargin.y * 2);

                        cell.calcTextsPosAndScale(cellCoords, textMargin, scale, g2d);

                        drawCell(cellCoords, cell);
                    }

                    if(!inAnyCell)
                        inAnyCell = !(xI >= cell.rect.x + cell.rect.width) && !(yI >= cell.rect.y + cell.rect.height)
                                     && !(xI < cell.rect.x) && !(yI < cell.rect.y);
                }

                if(!inAnyCell) {
                    drawRect(cellCoords, Color.WHITE);
                }

                yI++;
                i++;
            }
            xI++;
            i++;
        }
    }

    private void drawRect(Rectangle2D.Double rect, Color color) {
        g2d.setColor(color);
        g2d.fill(rect);
    }

    private void drawCell(Rectangle2D.Double rect, Cell cell) {
        drawRect(rect, cell.col);

        // Define rendering hint, font name, font style and font size
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if(cell.getCenterDrwText().shouldBeDrawn()) {
            cell.getCenterDrwText().draw(g2d);
        }

        if(cell.getBottomDrwText().shouldBeDrawn()) {
            double southBgHeight = cell.getBottomDrwText().getSize().y + textMargin.x * 2;
            Rectangle2D.Double southBg = new Rectangle2D.Double(
                    rect.x, rect.y + rect.height - southBgHeight,
                    rect.width, southBgHeight
            );
            drawRect(southBg, SGData.Colors.TEXT_BG_COLOR);

            cell.getBottomDrwText().draw(g2d);
        }

        if(cell.getTopLeftDrwText().shouldBeDrawn()) {
            Rectangle2D.Double topLeftBg = new Rectangle2D.Double(
                    rect.x, rect.y,
                    cell.getTopLeftDrwText().getSize().x + textMargin.x * 2,
                    cell.getTopLeftDrwText().getSize().y + textMargin.y * 2
            );
            drawRect(topLeftBg, SGData.Colors.TEXT_BG_COLOR);

            cell.getTopLeftDrwText().draw(g2d);
        }
    }

    private ArrayList<Cell> generateCells(ArrayList<Course> courses) {
        System.out.println("generate cells " + (courses == null));
        if(courses == null) return null;

        ArrayList<Cell> cells = new ArrayList<>();

        // top left content
        Cell topLeftCell = new Cell(new Rectangle(0, 0, 1, 1),
                SGData.Colors.BASE_COLOR, topLeftCText, null, null, 2.5 * scale.x);
        topLeftCell.setFontStyle(font);
        cells.add(topLeftCell);

        // days
        for(int i = 1; i <= DAYS_OF_WEEK_HU.length; i++) {
            Rectangle t = new Rectangle(0, i * 2 - 1, 1, 2);

            Cell cell = new Cell(t, SGData.Colors.BASE_COLOR, DAYS_OF_WEEK_HU[i - 1], null, null, 0);
            cell.setCenterFontSize(font, (float) (this.scale.x * 9F));

            cells.add(cell);
        }

        // intervals
        for(int i = 1; i <= intervals.size(); i++) {
            Rectangle t = new Rectangle(i, 0, 1, 1);
            String displayInterval = TimeFormatter.localTimeArrToDisplayFormat(intervals.get(i - 1));
            Cell cell = new Cell(t, SGData.Colors.BASE_COLOR, displayInterval, null, null, 2.5 * scale.x);
            cell.setCenterFontSize(font, (float) (this.scale.x * 5F));

            cells.add(cell);
        }

        // courses
        for(var course : courses) {
            // course properties
            String courseName = course.getSubjectAlias();
            String courseSouthInfo = course.getTypeInHu().getAbbreviation();
            String courseNorthInfo;
            if(course.getFreqInHu() != null) {
                courseNorthInfo = course.getContent(Course.HEADER_CONTENT.HALL) + "  (" + course.getFreqInHu() + ")";
            } else {
                courseNorthInfo = course.getContent(Course.HEADER_CONTENT.HALL) ;
            }

            // cell coordinates
            int startX = 0;
            int endX = 0;
            LocalTime[] courseIntArr = course.getIntervalAsLocalTimeArr();
            for(int i = 0; i < intervals.size(); i++) {
                if(intervals.get(i)[0].equals(courseIntArr[0])) startX = i + 1;
                if(intervals.get(i)[1].equals(courseIntArr[1])) endX = i + 2;
            }
            int dayIndex = course.getDayIndexInRO_DAYS();
            int width = endX - startX;
            Rectangle cellCoords = new Rectangle(startX, dayIndex * 2 + 1, width, 2);

            // solve cells on same positions
            if(course.isDuplicate()) {
                if(course.getFreqAsNum() == 2) cellCoords.y += 1;
                cellCoords.height = 1;
                courseSouthInfo = null;
                courseNorthInfo += "  " + course.getTypeInHu().getFirstLetter();
            }

            // double check for cells on same position
            Optional<Cell> cellOnSamePos = cells.stream().
                    filter(cell -> Objects.equals(cell.rect, cellCoords)).
                    findFirst();
            if(cellOnSamePos.isPresent()) {
                cellOnSamePos.get().rect.height = 1;
                cellCoords.height = 1;
                cellCoords.y += 1;
            }

            Cell clsDrw = new Cell(
                    cellCoords, course.getSubjectColor(), courseName,
                    courseSouthInfo, courseNorthInfo, 0
            );

            clsDrw.setBottomFontSize(font, (float) (this.scale.x * 5F));
            clsDrw.setTopLeftFontSize(font, (float) (this.scale.x * 3.5F));

            cells.add(clsDrw);
        }

        return cells;
    }

    public ScheduleDrawer getHighResVersion() {
        ScheduleDrawer tmp = new ScheduleDrawer(new Point2D.Double(0,0), new Point2D.Double(3508, 2480), intervals);
        tmp.setSpecificProps(topLeftCText, courses);
        return tmp;
    }
}
