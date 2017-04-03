package com.kaipingzhou.mindcloud.game2048;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kaipingzhou.mindcloud.utils.CacheUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 周开平 on 2017/3/31 16:48.
 * qq 275557625@qq.com
 * 作用：
 */

public class GameView extends LinearLayout {

    private Context context;

    public GameView(Context context) {
        super(context);
        this.context = context;
        initGameView();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initGameView();
    }

    private void initGameView() {
        setOrientation(LinearLayout.VERTICAL);
        setBackgroundColor(0xffbbada0);


        setOnTouchListener(new View.OnTouchListener() {

            private float startX, startY, offsetX, offsetY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        offsetX = event.getX() - startX;
                        offsetY = event.getY() - startY;


                        if (Math.abs(offsetX) > Math.abs(offsetY)) {
                            if (offsetX < -5) {
                                swipeLeft();
                            } else if (offsetX > 5) {
                                swipeRight();
                            }
                        } else {
                            if (offsetY < -5) {
                                swipeUp();
                            } else if (offsetY > 5) {
                                swipeDown();
                            }
                        }

                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        Config.CARD_WIDTH = (Math.min(w, h) - 10) / Config.LINES;

        addCards(Config.CARD_WIDTH, Config.CARD_WIDTH);

        startGame();
    }

    private void addCards(int cardWidth, int cardHeight) {

        Card c;

        LinearLayout line;
        LinearLayout.LayoutParams lineLp;

        for (int y = 0; y < Config.LINES; y++) {
            line = new LinearLayout(getContext());
            lineLp = new LinearLayout.LayoutParams(-1, cardHeight);
            addView(line, lineLp);

            for (int x = 0; x < Config.LINES; x++) {
                c = new Card(getContext());
                line.addView(c, cardWidth, cardHeight);

                cardsMap[x][y] = c;
            }
        }
    }

    public void startGame() {

        Game2048Activity aty = Game2048Activity.getGame2048Activity();
        aty.clearScore();
        aty.showBestScore(aty.getBestScore());

        for (int y = 0; y < Config.LINES; y++) {
            for (int x = 0; x < Config.LINES; x++) {
                cardsMap[x][y].setNum(0);
            }
        }

        addRandomNum();
        addRandomNum();
    }

    private void addRandomNum() {

        emptyPoints.clear();

        for (int y = 0; y < Config.LINES; y++) {
            for (int x = 0; x < Config.LINES; x++) {
                if (cardsMap[x][y].getNum() <= 0) {
                    emptyPoints.add(new Point(x, y));
                }
            }
        }

        if (emptyPoints.size() > 0) {

            Point p = emptyPoints.remove((int) (Math.random() * emptyPoints.size()));
            cardsMap[p.x][p.y].setNum(Math.random() > 0.1 ? 2 : 4);

            Game2048Activity.getGame2048Activity().getAnimLayer().createScaleTo1(cardsMap[p.x][p.y]);
        }
    }


    private void swipeLeft() {

        boolean merge = false;

        for (int y = 0; y < Config.LINES; y++) {
            for (int x = 0; x < Config.LINES; x++) {

                for (int x1 = x + 1; x1 < Config.LINES; x1++) {
                    if (cardsMap[x1][y].getNum() > 0) {

                        if (cardsMap[x][y].getNum() <= 0) {

                            Game2048Activity.getGame2048Activity().getAnimLayer().createMoveAnim(cardsMap[x1][y], cardsMap[x][y], x1, x, y, y);

                            cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
                            cardsMap[x1][y].setNum(0);

                            x--;
                            merge = true;

                        } else if (cardsMap[x][y].equals(cardsMap[x1][y])) {
                            Game2048Activity.getGame2048Activity().getAnimLayer().createMoveAnim(cardsMap[x1][y], cardsMap[x][y], x1, x, y, y);
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum() * 2);
                            cardsMap[x1][y].setNum(0);

                            Game2048Activity.getGame2048Activity().addScore(cardsMap[x][y].getNum());
                            merge = true;
                        }

                        break;
                    }
                }
            }
        }

        if (merge) {
            addRandomNum();
            checkComplete();
        }
    }

    private void swipeRight() {

        boolean merge = false;

        for (int y = 0; y < Config.LINES; y++) {
            for (int x = Config.LINES - 1; x >= 0; x--) {

                for (int x1 = x - 1; x1 >= 0; x1--) {
                    if (cardsMap[x1][y].getNum() > 0) {

                        if (cardsMap[x][y].getNum() <= 0) {
                            Game2048Activity.getGame2048Activity().getAnimLayer().createMoveAnim(cardsMap[x1][y], cardsMap[x][y], x1, x, y, y);
                            cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
                            cardsMap[x1][y].setNum(0);

                            x++;
                            merge = true;
                        } else if (cardsMap[x][y].equals(cardsMap[x1][y])) {
                            Game2048Activity.getGame2048Activity().getAnimLayer().createMoveAnim(cardsMap[x1][y], cardsMap[x][y], x1, x, y, y);
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum() * 2);
                            cardsMap[x1][y].setNum(0);
                            Game2048Activity.getGame2048Activity().addScore(cardsMap[x][y].getNum());
                            merge = true;
                        }

                        break;
                    }
                }
            }
        }

        if (merge) {
            addRandomNum();
            checkComplete();
        }
    }

