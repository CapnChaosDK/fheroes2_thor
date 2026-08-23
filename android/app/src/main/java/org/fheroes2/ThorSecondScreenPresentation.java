/***************************************************************************
 *   fheroes2: https://github.com/ihhub/fheroes2                           *
 *   Copyright (C) 2026                                                    *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
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
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/** Full-screen, context-sensitive command deck for the AYN Thor lower display. */
final class ThorSecondScreenPresentation extends Presentation
{
    static final int CONTEXT_FALLBACK = 0;
    private static final int CONTEXT_MAIN_MENU = 1;
    private static final int CONTEXT_DIALOG = 2;
    private static final int CONTEXT_ADVENTURE_MAP = 3;
    private static final int CONTEXT_HERO = 4;
    private static final int CONTEXT_CASTLE = 5;
    private static final int CONTEXT_BATTLE = 6;

    private static final int ACTION_NONE = 0;
    private static final int ACTION_BATTLE_CAST_SPELL = 1;
    private static final int ACTION_BATTLE_SKIP = 2;
    private static final int ACTION_BATTLE_TOGGLE_AUTO_COMBAT = 3;
    private static final int ACTION_BATTLE_QUICK_COMBAT = 4;
    private static final int ACTION_BATTLE_RETREAT = 5;
    private static final int ACTION_BATTLE_SURRENDER = 6;
    private static final int ACTION_BATTLE_OPTIONS = 7;
    private static final int ACTION_BATTLE_TOGGLE_TURN_ORDER = 8;
    private static final int ACTION_ADVENTURE_NEXT_HERO = 9;
    private static final int ACTION_ADVENTURE_NEXT_TOWN = 10;
    private static final int ACTION_ADVENTURE_MOVE = 11;
    private static final int ACTION_ADVENTURE_DEFAULT_ACTION = 12;
    private static final int ACTION_ADVENTURE_CAST_SPELL = 13;
    private static final int ACTION_ADVENTURE_END_TURN = 14;
    private static final int ACTION_ADVENTURE_OPTIONS = 15;
    private static final int ACTION_ADVENTURE_FILE_OPTIONS = 16;
    private static final int ACTION_ADVENTURE_PUZZLE_MAP = 17;
    private static final int ACTION_ADVENTURE_KINGDOM_SUMMARY = 18;
    private static final int ACTION_ADVENTURE_VIEW_WORLD = 19;
    private static final int ACTION_ADVENTURE_DIG_ARTIFACT = 20;
    private static final int ACTION_HERO_PREVIOUS = 21;
    private static final int ACTION_HERO_NEXT = 22;
    private static final int ACTION_HERO_DISMISS = 23;
    private static final int ACTION_HERO_UPGRADE_SELECTED = 24;
    private static final int ACTION_HERO_SPLIT_SELECTED_HALF = 25;
    private static final int ACTION_HERO_SPLIT_SELECTED_ONE = 26;
    private static final int ACTION_HERO_JOIN_SELECTED = 27;
    private static final int ACTION_HERO_SWAP_ARMIES = 28;
    private static final int ACTION_HERO_CLOSE = 29;
    private static final int ACTION_CASTLE_PREVIOUS = 30;
    private static final int ACTION_CASTLE_NEXT = 31;
    private static final int ACTION_CASTLE_WELL = 32;
    private static final int ACTION_CASTLE_MARKETPLACE = 33;
    private static final int ACTION_CASTLE_MAGE_GUILD = 34;
    private static final int ACTION_CASTLE_SHIPYARD = 35;
    private static final int ACTION_CASTLE_THIEVES_GUILD = 36;
    private static final int ACTION_CASTLE_TAVERN = 37;
    private static final int ACTION_CASTLE_CONSTRUCTION = 38;
    private static final int ACTION_CASTLE_TRANSFER_TO_HERO = 39;
    private static final int ACTION_CASTLE_TRANSFER_TO_GARRISON = 40;
    private static final int ACTION_CASTLE_UPGRADE_SELECTED = 41;
    private static final int ACTION_CASTLE_CLOSE = 42;

    interface KeySender
    {
        void send( int keyCode, boolean pressed );
    }

    interface ActionSender
    {
        boolean send( int action );
    }

