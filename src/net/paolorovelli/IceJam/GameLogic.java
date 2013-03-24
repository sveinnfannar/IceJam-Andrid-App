/**
 * This class provides the logic for the TrafficJam puzzle.
 *
 */
package net.paolorovelli.IceJam;

import java.util.*;
import java.util.regex.MatchResult;

public class GameLogic {
    public static final int NUM_COLS = 6;
    public static final int NUM_ROWS = 6;

    public static final int GOAL_COL = 5;
    public static final int GOAL_ROW = 3;
    public static final int GOAL_CAR_ID = 0;  // first can assumed to be the goal car.

    public static enum Orientation { Horizontal, Vertical }

    public static class Car {
        private Orientation m_orientation;
        private int m_col;
        private int m_row;
        private int m_length;

        public Car( Orientation orientation, int col, int row, int length ) {
            m_orientation = orientation;
            m_col = col;
            m_row = row;
            m_length = length;
        }

        public Car( Car other ) {
            m_orientation = other.m_orientation;
            m_col = other.m_col;
            m_row = other.m_row;
            m_length = other.m_length;
        }

        public int getCol() { return m_col; }

        public int getRow() { return m_row; }

        public Orientation getOrientation() { return m_orientation; }

        public int getLength() { return m_length; }

        public void slide( int offset ) {
            if ( getOrientation() == Orientation.Horizontal ) {
                m_col += offset;
            }
            else {
                m_row += offset;
            }
        }

        public String toString( ) {
            StringBuilder sb = new StringBuilder();
            sb.append( '(' );
            sb.append( getOrientation() == Orientation.Horizontal ? 'H' : 'V' );
            sb.append( ' ' );
            sb.append( getCol() );
            sb.append( ' ' );
            sb.append( getRow() );
            sb.append( ' ' );
            sb.append( getLength() );
            sb.append( ')' );
            return sb.toString();
        }

    }

    /**
     * Action class
     */
    public static class Action {

        private int m_id;
        private int m_offset;

        Action( int id, int offset ) {
            m_id = id;
            m_offset = offset;
        }

        public int getId()  { return m_id; }

        public int getOffset() { return m_offset; }

        public String toString( ) {
            StringBuilder sb = new StringBuilder();
            sb.append( '(' );
            sb.append( getId() );
            sb.append( ',' );
            sb.append( getOffset() );
            sb.append( ')' );
            return sb.toString();
        }
    }


    /**
     * Create a new car from a string representation
     *
     *   (orientation col row length)
     *
     *  e.g.  (H 1 2 2) or (V 2 3 3)
     *
     * The method verifies that the car will fall within the bounds of the puzzle
     *
     * @param carStr A string representing a car.
     * @return A car object if successful, null otherwise.
     */
    public static Car carFromString( String carStr ) {
        Car carReturn = null;
        //System.out.println( "[" + carStr + "]" );
        Scanner s = new Scanner( carStr );
        s.findInLine("\\s*\\(\\s*(\\w+)\\s*(\\d+)\\s*(\\d+)\\s*(\\d+)\\s*\\)\\s*");
        try {
            MatchResult result = s.match();
            if ( result.groupCount() == 4 ) {
                boolean isSuccessful = true;
                Orientation orientation = null;
                if ( result.group(1).equals("H") ) {
                    orientation = Orientation.Horizontal;
                }
                else if ( result.group(1).equals( "V" ) ) {
                    orientation = Orientation.Vertical;
                }
                else { isSuccessful = false; }
                if ( isSuccessful ) {
                    int col = Integer.parseInt( result.group( 2 ) );
                    int row = Integer.parseInt( result.group( 3 ) );
                    int length = Integer.parseInt( result.group( 4 ) );
                    Car car = new Car( orientation, col, row, length );
                    if ( isWithinBounds( car ) ) {
                        carReturn = car;
                    }
                }
            }
        }
        catch ( IllegalStateException e ) {
            // Match not found.
        }
        s.close();
        return carReturn;
    }


    /**
     *  Constructor
     */
    public GameLogic(  ) {
        m_cars = new ArrayList<Car>();
        m_isSolved = false;
    }


