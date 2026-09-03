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
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
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
    private static final int CONTEXT_ADVENTURE_MAP_OVERVIEW = 43;
    private static final int CONTEXT_MENU_FALLBACK = 44;
    private static final int CONTEXT_HERO_MEETING = 45;

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
    private static final int ACTION_ADVENTURE_OPEN_MAP_OVERVIEW = 214;
    private static final int ACTION_ADVENTURE_OVERVIEW_OPEN_HERO_LIST = 215;
    private static final int ACTION_ADVENTURE_OVERVIEW_OPEN_CASTLE_LIST = 216;
    private static final int ACTION_ADVENTURE_OVERVIEW_BACK = 217;
    private static final int ACTION_HERO_MEETING_TRANSFER_TO_RIGHT = 218;
    private static final int ACTION_HERO_MEETING_TRANSFER_TO_LEFT = 219;
    private static final int ACTION_HERO_MEETING_SWAP_ARMIES = 220;
    private static final int ACTION_HERO_MEETING_CLOSE = 221;

    private static final int SELECTION_KIND_HERO = 1;
    private static final int SELECTION_KIND_CASTLE = 2;
    private static final int SELECTION_RELATIONSHIP_OWNED = 1;
    private static final int SELECTION_RELATIONSHIP_ALLIED = 2;
    private static final int SELECTION_RELATIONSHIP_ENEMY = 3;
    private static final int SELECTION_RELATIONSHIP_NEUTRAL = 4;

    private static final int SELECTION_PAGE_SIZE = 8;
    private static final int LOCAL_PREVIOUS_PAGE = 1;
    private static final int LOCAL_NEXT_PAGE = 2;
    private static final int LOCAL_CYCLE_OVERVIEW_KIND_FILTER = 3;
    private static final int LOCAL_CYCLE_OVERVIEW_RELATIONSHIP_FILTER = 4;

    private static final int OVERVIEW_KIND_FILTER_BOTH = 0;
    private static final int OVERVIEW_RELATIONSHIP_FILTER_ALL = 0;

    private static final int OVERVIEW_ZOOM_LEVEL_COUNT = 3;
    private static final float OVERVIEW_ZOOM_IN_THRESHOLD = 1.25f;
    private static final float OVERVIEW_ZOOM_OUT_THRESHOLD = 0.8f;
    private static final float OVERVIEW_CLUSTER_DISTANCE_1X = 64f;
    private static final float OVERVIEW_CLUSTER_DISTANCE_2X = 48f;
    private static final float OVERVIEW_MARKER_HIT_RADIUS = 44f;
    private static final float OVERVIEW_LABEL_MAX_WIDTH_2X = 160f;
    private static final float OVERVIEW_LABEL_MAX_WIDTH_4X = 220f;
    private static final float OVERVIEW_LABEL_HEIGHT = 36f;
    private static final float OVERVIEW_LABEL_TEXT_SIZE = 20f;
    private static final float OVERVIEW_LABEL_HORIZONTAL_PADDING = 10f;
    private static final float OVERVIEW_LABEL_MARKER_GAP = 8f;
    private static final float OVERVIEW_LABEL_COLLISION_GAP = 4f;

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
        boolean send( int context, long revision, int kind, int id );
    }

    interface MarkerInfoSender
    {
        boolean send( int context, long revision, int kind, int id );
    }

    interface TroopMoveSender
    {
        boolean send( int context, long revision, int sourceSide, int sourceSlot, int destinationSide, int destinationSlot );
    }

    private final KeySender keySender;
    private final ActionSender actionSender;
    private final ViewportSender viewportSender;
    private final SelectionSender selectionSender;
    private final MarkerInfoSender markerInfoSender;
    private final TroopMoveSender troopMoveSender;
    private CommandDeckView commandDeckView;

    ThorSecondScreenPresentation( final Context context, final Display display, final KeySender keySender, final ActionSender actionSender,
                                  final ViewportSender viewportSender, final SelectionSender selectionSender, final MarkerInfoSender markerInfoSender,
                                  final TroopMoveSender troopMoveSender )
    {
        super( context, display );
        this.keySender = keySender;
        this.actionSender = actionSender;
        this.viewportSender = viewportSender;
        this.selectionSender = selectionSender;
        this.markerInfoSender = markerInfoSender;
        this.troopMoveSender = troopMoveSender;
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

        commandDeckView
            = new CommandDeckView( getContext(), keySender, actionSender, viewportSender, selectionSender, markerInfoSender, troopMoveSender );
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
                       final int[] radarSnapshot, final int[] visualSnapshot, final String[] selectionSnapshot, final String[] troopSnapshot,
                       final int[] troopVisualSnapshot )
    {
        if ( commandDeckView != null ) {
            commandDeckView.setGameState( context, enabledActions, informationSnapshot, viewportControlEnabled, radarSnapshot, visualSnapshot, selectionSnapshot,
                                          troopSnapshot, troopVisualSnapshot );
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
        private final MarkerInfoSender markerInfoSender;
        private final TroopMoveSender troopMoveSender;
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
        private Bitmap visualBitmap;
        private int visualContext = -1;
        private long visualRevision = -1;
        private boolean viewportControlEnabled;
        private boolean radarGestureActive;
        private boolean radarGestureMoved;
        private float radarDownX;
        private float radarDownY;
        private OverviewMapItem radarTapItem;
        private boolean radarLongPressTriggered;
        private int selectionContext = -1;
        private long selectionRevision = -1;
        private final List<SelectionEntry> selectionEntries = new ArrayList<>();
        private int selectionPage;
        private int troopContext = -1;
        private long troopRevision = -1;
        private String leftHeroName = "";
        private String rightHeroName = "";
        private int upperSelectedSide = -1;
        private int upperSelectedSlot = -1;
        private final List<TroopSlot> troopSlots = new ArrayList<>();
        private int troopVisualContext = -1;
        private long troopVisualRevision = -1;
        private final List<Bitmap> troopBitmaps = new ArrayList<>();
        private final RectF[] troopSlotBounds = new RectF[10];
        private int selectedTroopSide = -1;
        private int selectedTroopSlot = -1;
        private int pressedTroopIndex = -1;
        private final int troopTouchSlop;
        private float troopTouchDownX;
        private float troopTouchDownY;
        private boolean troopDragActive;
        private int troopDragDestinationIndex = -1;
        private float troopDragX;
        private float troopDragY;
        private int overviewKindFilter = OVERVIEW_KIND_FILTER_BOTH;
        private int overviewRelationshipFilter = OVERVIEW_RELATIONSHIP_FILTER_ALL;
        private int overviewZoomLevel;
        private float overviewZoomCenterX = 0.5f;
        private float overviewZoomCenterY = 0.5f;
        private int overviewZoomWorldWidth;
        private int overviewZoomWorldHeight;
        private boolean radarZoomGestureActive;
        private float radarZoomPreviousMidpointX;
        private float radarZoomPreviousMidpointY;
        private float radarZoomReferenceDistance;
        private final Runnable radarLongPress;

        CommandDeckView( final Context context, final KeySender keySender, final ActionSender actionSender, final ViewportSender viewportSender,
                         final SelectionSender selectionSender, final MarkerInfoSender markerInfoSender, final TroopMoveSender troopMoveSender )
        {
            super( context );
            this.keySender = keySender;
            this.actionSender = actionSender;
            this.viewportSender = viewportSender;
            this.selectionSender = selectionSender;
            this.markerInfoSender = markerInfoSender;
            this.troopMoveSender = troopMoveSender;
            troopTouchSlop = ViewConfiguration.get( context ).getScaledTouchSlop();
            for ( int index = 0; index < troopSlotBounds.length; ++index ) {
                troopSlotBounds[index] = new RectF();
            }
            radarLongPress = () -> {
                if ( radarGestureActive && !radarGestureMoved && gameContext == CONTEXT_ADVENTURE_MAP_OVERVIEW && radarTapItem != null
                     && selectionContext == gameContext ) {
                    radarLongPressTriggered = true;
                    if ( !radarTapItem.isCluster() ) {
                        this.markerInfoSender.send( gameContext, selectionRevision, radarTapItem.entry.kind, radarTapItem.entry.id );
                    }
                }
            };
            setBackgroundColor( BACKGROUND_COLOR );
            setFocusable( true );
            setGameState( CONTEXT_FALLBACK, -1L, null, false, null, null, null, null, null );
        }

        void setGameState( final int requestedContext, final long requestedEnabledActions, final String[] requestedInformationSnapshot,
                           final boolean requestedViewportControlEnabled, final int[] requestedRadarSnapshot, final int[] requestedVisualSnapshot,
                           final String[] requestedSelectionSnapshot, final String[] requestedTroopSnapshot, final int[] requestedTroopVisualSnapshot )
        {
            final int context
                = requestedContext >= CONTEXT_FALLBACK && requestedContext <= CONTEXT_HERO_MEETING ? requestedContext : CONTEXT_FALLBACK;
            final boolean informationChanged = applyInformationSnapshot( requestedInformationSnapshot );
            final boolean radarChanged = applyRadarSnapshot( requestedRadarSnapshot );
            final boolean visualChanged = applyVisualSnapshot( requestedVisualSnapshot );
            final boolean selectionChanged = applySelectionSnapshot( requestedSelectionSnapshot );
            final boolean troopChanged = applyTroopSnapshot( requestedTroopSnapshot );
            final boolean troopVisualChanged = applyTroopVisualSnapshot( requestedTroopVisualSnapshot );
            if ( gameContext == context && enabledActions == requestedEnabledActions && viewportControlEnabled == requestedViewportControlEnabled
                 && !informationChanged && !radarChanged && !visualChanged && !selectionChanged && !troopChanged && !troopVisualChanged ) {
                return;
            }

            final boolean contextChanged = gameContext != context;
            if ( contextChanged || selectionChanged || troopChanged || enabledActions != requestedEnabledActions
                 || ( viewportControlEnabled && !requestedViewportControlEnabled ) ) {
                releasePressedButton();
            }
            gameContext = context;
            enabledActions = requestedEnabledActions;
            viewportControlEnabled = requestedViewportControlEnabled;
            if ( !viewportControlEnabled ) {
                radarGestureActive = false;
            }
            if ( contextChanged || selectionChanged || troopChanged
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

            final int worldWidth = snapshot[5];
            final int worldHeight = snapshot[6];
            if ( worldWidth != overviewZoomWorldWidth || worldHeight != overviewZoomWorldHeight ) {
                releasePressedButton();
                resetOverviewZoom( worldWidth, worldHeight );
            }
            radarWorldWidth = worldWidth;
            radarWorldHeight = worldHeight;
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

        private boolean applyVisualSnapshot( final int[] snapshot )
        {
            if ( snapshot == null || snapshot.length < 6 || snapshot[0] != 1 || snapshot[2] == visualRevision ) {
                return false;
            }

            visualRevision = snapshot[2];
            visualContext = snapshot[1];
            final int width = snapshot[3];
            final int height = snapshot[4];
            final int pixelCount = snapshot[5];
            if ( width <= 0 || height <= 0 || width > 256 || height > 256 || pixelCount != width * height || snapshot.length != 6 + pixelCount ) {
                visualBitmap = null;
                return true;
            }

            visualBitmap = Bitmap.createBitmap( snapshot, 6, width, width, height, Bitmap.Config.ARGB_8888 );
            return true;
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
                if ( version != 3 || revision == selectionRevision || count < 0 || snapshot.length != 4 + count * 9 ) {
                    return false;
                }

                final List<SelectionEntry> entries = new ArrayList<>( count );
                int selectedIndex = -1;
                for ( int index = 0; index < count; ++index ) {
                    final int offset = 4 + index * 9;
                    final int id = Integer.parseInt( snapshot[offset] );
                    final boolean selected = "1".equals( snapshot[offset + 3] );
                    entries.add( new SelectionEntry( id, snapshot[offset + 1] == null ? "" : snapshot[offset + 1],
                                                     snapshot[offset + 2] == null ? "" : snapshot[offset + 2], selected,
                                                     Integer.parseInt( snapshot[offset + 4] ), Integer.parseInt( snapshot[offset + 5] ),
                                                     Integer.parseInt( snapshot[offset + 6] ), Integer.parseInt( snapshot[offset + 7] ),
                                                     "1".equals( snapshot[offset + 8] ) ) );
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

        private boolean applyTroopSnapshot( final String[] snapshot )
        {
            if ( snapshot == null || snapshot.length < 8 ) {
                return false;
            }

            try {
                final int version = Integer.parseInt( snapshot[0] );
                final int context = Integer.parseInt( snapshot[1] );
                final long revision = Long.parseLong( snapshot[2] );
                final int count = Integer.parseInt( snapshot[7] );
                if ( version != 1 || revision == troopRevision || ( count != 0 && count != 10 ) || snapshot.length != 8 + count * 3 ) {
                    return false;
                }

                final List<TroopSlot> slots = new ArrayList<>( count );
                for ( int index = 0; index < count; ++index ) {
                    final int offset = 8 + index * 3;
                    slots.add( new TroopSlot( Integer.parseInt( snapshot[offset] ), snapshot[offset + 1] == null ? "" : snapshot[offset + 1],
                                              Long.parseLong( snapshot[offset + 2] ) ) );
                }

                troopContext = context;
                troopRevision = revision;
                leftHeroName = snapshot[3] == null ? "" : snapshot[3];
                rightHeroName = snapshot[4] == null ? "" : snapshot[4];
                upperSelectedSide = Integer.parseInt( snapshot[5] );
                upperSelectedSlot = Integer.parseInt( snapshot[6] );
                troopSlots.clear();
                troopSlots.addAll( slots );
                clearTroopSelection();
                return true;
            }
            catch ( final NumberFormatException ex ) {
                return false;
            }
        }

        private boolean applyTroopVisualSnapshot( final int[] snapshot )
        {
            if ( snapshot == null || snapshot.length < 4 || snapshot[0] != 1 || snapshot[2] == troopVisualRevision ) {
                return false;
            }

            final int count = snapshot[3];
            if ( count != 0 && count != 10 ) {
                return false;
            }

            final List<Bitmap> bitmaps = new ArrayList<>( count );
            int offset = 4;
            for ( int index = 0; index < count; ++index ) {
                if ( offset + 3 > snapshot.length ) {
                    return false;
                }
                final int width = snapshot[offset++];
                final int height = snapshot[offset++];
                final int pixelCount = snapshot[offset++];
                if ( width < 0 || height < 0 || width > 64 || height > 64 || pixelCount != width * height || offset + pixelCount > snapshot.length ) {
                    return false;
                }
                bitmaps.add( pixelCount == 0 ? null : Bitmap.createBitmap( snapshot, offset, width, width, height, Bitmap.Config.ARGB_8888 ) );
                offset += pixelCount;
            }
            if ( offset != snapshot.length ) {
                return false;
            }

            troopVisualContext = snapshot[1];
            troopVisualRevision = snapshot[2];
            troopBitmaps.clear();
            troopBitmaps.addAll( bitmaps );
            return true;
        }

        @Override
        protected void onSizeChanged( final int width, final int height, final int oldWidth, final int oldHeight )
        {
            super.onSizeChanged( width, height, oldWidth, oldHeight );
            layoutButtons( width, height );
            layoutTroopSlots( width, height );
        }

        @Override
        protected void onDraw( final Canvas canvas )
        {
            super.onDraw( canvas );
            drawStoneBackground( canvas );
            if ( gameContext == CONTEXT_ADVENTURE_MAP_OVERVIEW ) {
                drawExpandedMap( canvas );
            }
            else {
                drawReservedInformationPanel( canvas );
            }

            if ( gameContext == CONTEXT_HERO_MEETING ) {
                drawTroopDeck( canvas );
            }

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

            if ( hasHeroPortraitSnapshot() ) {
                final float maximumHeight = innerPanel.height() - 16f;
                final float maximumWidth = innerPanel.width() * 0.2f;
                final float scale = Math.min( maximumWidth / visualBitmap.getWidth(), maximumHeight / visualBitmap.getHeight() );
                final float portraitWidth = visualBitmap.getWidth() * scale;
                final float portraitHeight = visualBitmap.getHeight() * scale;
                final RectF portraitBounds = new RectF( innerPanel.left + 8f, innerPanel.centerY() - portraitHeight * 0.5f,
                                                        innerPanel.left + 8f + portraitWidth, innerPanel.centerY() + portraitHeight * 0.5f );
                drawHeroPortrait( canvas, portraitBounds );
                informationPanel.left = portraitBounds.right + 20f;
            }

            if ( ( gameContext == CONTEXT_ADVENTURE_MAP || gameContext == CONTEXT_HERO || gameContext == CONTEXT_CASTLE || gameContext == CONTEXT_BATTLE
                   || gameContext == CONTEXT_HERO_MEETING
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

        private void drawTroopDeck( final Canvas canvas )
        {
            if ( troopContext != CONTEXT_HERO_MEETING || troopSlots.size() != 10 ) {
                return;
            }

            drawTroopRow( canvas, 0, leftHeroName );
            drawTroopRow( canvas, 1, rightHeroName );
            drawTroopDragPreview( canvas );
        }

        private void drawTroopRow( final Canvas canvas, final int side, final String heroName )
        {
            final int firstIndex = side * 5;
            final RectF firstBounds = troopSlotBounds[firstIndex];
            paint.setStyle( Paint.Style.FILL );
            paint.setTypeface( Typeface.create( Typeface.SERIF, Typeface.BOLD ) );
            paint.setTextAlign( Paint.Align.LEFT );
            paint.setColor( side == 0 ? GOLD_LIGHT_COLOR : TEXT_COLOR );
            drawFittedText( canvas, ( side == 0 ? "LEFT  " : "RIGHT  " ) + heroName, firstBounds.left, firstBounds.top - 10f,
                            getWidth() - 2f * getMargin(), 25f );

            for ( int slot = 0; slot < 5; ++slot ) {
                final int index = firstIndex + slot;
                final TroopSlot troop = troopSlots.get( index );
                final RectF bounds = troopSlotBounds[index];
                final boolean selected = selectedTroopSide == side && selectedTroopSlot == slot;
                final boolean pressed = pressedTroopIndex == index;
                final boolean dragSource = troopDragActive && pressed;
                final boolean dragDestination = troopDragActive && troopDragDestinationIndex == index;

                paint.setStyle( Paint.Style.FILL );
                paint.setColor( selected || pressed || dragDestination ? BUTTON_PRESSED_COLOR : PANEL_INNER_COLOR );
                canvas.drawRoundRect( bounds, 13f, 13f, paint );
                paint.setStyle( Paint.Style.STROKE );
                paint.setStrokeWidth( selected || dragSource || dragDestination ? 6f : 3f );
                paint.setColor( selected || dragSource || dragDestination ? GOLD_LIGHT_COLOR : GOLD_COLOR );
                canvas.drawRoundRect( bounds, 13f, 13f, paint );

                if ( troop.isOccupied() ) {
                    final Bitmap bitmap = troopVisualContext == troopContext && troopVisualRevision == troopRevision && troopBitmaps.size() == 10
                                              ? troopBitmaps.get( index )
                                              : null;
                    if ( bitmap != null ) {
                        final float maximumImageWidth = Math.min( bounds.width() - 18f, 62f );
                        final float maximumImageHeight = Math.min( bounds.height() * 0.48f, 62f );
                        final RectF imageBounds = fitTroopBitmap( bitmap, bounds.centerX(), bounds.top + 9f, maximumImageWidth, maximumImageHeight );
                        paint.setFilterBitmap( false );
                        canvas.drawBitmap( bitmap, null, imageBounds, paint );
                    }

                    paint.setStyle( Paint.Style.FILL );
                    paint.setTypeface( Typeface.create( Typeface.SERIF, Typeface.BOLD ) );
                    paint.setTextAlign( Paint.Align.CENTER );
                    paint.setColor( TEXT_COLOR );
                    drawFittedText( canvas, troop.name, bounds.centerX(), bounds.bottom - 37f, bounds.width() - 18f, 20f );
                    paint.setColor( GOLD_LIGHT_COLOR );
                    drawFittedText( canvas, Long.toString( troop.count ), bounds.centerX(), bounds.bottom - 13f, bounds.width() - 18f, 22f );
                }
                else {
                    paint.setStyle( Paint.Style.FILL );
                    paint.setTypeface( Typeface.create( Typeface.SERIF, Typeface.NORMAL ) );
                    paint.setTextAlign( Paint.Align.CENTER );
                    paint.setColor( MUTED_TEXT_COLOR );
                    paint.setTextSize( 20f );
                    canvas.drawText( "EMPTY", bounds.centerX(), bounds.centerY() - ( paint.ascent() + paint.descent() ) * 0.5f, paint );
                }
            }
        }

        private void drawTroopDragPreview( final Canvas canvas )
        {
            if ( !troopDragActive || pressedTroopIndex < 0 || pressedTroopIndex >= troopSlots.size() ) {
                return;
            }

            final float previewWidth = 112f;
            final float previewHeight = 104f;
            final float centerX = Math.max( previewWidth * 0.5f, Math.min( getWidth() - previewWidth * 0.5f, troopDragX ) );
            final float centerY = Math.max( previewHeight * 0.5f, Math.min( getHeight() - previewHeight * 0.5f, troopDragY - 72f ) );
            final RectF previewBounds
                = new RectF( centerX - previewWidth * 0.5f, centerY - previewHeight * 0.5f, centerX + previewWidth * 0.5f, centerY + previewHeight * 0.5f );

            paint.setStyle( Paint.Style.FILL );
            paint.setColor( BUTTON_PRESSED_COLOR );
            paint.setAlpha( 225 );
            canvas.drawRoundRect( previewBounds, 13f, 13f, paint );
            paint.setStyle( Paint.Style.STROKE );
            paint.setStrokeWidth( 5f );
            paint.setColor( GOLD_LIGHT_COLOR );
            paint.setAlpha( 225 );
            canvas.drawRoundRect( previewBounds, 13f, 13f, paint );

            final Bitmap bitmap = troopVisualContext == troopContext && troopVisualRevision == troopRevision && troopBitmaps.size() == 10
                                      ? troopBitmaps.get( pressedTroopIndex )
                                      : null;
            if ( bitmap != null ) {
                final RectF imageBounds = fitTroopBitmap( bitmap, centerX, previewBounds.top + 7f, 64f, 64f );
                paint.setFilterBitmap( false );
                paint.setAlpha( 225 );
                canvas.drawBitmap( bitmap, null, imageBounds, paint );
            }

            paint.setAlpha( 255 );
            paint.setStyle( Paint.Style.FILL );
            paint.setTypeface( Typeface.create( Typeface.SERIF, Typeface.BOLD ) );
            paint.setTextAlign( Paint.Align.CENTER );
            paint.setColor( GOLD_LIGHT_COLOR );
            drawFittedText( canvas, Long.toString( troopSlots.get( pressedTroopIndex ).count ), centerX, previewBounds.bottom - 10f, previewWidth - 16f, 22f );
        }

        private RectF fitTroopBitmap( final Bitmap bitmap, final float centerX, final float top, final float maximumWidth, final float maximumHeight )
        {
            final float scale = Math.min( maximumWidth / bitmap.getWidth(), maximumHeight / bitmap.getHeight() );
            final float width = bitmap.getWidth() * scale;
            final float height = bitmap.getHeight() * scale;
            final float centeredTop = top + ( maximumHeight - height ) * 0.5f;
            return new RectF( centerX - width * 0.5f, centeredTop, centerX + width * 0.5f, centeredTop + height );
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
            if ( action == MotionEvent.ACTION_POINTER_DOWN ) {
                releasePressedButton();
                if ( gameContext == CONTEXT_HERO_MEETING ) {
                    clearTroopSelection();
                }
                if ( event.getPointerCount() == 2 && gameContext == CONTEXT_ADVENTURE_MAP_OVERVIEW && viewportControlEnabled && hasRadarSnapshot()
                     && radarBounds.contains( event.getX( 0 ), event.getY( 0 ) ) && radarBounds.contains( event.getX( 1 ), event.getY( 1 ) ) ) {
                    beginOverviewZoomGesture( event );
                }
                return true;
            }

            if ( action == MotionEvent.ACTION_POINTER_UP ) {
                releasePressedButton();
                return true;
            }

            if ( action == MotionEvent.ACTION_DOWN ) {
                if ( event.getPointerCount() == 1 && gameContext == CONTEXT_HERO_MEETING ) {
                    pressedTroopIndex = troopSlotAt( event.getX(), event.getY() );
                    if ( pressedTroopIndex >= 0 ) {
                        troopTouchDownX = event.getX();
                        troopTouchDownY = event.getY();
                        troopDragX = troopTouchDownX;
                        troopDragY = troopTouchDownY;
                        invalidate();
                        return true;
                    }
                }

                if ( event.getPointerCount() == 1 && viewportControlEnabled && hasRadarSnapshot()
                     && radarBounds.contains( event.getX(), event.getY() ) ) {
                    radarGestureActive = true;
                    radarGestureMoved = false;
                    radarLongPressTriggered = false;
                    radarDownX = event.getX();
                    radarDownY = event.getY();
                    radarTapItem = gameContext == CONTEXT_ADVENTURE_MAP_OVERVIEW ? overviewItemAt( radarDownX, radarDownY ) : null;
                    if ( radarTapItem != null ) {
                        postDelayed( radarLongPress, ViewConfiguration.getLongPressTimeout() );
                    }
                    if ( gameContext != CONTEXT_ADVENTURE_MAP_OVERVIEW ) {
                        radarGestureActive = sendViewportRequest( event.getX(), event.getY() );
                    }
                    return true;
                }

                pressedButton = buttonAt( event.getX(), event.getY() );
                if ( pressedButton != null ) {
                    if ( pressedButton.selectionId >= 0 ) {
                        pressedButton.sentSemantically
                            = selectionContext == gameContext
                              && selectionSender.send( gameContext, selectionRevision, pressedButton.selectionKind, pressedButton.selectionId );
                    }
                    else if ( pressedButton.localCommand != 0 ) {
                        handleLocalCommand( pressedButton.localCommand );
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
                if ( pressedTroopIndex >= 0 ) {
                    if ( event.getPointerCount() != 1 ) {
                        cancelTroopGesture();
                        invalidate();
                        return true;
                    }

                    final float deltaX = event.getX() - troopTouchDownX;
                    final float deltaY = event.getY() - troopTouchDownY;
                    if ( !troopDragActive && deltaX * deltaX + deltaY * deltaY > troopTouchSlop * troopTouchSlop ) {
                        if ( pressedTroopIndex < troopSlots.size() && troopSlots.get( pressedTroopIndex ).isOccupied() ) {
                            troopDragActive = true;
                            selectedTroopSide = -1;
                            selectedTroopSlot = -1;
                        }
                        else {
                            cancelTroopGesture();
                            invalidate();
                            return true;
                        }
                    }
                    if ( troopDragActive ) {
                        troopDragX = event.getX();
                        troopDragY = event.getY();
                        troopDragDestinationIndex = troopSlotAt( troopDragX, troopDragY );
                        if ( troopDragDestinationIndex == pressedTroopIndex ) {
                            troopDragDestinationIndex = -1;
                        }
                        invalidate();
                    }
                    return true;
                }

                if ( radarZoomGestureActive ) {
                    if ( event.getPointerCount() == 2 ) {
                        updateOverviewZoomGesture( event );
                    }
                    else {
                        releasePressedButton();
                    }
                    return true;
                }

                if ( radarGestureActive ) {
                    if ( event.getPointerCount() == 1 ) {
                        if ( gameContext == CONTEXT_ADVENTURE_MAP_OVERVIEW ) {
                            final float deltaX = event.getX() - radarDownX;
                            final float deltaY = event.getY() - radarDownY;
                            if ( !radarGestureMoved && deltaX * deltaX + deltaY * deltaY > 18f * 18f ) {
                                radarGestureMoved = true;
                                removeCallbacks( radarLongPress );
                                radarTapItem = null;
                            }
                            if ( !radarGestureMoved ) {
                                return true;
                            }
                        }
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

            if ( action == MotionEvent.ACTION_UP ) {
                if ( pressedTroopIndex >= 0 ) {
                    final int sourceIndex = pressedTroopIndex;
                    final boolean wasDrag = troopDragActive;
                    final int destinationIndex = wasDrag ? troopSlotAt( event.getX(), event.getY() ) : -1;
                    cancelTroopGesture();
                    if ( wasDrag ) {
                        if ( destinationIndex >= 0 && destinationIndex != sourceIndex ) {
                            troopMoveSender.send( gameContext, troopRevision, sourceIndex / 5, sourceIndex % 5, destinationIndex / 5,
                                                  destinationIndex % 5 );
                        }
                    }
                    else if ( troopSlotBounds[sourceIndex].contains( event.getX(), event.getY() ) ) {
                        handleTroopTap( sourceIndex );
                    }
                    invalidate();
                    return true;
                }

                removeCallbacks( radarLongPress );
                if ( radarGestureActive && gameContext == CONTEXT_ADVENTURE_MAP_OVERVIEW && !radarGestureMoved && !radarLongPressTriggered ) {
                    if ( radarTapItem != null && radarTapItem.isCluster() ) {
                        drillDownOverviewCluster( radarTapItem );
                    }
                    else if ( radarTapItem != null && radarTapItem.entry.selectable && selectionContext == gameContext ) {
                        if ( selectionSender.send( gameContext, selectionRevision, radarTapItem.entry.kind, radarTapItem.entry.id ) ) {
                            performOverviewHapticFeedback();
                        }
                    }
                    else {
                        if ( radarTapItem == null && selectionContext == gameContext ) {
                            markerInfoSender.send( gameContext, selectionRevision, SELECTION_KIND_HERO, -1 );
                        }
                        sendViewportRequest( event.getX(), event.getY() );
                    }
                }
                releasePressedButton();
                return true;
            }

            if ( action == MotionEvent.ACTION_CANCEL ) {
                releasePressedButton();
                return true;
            }

            return true;
        }

        private int troopSlotAt( final float x, final float y )
        {
            if ( troopContext != gameContext || troopSlots.size() != 10 ) {
                return -1;
            }
            for ( int index = 0; index < troopSlotBounds.length; ++index ) {
                if ( troopSlotBounds[index].contains( x, y ) ) {
                    return index;
                }
            }
            return -1;
        }

        private void handleTroopTap( final int index )
        {
            if ( index < 0 || index >= troopSlots.size() || troopContext != CONTEXT_HERO_MEETING ) {
                return;
            }

            final int side = index / 5;
            final int slot = index % 5;
            final TroopSlot tappedTroop = troopSlots.get( index );
            if ( selectedTroopSide < 0 ) {
                if ( tappedTroop.isOccupied() ) {
                    selectedTroopSide = side;
                    selectedTroopSlot = slot;
                }
                return;
            }

            if ( selectedTroopSide == side ) {
                if ( selectedTroopSlot == slot ) {
                    clearTroopSelection();
                }
                else if ( tappedTroop.isOccupied() ) {
                    selectedTroopSlot = slot;
                }
                return;
            }

            if ( troopMoveSender.send( gameContext, troopRevision, selectedTroopSide, selectedTroopSlot, side, slot ) ) {
                clearTroopSelection();
            }
        }

        private void clearTroopSelection()
        {
            selectedTroopSide = -1;
            selectedTroopSlot = -1;
            cancelTroopGesture();
        }

        private void cancelTroopGesture()
        {
            pressedTroopIndex = -1;
            troopDragActive = false;
            troopDragDestinationIndex = -1;
        }

        private boolean hasHeroPortraitSnapshot()
        {
            return gameContext == CONTEXT_HERO && visualContext == CONTEXT_HERO && visualRevision >= 0 && visualBitmap != null;
        }

        private void drawHeroPortrait( final Canvas canvas, final RectF bounds )
        {
            paint.setStyle( Paint.Style.FILL );
            paint.setColor( SHADOW_COLOR );
            canvas.drawRect( new RectF( bounds.left + 5f, bounds.top + 6f, bounds.right + 5f, bounds.bottom + 6f ), paint );

            paint.setFilterBitmap( false );
            canvas.drawBitmap( visualBitmap, null, bounds, paint );

            paint.setStyle( Paint.Style.STROKE );
            paint.setStrokeWidth( 4f );
            paint.setColor( GOLD_COLOR );
            canvas.drawRect( bounds, paint );
            paint.setStyle( Paint.Style.FILL );
        }

        private boolean sendViewportRequest( final float x, final float y )
        {
            if ( radarBounds.isEmpty() || !viewportControlEnabled ) {
                return false;
            }

            final float normalizedX = screenToWorldX( x );
            final float normalizedY = screenToWorldY( y );
            return viewportSender.send( normalizedX, normalizedY );
        }

        private void beginOverviewZoomGesture( final MotionEvent event )
        {
            radarZoomGestureActive = true;
            radarZoomPreviousMidpointX = ( event.getX( 0 ) + event.getX( 1 ) ) * 0.5f;
            radarZoomPreviousMidpointY = ( event.getY( 0 ) + event.getY( 1 ) ) * 0.5f;
            radarZoomReferenceDistance = pointerDistance( event );
        }

        private void updateOverviewZoomGesture( final MotionEvent event )
        {
            final float midpointX = ( event.getX( 0 ) + event.getX( 1 ) ) * 0.5f;
            final float midpointY = ( event.getY( 0 ) + event.getY( 1 ) ) * 0.5f;
            final float anchoredWorldX = screenToWorldX( radarZoomPreviousMidpointX );
            final float anchoredWorldY = screenToWorldY( radarZoomPreviousMidpointY );
            centerOverviewZoomAt( anchoredWorldX, anchoredWorldY, midpointX, midpointY );

            final float distance = pointerDistance( event );
            if ( radarZoomReferenceDistance > 0f && distance >= radarZoomReferenceDistance * OVERVIEW_ZOOM_IN_THRESHOLD ) {
                if ( overviewZoomLevel + 1 < OVERVIEW_ZOOM_LEVEL_COUNT ) {
                    changeOverviewZoomLevel( overviewZoomLevel + 1, midpointX, midpointY );
                }
                radarZoomReferenceDistance = distance;
            }
            else if ( radarZoomReferenceDistance > 0f && distance <= radarZoomReferenceDistance * OVERVIEW_ZOOM_OUT_THRESHOLD ) {
                if ( overviewZoomLevel > 0 ) {
                    changeOverviewZoomLevel( overviewZoomLevel - 1, midpointX, midpointY );
                }
                radarZoomReferenceDistance = distance;
            }

            radarZoomPreviousMidpointX = midpointX;
            radarZoomPreviousMidpointY = midpointY;
            invalidate();
        }

        private float pointerDistance( final MotionEvent event )
        {
            return (float)Math.hypot( event.getX( 1 ) - event.getX( 0 ), event.getY( 1 ) - event.getY( 0 ) );
        }

        private void changeOverviewZoomLevel( final int zoomLevel, final float anchorScreenX, final float anchorScreenY )
        {
            final float anchorWorldX = screenToWorldX( anchorScreenX );
            final float anchorWorldY = screenToWorldY( anchorScreenY );
            final int clampedZoomLevel = Math.max( 0, Math.min( OVERVIEW_ZOOM_LEVEL_COUNT - 1, zoomLevel ) );
            if ( clampedZoomLevel == overviewZoomLevel ) {
                return;
            }

            overviewZoomLevel = clampedZoomLevel;
            centerOverviewZoomAt( anchorWorldX, anchorWorldY, anchorScreenX, anchorScreenY );
            performOverviewHapticFeedback();
        }

        private void centerOverviewZoomAt( final float worldX, final float worldY, final float screenX, final float screenY )
        {
            final float span = overviewZoomSpan();
            final float relativeX = Math.max( 0f, Math.min( 1f, ( screenX - radarBounds.left ) / radarBounds.width() ) );
            final float relativeY = Math.max( 0f, Math.min( 1f, ( screenY - radarBounds.top ) / radarBounds.height() ) );
            overviewZoomCenterX = worldX - ( relativeX - 0.5f ) * span;
            overviewZoomCenterY = worldY - ( relativeY - 0.5f ) * span;
            clampOverviewZoomCenter();
        }

        private void drillDownOverviewCluster( final OverviewMapItem cluster )
        {
            if ( !cluster.isCluster() || overviewZoomLevel + 1 >= OVERVIEW_ZOOM_LEVEL_COUNT ) {
                return;
            }

            ++overviewZoomLevel;
            overviewZoomCenterX = cluster.worldX;
            overviewZoomCenterY = cluster.worldY;
            clampOverviewZoomCenter();
            performOverviewHapticFeedback();
            invalidate();
        }

        private void handleLocalCommand( final int command )
        {
            if ( command == LOCAL_CYCLE_OVERVIEW_KIND_FILTER ) {
                overviewKindFilter = overviewKindFilter == SELECTION_KIND_CASTLE ? OVERVIEW_KIND_FILTER_BOTH : overviewKindFilter + 1;
                applyOverviewFilterChange();
                performOverviewHapticFeedback();
                return;
            }
            if ( command == LOCAL_CYCLE_OVERVIEW_RELATIONSHIP_FILTER ) {
                overviewRelationshipFilter
                    = overviewRelationshipFilter == SELECTION_RELATIONSHIP_NEUTRAL ? OVERVIEW_RELATIONSHIP_FILTER_ALL : overviewRelationshipFilter + 1;
                applyOverviewFilterChange();
                performOverviewHapticFeedback();
                return;
            }

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

        private void applyOverviewFilterChange()
        {
            releasePressedButton();
            clearInformationDisplay();
            if ( gameContext == CONTEXT_ADVENTURE_MAP_OVERVIEW && selectionContext == gameContext ) {
                markerInfoSender.send( gameContext, selectionRevision, SELECTION_KIND_HERO, -1 );
            }
            rebuildActions();
            layoutButtons( getWidth(), getHeight() );
            invalidate();
        }

        private void performOverviewHapticFeedback()
        {
            performHapticFeedback( HapticFeedbackConstants.CLOCK_TICK );
        }

        private void clearInformationDisplay()
        {
            informationContext = -1;
            informationTitle = "";
            informationCategory = "";
            informationDetail = "";
            informationDate = "";
            informationResources = "";
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
                addAction( "MAP", ACTION_ADVENTURE_OPEN_MAP_OVERVIEW, KeyEvent.KEYCODE_UNKNOWN );
                break;
            case CONTEXT_ADVENTURE_MAP_OVERVIEW:
                contextTitle = "KINGDOM MAP";
                buttons.add( new CommandButton( overviewKindFilterLabel(), LOCAL_CYCLE_OVERVIEW_KIND_FILTER ) );
                buttons.add( new CommandButton( overviewRelationshipFilterLabel(), LOCAL_CYCLE_OVERVIEW_RELATIONSHIP_FILTER ) );
                addAction( "HEROES", ACTION_ADVENTURE_OVERVIEW_OPEN_HERO_LIST, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "TOWNS", ACTION_ADVENTURE_OVERVIEW_OPEN_CASTLE_LIST, KeyEvent.KEYCODE_UNKNOWN );
                addAction( "BACK", ACTION_ADVENTURE_OVERVIEW_BACK, KeyEvent.KEYCODE_ESCAPE );
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
            case CONTEXT_HERO_MEETING:
                contextTitle = "HERO MEETING";
                addAction( "ARMY →", ACTION_HERO_MEETING_TRANSFER_TO_RIGHT, KeyEvent.KEYCODE_DPAD_RIGHT );
                addAction( "← ARMY", ACTION_HERO_MEETING_TRANSFER_TO_LEFT, KeyEvent.KEYCODE_DPAD_LEFT );
                addAction( "SWAP ARMIES", ACTION_HERO_MEETING_SWAP_ARMIES, KeyEvent.KEYCODE_X );
                addAction( "CLOSE", ACTION_HERO_MEETING_CLOSE, KeyEvent.KEYCODE_ESCAPE );
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
            case CONTEXT_MENU_FALLBACK:
                contextTitle = "MENU NAVIGATION";
                addAction( "LEFT", KeyEvent.KEYCODE_DPAD_LEFT );
                addAction( "UP", KeyEvent.KEYCODE_DPAD_UP );
                addAction( "RIGHT", KeyEvent.KEYCODE_DPAD_RIGHT );
                addAction( "BACK", KeyEvent.KEYCODE_ESCAPE );
                addAction( "DOWN", KeyEvent.KEYCODE_DPAD_DOWN );
                addAction( "CONFIRM", KeyEvent.KEYCODE_ENTER );
                break;
            case CONTEXT_FALLBACK:
            default:
                contextTitle = "UPPER-SCREEN CONTROL";
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
            final boolean matchingContext
                = radarContext == gameContext
                  || ( gameContext == CONTEXT_ADVENTURE_MAP_OVERVIEW
                       && ( radarContext == CONTEXT_ADVENTURE_MAP || radarContext == CONTEXT_ADVENTURE_MAP_OVERVIEW ) );
            return ( gameContext == CONTEXT_ADVENTURE_MAP || gameContext == CONTEXT_ADVENTURE_MAP_OVERVIEW || gameContext == CONTEXT_EDITOR_INTERFACE )
                   && matchingContext && radarBitmap != null && radarWorldWidth > 0 && radarWorldHeight > 0;
        }

        private void drawExpandedMap( final Canvas canvas )
        {
            final float margin = getMargin();
            final float gap = margin * 0.55f;
            final float sideWidth = Math.max( 220f, Math.min( 280f, getWidth() * 0.22f ) );
            final float mapSize = Math.min( getHeight() - 2f * margin, getWidth() - 3f * margin - sideWidth );
            final float mapTop = ( getHeight() - mapSize ) * 0.5f;
            radarBounds.set( margin, mapTop, margin + mapSize, mapTop + mapSize );

            paint.setStyle( Paint.Style.FILL );
            paint.setColor( PANEL_INNER_COLOR );
            canvas.drawRect( radarBounds, paint );
            if ( hasRadarSnapshot() ) {
                drawRadar( canvas );
            }

            final RectF sidePanel = new RectF( radarBounds.right + gap, margin, getWidth() - margin, getHeight() - margin );
            paint.setColor( PANEL_COLOR );
            canvas.drawRoundRect( sidePanel, 18f, 18f, paint );
            paint.setStyle( Paint.Style.STROKE );
            paint.setStrokeWidth( 4f );
            paint.setColor( GOLD_COLOR );
            canvas.drawRoundRect( sidePanel, 18f, 18f, paint );
            paint.setStyle( Paint.Style.FILL );
            paint.setTypeface( Typeface.create( Typeface.SERIF, Typeface.BOLD ) );
            paint.setTextAlign( Paint.Align.CENTER );
            paint.setColor( TEXT_COLOR );
            paint.setTextSize( 32f );
            canvas.drawText( "KINGDOM MAP", sidePanel.centerX(), sidePanel.top + 54f, paint );

            final float textWidth = sidePanel.width() - 28f;
            if ( informationContext == CONTEXT_ADVENTURE_MAP_OVERVIEW && informationRevision >= 0 && !informationTitle.isEmpty() ) {
                paint.setColor( GOLD_LIGHT_COLOR );
                drawFittedText( canvas, informationTitle, sidePanel.centerX(), sidePanel.top + 96f, textWidth, 27f );
                paint.setTypeface( Typeface.create( Typeface.SERIF, Typeface.BOLD ) );
                paint.setColor( MUTED_TEXT_COLOR );
                drawFittedText( canvas, informationCategory, sidePanel.centerX(), sidePanel.top + 128f, textWidth, 17f );
                paint.setColor( TEXT_COLOR );
                drawFittedText( canvas, informationDate, sidePanel.centerX(), sidePanel.top + 157f, textWidth, 19f );
                paint.setTypeface( Typeface.create( Typeface.SERIF, Typeface.NORMAL ) );
                paint.setColor( GOLD_LIGHT_COLOR );
                drawOverviewLines( canvas, informationDetail, sidePanel, sidePanel.top + 190f, 23f, 3 );
                paint.setColor( TEXT_COLOR );
                drawOverviewLines( canvas, informationResources, sidePanel, sidePanel.top + 286f, 25f, 6 );
            }
            else {
                SelectionEntry focused = null;
                for ( final SelectionEntry entry : selectionEntries ) {
                    if ( entry.selected && isOverviewMarkerVisible( entry ) ) {
                        focused = entry;
                        break;
                    }
                }
                paint.setTypeface( Typeface.create( Typeface.SERIF, Typeface.NORMAL ) );
                paint.setColor( GOLD_LIGHT_COLOR );
                drawFittedText( canvas, focused == null ? "NO SELECTION" : focused.name, sidePanel.centerX(), sidePanel.top + 103f, textWidth, 25f );
                paint.setColor( MUTED_TEXT_COLOR );
                drawFittedText( canvas, "LONG-PRESS A MARKER", sidePanel.centerX(), sidePanel.top + 143f, textWidth, 20f );
                drawFittedText( canvas, "FOR QUICK INFO", sidePanel.centerX(), sidePanel.top + 170f, textWidth, 20f );
                drawFittedText( canvas, "● HERO     ■ TOWN", sidePanel.centerX(), sidePanel.top + 215f, textWidth, 19f );
                drawFittedText( canvas, "BLUE OWN     GREEN ALLY", sidePanel.centerX(), sidePanel.top + 244f, textWidth, 16f );
                drawFittedText( canvas, "RED ENEMY     GRAY NEUTRAL", sidePanel.centerX(), sidePanel.top + 270f, textWidth, 16f );
                drawFittedText( canvas, "COUNT: TAP TO ZOOM", sidePanel.centerX(), sidePanel.top + 302f, textWidth, 16f );
            }
        }

        private void drawOverviewLines( final Canvas canvas, final String value, final RectF panel, final float firstBaseline, final float lineHeight,
                                        final int maximumLines )
        {
            if ( value.isEmpty() ) {
                return;
            }

            final String[] lines = value.split( "\\n", maximumLines + 1 );
            final int lineCount = Math.min( lines.length, maximumLines );
            for ( int index = 0; index < lineCount; ++index ) {
                drawFittedText( canvas, lines[index], panel.centerX(), firstBaseline + index * lineHeight, panel.width() - 28f, 19f );
            }
        }

        private void drawRadar( final Canvas canvas )
        {
            paint.setStyle( Paint.Style.FILL );
            paint.setColor( Color.BLACK );
            canvas.drawRect( radarBounds, paint );
            final int saveCount = canvas.save();
            canvas.clipRect( radarBounds );
            paint.setFilterBitmap( false );
            final RectF radarImageBounds = new RectF( worldToScreenX( 0f ), worldToScreenY( 0f ), worldToScreenX( 1f ), worldToScreenY( 1f ) );
            canvas.drawBitmap( radarBitmap, null, radarImageBounds, paint );

            final float viewportLeft = Math.max( 0, radarViewportX ) / (float)radarWorldWidth;
            final float viewportTop = Math.max( 0, radarViewportY ) / (float)radarWorldHeight;
            final float viewportRight = Math.min( radarWorldWidth, radarViewportX + radarViewportWidth ) / (float)radarWorldWidth;
            final float viewportBottom = Math.min( radarWorldHeight, radarViewportY + radarViewportHeight ) / (float)radarWorldHeight;
            final RectF viewport
                = new RectF( worldToScreenX( viewportLeft ), worldToScreenY( viewportTop ), worldToScreenX( viewportRight ), worldToScreenY( viewportBottom ) );

            paint.setStyle( Paint.Style.STROKE );
            paint.setStrokeWidth( 4f );
            paint.setColor( Color.WHITE );
            canvas.drawRect( viewport, paint );
            if ( gameContext == CONTEXT_ADVENTURE_MAP_OVERVIEW ) {
                drawOverviewMarkers( canvas );
            }
            canvas.restoreToCount( saveCount );
            if ( gameContext == CONTEXT_ADVENTURE_MAP_OVERVIEW ) {
                drawOverviewZoomBadge( canvas );
            }
            paint.setStyle( Paint.Style.STROKE );
            paint.setStrokeWidth( 3f );
            paint.setColor( GOLD_LIGHT_COLOR );
            canvas.drawRect( radarBounds, paint );
            paint.setStyle( Paint.Style.FILL );
        }

        private void drawOverviewMarkers( final Canvas canvas )
        {
            final List<OverviewMapItem> layout = buildOverviewLayout();
            for ( final OverviewMapItem item : layout ) {
                if ( item.isCluster() ) {
                    drawOverviewCluster( canvas, item );
                    continue;
                }

                final SelectionEntry entry = item.entry;
                final float x = item.screenX;
                final float y = item.screenY;
                if ( entry.selected ) {
                    paint.setStyle( Paint.Style.STROKE );
                    paint.setStrokeWidth( 6f );
                    paint.setColor( Color.WHITE );
                    canvas.drawCircle( x, y, 25f, paint );
                    paint.setStrokeWidth( 3f );
                    paint.setColor( GOLD_LIGHT_COLOR );
                    canvas.drawCircle( x, y, 20f, paint );
                }

                paint.setStyle( Paint.Style.FILL );
                paint.setColor( markerColor( entry.relationship ) );
                if ( entry.kind == SELECTION_KIND_HERO ) {
                    canvas.drawCircle( x, y, 14f, paint );
                }
                else {
                    canvas.drawRect( x - 14f, y - 14f, x + 14f, y + 14f, paint );
                }
                paint.setStyle( Paint.Style.STROKE );
                paint.setStrokeWidth( 3f );
                paint.setColor( Color.BLACK );
                if ( entry.kind == SELECTION_KIND_HERO ) {
                    canvas.drawCircle( x, y, 14f, paint );
                }
                else {
                    canvas.drawRect( x - 14f, y - 14f, x + 14f, y + 14f, paint );
                }
            }
            drawOverviewLabels( canvas, layout );
            paint.setStyle( Paint.Style.FILL );
        }

        private void drawOverviewLabels( final Canvas canvas, final List<OverviewMapItem> layout )
        {
            final List<OverviewMarkerLabel> labels = buildOverviewLabelLayout( layout );
            paint.setTypeface( Typeface.create( Typeface.SERIF, Typeface.BOLD ) );
            paint.setTextAlign( Paint.Align.CENTER );
            paint.setTextSize( OVERVIEW_LABEL_TEXT_SIZE );
            final Paint.FontMetrics metrics = paint.getFontMetrics();
            final float baselineOffset = -( metrics.ascent + metrics.descent ) * 0.5f;
            for ( final OverviewMarkerLabel label : labels ) {
                paint.setStyle( Paint.Style.FILL );
                paint.setColor( Color.argb( 225, 20, 18, 14 ) );
                canvas.drawRoundRect( label.bounds, 9f, 9f, paint );
                paint.setStyle( Paint.Style.STROKE );
                paint.setStrokeWidth( label.entry.selected ? 3f : 2f );
                paint.setColor( label.entry.selected ? Color.WHITE : GOLD_LIGHT_COLOR );
                canvas.drawRoundRect( label.bounds, 9f, 9f, paint );
                paint.setStyle( Paint.Style.FILL );
                paint.setColor( TEXT_COLOR );
                canvas.drawText( label.text, label.bounds.centerX(), label.bounds.centerY() + baselineOffset, paint );
            }
        }

        private List<OverviewMarkerLabel> buildOverviewLabelLayout( final List<OverviewMapItem> layout )
        {
            final List<OverviewMarkerLabel> labels = new ArrayList<>();
            if ( overviewZoomLevel == 0 ) {
                return labels;
            }

            final List<RectF> blockers = new ArrayList<>();
            for ( final OverviewMapItem item : layout ) {
                final float radius = item.isCluster() ? 36f : ( item.entry.selected ? 27f : 18f );
                blockers.add( new RectF( item.screenX - radius, item.screenY - radius, item.screenX + radius, item.screenY + radius ) );
            }
            blockers.add( new RectF( radarBounds.left + 14f, radarBounds.top + 14f, radarBounds.left + 78f, radarBounds.top + 58f ) );

            paint.setTypeface( Typeface.create( Typeface.SERIF, Typeface.BOLD ) );
            paint.setTextSize( OVERVIEW_LABEL_TEXT_SIZE );
            for ( int priority = 0; priority < 5; ++priority ) {
                for ( final OverviewMapItem item : layout ) {
                    final boolean eligibleForPriority = priority == 0
                                                            ? item.entry.relationship == SELECTION_RELATIONSHIP_OWNED && item.entry.selected
                                                            : priority == 1
                                                                ? item.entry.relationship == SELECTION_RELATIONSHIP_OWNED && !item.entry.selected
                                                                : priority == 2
                                                                    ? item.entry.relationship == SELECTION_RELATIONSHIP_ALLIED
                                                                    : priority == 3
                                                                        ? item.entry.relationship == SELECTION_RELATIONSHIP_ENEMY
                                                                        : item.entry.relationship == SELECTION_RELATIONSHIP_NEUTRAL;
                    if ( item.isCluster() || !eligibleForPriority || item.entry.name.trim().isEmpty() ) {
                        continue;
                    }

                    final float maximumWidth = overviewZoomLevel == 1 ? OVERVIEW_LABEL_MAX_WIDTH_2X : OVERVIEW_LABEL_MAX_WIDTH_4X;
                    final float maximumTextWidth = maximumWidth - 2f * OVERVIEW_LABEL_HORIZONTAL_PADDING;
                    final String text = ellipsizeOverviewLabel( item.entry.name.trim(), maximumTextWidth );
                    final float width = Math.min( maximumWidth, paint.measureText( text ) + 2f * OVERVIEW_LABEL_HORIZONTAL_PADDING );
                    final float markerRadius = item.entry.selected ? 27f : 18f;
                    final float offset = markerRadius + OVERVIEW_LABEL_MARKER_GAP;
                    final RectF[] candidates = {
                        new RectF( item.screenX - width * 0.5f, item.screenY - offset - OVERVIEW_LABEL_HEIGHT,
                                   item.screenX + width * 0.5f, item.screenY - offset ),
                        new RectF( item.screenX - width * 0.5f, item.screenY + offset, item.screenX + width * 0.5f,
                                   item.screenY + offset + OVERVIEW_LABEL_HEIGHT ),
                        new RectF( item.screenX + offset, item.screenY - OVERVIEW_LABEL_HEIGHT * 0.5f, item.screenX + offset + width,
                                   item.screenY + OVERVIEW_LABEL_HEIGHT * 0.5f ),
                        new RectF( item.screenX - offset - width, item.screenY - OVERVIEW_LABEL_HEIGHT * 0.5f, item.screenX - offset,
                                   item.screenY + OVERVIEW_LABEL_HEIGHT * 0.5f )
                    };

                    for ( final RectF candidate : candidates ) {
                        if ( isOverviewLabelPlacementAvailable( candidate, blockers ) ) {
                            final RectF acceptedBounds = new RectF( candidate );
                            labels.add( new OverviewMarkerLabel( item.entry, text, acceptedBounds ) );
                            final RectF collisionBounds = new RectF( acceptedBounds );
                            collisionBounds.inset( -OVERVIEW_LABEL_COLLISION_GAP, -OVERVIEW_LABEL_COLLISION_GAP );
                            blockers.add( collisionBounds );
                            break;
                        }
                    }
                }
            }
            return labels;
        }

        private String ellipsizeOverviewLabel( final String value, final float maximumWidth )
        {
            if ( paint.measureText( value ) <= maximumWidth ) {
                return value;
            }

            final String ellipsis = "\u2026";
            final float availableWidth = Math.max( 0f, maximumWidth - paint.measureText( ellipsis ) );
            int characterCount = paint.breakText( value, true, availableWidth, null );
            if ( characterCount > 0 && Character.isHighSurrogate( value.charAt( characterCount - 1 ) ) ) {
                --characterCount;
            }
            return value.substring( 0, characterCount ).trim() + ellipsis;
        }

        private boolean isOverviewLabelPlacementAvailable( final RectF candidate, final List<RectF> blockers )
        {
            if ( candidate.left < radarBounds.left || candidate.top < radarBounds.top || candidate.right > radarBounds.right
                 || candidate.bottom > radarBounds.bottom ) {
                return false;
            }
            for ( final RectF blocker : blockers ) {
                if ( RectF.intersects( candidate, blocker ) ) {
                    return false;
                }
            }
            return true;
        }

        private void drawOverviewCluster( final Canvas canvas, final OverviewMapItem cluster )
        {
            final float x = cluster.screenX;
            final float y = cluster.screenY;
            if ( cluster.containsSelected ) {
                paint.setStyle( Paint.Style.STROKE );
                paint.setStrokeWidth( 5f );
                paint.setColor( Color.WHITE );
                canvas.drawCircle( x, y, 34f, paint );
            }

            paint.setStyle( Paint.Style.FILL );
            paint.setColor( Color.argb( 235, 20, 18, 14 ) );
            canvas.drawCircle( x, y, 27f, paint );

            final RectF ring = new RectF( x - 25f, y - 25f, x + 25f, y + 25f );
            paint.setStyle( Paint.Style.STROKE );
            paint.setStrokeWidth( 6f );
            float startAngle = -90f;
            for ( int relationship = SELECTION_RELATIONSHIP_OWNED; relationship <= SELECTION_RELATIONSHIP_NEUTRAL; ++relationship ) {
                final int relationshipCount = cluster.relationshipCounts[relationship];
                if ( relationshipCount == 0 ) {
                    continue;
                }
                final float sweepAngle = 360f * relationshipCount / cluster.members.size();
                paint.setColor( markerColor( relationship ) );
                canvas.drawArc( ring, startAngle, sweepAngle, false, paint );
                startAngle += sweepAngle;
            }

            paint.setStyle( Paint.Style.FILL );
            paint.setTypeface( Typeface.create( Typeface.SERIF, Typeface.BOLD ) );
            paint.setTextAlign( Paint.Align.CENTER );
            paint.setTextSize( 24f );
            paint.setColor( TEXT_COLOR );
            canvas.drawText( Integer.toString( cluster.members.size() ), x, y + 5f, paint );

            final float kindY = y + 16f;
            if ( cluster.hasHeroes ) {
                final float heroX = x + ( cluster.hasTowns ? -8f : 0f );
                paint.setColor( GOLD_LIGHT_COLOR );
                canvas.drawCircle( heroX, kindY, 4f, paint );
            }
            if ( cluster.hasTowns ) {
                final float townX = x + ( cluster.hasHeroes ? 8f : 0f );
                paint.setColor( GOLD_LIGHT_COLOR );
                canvas.drawRect( townX - 4f, kindY - 4f, townX + 4f, kindY + 4f, paint );
            }
        }

        private int markerColor( final int relationship )
        {
            switch ( relationship ) {
            case SELECTION_RELATIONSHIP_OWNED:
                return Color.rgb( 80, 190, 255 );
            case SELECTION_RELATIONSHIP_ALLIED:
                return Color.rgb( 70, 205, 105 );
            case SELECTION_RELATIONSHIP_ENEMY:
                return Color.rgb( 235, 75, 70 );
            case SELECTION_RELATIONSHIP_NEUTRAL:
            default:
                return Color.rgb( 185, 185, 185 );
            }
        }

        private float markerScreenX( final SelectionEntry entry )
        {
            float x = worldToScreenX( ( entry.x + 0.5f ) / radarWorldWidth );
            for ( final SelectionEntry other : selectionEntries ) {
                if ( other != entry && isOverviewMarkerVisible( other ) && other.x == entry.x && other.y == entry.y && other.kind != entry.kind ) {
                    x += entry.kind == SELECTION_KIND_HERO ? -18f : 18f;
                    break;
                }
            }
            return x;
        }

        private float markerScreenY( final SelectionEntry entry )
        {
            return worldToScreenY( ( entry.y + 0.5f ) / radarWorldHeight );
        }

        private List<OverviewMapItem> buildOverviewLayout()
        {
            final List<OverviewMapItem> layout = new ArrayList<>();
            if ( selectionContext != CONTEXT_ADVENTURE_MAP_OVERVIEW ) {
                return layout;
            }

            final List<SelectionEntry> candidates = new ArrayList<>();
            for ( final SelectionEntry entry : selectionEntries ) {
                if ( entry.x >= 0 && entry.y >= 0 && isOverviewMarkerVisible( entry ) && isOverviewMarkerInView( entry ) ) {
                    candidates.add( entry );
                }
            }

            final boolean[] protectedSameTilePair = new boolean[candidates.size()];
            for ( int first = 0; first < candidates.size(); ++first ) {
                final SelectionEntry firstEntry = candidates.get( first );
                for ( int second = first + 1; second < candidates.size(); ++second ) {
                    final SelectionEntry secondEntry = candidates.get( second );
                    if ( firstEntry.x == secondEntry.x && firstEntry.y == secondEntry.y && firstEntry.kind != secondEntry.kind ) {
                        protectedSameTilePair[first] = true;
                        protectedSameTilePair[second] = true;
                    }
                }
            }

            final float clusterDistance = overviewZoomLevel == 0 ? OVERVIEW_CLUSTER_DISTANCE_1X
                                                                 : ( overviewZoomLevel == 1 ? OVERVIEW_CLUSTER_DISTANCE_2X : 0f );
            final boolean[] visited = new boolean[candidates.size()];
            for ( int index = 0; index < candidates.size(); ++index ) {
                if ( visited[index] ) {
                    continue;
                }

                final List<SelectionEntry> members = new ArrayList<>();
                final List<Integer> pending = new ArrayList<>();
                visited[index] = true;
                pending.add( index );
                for ( int pendingIndex = 0; pendingIndex < pending.size(); ++pendingIndex ) {
                    final int memberIndex = pending.get( pendingIndex );
                    final SelectionEntry member = candidates.get( memberIndex );
                    members.add( member );
                    if ( clusterDistance <= 0f || protectedSameTilePair[memberIndex] ) {
                        continue;
                    }

                    final float memberX = markerScreenX( member );
                    final float memberY = markerScreenY( member );
                    for ( int otherIndex = 0; otherIndex < candidates.size(); ++otherIndex ) {
                        if ( visited[otherIndex] || protectedSameTilePair[otherIndex] ) {
                            continue;
                        }
                        final SelectionEntry other = candidates.get( otherIndex );
                        final float deltaX = memberX - markerScreenX( other );
                        final float deltaY = memberY - markerScreenY( other );
                        if ( deltaX * deltaX + deltaY * deltaY <= clusterDistance * clusterDistance ) {
                            visited[otherIndex] = true;
                            pending.add( otherIndex );
                        }
                    }
                }

                if ( members.size() == 1 ) {
                    final SelectionEntry entry = members.get( 0 );
                    layout.add( OverviewMapItem.forMarker( entry, markerScreenX( entry ), markerScreenY( entry ) ) );
                }
                else {
                    layout.add( OverviewMapItem.forCluster( members, radarWorldWidth, radarWorldHeight, this ) );
                }
            }
            return layout;
        }

        private OverviewMapItem overviewItemAt( final float x, final float y )
        {
            OverviewMapItem nearest = null;
            float nearestDistanceSquared = OVERVIEW_MARKER_HIT_RADIUS * OVERVIEW_MARKER_HIT_RADIUS;
            for ( final OverviewMapItem item : buildOverviewLayout() ) {
                final float deltaX = x - item.screenX;
                final float deltaY = y - item.screenY;
                final float distanceSquared = deltaX * deltaX + deltaY * deltaY;
                if ( distanceSquared < nearestDistanceSquared ) {
                    nearest = item;
                    nearestDistanceSquared = distanceSquared;
                }
            }
            return nearest;
        }

        private boolean isOverviewMarkerInView( final SelectionEntry entry )
        {
            final float worldX = ( entry.x + 0.5f ) / radarWorldWidth;
            final float worldY = ( entry.y + 0.5f ) / radarWorldHeight;
            return worldX >= overviewVisibleLeft() && worldX <= overviewVisibleRight() && worldY >= overviewVisibleTop()
                   && worldY <= overviewVisibleBottom();
        }

        private float overviewZoomSpan()
        {
            return gameContext == CONTEXT_ADVENTURE_MAP_OVERVIEW ? 1f / ( 1 << overviewZoomLevel ) : 1f;
        }

        private float overviewVisibleLeft()
        {
            return gameContext == CONTEXT_ADVENTURE_MAP_OVERVIEW ? overviewZoomCenterX - overviewZoomSpan() * 0.5f : 0f;
        }

        private float overviewVisibleTop()
        {
            return gameContext == CONTEXT_ADVENTURE_MAP_OVERVIEW ? overviewZoomCenterY - overviewZoomSpan() * 0.5f : 0f;
        }

        private float overviewVisibleRight()
        {
            return overviewVisibleLeft() + overviewZoomSpan();
        }

        private float overviewVisibleBottom()
        {
            return overviewVisibleTop() + overviewZoomSpan();
        }

        private float worldToScreenX( final float worldX )
        {
            return radarBounds.left + ( worldX - overviewVisibleLeft() ) * radarBounds.width() / overviewZoomSpan();
        }

        private float worldToScreenY( final float worldY )
        {
            return radarBounds.top + ( worldY - overviewVisibleTop() ) * radarBounds.height() / overviewZoomSpan();
        }

        private float screenToWorldX( final float screenX )
        {
            final float relativeX = Math.max( 0f, Math.min( 1f, ( screenX - radarBounds.left ) / radarBounds.width() ) );
            return overviewVisibleLeft() + relativeX * overviewZoomSpan();
        }

        private float screenToWorldY( final float screenY )
        {
            final float relativeY = Math.max( 0f, Math.min( 1f, ( screenY - radarBounds.top ) / radarBounds.height() ) );
            return overviewVisibleTop() + relativeY * overviewZoomSpan();
        }

        private void resetOverviewZoom( final int worldWidth, final int worldHeight )
        {
            overviewZoomLevel = 0;
            overviewZoomCenterX = 0.5f;
            overviewZoomCenterY = 0.5f;
            overviewZoomWorldWidth = worldWidth;
            overviewZoomWorldHeight = worldHeight;
        }

        private void clampOverviewZoomCenter()
        {
            final float halfSpan = overviewZoomSpan() * 0.5f;
            overviewZoomCenterX = Math.max( halfSpan, Math.min( 1f - halfSpan, overviewZoomCenterX ) );
            overviewZoomCenterY = Math.max( halfSpan, Math.min( 1f - halfSpan, overviewZoomCenterY ) );
        }

        private void drawOverviewZoomBadge( final Canvas canvas )
        {
            final String label = ( 1 << overviewZoomLevel ) + "\u00d7";
            final RectF badge = new RectF( radarBounds.left + 14f, radarBounds.top + 14f, radarBounds.left + 78f, radarBounds.top + 58f );
            paint.setStyle( Paint.Style.FILL );
            paint.setColor( Color.argb( 210, 20, 18, 14 ) );
            canvas.drawRoundRect( badge, 10f, 10f, paint );
            paint.setStyle( Paint.Style.STROKE );
            paint.setStrokeWidth( 2f );
            paint.setColor( GOLD_LIGHT_COLOR );
            canvas.drawRoundRect( badge, 10f, 10f, paint );
            paint.setStyle( Paint.Style.FILL );
            paint.setTypeface( Typeface.create( Typeface.SERIF, Typeface.BOLD ) );
            paint.setTextAlign( Paint.Align.CENTER );
            paint.setTextSize( 24f );
            paint.setColor( TEXT_COLOR );
            canvas.drawText( label, badge.centerX(), badge.centerY() + 8f, paint );
        }

        private boolean isOverviewMarkerVisible( final SelectionEntry entry )
        {
            return ( overviewKindFilter == OVERVIEW_KIND_FILTER_BOTH || entry.kind == overviewKindFilter )
                   && ( overviewRelationshipFilter == OVERVIEW_RELATIONSHIP_FILTER_ALL
                        || entry.relationship == overviewRelationshipFilter );
        }

        private String overviewKindFilterLabel()
        {
            switch ( overviewKindFilter ) {
            case SELECTION_KIND_HERO:
                return "KIND: HEROES";
            case SELECTION_KIND_CASTLE:
                return "KIND: TOWNS";
            case OVERVIEW_KIND_FILTER_BOTH:
            default:
                return "KIND: BOTH";
            }
        }

        private String overviewRelationshipFilterLabel()
        {
            switch ( overviewRelationshipFilter ) {
            case SELECTION_RELATIONSHIP_OWNED:
                return "RELATION: OWNED";
            case SELECTION_RELATIONSHIP_ALLIED:
                return "RELATION: ALLIED";
            case SELECTION_RELATIONSHIP_ENEMY:
                return "RELATION: ENEMY";
            case SELECTION_RELATIONSHIP_NEUTRAL:
                return "RELATION: NEUTRAL";
            case OVERVIEW_RELATIONSHIP_FILTER_ALL:
            default:
                return "RELATION: ALL";
            }
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
                    buttons.add( new CommandButton( entry.name, entry.detail, entry.kind, entry.id, entry.selected ) );
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
            if ( gameContext == CONTEXT_ADVENTURE_MAP_OVERVIEW ) {
                final float sideWidth = Math.max( 220f, Math.min( 280f, width * 0.22f ) );
                final float mapSize = Math.min( height - 2f * margin, width - 3f * margin - sideWidth );
                final float left = margin + mapSize + gap;
                final float right = width - margin;
                final float buttonGap = 14f;
                final float navigationButtonHeight = Math.min( 120f, ( height - 2f * margin - 4f * buttonGap ) / 4.3f );
                final float filterButtonHeight = navigationButtonHeight * 0.65f;
                final float totalButtonHeight = 2f * filterButtonHeight + 3f * navigationButtonHeight + 4f * buttonGap;
                final float top = height - margin - totalButtonHeight;
                float buttonTop = top;
                for ( int index = 0; index < buttons.size(); ++index ) {
                    final CommandButton button = buttons.get( index );
                    final boolean filterButton = button.localCommand == LOCAL_CYCLE_OVERVIEW_KIND_FILTER
                                                 || button.localCommand == LOCAL_CYCLE_OVERVIEW_RELATIONSHIP_FILTER;
                    final float buttonHeight = filterButton ? filterButtonHeight : navigationButtonHeight;
                    button.bounds.set( left, buttonTop, right, buttonTop + buttonHeight );
                    buttonTop += buttonHeight + buttonGap;
                }
                return;
            }
            final float top = gameContext == CONTEXT_HERO_MEETING ? height * 0.76f : height * 0.265f;
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

        private void layoutTroopSlots( final int width, final int height )
        {
            if ( width <= 0 || height <= 0 ) {
                return;
            }

            final float margin = getMargin();
            final float gap = margin * 0.55f;
            final float rowTop = height * 0.295f;
            final float rowStride = height * 0.215f;
            final float slotTopOffset = 24f;
            final float slotHeight = height * 0.16f;
            final float slotWidth = ( width - 2f * margin - 4f * gap ) / 5f;
            for ( int side = 0; side < 2; ++side ) {
                final float top = rowTop + side * rowStride + slotTopOffset;
                for ( int slot = 0; slot < 5; ++slot ) {
                    final float left = margin + slot * ( slotWidth + gap );
                    troopSlotBounds[side * 5 + slot].set( left, top, left + slotWidth, top + slotHeight );
                }
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
            removeCallbacks( radarLongPress );
            radarGestureActive = false;
            radarGestureMoved = false;
            radarLongPressTriggered = false;
            radarTapItem = null;
            radarZoomGestureActive = false;
            radarZoomReferenceDistance = 0f;
            if ( pressedTroopIndex >= 0 || troopDragActive ) {
                cancelTroopGesture();
                invalidate();
            }
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

    private static final class OverviewMapItem
    {
        final SelectionEntry entry;
        final List<SelectionEntry> members;
        final float screenX;
        final float screenY;
        final float worldX;
        final float worldY;
        final int[] relationshipCounts;
        final boolean hasHeroes;
        final boolean hasTowns;
        final boolean containsSelected;

        private OverviewMapItem( final SelectionEntry entry, final List<SelectionEntry> members, final float screenX, final float screenY,
                                 final float worldX, final float worldY, final int[] relationshipCounts, final boolean hasHeroes,
                                 final boolean hasTowns, final boolean containsSelected )
        {
            this.entry = entry;
            this.members = members;
            this.screenX = screenX;
            this.screenY = screenY;
            this.worldX = worldX;
            this.worldY = worldY;
            this.relationshipCounts = relationshipCounts;
            this.hasHeroes = hasHeroes;
            this.hasTowns = hasTowns;
            this.containsSelected = containsSelected;
        }

        static OverviewMapItem forMarker( final SelectionEntry entry, final float screenX, final float screenY )
        {
            return new OverviewMapItem( entry, null, screenX, screenY, 0f, 0f, null, entry.kind == SELECTION_KIND_HERO,
                                        entry.kind == SELECTION_KIND_CASTLE, entry.selected );
        }

        static OverviewMapItem forCluster( final List<SelectionEntry> members, final int worldWidth, final int worldHeight,
                                           final CommandDeckView view )
        {
            float screenX = 0f;
            float screenY = 0f;
            float worldX = 0f;
            float worldY = 0f;
            final int[] relationshipCounts = new int[SELECTION_RELATIONSHIP_NEUTRAL + 1];
            boolean hasHeroes = false;
            boolean hasTowns = false;
            boolean containsSelected = false;
            for ( final SelectionEntry member : members ) {
                screenX += view.markerScreenX( member );
                screenY += view.markerScreenY( member );
                worldX += ( member.x + 0.5f ) / worldWidth;
                worldY += ( member.y + 0.5f ) / worldHeight;
                if ( member.relationship >= SELECTION_RELATIONSHIP_OWNED && member.relationship <= SELECTION_RELATIONSHIP_NEUTRAL ) {
                    ++relationshipCounts[member.relationship];
                }
                hasHeroes |= member.kind == SELECTION_KIND_HERO;
                hasTowns |= member.kind == SELECTION_KIND_CASTLE;
                containsSelected |= member.selected;
            }

            final float memberCount = members.size();
            return new OverviewMapItem( null, members, screenX / memberCount, screenY / memberCount, worldX / memberCount, worldY / memberCount,
                                        relationshipCounts, hasHeroes, hasTowns, containsSelected );
        }

        boolean isCluster()
        {
            return entry == null;
        }
    }

    private static final class CommandButton
    {
        final String label;
        final String detail;
        final int action;
        final int keyCode;
        final int selectionId;
        final int selectionKind;
        final int localCommand;
        final boolean selected;
        final RectF bounds = new RectF();
        boolean sentSemantically;

        CommandButton( final String label, final int action, final int keyCode )
        {
            this( label, "", action, keyCode, -1, 0, 0, false );
        }

        CommandButton( final String label, final String detail, final int selectionKind, final int selectionId, final boolean selected )
        {
            this( label, detail, ACTION_NONE, KeyEvent.KEYCODE_UNKNOWN, selectionId, selectionKind, 0, selected );
        }

        CommandButton( final String label, final int localCommand )
        {
            this( label, "", ACTION_NONE, KeyEvent.KEYCODE_UNKNOWN, -1, 0, localCommand, false );
        }

        private CommandButton( final String label, final String detail, final int action, final int keyCode, final int selectionId, final int selectionKind,
                               final int localCommand, final boolean selected )
        {
            this.label = label;
            this.detail = detail;
            this.action = action;
            this.keyCode = keyCode;
            this.selectionId = selectionId;
            this.selectionKind = selectionKind;
            this.localCommand = localCommand;
            this.selected = selected;
        }
    }

    private static final class OverviewMarkerLabel
    {
        final SelectionEntry entry;
        final String text;
        final RectF bounds;

        OverviewMarkerLabel( final SelectionEntry entry, final String text, final RectF bounds )
        {
            this.entry = entry;
            this.text = text;
            this.bounds = bounds;
        }
    }

    private static final class SelectionEntry
    {
        final int id;
        final String name;
        final String detail;
        final boolean selected;
        final int kind;
        final int x;
        final int y;
        final int relationship;
        final boolean selectable;

        SelectionEntry( final int id, final String name, final String detail, final boolean selected, final int kind, final int x, final int y,
                        final int relationship, final boolean selectable )
        {
            this.id = id;
            this.name = name;
            this.detail = detail;
            this.selected = selected;
            this.kind = kind;
            this.x = x;
            this.y = y;
            this.relationship = relationship;
            this.selectable = selectable;
        }
    }

    private static final class TroopSlot
    {
        final int monsterId;
        final String name;
        final long count;

        TroopSlot( final int monsterId, final String name, final long count )
        {
            this.monsterId = monsterId;
            this.name = name;
            this.count = count;
        }

        boolean isOccupied()
        {
            return monsterId >= 0 && count > 0;
        }
    }
}