    private final KeySender keySender;
    private final ActionSender actionSender;
    private CommandDeckView commandDeckView;

    ThorSecondScreenPresentation( final Context context, final Display display, final KeySender keySender, final ActionSender actionSender )
    {
        super( context, display );
        this.keySender = keySender;
        this.actionSender = actionSender;
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

        commandDeckView = new CommandDeckView( getContext(), keySender, actionSender );
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

    void setGameState( final int context, final long enabledActions, final String[] informationSnapshot )
    {
        if ( commandDeckView != null ) {
            commandDeckView.setGameState( context, enabledActions, informationSnapshot );
        }
    }

    private static final class CommandDeckView extends View
    {
        private static final int BACKGROUND_COLOR = Color.rgb( 20, 17, 14 );
        private static final int STONE_DARK_COLOR = Color.rgb( 47, 43, 38 );
        private static final int STONE_LIGHT_COLOR = Color.rgb( 67, 61, 52 );
        private static final int PANEL_COLOR = Color.rgb( 77, 57, 34 );
        private static final int PANEL_INNER_COLOR = Color.rgb( 38, 29, 20 );
        private static final int BUTTON_COLOR = Color.rgb( 112, 77, 38 );
        private static final int BUTTON_PRESSED_COLOR = Color.rgb( 163, 111, 47 );
        private static final int GOLD_COLOR = Color.rgb( 225, 188, 100 );
        private static final int GOLD_LIGHT_COLOR = Color.rgb( 255, 230, 154 );
        private static final int SHADOW_COLOR = Color.rgb( 30, 20, 12 );
        private static final int TEXT_COLOR = Color.rgb( 255, 239, 190 );
        private static final int MUTED_TEXT_COLOR = Color.rgb( 190, 164, 112 );

        private final KeySender keySender;
        private final ActionSender actionSender;
        private final Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG );
        private final List<CommandButton> buttons = new ArrayList<>();

        private CommandButton pressedButton;
        private int gameContext = -1;
        private long enabledActions = -1L;
        private String contextTitle = "COMMAND DECK";
        private int informationContext = -1;
        private long informationRevision = -1;
        private String informationTitle = "";
        private String informationCategory = "";
        private String informationDetail = "";
        private String informationDate = "";
        private String informationResources = "";

        CommandDeckView( final Context context, final KeySender keySender, final ActionSender actionSender )
        {
            super( context );
            this.keySender = keySender;
            this.actionSender = actionSender;
            setBackgroundColor( BACKGROUND_COLOR );
            setFocusable( true );
            setGameState( CONTEXT_FALLBACK, -1L, null );
        }

        void setGameState( final int requestedContext, final long requestedEnabledActions, final String[] requestedInformationSnapshot )
        {
            final int context = requestedContext >= CONTEXT_FALLBACK && requestedContext <= CONTEXT_BATTLE ? requestedContext : CONTEXT_FALLBACK;
            final boolean informationChanged = applyInformationSnapshot( requestedInformationSnapshot );
            if ( gameContext == context && enabledActions == requestedEnabledActions && !informationChanged ) {
                return;
            }

            releasePressedButton();
            final boolean contextChanged = gameContext != context;
            gameContext = context;
            enabledActions = requestedEnabledActions;
            if ( contextChanged ) {
                rebuildActions();
                layoutButtons( getWidth(), getHeight() );
            }
            invalidate();
        }

        private boolean applyInformationSnapshot( final String[] snapshot )
        {
            if ( snapshot == null || snapshot.length < 8 ) {
                return false;
            }

            try {
                final int version = Integer.parseInt( snapshot[0] );
                final int context = Integer.parseInt( snapshot[1] );
                final long revision = Long.parseLong( snapshot[2] );
                if ( version != 1 || revision == informationRevision ) {
                    return false;
                }

                informationContext = context;
                informationRevision = revision;
                informationTitle = snapshot[3] == null ? "" : snapshot[3];
                informationCategory = snapshot[4] == null ? "" : snapshot[4];
                informationDetail = snapshot[5] == null ? "" : snapshot[5];
                informationDate = snapshot[6] == null ? "" : snapshot[6];
                informationResources = snapshot[7] == null ? "" : snapshot[7];
                return true;
            }
            catch ( final NumberFormatException ex ) {
                return false;
            }
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
            drawStoneBackground( canvas );
            drawReservedInformationPanel( canvas );

            for ( final CommandButton button : buttons ) {
                drawButton( canvas, button );
            }
        }

        private void drawStoneBackground( final Canvas canvas )
        {
            canvas.drawColor( BACKGROUND_COLOR );
            paint.setStyle( Paint.Style.FILL );
            final int tileSize = Math.max( 80, getWidth() / 10 );
            for ( int y = 0; y < getHeight(); y += tileSize ) {
                final int row = y / tileSize;
                for ( int x = -tileSize; x < getWidth(); x += tileSize ) {
                    final int offsetX = ( row % 2 == 0 ) ? 0 : tileSize / 2;
                    paint.setColor( ( ( x / tileSize ) + row ) % 2 == 0 ? STONE_DARK_COLOR : STONE_LIGHT_COLOR );
                    canvas.drawRect( x + offsetX + 2, y + 2, x + offsetX + tileSize - 2, y + tileSize - 2, paint );
                }
            }
        }

        private void drawReservedInformationPanel( final Canvas canvas )
        {
            final float margin = getMargin();
            final RectF shadow = new RectF( margin + 7, margin + 8, getWidth() - margin + 7, getHeight() * 0.235f + 8 );
            paint.setColor( SHADOW_COLOR );
            canvas.drawRoundRect( shadow, 18, 18, paint );

            final RectF panel = new RectF( margin, margin, getWidth() - margin, getHeight() * 0.235f );
            paint.setColor( PANEL_COLOR );
            canvas.drawRoundRect( panel, 18, 18, paint );

            paint.setStyle( Paint.Style.STROKE );
            paint.setStrokeWidth( 5 );
            paint.setColor( GOLD_COLOR );
            canvas.drawRoundRect( panel, 18, 18, paint );

            final RectF innerPanel = new RectF( panel.left + 14, panel.top + 14, panel.right - 14, panel.bottom - 14 );
            paint.setStrokeWidth( 2 );
            paint.setColor( GOLD_LIGHT_COLOR );
            canvas.drawRoundRect( innerPanel, 12, 12, paint );
            paint.setStyle( Paint.Style.FILL );
            paint.setColor( PANEL_INNER_COLOR );
            canvas.drawRoundRect( new RectF( innerPanel.left + 3, innerPanel.top + 3, innerPanel.right - 3, innerPanel.bottom - 3 ), 10, 10, paint );

            if ( ( gameContext == CONTEXT_ADVENTURE_MAP || gameContext == CONTEXT_HERO || gameContext == CONTEXT_CASTLE || gameContext == CONTEXT_BATTLE )
                 && informationContext == gameContext && informationRevision >= 0 && !informationTitle.isEmpty() ) {
                if ( gameContext == CONTEXT_BATTLE ) {
                    drawBattleInformationCard( canvas, innerPanel );
                }
                else {
                    drawInformationCard( canvas, innerPanel );
                }
            }
            else {
                paint.setTextAlign( Paint.Align.CENTER );
                paint.setTypeface( Typeface.create( Typeface.SERIF, Typeface.BOLD ) );
                paint.setColor( TEXT_COLOR );
                paint.setTextSize( Math.min( panel.height() * 0.34f, 68f ) );
                final float titleBaseline = panel.centerY() - 8 - ( paint.ascent() + paint.descent() ) / 2;
                canvas.drawText( contextTitle, panel.centerX(), titleBaseline, paint );

                paint.setTypeface( Typeface.create( Typeface.SERIF, Typeface.NORMAL ) );
                paint.setColor( MUTED_TEXT_COLOR );
                paint.setTextSize( Math.min( panel.height() * 0.14f, 27f ) );
                canvas.drawText( "INFORMATION PANEL RESERVED", panel.centerX(), panel.bottom - 25, paint );
            }
        }

        private void drawInformationCard( final Canvas canvas, final RectF panel )
        {
            final float horizontalPadding = 26f;
            final float contentWidth = panel.width() - horizontalPadding * 2;

            paint.setTypeface( Typeface.create( Typeface.SERIF, Typeface.BOLD ) );
            paint.setColor( GOLD_LIGHT_COLOR );
            paint.setTextSize( 27f );
            paint.setTextAlign( Paint.Align.LEFT );
            canvas.drawText( informationCategory, panel.left + horizontalPadding, panel.top + 39f, paint );

            paint.setTextAlign( Paint.Align.RIGHT );
            paint.setColor( MUTED_TEXT_COLOR );
            drawFittedText( canvas, informationDate, panel.right - horizontalPadding, panel.top + 39f, contentWidth * 0.55f, 27f );

            paint.setTextAlign( Paint.Align.CENTER );
            paint.setTypeface( Typeface.create( Typeface.SERIF, Typeface.BOLD ) );
            paint.setColor( TEXT_COLOR );
            drawFittedText( canvas, informationTitle, panel.centerX(), panel.top + panel.height() * 0.48f, contentWidth, 48f );

            paint.setTypeface( Typeface.create( Typeface.SERIF, Typeface.NORMAL ) );
            paint.setColor( GOLD_LIGHT_COLOR );
            drawFittedText( canvas, informationDetail, panel.centerX(), panel.top + panel.height() * 0.68f, contentWidth, 28f );

            paint.setColor( GOLD_COLOR );
            paint.setStrokeWidth( 2f );
            canvas.drawRect( panel.left + horizontalPadding, panel.top + panel.height() * 0.76f, panel.right - horizontalPadding,
                             panel.top + panel.height() * 0.76f + 2f, paint );

            paint.setColor( MUTED_TEXT_COLOR );
            drawFittedText( canvas, informationResources, panel.centerX(), panel.bottom - 18f, contentWidth, 25f );
        }

        private void drawBattleInformationCard( final Canvas canvas, final RectF panel )
        {
            final float horizontalPadding = 26f;
            final float contentWidth = panel.width() - horizontalPadding * 2;
            final int lineBreak = informationResources.indexOf( '\n' );
            final String condition = lineBreak >= 0 ? informationResources.substring( 0, lineBreak ) : informationResources;
            final String turnOrder = lineBreak >= 0 ? informationResources.substring( lineBreak + 1 ) : "";

            paint.setTypeface( Typeface.create( Typeface.SERIF, Typeface.BOLD ) );
            paint.setColor( GOLD_LIGHT_COLOR );
            paint.setTextAlign( Paint.Align.LEFT );
            drawFittedText( canvas, informationCategory, panel.left + horizontalPadding, panel.top + 35f, contentWidth * 0.55f, 26f );

            paint.setTextAlign( Paint.Align.RIGHT );
            paint.setColor( MUTED_TEXT_COLOR );
            drawFittedText( canvas, informationDate, panel.right - horizontalPadding, panel.top + 35f, contentWidth * 0.4f, 26f );

            paint.setTextAlign( Paint.Align.CENTER );
            paint.setColor( TEXT_COLOR );
            drawFittedText( canvas, informationTitle, panel.centerX(), panel.top + panel.height() * 0.43f, contentWidth, 43f );

            paint.setTypeface( Typeface.create( Typeface.SERIF, Typeface.NORMAL ) );
            paint.setColor( GOLD_LIGHT_COLOR );
            drawFittedText( canvas, informationDetail, panel.centerX(), panel.top + panel.height() * 0.61f, contentWidth, 25f );

            paint.setColor( MUTED_TEXT_COLOR );
            drawFittedText( canvas, condition, panel.centerX(), panel.top + panel.height() * 0.76f, contentWidth, 23f );

            paint.setColor( GOLD_COLOR );
            paint.setStrokeWidth( 2f );
            canvas.drawRect( panel.left + horizontalPadding, panel.top + panel.height() * 0.81f, panel.right - horizontalPadding,
                             panel.top + panel.height() * 0.81f + 2f, paint );

            paint.setColor( MUTED_TEXT_COLOR );
            drawFittedText( canvas, turnOrder, panel.centerX(), panel.bottom - 12f, contentWidth, 21f );
        }

        private void drawFittedText( final Canvas canvas, final String text, final float x, final float baseline, final float maximumWidth, final float preferredSize )
        {
            paint.setTextSize( preferredSize );
            final float measuredWidth = paint.measureText( text );
            if ( measuredWidth > maximumWidth && measuredWidth > 0 ) {
                paint.setTextSize( preferredSize * maximumWidth / measuredWidth );
            }
            canvas.drawText( text, x, baseline, paint );
        }

        private void drawButton( final Canvas canvas, final CommandButton button )
        {
            final RectF shadow = new RectF( button.bounds.left + 6, button.bounds.top + 7, button.bounds.right + 6, button.bounds.bottom + 7 );
            paint.setStyle( Paint.Style.FILL );
            paint.setColor( SHADOW_COLOR );
            canvas.drawRoundRect( shadow, 17, 17, paint );

            final boolean enabled = isEnabled( button );
            paint.setColor( !enabled ? PANEL_COLOR : ( button == pressedButton ? BUTTON_PRESSED_COLOR : BUTTON_COLOR ) );
            canvas.drawRoundRect( button.bounds, 17, 17, paint );

            paint.setStyle( Paint.Style.STROKE );
            paint.setStrokeWidth( 5 );
            paint.setColor( GOLD_COLOR );
            canvas.drawRoundRect( button.bounds, 17, 17, paint );

            paint.setStrokeWidth( 2 );
            paint.setColor( !enabled ? GOLD_COLOR : ( button == pressedButton ? SHADOW_COLOR : GOLD_LIGHT_COLOR ) );
            final RectF inner = new RectF( button.bounds.left + 8, button.bounds.top + 8, button.bounds.right - 8, button.bounds.bottom - 8 );
            canvas.drawRoundRect( inner, 11, 11, paint );

            paint.setStyle( Paint.Style.FILL );
            paint.setColor( enabled ? TEXT_COLOR : MUTED_TEXT_COLOR );
            paint.setTextAlign( Paint.Align.CENTER );
            paint.setTypeface( Typeface.create( Typeface.SERIF, Typeface.BOLD ) );
            final float widthLimitedSize = button.bounds.width() / Math.max( 3.5f, button.label.length() * 0.62f );
            paint.setTextSize( Math.min( 42f, Math.min( button.bounds.height() * 0.25f, widthLimitedSize ) ) );
            final float maximumTextWidth = button.bounds.width() - 28;
            final float measuredTextWidth = paint.measureText( button.label );
            if ( measuredTextWidth > maximumTextWidth ) {
                paint.setTextSize( paint.getTextSize() * maximumTextWidth / measuredTextWidth );
            }
            final float baseline = button.bounds.centerY() - ( paint.ascent() + paint.descent() ) / 2;
            canvas.drawText( button.label, button.bounds.centerX(), baseline, paint );
        }

        @Override
        public boolean onTouchEvent( final MotionEvent event )
        {
            if ( event.getActionIndex() != 0 ) {
                return true;
            }

            final int action = event.getActionMasked();
            if ( action == MotionEvent.ACTION_DOWN ) {
                pressedButton = buttonAt( event.getX(), event.getY() );
                if ( pressedButton != null ) {
                    pressedButton.sentSemantically = pressedButton.action != ACTION_NONE && actionSender.send( pressedButton.action );
                    if ( !pressedButton.sentSemantically ) {
                        keySender.send( pressedButton.keyCode, true );
                    }
                    invalidate();
                }
                return true;
            }

            if ( action == MotionEvent.ACTION_MOVE ) {
                if ( pressedButton != null && !pressedButton.bounds.contains( event.getX(), event.getY() ) ) {
                    releasePressedButton();
                }
                return true;
            }

            if ( action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL ) {
                releasePressedButton();
                return true;
            }

            return true;
        }

        private void rebuildActions()
        {
            buttons.clear();

            switch ( gameContext ) {
            case CONTEXT_MAIN_MENU:
                contextTitle = "MAIN MENU";
                addAction( "NEW GAME", KeyEvent.KEYCODE_N );
                addAction( "LOAD GAME", KeyEvent.KEYCODE_L );
                addAction( "SETTINGS", KeyEvent.KEYCODE_T );
                addAction( "HIGH SCORES", KeyEvent.KEYCODE_H );
                addAction( "CREDITS", KeyEvent.KEYCODE_C );
                addAction( "QUIT", KeyEvent.KEYCODE_Q );
                break;
            case CONTEXT_ADVENTURE_MAP:
                contextTitle = "ADVENTURE MAP";
                addAction( "NEXT HERO", ACTION_ADVENTURE_NEXT_HERO, KeyEvent.KEYCODE_H );
                addAction( "NEXT TOWN", ACTION_ADVENTURE_NEXT_TOWN, KeyEvent.KEYCODE_T );
                addAction( "MOVE", ACTION_ADVENTURE_MOVE, KeyEvent.KEYCODE_M );
                addAction( "ACTION", ACTION_ADVENTURE_DEFAULT_ACTION, KeyEvent.KEYCODE_SPACE );
                addAction( "SPELL", ACTION_ADVENTURE_CAST_SPELL, KeyEvent.KEYCODE_C );
                addAction( "END TURN", ACTION_ADVENTURE_END_TURN, KeyEvent.KEYCODE_E );
                addAction( "ADVENTURE", ACTION_ADVENTURE_OPTIONS, KeyEvent.KEYCODE_A );
                addAction( "FILE", ACTION_ADVENTURE_FILE_OPTIONS, KeyEvent.KEYCODE_F );
                addAction( "PUZZLE", ACTION_ADVENTURE_PUZZLE_MAP, KeyEvent.KEYCODE_P );
                addAction( "KINGDOM", ACTION_ADVENTURE_KINGDOM_SUMMARY, KeyEvent.KEYCODE_K );
                addAction( "VIEW WORLD", ACTION_ADVENTURE_VIEW_WORLD, KeyEvent.KEYCODE_V );
                addAction( "DIG", ACTION_ADVENTURE_DIG_ARTIFACT, KeyEvent.KEYCODE_D );
                break;
            case CONTEXT_HERO:
                contextTitle = "HERO";
                addAction( "PREVIOUS", ACTION_HERO_PREVIOUS, KeyEvent.KEYCODE_DPAD_LEFT );
                addAction( "NEXT", ACTION_HERO_NEXT, KeyEvent.KEYCODE_DPAD_RIGHT );
                addAction( "DISMISS", ACTION_HERO_DISMISS, KeyEvent.KEYCODE_D );
                addAction( "SPLIT HALF", ACTION_HERO_SPLIT_SELECTED_HALF, KeyEvent.KEYCODE_SHIFT_LEFT );
                addAction( "SPLIT ONE", ACTION_HERO_SPLIT_SELECTED_ONE, KeyEvent.KEYCODE_CTRL_LEFT );
                addAction( "JOIN", ACTION_HERO_JOIN_SELECTED, KeyEvent.KEYCODE_ALT_LEFT );
                addAction( "SWAP", ACTION_HERO_SWAP_ARMIES, KeyEvent.KEYCODE_X );
                addAction( "CLOSE", ACTION_HERO_CLOSE, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_CASTLE:
                contextTitle = "CASTLE";
                addAction( "PREVIOUS", ACTION_CASTLE_PREVIOUS, KeyEvent.KEYCODE_DPAD_LEFT );
                addAction( "NEXT", ACTION_CASTLE_NEXT, KeyEvent.KEYCODE_DPAD_RIGHT );
                addAction( "WELL", ACTION_CASTLE_WELL, KeyEvent.KEYCODE_W );
                addAction( "MARKET", ACTION_CASTLE_MARKETPLACE, KeyEvent.KEYCODE_M );
                addAction( "MAGE GUILD", ACTION_CASTLE_MAGE_GUILD, KeyEvent.KEYCODE_S );
                addAction( "SHIPYARD", ACTION_CASTLE_SHIPYARD, KeyEvent.KEYCODE_N );
                addAction( "THIEVES", ACTION_CASTLE_THIEVES_GUILD, KeyEvent.KEYCODE_T );
                addAction( "TAVERN", ACTION_CASTLE_TAVERN, KeyEvent.KEYCODE_R );
                addAction( "BUILD", ACTION_CASTLE_CONSTRUCTION, KeyEvent.KEYCODE_B );
                addAction( "TO HERO", ACTION_CASTLE_TRANSFER_TO_HERO, KeyEvent.KEYCODE_DPAD_DOWN );
                addAction( "TO GARRISON", ACTION_CASTLE_TRANSFER_TO_GARRISON, KeyEvent.KEYCODE_DPAD_UP );
                addAction( "UPGRADE", ACTION_CASTLE_UPGRADE_SELECTED, KeyEvent.KEYCODE_U );
                addAction( "EXIT", ACTION_CASTLE_CLOSE, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_BATTLE:
                contextTitle = "BATTLE";
                addAction( "SPELL", ACTION_BATTLE_CAST_SPELL, KeyEvent.KEYCODE_C );
                addAction( "WAIT / DEFEND", ACTION_BATTLE_SKIP, KeyEvent.KEYCODE_SPACE );
                addAction( "AUTO", ACTION_BATTLE_TOGGLE_AUTO_COMBAT, KeyEvent.KEYCODE_A );
                addAction( "QUICK COMBAT", ACTION_BATTLE_QUICK_COMBAT, KeyEvent.KEYCODE_Q );
                addAction( "RETREAT", ACTION_BATTLE_RETREAT, KeyEvent.KEYCODE_R );
                addAction( "SURRENDER", ACTION_BATTLE_SURRENDER, KeyEvent.KEYCODE_S );
                addAction( "OPTIONS", ACTION_BATTLE_OPTIONS, KeyEvent.KEYCODE_O );
                addAction( "TURN ORDER", ACTION_BATTLE_TOGGLE_TURN_ORDER, KeyEvent.KEYCODE_T );
                break;
            case CONTEXT_DIALOG:
                contextTitle = "DIALOG";
                addDialogActions();
                break;
            case CONTEXT_FALLBACK:
            default:
                contextTitle = "COMMAND DECK";
                addDialogActions();
                break;
            }
        }

        private void addDialogActions()
        {
            addAction( "CONFIRM", KeyEvent.KEYCODE_ENTER );
            addAction( "CANCEL", KeyEvent.KEYCODE_ESCAPE );
        }

        private void addAction( final String label, final int keyCode )
        {
            addAction( label, ACTION_NONE, keyCode );
        }

        private void addAction( final String label, final int action, final int keyCode )
        {
            buttons.add( new CommandButton( label, action, keyCode ) );
        }

        private void layoutButtons( final int width, final int height )
        {
            if ( width <= 0 || height <= 0 || buttons.isEmpty() ) {
                return;
            }

            final float margin = getMargin();
            final float gap = margin * 0.55f;
            final float top = height * 0.265f;
            final int columns = buttons.size() <= 2 ? 2 : ( buttons.size() <= 6 ? 3 : 4 );
            final int rows = ( buttons.size() + columns - 1 ) / columns;
            final float columnWidth = ( width - 2 * margin - ( columns - 1 ) * gap ) / columns;
            final float rowHeight = ( height - top - margin - ( rows - 1 ) * gap ) / rows;

            for ( int i = 0; i < buttons.size(); ++i ) {
                final int column = i % columns;
                final int row = i / columns;
                final float left = margin + column * ( columnWidth + gap );
                final float buttonTop = top + row * ( rowHeight + gap );
                buttons.get( i ).bounds.set( left, buttonTop, left + columnWidth, buttonTop + rowHeight );
            }
        }

        private float getMargin()
        {
            return Math.max( 20, Math.min( getWidth(), getHeight() ) * 0.025f );
        }

        private CommandButton buttonAt( final float x, final float y )
        {
            for ( final CommandButton button : buttons ) {
                if ( button.bounds.contains( x, y ) && isEnabled( button ) ) {
                    return button;
                }
            }
            return null;
        }

        private boolean isEnabled( final CommandButton button )
        {
            return button.action == ACTION_NONE || ( enabledActions & ( 1L << button.action ) ) != 0;
        }

        void releasePressedButton()
        {
            if ( pressedButton != null ) {
                if ( !pressedButton.sentSemantically ) {
                    keySender.send( pressedButton.keyCode, false );
                }
                pressedButton.sentSemantically = false;
                pressedButton = null;
                invalidate();
            }
        }
    }

    private static final class CommandButton
    {
        final String label;
        final int action;
        final int keyCode;
        final RectF bounds = new RectF();
        boolean sentSemantically;

        CommandButton( final String label, final int action, final int keyCode )
        {
            this.label = label;
            this.action = action;
            this.keyCode = keyCode;
        }
    }
}