    private void swipeUp() {

        boolean merge = false;

        for (int x = 0; x < Config.LINES; x++) {
            for (int y = 0; y < Config.LINES; y++) {

                for (int y1 = y + 1; y1 < Config.LINES; y1++) {
                    if (cardsMap[x][y1].getNum() > 0) {

                        if (cardsMap[x][y].getNum() <= 0) {
                            Game2048Activity.getGame2048Activity().getAnimLayer().createMoveAnim(cardsMap[x][y1], cardsMap[x][y], x, x, y1, y);
                            cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
                            cardsMap[x][y1].setNum(0);

                            y--;

                            merge = true;
                        } else if (cardsMap[x][y].equals(cardsMap[x][y1])) {
                            Game2048Activity.getGame2048Activity().getAnimLayer().createMoveAnim(cardsMap[x][y1], cardsMap[x][y], x, x, y1, y);
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum() * 2);
                            cardsMap[x][y1].setNum(0);
                            Game2048Activity.getGame2048Activity().addScore(cardsMap[x][y].getNum());
                            merge = true;
                        }

                        break;

                    }
                }
            }
        }

        if (merge) {
            addRandomNum();
            checkComplete();
        }
    }

    private void swipeDown() {

        boolean merge = false;

        for (int x = 0; x < Config.LINES; x++) {
            for (int y = Config.LINES - 1; y >= 0; y--) {

                for (int y1 = y - 1; y1 >= 0; y1--) {
                    if (cardsMap[x][y1].getNum() > 0) {

                        if (cardsMap[x][y].getNum() <= 0) {
                            Game2048Activity.getGame2048Activity().getAnimLayer().createMoveAnim(cardsMap[x][y1], cardsMap[x][y], x, x, y1, y);
                            cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
                            cardsMap[x][y1].setNum(0);

                            y++;
                            merge = true;
                        } else if (cardsMap[x][y].equals(cardsMap[x][y1])) {
                            Game2048Activity.getGame2048Activity().getAnimLayer().createMoveAnim(cardsMap[x][y1], cardsMap[x][y], x, x, y1, y);
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum() * 2);
                            cardsMap[x][y1].setNum(0);
                            Game2048Activity.getGame2048Activity().addScore(cardsMap[x][y].getNum());
                            merge = true;
                        }

                        break;
                    }
                }
            }
        }

        if (merge) {
            addRandomNum();
            checkComplete();
        }
    }

    private void checkComplete() {

        boolean complete = true;

        ALL:
        for (int y = 0; y < Config.LINES; y++) {
            for (int x = 0; x < Config.LINES; x++) {
                if (cardsMap[x][y].getNum() == 0 ||
                        (x > 0 && cardsMap[x][y].equals(cardsMap[x - 1][y])) ||
                        (x < Config.LINES - 1 && cardsMap[x][y].equals(cardsMap[x + 1][y])) ||
                        (y > 0 && cardsMap[x][y].equals(cardsMap[x][y - 1])) ||
                        (y < Config.LINES - 1 && cardsMap[x][y].equals(cardsMap[x][y + 1]))) {

                    complete = false;
                    break ALL;
                }
            }
        }

        if (complete) {
            String maxNum = CacheUtils.getString(context, "maxNum", 0);
            int maxNumInt = Integer.parseInt(maxNum);

            String level = "";

            switch (maxNumInt) {
                case 0:

                    break;
                case 2:
                    level = "同学,真为你的智商捉急~";
                    break;
                case 4:
                    level = "同学,真为你的智商捉急~";
                    break;
                case 8:
                    level = "同学,真为你的智商捉急~";
                    break;
                case 16:
                    level = "同学,真为你的智商捉急~";
                    break;
                case 32:
                    level = "同学,真为你的智商捉急~";
                    break;
                case 64:
                    level = "同学,真为你的智商捉急~";
                    break;
                case 128:
                    level = "你刚达到学弱的水平，继续努力呀~";
                    break;
                case 256:
                    level = "你刚达到学民的水平，继续努力呀~";
                    break;
                case 512:
                    level = "你刚达到学屌的水平，继续努力呀~";
                    break;
                case 1024:
                    level = "你已达到学痞的水平，请保持~";
                    break;
                case 2048:
                    level = "你已达到学霸的水平，请保持~";
                    break;
                case 4096:
                    level = "你已达到学神的水平，超越全国99%的玩家";
                    break;
                case 8172:
                    level = "你已达到学鬼的水平，屌爆了~";
                    break;
                case 16344:
                    level = "你已达到学魔的水平，屌爆了~";
                    break;
            }

            final String finalLevel = level;
            new AlertDialog.Builder(getContext()).setTitle("你好").setMessage("游戏结束").setPositiveButton("确定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(Game2048Activity.getGame2048Activity().getApplicationContext(), finalLevel, Toast.LENGTH_SHORT).show();
                }
            }).show();
        }
    }

    private Card[][] cardsMap = new Card[Config.LINES][Config.LINES];
    private List<Point> emptyPoints = new ArrayList<Point>();
}

