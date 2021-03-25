/*
  Java 1.1 AWT Applet - Tic-Tac-Toe, X's and O's, Naughts and Crosses
  Written by: Keith Fenske, http://www.psc-consulting.ca/fenske/
  Saturday, 3 January 2004
  Java class name: TicTacToe4
  Copyright (c) 2004 by Keith Fenske.  Released under GNU Public License.

  This is a Java AWT applet to play Tic-Tac-Toe.  It is an example to show
  basic GUI programming, and is not intended for advanced GUI design or for
  optimal game playing.  Getting a GUI program working is hard enough the first
  time without worrying about too many details!

  The user (player) has the magenta X's and makes the first move.  The computer
  has the blue O's.  If the board is too small, filled squares are substituted
  for X's and filled circles for O's.

  The user may change the size of the board.  The number of rows and columns
  may be changed independently.  The range allowed (from 2 to 18 positions) is
  more than is practical for playing a fair game.  The point is to have correct
  programming logic even with extreme values of the input.  The program will
  redraw the game board to make the best use of the available window size.

  To win the game, one player must have a complete horizontal line, a complete
  vertical line, or a complete diagonal line.  In a regular game, the number of
  rows and columns are equal, so all complete lines are the same length.  This
  program allows the number of rows to be different than the number of columns.
  Two possible definitions of a winning line present themselves:

  (1) Choose the smaller of the number of rows and the number of columns.  Then
      any straight line anywhere on the board, with that many positions
      belonging to the same player, is a winning line.  If, for example, there
      are 6 rows and 10 columns, a winning line of 6 positions might appear from
      coordinates (3,2) to (3,7).

  (2) Ignore the fact that the rows and columns aren't equal, and still insist
      on a complete row, a complete column, or a complete diagonal -- and there
      may be several diagonals!  This is visually easier to see and understand.

  Both options have their merits from a programming point of view, because both
  options force the programmer to avoid assumptions about board size.  This
  program implements option (2).  Option (1) could be implemented as an extra
  feature.  As instructors are so often heard to declare, the necessary changes
  are left as an exercise to the reader!

  GNU General Public License (GPL)
  --------------------------------
  TicTacToe4 is free software: you can redistribute it and/or modify it under
  the terms of the GNU General Public License as published by the Free Software
  Foundation, either version 3 of the License or (at your option) any later
  version.  This program is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, without even the implied warranty of MERCHANTABILITY or
  FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
  more details.

  You should have received a copy of the GNU General Public License along with
  this program.  If not, see the http://www.gnu.org/licenses/ web page.

  -----------------------------------------------------------------------------

  Java Applet Notes:

  The recommended way of writing applets is to use Java Swing, according to Sun
  Microsystems, the creators and sponsors of Java.  Unfortunately, most web
  browsers don't support Swing unless you download a recent copy of the Java
  run-time environment from Sun.  This leaves a Java programmer with two
  choices:

  (1) Write applets using only old features found in the AWT interface.  The
      advantage, if you can see it this way, is that the programmer gets a
      detailed opportunity to interact with the graphical interface.  (Joke.)

  (2) Force users to visit http://java.sun.com/downloads/ to download and
      install a newer version of Java.  However, forcing anyone to download
      something before they can visit your web page is a poor idea.

  A worse idea is new browsers that don't have any Java support at all, unless
  the user first downloads Sun Java.  Microsoft stopped distributing their
  version of Java in 2003 starting with Windows XP SP1a (February), then
  Windows 2000 SP4 (June).  Until Microsoft and Sun resolve their various
  lawsuits -- or until Microsoft agrees to distribute an unaltered version of
  Sun Java -- there will be an increasing number of internet users that have
  *no* version of Java installed on their machines!

  The design considerations for this applet are as follows:

  (1) The applet should run on older browsers as-is, without needing any
      additional downloads and/or features.  The minimum target is JDK1.1 which
      is Microsoft Internet Explorer 5.0 (Windows 98) and Netscape 4.7/4.8 (JDK
      1.1.5 from 1997).

  (2) The applet should contain only a single class, with no external images,
      so that it can be downloaded as one file with an HTML web page.

  (3) The default background in the Sun Java applet viewer is white, but most
      web browsers use light grey.  To get the background color that you want,
      you must setBackground() on components or fillRect() with the color of
      your choice.

  (4) A small main() method is included with a WindowAdapter subclass, so that
      this program can be run as an application.  The default window size and
      position won't please everyone.
*/

import java.applet.*;             // older Java applet support
import java.awt.*;                // older Java GUI support
import java.awt.event.*;          // older Java GUI event support

