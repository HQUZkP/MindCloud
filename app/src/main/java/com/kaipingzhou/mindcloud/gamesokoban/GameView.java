package com.kaipingzhou.mindcloud.gamesokoban;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;

import com.kaipingzhou.mindcloud.R;

import java.util.ArrayList;

/**
 * Created by 周开平 on 2017/4/1 20:06.
 * qq 275557625@qq.com
 * 作用：
 */

public class GameView extends SurfaceView implements SurfaceHolder.Callback, OnGestureListener, OnTouchListener {

    private SurfaceHolder holder;
    private int grade = 0;
    //row,column记载人的行号 列号
    //leftX,leftY 记载左上角图片的位置  避免图片从(0,0)坐标开始
    private int row = 7, column = 7, leftX = 0, leftY = 0;
    //记载地图的行列数
    private int mapRow = 0, mapColumn = 0;
    //width,height 记载屏幕的大小
    private int width = 0, height = 0;
    private boolean acceptKey = true;
    //程序所用到的图片
    private Bitmap pic[] = null;
    //定义一些常量，对应地图的元素
    final byte WALL = 1, BOX = 2, BOXONEND = 3, END = 4, MANDOWN = 5, MANLEFT = 6, MANRIGHT = 7, MANUP = 8, GRASS = 9, MANDOWNONEND = 10, MANLEFTONEND = 11, MANRIGHTONEND = 12, MANUPONEND = 13;
    private Paint paint = null;
    private GameTXZActivity gameTXZActivity = null;
    private byte[][] map = null;
    private ArrayList list = new ArrayList();
    private GestureDetector mGestureDetector;


    public void getManPosition() {
        for (int i = 0; i < map.length; i++)
            for (int j = 0; j < map[0].length; j++)
                if (map[i][j] == MANDOWN || map[i][j] == MANDOWNONEND || map[i][j] == MANUP || map[i][j] == MANUPONEND || map[i][j] == MANLEFT || map[i][j] == MANLEFTONEND || map[i][j] == MANRIGHT || map[i][j] == MANRIGHTONEND) {
                    row = i;
                    column = j;
                    break;
                }
    }

    public void undo() {
        if (acceptKey) {
            //撤销
            if (list.size() > 0) {
                //若要撤销 必须走过
                Map priorMap = (Map) list.get(list.size() - 1);
                map = priorMap.getMap();
                row = priorMap.getManX();
                column = priorMap.getManY();
                repaint();
                list.remove(list.size() - 1);
            } else

                Toast.makeText(this.getContext(), "不能再撤销！", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this.getContext(), "此关已完成，不能撤销！", Toast.LENGTH_SHORT).show();
        }
    }

    public void nextGrade() {
        //grade++;
        if (grade >= MapFactory.getCount() - 1) {
            Toast.makeText(this.getContext(), "恭喜你完成所有关卡！", Toast.LENGTH_LONG).show();
            acceptKey = false;
        } else {
            grade++;
            initMap();
            repaint();
            acceptKey = true;
        }
    }

    public void priorGrade() {
        grade--;
        acceptKey = true;
        if (grade < 0)
            grade = 0;
        initMap();
        repaint();
    }

    public void initMap() {
        map = gameTXZActivity.getMap(grade);
        list.clear();
        getMapSizeAndPosition();
        getManPosition();
//		Map currMap=new Map(row, column, map);
//		list.add(currMap);
    }

    public void resumeGame() {
        SharedPreferences pre = this.getContext().getSharedPreferences("map", 0);
        String mapString = pre.getString("mapString", "");
        if (mapString.equals(""))
            initMap();
        else {
            row = pre.getInt("manX", 0);
            column = pre.getInt("manY", 0);
            int rowCount = pre.getInt("row", 0);
            int columnCount = pre.getInt("column", 0);
            grade = pre.getInt("grade", 0);
            map = new byte[rowCount][columnCount];
            String str[] = mapString.split(",");
            int index = 0;
            for (int i = 0; i < rowCount; i++)
                for (int j = 0; j < columnCount; j++) {
                    map[i][j] = (byte) Integer.parseInt(str[index++]);
                }
            getMapSizeAndPosition();
        }
        //getManPosition();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gameTXZActivity = (GameTXZActivity) context;
        getPic();
        holder = this.getHolder();
        holder.addCallback(this);
        this.setOnTouchListener(this);
        this.setLongClickable(true);
        WindowManager manager = gameTXZActivity.getWindowManager();
        width = manager.getDefaultDisplay().getWidth();
        height = manager.getDefaultDisplay().getHeight();
        this.setFocusable(true);
        GestureDetector localGestureDetector = new GestureDetector(this);
        this.mGestureDetector = localGestureDetector;
        //initMap();
        //构造方法执行时从优先数据中恢复游戏
        //关卡切换时调用initMap()
        resumeGame();
    }

