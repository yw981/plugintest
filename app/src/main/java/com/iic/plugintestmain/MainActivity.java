package com.iic.plugintestmain;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.StaticLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.view.View.OnTouchListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textView = (TextView) findViewById(R.id.tv);

        textView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO 自动生成的方法存根
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        float x=event.getX();
                        float y=event.getY();
                        //因为行设置行间距后，文本在顶部，空白在底部，加上一半的行间距，可以实现与文本垂直居中一样的效果
                        y=y+textView.getLineSpacingExtra()/2-textView.getPaddingTop();
                        //在顶部padding空白处点击
                        if(y<0)
                            return false;
                        int singleLineHeight=textView.getLineHeight();
                        StaticLayout layout=(StaticLayout)textView.getLayout();

                        int lineNumber=Math.round(y/singleLineHeight)-1;
                        if(lineNumber<0)
                            lineNumber=0;
                        if(lineNumber>textView.getLineCount()-1)
                            lineNumber-=1;
                        int start=layout.getLineStart(lineNumber);
                        int end=layout.getLineEnd(lineNumber);

                        String str=textView.getText().toString().substring(start, end);
                        Paint paint=new Paint();
                        paint.setTextSize(textView.getTextSize());//设置字符大小
                        //单字节占宽度
                        int sigleByteWidth=(int)paint.measureText("1", 0, 1);

                        //减掉padding
                        float realX=x-textView.getPaddingLeft();
                        //在左侧padding空白处点击
                        if(realX<0)
                        {
                            return false;
                        }
                        //字符串可能的最大长度
                        //这里应该有更好更快的算法
                        int maxLength=(int)Math.floor(realX/sigleByteWidth);
                        //一般不会有字符显示超过两个1的宽度
                        int miniLenth=maxLength/2;
                        int strLength=str.length();
                        for(int i=strLength;i>=miniLenth;i--)
                        {
                            //字符串显示的宽度
                            float displayWidth=paint.measureText(str, 0, i);
                            if(Math.abs(displayWidth-realX)<sigleByteWidth*2)
                            {
                                //找到了，如果处理中文，这里用substring就可以了
                                int clickChar=str.charAt(i-1);
                                //不是英文字母，直接返回
                                if(clickChar<65||(clickChar>90&&clickChar<97)||clickChar>122)
                                {
                                    return false;
                                }
                                //取单词,这段只针对英文，中文需要海量词库，太复杂了，前后看是不是空格或符号
                                StringBuilder sb=new StringBuilder();
                                sb.append(str.substring(i-1,i));
                                //查找前后的字符是否是同一个单词
                                String strBefore=str.substring(0,i-1);
                                String strAfter=str.substring(i,str.length());

                                String strBeforeReverse=reverseStr(strBefore);
                                Pattern p1=Pattern.compile("(^[a-zA-Z]+)");
                                //正则
                                Matcher m1=p1.matcher(strBeforeReverse);
                                if(m1.find())
                                {
                                    sb.insert(0, reverseStr(m1.group(1)));
                                }

                                m1=p1.matcher(strAfter);
                                if(m1.find())
                                {
                                    sb.append(m1.group(1));
                                }
                                Log.d("MA D","点击单词"+sb.toString());
                                //System.out.println("点击单词"+sb.toString());
                                break;
                            }
                        }
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 反转字符串
     * @param s
     * @return
     */
    public String reverseStr(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = s.length() - 1; i >= 0; i--) {
            sb.append(s.charAt(i));
        }
        return sb.toString();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