    /**
     * Setup a specific puzzle. The method verifies that the setup is legitimate, that is, the cars
     * do not overlap. It does not, however, check whether the puzzle is solvable.
     *
     * The first car in the input list is assumed to be the goal car.
     *
     * @param cars List of cars
     *
     * @return True if successful, false otherwise.
     */
    public boolean setup( List<Car> cars ) {
        m_cars.clear();
        m_isSolved = false;
        for ( Car car : cars ) {
            if ( !canPlace( car ) ) {
                return false;
            }
            if ( car == cars.get( GOAL_CAR_ID ) ) {
                m_isSolved = m_isSolved || doOverlap( car, m_goal );
            }
            m_cars.add( new Car( car ) );
        }
        updateGrid();
        return true;
    }


    /**
     * Setup a specific puzzle from a puzzle string:
     *
     *   (H 1 2 2), (V 3 3 3)
     *
     * The first car in the sequence is assumed to be the goal car.
     *
     * The method verifies that the setup is legitimate, that is, the cars
     * do not overlap. It does not, however, check whether the puzzle is solvable.
     *
     * @param puzzleStr A string representing a legitimate car sequence.
     *
     * @return True if successful, false otherwise.
     */
    public boolean setup( String puzzleStr ) {
        List<Car> cars = new ArrayList<Car>();
        String carsStr[] = puzzleStr.split( "," );

        for( String carStr : carsStr ) {
            Car car = carFromString( carStr );
            if ( car != null ) {
                cars.add( car );
            }
            else { return false; }
        }

        return setup( cars );
    }


    /**
     * Get a list of the actions possible in the current state.
     *
     * @return List of possible actions.
     */
    public List<Action> getActions( ) {
        List<Action> actions = new ArrayList<Action>( );
        if ( !isSolved() ) {
            updateGrid();
            for ( int n=m_cars.size(), i=0; i<n; ++i  ) {
                Car car   = m_cars.get(i);
                int colOn = car.getCol();
                int rowOn = car.getRow();
                if ( car.getOrientation() == Orientation.Horizontal ) {
                    for ( int col=colOn-1; col>=0 && m_grid[col][rowOn]==-1; --col ) {
                        actions.add( new Action( i, col-colOn ) );
                    }
                    colOn += car.getLength() - 1;
                    for ( int col=colOn+1; col<NUM_COLS && m_grid[col][rowOn]==-1; ++col ) {
                        actions.add( new Action( i, col-colOn ) );
                    }
                }
                else {
                    for ( int row=rowOn-1; row>=0 && m_grid[colOn][row]==-1; --row ) {
                        actions.add( new Action( i, row-rowOn ) );
                    }
                    rowOn += car.getLength() - 1;
                    for ( int row=rowOn+1; row<NUM_ROWS && m_grid[colOn][row]==-1; ++row ) {
                        actions.add( new Action( i, row-rowOn ) );
                    }
                }
            }
        }
        return actions;
    }


    /**
     * Convert a string representing an action to an actual action.
     *
     * @param actionStr  String representing the action.
     * @return An action if the input represents a legitimate action, null otherwise.
     */
    public Action strToAction( String actionStr ) {
        List<Action> actions = getActions();
        for ( Action action : actions ) {
            if ( actionStr.equals( action.toString() ) ) {
                return action;
            }
        }
        return null;
    }


    /**
     * Execute the given action in the current puzzle state and update it accordingly.
     *
     * @param action  The action to execute (assumed to be legitimate).
     */
    public void makeAction( Action action ) {
        Car car = m_cars.get( action.getId() );
        car.slide( action.getOffset() );
        if ( action.getId() == GOAL_CAR_ID ) {
            m_isSolved = doOverlap( car, m_goal );
        }
    }


    /**
     * Undo the given action.
     *
     * @param action  The action to undo (assumed to be the last action executed in the state).
     */
    public void retractAction( Action action ) {
        m_cars.get( action.getId() ).slide( -action.getOffset() );
        if ( m_isSolved ) {
            m_isSolved = false;
        }
    }