    private void getMapSizeAndPosition() {
        mapRow = map.length;
        mapColumn = map[0].length;
        leftX = (width - map[0].length * dip2px(getContext(), 35)) / 2;
        leftY = (height - map.length * dip2px(getContext(), 35)) / 2;
        System.out.println(leftX);
        System.out.println(leftY);
        System.out.println(mapRow);
        System.out.println(mapColumn);
    }

    public void getPic() {
        pic = new Bitmap[14];
        //pic[0]=BitmapFactory.decodeResource(getResources(), R.drawable.pic0);
        pic[1] = BitmapFactory.decodeResource(getResources(), R.drawable.pic1);
        pic[2] = BitmapFactory.decodeResource(getResources(), R.drawable.pic2);
        pic[3] = BitmapFactory.decodeResource(getResources(), R.drawable.pic3);
        pic[4] = BitmapFactory.decodeResource(getResources(), R.drawable.pic4);
        pic[5] = BitmapFactory.decodeResource(getResources(), R.drawable.pic5);
        pic[6] = BitmapFactory.decodeResource(getResources(), R.drawable.pic6);
        pic[7] = BitmapFactory.decodeResource(getResources(), R.drawable.pic7);
        pic[8] = BitmapFactory.decodeResource(getResources(), R.drawable.pic8);
        pic[9] = BitmapFactory.decodeResource(getResources(), R.drawable.pic9);
        pic[10] = BitmapFactory.decodeResource(getResources(), R.drawable.pic10);
        pic[11] = BitmapFactory.decodeResource(getResources(), R.drawable.pic11);
        pic[12] = BitmapFactory.decodeResource(getResources(), R.drawable.pic12);
        pic[13] = BitmapFactory.decodeResource(getResources(), R.drawable.pic13);

        for (int i = 1; i < pic.length; i++) {
            pic[i] = resizeBitmap(pic[i], dip2px(getContext(), 35), dip2px(getContext(), 35));
        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public Bitmap resizeBitmap(Bitmap bitmap, int w, int h) {
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int newWidth = w;
            int newHeight = h;
            float scaleWight = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWight, scaleHeight);
            Bitmap res = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
            return res;
        } else {
            return null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        paint = new Paint();
        repaint();
        repaint();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!acceptKey)
            return super.onKeyDown(keyCode, event);
        if (keyCode == 19) {
            //向上
            moveUp();
        }
        if (keyCode == 20) {
            //向下
            moveDown();
        }
        if (keyCode == 21) {
            //向左
            moveLeft();
        }
        if (keyCode == 22) {
            //向右
            moveRight();
        }
        repaint();
        ///////////////////
        if (isFinished()) {
            //禁用按键
            acceptKey = false;
            //提示进入下一关
            Builder builder = new Builder(gameTXZActivity);
            builder.setTitle("恭喜过关!");
            builder.setMessage("继续下一关吗?");
            builder.setPositiveButton("继续", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //进入下一关
                    acceptKey = true;
                    nextGrade();
                }
            });
            builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    gameTXZActivity.finish();
                }
            });
            builder.create().show();
        }
        ///////////////////
        return super.onKeyDown(keyCode, event);
    }

    public byte grassOrEnd(byte man) {
        byte result = GRASS;
        if (man == MANDOWNONEND || man == MANLEFTONEND || man == MANRIGHTONEND || man == MANUPONEND)
            result = END;
        return result;
    }

    private void moveUp() {
        //上一位为BOX,BOXONEND,WALL
        if (map[row - 1][column] < 4) {
            //上一位为 BOX,BOXONEND
            if (map[row - 1][column] == BOX || map[row - 1][column] == BOXONEND) {
                //上上一位为 END,GRASS则向上一步,其他不用处理
                if (map[row - 2][column] == END || map[row - 2][column] == GRASS) {
                    Map currMap = new Map(row, column, map);
                    list.add(currMap);
                    byte boxTemp = map[row - 2][column] == END ? BOXONEND : BOX;
                    byte manTemp = map[row - 1][column] == BOX ? MANUP : MANUPONEND;
                    //箱子变成temp,箱子往前一步
                    map[row - 2][column] = boxTemp;
                    //人变成MANUP,往上走一步
                    map[row - 1][column] = manTemp;
                    //人刚才站的地方变成GRASS或者END
                    map[row][column] = grassOrEnd(map[row][column]);
                    //人离开后修改人的坐标
                    row--;
                }
            }
        } else {
            //上一位为 GRASS,END,其他情况不用处理
            if (map[row - 1][column] == GRASS || map[row - 1][column] == END) {
                Map currMap = new Map(row, column, map);
                list.add(currMap);
                byte temp = map[row - 1][column] == END ? MANUPONEND : MANUP;
                //人变成temp,人往上走一步
                map[row - 1][column] = temp;
                //人刚才站的地方变成GRASS或者END
                map[row][column] = grassOrEnd(map[row][column]);
                //人离开后修改人的坐标
                row--;
            }
        }
    }

    private void moveDown() {
        //下一位为BOX,BOXONEND,WALL
        if (map[row + 1][column] < 4) {
            //下一位为 BOX,BOXONEND
            if (map[row + 1][column] == BOX || map[row + 1][column] == BOXONEND) {
                //下下一位为 END,GRASS则向下一步,其他不用处理
                if (map[row + 2][column] == END || map[row + 2][column] == GRASS) {
                    Map currMap = new Map(row, column, map);
                    list.add(currMap);
                    byte boxTemp = map[row + 2][column] == END ? BOXONEND : BOX;
                    byte manTemp = map[row + 1][column] == BOX ? MANDOWN : MANDOWNONEND;
                    //箱子变成boxTemp,箱子往下一步
                    map[row + 2][column] = boxTemp;
                    //人变成manTemp,往下走一步
                    map[row + 1][column] = manTemp;
                    //人刚才站的地方变成 grassOrEnd(map[row][column])
                    map[row][column] = grassOrEnd(map[row][column]);
                    row++;

                }
            }
        } else {
            //下一位为 GRASS,END,其他情况不用处理
            if (map[row + 1][column] == GRASS || map[row + 1][column] == END) {
                Map currMap = new Map(row, column, map);
                list.add(currMap);
                byte temp = map[row + 1][column] == END ? MANDOWNONEND : MANDOWN;
                //人变成temp,人往下走一步
                map[row + 1][column] = temp;
                //人刚才站的地方变成 grassOrEnd(map[row][column])
                map[row][column] = grassOrEnd(map[row][column]);
                row++;

            }
        }
    }

    private void moveLeft() {
        //左一位为BOX,BOXONEND,WALL
        if (map[row][column - 1] < 4) {
            //左一位为 BOX,BOXONEND
            if (map[row][column - 1] == BOX || map[row][column - 1] == BOXONEND) {
                //左左一位为 END,GRASS则向左一步,其他不用处理
                if (map[row][column - 2] == END || map[row][column - 2] == GRASS) {
                    Map currMap = new Map(row, column, map);
                    list.add(currMap);
                    byte boxTemp = map[row][column - 2] == END ? BOXONEND : BOX;
                    byte manTemp = map[row][column - 1] == BOX ? MANLEFT : MANLEFTONEND;
                    //箱子变成boxTemp,箱子往左一步
                    map[row][column - 2] = boxTemp;
                    //人变成manTemp,往左走一步
                    map[row][column - 1] = manTemp;
                    //人刚才站的地方变成 grassOrEnd(map[row][column])
                    map[row][column] = grassOrEnd(map[row][column]);
                    column--;

                }
            }
        } else {
            //左一位为 GRASS,END,其他情况不用处理
            if (map[row][column - 1] == GRASS || map[row][column - 1] == END) {
                Map currMap = new Map(row, column, map);
                list.add(currMap);
                byte temp = map[row][column - 1] == END ? MANLEFTONEND : MANLEFT;
                //人变成temp,人往左走一步
                map[row][column - 1] = temp;
                //人刚才站的地方变成 grassOrEnd(map[row][column])
                map[row][column] = grassOrEnd(map[row][column]);
                column--;

            }
        }
    }

    private void moveRight() {
        //右一位为BOX,BOXONEND,WALL
        if (map[row][column + 1] < 4) {
            //右一位为 BOX,BOXONEND
            if (map[row][column + 1] == BOX || map[row][column + 1] == BOXONEND) {
                //右右一位为 END,GRASS则向右一步,其他不用处理
                if (map[row][column + 2] == END || map[row][column + 2] == GRASS) {
                    Map currMap = new Map(row, column, map);
                    list.add(currMap);
                    byte boxTemp = map[row][column + 2] == END ? BOXONEND : BOX;
                    byte manTemp = map[row][column + 1] == BOX ? MANRIGHT : MANRIGHTONEND;
                    //箱子变成boxTemp,箱子往右一步
                    map[row][column + 2] = boxTemp;
                    //人变成manTemp,往右走一步
                    map[row][column + 1] = manTemp;
                    //人刚才站的地方变成 grassOrEnd(map[row][column])
                    map[row][column] = grassOrEnd(map[row][column]);
                    column++;

                }
            }
        } else {
            //右一位为 GRASS,END,其他情况不用处理
            if (map[row][column + 1] == GRASS || map[row][column + 1] == END) {
                Map currMap = new Map(row, column, map);
                list.add(currMap);
                byte temp = map[row][column + 1] == END ? MANRIGHTONEND : MANRIGHT;
                //人变成temp,人往右走一步
                map[row][column + 1] = temp;
                //人刚才站的地方变成 grassOrEnd(map[row][column])
                map[row][column] = grassOrEnd(map[row][column]);
                column++;

            }
        }
    }

    public boolean isFinished() {
        for (int i = 0; i < mapRow; i++)
            for (int j = 0; j < mapColumn; j++)
                if (map[i][j] == END || map[i][j] == MANDOWNONEND || map[i][j] == MANUPONEND || map[i][j] == MANLEFTONEND || map[i][j] == MANRIGHTONEND)
                    return false;
        return true;
    }

    protected void paint(Canvas canvas) {
        //canvas.drawARGB(125, 0x94,0xDA, 0x3C);
        //canvas.drawRect(leftX, leftY,mapColumn*30,mapRow*30, paint);
        canvas.drawColor(Color.BLACK);
        for (int i = 0; i < mapRow; i++)
            for (int j = 0; j < mapColumn; j++) {
                //画出地图 i代表行数,j代表列数
                if (map[i][j] != 0)
                    canvas.drawBitmap(
                            pic[map[i][j]],
                            leftX + j * dip2px(getContext(), 35),
                            leftY + i * dip2px(getContext(), 35),
                            paint);
            }
    }

    public void repaint() {
        Canvas c = null;
        try {
            c = holder.lockCanvas();
            paint(c);
        } finally {
            if (c != null)
                holder.unlockCanvasAndPost(c);
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        //Toast.makeText(gameMain, "ddd", Toast.LENGTH_LONG).show();
        float x1 = e1.getX();
        float x2 = e2.getX();
        float y1 = e1.getY();
        float y2 = e2.getY();
        float x = Math.abs(x1 - x2);
        float y = Math.abs(y1 - y2);
        if (x > y)
            if (x1 < x2)
                this.onKeyDown(22, null);
            else
                this.onKeyDown(21, null);
        else if (y1 < y2)
            this.onKeyDown(20, null);
        else
            this.onKeyDown(19, null);
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }


    public int getManX() {
        return row;
    }

    public int getManY() {
        return column;
    }

    public int getGrade() {
        return grade;
    }

    public byte[][] getMap() {
        return map;
    }

}

