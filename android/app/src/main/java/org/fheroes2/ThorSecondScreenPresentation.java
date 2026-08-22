/***************************************************************************
 *   fheroes2: https://github.com/ihhub/fheroes2                           *
 *   Copyright (C) 2026                                                    *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 ***************************************************************************/

package org.fheroes2;

import java.util.ArrayList;
import java.util.List;

import android.app.Presentation;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/** Full-screen, touch-first command deck for the AYN Thor lower display. */
final class ThorSecondScreenPresentation extends Presentation
{
    interface KeySender
    {
        void send( int keyCode, boolean pressed );
    }

    private final KeySender keySender;
    private CommandDeckView commandDeckView;

    ThorSecondScreenPresentation( final Context context, final Display display, final KeySender keySender )
    {
        super( context, display );
        this.keySender = keySender;
    }

    @Override
    protected void onCreate( final Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        final Window window = getWindow();
        if ( window != null ) {
            // Keep the SDL game window focused while still allowing this window to receive touch.
            window.addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE );
            window.setNavigationBarColor( Color.BLACK );
            window.setStatusBarColor( Color.BLACK );
            window.getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );
        }

        commandDeckView = new CommandDeckView( getContext(), keySender );
        setContentView( commandDeckView );
    }

    @Override
    protected void onStop()
    {
        if ( commandDeckView != null ) {
            commandDeckView.releasePressedButton();
        }
        super.onStop();
    }

    private static final class CommandDeckView extends View
    {
        private static final int BACKGROUND_COLOR = Color.rgb( 17, 13, 9 );
        private static final int PANEL_COLOR = Color.rgb( 53, 39, 25 );
        private static final int BUTTON_COLOR = Color.rgb( 105, 73, 38 );
        private static final int BUTTON_PRESSED_COLOR = Color.rgb( 166, 116, 53 );
        private static final int BORDER_COLOR = Color.rgb( 224, 187, 103 );
        private static final int TEXT_COLOR = Color.rgb( 255, 239, 190 );

        private final KeySender keySender;
        private final Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG );
        private final List<CommandButton> buttons = new ArrayList<>();

        private CommandButton pressedButton;

        CommandDeckView( final Context context, final KeySender keySender )
        {
            super( context );
            this.keySender = keySender;
            setBackgroundColor( BACKGROUND_COLOR );
            setFocusable( true );
        }

        @Override
        protected void onSizeChanged( final int width, final int height, final int oldWidth, final int oldHeight )
        {
            super.onSizeChanged( width, height, oldWidth, oldHeight );
            layoutButtons( width, height );
        }

        @Override
        protected void onDraw( final Canvas canvas )
        {
            super.onDraw( canvas );

            paint.setStyle( Paint.Style.FILL );
            paint.setColor( PANEL_COLOR );
            canvas.drawRoundRect( new RectF( 12, 12, getWidth() - 12, getHeight() - 12 ), 28, 28, paint );

            for ( final CommandButton button : buttons ) {
                paint.setColor( button == pressedButton ? BUTTON_PRESSED_COLOR : BUTTON_COLOR );
                canvas.drawRoundRect( button.bounds, 22, 22, paint );

                paint.setStyle( Paint.Style.STROKE );
                paint.setStrokeWidth( 3 );
                paint.setColor( BORDER_COLOR );
                canvas.drawRoundRect( button.bounds, 22, 22, paint );

                paint.setStyle( Paint.Style.FILL );
                paint.setColor( TEXT_COLOR );
                paint.setTextAlign( Paint.Align.CENTER );
                paint.setTypeface( android.graphics.Typeface.DEFAULT_BOLD );
                paint.setTextSize( Math.min( button.bounds.height() * 0.27f, 42f ) );
                final float baseline = button.bounds.centerY() - ( paint.ascent() + paint.descent() ) / 2;
                canvas.drawText( button.label, button.bounds.centerX(), baseline, paint );
            }
        }

        @Override
        public boolean onTouchEvent( final MotionEvent event )
        {
            final int action = event.getActionMasked();
            if ( action == MotionEvent.ACTION_DOWN ) {
                pressedButton = buttonAt( event.getX(), event.getY() );
                if ( pressedButton != null ) {
                    keySender.send( pressedButton.keyCode, true );
                    invalidate();
                }
                return true;
            }

            if ( action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL ) {
                if ( pressedButton != null ) {
                    keySender.send( pressedButton.keyCode, false );
                    pressedButton = null;
                    invalidate();
                }
                return true;
            }

            return true;
        }

        private void layoutButtons( final int width, final int height )
        {
            buttons.clear();

            final float margin = Math.max( 20, Math.min( width, height ) * 0.025f );
            final float gap = margin * 0.55f;
            final float availableWidth = width - 2 * margin;
            final float availableHeight = height - 2 * margin;
            final float columnWidth = ( availableWidth - 3 * gap ) / 4;
            final float rowHeight = ( availableHeight - 2 * gap ) / 3;

            addButton( "UP", KeyEvent.KEYCODE_DPAD_UP, 0, 0, columnWidth, rowHeight, margin, gap );
            addButton( "LEFT", KeyEvent.KEYCODE_DPAD_LEFT, 0, 1, columnWidth, rowHeight, margin, gap );
            addButton( "DOWN", KeyEvent.KEYCODE_DPAD_DOWN, 1, 1, columnWidth, rowHeight, margin, gap );
            addButton( "RIGHT", KeyEvent.KEYCODE_DPAD_RIGHT, 2, 1, columnWidth, rowHeight, margin, gap );

            addButton( "CONFIRM", KeyEvent.KEYCODE_ENTER, 1, 0, columnWidth, rowHeight, margin, gap );
            addButton( "CANCEL", KeyEvent.KEYCODE_ESCAPE, 3, 1, columnWidth, rowHeight, margin, gap );

            addButton( "NEXT HERO", KeyEvent.KEYCODE_H, 2, 0, columnWidth, rowHeight, margin, gap );
            addButton( "NEXT TOWN", KeyEvent.KEYCODE_T, 0, 2, columnWidth, rowHeight, margin, gap );
            addButton( "MOVE", KeyEvent.KEYCODE_M, 1, 2, columnWidth, rowHeight, margin, gap );

            addButton( "SPELL", KeyEvent.KEYCODE_C, 3, 0, columnWidth, rowHeight, margin, gap );
            addButton( "ACTION", KeyEvent.KEYCODE_SPACE, 2, 2, columnWidth, rowHeight, margin, gap );
            addButton( "END TURN", KeyEvent.KEYCODE_E, 3, 2, columnWidth, rowHeight, margin, gap );
        }

        private void addButton( final String label, final int keyCode, final int column, final int row, final float columnWidth, final float rowHeight,
                                final float margin, final float gap )
        {
            final float left = margin + column * ( columnWidth + gap );
            final float top = margin + row * ( rowHeight + gap );
            buttons.add( new CommandButton( label, keyCode, new RectF( left, top, left + columnWidth, top + rowHeight ) ) );
        }

        private CommandButton buttonAt( final float x, final float y )
        {
            for ( final CommandButton button : buttons ) {
                if ( button.bounds.contains( x, y ) ) {
                    return button;
                }
            }
            return null;
        }

        void releasePressedButton()
        {
            if ( pressedButton != null ) {
                keySender.send( pressedButton.keyCode, false );
                pressedButton = null;
                invalidate();
            }
        }
    }

    private static final class CommandButton
    {
        final String label;
        final int keyCode;
        final RectF bounds;

        CommandButton( final String label, final int keyCode, final RectF bounds )
        {
            this.label = label;
            this.keyCode = keyCode;
            this.bounds = bounds;
        }
    }
}