    /**
     * Check whether the puzzle has been solved.
     *
     * @return True if puzzle is solved, false otherwise.
     */
    public boolean isSolved( ) {
        return m_isSolved;
    }


    /**
     * Get information about the puzzle cars.
     *
     * @return List of cars.
     */
    public List<Car> getCars() {
        return Collections.unmodifiableList( m_cars );
    }


    /**
     * Returns a string representation of the puzzle state.
     *
     * @return  A string, representing the state.
     */
    public String toString()  {
        updateGrid( );
        StringBuilder sb = new StringBuilder();
        for ( int row=NUM_ROWS-1; row>=0; --row ) {
            for ( int col=0; col<NUM_COLS; ++col ) {
                if ( m_grid[col][row] == -1 ) {
                    sb.append( '.' );
                }
                else {
                    sb.append( m_grid[col][row] );
                }
            }
            sb.append( '\n' );
        }
        sb.append( "Solved: " );
        sb.append( isSolved() );
        sb.append( '\n' );
        return sb.toString();
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // A 'fake' car representing the goal (to make goal check easier as 'overlap check' ).
    private final Car m_goal = new Car( Orientation.Horizontal, GOAL_COL, GOAL_ROW, 1 );

    private List<Car> m_cars;
    private boolean   m_isSolved;
    private int[][]   m_grid = new int[NUM_COLS][NUM_ROWS];

    private static boolean intersect( int x1, int dx1, int x2, int dx2 ) {
        return ( (x1 <= x2) && (x2 < x1 + dx1) ) || ( (x2 <= x1) && (x1 < x2 + dx2) );
    }

    private static boolean doOverlap( Car car1, Car car2 ) {
        if ( car1.getOrientation() == Orientation.Horizontal ) {
            if ( car2.getOrientation() == Orientation.Horizontal ) {
                return (car1.getRow() == car2.getRow()) &&
                       intersect( car1.getCol(), car1.getLength(), car2.getCol(), car2.getLength() );
            }
            else {
                return intersect( car1.getCol(), car1.getLength(), car2.getCol(), 1 ) &&
                       intersect( car1.getRow(), 1, car2.getRow(), car2.getLength() );
            }
        }
        else {
            if ( car2.getOrientation() == Orientation.Vertical ) {
                return (car1.getCol() == car2.getCol()) &&
                       intersect( car1.getRow(), car1.getLength(), car2.getRow(), car2.getLength() );
            }
            else {
                return intersect( car1.getRow(), car1.getLength(), car2.getRow(), 1 ) &&
                       intersect( car1.getCol(), 1, car2.getCol(), car2.getLength() );
            }
        }
    }

    private static boolean isWithinBounds( Car carIn  ) {
        boolean ok;
        if ( carIn.getOrientation() == Orientation.Horizontal ) {
            ok = (carIn.getCol() >= 0) && ((carIn.getCol() + carIn.getLength()) <= NUM_COLS);
        }
        else {
            ok = (carIn.getRow() >= 0) && ((carIn.getRow() + carIn.getLength()) <= NUM_ROWS);
        }
        return ok;
    }

    private boolean canPlace( Car carIn ) {
        if ( !isWithinBounds( carIn ) ) {
            return false;
        }
        for ( Car car : m_cars ) {
            if ( doOverlap( carIn, car ) ) {
                return false;
            }
        }
        return true;
    }

    private void updateGrid( ) {
        for ( int col=0; col<NUM_COLS; ++col ) {
            for ( int row=0; row<NUM_ROWS; ++row ) {
                m_grid[col][row] = -1;
            }
        }

        for ( int i=0; i<m_cars.size(); ++i ) {
            Car car = m_cars.get(i);
            if ( car.getOrientation() == Orientation.Horizontal ) {
                for ( int end=car.getCol()+car.getLength(), col=car.getCol(); col<end; ++col ) {
                    m_grid[col][car.getRow()] = i;
                }
            }
            else {
                for ( int end=car.getRow()+car.getLength(), row=car.getRow(); row<end; ++row ) {
                    m_grid[car.getCol()][row] = i;
                }
            }
        }
    }
}