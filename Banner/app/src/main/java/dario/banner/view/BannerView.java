/*
 * Copyright (C) 2015 Dario A Lencina Talarico
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dario.banner.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;


public class BannerView extends View {

    public BannerView(Context context) {
        this(context, null, 0);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }



    public void setOffset(int offset) {
        this.offset = offset * 35;
    }

    int offset = 0;

    PorterDuffXfermode mode11 = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

    PorterDuffXfermode mode12 = new PorterDuffXfermode(PorterDuff.Mode.SRC);

    Paint antialias = new Paint(Paint.ANTI_ALIAS_FLAG);

    Paint painter = new Paint();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        painter.setXfermode(mode11);
        canvas.drawPaint(painter);
        painter.setXfermode(mode12);
        Paint myPaint = antialias;
        myPaint.setStrokeWidth(5);
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setColor(white);
        myPaint.setStyle(Paint.Style.FILL);
        myPaint.setTextSize(900);
        canvas.drawText("METALLICA",-1 * this.offset, 800, myPaint);
    }

    private int white= Color.rgb(0xFF, 0xFF, 0xFF);

}
