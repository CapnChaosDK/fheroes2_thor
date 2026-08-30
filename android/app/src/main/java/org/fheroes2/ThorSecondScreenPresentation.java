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
import android.graphics.Bitmap;
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
    private static final int CONTEXT_NEW_GAME_MENU = 7;
    private static final int CONTEXT_CAMPAIGN_MENU = 8;
    private static final int CONTEXT_MULTIPLAYER_MENU = 9;
    private static final int CONTEXT_HOT_SEAT_MENU = 10;
    private static final int CONTEXT_LOAD_GAME_MENU = 11;
    private static final int CONTEXT_SCENARIO_SETUP = 12;
    private static final int CONTEXT_BATTLE_ONLY_SETUP = 13;
    private static final int CONTEXT_HIGH_SCORES_STANDARD = 14;
    private static final int CONTEXT_HIGH_SCORES_CAMPAIGN = 15;
    private static final int CONTEXT_CAMPAIGN_INTRO = 16;
    private static final int CONTEXT_SUCCESSION_WARS_CAMPAIGN = 17;
    private static final int CONTEXT_PRICE_OF_LOYALTY_CAMPAIGN = 18;
    private static final int CONTEXT_GAME_SETTINGS = 19;
    private static final int CONTEXT_EDITOR_MAIN_MENU = 20;
    private static final int CONTEXT_EDITOR_NEW_MAP_MENU = 21;
    private static final int CONTEXT_EDITOR_MAP_SIZE_SCRATCH = 22;
    private static final int CONTEXT_EDITOR_MAP_SIZE_RANDOM = 23;
    private static final int CONTEXT_EDITOR_INTERFACE = 24;
    private static final int CONTEXT_EDITOR_FILE_OPTIONS = 25;
    private static final int CONTEXT_EDITOR_SYSTEM_OPTIONS = 26;
    private static final int CONTEXT_EDITOR_MAP_SPECIFICATIONS = 27;
    private static final int CONTEXT_EDITOR_MAP_SPEC_PLAYERS = 28;
    private static final int CONTEXT_EDITOR_MAP_SPEC_VICTORY = 29;
    private static final int CONTEXT_EDITOR_MAP_SPEC_LOSS = 30;
    private static final int CONTEXT_EDITOR_TOOLS = 31;
    private static final int CONTEXT_EDITOR_TOOL_TERRAIN = 32;
    private static final int CONTEXT_EDITOR_TOOL_LANDSCAPE = 33;
    private static final int CONTEXT_EDITOR_TOOL_DETAIL = 34;
    private static final int CONTEXT_EDITOR_TOOL_ADVENTURE = 35;
    private static final int CONTEXT_EDITOR_TOOL_KINGDOM = 36;
    private static final int CONTEXT_EDITOR_TOOL_MONSTERS = 37;
    private static final int CONTEXT_EDITOR_TOOL_STREAMS = 38;
    private static final int CONTEXT_EDITOR_TOOL_ROADS = 39;
    private static final int CONTEXT_EDITOR_TOOL_ERASE = 40;
    private static final int CONTEXT_ADVENTURE_HERO_LIST = 41;
    private static final int CONTEXT_ADVENTURE_CASTLE_LIST = 42;

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
    private static final int ACTION_MENU_STANDARD_GAME = 43;
    private static final int ACTION_MENU_CAMPAIGN_GAME = 44;
    private static final int ACTION_MENU_MULTIPLAYER_GAME = 45;
    private static final int ACTION_MENU_BATTLE_ONLY = 46;
    private static final int ACTION_MENU_SETTINGS = 47;
    private static final int ACTION_MENU_BACK = 48;
    private static final int ACTION_MENU_ORIGINAL_CAMPAIGN = 49;
    private static final int ACTION_MENU_EXPANSION_CAMPAIGN = 50;
    private static final int ACTION_MENU_HOT_SEAT = 51;
    private static final int ACTION_MENU_HOT_SEAT_2_PLAYERS = 52;
    private static final int ACTION_MENU_HOT_SEAT_3_PLAYERS = 53;
    private static final int ACTION_MENU_HOT_SEAT_4_PLAYERS = 54;
    private static final int ACTION_MENU_HOT_SEAT_5_PLAYERS = 55;
    private static final int ACTION_MENU_HOT_SEAT_6_PLAYERS = 56;
    private static final int ACTION_MENU_LOAD_STANDARD = 57;
    private static final int ACTION_MENU_LOAD_CAMPAIGN = 58;
    private static final int ACTION_MENU_LOAD_HOT_SEAT = 59;
    private static final int ACTION_SCENARIO_SELECT_MAP = 60;
    private static final int ACTION_SCENARIO_DIFFICULTY_EASY = 61;
    private static final int ACTION_SCENARIO_DIFFICULTY_NORMAL = 62;
    private static final int ACTION_SCENARIO_DIFFICULTY_HARD = 63;
    private static final int ACTION_SCENARIO_DIFFICULTY_EXPERT = 64;
    private static final int ACTION_SCENARIO_DIFFICULTY_IMPOSSIBLE = 65;
    private static final int ACTION_SCENARIO_START = 66;
    private static final int ACTION_SCENARIO_PREVIOUS_PLAYER = 67;
    private static final int ACTION_SCENARIO_NEXT_PLAYER = 68;
    private static final int ACTION_SCENARIO_PLAYER_CONTROL = 69;
    private static final int ACTION_SCENARIO_PREVIOUS_FACTION = 70;
    private static final int ACTION_SCENARIO_NEXT_FACTION = 71;
    private static final int ACTION_SCENARIO_HANDICAP = 72;
    private static final int ACTION_BATTLE_ONLY_SELECT_ATTACKER = 73;
    private static final int ACTION_BATTLE_ONLY_SELECT_DEFENDER = 74;
    private static final int ACTION_BATTLE_ONLY_PREVIOUS_TERRAIN = 75;
    private static final int ACTION_BATTLE_ONLY_NEXT_TERRAIN = 76;
    private static final int ACTION_BATTLE_ONLY_TOGGLE_DEFENDER_CONTROL = 77;
    private static final int ACTION_BATTLE_ONLY_RESET = 78;
    private static final int ACTION_BATTLE_ONLY_START = 79;
    private static final int ACTION_BATTLE_ONLY_EXIT = 80;
    private static final int ACTION_HIGH_SCORES_VIEW_STANDARD = 81;
    private static final int ACTION_HIGH_SCORES_VIEW_CAMPAIGN = 82;
    private static final int ACTION_HIGH_SCORES_EXIT = 83;
    private static final int ACTION_CAMPAIGN_SELECT_ROLAND = 84;
    private static final int ACTION_CAMPAIGN_SELECT_ARCHIBALD = 85;
    private static final int ACTION_CAMPAIGN_SELECT_PRICE_OF_LOYALTY = 86;
    private static final int ACTION_CAMPAIGN_SELECT_VOYAGE_HOME = 87;
    private static final int ACTION_CAMPAIGN_SELECT_WIZARDS_ISLE = 88;
    private static final int ACTION_CAMPAIGN_SELECT_DESCENDANTS = 89;
    private static final int ACTION_GAME_SETTINGS_LANGUAGE = 90;
    private static final int ACTION_GAME_SETTINGS_GRAPHICS = 91;
    private static final int ACTION_GAME_SETTINGS_AUDIO = 92;
    private static final int ACTION_GAME_SETTINGS_HOT_KEYS = 93;
    private static final int ACTION_GAME_SETTINGS_CURSOR_TYPE = 94;
    private static final int ACTION_GAME_SETTINGS_INTERFACE_TYPE = 95;
    private static final int ACTION_GAME_SETTINGS_TEXT_SUPPORT = 96;
    private static final int ACTION_GAME_SETTINGS_CLOSE = 97;
    private static final int ACTION_MENU_EDITOR = 98;
    private static final int ACTION_EDITOR_NEW_MAP = 99;
    private static final int ACTION_EDITOR_LOAD_MAP = 100;
    private static final int ACTION_EDITOR_EXIT_TO_MAIN_MENU = 101;
    private static final int ACTION_EDITOR_FROM_SCRATCH = 102;
    private static final int ACTION_EDITOR_RANDOM_MAP = 103;
    private static final int ACTION_EDITOR_MAP_SIZE_SMALL = 104;
    private static final int ACTION_EDITOR_MAP_SIZE_MEDIUM = 105;
    private static final int ACTION_EDITOR_MAP_SIZE_LARGE = 106;
    private static final int ACTION_EDITOR_MAP_SIZE_EXTRA_LARGE = 107;
    private static final int ACTION_EDITOR_OPEN_FILE_OPTIONS = 108;
    private static final int ACTION_EDITOR_FILE_NEW_MAP = 109;
    private static final int ACTION_EDITOR_FILE_LOAD_MAP = 110;
    private static final int ACTION_EDITOR_FILE_START_MAP = 111;
    private static final int ACTION_EDITOR_FILE_SAVE_MAP = 112;
    private static final int ACTION_EDITOR_FILE_MAIN_MENU = 113;
    private static final int ACTION_EDITOR_FILE_QUIT = 114;
    private static final int ACTION_EDITOR_FILE_AUTO_PLAYTEST = 115;
    private static final int ACTION_EDITOR_FILE_CANCEL = 116;
    private static final int ACTION_EDITOR_OPEN_SYSTEM_OPTIONS = 117;
    private static final int ACTION_EDITOR_SYSTEM_LANGUAGE = 118;
    private static final int ACTION_EDITOR_SYSTEM_GRAPHICS = 119;
    private static final int ACTION_EDITOR_SYSTEM_AUDIO = 120;
    private static final int ACTION_EDITOR_SYSTEM_HOT_KEYS = 121;
    private static final int ACTION_EDITOR_SYSTEM_ANIMATION = 122;
    private static final int ACTION_EDITOR_SYSTEM_PASSABILITY = 123;
    private static final int ACTION_EDITOR_SYSTEM_INTERFACE_TYPE = 124;
    private static final int ACTION_EDITOR_SYSTEM_CURSOR_TYPE = 125;
    private static final int ACTION_EDITOR_SYSTEM_SCROLL_SPEED = 126;
    private static final int ACTION_EDITOR_SYSTEM_CLOSE = 127;
    private static final int ACTION_EDITOR_OPEN_MAP_SPECIFICATIONS = 128;
    private static final int ACTION_EDITOR_MAP_SPEC_NAME = 129;
    private static final int ACTION_EDITOR_MAP_SPEC_DESCRIPTION = 130;
    private static final int ACTION_EDITOR_MAP_SPEC_PLAYERS = 131;
    private static final int ACTION_EDITOR_MAP_SPEC_DIFFICULTY = 132;
    private static final int ACTION_EDITOR_MAP_SPEC_VICTORY = 133;
    private static final int ACTION_EDITOR_MAP_SPEC_LOSS = 134;
    private static final int ACTION_EDITOR_MAP_SPEC_RUMORS = 135;
    private static final int ACTION_EDITOR_MAP_SPEC_EVENTS = 136;
    private static final int ACTION_EDITOR_MAP_SPEC_LANGUAGE = 137;
    private static final int ACTION_EDITOR_MAP_SPEC_ABOUT = 138;
    private static final int ACTION_EDITOR_MAP_SPEC_OKAY = 139;
    private static final int ACTION_EDITOR_MAP_SPEC_CANCEL = 140;
    private static final int ACTION_EDITOR_MAP_SPEC_PREVIOUS_PLAYER = 141;
    private static final int ACTION_EDITOR_MAP_SPEC_NEXT_PLAYER = 142;
    private static final int ACTION_EDITOR_MAP_SPEC_PLAYER_TYPE = 143;
    private static final int ACTION_EDITOR_MAP_SPEC_PREVIOUS_CONDITION = 144;
    private static final int ACTION_EDITOR_MAP_SPEC_NEXT_CONDITION = 145;
    private static final int ACTION_EDITOR_MAP_SPEC_SELECT_TARGET = 146;
    private static final int ACTION_EDITOR_MAP_SPEC_TOGGLE_STANDARD_VICTORY = 147;
    private static final int ACTION_EDITOR_MAP_SPEC_TOGGLE_AI_VICTORY = 148;
    private static final int ACTION_EDITOR_MAP_SPEC_PREVIOUS_ALLIANCE_PLAYER = 149;
    private static final int ACTION_EDITOR_MAP_SPEC_NEXT_ALLIANCE_PLAYER = 150;
    private static final int ACTION_EDITOR_MAP_SPEC_SWITCH_ALLIANCE = 151;
    private static final int ACTION_EDITOR_MAP_SPEC_DECREASE_VALUE = 152;
    private static final int ACTION_EDITOR_MAP_SPEC_INCREASE_VALUE = 153;
    private static final int ACTION_EDITOR_MAP_SPEC_SUBMENU_BACK = 154;
    private static final int ACTION_EDITOR_OPEN_TOOLS = 155;
    private static final int ACTION_EDITOR_TOOL_TERRAIN = 156;
    private static final int ACTION_EDITOR_TOOL_LANDSCAPE = 157;
    private static final int ACTION_EDITOR_TOOL_DETAIL = 158;
    private static final int ACTION_EDITOR_TOOL_ADVENTURE = 159;
    private static final int ACTION_EDITOR_TOOL_KINGDOM = 160;
    private static final int ACTION_EDITOR_TOOL_MONSTERS = 161;
    private static final int ACTION_EDITOR_TOOL_STREAMS = 162;
    private static final int ACTION_EDITOR_TOOL_ROADS = 163;
    private static final int ACTION_EDITOR_TOOL_ERASE = 164;
    private static final int ACTION_EDITOR_TOOL_MAGNIFY = 165;
    private static final int ACTION_EDITOR_TOOL_UNDO = 166;
    private static final int ACTION_EDITOR_TOOL_REDO = 167;
    private static final int ACTION_EDITOR_TOOL_BACK = 168;
    private static final int ACTION_EDITOR_BRUSH_SMALL = 169;
    private static final int ACTION_EDITOR_BRUSH_MEDIUM = 170;
    private static final int ACTION_EDITOR_BRUSH_LARGE = 171;
    private static final int ACTION_EDITOR_BRUSH_AREA = 172;
    private static final int ACTION_EDITOR_TERRAIN_WATER = 173;
    private static final int ACTION_EDITOR_TERRAIN_GRASS = 174;
    private static final int ACTION_EDITOR_TERRAIN_SNOW = 175;
    private static final int ACTION_EDITOR_TERRAIN_SWAMP = 176;
    private static final int ACTION_EDITOR_TERRAIN_LAVA = 177;
    private static final int ACTION_EDITOR_TERRAIN_DESERT = 178;
    private static final int ACTION_EDITOR_TERRAIN_DIRT = 179;
    private static final int ACTION_EDITOR_TERRAIN_WASTELAND = 180;
    private static final int ACTION_EDITOR_TERRAIN_BEACH = 181;
    private static final int ACTION_EDITOR_LANDSCAPE_MOUNTAINS = 182;
    private static final int ACTION_EDITOR_LANDSCAPE_ROCKS = 183;
    private static final int ACTION_EDITOR_LANDSCAPE_TREES = 184;
    private static final int ACTION_EDITOR_LANDSCAPE_WATER = 185;
    private static final int ACTION_EDITOR_LANDSCAPE_MISC = 186;
    private static final int ACTION_EDITOR_DETAIL_EDIT = 187;
    private static final int ACTION_EDITOR_DETAIL_MOVE = 188;
    private static final int ACTION_EDITOR_DETAIL_COPY = 189;
    private static final int ACTION_EDITOR_ADVENTURE_ARTIFACTS = 190;
    private static final int ACTION_EDITOR_ADVENTURE_DWELLINGS = 191;
    private static final int ACTION_EDITOR_ADVENTURE_MINES = 192;
    private static final int ACTION_EDITOR_ADVENTURE_POWER_UPS = 193;
    private static final int ACTION_EDITOR_ADVENTURE_TREASURES = 194;
    private static final int ACTION_EDITOR_ADVENTURE_WATER = 195;
    private static final int ACTION_EDITOR_ADVENTURE_MISC = 196;
    private static final int ACTION_EDITOR_KINGDOM_HEROES = 197;
    private static final int ACTION_EDITOR_KINGDOM_TOWNS = 198;
    private static final int ACTION_EDITOR_MONSTER_SELECT = 199;
    private static final int ACTION_EDITOR_ERASE_MOUNTAINS = 200;
    private static final int ACTION_EDITOR_ERASE_ROCKS = 201;
    private static final int ACTION_EDITOR_ERASE_TREES = 202;
    private static final int ACTION_EDITOR_ERASE_LANDSCAPE = 203;
    private static final int ACTION_EDITOR_ERASE_ADVENTURE_NON_PICKABLE = 204;
    private static final int ACTION_EDITOR_ERASE_TOWNS = 205;
    private static final int ACTION_EDITOR_ERASE_ADVENTURE_PICKABLE = 206;
    private static final int ACTION_EDITOR_ERASE_MONSTERS = 207;
    private static final int ACTION_EDITOR_ERASE_HEROES = 208;
    private static final int ACTION_EDITOR_ERASE_ROADS = 209;
    private static final int ACTION_EDITOR_ERASE_STREAMS = 210;
    private static final int ACTION_ADVENTURE_OPEN_HERO_LIST = 211;
    private static final int ACTION_ADVENTURE_OPEN_CASTLE_LIST = 212;
    private static final int ACTION_ADVENTURE_SELECTION_BACK = 213;

    private static final int SELECTION_PAGE_SIZE = 8;
    private static final int LOCAL_PREVIOUS_PAGE = 1;
    private static final int LOCAL_NEXT_PAGE = 2;

    interface KeySender
    {
        void send( int keyCode, boolean pressed );
    }

    interface ActionSender
    {
        boolean send( int action );
    }

    interface ViewportSender
    {
        boolean send( float normalizedX, float normalizedY );
    }

    interface SelectionSender
    {
        boolean send( int context, long revision, int id );
    }

    private final KeySender keySender;
    private final ActionSender actionSender;
    private final ViewportSender viewportSender;
    private final SelectionSender selectionSender;
    private CommandDeckView commandDeckView;

    ThorSecondScreenPresentation( final Context context, final Display display, final KeySender keySender, final ActionSender actionSender,
                                  final ViewportSender viewportSender, final SelectionSender selectionSender )
    {
        super( context, display );
        this.keySender = keySender;
        this.actionSender = actionSender;
        this.viewportSender = viewportSender;
        this.selectionSender = selectionSender;
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

        commandDeckView = new CommandDeckView( getContext(), keySender, actionSender, viewportSender, selectionSender );
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

    void setGameState( final int context, final long enabledActions, final String[] informationSnapshot, final boolean viewportControlEnabled,
                       final int[] radarSnapshot, final String[] selectionSnapshot )
    {
        if ( commandDeckView != null ) {
            commandDeckView.setGameState( context, enabledActions, informationSnapshot, viewportControlEnabled, radarSnapshot, selectionSnapshot );
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
        private final ViewportSender viewportSender;
        private final SelectionSender selectionSender;
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
        private final RectF radarBounds = new RectF();
        private Bitmap radarBitmap;
        private int radarContext = -1;
        private long radarRevision = -1;
        private int radarWorldWidth;
        private int radarWorldHeight;
        private int radarViewportX;
        private int radarViewportY;
        private int radarViewportWidth;
        private int radarViewportHeight;
        private boolean viewportControlEnabled;
        private boolean radarGestureActive;
        private int selectionContext = -1;
        private long selectionRevision = -1;
        private final List<SelectionEntry> selectionEntries = new ArrayList<>();
        private int selectionPage;

        CommandDeckView( final Context context, final KeySender keySender, final ActionSender actionSender, final ViewportSender viewportSender,
                         final SelectionSender selectionSender )
        {
            super( context );
            this.keySender = keySender;
            this.actionSender = actionSender;
            this.viewportSender = viewportSender;
            this.selectionSender = selectionSender;
            setBackgroundColor( BACKGROUND_COLOR );
            setFocusable( true );
            setGameState( CONTEXT_FALLBACK, -1L, null, false, null, null );
        }

        void setGameState( final int requestedContext, final long requestedEnabledActions, final String[] requestedInformationSnapshot,
                           final boolean requestedViewportControlEnabled, final int[] requestedRadarSnapshot, final String[] requestedSelectionSnapshot )
        {
            final int context
                = requestedContext >= CONTEXT_FALLBACK && requestedContext <= CONTEXT_ADVENTURE_CASTLE_LIST ? requestedContext : CONTEXT_FALLBACK;
            final boolean informationChanged = applyInformationSnapshot( requestedInformationSnapshot );
            final boolean radarChanged = applyRadarSnapshot( requestedRadarSnapshot );
            final boolean selectionChanged = applySelectionSnapshot( requestedSelectionSnapshot );
            if ( gameContext == context && enabledActions == requestedEnabledActions && viewportControlEnabled == requestedViewportControlEnabled
                 && !informationChanged && !radarChanged && !selectionChanged ) {
                return;
            }

            final boolean contextChanged = gameContext != context;
            if ( contextChanged || enabledActions != requestedEnabledActions || ( viewportControlEnabled && !requestedViewportControlEnabled ) ) {
                releasePressedButton();
            }
            gameContext = context;
            enabledActions = requestedEnabledActions;
            viewportControlEnabled = requestedViewportControlEnabled;
            if ( !viewportControlEnabled ) {
                radarGestureActive = false;
            }
            if ( contextChanged || selectionChanged
                 || ( informationChanged && ( gameContext == CONTEXT_SCENARIO_SETUP || gameContext == CONTEXT_BATTLE_ONLY_SETUP ) ) ) {
                rebuildActions();
                layoutButtons( getWidth(), getHeight() );
            }
            invalidate();
        }

        private boolean applyRadarSnapshot( final int[] snapshot )
        {
            if ( snapshot == null || snapshot.length < 12 || snapshot[0] != 1 || snapshot[2] == radarRevision ) {
                return false;
            }

            radarRevision = snapshot[2];
            radarContext = snapshot[1];
            final int width = snapshot[3];
            final int height = snapshot[4];
            final int pixelCount = snapshot[11];
            if ( width <= 0 || height <= 0 || pixelCount != width * height || snapshot.length != 12 + pixelCount ) {
                radarBitmap = null;
                radarWorldWidth = 0;
                radarWorldHeight = 0;
                return true;
            }

            radarWorldWidth = snapshot[5];
            radarWorldHeight = snapshot[6];
            radarViewportX = snapshot[7];
            radarViewportY = snapshot[8];
            radarViewportWidth = snapshot[9];
            radarViewportHeight = snapshot[10];
            radarBitmap = Bitmap.createBitmap( snapshot, 12, width, width, height, Bitmap.Config.ARGB_8888 );
            return true;
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

        private boolean applySelectionSnapshot( final String[] snapshot )
        {
            if ( snapshot == null || snapshot.length < 4 ) {
                return false;
            }

            try {
                final int version = Integer.parseInt( snapshot[0] );
                final int context = Integer.parseInt( snapshot[1] );
                final long revision = Long.parseLong( snapshot[2] );
                final int count = Integer.parseInt( snapshot[3] );
                if ( version != 1 || revision == selectionRevision || count < 0 || snapshot.length != 4 + count * 4 ) {
                    return false;
                }

                final List<SelectionEntry> entries = new ArrayList<>( count );
                int selectedIndex = -1;
                for ( int index = 0; index < count; ++index ) {
                    final int offset = 4 + index * 4;
                    final int id = Integer.parseInt( snapshot[offset] );
                    final boolean selected = "1".equals( snapshot[offset + 3] );
                    entries.add( new SelectionEntry( id, snapshot[offset + 1] == null ? "" : snapshot[offset + 1],
                                                     snapshot[offset + 2] == null ? "" : snapshot[offset + 2], selected ) );
                    if ( selected ) {
                        selectedIndex = index;
                    }
                }

                selectionContext = context;
                selectionRevision = revision;
                selectionEntries.clear();
                selectionEntries.addAll( entries );
                selectionPage = selectedIndex < 0 ? 0 : selectedIndex / SELECTION_PAGE_SIZE;
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

            final RectF informationPanel = new RectF( innerPanel );
            if ( hasRadarSnapshot() ) {
                final float radarSize = innerPanel.height() - 16f;
                radarBounds.set( innerPanel.left + 8f, innerPanel.top + 8f, innerPanel.left + 8f + radarSize, innerPanel.top + 8f + radarSize );
                drawRadar( canvas );
                informationPanel.left = radarBounds.right + 20f;
            }
            else {
                radarBounds.setEmpty();
            }

            if ( ( gameContext == CONTEXT_ADVENTURE_MAP || gameContext == CONTEXT_HERO || gameContext == CONTEXT_CASTLE || gameContext == CONTEXT_BATTLE
                   || gameContext == CONTEXT_SCENARIO_SETUP || gameContext == CONTEXT_BATTLE_ONLY_SETUP || gameContext == CONTEXT_GAME_SETTINGS
                   || gameContext == CONTEXT_EDITOR_INTERFACE || gameContext == CONTEXT_EDITOR_SYSTEM_OPTIONS || gameContext == CONTEXT_EDITOR_MAP_SPECIFICATIONS
                   || gameContext == CONTEXT_EDITOR_MAP_SPEC_PLAYERS || gameContext == CONTEXT_EDITOR_MAP_SPEC_VICTORY
                   || gameContext == CONTEXT_EDITOR_MAP_SPEC_LOSS || ( gameContext >= CONTEXT_EDITOR_TOOLS && gameContext <= CONTEXT_EDITOR_TOOL_ERASE ) )
                 && informationContext == gameContext && informationRevision >= 0 && !informationTitle.isEmpty() ) {
                if ( gameContext == CONTEXT_BATTLE ) {
                    drawBattleInformationCard( canvas, informationPanel );
                }
                else {
                    drawInformationCard( canvas, informationPanel );
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
            canvas.drawRect( panel.left + horizontalPadding, panel.top + panel.height() * 0.76f, panel.right - horizontalPadding, panel.top + panel.height() * 0.76f + 2f,
                             paint );

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
            canvas.drawRect( panel.left + horizontalPadding, panel.top + panel.height() * 0.81f, panel.right - horizontalPadding, panel.top + panel.height() * 0.81f + 2f,
                             paint );

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
            paint.setColor( !enabled ? PANEL_COLOR : ( button == pressedButton || button.selected ? BUTTON_PRESSED_COLOR : BUTTON_COLOR ) );
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
            final float baseline = button.detail.isEmpty() ? button.bounds.centerY() - ( paint.ascent() + paint.descent() ) / 2 : button.bounds.centerY() - 7f;
            canvas.drawText( button.label, button.bounds.centerX(), baseline, paint );

            if ( !button.detail.isEmpty() ) {
                paint.setTypeface( Typeface.create( Typeface.SERIF, Typeface.NORMAL ) );
                paint.setColor( enabled ? GOLD_LIGHT_COLOR : MUTED_TEXT_COLOR );
                drawFittedText( canvas, button.detail, button.bounds.centerX(), button.bounds.centerY() + 28f, maximumTextWidth, 22f );
            }
        }

        @Override
        public boolean onTouchEvent( final MotionEvent event )
        {
            final int action = event.getActionMasked();
            if ( action == MotionEvent.ACTION_POINTER_DOWN || action == MotionEvent.ACTION_POINTER_UP ) {
                radarGestureActive = false;
                releasePressedButton();
                return true;
            }

            if ( action == MotionEvent.ACTION_DOWN ) {
                if ( event.getPointerCount() == 1 && viewportControlEnabled && hasRadarSnapshot()
                     && radarBounds.contains( event.getX(), event.getY() ) ) {
                    radarGestureActive = sendViewportRequest( event.getX(), event.getY() );
                    return true;
                }

                pressedButton = buttonAt( event.getX(), event.getY() );
                if ( pressedButton != null ) {
                    if ( pressedButton.selectionId >= 0 ) {
                        pressedButton.sentSemantically
                            = selectionContext == gameContext && selectionSender.send( gameContext, selectionRevision, pressedButton.selectionId );
                    }
                    else if ( pressedButton.localCommand != 0 ) {
                        changeSelectionPage( pressedButton.localCommand );
                        return true;
                    }
                    else {
                        pressedButton.sentSemantically = pressedButton.action != ACTION_NONE && actionSender.send( pressedButton.action );
                    }
                    if ( !pressedButton.sentSemantically && pressedButton.keyCode != KeyEvent.KEYCODE_UNKNOWN ) {
                        keySender.send( pressedButton.keyCode, true );
                    }
                    invalidate();
                }
                return true;
            }

            if ( action == MotionEvent.ACTION_MOVE ) {
                if ( radarGestureActive ) {
                    if ( event.getPointerCount() == 1 ) {
                        radarGestureActive = sendViewportRequest( event.getX(), event.getY() );
                    }
                    else {
                        radarGestureActive = false;
                    }
                    return true;
                }

                if ( pressedButton != null && !pressedButton.bounds.contains( event.getX(), event.getY() ) ) {
                    releasePressedButton();
                }
                return true;
            }

            if ( action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL ) {
                radarGestureActive = false;
                releasePressedButton();
                return true;
            }

            return true;
        }

        private boolean sendViewportRequest( final float x, final float y )
        {
            if ( radarBounds.isEmpty() || !viewportControlEnabled ) {
                return false;
            }

            final float normalizedX = Math.max( 0f, Math.min( 1f, ( x - radarBounds.left ) / radarBounds.width() ) );
            final float normalizedY = Math.max( 0f, Math.min( 1f, ( y - radarBounds.top ) / radarBounds.height() ) );
            return viewportSender.send( normalizedX, normalizedY );
        }

        private void changeSelectionPage( final int command )
        {
            final int pageCount = Math.max( 1, ( selectionEntries.size() + SELECTION_PAGE_SIZE - 1 ) / SELECTION_PAGE_SIZE );
            if ( command == LOCAL_PREVIOUS_PAGE && selectionPage > 0 ) {
                --selectionPage;
            }
            else if ( command == LOCAL_NEXT_PAGE && selectionPage + 1 < pageCount ) {
                ++selectionPage;
            }
            releasePressedButton();
            rebuildActions();
            layoutButtons( getWidth(), getHeight() );
            invalidate();
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
                addAction( "EDITOR", ACTION_MENU_EDITOR, KeyEvent.KEYCODE_E );
                addAction( "QUIT", KeyEvent.KEYCODE_Q );
                break;
            case CONTEXT_ADVENTURE_MAP:
                contextTitle = "ADVENTURE MAP";
                addAction( "NEXT HERO", ACTION_ADVENTURE_NEXT_HERO, KeyEvent.KEYCODE_H );
                addAction( "NEXT TOWN", ACTION_ADVENTURE_NEXT_TOWN, KeyEvent.KEYCODE_T );
                addAction( "HEROES", ACTION_ADVENTURE_OPEN_HERO_LIST, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "TOWNS", ACTION_ADVENTURE_OPEN_CASTLE_LIST, KeyEvent.KEYCODE_UNKNOWN );
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
            case CONTEXT_ADVENTURE_HERO_LIST:
                contextTitle = "SELECT HERO";
                addSelectionActions();
                break;
            case CONTEXT_ADVENTURE_CASTLE_LIST:
                contextTitle = "SELECT TOWN";
                addSelectionActions();
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
            case CONTEXT_NEW_GAME_MENU:
                contextTitle = "NEW GAME";
                addAction( "STANDARD", ACTION_MENU_STANDARD_GAME, KeyEvent.KEYCODE_S );
                addAction( "CAMPAIGN", ACTION_MENU_CAMPAIGN_GAME, KeyEvent.KEYCODE_C );
                addAction( "MULTIPLAYER", ACTION_MENU_MULTIPLAYER_GAME, KeyEvent.KEYCODE_M );
                addAction( "BATTLE ONLY", ACTION_MENU_BATTLE_ONLY, KeyEvent.KEYCODE_B );
                addAction( "SETTINGS", ACTION_MENU_SETTINGS, KeyEvent.KEYCODE_T );
                addAction( "BACK", ACTION_MENU_BACK, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_CAMPAIGN_MENU:
                contextTitle = "CAMPAIGN";
                addAction( "ORIGINAL", ACTION_MENU_ORIGINAL_CAMPAIGN, KeyEvent.KEYCODE_O );
                addAction( "EXPANSION", ACTION_MENU_EXPANSION_CAMPAIGN, KeyEvent.KEYCODE_E );
                addAction( "BACK", ACTION_MENU_BACK, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_MULTIPLAYER_MENU:
                contextTitle = "MULTIPLAYER";
                addAction( "HOT SEAT", ACTION_MENU_HOT_SEAT, KeyEvent.KEYCODE_H );
                addAction( "BACK", ACTION_MENU_BACK, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_HOT_SEAT_MENU:
                contextTitle = "HOT SEAT";
                addAction( "2 PLAYERS", ACTION_MENU_HOT_SEAT_2_PLAYERS, KeyEvent.KEYCODE_2 );
                addAction( "3 PLAYERS", ACTION_MENU_HOT_SEAT_3_PLAYERS, KeyEvent.KEYCODE_3 );
                addAction( "4 PLAYERS", ACTION_MENU_HOT_SEAT_4_PLAYERS, KeyEvent.KEYCODE_4 );
                addAction( "5 PLAYERS", ACTION_MENU_HOT_SEAT_5_PLAYERS, KeyEvent.KEYCODE_5 );
                addAction( "6 PLAYERS", ACTION_MENU_HOT_SEAT_6_PLAYERS, KeyEvent.KEYCODE_6 );
                addAction( "BACK", ACTION_MENU_BACK, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_LOAD_GAME_MENU:
                contextTitle = "LOAD GAME";
                addAction( "STANDARD", ACTION_MENU_LOAD_STANDARD, KeyEvent.KEYCODE_S );
                addAction( "CAMPAIGN", ACTION_MENU_LOAD_CAMPAIGN, KeyEvent.KEYCODE_C );
                addAction( "HOT SEAT", ACTION_MENU_LOAD_HOT_SEAT, KeyEvent.KEYCODE_H );
                addAction( "BACK", ACTION_MENU_BACK, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_SCENARIO_SETUP:
                contextTitle = "SCENARIO SETUP";
                addAction( "SELECT MAP", ACTION_SCENARIO_SELECT_MAP, KeyEvent.KEYCODE_M );
                addAction( "PREV PLAYER", ACTION_SCENARIO_PREVIOUS_PLAYER, KeyEvent.KEYCODE_DPAD_LEFT );
                addAction( "NEXT PLAYER", ACTION_SCENARIO_NEXT_PLAYER, KeyEvent.KEYCODE_DPAD_RIGHT );
                addAction( "HOT SEAT".equals( informationCategory ) ? "SELECT / SWAP" : "SET HUMAN", ACTION_SCENARIO_PLAYER_CONTROL,
                           KeyEvent.KEYCODE_UNKNOWN );
                addAction( "PREV FACTION", ACTION_SCENARIO_PREVIOUS_FACTION, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "NEXT FACTION", ACTION_SCENARIO_NEXT_FACTION, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "HANDICAP", ACTION_SCENARIO_HANDICAP, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "EASY", ACTION_SCENARIO_DIFFICULTY_EASY, KeyEvent.KEYCODE_1 );
                addAction( "NORMAL", ACTION_SCENARIO_DIFFICULTY_NORMAL, KeyEvent.KEYCODE_2 );
                addAction( "HARD", ACTION_SCENARIO_DIFFICULTY_HARD, KeyEvent.KEYCODE_3 );
                addAction( "EXPERT", ACTION_SCENARIO_DIFFICULTY_EXPERT, KeyEvent.KEYCODE_4 );
                addAction( "IMPOSSIBLE", ACTION_SCENARIO_DIFFICULTY_IMPOSSIBLE, KeyEvent.KEYCODE_5 );
                addAction( "START", ACTION_SCENARIO_START, KeyEvent.KEYCODE_ENTER );
                addAction( "BACK", ACTION_MENU_BACK, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_BATTLE_ONLY_SETUP:
                contextTitle = "BATTLE ONLY";
                addAction( "SELECT ATTACKER", ACTION_BATTLE_ONLY_SELECT_ATTACKER, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "SELECT DEFENDER", ACTION_BATTLE_ONLY_SELECT_DEFENDER, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "PREV TERRAIN", ACTION_BATTLE_ONLY_PREVIOUS_TERRAIN, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "NEXT TERRAIN", ACTION_BATTLE_ONLY_NEXT_TERRAIN, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "DEFENDER CONTROL", ACTION_BATTLE_ONLY_TOGGLE_DEFENDER_CONTROL, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "RESET", ACTION_BATTLE_ONLY_RESET, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "START", ACTION_BATTLE_ONLY_START, KeyEvent.KEYCODE_ENTER );
                addAction( "EXIT", ACTION_BATTLE_ONLY_EXIT, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_HIGH_SCORES_STANDARD:
                contextTitle = "HIGH SCORES — STANDARD";
                addAction( "CAMPAIGN", ACTION_HIGH_SCORES_VIEW_CAMPAIGN, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "EXIT", ACTION_HIGH_SCORES_EXIT, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_HIGH_SCORES_CAMPAIGN:
                contextTitle = "HIGH SCORES — CAMPAIGN";
                addAction( "STANDARD", ACTION_HIGH_SCORES_VIEW_STANDARD, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "EXIT", ACTION_HIGH_SCORES_EXIT, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_CAMPAIGN_INTRO:
                contextTitle = "CAMPAIGN INTRO PLAYING";
                break;
            case CONTEXT_SUCCESSION_WARS_CAMPAIGN:
                contextTitle = "ORIGINAL CAMPAIGN";
                addAction( "ROLAND", ACTION_CAMPAIGN_SELECT_ROLAND, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "ARCHIBALD", ACTION_CAMPAIGN_SELECT_ARCHIBALD, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "BACK", ACTION_MENU_BACK, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_PRICE_OF_LOYALTY_CAMPAIGN:
                contextTitle = "EXPANSION CAMPAIGNS";
                addAction( "PRICE OF LOYALTY", ACTION_CAMPAIGN_SELECT_PRICE_OF_LOYALTY, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "VOYAGE HOME", ACTION_CAMPAIGN_SELECT_VOYAGE_HOME, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "WIZARD'S ISLE", ACTION_CAMPAIGN_SELECT_WIZARDS_ISLE, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "DESCENDANTS", ACTION_CAMPAIGN_SELECT_DESCENDANTS, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "BACK", ACTION_MENU_BACK, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_GAME_SETTINGS:
                contextTitle = "GAME SETTINGS";
                addAction( "LANGUAGE", ACTION_GAME_SETTINGS_LANGUAGE, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "GRAPHICS", ACTION_GAME_SETTINGS_GRAPHICS, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "AUDIO", ACTION_GAME_SETTINGS_AUDIO, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "HOT KEYS", ACTION_GAME_SETTINGS_HOT_KEYS, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "CURSOR TYPE", ACTION_GAME_SETTINGS_CURSOR_TYPE, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "INTERFACE TYPE", ACTION_GAME_SETTINGS_INTERFACE_TYPE, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "TEXT SUPPORT", ACTION_GAME_SETTINGS_TEXT_SUPPORT, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "OKAY / BACK", ACTION_GAME_SETTINGS_CLOSE, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_EDITOR_MAIN_MENU:
                contextTitle = "MAP EDITOR";
                addAction( "NEW MAP", ACTION_EDITOR_NEW_MAP, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "LOAD MAP", ACTION_EDITOR_LOAD_MAP, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "MAIN MENU", ACTION_EDITOR_EXIT_TO_MAIN_MENU, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_EDITOR_NEW_MAP_MENU:
                contextTitle = "NEW MAP";
                addAction( "FROM SCRATCH", ACTION_EDITOR_FROM_SCRATCH, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "RANDOM", ACTION_EDITOR_RANDOM_MAP, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "BACK", ACTION_MENU_BACK, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_EDITOR_MAP_SIZE_SCRATCH:
                contextTitle = "FROM SCRATCH — MAP SIZE";
                addEditorMapSizeActions();
                break;
            case CONTEXT_EDITOR_MAP_SIZE_RANDOM:
                contextTitle = "RANDOM — MAP SIZE";
                addEditorMapSizeActions();
                break;
            case CONTEXT_EDITOR_INTERFACE:
                contextTitle = "MAP EDITOR";
                addAction( "EDITOR TOOLS", ACTION_EDITOR_OPEN_TOOLS, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "FILE OPTIONS", ACTION_EDITOR_OPEN_FILE_OPTIONS, KeyEvent.KEYCODE_F );
                addAction( "SYSTEM OPTIONS", ACTION_EDITOR_OPEN_SYSTEM_OPTIONS, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "MAP SPECIFICATIONS", ACTION_EDITOR_OPEN_MAP_SPECIFICATIONS, KeyEvent.KEYCODE_UNKNOWN );
                break;
            case CONTEXT_EDITOR_FILE_OPTIONS:
                contextTitle = "EDITOR — FILE OPTIONS";
                addAction( "NEW MAP", ACTION_EDITOR_FILE_NEW_MAP, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "LOAD MAP", ACTION_EDITOR_FILE_LOAD_MAP, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "START MAP", ACTION_EDITOR_FILE_START_MAP, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "SAVE MAP", ACTION_EDITOR_FILE_SAVE_MAP, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "MAIN MENU", ACTION_EDITOR_FILE_MAIN_MENU, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "QUIT", ACTION_EDITOR_FILE_QUIT, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "AUTO PLAYTEST", ACTION_EDITOR_FILE_AUTO_PLAYTEST, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "CANCEL", ACTION_EDITOR_FILE_CANCEL, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_EDITOR_SYSTEM_OPTIONS:
                contextTitle = "EDITOR — SYSTEM OPTIONS";
                addAction( "LANGUAGE", ACTION_EDITOR_SYSTEM_LANGUAGE, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "GRAPHICS", ACTION_EDITOR_SYSTEM_GRAPHICS, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "AUDIO", ACTION_EDITOR_SYSTEM_AUDIO, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "HOT KEYS", ACTION_EDITOR_SYSTEM_HOT_KEYS, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "ANIMATION", ACTION_EDITOR_SYSTEM_ANIMATION, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "PASSABILITY", ACTION_EDITOR_SYSTEM_PASSABILITY, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "INTERFACE TYPE", ACTION_EDITOR_SYSTEM_INTERFACE_TYPE, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "CURSOR TYPE", ACTION_EDITOR_SYSTEM_CURSOR_TYPE, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "SCROLL SPEED", ACTION_EDITOR_SYSTEM_SCROLL_SPEED, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "OKAY / BACK", ACTION_EDITOR_SYSTEM_CLOSE, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_EDITOR_MAP_SPECIFICATIONS:
                contextTitle = "EDITOR — MAP SPECIFICATIONS";
                addAction( "MAP NAME", ACTION_EDITOR_MAP_SPEC_NAME, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "DESCRIPTION", ACTION_EDITOR_MAP_SPEC_DESCRIPTION, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "PLAYER SETUP", ACTION_EDITOR_MAP_SPEC_PLAYERS, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "DIFFICULTY", ACTION_EDITOR_MAP_SPEC_DIFFICULTY, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "VICTORY", ACTION_EDITOR_MAP_SPEC_VICTORY, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "LOSS", ACTION_EDITOR_MAP_SPEC_LOSS, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "RUMORS", ACTION_EDITOR_MAP_SPEC_RUMORS, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "EVENTS", ACTION_EDITOR_MAP_SPEC_EVENTS, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "MAP LANGUAGE", ACTION_EDITOR_MAP_SPEC_LANGUAGE, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "CREATOR NOTES", ACTION_EDITOR_MAP_SPEC_ABOUT, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "OKAY", ACTION_EDITOR_MAP_SPEC_OKAY, KeyEvent.KEYCODE_ENTER );
                addAction( "BACK / CANCEL", ACTION_EDITOR_MAP_SPEC_CANCEL, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_EDITOR_MAP_SPEC_PLAYERS:
                contextTitle = "MAP SPECIFICATIONS — PLAYERS";
                addAction( "PREV PLAYER", ACTION_EDITOR_MAP_SPEC_PREVIOUS_PLAYER, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "NEXT PLAYER", ACTION_EDITOR_MAP_SPEC_NEXT_PLAYER, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "PLAYER TYPE", ACTION_EDITOR_MAP_SPEC_PLAYER_TYPE, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "BACK", ACTION_EDITOR_MAP_SPEC_SUBMENU_BACK, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_EDITOR_MAP_SPEC_VICTORY:
                contextTitle = "MAP SPECIFICATIONS — VICTORY";
                addAction( "PREV CONDITION", ACTION_EDITOR_MAP_SPEC_PREVIOUS_CONDITION, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "NEXT CONDITION", ACTION_EDITOR_MAP_SPEC_NEXT_CONDITION, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "SELECT TARGET", ACTION_EDITOR_MAP_SPEC_SELECT_TARGET, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "STANDARD VICTORY", ACTION_EDITOR_MAP_SPEC_TOGGLE_STANDARD_VICTORY, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "ALLOW AI", ACTION_EDITOR_MAP_SPEC_TOGGLE_AI_VICTORY, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "PREV ALLIANCE PLAYER", ACTION_EDITOR_MAP_SPEC_PREVIOUS_ALLIANCE_PLAYER, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "NEXT ALLIANCE PLAYER", ACTION_EDITOR_MAP_SPEC_NEXT_ALLIANCE_PLAYER, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "SWITCH ALLIANCE", ACTION_EDITOR_MAP_SPEC_SWITCH_ALLIANCE, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "LESS GOLD", ACTION_EDITOR_MAP_SPEC_DECREASE_VALUE, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "MORE GOLD", ACTION_EDITOR_MAP_SPEC_INCREASE_VALUE, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "BACK", ACTION_EDITOR_MAP_SPEC_SUBMENU_BACK, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_EDITOR_MAP_SPEC_LOSS:
                contextTitle = "MAP SPECIFICATIONS — LOSS";
                addAction( "PREV CONDITION", ACTION_EDITOR_MAP_SPEC_PREVIOUS_CONDITION, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "NEXT CONDITION", ACTION_EDITOR_MAP_SPEC_NEXT_CONDITION, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "SELECT TARGET", ACTION_EDITOR_MAP_SPEC_SELECT_TARGET, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "LESS TIME", ACTION_EDITOR_MAP_SPEC_DECREASE_VALUE, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "MORE TIME", ACTION_EDITOR_MAP_SPEC_INCREASE_VALUE, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "BACK", ACTION_EDITOR_MAP_SPEC_SUBMENU_BACK, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_EDITOR_TOOLS:
                contextTitle = "EDITOR TOOLS";
                addAction( "TERRAIN", ACTION_EDITOR_TOOL_TERRAIN, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "LANDSCAPE", ACTION_EDITOR_TOOL_LANDSCAPE, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "DETAIL", ACTION_EDITOR_TOOL_DETAIL, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "ADVENTURE", ACTION_EDITOR_TOOL_ADVENTURE, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "KINGDOM", ACTION_EDITOR_TOOL_KINGDOM, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "MONSTERS", ACTION_EDITOR_TOOL_MONSTERS, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "STREAMS", ACTION_EDITOR_TOOL_STREAMS, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "ROADS", ACTION_EDITOR_TOOL_ROADS, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "ERASE", ACTION_EDITOR_TOOL_ERASE, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "MAGNIFY", ACTION_EDITOR_TOOL_MAGNIFY, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "UNDO", ACTION_EDITOR_TOOL_UNDO, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "REDO", ACTION_EDITOR_TOOL_REDO, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "BACK", ACTION_EDITOR_TOOL_BACK, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_EDITOR_TOOL_TERRAIN:
                contextTitle = "TOOLS — TERRAIN";
                addAction( "WATER", ACTION_EDITOR_TERRAIN_WATER, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "GRASS", ACTION_EDITOR_TERRAIN_GRASS, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "SNOW", ACTION_EDITOR_TERRAIN_SNOW, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "SWAMP", ACTION_EDITOR_TERRAIN_SWAMP, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "LAVA", ACTION_EDITOR_TERRAIN_LAVA, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "DESERT", ACTION_EDITOR_TERRAIN_DESERT, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "DIRT", ACTION_EDITOR_TERRAIN_DIRT, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "WASTELAND", ACTION_EDITOR_TERRAIN_WASTELAND, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "BEACH", ACTION_EDITOR_TERRAIN_BEACH, KeyEvent.KEYCODE_UNKNOWN );
                addBrushActions();
                addAction( "BACK", ACTION_EDITOR_TOOL_BACK, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_EDITOR_TOOL_LANDSCAPE:
                contextTitle = "TOOLS — LANDSCAPE";
                addAction( "MOUNTAINS", ACTION_EDITOR_LANDSCAPE_MOUNTAINS, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "ROCKS", ACTION_EDITOR_LANDSCAPE_ROCKS, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "TREES", ACTION_EDITOR_LANDSCAPE_TREES, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "WATER OBJECTS", ACTION_EDITOR_LANDSCAPE_WATER, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "MISCELLANEOUS", ACTION_EDITOR_LANDSCAPE_MISC, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "BACK", ACTION_EDITOR_TOOL_BACK, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_EDITOR_TOOL_DETAIL:
                contextTitle = "TOOLS — DETAIL";
                addAction( "EDIT", ACTION_EDITOR_DETAIL_EDIT, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "MOVE", ACTION_EDITOR_DETAIL_MOVE, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "COPY", ACTION_EDITOR_DETAIL_COPY, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "BACK", ACTION_EDITOR_TOOL_BACK, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_EDITOR_TOOL_ADVENTURE:
                contextTitle = "TOOLS — ADVENTURE";
                addAction( "ARTIFACTS", ACTION_EDITOR_ADVENTURE_ARTIFACTS, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "DWELLINGS", ACTION_EDITOR_ADVENTURE_DWELLINGS, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "MINES", ACTION_EDITOR_ADVENTURE_MINES, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "POWER-UPS", ACTION_EDITOR_ADVENTURE_POWER_UPS, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "TREASURES", ACTION_EDITOR_ADVENTURE_TREASURES, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "WATER OBJECTS", ACTION_EDITOR_ADVENTURE_WATER, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "MISCELLANEOUS", ACTION_EDITOR_ADVENTURE_MISC, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "BACK", ACTION_EDITOR_TOOL_BACK, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_EDITOR_TOOL_KINGDOM:
                contextTitle = "TOOLS — KINGDOM";
                addAction( "HEROES", ACTION_EDITOR_KINGDOM_HEROES, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "TOWNS", ACTION_EDITOR_KINGDOM_TOWNS, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "BACK", ACTION_EDITOR_TOOL_BACK, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_EDITOR_TOOL_MONSTERS:
                contextTitle = "TOOLS — MONSTERS";
                addAction( "SELECT MONSTER", ACTION_EDITOR_MONSTER_SELECT, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "BACK", ACTION_EDITOR_TOOL_BACK, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_EDITOR_TOOL_STREAMS:
                contextTitle = "TOOLS — STREAMS";
                addAction( "BACK", ACTION_EDITOR_TOOL_BACK, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_EDITOR_TOOL_ROADS:
                contextTitle = "TOOLS — ROADS";
                addAction( "BACK", ACTION_EDITOR_TOOL_BACK, KeyEvent.KEYCODE_ESCAPE );
                break;
            case CONTEXT_EDITOR_TOOL_ERASE:
                contextTitle = "TOOLS — ERASE";
                addAction( "MOUNTAINS", ACTION_EDITOR_ERASE_MOUNTAINS, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "ROCKS", ACTION_EDITOR_ERASE_ROCKS, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "TREES", ACTION_EDITOR_ERASE_TREES, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "LANDSCAPE", ACTION_EDITOR_ERASE_LANDSCAPE, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "NON-PICKABLE", ACTION_EDITOR_ERASE_ADVENTURE_NON_PICKABLE, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "TOWNS", ACTION_EDITOR_ERASE_TOWNS, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "PICKABLE", ACTION_EDITOR_ERASE_ADVENTURE_PICKABLE, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "MONSTERS", ACTION_EDITOR_ERASE_MONSTERS, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "HEROES", ACTION_EDITOR_ERASE_HEROES, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "ROADS", ACTION_EDITOR_ERASE_ROADS, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "STREAMS", ACTION_EDITOR_ERASE_STREAMS, KeyEvent.KEYCODE_UNKNOWN );
                addBrushActions();
                addAction( "BACK", ACTION_EDITOR_TOOL_BACK, KeyEvent.KEYCODE_ESCAPE );
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

        private boolean hasRadarSnapshot()
        {
            return ( gameContext == CONTEXT_ADVENTURE_MAP || gameContext == CONTEXT_EDITOR_INTERFACE ) && radarContext == gameContext && radarBitmap != null
                   && radarWorldWidth > 0 && radarWorldHeight > 0;
        }

        private void drawRadar( final Canvas canvas )
        {
            paint.setStyle( Paint.Style.FILL );
            paint.setColor( Color.BLACK );
            canvas.drawRect( radarBounds, paint );
            paint.setFilterBitmap( false );
            canvas.drawBitmap( radarBitmap, null, radarBounds, paint );

            final float viewportLeft = Math.max( 0, radarViewportX ) / (float)radarWorldWidth;
            final float viewportTop = Math.max( 0, radarViewportY ) / (float)radarWorldHeight;
            final float viewportRight = Math.min( radarWorldWidth, radarViewportX + radarViewportWidth ) / (float)radarWorldWidth;
            final float viewportBottom = Math.min( radarWorldHeight, radarViewportY + radarViewportHeight ) / (float)radarWorldHeight;
            final RectF viewport = new RectF( radarBounds.left + viewportLeft * radarBounds.width(), radarBounds.top + viewportTop * radarBounds.height(),
                                              radarBounds.left + viewportRight * radarBounds.width(), radarBounds.top + viewportBottom * radarBounds.height() );

            paint.setStyle( Paint.Style.STROKE );
            paint.setStrokeWidth( 4f );
            paint.setColor( Color.WHITE );
            canvas.drawRect( viewport, paint );
            paint.setStrokeWidth( 3f );
            paint.setColor( GOLD_LIGHT_COLOR );
            canvas.drawRect( radarBounds, paint );
            paint.setStyle( Paint.Style.FILL );
        }

        private void addBrushActions()
        {
            addAction( "1 × 1", ACTION_EDITOR_BRUSH_SMALL, KeyEvent.KEYCODE_UNKNOWN );
            addAction( "2 × 2", ACTION_EDITOR_BRUSH_MEDIUM, KeyEvent.KEYCODE_UNKNOWN );
            addAction( "4 × 4", ACTION_EDITOR_BRUSH_LARGE, KeyEvent.KEYCODE_UNKNOWN );
            addAction( "AREA", ACTION_EDITOR_BRUSH_AREA, KeyEvent.KEYCODE_UNKNOWN );
        }

        private void addEditorMapSizeActions()
        {
            addAction( "SMALL", ACTION_EDITOR_MAP_SIZE_SMALL, KeyEvent.KEYCODE_UNKNOWN );
            addAction( "MEDIUM", ACTION_EDITOR_MAP_SIZE_MEDIUM, KeyEvent.KEYCODE_UNKNOWN );
            addAction( "LARGE", ACTION_EDITOR_MAP_SIZE_LARGE, KeyEvent.KEYCODE_UNKNOWN );
            addAction( "EXTRA LARGE", ACTION_EDITOR_MAP_SIZE_EXTRA_LARGE, KeyEvent.KEYCODE_UNKNOWN );
            addAction( "BACK", ACTION_MENU_BACK, KeyEvent.KEYCODE_ESCAPE );
        }

        private void addSelectionActions()
        {
            if ( selectionContext == gameContext ) {
                final int firstIndex = selectionPage * SELECTION_PAGE_SIZE;
                final int lastIndex = Math.min( selectionEntries.size(), firstIndex + SELECTION_PAGE_SIZE );
                for ( int index = firstIndex; index < lastIndex; ++index ) {
                    final SelectionEntry entry = selectionEntries.get( index );
                    buttons.add( new CommandButton( entry.name, entry.detail, entry.id, entry.selected ) );
                }
            }

            final int pageCount = Math.max( 1, ( selectionEntries.size() + SELECTION_PAGE_SIZE - 1 ) / SELECTION_PAGE_SIZE );
            if ( pageCount > 1 ) {
                buttons.add( new CommandButton( "PREVIOUS PAGE", LOCAL_PREVIOUS_PAGE ) );
                buttons.add( new CommandButton( "NEXT PAGE", LOCAL_NEXT_PAGE ) );
            }
            addAction( "BACK", ACTION_ADVENTURE_SELECTION_BACK, KeyEvent.KEYCODE_ESCAPE );
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
            final boolean selectionList = gameContext == CONTEXT_ADVENTURE_HERO_LIST || gameContext == CONTEXT_ADVENTURE_CASTLE_LIST;
            final int columns = selectionList ? 2 : ( buttons.size() <= 2 || buttons.size() == 4 ? 2 : ( buttons.size() <= 6 ? 3 : 4 ) );
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
            if ( button.localCommand == LOCAL_PREVIOUS_PAGE ) {
                return selectionPage > 0;
            }
            if ( button.localCommand == LOCAL_NEXT_PAGE ) {
                return ( selectionPage + 1 ) * SELECTION_PAGE_SIZE < selectionEntries.size();
            }
            if ( button.selectionId >= 0 ) {
                return selectionContext == gameContext;
            }
            return button.action == ACTION_NONE || ( enabledActions & actionMask( button.action ) ) != 0;
        }

        private static long actionMask( final int action )
        {
            if ( action <= 0 ) {
                return 0;
            }

            final int usableBitCount = 63;
            final int bit = action <= usableBitCount ? action : ( ( action - 1 ) % usableBitCount ) + 1;
            return 1L << bit;
        }

        void releasePressedButton()
        {
            radarGestureActive = false;
            if ( pressedButton != null ) {
                if ( !pressedButton.sentSemantically && pressedButton.keyCode != KeyEvent.KEYCODE_UNKNOWN ) {
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
        final String detail;
        final int action;
        final int keyCode;
        final int selectionId;
        final int localCommand;
        final boolean selected;
        final RectF bounds = new RectF();
        boolean sentSemantically;

        CommandButton( final String label, final int action, final int keyCode )
        {
            this( label, "", action, keyCode, -1, 0, false );
        }

        CommandButton( final String label, final String detail, final int selectionId, final boolean selected )
        {
            this( label, detail, ACTION_NONE, KeyEvent.KEYCODE_UNKNOWN, selectionId, 0, selected );
        }

        CommandButton( final String label, final int localCommand )
        {
            this( label, "", ACTION_NONE, KeyEvent.KEYCODE_UNKNOWN, -1, localCommand, false );
        }

        private CommandButton( final String label, final String detail, final int action, final int keyCode, final int selectionId, final int localCommand,
                               final boolean selected )
        {
            this.label = label;
            this.detail = detail;
            this.action = action;
            this.keyCode = keyCode;
            this.selectionId = selectionId;
            this.localCommand = localCommand;
            this.selected = selected;
        }
    }

    private static final class SelectionEntry
    {
        final int id;
        final String name;
        final String detail;
        final boolean selected;

        SelectionEntry( final int id, final String name, final String detail, final boolean selected )
        {
            this.id = id;
            this.name = name;
            this.detail = detail;
            this.selected = selected;
        }
    }
}