public class TicTacToe4
       extends Applet
       implements ActionListener, ItemListener, MouseListener
{
  /* constants */

  static final String beginMessage = "Click the mouse on a position of your choice.  You are the magenta X's.";
  static final int canvasBorder = 10; // empty pixels around game board
  static final int DefCOLS = 5;   // default number of columns in game board
  static final int DefROWS = 3;   // default number of rows in game board
  static final String noMessage = " "; // message text when nothing to say
  static final String[] rowColumnCounters = {"2", "3", "4", "5", "6", "8",
    "12", "18"};

  static final Color BACKGROUND = new Color(255, 204, 204); // light pink
  static final Color ColorCOMPUTER = new Color(102, 102, 255); // light blue
  static final Color ColorGRIDLINE = new Color(204, 153, 153); // darker pink
  static final Color ColorUSER = Color.magenta;
  static final Color ColorWINCOMP = Color.red;
  static final Color ColorWINUSER = Color.red;

  static final int PlayCOMPUTER = 1;  // board position: computer (regular)
  static final int PlayNONE = 2;      // board position: empty
  static final int PlayUSER = 3;      // board position: user (regular)
  static final int PlayWINCOMP = 4;   // board position: computer wins here
  static final int PlayWINUSER = 5;   // board position: user wins here

  static final int ValueOPENCOMP = 5; // relative importance (to the computer)
                                      // ... of an empty position on a path
                                      // ... that is still open (that is, has
                                      // ... no user positions)
  static final int ValueOPENUSER = 2; // relative importance of an empty
                                      // ... position on a path that is still
                                      // ... open to the user (no computer)
  static final int ValueWINCOMP = 500; // relative importance of a single empty
                                      // ... position that will win the game
                                      // ... for the computer
  static final int ValueWINUSER = 200; // relative importance of a single empty
                                      // ... position that will stop the user
                                      // ... from winning in the next move

  /* class variables */

  /* instance variables, including shared GUI components */

  Canvas boardCanvas;             // where we draw the game board
  int[][] boardData;              // internal game board (PlayXXX)
  int boardGridStep;              // calculated size of each board position,
                                  // ... including inner borders and one set of
                                  // ... grid lines
  int boardInnerBorder;           // pixels in each position's inner border
  int boardLeftMargin;            // adjusted left margin to center game board
  int boardSymbolSize;            // pixels for each position's player symbol
  int boardTopMargin;             // adjusted top margin to center game board
  int[][] boardValues;            // estimated value of each empty position
  Choice columnCounter;           // column counter (number of columns)
  int gameState;                  // state variable for current game
  Label messageText;              // information or status message for user
  int numCols = DefCOLS;          // number of columns in current game board
  int[][] pathList;               // list of winning lines in game board
  int numPaths;                   // a better name for <pathList.length>
  int numRows = DefROWS;          // number of rows in current game board
  Choice rowCounter;              // row counter (number of rows)
  Button skipButton;              // "Skip Turn" button
  Button startButton;             // "New Game" button


/*
  init() method

  Initialize this applet (equivalent to the main() method in an application).
  Please note the following about writing applets:

  (1) An Applet is an AWT Component just like a Button, Frame, or Panel.  It
      has a width, a height, and you can draw on it (given a proper graphical
      context, as in the paint() method).

  (2) Applets shouldn't attempt to exit, such as by calling the System.exit()
      method, because this isn't allowed on a web page.
*/
  public void init()
  {
    /* Intialize our own data before creating the GUI interface. */

    clearBoard();                 // clear (create) the game board

    /* Create the GUI interface as a series of little panels inside bigger
    panels.  The intermediate panel names (panel1, panel2, etc) are of no
    importance and hence are only numbered. */

    /* Make a horizontal panel to hold the row and column counters. */

    Panel panel1 = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    Label label1 = new Label("Rows: ", Label.RIGHT);
    label1.setBackground(BACKGROUND);
    panel1.add(label1);
    rowCounter = new Choice();
    for (int i = 0; i < rowColumnCounters.length; i ++)
      rowCounter.add(rowColumnCounters[i]);
    rowCounter.select(String.valueOf(DefROWS));
    rowCounter.addItemListener((ItemListener) this);
    panel1.add(rowCounter);

    Panel panel2 = new Panel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    Label label2 = new Label("Columns: ", Label.RIGHT);
    label2.setBackground(BACKGROUND);
    panel2.add(label2);
    columnCounter = new Choice();
    for (int i = 0; i < rowColumnCounters.length; i ++)
      columnCounter.add(rowColumnCounters[i]);
    columnCounter.select(String.valueOf(DefCOLS));
    columnCounter.addItemListener((ItemListener) this);
    panel2.add(columnCounter);

    Panel panel3 = new Panel(new FlowLayout(FlowLayout.CENTER, 10, 0));
    panel3.add(panel1);
    panel3.add(panel2);

    /* Make a horizontal panel to hold two equally-spaced buttons. */

    Panel panel4 = new Panel(new GridLayout(1, 2, 30, 0));

    startButton = new Button("New Game");
    startButton.addActionListener((ActionListener) this);
    panel4.add(startButton);

    skipButton = new Button("Skip Turn");
    skipButton.addActionListener((ActionListener) this);
    panel4.add(skipButton);

    /* Put the counters and buttons together into one horizontal panel. */

    Panel panel5 = new Panel(new FlowLayout(FlowLayout.CENTER, 20, 5));
    panel5.add(panel3);
    Label label3 = new Label(" ");      // a cheap separator
    label3.setBackground(BACKGROUND);
    panel5.add(label3);
    panel5.add(panel4);

    /* Put the message field under the counters/buttons. */

    Panel panel6 = new Panel(new GridLayout(2, 1, 0, 5));
    panel6.add(panel5);
//  messageText = new Label(beginMessage, Label.CENTER);
    messageText = new Label("Tic-Tac-Toe (Java applet).  Copyright (c) 2004 by Keith Fenske.  GNU Public License.", Label.CENTER);
    // JDK1.1 note: replace Font(null,...) with Font("Default",...)
    messageText.setFont(new Font("Default", Font.PLAIN, 14));
    messageText.setBackground(BACKGROUND);
    panel6.add(messageText);

    /* Now put all that on top of a canvas for the game board, giving the game
    board the remaining window space.  Since we don't set a size for the board
    canvas, it is assigned some arbitrary width and zero height.  The paint()
    method will calculate the correct placement later. */

    Panel panel7 = new Panel(new BorderLayout(canvasBorder, canvasBorder));
    panel7.add(panel6, BorderLayout.NORTH);
    boardCanvas = new Canvas();
    panel7.add(boardCanvas, BorderLayout.CENTER);
    panel7.setBackground(BACKGROUND); // for Netscape 4.7/4.8 (JDK1.1)

    /* Create this applet's window as a single combined panel. */

    this.add(panel7);
    this.addMouseListener((MouseListener) this);
    this.setBackground(BACKGROUND);
    this.validate();              // do the window layout

    /* Now let the GUI interface run the game. */

  } // end of init() method


/*
  main() method

  Applets only need an init() method to start execution.  This main() method is
  a wrapper that allows the same applet code to run as an application.
*/
  public static void main(String[] args)
  {
    Applet appletPanel;           // the target applet's window
    Frame mainFrame;              // this application's window

    mainFrame = new Frame(
      "Tic-Tac-Toe, X's and O's, Naughts and Crosses - by: Keith Fenske");
    mainFrame.addWindowListener(new TicTacToe4Window());
    mainFrame.setLayout(new BorderLayout(5, 5));
    mainFrame.setLocation(new Point(50, 50)); // top-left corner of app window
    mainFrame.setSize(700, 500);  // initial size of application window
    appletPanel = new TicTacToe4(); // create instance of target applet
    mainFrame.add(appletPanel, BorderLayout.CENTER); // give applet full frame
    mainFrame.validate();         // do the application window layout
    appletPanel.init();           // initialize applet
    mainFrame.setVisible(true);   // show the application window

  } // end of main() method

// ------------------------------------------------------------------------- //

/*
  actionPerformed() method

  This method is called when the user clicks on the "New Game" or "Skip Turn"
  buttons.
*/
  public void actionPerformed(ActionEvent event)
  {
    Object source = event.getSource(); // where the event came from
    if (source == skipButton)
    {
      /* The user clicked the "Skip Turn" button; the computer will move next
      instead. */

      switch (gameState)
      {
        case PlayCOMPUTER:
          messageText.setText("But it's the computer's turn to play!");
          break;

        case PlayNONE:
          messageText.setText("You must start a new game before you can skip your turn.");
          break;

        case PlayUSER:
//        messageText.setText("Skipping your turn: the computer will move next.");
          messageText.setText(noMessage);
          gameState = PlayCOMPUTER;
          moveComputer();         // check for winner, make computer's move
          break;

        default:
          System.out.println("error in actionPerformed(): bad gameState = "
            + gameState);
      }
      repaint();
    }
    else if (source == startButton)
    {
      /* The user clicked the "New Game" button and wants to start over. */

      clearBoard();               // start a new game board
      messageText.setText(beginMessage);
      repaint();
    }
    else
    {
      System.out.println("error in actionPerformed(): ActionEvent not recognized: "
        + event);
    }
  } // end of actionPerformed() method


/*
  clearBoard() method

  Create a new game board, or clear the existing game board to all empty
  positions.

  This method should not do any GUI calls such as repaint(), because there are
  several methods that make changes to GUI objects after calling clearBoard()
  and before they are ready to repaint.  If clearBoard() forced a repaint, then
  too many unnecessary paint operations would be performed.
*/
  void clearBoard()
  {
    if ((boardData == null)                 // if no previous game board
      || (boardData.length != numRows)      // if new number of rows
      || (boardData[0].length != numCols))  // if new number of columns
    {
      boardData = new int[numRows][numCols]; // array for board positions
      boardValues = new int[numRows][numCols]; // array for estimated values
      createPathList();           // create list of winning lines in game board
    }

    for (int row = 0; row < numRows; row ++)
      for (int col = 0; col < numCols; col ++)
        boardData[row][col] = PlayNONE; // make all positions empty

    gameState = PlayUSER;         // user's turn to move

  } // end of clearBoard() method


/*
  createPathList() method

  Create a list of paths representing the possible winning lines in the game
  board.  This method must be called when the board is initialized or after the
  board size changes.

  Having a separate loop for horizontal logic, for vertical logic, and for
  diagonal logic looks like an easy (if somewhat lengthy) solution, but is
  actually a harder way of traversing the game board.  A better way is to
  create an array with one entry for each possible line through the game board.
  Each entry has:

    [0] = the starting row number (index)
    [1] = the starting column number (index)
    [2] = the row increment (-1, 0, +1) to the next board position
    [3] = the column increment

  The number of diagonal paths is two, if the number of rows and columns are
  equal, but increases as the two diverge.
*/
  void createPathList()
  {
    int col;                      // temporary column number (index)
    int path;                     // temporary path number (index)
    int row;                      // temporary row number (index)

    numPaths = numRows + numCols + 2 + (2 * Math.abs(numRows - numCols));
    pathList = new int[numPaths][4];
    path = 0;                     // no paths in <pathList> yet

    for (row = 0; row < numRows; row ++)
    {                             // add all horizontal rows as paths
      pathList[path][0] = row;    // this row number
      pathList[path][1] = 0;      // all rows start in first column
      pathList[path][2] = 0;      // next position is in same row
      pathList[path][3] = +1;     // next position is in next column
      path ++;                    // index of next path in list
    }

    for (col = 0; col < numCols; col ++)
    {                             // add all vertical columns as paths
      pathList[path][0] = 0;      // all columns start in first row
      pathList[path][1] = col;    // this column number
      pathList[path][2] = +1;     // next position is in next row
      pathList[path][3] = 0;      // next position is in same column
      path ++;                    // index of next path in list
    }

    /* If the rows and columns are equal, then there are exactly two diagonal
    paths.  If there are fewer rows than columns, then the extra columns also
    have diagonals.  If there more rows than columns, then the extra rows have
    diagonals. */

    if (numRows <= numCols)       // diagonals are positioned differently
    {
      for (col = 0; col <= Math.abs(numRows - numCols); col ++)
      {                           // add top-left to bottom-right diagonals
        pathList[path][0] = 0;    // these diagonals start in first row
        pathList[path][1] = col;  // starting column number
        pathList[path][2] = +1;   // next position is in next row
        pathList[path][3] = +1;   // next position is in next column
        path ++;                  // index of next path in list
      }

      for (col = 0; col <= Math.abs(numRows - numCols); col ++)
      {                           // add top-right to bottom-left diagonals
        pathList[path][0] = 0;    // these diagonals start in first row
        pathList[path][1] = (numCols - col - 1); // starting column number
        pathList[path][2] = +1;   // next position is in next row
        pathList[path][3] = -1;   // next position is in previous column
        path ++;                  // index of next path in list
      }
    }
    else // numRows > numCols
    {
      for (row = 0; row <= Math.abs(numRows - numCols); row ++)
      {                           // add top-left to bottom-right diagonals
        pathList[path][0] = row;  // starting row number
        pathList[path][1] = 0;    // these diagonals start in first column
        pathList[path][2] = +1;   // next position is in next row
        pathList[path][3] = +1;   // next position is in next column
        path ++;                  // index of next path in list
      }

      for (row = 0; row <= Math.abs(numRows - numCols); row ++)
      {                           // add bottom-left to top-right diagonals
        pathList[path][0] = (numRows - row - 1); // starting row number
        pathList[path][1] = 0;    // these diagonals start in first column
        pathList[path][2] = -1;   // next position is in previous row
        pathList[path][3] = +1;   // next position is in next column
        path ++;                  // index of next path in list
      }
    }

    /* After reading the above section, you will see that having three
    different ways (horizontal, vertical, and diagonal) of traversing the game
    board could create six special cases in every piece of code that analyzes
    the board.  By using a single list with enough information to recreate each
    path, we reduce the number of special cases that the code must consider,
    and thereby also reduce our chances of programming errors. */

  } // end of createPathList() method


/*
  drawBigO() method

  Draw an "O" for a player symbol at the given coordinates and size.  If the
  size is too small, then just draw a filled circle.  Note that this method
  usually returns with the drawing color set to the background color.

  We draw the "O" as a larger circle with a smaller center circle removed.
*/
  void drawBigO(
    Graphics gr,                  // graphics context
    int hz,                       // x coordinate of top-left corner
    int vt,                       // y coordinate of top-right corner
    int size)                     // size of symbol in pixels
  {
    int width;                    // width of line forming the circle

    gr.fillOval(hz, vt, size, size); // start by filling the outer circle
    if (size > 15)                // if size is big enough, remove center
    {
      width = (int) (size * 0.14); // make line width proportional to size
      gr.setColor(BACKGROUND);    // erase center by drawing background color
      gr.fillOval((hz + width), (vt + width), (size - (2 * width)),
        (size - (2 * width)));
    }
  } // end of drawBigO() method


/*
  drawBigX() method

  Draw an "X" for a player symbol at the given coordinates and size.  If the
  size is too small, then just draw a filled square.  Unlike drawBigO(), this
  method does not change the drawing color.

  We could draw the "X" as a single polygon shape with 16 points.  However, we
  get a more regular "X" if we draw two linear polygon shapes with 6 points
  each.  A small, additional border is added around the "X" so that an "X"
  appears to be more in proportion with an "O".
*/
  void drawBigX(
    Graphics gr,                  // graphics context
    int hz,                       // x coordinate of top-left corner
    int vt,                       // y coordinate of top-right corner
    int size)                     // size of symbol in pixels
  {
    int border;                   // additional border to make "X" look smaller
    int width;                    // width of line forming the cross
    int[] xlist;                  // x coordinates for polygon shape
    int[] ylist;                  // y coordinates for polygon shape

    if (size > 15)                // if big enough to draw an "X" (cross)
    {
      border = (int) (size * 0.04); // extra border is proportional to size
      width = (int) (size * 0.10); // make line width proportional to size
      xlist = new int[6];         // six points needed to describe one stroke
      ylist = new int[6];

      /* Draw stroke from top left to bottom right. */

      xlist[0] = hz + border;     // first x coordinate, clockwise
      xlist[1] = hz + border + width;
      xlist[2] = hz + size - border;
      xlist[3] = hz + size - border;
      xlist[4] = hz + size - border - width;
      xlist[5] = hz + border;

      ylist[0] = vt + border;     // first y coordinate, clockwise
      ylist[1] = vt + border;
      ylist[2] = vt + size - border - width;
      ylist[3] = vt + size - border;
      ylist[4] = vt + size - border;
      ylist[5] = vt + border + width;

      gr.fillPolygon(xlist, ylist, 6); // draw stroke as a filled polygon

      /* Draw overlapping stroke from top right to bottom left. */

      xlist[0] = hz + size - border; // first x coordinate, clockwise
      xlist[1] = hz + size - border;
      xlist[2] = hz + border + width;
      xlist[3] = hz + border;
      xlist[4] = hz + border;
      xlist[5] = hz + size - border - width;

      ylist[0] = vt + border;     // first y coordinate, clockwise
      ylist[1] = vt + border + width;
      ylist[2] = vt + size - border;
      ylist[3] = vt + size - border;
      ylist[4] = vt + size - border - width;
      ylist[5] = vt + border;

      gr.fillPolygon(xlist, ylist, 6); // draw stroke as a filled polygon
    }
    else
    {
      gr.fillRect(hz, vt, size, size); // too small, just fill square
    }
  } // end of drawBigX() method


/*
  itemStateChanged() method

  This method is called when the user changes a row or column counter.  We
  assume that the value returned from the GUI is in the proper range.  A new
  game is started.
*/
  public void itemStateChanged(ItemEvent event)
  {
    Object source = event.getSource(); // where the event came from
    if (source == columnCounter)  // new number of columns?
    {
      numCols = Integer.parseInt(columnCounter.getSelectedItem());
      clearBoard();               // start a new game board
      messageText.setText("New game board is " + numRows + " rows by "
        + numCols + " columns.");
      repaint();
    }
    else if (source == rowCounter) // new number of rows?
    {
      numRows = Integer.parseInt(rowCounter.getSelectedItem());
      clearBoard();               // start a new game board
      messageText.setText("New game board is " + numRows + " rows by "
        + numCols + " columns.");
      repaint();
    }
    else
    {
      System.out.println("error in itemStateChanged(): ItemEvent not recognized: "
        + event);
    }
  } // end of itemStateChanged() method


/*
  markWinningPath() method

  A path in the game board is a winning path.  Mark this path so that it will
  be displayed differently the next time the board is redrawn.
*/
  void markWinningPath(
    int startRow,                 // starting row number (index)
    int startCol,                 // starting column number (index)
    int rowIncr,                  // row increment
    int colIncr)                  // column increment
  {
    int col;                      // temporary column number (index)
    int row;                      // temporary row number (index)

    row = startRow;               // path starts with this row
    col = startCol;               // and with this column
    while ((row >= 0) && (row < numRows) && (col >= 0) && (col < numCols))
    {
      switch (boardData[row][col])
      {
        case PlayCOMPUTER:        // computer's position
          boardData[row][col] = PlayWINCOMP; // change to winning position
          break;

        case PlayUSER:            // user's position
          boardData[row][col] = PlayWINUSER; // change to winning position
          break;

        default:
          System.out.println("error in markWinningPath(): bad boardData["
            + row + "][" + col + "] = " + boardData[row][col]);
      }
      row += rowIncr;           // row index of next position
      col += colIncr;           // column index of next position
    }
  } // end of markWinningPath() method


/*
  mouseClicked() method

  This method is called when the user clicks the mouse on the game board.  To
  be consistent with the (x,y) coordinates in paint(), this mouse listener is
  for the whole applet window -- not just the dummy board canvas.  We must
  determine:

  (1) if the user is allowed to choose a board position (user's turn to move);
  (2) which position the mouse is pointing at;
  (3) if the position is empty (available); and
  (4) if choosing the position ends the game.

  Our calculations make use of several global variables set by the paint()
  method.
*/
  public void mouseClicked(MouseEvent event)
  {
    int col;                      // calculate column number
    int colExtra;                 // remainder from column calculation
    int row;                      // calculate row number
    int rowExtra;                 // remainder from row calculation

    /* Convert the (x,y) coordinates into row and column numbers, with a little
    extra information to tell us if the user was clicking on a board position,
    or if the clicks are on an inner or outer border. */

    col = (event.getX() - boardLeftMargin) / boardGridStep;
    colExtra = (event.getX() - boardLeftMargin) % boardGridStep;

    row = (event.getY() - boardTopMargin) / boardGridStep;
    rowExtra = (event.getY() - boardTopMargin) % boardGridStep;

    /* Now start checking if this mouse click is a legal move. */

    if (gameState == PlayNONE)
    {
      /* There is no active game, so mouse clicks aren't useful. */

      messageText.setText("This game is finished.  You must start a new game before you can move again.");
    }
    else if (gameState != PlayUSER)
    {
      /* It's the computer's turn to move, not the user's. */

      messageText.setText("Sorry, it's not your turn to move.  The computer is thinking.");
    }
    else if ((col < 0)            // if click is outside game board
      || (col >= numCols)
      || (colExtra < boardInnerBorder) // or on inner border
      || (colExtra > (boardInnerBorder + boardSymbolSize))
      || (row < 0)                // if click is outside game board
      || (row >= numRows)
      || (rowExtra < boardInnerBorder) // or on inner border
      || (rowExtra > (boardInnerBorder + boardSymbolSize)))
    {
      /* Ignore clicks that are not directly on a board position. */

      messageText.setText("Please click on a board position.");
    }
    else if (boardData[row][col] != PlayNONE)
    {
      /* The user clicked on a position that is already occupied. */

      messageText.setText("Sorry, that board position has already been chosen.");
    }
    else
    {
      boardData[row][col] = PlayUSER; // user now occupies this position
      messageText.setText("Your move is row " + (row + 1) + " and column "
        + (col + 1) + ".");
      gameState = PlayCOMPUTER;
      moveComputer();             // check for winner, make computer's move
    }
    repaint();

  } // end of mouseClicked() method

  public void mouseEntered(MouseEvent event) { }
  public void mouseExited(MouseEvent event) { }
  public void mousePressed(MouseEvent event) { }
  public void mouseReleased(MouseEvent event) { }


/*
  moveComputer() method

  This method is called after the user makes a move (that is, chooses a board
  position), or passes (that is, skips his/her turn).  We must do more than
  just make the computer's move.  We must:

  (1) do nothing if there is no active game (a sensible precaution);
  (2) check if the user has won;
  (3) check if the game is tied, because no empty positions remain;
  (4) choose a move for the computer;
  (5) check if the computer has won; and
  (6) check again if the game is tied, because no empty positions remain.

  Most of those steps involve traversing the game board horizontally,
  vertically, and diagonally in a similar fashion.  A more interesting approach
  is to traverse the board only once or twice, collecting information as we go
  along, and analyzing that information later to make a decision.  In other
  words, hold onto your hats, because this is a multi-tasking kind of method!
  (Well, not literally, because there are no Java threads involved.  I should
  be more careful with computer terms like "multi-tasking" even if they have
  entered common speech with a slightly different meaning.)

  Note that this method doesn't need to know anything about the GUI interface
  (except maybe setting the message text), and doesn't need to do any painting.
  Both places where moveComputer() is called will do a repaint later anyway.
*/
  void moveComputer()
  {
    int col;                      // temporary column number (index)
    int maxOccurs;                // how many times <maxValue> occurs in the
                                  // ... <boardValues> array
    int maxValue;                 // maximum value found in <boardValues>
    int moveCol;                  // column of computer's next move
    int moveRow;                  // row of computer's next move
    int row;                      // temporary row number (index)

    /* Check if we shouldn't have been called.  This won't happen when the
    program is finished, but may happen during debugging. */

    if (gameState != PlayCOMPUTER)
    {
      System.out.println("error in moveComputer(): called with gameState = "
        + gameState);
      return;                     // error ends moveComputer()
    }

    /* Check if someone wins, the game is tied, or no useful moves remain. */

    scanBoard(true);              // check board for winners and losers
    if (gameState == PlayNONE)    // nothing to do if game is now over
      return;

    /* Somebody can still win this game.  This may not be the computer....
    Choose a board position with the maximum value estimated by scanBoard().
    If the computer can't win, then we can at least aggressively block the user
    and hasten the end of the game! */

    maxOccurs = 0;                // no occurrences of maximum found yet
    maxValue = -1;                // default is less than any position's value
    moveCol = moveRow = -1;       // bad value just to make compiler happy
    for (row = 0; row < numRows; row ++)
    {
      for (col = 0; col < numCols; col ++)
      {
        if (boardValues[row][col] > maxValue)
        {
          maxValue = boardValues[row][col]; // set new maximum
          maxOccurs = 1;          // first occurrence
          moveRow = row;          // remember first position with this maximum
          moveCol = col;
        }
        else if (boardValues[row][col] == maxValue)
          maxOccurs ++;           // count the number of occurences
      }
    }

    /* If more than one position has the same maximum value, then make a random
    selection because this is more interesting for the user. */

    if (maxOccurs > 1)            // make a selection if more than one choice
    {
      int looking = 1 + (int) (Math.random() * maxOccurs);
                                  // random number from 1 to <maxOccurs>
      int current = 0;            // current occurence found

      for (row = 0; row < numRows; row ++)
      {
        for (col = 0; col < numCols; col ++)
        {
          if (boardValues[row][col] == maxValue)
          {
            current ++;           // found another occurence
            if (current == looking) // is this the one we're looking for?
            {
              moveRow = row;      // yes, save row number
              moveCol = col;      // save column number
              break;              // break out of for column loop
            }
          }
        }
        if (current == looking)   // did we find what we were looking for?
          break;                  // yes, break out of for row loop
      }
      if (current != looking)
        System.out.println("error in moveComputer(): current " + current
          + " is not equal to looking " + looking);
    }

    /* The selected empty position becomes the computer's move. */

    boardData[moveRow][moveCol] = PlayCOMPUTER;
    gameState = PlayUSER;         // user's turn to move
    scanBoard(false);             // check board for winners and losers

  } // end of moveComputer() method


/*
  paint() method

  Draw the game board directly onto the lower part of the applet window.  After
  this method returns, other AWT components (buttons, counters, etc) will draw
  themselves on *top* of whatever we draw here.  We estimate the starting
  position of the game board from the <boardCanvas> component.  Several global
  variables are set for later use by the mouse listener to determine where
  board positions are located.

  When an applet runs on a web page, the initial window size is chosen by the
  web page's HTML code and can't be changed by the applet.  Applets running
  outside of a web page (such as with Sun's applet viewer) can change their
  window size at any time.  The user may enlarge or reduce the window to make
  it fit better on his/her display.  Hence, while this applet doesn't attempt
  to change the window size, it must accept that the window size may be
  different each time the paint() method is called.  A good applet redraws its
  components to fit the window size.
*/
  public void paint(Graphics gr)
  {
    int boardHeight;              // height (in pixels) of actual game board
    int boardWidth;               // width (in pixels) of actual game board
    int col;                      // temporary column number (index)
    int hz;                       // temporary number of horizontal pixels
    int lineWidth;                // width of grid lines (in pixels)
    int row;                      // temporary row number (index)
    int vt;                       // temporary number of vertical pixels

    /* Clear the entire applet window to our own background color, not the
    default (which is sometimes white and sometimes grey). */

//  gr.setColor(BACKGROUND);
//  gr.fillRect(0, 0, getWidth(), getHeight());

    /* If the current message field is a complaint from us about the applet
    window being too small, then clear the message text.  Should the window
    problem persist, we will regenerate the error message anyway. */

    if (messageText.getText().startsWith("Applet window"))
      messageText.setText(noMessage);

    /* The <boardCanvas> component gets positioned below our buttons and the
    message field.  The assigned size of <boardCanvas> is not important; we
    only need to know the starting y coordinate, so that we can draw the board
    below all the other AWT components.  We calculate the size of the board (in
    pixels) using the applet's window size minus the starting y coordinate of
    <boardCanvas>, minus a border.  The border serves two purposes: to prevent
    the game board from touching the window's edge, and as a safety zone above
    the board where the other AWT components will appear along with a lower
    border that is *not* accounted for in the starting y coordinate for
    <boardCanvas>. */

    boardLeftMargin = canvasBorder; // first estimate for board's left margin
    // JDK1.1 note: replace boardCanvas.getY() with boardCanvas.getLocation().y
    boardTopMargin = boardCanvas.getLocation().y + canvasBorder;
                                  // first estimate for board's top margin
    // JDK1.1 note: replace this.getWidth() with this.getSize().width
    boardWidth = this.getSize().width - boardLeftMargin - canvasBorder;
    boardWidth = (boardWidth > 0) ? boardWidth : 0;
    // JDK1.1 note: replace this.getHeight() with this.getSize().height
    boardHeight = this.getSize().height - boardTopMargin - canvasBorder;
    boardHeight = (boardHeight > 0) ? boardHeight : 0;

    /* Estimate size of each board position, including inner borders and grid
    lines between positions.  Inner borders and grid lines are proportional,
    with a minimum size of one pixel.  Board squares (positions) must be kept
    ... square. */

    hz = boardWidth / numCols;    // possible pixels per column
    vt = boardHeight / numRows;   // possible pixels per row
    boardGridStep = (hz < vt) ? hz : vt; // choose smaller of the two sizes
    lineWidth = (int) (boardGridStep * 0.03);
                                  // pixels for grid lines
    lineWidth = (lineWidth > 1) ? lineWidth : 1;
                                  // minimum of 1 pixel
    boardInnerBorder = (int) (boardGridStep * 0.06);
                                  // pixels for inner borders
    boardInnerBorder = (boardInnerBorder > 1) ? boardInnerBorder : 1;
                                  // minimum of 1 pixel
    boardSymbolSize = boardGridStep - lineWidth - (2 * boardInnerBorder);
                                  // pixels for each position's player symbol
    boardSymbolSize = (boardSymbolSize > 10) ? boardSymbolSize : 10;
                                  // minimum of 10 pixels
    boardGridStep = boardSymbolSize + (2 * boardInnerBorder) + lineWidth;
                                  // re-calculate: result will be positive

    /* Compute a new left margin and top margin so that our game board will be
    centered on the panel. */

    hz = (boardWidth - (numCols * boardGridStep) + lineWidth) / 2;
    if (hz < 0)
    {
      messageText.setText("Applet window is too narrow to display " + numCols
        + " columns.");
      hz = 0;                     // reset and continue
    }
    boardLeftMargin += hz;        // plus defined left border

    vt = (boardHeight - (numRows * boardGridStep) + lineWidth) / 2;
    if (vt < 0)
    {
      messageText.setText("Applet window is too short to display " + numRows
        + " rows.");
      vt = 0;                     // reset and continue
    }
    boardTopMargin += vt;         // plus defined top border

    /* Draw vertical grid lines between columns. */

    gr.setColor(ColorGRIDLINE);
    hz = boardLeftMargin + boardGridStep - lineWidth;
                                  // x coordinate of first grid line
    vt = (numRows * boardGridStep) - lineWidth; // vertical pixels (constant)
    for (col = 1; col < numCols; col ++)
    {
      gr.fillRect(hz, boardTopMargin, lineWidth, vt);
      hz += boardGridStep;        // x coordinate for next column
    }

    /* Draw horizontal grid lines between rows. */

    vt = boardTopMargin + boardGridStep - lineWidth;
                                  // y coordinate of first grid line
    hz = (numCols * boardGridStep) - lineWidth; // horizontal pixels (constant)
    for (row = 1; row < numRows; row ++)
    {
      gr.fillRect(boardLeftMargin, vt, hz, lineWidth);
      vt += boardGridStep;        // y coordinate for next row
    }

    /* Draw the board. */

    vt = boardTopMargin + boardInnerBorder;
                                  // y coordinate of first position symbol in
                                  // ... first column in first row
    for (row = 0; row < numRows; row ++)
    {
      hz = boardLeftMargin + boardInnerBorder;
                                  // x coordinate of first position symbol in
                                  // ... first column in this row
      for (col = 0; col < numCols; col ++)
      {
        switch (boardData[row][col])
        {
          case PlayCOMPUTER:      // computer has this position
            gr.setColor(ColorCOMPUTER);
            drawBigO(gr, hz, vt, boardSymbolSize); // draw "O"
            break;

          case PlayNONE:         // empty position
            /* do nothing, paint nothing, keep background only */
            break;

          case PlayUSER:          // user has this position
            gr.setColor(ColorUSER);
            drawBigX(gr, hz, vt, boardSymbolSize); // draw "X"
            break;

          case PlayWINCOMP:       // computer wins with this position
            gr.setColor(ColorWINCOMP);
            drawBigO(gr, hz, vt, boardSymbolSize); // draw "O"
            break;

          case PlayWINUSER:       // user wins with this position
            gr.setColor(ColorWINUSER);
            drawBigX(gr, hz, vt, boardSymbolSize); // draw "X"
            break;

          default:
            System.out.println("error in paint(): bad boardData[" + row
              + "][" + col + "] = " + boardData[row][col]);
        }
        hz += boardGridStep;      // x coordinate for next column
      }
      vt += boardGridStep;        // y coordinate for next row
    }
  } // end of paint() method


/*
  scanBoard() method

  This is a general method to scan the game board to determine:

  (1) if there is a winner;
  (2) if the game is tied (no moves possible);
  (3) if the game is hopeless (empty positions but nobody can win); or
  (4) if the game should continue.

  The <gameState> variable will be set to <PlayNONE> if the game should end.

  As an option, while this method is scanning the board, it will also estimate
  the value (to the computer) of each empty board position.  This is an option
  because we only need this information before the computer moves, not
  afterward.
*/
  void scanBoard(
    boolean valueFlag)            // true if we estimate position values
  {
    int col;                      // temporary column number (index)
    int colIncr;                  // column increment
    int foundComputer;            // number of computer positions on this path
    int foundEmpty;               // number of empty positions on this path
    int foundTotal;               // total number of positions on this path
    int foundUser;                // number of user positions on this path
    int openComputerPaths;        // number of paths where the computer can
                                  // ... still make a move (no user positions)
    int openUserPaths;            // number of paths where the user can still
                                  // ... make a move (no computer positions)
    int path;                     // temporary path number (index)
    int row;                      // temporary row number (index)
    int rowIncr;                  // row increment
    int value;                    // temporary estimated value

    /* If there is no active game, then do nothing. */

    if (gameState == PlayNONE)
      return;

    /* Clear our estimated values for each board position. */

    if (valueFlag)                // if caller wants estimated values
    {
      for (row = 0; row < numRows; row ++)
        for (col = 0; col < numCols; col ++)
          boardValues[row][col] = 0;
    }

    /* Now examine each possible winning path in the game board.  If we find a
    winner, then we act upon that immediately.  Otherwise, we make notes for
    later:

    (1) The most valuable positions are those that will win the game for the
        computer.
    (2) The next most valuable are positions that will prevent the user from
        winning.
    (3) Third are empty paths, or paths with only computer positions.
    (4) Fourth are empty positions where the computer can block the user (that
        is, make a path useless to the user).  This speeds up the end of the
        game, when the computer can no longer win.
    (5) Paths with at least one computer position and at least one user
        position are of no value to anyone.
    */

    openComputerPaths = openUserPaths = 0;

    for (path = 0; path < numPaths; path ++)
    {
      row = pathList[path][0];    // starting row number (index)
      col = pathList[path][1];    // starting column number (index)
      rowIncr = pathList[path][2]; // row increment to next position
      colIncr = pathList[path][3]; // column increment to next position

      foundComputer = foundEmpty = foundTotal = foundUser = 0;

      while ((row >= 0) && (row < numRows) && (col >= 0) && (col < numCols))
      {
        switch (boardData[row][col])
        {
          case PlayCOMPUTER:      // computer's position
            foundComputer ++;
            break;

          case PlayNONE:          // empty position
            foundEmpty ++;
            break;

          case PlayUSER:          // user's position
            foundUser ++;
            break;

          default:                // error checking (should not occur)
            System.out.println("error in scanBoard(): bad boardData[" + row
              + "][" + col + "] = " + boardData[row][col]);
        }
        foundTotal ++;            // to compute total length of path
        row += rowIncr;           // row index of next position
        col += colIncr;           // column index of next position
      }

      /* A player wins if the number of player positions is equal to the total
      number of positions on this path.  Check the user first, out of courtesy,
      even though both the computer and the user can't win at the same time ...
      unless this program has a logic error. */

      if (foundUser == foundTotal) // user wins
      {
        gameState = PlayNONE;     // game is over
        markWinningPath(pathList[path][0], pathList[path][1], rowIncr, colIncr);
        messageText.setText("You win!  Congratulations!");
        return;                   // losing game ends scanBoard()
      }

      if (foundComputer == foundTotal) // computer wins
      {
        gameState = PlayNONE;     // game is over
        markWinningPath(pathList[path][0], pathList[path][1], rowIncr, colIncr);
        messageText.setText("The computer wins this game.");
        return;                   // winning game ends scanBoard()
      }

      /* Count how many paths are still available to each player (because the
      paths don't contain positions occupied by the other player). */

      if (foundComputer == 0)     // no computer is good for user
        openUserPaths ++;         // one more good path for user

      if (foundUser == 0)         // no user is good for computer
        openComputerPaths ++;     // one more good path for computer

      /* Now do the optional work of estimating the value of each empty square
      on this path. */

      if (valueFlag)              // if caller wants estimated values
      {
        value = 0;                // assume empty position has no value

        /* Single empty squares that will end the game have greater value. */

        if ((foundComputer + foundEmpty) == foundTotal)
          value += (foundEmpty == 1) ? ValueWINCOMP : ValueOPENCOMP;
        if ((foundUser + foundEmpty) == foundTotal)
          value += (foundEmpty == 1) ? ValueWINUSER : ValueOPENUSER;

        /* Add the estimated value to each empty position on this path. */

        if (value > 0)            // do this work only if something to add!
        {
          row = pathList[path][0];  // starting row number (index)
          col = pathList[path][1];  // starting column number (index)
          rowIncr = pathList[path][2]; // row increment to next position
          colIncr = pathList[path][3]; // column increment to next position

          while ((row >= 0) && (row < numRows) && (col >= 0) && (col < numCols))
          {
            if (boardData[row][col] == PlayNONE)
              boardValues[row][col] += value;
            row += rowIncr;       // row index of next position
            col += colIncr;       // column index of next position
          }
        }
      }

    } // end of for path loop

    /* We looked once at every path.  Nobody won.  If there are no more empty
    positions, then the game is tied.  We detect a tie game when all paths are
    "dead" because nobody can win. */

    if ((openComputerPaths == 0) && (openUserPaths == 0))
    {
      gameState = PlayNONE;       // game is over
      messageText.setText("Nobody can win this game.  The game is tied.");
//    return;                     // hopeless situation ends scanBoard()
    }

  } // end of scanBoard() method

} // end of TicTacToe4 class

// ------------------------------------------------------------------------- //

/*
  TicTacToe4Window class

  This applet can also be run as an application by calling the main() method
  instead of the init() method.  As an application, it must exit when its main
  window is closed.  A window listener is necessary because EXIT_ON_CLOSE is a
  JFrame option in Java Swing, not a basic AWT Frame option.  It is easier to
  extend WindowAdapter here with one method than to implement all methods of
  WindowListener in the main applet.
*/

class TicTacToe4Window extends WindowAdapter
{
  public void windowClosing(WindowEvent event)
  {
    System.exit(0);               // exit from this application
  }
} // end of TicTacToe4Window class

/* Copyright (c) 2004 by Keith Fenske.  Released under GNU Public License. */
