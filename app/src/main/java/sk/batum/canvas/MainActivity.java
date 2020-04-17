package sk.batum.canvas;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends Activity {

    Paint pnt = new Paint();
    Paint sq = new Paint();
    Paint text = new Paint();
    Bitmap btm;
    Canvas cnv = new Canvas();
    ImageView img;
    int s,v;
    int a,b;
    int vyska, sirka;
    int rozmer, lavo, hore;
    int savedSudoku;
    int noveSudoku;
    public String[] moznosti;
    public int[] strokeWidths = {9,6,3};
    String[] sudokus;
    Sudoku[][] sdk = new Sudoku[9][9];
    TextView textView;
    public boolean solved = false;

    public void options (View view) {
        View hiddenL = findViewById(R.id.layoutHidden);
        //View hiddenL2 = findViewById(R.id.layoutHidden2);
        View lay1 = findViewById(R.id.layout1);
        View lay2 = findViewById(R.id.layout2);
        hiddenL.setVisibility(View.VISIBLE);
        //hiddenL2.setVisibility(View.VISIBLE);
        lay1.setVisibility(View.INVISIBLE);
        lay2.setVisibility(View.GONE);
    }

    public void podBack (View view) {
        View hiddenL = findViewById(R.id.layoutHidden);
        //View hiddenL2 = findViewById(R.id.layoutHidden2);
        View lay1 = findViewById(R.id.layout1);
        View lay2 = findViewById(R.id.layout2);
        hiddenL.setVisibility(View.GONE);
        //hiddenL2.setVisibility(View.GONE);
        lay1.setVisibility(View.VISIBLE);
        lay2.setVisibility(View.VISIBLE);
    }

    public void clear (View view) {
        for (int i = 0; i < 81; i++) {
            if (!sdk[i/9][i%9].solid) {
                sdk[i/9][i%9].c = 0;
            }
        }
        vykresli(img);
    }

    public void zadajCislo(View view) {
        if (sdk[a][b].solid) {
            return;
        }
        Button button = findViewById(view.getId());
        String c = button.getText().toString();
        Log.i("Button", button.getText().toString());
        if (c.equals("X")) {
            sdk[a][b].c = 0;
        } else {
            sdk[a][b].c = Integer.parseInt(c);
        }
        vykresli(img);
        if (hotovo() && !solved) {
            okno();
        }
    }

    public void vykresli(ImageView view) {
        int i,j;
        btm = Bitmap.createBitmap(s,s + hore, Bitmap.Config.ARGB_8888);
        view.setLayoutParams(new ConstraintLayout.LayoutParams(s,s + hore));
        view.setImageBitmap(btm);
        cnv = new Canvas(btm);
        sq.setColor(Color.WHITE);
        cnv.drawRect(lavo, hore, lavo + rozmer*9, hore + rozmer*9, sq);
        sq.setColor(getResources().getColor(R.color.malyStvorcek));
        cnv.drawRect(lavo + rozmer*a, hore + rozmer*b, lavo + rozmer*(a + 1), hore + rozmer*(b + 1), sq);
        for (i = 0; i < 10; i++)
        {
            if (i%3 == 0) {
                if (i%9 == 0) {
                    pnt.setStrokeWidth(strokeWidths[0]);
                } else {
                    pnt.setStrokeWidth(strokeWidths[1]);
                }
            } else {
                pnt.setStrokeWidth(strokeWidths[2]);
            }
            cnv.drawLine(lavo + rozmer*i, hore,
                    lavo + rozmer*i, hore + 9*rozmer, pnt);
            cnv.drawLine(lavo, hore + rozmer*i, lavo + 9*rozmer, hore + rozmer*i, pnt);
        }
        for (i = 0; i < 9; i++) {
            for (j = 0; j < 9; j++) {
                if (sdk[i][j].c != 0) {
                    if (sdk[i][j].solid) {
                        text.setColor(Color.BLACK);
                    } else {
                        text.setColor(Color.BLUE);
                    }
                    cnv.drawText(String.valueOf(sdk[i][j].c), lavo + rozmer*i + 2*rozmer/7, hore + rozmer*(j+1) - rozmer/4, text);
                }
            }
        }
    }

    public void newSudoku(View view) {
        solved = false;
        //Log.i(String.valueOf(sirka), String.valueOf(vyska));
        Random rnd = new Random();
        int a = 0;
        if (savedSudoku == 0) {
            a = rnd.nextInt(sudokus.length);
        } else {
            a = savedSudoku;
            savedSudoku = 0;
        }
        noveSudoku = a;
        String sudoku = sudokus[a];
        for (int i = 0; i < 81; i++) {
            sdk[i%9][i/9].c = Character.getNumericValue(sudoku.charAt(i));
            if (sdk[i%9][i/9].c != 0) {
                sdk[i%9][i/9].solid = true;
            } else {
                sdk[i%9][i/9].solid = false;
            }
        }
        textView.setText("Sudoku No. " + String.valueOf(a));
        vykresli(img);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (savedInstanceState != null) {
            savedSudoku = savedInstanceState.getInt(savedGrid);
        }
        setContentView(R.layout.activity_main);
        img = findViewById(R.id.imgView);
        img.setOnTouchListener(handleTouch);
        pnt.setColor(Color.BLACK);
        text.setColor(Color.BLACK);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        vyska = displayMetrics.heightPixels;
        sirka = displayMetrics.widthPixels;
        if (sirka < 900) {
            for (int i = 0; i <= 2; i++) {
                strokeWidths[i] = 2*(3 - i);
            }
        }
        textView = (TextView) findViewById(R.id.textViewSudoku);
        /*moznosti = getResources().getStringArray(R.array.moznosti);
        Spinner spn = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.moznosti, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn.setAdapter(adapter);
        spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this,  String.valueOf(moznosti[position]), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
        sudokus = getResources().getStringArray(R.array.sudokus17);
        final Button[] buttons = {findViewById(R.id.button1), findViewById(R.id.button2), findViewById(R.id.button3), findViewById(R.id.button4),
                findViewById(R.id.button5), findViewById(R.id.button6), findViewById(R.id.button7), findViewById(R.id.button8),
                findViewById(R.id.button9), findViewById(R.id.buttonX)};
        img.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressLint("NewApi")
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    img.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                else {
                    img.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                if (s == 0) {
                    s = img.getWidth();
                }
                if (v == 0) {
                    v = img.getHeight();
                }
                rozmer = s/9 - (5 + (s + 1)%2);
                lavo = (s - 9*rozmer)/2;
                hore = vyska/9;
                text.setTextSize(rozmer*3/4);
                text.setColor(Color.BLACK);
                //vykresli(img);
                newSudoku(img);
                int v = buttons[0].getHeight();
                RadioGroup.LayoutParams[] params = new RadioGroup.LayoutParams[10];
                for (int i = 0; i < 10; i++) {
                    params[i] = new RadioGroup.LayoutParams(v,v);
                    buttons[i].setLayoutParams(params[i]);
                }
                View sp = findViewById(R.id.space1);
                int spaceWidth = sp.getWidth();
                View opt = findViewById(R.id.buttonOptions);
                opt.setLayoutParams(new RadioGroup.LayoutParams(2*(v + spaceWidth), opt.getHeight()));
                View rl = findViewById(R.id.relativeLayout);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, hore/4);
                rl.setY(hore/2);

            }
        });
        int i,j;
        for (i = 0; i < 9; i++) {
            for (j = 0; j < 9; j++) {
                sdk[i][j] = new Sudoku();
            }
        }
    }

    private View.OnTouchListener handleTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            int x = (int) event.getX();
            int y = (int) event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //Log.i("TAG", "touched down");
                    if (x < lavo || x > lavo + 9*rozmer || y < hore || y > hore + 9*rozmer) {
                        break;
                    }
                    a = (x - lavo)/rozmer;
                    b = (y - hore)/rozmer;
                    vykresli(img);
                    break;
                case MotionEvent.ACTION_MOVE:
                    //Log.i("TAG", "moving: (" + x + ", " + y + ")");
                    if (x < lavo || x > lavo + 9*rozmer || y < hore || y > hore + 9*rozmer) {
                        break;
                    }
                    a = (x - lavo)/rozmer;
                    b = (y - hore)/rozmer;
                    vykresli(img);
                    break;
                case MotionEvent.ACTION_UP:
                    //Log.i("TAG", "touched up");
                    //Toast.makeText(MainActivity.this, String.valueOf(p), Toast.LENGTH_SHORT).show();
                    break;
            }

            return false;
        }
    };

    public boolean chyba() {
       int i,a,b,k;
        for (i = 0; i < 81; i++) {
            if (stvorec(i)) {
                return  true;
            }
            a = i/9;
            b = i%9;
            for (k = 0; k < 9; k++) {
                if (k != a && sdk[a][b].c == sdk[k][b].c) {
                    return true;
                }
                if (k != b && sdk[a][b].c == sdk[a][k].c) {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean stvorec(int n) {
        int a,b,i,j,k,l;
        k = n/9;
        l = n%9;
        a = k - k%3;
        b = l - l%3;
        for (i = a; i <= a + 2; i++) {
            for (j = b; j <= b + 2; j++) {
                if (i != k || j != l) {
                    if (sdk[i][j].c == sdk[k][l].c) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void okno() {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        if (!chyba()) {
            solved = true;
            dlgAlert.setMessage("Congratulations!");
            //dlgAlert.setTitle("App Title");
            dlgAlert.setPositiveButton("Thanks", null);
        } else {
            dlgAlert.setMessage("Wrong solution!");
            dlgAlert.setPositiveButton("OK",null);
        }
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    public boolean hotovo() {
        int i;
        for (i = 0; i < 81; i++) {
            if (sdk[i/9][i%9].c == 0) {
                return false;
            }
        }
        return true;
    }

    static final String savedGrid = "savedGrid";

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedSudoku = savedInstanceState.getInt(savedGrid);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(savedGrid, noveSudoku);
        super.onSaveInstanceState(outState);
    }



}

class Sudoku
{
    public int c;
    public boolean solid;
}
