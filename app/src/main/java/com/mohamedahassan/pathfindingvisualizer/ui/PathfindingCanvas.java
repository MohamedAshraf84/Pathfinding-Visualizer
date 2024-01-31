package com.mohamedahassan.pathfindingvisualizer.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.mohamedahassan.pathfindingvisualizer.R;

import java.security.SecureRandom;
import java.util.Objects;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class PathfindingCanvas extends View {

    public MazeNode[][]mMaze;
    private Pair<Integer, Integer> mStart;
    private Pair<Integer, Integer> mEnd;
    private static SecureRandom mRandom;
    private boolean mIsMazeInitialized;
    private float mCellSize;
    private Paint mMazePaint;
    private Paint mStartPaint;
    private Paint  mEndPaint;
    private Paint  mWeightPaint;
    private Paint  mVisitedPaint;
    private Paint  mWallPaint;
    private Paint  mCurrentPaint;
    private Paint mPathPaint;
    public static int NUM_OF_ROWS;
    public static int NUM_OF_COLUMNS;
    private int SCREEN_WIDTH;
    private int SCREEN_HEIGHT;
    private int xCoord = -1, yCoord = -1;
    private int whatToDraw = -1;
    private final int MIN_CELL_SIZE = 10;
    private final int INITIAL_CELL_SIZE = MIN_CELL_SIZE + 15;
    public final int MAX_WEIGHT = 3;
    public final static int START_NODE = 0;
    public final static int END_NODE = 1;
    public final static int WALL_NODE = 2;
    public final static int WEIGHT_NODE = 3;

    public void setStart(Pair<Integer, Integer> mStart) {
        this.mStart = mStart;
    }
    public Pair<Integer, Integer> getStart() {
        return mStart;
    }
    public void setEnd(Pair<Integer, Integer> mEnd) {
        this.mEnd = mEnd;
    }
    public Pair<Integer, Integer> getEnd() {
        return mEnd;
    }

    public PathfindingCanvas(Context context) {
        super(context);
        init(null);
    }

    public PathfindingCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PathfindingCanvas(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet set) {

        mMazePaint = new Paint();
        mWallPaint = new Paint();
        mWeightPaint = new Paint();
        mStartPaint = new Paint();
        mEndPaint = new Paint();
        mVisitedPaint = new Paint();
        mPathPaint = new Paint();
        mCurrentPaint = new Paint();
        mRandom = new SecureRandom();
        mCellSize = INITIAL_CELL_SIZE;
    }

    void initMaze() {

        if (!mIsMazeInitialized)
        {
            NUM_OF_ROWS = (int) (SCREEN_HEIGHT / mCellSize);
            NUM_OF_COLUMNS = (int) (SCREEN_WIDTH / mCellSize);
            mMaze = new MazeNode[NUM_OF_ROWS][NUM_OF_COLUMNS];

            for (int i = 0; i < NUM_OF_ROWS; ++i)
                for (int j = 0; j < NUM_OF_COLUMNS; ++j)
                    mMaze[i][j] = new MazeNode(new Pair<>(i, j));

            setStartAndTarget();
            mIsMazeInitialized = true;
        }

    }

    public void resetMaze()
    {
        for (int i = 0; i < NUM_OF_ROWS; ++i)
        {
            for (int j = 0; j < NUM_OF_COLUMNS; ++j) {

                if (!isWall(mMaze[i][j]) && !isEmpty(mMaze[i][j]) && !isWeight(mMaze[i][j]))
                {
                    mMaze[i][j].setVisited(false);
                    mMaze[i][j].setParent(null);
                    mMaze[i][j].setCost(Integer.MAX_VALUE);

                    if (isVisitedWeight(mMaze[i][j]) || isWeightPath(mMaze[i][j]))
                    {
                        mMaze[i][j].setState(NodeState.WEIGHT);
                        continue;
                    }

                    if (isStart(mMaze[i][j]) || isEnd(mMaze[i][j]))
                        continue;

                    mMaze[i][j].setWeight(0);
                    mMaze[i][j].setState(NodeState.EMPTY);
                }
            }
        }
        invalidate();
    }

    private void setStartAndTarget()
    {
        do {
            mStart = generateRandomCoordinate();
        } while (mMaze[mStart.first][mStart.second].getState() == NodeState.WALL);

        do {
            mEnd =  generateRandomCoordinate();
        } while (mStart.equals(mEnd) && mMaze[mEnd.first][mEnd.second].getState() == NodeState.WALL);

        mMaze[mStart.first][mStart.second].setState(NodeState.START);
        mMaze[mEnd.first][mEnd.second].setState(NodeState.END);
    }

    public static Pair<Integer, Integer> generateRandomCoordinate() {
        return new Pair<>(mRandom.nextInt(NUM_OF_ROWS), mRandom.nextInt(NUM_OF_COLUMNS));
    }

    public void setSelectedNode(int whatToDraw)
    {
        this.whatToDraw = whatToDraw;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        xCoord = (int) (event.getY() / mCellSize);
        yCoord = (int) (event.getX() / mCellSize);

        if (isInsideGrid()) {
            int stX = mStart.first, stY = mStart.second;
            int endX = mEnd.first, endY = mEnd.second;
            if ( ((xCoord != stX  || yCoord != stY)  && (xCoord != endX || yCoord != endY)) &&
                   !isWall(mMaze[xCoord][yCoord])) {

                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (whatToDraw == WALL_NODE) {
                        mMaze[xCoord][yCoord].setState(NodeState.WALL);
                    } else if (whatToDraw == WEIGHT_NODE) {
                        mMaze[xCoord][yCoord].setState(NodeState.WEIGHT);
                        mMaze[xCoord][yCoord].setWeight(1 + mRandom.nextInt(MAX_WEIGHT));
                    } else if (whatToDraw == START_NODE) {
                        mMaze[stX][stY].setState(NodeState.EMPTY);
                        mStart = new Pair<>(xCoord, yCoord);
                        mMaze[xCoord][yCoord].setState(NodeState.START);
                    } else if (whatToDraw == END_NODE) {
                        mMaze[endX][endY].setState(NodeState.EMPTY);
                        mEnd = new Pair<>(xCoord, yCoord);
                        mMaze[xCoord][yCoord].setState(NodeState.END);
                    }
                }
                invalidate();
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    private boolean isInsideGrid() {
        return xCoord >= 0 && yCoord >= 0 && xCoord < NUM_OF_ROWS && yCoord < NUM_OF_COLUMNS;
    }

    @Override
    protected void onDraw(@androidx.annotation.NonNull Canvas canvas) {
        super.onDraw(canvas);
        Log.d("DR", "onDraw");

        mMazePaint.setStyle(Paint.Style.STROKE);
        mMazePaint.setColor(getResources().getColor(R.color.maze_color));
        mMazePaint.setStrokeWidth(2);
        mMazePaint.setAntiAlias(true);

        mWallPaint.setStyle(Paint.Style.FILL);
        mWallPaint.setColor(getResources().getColor(R.color.wall_node_color));
        mWallPaint.setAntiAlias(true);

        mWeightPaint.setStyle(Paint.Style.FILL);
        mWeightPaint.setColor(Color.RED);
        mWeightPaint.setAntiAlias(true);

        mStartPaint.setStyle(Paint.Style.FILL);
        mStartPaint.setColor(Color.BLUE);
        mStartPaint.setAntiAlias(true);
        
        
        mEndPaint.setStyle(Paint.Style.FILL);
        mEndPaint.setColor(Color.GREEN);
        mEndPaint.setAntiAlias(true);
        
        mVisitedPaint.setStyle(Paint.Style.FILL);
        mVisitedPaint.setColor(getResources().getColor(R.color.visited_node_color));
        mVisitedPaint.setAntiAlias(true);


        mPathPaint.setStyle(Paint.Style.FILL);
        mPathPaint.setColor(getResources().getColor(R.color.path_node_color));
        mPathPaint.setAntiAlias(true);

        mCurrentPaint.setStyle(Paint.Style.FILL);
        mCurrentPaint.setColor(getResources().getColor(R.color.current_node_color));
        mCurrentPaint.setAntiAlias(true);

        drawGrid(canvas);
        drawMaze(canvas);
    }

    private void drawGrid(Canvas canvas) {
        drawRows(canvas);
        drawColumns(canvas);
    }
    private void drawRows(Canvas canvas) {
        for (int row = 0; row <= NUM_OF_ROWS; ++row) {
            canvas.drawLine( 0.0f, mCellSize * row, SCREEN_WIDTH - SCREEN_WIDTH % mCellSize, mCellSize * row, mMazePaint);
        }
    }

    private void drawColumns(Canvas canvas) {
        for (int col = 0; col <= NUM_OF_COLUMNS; ++col) {
            canvas.drawLine(mCellSize * col, 0.0f, mCellSize * col, SCREEN_HEIGHT - (SCREEN_HEIGHT % mCellSize), mMazePaint);
        }
    }

    public void drawMaze(Canvas canvas)
    {
        for (int i = 0; i < NUM_OF_ROWS; ++i) {
            for (int j = 0; j < NUM_OF_COLUMNS; ++j) {

                MazeNode cur = mMaze[i][j];
                if(isStart(cur)) {
                    drawNode(canvas, R.drawable.angle_right, i, j);
                }
                else if(isEnd(cur)) {
                    drawNode(canvas, R.drawable.end_node, i, j);
                }
                else if (isWall(cur)) {
                    drawNode(canvas, i, j, mWallPaint);
                }
                else if(isVisited(cur)) {
                    drawNode(canvas, i, j, mVisitedPaint);
                }
                else if (isWeight(cur)) {
                    drawWeightNode(canvas, i, j);
                }
                else if(isVisitedWeight(cur)) {
                    drawNode(canvas, i, j, mVisitedPaint);
                    drawWeightNode(canvas, i, j);
                }
                else if(isWeightPath(cur)) {
                    drawNode(canvas, i, j, mPathPaint);
                    drawWeightNode(canvas, i, j);
                }
                else if (isPath(cur)) {
                    drawNode(canvas, i, j, mPathPaint);
                }
                else if (isCurrent(cur))
                {
                    drawNode(canvas, i, j, mCurrentPaint);
                }
                else if (cur.getState() == NodeState.PATH_L)
                {
                    Bitmap rotatedBitmap = rotateDrawable(
                            Objects.requireNonNull(
                            ResourcesCompat.getDrawable(getResources(), R.drawable.angle_right, null)),
                            -180);
                    drawNode(canvas, rotatedBitmap, i, j);
                }
                else if (cur.getState() == NodeState.PATH_R)
                {
                    drawNode(canvas, R.drawable.angle_right, i, j);
                }
                else if (cur.getState() == NodeState.PATH_U)
                {
                    drawNode(canvas, R.drawable.angle_up, i, j);
                }
                else if (cur.getState() == NodeState.PATH_D)
                {
                    Bitmap rotatedBitmap = rotateDrawable(
                            Objects.requireNonNull(
                            ResourcesCompat.getDrawable(getResources(), R.drawable.angle_up, null)),
                            180);
                    drawNode(canvas, rotatedBitmap, i, j);
                }
            }
        }
    }

    private void drawWeightNode(Canvas canvas, int r, int c)
    {
        if(mMaze[r][c].getWeight() == 1)
        {
            drawNode(canvas, R.drawable.weight1_node, r, c);
        }else if(mMaze[r][c].getWeight() == 2)
        {
            drawNode(canvas, R.drawable.weight2_node, r, c);
        }else if(mMaze[r][c].getWeight() == 3)
        {
            drawNode(canvas, R.drawable.weight3_node, r, c);
        }
    }

    private Bitmap rotateDrawable(Drawable drawable, float degrees) {
        Bitmap bitmap = getBitmap(drawable);
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void drawNode(Canvas canvas, int drawable, float r, float c)
    {
        Bitmap img = getBitmap(Objects.requireNonNull(ResourcesCompat.getDrawable(getResources(), drawable, null)));
        if (!img.isRecycled()) {
            canvas.drawBitmap(img, c * mCellSize, r * mCellSize, null);
        }
    }

    private void drawNode(Canvas canvas, Bitmap bitmap, int r, int c)
    {
        canvas.drawBitmap(bitmap, c * mCellSize, r * mCellSize, null);
    }

    private void drawNode(Canvas canvas, int x, int y, Paint paint) {
        if(mMaze[x][y].getState() != NodeState.CURRENT)
            canvas.drawRect(y * mCellSize, x * mCellSize , (y + 1) * mCellSize, (x + 1) * mCellSize, paint);
        else
            canvas.drawCircle(y * mCellSize +  mCellSize / 2 , x  * mCellSize + mCellSize / 2, mCellSize / 4,  paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize, heightSize;
        int widthMode, heightMode;

        widthMode = MeasureSpec.getMode(widthMeasureSpec);
        widthSize = MeasureSpec.getSize(widthMeasureSpec);
        heightMode = MeasureSpec.getMode(heightMeasureSpec);
        heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int desiredWidth = 400, desiredHeight = 700;
        if (widthMode == MeasureSpec.EXACTLY) {
            SCREEN_WIDTH = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            SCREEN_WIDTH = Math.min(desiredWidth, widthSize);
        } else {
            SCREEN_WIDTH = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            SCREEN_HEIGHT = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            SCREEN_HEIGHT = Math.min(desiredHeight, heightSize);
        } else {
            SCREEN_HEIGHT = desiredHeight;
        }

        setMeasuredDimension(SCREEN_WIDTH, SCREEN_HEIGHT);

        initMaze();
    }

    public void clearPath()
    {
        resetMaze();
    }

    public void clear() {
        for (int i = 0; i < NUM_OF_ROWS; ++i)
        {
            for (int j = 0; j < NUM_OF_COLUMNS; ++j) {
                mMaze[i][j].setVisited(false);
                mMaze[i][j].setParent(null);
                mMaze[i][j].setWeight(0);
                mMaze[i][j].setCost(Integer.MAX_VALUE);
                if (isStart(mMaze[i][j]) || isEnd(mMaze[i][j]))
                    continue;
                mMaze[i][j].setState(NodeState.EMPTY);
            }
        }
        invalidate();
    }

    public void clearAll() {
        clear();
        mMaze[mStart.first][mStart.second].setState(NodeState.EMPTY);
        mMaze[mEnd.first][mEnd.second].setState(NodeState.EMPTY);
        invalidate();
    }
    public void resizeGrid(int cellSize) {
        this.mCellSize = MIN_CELL_SIZE + cellSize;
        mIsMazeInitialized = false;
        initMaze();
        invalidate();
    }

    private Bitmap getBitmap(Drawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap((int) mCellSize,
                (int) mCellSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds( 0,  0, (int) mCellSize, (int) mCellSize);
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    boolean isStart(MazeNode node)
    {
        return node.getState() == NodeState.START;
    }
    boolean isEnd(MazeNode node)
    {
        return node.getState() == NodeState.END;
    }
    boolean isWall(MazeNode node)
    {
        return node.getState() == NodeState.WALL;
    }
    boolean isEmpty(MazeNode node)
    {
        return node.getState() == NodeState.EMPTY;
    }
    boolean isVisited(MazeNode node)
    {
        return node.getState() == NodeState.VISITED;
    }
    boolean isWeight(MazeNode node)
    {
        return node.getState() == NodeState.WEIGHT;
    }
    boolean isCurrent(MazeNode node)
    {
        return node.getState() == NodeState.CURRENT;
    }
    boolean isPath(MazeNode node)
    {
        return node.getState() == NodeState.PATH;
    }
    boolean isWeightPath(MazeNode node)
    {
        return node.getState() == NodeState.WEIGHT_PATH;
    }
    boolean isVisitedWeight(MazeNode node)
    {
        return node.getState() == NodeState.VISITED_WEIGHT;
    }

    private final Observer<MazeNode> mazeGenerationObserver = new Observer<MazeNode>() {
        @Override
        public void onSubscribe(@NonNull Disposable d) {
            clearAll();
            allWalls();
            invalidate();
        }

        @Override
        public void onNext(@NonNull MazeNode mazeNode) {
            Pair<Integer, Integer> coord = mazeNode.getCoordinate();
            mMaze[coord.first][coord.second].setState(mazeNode.getState());
            invalidate();
        }

        @Override
        public void onError(@NonNull Throwable e) {

        }
        @Override
        public void onComplete() {
            setStartAndTarget();
            invalidate();
        }
    };

    private void allWalls() {
        for (int i = 0; i < NUM_OF_ROWS; ++i)
            for (int j = 0; j < NUM_OF_COLUMNS; ++j)
                mMaze[i][j].setState(NodeState.WALL);
    }

    public Observer<MazeNode> getMazeGenerationObserver() {
        return mazeGenerationObserver;
    }

    public Observer<MazeNode> getPathFinderObserver() {
        return pathFinderObserver;
    }

    private final Observer<MazeNode> pathFinderObserver = new Observer<MazeNode>(){
        @Override
        public void onSubscribe(@NonNull Disposable d) {
            Log.d("GGG", "onSubscribe");
        }
        @Override
        public void onNext(@NonNull MazeNode mazeNode) {
            Pair<Integer, Integer> coord = mazeNode.getCoordinate();
            mMaze[coord.first][coord.second] = mazeNode;
            invalidate();
        }
        @Override
        public void onError(@NonNull Throwable e) {

        }

        @Override
        public void onComplete() {
            Log.d("GGG", "onComplete");
        }
    };
}